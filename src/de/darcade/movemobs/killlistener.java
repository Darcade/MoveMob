package de.darcade.movemobs;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;

public class killlistener implements Listener {

	private Player p = null;
	private Plugin plugin;
	private boolean newuser;
	//String databasedir = plugin.getDataFolder().getAbsolutePath();
	String databasedir = "jdbc:sqlite:plugins/MoveMobs/database.sqlite";
	SQLitehandler sqlitehandler = new SQLitehandler(databasedir);


	public killlistener(Player p, Plugin plugin, boolean newuser) {
		this.p = p;
		this.plugin = plugin;
		this.newuser = newuser;
	}
	
	private boolean checkforwhitelist(String vartocheck) {
		List<String> whitelist = plugin.getConfig().getStringList("mobwhitelist");
		boolean output = false;

		for (String k : whitelist) {
			if (vartocheck.equalsIgnoreCase(k)) {
				output = true;
			}
		}
		return output;
	}
	
	@EventHandler
	public void onEntityHit(EntityDamageByEntityEvent event) {
		String pickedup = plugin.getConfig().getString("Message.pickedup");
		if (event.getDamager() == p) {
			if (checkforwhitelist(event.getEntityType().toString())) {
				//event.setDamage(0);
				event.setCancelled(true);
				
				if (event.getEntityType() == EntityType.HORSE){
					Horse horseentity = (Horse) event.getEntity();
					horseentity.setBaby();
					
				}
				
				p.sendMessage("Mob selected.");
				
				if (newuser) {
					sqlitehandler.setnewmob(p.getDisplayName(), event
							.getEntityType().toString());
					p.sendMessage(ChatColor.GREEN + pickedup);
				} else {
					sqlitehandler.updatemob(p.getDisplayName(), event
								.getEntityType().toString());
					p.sendMessage(ChatColor.GREEN + pickedup);
				}
				EntityDeathEvent.getHandlerList().unregister(plugin);
			}
		}
	}
	
	

}
