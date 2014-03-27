package de.amshaegar.economy.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public abstract class SQLConnector {

	protected Connection connection;

	public abstract void open() throws SQLException;
	public abstract void createTables() throws SQLException;
	
	public void close() throws SQLException {
		if(connection != null) {
			connection.close();
		}
	}

	public abstract String getTableName(String table);
	public abstract void insertOrIgnoreSubject(String subject) throws SQLException;
	
	public ResultSet selectPlayer(String player) throws SQLException {
		PreparedStatement selectId = connection.prepareStatement("SELECT id FROM "+getTableName("player")+" WHERE name = ?");
		selectId.setString(1, player);
		return selectId.executeQuery();
	}
	
	public boolean insertPlayer(String player) throws SQLException {
		PreparedStatement insertPlayer = connection.prepareStatement("INSERT INTO "+getTableName("player")+" (name) VALUES (?)");
		insertPlayer.setString(1, player);
		return insertPlayer.executeUpdate() != 0;
	}
	
	public ResultSet selectBalance(String player) throws SQLException {
		PreparedStatement getBalance = connection.prepareStatement("SELECT SUM(amount) FROM "+getTableName("transfer")+" AS t JOIN "+getTableName("player")+" AS p ON t.player = p.id WHERE name = ?");
		getBalance.setString(1, player);
		return getBalance.executeQuery();
	}
	
	public boolean insertTransfer(String player, float amount, String subject) throws SQLException {
		PreparedStatement insertBalance = connection.prepareStatement("INSERT INTO "+getTableName("transfer")+""
				+ " (time, player, amount, subject)"
				+ " VALUES (?, (SELECT id FROM "+getTableName("player")+" WHERE name = ?), ?, (SELECT id FROM "+getTableName("subject")+" WHERE subject = ?))");
		insertBalance.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
		insertBalance.setString(2, player);
		insertBalance.setFloat(3, amount);
		insertBalance.setString(4, subject);
		return insertBalance.executeUpdate() != 0;
	}
	
	public ResultSet selectTransfers(int limit) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("SELECT *, COALESCE(s.alias, s.subject) AS alisub FROM "+getTableName("transfer")+" AS t"
				+ " JOIN "+getTableName("player")+" AS p ON t.player = p.id"
				+ " JOIN "+getTableName("subject")+" AS s ON t.subject = s.id"
				+ " ORDER BY t.id DESC LIMIT ?");
		ps.setInt(1, limit);
		return ps.executeQuery();
	}
	public ResultSet selectSubjects(int limit) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("SELECT * FROM "+getTableName("subject")+" ORDER BY id DESC LIMIT ?");
		ps.setInt(1, limit);
		return ps.executeQuery();
	}

}
