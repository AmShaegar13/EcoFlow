package de.amshaegar.economy.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import de.amshaegar.economy.EcoMain;

public class MySQLConnector implements SQLConnector {
	
	private String host;
	private int port;
	private String db;
	private String user;
	private String pass;
	private Connection connection;

	public MySQLConnector(String host, int port, String db, String user, String pass) {
		this.host = host;
		this.port = port;
		this.db = db;
		this.user = user;
		this.pass = pass;
	}

	@Override
	public void open() throws SQLException {
		connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%d/%s", host, port, db), user, pass);
	}

	@Override
	public void close() throws SQLException {
		connection.close();
	}

	@Override
	public Connection getConnection() {
		return connection;
	}

	@Override
	public void createTables() throws SQLException {
		String prefix = EcoMain.getPlugin().getConfig().getString("database.prefix");
		PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `"+prefix+"player` (" +
				"  `id` INTEGER AUTO_INCREMENT PRIMARY KEY," +
				"  `name` VARCHAR" +
				");");
		ps.execute();
		ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `"+prefix+"transaction` (" +
				"  `id` INTEGER AUTO_INCREMENT PRIMARY KEY," +
				"  `time` DATETIME," +
				"  `player` INTEGER," +
				"  `amount` INTEGER," +
				"  `subject` VARCHAR," +
				");");
		ps.execute();
	}

}
