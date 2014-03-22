package de.amshaegar.economy.db;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQLConnector extends SQLConnector {
	
	private String host;
	private int port;
	private String db;
	private String prefix;
	private String user;
	private String pass;

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
	public String getTableName(String table) {
		return prefix+table;
	}

	@Override
	public void createTables() throws SQLException {
		PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `"+getTableName("player")+"` (" +
				"  `id` INTEGER AUTO_INCREMENT PRIMARY KEY," +
				"  `name` VARCHAR(16)" +
				");");
		ps.execute();
		ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `"+getTableName("transfer")+"` (" +
				"  `id` INTEGER AUTO_INCREMENT PRIMARY KEY," +
				"  `time` DATETIME," +
				"  `player` INTEGER NOT NULL," +
				"  `amount` FLOAT," +
				"  `subject` INTEGER" +
				");");
		ps.execute();
		ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `"+getTableName("subject")+"` (" +
				"  `id` INTEGER AUTO_INCREMENT PRIMARY KEY," +
				"  `alias` VARCHAR(32)," +
				"  `subject` VARCHAR(255) UNIQUE" +
				");");
		ps.execute();
	}
	
	@Override
	public void insertOrIgnoreSubject(String subject) throws SQLException {
		PreparedStatement insertSubject = connection.prepareStatement("INSERT IGNORE INTO "+getTableName("subject")+" (subject) VALUES (?)");
		insertSubject.setString(1, subject);
		insertSubject.execute();
	}

}
