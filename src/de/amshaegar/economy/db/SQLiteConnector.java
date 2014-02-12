package de.amshaegar.economy.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import de.amshaegar.economy.EcoFlow;

public class SQLiteConnector implements SQLConnector {
	
	private String filename;
	private Connection connection;

	public SQLiteConnector(String filename) {
		this.filename = filename;
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void open() throws SQLException {
		connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", filename));
	}

	@Override
	public void close() throws SQLException {
		connection.close();
	}
	
	public Connection getConnection() {
		return connection;
	}

	@Override
	public void createTables() throws SQLException {
		String prefix = EcoFlow.getPlugin().getConfig().getString("database.prefix");
		PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `"+prefix+"player` (" +
				"  `id` INTEGER PRIMARY KEY," +
				"  `name` VARCHAR" +
				");");
		ps.execute();
		ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `"+prefix+"transaction` (" +
				"  `id` INTEGER PRIMARY KEY," +
				"  `time` DATETIME," +
				"  `player` INTEGER NOT NULL," +
				"  `amount` REAL," +
				"  `subject` VARCHAR" +
				");");
		ps.execute();
	}

}
