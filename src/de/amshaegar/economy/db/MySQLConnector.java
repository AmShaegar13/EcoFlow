package de.amshaegar.economy.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import de.amshaegar.economy.EcoFlow;

public class MySQLConnector extends SQLConnector {
	
	private String host;
	private int port;
	private String db;
	private String prefix;
	private String user;
	private String pass;
	private Connection connection;

	public MySQLConnector(String host, int port, String db, String prefix, String user, String pass) {
		this.host = host;
		this.port = port;
		this.db = db;
		this.prefix = prefix;
		this.user = user;
		this.pass = pass;
	}

	@Override
	public void open() throws SQLException {
		connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%d/%s?autoReconnect=true", host, port, db), user, pass);
	}

	@Override
	public void createTables() throws SQLException {
		String prefix = EcoFlow.getPlugin().getConfig().getString("database.prefix");
		PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `"+prefix+"player` (" +
				"  `id` INTEGER AUTO_INCREMENT PRIMARY KEY," +
				"  `name` VARCHAR" +
				");");
		ps.execute();
		ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `"+prefix+"transfer` (" +
				"  `id` INTEGER AUTO_INCREMENT PRIMARY KEY," +
				"  `time` DATETIME," +
				"  `player` INTEGER NOT NULL," +
				"  `amount` FLOAT," +
				"  `subject` VARCHAR," +
				");");
		ps.execute();
	}

	@Override
	public String getTableName(String table) {
		return prefix+table;
	}

}
