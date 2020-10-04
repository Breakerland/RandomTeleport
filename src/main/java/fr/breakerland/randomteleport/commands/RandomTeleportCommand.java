package fr.breakerland.randomteleport.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.earth2me.essentials.User;

import fr.breakerland.randomteleport.RandomTeleport;
import fr.breakerland.randomteleport.configs.Message;
import me.angeschossen.lands.api.player.LandPlayer;

public class RandomTeleportCommand implements CommandExecutor {
	private final RandomTeleport plugin;
	private Player leader;
	private Long lastLeader;

	public RandomTeleportCommand(RandomTeleport plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = null;
		World world = null;
		boolean group = false;
		boolean force = false;

		if (sender instanceof Player)
			player = (Player) sender;
		else
			for (String arg : args)
				if (!force && arg.equals("force"))
					force = true;
				else if (player == null)
					player = Bukkit.getPlayer(arg);
				else if (world == null)
					world = Bukkit.getWorld(arg);
				else if (group)
					group = Boolean.valueOf(arg);

		if (player == null)
			return false;

		if (force)
			randomTeleport(sender, player, world, group);
		else
			for (String priority : plugin.getTeleportPriority())
				switch (priority) {
				case "home":
					if (plugin.useEssentials()) {
						User user = plugin.getEssentials().getUser(player);
						List<String> homes = user.getHomes();
						if (homes.size() > 0)
							try {
								player.teleport(user.getHome(homes.get(0)));
								if (sender.equals(player))
									Message.NO_RTP.sendMessage(sender);
								break;
							} catch (Exception e) {}
					}
				case "land":
					if (plugin.useLands()) {
						LandPlayer landPlayer = plugin.getLands().getLandPlayer(player.getUniqueId());
						if (landPlayer.ownsLand()) {
							Location landSpawn = landPlayer.getOwningLand().getSpawn();
							if (landSpawn != null) {
								player.teleport(landSpawn);
								if (sender.equals(player))
									Message.NO_RTP.sendMessage(sender);
								break;
							}
						}
					}
				case "rtp":
					randomTeleport(sender, player, world, group);
					break;
				}

		return true;
	}

	private void randomTeleport(CommandSender sender, Player player, World world, boolean group) {
		if (group)
			if (leader != null && canGroupTeleport())
				player.teleport(leader);
			else {
				leader = player;
				lastLeader = System.currentTimeMillis();
			}

		if (world == null)
			world = player.getWorld();

		if (plugin.isDisabledWorld(world) && (world = plugin.getFallbackWorld()) == null)
			Message.NO_WORLD.sendMessage(sender);
		else
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), plugin.getRandomTeleportCommand().replaceAll("%world%", world.getName()).replaceAll("%player%", player.getName()));
	}

	private boolean canGroupTeleport() {
		if (!leader.isOnline() || System.currentTimeMillis() - lastLeader > plugin.getMaxTimeGroup() * 1000) {
			leader = null;
			return false;
		}

		return true;
	}
}