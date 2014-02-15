package de.amshaegar.economy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Locale;

import org.bukkit.Bukkit;

import de.amshaegar.economy.db.SQLConnector;

/**
 * @author AmShaegar
 *
 */
public class EcoProvider {

	private HashMap<String, Float> balances = new HashMap<String, Float>();
	private SQLConnector connector;

	public EcoProvider() {
		connector = EcoFlow.getConnector();
	}

	/**
	 * Deposit initial balance on a player's account if it doesn't exist.
	 * 
	 * @param player Name of the player
	 */
	public boolean createAccount(String player) {
		if(balances.containsKey(player)) {
			return false;
		}
		
		Connection c = connector.getConnection();

		try {
			// check if player already exists
			PreparedStatement selectId = c.prepareStatement("SELECT id FROM "+connector.getTableName("player")+" WHERE name = ?");
			selectId.setString(1, player);
			ResultSet rs = selectId.executeQuery();
			if(!rs.next()) {
				// add player to database
				PreparedStatement insertPlayer = c.prepareStatement("INSERT INTO "+connector.getTableName("player")+" (name) VALUES (?)");
				insertPlayer.setString(1, player);
				if(insertPlayer.executeUpdate() == 0) {
					Bukkit.getLogger().severe("Could not add player to the database: "+player);
					return false;
				}

				// deposit initial balance
				Transfer t = deposit(player, EcoFlow.getPlugin().getConfig().getInt("settings.balance.init"), "initial balance");
				return t.isSuccess();
			}
		} catch (SQLException e) {
			Bukkit.getLogger().severe("Failed to create account: "+e.getMessage());
		}
		return false;
	}

	/**
	 * Returns the balance of a player's account. Value is cached in a {@code HashMap}.
	 * 
	 * @param player	name of the player.
	 * @return			the balance of the player's account
	 */
	public float getBalance(String player) {
		if(balances.containsKey(player)) {
			return balances.get(player);
		}

		Connection c = connector.getConnection();

		try {
			PreparedStatement getBalance = c.prepareStatement("SELECT SUM(amount) FROM "+connector.getTableName("transfer")+" AS t JOIN "+connector.getTableName("player")+" AS p ON t.player = p.id WHERE name = ?");
			getBalance.setString(1, player);
			ResultSet rs = getBalance.executeQuery();
			if(rs.next()) {
				float balance = rs.getFloat(1);
				balances.put(player, balance);
				return balance;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * Get the calling method and class in this format:
	 * <pre>
	 * 	OuterClass.method()->InnerClass.method()
	 * </pre> 
	 * 
	 * @return	the formatted caller as String.
	 */
	private String getCaller() {
		StringBuilder caller = new StringBuilder();

		for(StackTraceElement s : Thread.currentThread().getStackTrace()) {
			if(s.getClassName().equals("java.lang.Thread")
					|| s.getClassName().equals(getClass().getName())
					|| s.getClassName().startsWith("net.milkbowl.vault.economy.plugins.")) {
				continue;
			}
			if(s.getClassName().startsWith("sun.reflect.")
					|| s.getClassName().startsWith("org.bukkit.")) {
				break;
			}
			String[] split = s.getClassName().split("\\.");
			caller.insert(0, String.format("%s.%s<%d>%s", split[split.length-1], s.getMethodName(), s.getLineNumber(), caller.length() == 0 ? "" : "->"));
		}
		return caller.toString();
	}

	/**
	 * Deposit an amount of money to the player's account. The transfer subject will be
	 * automatically chosen by determining the calling method from the stack trace. This will
	 * result in a subject like:
	 * <pre>
	 * 	ListenerClass.onSell()->PlayerManager.sellItem()
	 * </pre>
	 * 
	 * @param player	name of the player.
	 * @param amount	amount of money to deposit.
	 * @return			<code>true</code> if successful. Otherwise <code>false</code>.
	 */
	public Transfer deposit(String player, float amount) {
		return deposit(player, amount, getCaller());
	}

	/**
	 * Deposit an amount of money to the player's account with a specific transfer subject.
	 * 
	 * @param player	name of the player.
	 * @param amount	amount of money to deposit.
	 * @param subject	the subject for the transfer log.
	 * @return			<code>true</code> if successful. Otherwise <code>false</code>.
	 */
	public Transfer deposit(String player, float amount, String subject) {
		if(amount < 0) {
			return new Transfer(false, "Cannot deposit negative amount.");
		}
		return transfer(player, amount, subject);
	}

	/**
	 * Withdraw an amount of money from the player's account. The transfer subject will be
	 * automatically chosen by determining the calling method from the stack trace. This will
	 * result in a subject like:
	 * <pre>
	 * 	ListenerClass.onBuy()->PlayerManager.buyItem()
	 * </pre>
	 * 
	 * @param player	name of the player.
	 * @param amount	amount of money to deposit.
	 * @return			<code>true</code> if successful. Otherwise <code>false</code>.
	 */
	public Transfer withdraw(String player, float amount) {
		return withdraw(player, amount, getCaller());
	}

	/**
	 * Withdraw an amount of money from the player's account with a specific transfer subject.
	 * 
	 * @param player	name of the player.
	 * @param amount	amount of money to deposit.
	 * @param subject	the subject for the transfer log.
	 * @return			<code>true</code> if successful. Otherwise <code>false</code>.
	 */
	public Transfer withdraw(String player, float amount, String subject) {
		if(amount < 0) {
			return new Transfer(false, "Cannot withdraw negative amount.");
		}
		if(amount > getBalance(player)) {
			return new Transfer(false, "Player does not have that much money.");
		}
		return transfer(player, -amount, subject);
	}

	private Transfer transfer(String player, float amount, String subject) {
		Connection c = connector.getConnection();
		try {
			PreparedStatement insertBalance = c.prepareStatement("INSERT INTO "+connector.getTableName("transfer")+" (time, player, amount, subject) VALUES (?, (SELECT id FROM "+connector.getTableName("player")+" WHERE name = ?), ?, ?)");
			insertBalance.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			insertBalance.setString(2, player);
			insertBalance.setFloat(3, amount);
			insertBalance.setString(4, subject);
			float balance = getBalance(player);
			if(insertBalance.executeUpdate() != 0) {
				balances.put(player, balance+amount);
				return new Transfer(true);
			}
		} catch (SQLException e) {
			return new Transfer(false, "Transfer failed: "+e.getMessage());
		}
		return new Transfer(false, "Transfer failed due to an unknown reason.");
	}

	public String format(float amount) {
		StringBuilder s = new StringBuilder(String.format(Locale.ENGLISH, "%.2f", amount));
		String symbol = EcoFlow.getPlugin().getConfig().getString("settings.currency.symbol");
		if(EcoFlow.getPlugin().getConfig().getBoolean("settings.currency.leading")) {
			s.insert(0, symbol+" ");
		} else {
			s.append(" "+symbol);
		}
		return s.toString();
	}

	protected void clearCache(String player) {
		balances.remove(player);
	}
}
