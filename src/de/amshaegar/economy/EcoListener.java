package de.amshaegar.economy;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EcoListener implements Listener {

	public EcoListener() {
		EcoFlow.getPlugin().getServer().getPluginManager().registerEvents(this, EcoFlow.getPlugin());
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		EcoFlow.getProvider().createAccount(event.getPlayer().getName());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		EcoFlow.getProvider().clearCache(event.getPlayer().getName());
	}
}
