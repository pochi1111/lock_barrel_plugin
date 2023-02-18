package san.kuroinu.lock_barrel_plugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import san.kuroinu.lock_barrel_plugin.commands.lock_barrel;

import java.sql.SQLException;

public final class Lock_barrel_plugin extends JavaPlugin {
    public static JavaPlugin plugin;
    private Listeners listeners;
    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        try {
            this.listeners = new Listeners();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Bukkit.getPluginManager().registerEvents(this.listeners, this);
        //getCommand("lock_barrel").setExecutor(new lock_barrel());
        super.onEnable();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        super.onDisable();
    }
    public static JavaPlugin getPlugin() {
        return plugin;
    }
}
