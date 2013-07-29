package net.but2002.minecraft.BukkitSpeak.Commands;

import java.util.List;
import java.util.logging.Level;

import net.but2002.minecraft.BukkitSpeak.BukkitSpeak;
import net.but2002.minecraft.BukkitSpeak.util.MessageUtil;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public abstract class BukkitSpeakCommand {
	
	protected static final int TS_MAXLENGHT = 100;
	private final String[] NAMES;
	
	protected BukkitSpeakCommand(String firstName, String... otherNames) {
		if (firstName == null || firstName.isEmpty()) {
			throw new IllegalArgumentException("A Command did not have a name specified.");
		}
		
		if (otherNames == null) {
			NAMES = new String[] {firstName};
		} else {
			NAMES = new String[otherNames.length + 1];
			NAMES[0] = firstName;
			for (int i = 0; i < otherNames.length; i++) {
				NAMES[i + 1] = otherNames[i];
			}
		}
	}
	
	protected void send(CommandSender sender, Level level, String msg) {
		String m = msg;
		if (sender instanceof Player) {
			m = m.replaceAll("((&|$)([a-fk-orA-FK-OR0-9]))", "\u00A7$3");
			sender.sendMessage(BukkitSpeak.getFullName() + m);
		} else if (sender instanceof ConsoleCommandSender) {
			m = m.replaceAll("((&|$|\u00A7)([a-fk-orA-FK-OR0-9]))", "");
			BukkitSpeak.log().log(level, m);
		} else {
			m = (BukkitSpeak.getFullName() + m).replaceAll("((&|$|\u00A7)([a-fk-orA-FK-OR0-9]))", "");
			sender.sendMessage(m);
		}
	}
	
	public void broadcastMessage(String mcMsg, CommandSender sender) {
		if (mcMsg == null || mcMsg.isEmpty()) return;
		for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
			if (!BukkitSpeak.getMuted(pl)) {
				pl.sendMessage(MessageUtil.toMinecraft(mcMsg, true, BukkitSpeak.getStringManager().getAllowLinks()));
			}
		}
		if (!(sender instanceof Player) || (BukkitSpeak.getStringManager().getLogInConsole())) {
			BukkitSpeak.log().info(MessageUtil.toMinecraft(mcMsg, false, BukkitSpeak.getStringManager().getAllowLinks()));
		}
	}
	
	protected boolean checkCommandPermission(CommandSender sender, String perm) {
		return sender.hasPermission("bukkitspeak.commands." + perm);
	}
	
	protected String combineSplit(int startIndex, String[] string, String seperator) {
		StringBuilder builder = new StringBuilder();
		
		for (int i = startIndex; i < string.length; i++) {
			builder.append(string[i]);
			builder.append(seperator);
		}
		
		builder.deleteCharAt(builder.length() - seperator.length());
		return builder.toString();
	}
	
	public final String getName() {
		return NAMES[0];
	}
	
	public final String[] getNames() {
		return NAMES;
	}
	
	public abstract void execute(CommandSender sender, String[] args);
	
	public abstract List<String> onTabComplete(CommandSender sender, String[] args);
}
