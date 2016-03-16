package com.kylantraynor.civilizations.v1_8_R3;

import net.minecraft.server.v1_8_R3.ChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class TitleDisplay {
	public static void send(String title, String subtitle, int in, int out, int stay, Player p){
		PlayerConnection connection = ((CraftPlayer)p).getHandle().playerConnection;
		ChatBaseComponent titleJSON = (ChatBaseComponent) ChatSerializer.a("{'text': '" + title + "'}");
	    IChatBaseComponent subtitleJSON = ChatSerializer.a("{'text': '" + subtitle + "'}");
	    PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleJSON, in, stay, out);
	    PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subtitleJSON);
	    connection.sendPacket(titlePacket);
	    connection.sendPacket(subtitlePacket);
	}
}