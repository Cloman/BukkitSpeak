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
import org.bukkit.entity.Player;

import de.stefan1200.jts3serverquery.JTS3ServerQuery;

public class CommandReply extends BukkitSpeakCommand {
	
	public CommandReply() {
		super("reply", "r");
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length < 2) {
			send(sender, Level.WARNING, "&aToo few arguments!");
			send(sender, Level.WARNING, "&aUsage: /ts r(eply) message");
			return;
		} else if (!BukkitSpeak.getQuery().isConnected()) {
			send(sender, Level.WARNING, "&4Can't communicate with the TeamSpeak server.");
			return;
		}
		
		Integer clid;
		if (sender instanceof Player) {
			clid = BukkitSpeak.getInstance().getSender(((Player) sender).getName());
		} else {
			String n = MessageUtil.toMinecraft(BukkitSpeak.getStringManager().getConsoleName(), false, false);
			clid = BukkitSpeak.getInstance().getSender(n);
		}
		
		if (clid == null || !BukkitSpeak.getClientList().containsID(clid)) {
			send(sender, Level.WARNING, "&4Nobody has sent you a PM yet.");
			return;
		}
		
		StringBuilder sb = new StringBuilder();
		for (String s : Arrays.copyOfRange(args, 1, args.length)) {
			sb.append(s);
			sb.append(" ");
		}
		sb.deleteCharAt(sb.length() - 1);
		
		String tsMsg = BukkitSpeak.getStringManager().getMessage("PrivateMessage");
		String mcMsg = BukkitSpeak.getStringManager().getMessage("Pm");
		
		Replacer r = new Replacer().addSender(sender).addMessage(sb.toString());
		r.addTargetClient(BukkitSpeak.getClientList().get(clid));
		tsMsg = MessageUtil.toTeamspeak(r.replace(tsMsg), true, BukkitSpeak.getStringManager().getAllowLinks());
		mcMsg = r.replace(mcMsg);
		
		if (tsMsg == null || tsMsg.isEmpty()) return;
		QuerySender qs = new QuerySender(clid, JTS3ServerQuery.TEXTMESSAGE_TARGET_CLIENT, tsMsg);
		Bukkit.getScheduler().runTaskAsynchronously(BukkitSpeak.getInstance(), qs);
		if (mcMsg == null || mcMsg.isEmpty()) return;
		if (sender instanceof Player) {
			sender.sendMessage(MessageUtil.toMinecraft(mcMsg, true, BukkitSpeak.getStringManager().getAllowLinks()));
		} else {
			BukkitSpeak.log().info(MessageUtil.toMinecraft(mcMsg, false, BukkitSpeak.getStringManager().getAllowLinks()));
		}
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		return null;
	}
}
