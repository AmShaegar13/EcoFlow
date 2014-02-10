package de.amshaegar.economy;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EcoListener implements Listener {

	public EcoListener() {
		EcoFlow.getPlugin().getServer().getPluginManager().registerEvents(this, EcoFlow.getPlugin());
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		EcoProvider provider = EcoFlow.getProvider();
		provider.createAccount(p.getName());
	}
}
