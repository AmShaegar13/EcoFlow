package de.amshaegar.economy.db;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class SQLConnector {

	protected Connection connection;

	public abstract void open() throws SQLException;
	public abstract void createTables() throws SQLException;
	
	public void close() throws SQLException {
		if(connection != null) {
			connection.close();
		}
	}

	public Connection getConnection() {
		return connection;
	}
	
	public abstract String getTableName(String table);
	public abstract void insertOrIgnoreSubject(String subject) throws SQLException;

}
