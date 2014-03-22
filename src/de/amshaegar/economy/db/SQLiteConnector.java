package de.amshaegar.economy.db;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLiteConnector extends SQLConnector {
	
	private String filename;

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
	public String getTableName(String table) {
		return table;
	}

	@Override
	public void createTables() throws SQLException {
		PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `"+getTableName("player")+"` (" +
				"  `id` INTEGER PRIMARY KEY," +
				"  `name` VARCHAR" +
				");");
		ps.execute();
		ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `"+getTableName("transfer")+"` (" +
				"  `id` INTEGER PRIMARY KEY," +
				"  `time` DATETIME," +
				"  `player` INTEGER NOT NULL," +
				"  `amount` REAL," +
				"  `subject` INTEGER" +
				");");
		ps.execute();
		ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `"+getTableName("subject")+"` (" +
				"  `id` INTEGER PRIMARY KEY," +
				"  `alias` VARCHAR," +
				"  `subject` VARCHAR UNIQUE" +
				");");
		ps.execute();
	}
	
	@Override
	public void insertOrIgnoreSubject(String subject) throws SQLException {
		PreparedStatement insertSubject = connection.prepareStatement("INSERT OR IGNORE INTO "+getTableName("subject")+" (subject) VALUES (?)");
		insertSubject.setString(1, subject);
		insertSubject.execute();
	}

}
