package de.amshaegar.economy.db;

import java.sql.Connection;
import java.sql.SQLException;

public interface SQLConnector {

	public void open() throws SQLException;
	public void close() throws SQLException;
	public Connection getConnection();
	public void createTables() throws SQLException;
}
