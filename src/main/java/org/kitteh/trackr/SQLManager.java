package org.kitteh.trackr;

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

import org.kitteh.trackr.data.Data;
import org.kitteh.trackr.data.DataType;
import org.kitteh.trackr.data.PersistentData;
import org.kitteh.trackr.data.elements.ServerSession;

public class SQLManager extends Thread {
    private final String url, user, password;
    private Connection connection;
    private final Map<DataType, List<Data>> dataMap = new EnumMap<DataType, List<Data>>(DataType.class);
    private final Trackr plugin;
    private boolean running = true;
    private boolean emptied = false;

    private long lastPing;
    private ServerSession session;
    private boolean hesDeadJim = false;

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

        this.lastPing = System.currentTimeMillis();
        this.session = new ServerSession();
        plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                SQLManager.this.lastPing = System.currentTimeMillis();
            }
        }, 5 * 20, 5 * 20);

        this.start();
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
            if ((System.currentTimeMillis() - this.lastPing) < (5 * 60 * 1000)) {
                if (this.hesDeadJim) {
                    this.plugin.getServer().getLogger().info("Server seems back alive again! Recording new uptime.");
                    this.session = new ServerSession();
                }
                this.add(this.session);
            } else {
                this.hesDeadJim = true;
                this.plugin.getServer().getLogger().info("Server seems to have taken a nap. Pausing uptime recording.");
            }
            this.process();
        }
        this.process(); // One more time!
        this.emptied = true;
    }

    private void connectionProd() throws SQLException {
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

    private void newConnection() throws SQLException {
        this.connection = DriverManager.getConnection(this.url, this.user, this.password);
    }

    private void process() {
        if (this.hesDeadJim) {
            return;
        }
        try {
            this.connectionProd();
            for (final DataType type : DataType.values()) {
                final List<Data> list = this.dataMap.get(type);
                if (list.isEmpty()) {
                    continue;
                }
                Data data = list.get(0);
                int batch = 0;
                PersistentData pData;
                final PreparedStatement statement = data.getStatement(this.connection);
                PreparedStatement initStatement = null;
                PreparedStatement getIDStatement = null;
                if (type.isPersistent()) {
                    pData = (PersistentData) data;
                    initStatement = pData.getInitStatement(this.connection);
                    getIDStatement = pData.getIDStatement(this.connection);
                }
                while (!list.isEmpty()) {
                    data = list.get(0);
                    if (type.isPersistent()) {
                        pData = (PersistentData) data;
                        if (!pData.isStarted()) {
                            pData.populateInitStatement(initStatement);
                            initStatement.executeUpdate();
                            pData.setStarted();
                        }
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
                if (batch > 0) {
                    statement.executeBatch();
                }
            }
        } catch (final SQLException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Error while saving data. Waiting until next round to try again: " + e.getMessage());
        }
    }

    /**
     * Add data to be saved
     * 
     * @param data
     */
    void add(Data data) {
        final List<Data> list = this.dataMap.get(data.getType());
        if ((data instanceof PersistentData) && list.contains(data)) {
            return;
        }
        list.add(data);
    }

    /**
     * Has the SQLManager emptied its queue for shutdown
     * 
     * @return if it's ok to shut down
     */
    boolean isEmptied() {
        return this.emptied;
    }

    /**
     * Dump all remaining items in a final push
     */
    void shutdown() {
        this.interrupt();
    }
}