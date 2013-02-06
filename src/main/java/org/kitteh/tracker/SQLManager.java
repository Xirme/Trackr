package org.kitteh.tracker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.kitteh.tracker.data.Data;
import org.kitteh.tracker.data.DataType;
import org.kitteh.tracker.data.PersistentData;

public class SQLManager extends Thread {
    private final String url, user, password;
    private Connection connection;
    private final Map<DataType, List<Data>> dataMap = new EnumMap<DataType, List<Data>>(DataType.class);
    private final Trackr plugin;
    private boolean running = true;
    private boolean emptied = false;

    public SQLManager(Trackr plugin, String host, String database, int port, String username, String password) throws ClassNotFoundException, SQLException {
        this.setName("Trackr Data Savr");
        this.plugin = plugin;
        Class.forName("com.mysql.jdbc.Driver");
        this.url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        this.user = username;
        this.password = password;
        this.newConnection();
        for (final DataType type : DataType.values()) {
            this.dataMap.put(type, Collections.synchronizedList(new ArrayList<Data>()));
        }
        this.start();
    }

    public boolean isEmptied() {
        return this.emptied;
    }

    @Override
    public void run() {
        while (this.running) {
            try {
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                Thread.sleep(10000);
            } catch (final InterruptedException e) {
                this.running = false;
            }
            this.process();
        }
        this.process(); // One more time!
        this.emptied = true;
    }

    /**
     * Dump all remaining items in a final push
     */
    public void shutdown() {
        this.interrupt();
    }

    private void connectionProd() {
        boolean valid = false;
        try {
            valid = this.connection.isValid(1);
        } catch (final SQLException e) {
            // This is only an exception if I passed something less than 0 to the method. Ignore.
        }
        if (!valid) {
            this.newConnection();
        }
    }

    private void newConnection() {
        try {
            this.connection = DriverManager.getConnection(this.url, this.user, this.password);
        } catch (final SQLException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Unable to initialize connection", e);
        }
    }

    private void process() {
        this.connectionProd();
        for (final DataType type : DataType.values()) {
            final List<Data> list = this.dataMap.get(type);
            if (list.isEmpty()) {
                continue;
            }
            Data data = list.get(0);
            int batch = 0;
            try {
                PersistentData pData;
                final PreparedStatement statement = data.getStatement(this.connection);
                PreparedStatement initStatement = null;
                PreparedStatement getIDStatement = null;
                if (type.isPersistent()) {
                    pData = (PersistentData) data;
                    initStatement = pData.getInit(this.connection);
                    getIDStatement = pData.getIDStatement(this.connection);
                }
                while (!list.isEmpty()) {
                    data = list.get(0);
                    if (type.isPersistent()) {
                        pData = (PersistentData) data;
                        pData.populateInitStatement(initStatement);
                        initStatement.executeUpdate();
                        pData.populateIDStatement(getIDStatement);
                        final ResultSet result = getIDStatement.executeQuery();
                        result.first();
                        pData.setID(result.getInt(1));
                    }
                    data.populateStatement(statement);
                    statement.addBatch();
                    batch++;
                    if (batch == 20) {
                        statement.executeBatch();
                        batch = 0;
                        statement.clearBatch();
                    }
                    list.remove(0);
                }
            } catch (final SQLException e) {
                this.plugin.getLogger().log(Level.SEVERE, "Error while saving data. Waiting until next round to try again.", e);
            }
        }
    }
}