package de.amshaegar.economy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;

import org.bukkit.Bukkit;

/**
 * @author AmShaegar
 *
 */
public class EcoProvider {

	private HashMap<String, Integer> balances = new HashMap<String, Integer>();
	private String prefix;
	private Connection connection;

	public EcoProvider() {
		prefix = EcoMain.getPlugin().getConfig().getString("database.prefix");
		connection = EcoMain.getConnector().getConnection();
	}

	/**
	 * Deposit initial balance on a player's account if it doesn't exist.
	 * 
	 * @param player Name of the player
	 */
	public void createAccount(String player) {
		if(balances.containsKey(player)) {
			return;
		}

		try {
			// check if player already exists
			PreparedStatement selectId = connection.prepareStatement("SELECT id FROM "+prefix+"player WHERE name = ?");
			selectId.setString(1, player);
			ResultSet rs = selectId.executeQuery();
			if(!rs.next()) {
				// add player to database
				PreparedStatement insertPlayer = connection.prepareStatement("INSERT INTO "+prefix+"player (name) VALUES (?)");
				insertPlayer.setString(1, player);
				if(insertPlayer.executeUpdate() == 0) {
					Bukkit.getLogger().severe("Could not add player to the database: "+player);
					return;
				}
				
				// deposit initial balance
				deposit(player, EcoMain.getPlugin().getConfig().getInt("settings.balance.init"), "initial balance");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deposit(String player, int amount) {
		StringBuilder subject = new StringBuilder();

		for(StackTraceElement s : Thread.currentThread().getStackTrace()) {
			if(s.getClassName().equals("java.lang.Thread") || s.getClassName().equals(getClass().getName())) {
				continue;
			}
			if(s.getClassName().startsWith("sun.reflect.")) {
				break;
			}
			String[] split = s.getClassName().split("\\.");
			subject.insert(0, String.format("%s.%s()%s", split[split.length-1], s.getMethodName(), subject.length() == 0 ? "" : "->"));
		}

		deposit(player, amount, subject.toString());
	}

	public void deposit(String player, int amount, String subject) {
		try {
			PreparedStatement insertBalance = connection.prepareStatement("INSERT INTO "+prefix+"transaction (time, player, amount, subject) VALUES (?, (SELECT id FROM "+prefix+"player WHERE name = ?), ?, ?)");
			insertBalance.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			insertBalance.setString(2, player);
			insertBalance.setInt(3, amount);
			insertBalance.setString(4, subject);
			insertBalance.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getBalance(String player) {
		if(balances.containsKey(player)) {
			return balances.get(player);
		}
		
		try {
			PreparedStatement getBalance = connection.prepareStatement("SELECT SUM(amount) FROM "+prefix+"transaction AS t JOIN "+prefix+"player AS p ON t.player = p.id WHERE name = ?");
			getBalance.setString(1, player);
			ResultSet rs = getBalance.executeQuery();
			if(rs.next()) {
				int balance = rs.getInt(1);
				balances.put(player, balance);
				return balance;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}
}
