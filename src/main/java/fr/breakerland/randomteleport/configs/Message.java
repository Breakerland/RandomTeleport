package fr.breakerland.randomteleport.configs;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

public enum Message {
	PREFIX("prefix", "&8[&6RandomTeleport&8]"), USAGE("commandHelp", "%prefix% &cPlease use /%cmd% [player] (world)"), NO_WORLD("noWorld", "%prefix% &cYou can't teleport you in this world."), NO_RTP("noRTP", "&cLe RTP n'est utilisable que pour les nouveaux arrivants ! Tu peux toujours utiliser la perle du voyage au /mage");

	private final String key;
	private String message;

	Message(String key, String message) {
		this.key = key;
		this.message = message;
	}

	public static void setup(ConfigurationSection config) {
		for (Message message : values()) {
			if (!config.contains(message.key))
				config.set(message.key, message.message);

			message.set(fr.breakerland.randomteleport.utils.Utils.parseColors(config.getString(message.getKey(), message.message)));
		}
	}

	public void set(String message) {
		this.message = message;
	}

	private String getKey() {
		return key;
	}

	public void sendMessage(CommandSender sender) {
		sender.sendMessage(toString());
	}

	@Override
	public String toString() {
		return message.replaceAll("%prefix%", PREFIX.message);
	}
}