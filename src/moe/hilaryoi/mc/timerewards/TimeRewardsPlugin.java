package moe.hilaryoi.mc.timerewards;

import com.monkeygamesmc.plugin.playerdata.PlayerDataPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TimeRewardsPlugin extends JavaPlugin implements Listener {

	HashMap<UUID, Long> sessions; // player uuid, join time in ms
	TimeEvent[] events;

	PlayerDataPlugin db;

	@Override
	public void onEnable () {

		sessions = new HashMap<> ();

		db = Bukkit.getServicesManager ().load (PlayerDataPlugin.class);

		saveDefaultConfig ();
		FileConfiguration config = getConfig ();

		ArrayList<TimeEvent> eventsList = new ArrayList<> ();

		for (String key : config.getKeys (false)) {

			List<String> commands = config.getStringList (key);

			// config is in mins
			eventsList.add (new TimeEvent (Long.parseLong (key) * 6000, commands.toArray (new String[commands.size ()])));


		}

		getServer ().getPluginManager ().registerEvents (this, this);

	}

	@Override
	public void onDisable () {

		for (Player player : Bukkit.getOnlinePlayers ()) endSession (player.getUniqueId ());

	}

	@EventHandler
	public void onJoin (PlayerJoinEvent e) {

		UUID uuid = e.getPlayer ().getUniqueId ();

		startSession (uuid);

		for (TimeEvent event : events) {

			if (event.shouldExecute (Long.parseLong (db.getPlayerData (uuid).getData (TIME_PLAYED_KEY)))) event.execute (e.getPlayer ().getName ());

		}

	}

	@EventHandler
	public void onLeave (PlayerQuitEvent e) { endSession (e.getPlayer ().getUniqueId ()); }

	void startSession (UUID player) { sessions.put (player, System.currentTimeMillis ()); }

	final String TIME_PLAYED_KEY = "timerewards.timeplayed";

	void endSession (UUID player) { db.setData (player, TIME_PLAYED_KEY, String.valueOf (System.currentTimeMillis () - sessions.get (player))); }


}