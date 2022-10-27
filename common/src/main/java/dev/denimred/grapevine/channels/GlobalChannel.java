package dev.denimred.grapevine.channels;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.TextFilter.FilteredText;
import net.minecraft.server.players.PlayerList;

import javax.annotation.Nullable;

public class GlobalChannel extends TextChannel {
    @Override
    public String getName() {
        return "global";
    }

    @Override
    public void send(ServerPlayer sender, FilteredText message) {
        PlayerList playerList = sender.server.getPlayerList();

        Component filtered = message.getFiltered().isEmpty() ? TextComponent.EMPTY : format(sender, message.getFiltered());
        Component raw = format(sender, message.getRaw());

        playerList.broadcastMessage(raw, listener -> sender.shouldFilterMessageTo(listener) ? filtered : raw, ChatType.CHAT, sender.getUUID());
    }

    @Override
    public String getShorthand() {
        return "G";
    }

    @Override
    @Nullable
    public ChatFormatting[] getFormatting() {
        return null;
    }
}
