package org.kitteh.trackr.data.elements;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.kitteh.trackr.Trackr;
import org.kitteh.trackr.data.Data;
import org.kitteh.trackr.data.DataType;

/**
 * Murder tracker
 */
public class Kill extends Data {
    private final String killer;
    private final String victim;
    private final Timestamp timestamp;
    private final String weapon;

    public Kill(LivingEntity killer, LivingEntity victim) {
        super(DataType.KILL);
        this.victim = typeOrName(victim);
        this.timestamp = new Timestamp(new Date().getTime());
        if (killer == null) {
            this.killer = "unknown";
            this.weapon = "unknown";
            return;
        }
        if (killer instanceof Projectile) {
            this.killer = Trackr.getInstance().getDamageTracker().get(victim.getUniqueId());
            this.weapon = killer.getType().name();
            return;
        }
        EntityEquipment equipment = killer.getEquipment();
        ItemStack hand = equipment.getItemInHand();
        this.killer = Kill.typeOrName(killer);
        if (hand == null) {
            weapon = "UNARMED";
        } else {
            weapon = hand.getType().name();
        }
    }

    public static String typeOrName(Entity entity) {
        return entity.getType().name() + (entity instanceof Player ? ":" + ((Player) entity).getName() : "");
    }

    @Override
    public void populateStatement(PreparedStatement statement) throws SQLException {
        statement.setString(1, Trackr.getServerName());
        statement.setString(2, this.killer);
        statement.setString(3, this.weapon);
        statement.setString(4, this.victim);
        statement.setTimestamp(5, this.timestamp);
    }

    @Override
    protected String getStatementString() {
        return "INSERT INTO `kills` (`server`,`killer`,`weapon`,`victim`,`timestamp`) VALUES (?,?,?,?,?);";
    }
}