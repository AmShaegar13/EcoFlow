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
			if(args.length == 0) {
				if(!(sender instanceof Player)) {
					return false;
				}
				
				Player p = (Player) sender;
				p.sendMessage(String.format("Current balance: %s", eco.format(eco.getBalance(p.getName()))));
			} else if(args.length == 1) {
				if(!sender.hasPermission("ecoflow.admin")) {
					sender.sendMessage(ChatColor.DARK_RED+"You do not have permission to do this.");
					return true;
				}
				OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
				if(!p.hasPlayedBefore()) {
					sender.sendMessage(ChatColor.DARK_RED+"Player '"+args[0]+"' does not exist.");
					return true;
				}
				
				sender.sendMessage(String.format("Balance of %s: %s", p.getName(), eco.format(eco.getBalance(p.getName()))));
			} else {
				return false;
			}
		} else if(commandLabel.equalsIgnoreCase("deposit")) {
			if(!sender.hasPermission("ecoflow.admin")) {
				sender.sendMessage(ChatColor.DARK_RED+"You do not have permission to do this.");
				return true;
			}
			if(args.length != 2) {
				return false;
			}
			OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
			if(!p.hasPlayedBefore()) {
				sender.sendMessage(ChatColor.DARK_RED+"Player "+ChatColor.YELLOW+args[0]+ChatColor.DARK_RED+" does not exist.");
				return true;
			}
			try {
				Float amount = Float.parseFloat(args[1].replace(',', '.'));
				Transaction t = eco.deposit(p.getName(), amount, String.format("deposit by command (%s)", sender.getName()));
				if(!t.isSuccess()) {
					sender.sendMessage(ChatColor.DARK_RED+t.getMessage());
					return true;
				}

				sender.sendMessage(String.format(ChatColor.GREEN+"Deposited %s for %s.", eco.format(amount), p.getName()));
				if(p.isOnline()) {
					((Player) p).sendMessage(String.format(ChatColor.GREEN+"Your account has been credited %s.", eco.format(amount)));
				}
			} catch(NumberFormatException e) {
				sender.sendMessage(String.format(ChatColor.YELLOW+"%s"+ChatColor.DARK_RED+" is not a number.", args[1]));
			}
		} else if(commandLabel.equalsIgnoreCase("withdraw")) {
			if(!sender.hasPermission("ecoflow.admin")) {
				sender.sendMessage(ChatColor.DARK_RED+"You do not have permission to do this.");
				return true;
			}
			if(args.length != 2) {
				return false;
			}
			OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
			if(!p.hasPlayedBefore()) {
				sender.sendMessage(ChatColor.DARK_RED+"Player "+ChatColor.YELLOW+args[0]+ChatColor.DARK_RED+" does not exist.");
				return true;
			}
			try {
				Float amount = Float.parseFloat(args[1].replace(',', '.'));
				Transaction t = eco.withdraw(p.getName(), amount, String.format("withdrawal by command (%s)", sender.getName()));
				if(!t.isSuccess()) {
					sender.sendMessage(ChatColor.DARK_RED+t.getMessage());
					return true;
				}

				sender.sendMessage(String.format(ChatColor.GREEN+"Withdrew %s from %s.", eco.format(amount), p.getName()));
				if(p.isOnline()) {
					((Player) p).sendMessage(String.format(ChatColor.GREEN+"Your account has been deducted %s.", eco.format(amount)));
				}
			} catch(NumberFormatException e) {
				sender.sendMessage(ChatColor.DARK_RED+String.format(ChatColor.YELLOW+"%s"+ChatColor.DARK_RED+" is not a number.", args[1]));
			}
		} else if(commandLabel.equalsIgnoreCase("createacc")) {
			if(!sender.hasPermission("ecoflow.admin")) {
				sender.sendMessage(ChatColor.DARK_RED+"You do not have permission to do this.");
				return true;
			}
			if(args.length != 1) {
				return false;
			}
			OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
			if(p.hasPlayedBefore()) {
				sender.sendMessage(ChatColor.DARK_RED+"Player "+ChatColor.YELLOW+p.getName()+ChatColor.DARK_RED+" already has an account.");
				return true;
			}
			if(!EcoFlow.getProvider().createAccount(p.getName())) {
				sender.sendMessage(ChatColor.DARK_RED+"Something went wrong. Please check the log.");
			} else {
				sender.sendMessage(ChatColor.GREEN+"Account successfully created.");
			}
		}
		return true;
	}

}
