package moe.hilaryoi.mc.timerewards;

import org.bukkit.Bukkit;

public class TimeEvent {

	long timeMs;
	String[] commands;

	public TimeEvent (long timeMs, String... commands) {

		this.timeMs = timeMs;
		this.commands = commands;

	}

	public boolean shouldExecute (long timeMs) {

		return timeMs >= this.timeMs;

	}

	public void execute (String playerName) {

		for (String cmd : commands) {

			Bukkit.getServer ().dispatchCommand (Bukkit.getServer ().getConsoleSender (), String.format (cmd, playerName));

		}

	}

	public String getKey () {

		return "timerewards_" + timeMs;
	}


}
