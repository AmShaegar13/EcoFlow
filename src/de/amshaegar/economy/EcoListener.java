package de.amshaegar.economy;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EcoListener implements Listener {

	public EcoListener() {
		EcoMain.getPlugin().getServer().getPluginManager().registerEvents(this, EcoMain.getPlugin());
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		EcoProvider provider = EcoMain.getProvider();
		provider.createAccount(p.getName());
		p.sendMessage(String.format("Welcome back, %s. Balance: %d", p.getName(), provider.getBalance(event.getPlayer().getName())));
	}
}
