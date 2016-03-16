package com.kylantraynor.civilizations.v1_8_R2;

import net.minecraft.server.v1_8_R2.ChatBaseComponent;
import net.minecraft.server.v1_8_R2.IChatBaseComponent;
import net.minecraft.server.v1_8_R2.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R2.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R2.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_8_R2.PlayerConnection;

import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
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