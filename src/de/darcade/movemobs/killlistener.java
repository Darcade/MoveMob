package de.darcade.movemobs;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;

public class killlistener implements Listener {

	private Player p = null;
	private Plugin plugin;
	private boolean newuser;
	private SQLitehandler sqlitehandler;

	public killlistener(Player p, Plugin plugin, boolean newuser,
			SQLitehandler sqlitehandler) {
		this.p = p;
		this.plugin = plugin;
		this.newuser = newuser;
		this.sqlitehandler = sqlitehandler;
	}

	private boolean checkforwhitelist(String vartocheck) {
		List<String> whitelist = plugin.getConfig().getStringList(
				"mobwhitelist");
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

				String username = p.getDisplayName();
				String entitytype = event.getEntityType().toString();

				// event.setDamage(0);
				event.setCancelled(true);

				if (event.getEntityType() == EntityType.HORSE) {
					Horse horseentity = (Horse) event.getEntity();

					String color = horseentity.getColor().toString();
					String style = horseentity.getStyle().toString();
					String variant = horseentity.getVariant().toString();

					if (newuser) {
						sqlitehandler.setnewhorse(username, entitytype, color,
								style, variant);
						//new EntityManager(plugin).saveEntity(event.getEntity(), p.getDisplayName());
						p.sendMessage(ChatColor.GREEN + pickedup);
					} else {
						sqlitehandler.updatehorse(username, entitytype, color,
								style, variant);
						p.sendMessage(ChatColor.GREEN + pickedup);
					}

				} else {

					if (newuser) {
						sqlitehandler.setnewmob(username, entitytype);
						p.sendMessage(ChatColor.GREEN + pickedup);
					} else {
						sqlitehandler.updatemob(username, entitytype);
						p.sendMessage(ChatColor.GREEN + pickedup);
					}

				}
				event.getEntity().remove();
				EntityDamageByEntityEvent.getHandlerList().unregister(plugin);
			}
		}
	}

}
