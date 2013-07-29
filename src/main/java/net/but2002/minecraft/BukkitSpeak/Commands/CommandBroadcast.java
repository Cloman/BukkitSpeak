package net.but2002.minecraft.BukkitSpeak.Commands;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import net.but2002.minecraft.BukkitSpeak.BukkitSpeak;
import net.but2002.minecraft.BukkitSpeak.AsyncQueryUtils.QuerySender;
import net.but2002.minecraft.BukkitSpeak.util.MessageUtil;
import net.but2002.minecraft.BukkitSpeak.util.Replacer;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import de.stefan1200.jts3serverquery.JTS3ServerQuery;

public class CommandBroadcast extends BukkitSpeakCommand {
	
	public CommandBroadcast() {
		super("broadcast");
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!BukkitSpeak.getStringManager().getUseTextServer()) {
			send(sender, Level.WARNING, "&4You need to enable ListenToServerBroadcasts in the config to use this command.");
			return;
		}
		
		if (args.length < 2) {
			send(sender, Level.WARNING, "&aToo few arguments!");
			send(sender, Level.WARNING, "&aUsage: /ts broadcast message");
			return;
		} else if (!BukkitSpeak.getQuery().isConnected()) {
			send(sender, Level.WARNING, "&4Can't communicate with the TeamSpeak server.");
			return;
		}
		
		StringBuilder sb = new StringBuilder();
		for (String s : Arrays.copyOfRange(args, 1, args.length)) {
			sb.append(s);
			sb.append(" ");
		}
		sb.deleteCharAt(sb.length() - 1);
		
		String tsMsg = BukkitSpeak.getStringManager().getMessage("ServerMessage");
		String mcMsg = BukkitSpeak.getStringManager().getMessage("Broadcast");
		
		Replacer r = new Replacer().addSender(sender).addMessage(sb.toString());
		tsMsg = MessageUtil.toTeamspeak(r.replace(tsMsg), true, BukkitSpeak.getStringManager().getAllowLinks());
		mcMsg = r.replace(mcMsg);
		
		if (tsMsg == null || tsMsg.isEmpty()) return;
		QuerySender qs = new QuerySender(BukkitSpeak.getQuery().getCurrentQueryClientServerID(),
				JTS3ServerQuery.TEXTMESSAGE_TARGET_VIRTUALSERVER, tsMsg);
		Bukkit.getScheduler().runTaskAsynchronously(BukkitSpeak.getInstance(), qs);
		broadcastMessage(mcMsg, sender);
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		return null;
	}
}
