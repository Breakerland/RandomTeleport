package fr.breakerland.randomteleport.utils;

import net.md_5.bungee.api.ChatColor;

public abstract class Utils {

	public static String parseColors(String input) {
		return ChatColor.translateAlternateColorCodes('&', input);
	}
}