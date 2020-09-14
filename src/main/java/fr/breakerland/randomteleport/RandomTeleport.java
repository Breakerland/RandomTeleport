package fr.breakerland.randomteleport;

import java.util.Collection;

import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.earth2me.essentials.Essentials;

import fr.breakerland.randomteleport.commands.RandomTeleportCommand;
import fr.breakerland.randomteleport.configs.Message;
import me.angeschossen.lands.api.integration.LandsIntegration;

public class RandomTeleport extends JavaPlugin {
	private int maxTimeGroup;
	private String[] teleportPriority;
	private World fallbackWorld;
	private Collection<String> disabledWorlds;
	private Essentials essentials;
	private LandsIntegration lands;

	@Override
	public void onEnable() {
		saveDefaultConfig();

		FileConfiguration config = getConfig();
		maxTimeGroup = config.getInt("maxTimeGroup", 20);
		teleportPriority = config.getString("teleportPriority", "home, land, rtp").trim().split(",");
		fallbackWorld = getServer().getWorld(config.getString("fallbackWorld", getServer().getWorlds().get(0).getName()));
		disabledWorlds = config.getStringList("disabledWorlds");

		PluginCommand randomTeleportCommand = getCommand("randomTeleport");
		randomTeleportCommand.setExecutor(new RandomTeleportCommand(this));
		randomTeleportCommand.setUsage(Message.USAGE.toString());

		if ( (essentials = (Essentials) getServer().getPluginManager().getPlugin("Essentials")) != null)
			getLogger().info("Essentials hook enabled!");

		if (getServer().getPluginManager().getPlugin("Lands") != null) {
			lands = new LandsIntegration(this);
			getLogger().info("Lands hook enabled!");
		}
	}

	public int getMaxTimeGroup() {
		return maxTimeGroup;
	}

	public World getFallbackWorld() {
		return fallbackWorld;
	}

	public boolean isDisabledWorld(World world) {
		return disabledWorlds.contains(world.getName());
	}

	public String[] getTeleportPriority() {
		return teleportPriority;
	}

	public boolean useEssentials() {
		return essentials != null;
	}

	public Essentials getEssentials() {
		return essentials;
	}

	public boolean useLands() {
		return lands != null;
	}

	public LandsIntegration getLands() {
		return lands;
	}
}