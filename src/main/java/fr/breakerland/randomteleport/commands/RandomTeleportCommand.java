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
	private final static String LAND_WILD_COMMAND = "l wild %s %s";
	private final RandomTeleport plugin;
	private Player leader;
	private Long lastLeader;

	public RandomTeleportCommand(RandomTeleport plugin) {
		this.plugin = plugin;
	}

	// /rtp [player] (world) (grouped)
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = null;
		World world = null;
		boolean group = false;

		if (sender instanceof Player)
			player = (Player) sender;
		else
			for (String arg : args)
				if (player == null)
					player = Bukkit.getPlayer(arg);
				else if (world == null)
					world = Bukkit.getWorld(arg);
				else if (group)
					group = Boolean.valueOf(arg);

		if (player == null)
			return false;

		for (String priority : plugin.getTeleportPriority())
			switch (priority) {
			case "home":
				if (plugin.useEssentials()) {
					User user = plugin.getEssentials().getUser(player);
					List<String> homes = user.getHomes();
					if (homes.size() > 0)
						try {
							player.teleport(user.getHome(homes.get(0)));
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
							break;
						}
					}
				}
			case "rtp":
				if (group)
					if (leader != null && canGroupTeleport()) {
						player.teleport(leader);
						break;
					} else {
						leader = player;
						lastLeader = System.currentTimeMillis();
					}

				if (world == null)
					world = player.getWorld();

				if (plugin.isDisabledWorld(world) && (world = plugin.getFallbackWorld()) == null)
					Message.NO_WORLD.sendMessage(sender);
				else
					plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), String.format(LAND_WILD_COMMAND, world.getName(), player.getName()));
			}

		return true;
	}

	private boolean canGroupTeleport() {
		if (!leader.isOnline() || System.currentTimeMillis() - lastLeader > plugin.getMaxTimeGroup() * 1000) {
			leader = null;
			return false;
		}

		return true;
	}
}