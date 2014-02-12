package de.amshaegar.economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EcoExecutor implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		EcoProvider eco = EcoFlow.getProvider();
		if(commandLabel.equalsIgnoreCase("balance")) {
			if(sender instanceof Player) {
				if(args.length == 0) {
					Player p = (Player) sender;
					p.sendMessage(String.format("Current balance: %s", eco.format(eco.getBalance(p.getName()))));
				} else if(args.length == 1) {
					// TODO admin balance
				} else {
					return false;
				}
			} else {
				sender.sendMessage("Only players can do that.");
			}
		} else if(commandLabel.equalsIgnoreCase("deposit")) {
			if(args.length != 2) {
				return false;
			}
			OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
			if(!p.hasPlayedBefore()) {
				sender.sendMessage(ChatColor.DARK_RED+"Player '"+args[0]+"' does not exist.");
				return true;
			}
			Float amount = Float.parseFloat(args[1]);
			Transaction t = eco.deposit(p.getName(), amount, "deposit by command");
			if(t.isSuccess()) {
				sender.sendMessage(String.format(ChatColor.GREEN+"Deposited %s for %s.", eco.format(amount), p.getName()));
				if(p.isOnline()) {
					((Player) p).sendMessage(String.format(ChatColor.GREEN+"Your account has been credited %s.", eco.format(amount)));
				}
			} else {
				sender.sendMessage(ChatColor.DARK_RED+t.getMessage());
			}
		} else if(commandLabel.equalsIgnoreCase("withdraw")) {
			if(args.length != 2) {
				return false;
			}
			OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
			if(!p.hasPlayedBefore()) {
				sender.sendMessage(ChatColor.DARK_RED+"Player '"+args[0]+"' does not exist.");
				return true;
			}
			Float amount = Float.parseFloat(args[1]);
			Transaction t = eco.withdraw(p.getName(), amount, "withdraw by command");
			if(t.isSuccess()) {
				sender.sendMessage(String.format(ChatColor.GREEN+"Withdrew %s from %s.", eco.format(amount), p.getName()));
				if(p.isOnline()) {
					((Player) p).sendMessage(String.format(ChatColor.GREEN+"Your account has been deducted %s.", eco.format(amount)));
				}
			} else {
				sender.sendMessage(ChatColor.DARK_RED+t.getMessage());
			}
		}
		return true;
	}

}
