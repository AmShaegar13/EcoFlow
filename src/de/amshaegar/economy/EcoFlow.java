package de.amshaegar.economy;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import de.amshaegar.economy.db.MySQLConnector;
import de.amshaegar.economy.db.SQLConnector;
import de.amshaegar.economy.db.SQLiteConnector;

public class EcoFlow extends JavaPlugin {

	private static Plugin plugin;
	private static SQLConnector connector;
	private static EcoProvider provider;

	@Override
	public void onEnable() {
		plugin = this;
		new EcoListener();
		CommandExecutor ce = new EcoExecutor();
		getCommand("balance").setExecutor(ce);
		getCommand("deposit").setExecutor(ce);
		getCommand("withdraw").setExecutor(ce);

		getConfig().addDefault("settings.balance.init", 10);
		getConfig().addDefault("settings.currency.symbol", "$");
		getConfig().addDefault("settings.currency.leading", true);
		getConfig().addDefault("settings.currency.singular", "dollar");
		getConfig().addDefault("settings.currency.plural", "dollars");
		
		getConfig().addDefault("database.mysql.use", false);
		getConfig().addDefault("database.mysql.host", "localhost");
		getConfig().addDefault("database.mysql.port", 3306);
		getConfig().addDefault("database.mysql.database", "dbname");
		getConfig().addDefault("database.mysql.user", "username");
		getConfig().addDefault("database.mysql.password", "password");
		getConfig().addDefault("database.prefix", "eco_");
		getConfig().addDefault("database.sqlite.filename", "plugins/"+getName()+"/transactions.db");
		
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		if(getConfig().getBoolean("database.mysql.use")) {
			connector = new MySQLConnector(
					getConfig().getString("database.mysql.host"),
					getConfig().getInt("database.mysql.port"),
					getConfig().getString("database.mysql.database"),
					getConfig().getString("database.mysql.user"),
					getConfig().getString("database.mysql.password")
					);
		} else {
			connector = new SQLiteConnector(getConfig().getString("database.sqlite.filename"));
		}

		try {
			connector.open();
			connector.createTables();
		} catch (SQLException e) {
			Bukkit.getLogger().warning("Failed to create necessary tables for economy: "+e.getMessage());
		}
		
		provider = new EcoProvider();
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll();
		try {
			connector.close();
		} catch (SQLException e) {
			Bukkit.getLogger().warning("Failed to properly close database connection: "+e.getMessage());
		}
	}

	public static Plugin getPlugin() {
		return plugin;
	}
	
	public static SQLConnector getConnector() {
		return connector;
	}

	public static EcoProvider getProvider() {
		return provider;
	}

}