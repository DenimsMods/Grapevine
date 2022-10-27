package dev.denimred.grapevine.channels;

import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static dev.denimred.grapevine.Grapevine.LOGGER;

public final class GrapevineChannels {
    private GrapevineChannels() {
        // no-op, utility class
    }

    private static final Map<String, TextChannel> TEXT_CHANNELS = new Object2ReferenceOpenHashMap<>();
    private static final Collection<TextChannel> TEXT_CHANNELS_VIEW = Collections.unmodifiableCollection(TEXT_CHANNELS.values());
    private static final Map<UUID, TextChannel> PLAYER_CHANNELS = new Object2ReferenceOpenHashMap<>();

    public static final GlobalChannel GLOBAL = registerChannel(new GlobalChannel());
    public static final ProximityTextChannel WHISPER = proximity("whisper", 8, ChatFormatting.GRAY, ChatFormatting.ITALIC);
    public static final ProximityTextChannel TALK = proximity("talk", 48, ChatFormatting.AQUA);
    public static final ProximityTextChannel YELL = proximity("yell", 160, ChatFormatting.YELLOW);

    private static TextChannel defaultChannel = TALK;

    private static ProximityTextChannel proximity(String name, int range, ChatFormatting... formatting) {
        return registerChannel(new ProximityTextChannel(name, range, Character.toUpperCase(name.charAt(0)), formatting));
    }

    public static <T extends TextChannel> T registerChannel(T channel) {
        if (TEXT_CHANNELS.putIfAbsent(channel.getName(), channel) != null) {
            LOGGER.warn("Failed to register channel '" + channel + "'", new IllegalArgumentException("Channel '" + channel + "' already registered"));
        } else LOGGER.debug("Registered new channel: {}", channel);
        return channel;
    }

    public static Collection<TextChannel> getChannels() {
        return TEXT_CHANNELS_VIEW;
    }

    public static void setDefaultChannel(TextChannel defaultChannel) {
        GrapevineChannels.defaultChannel = defaultChannel;
    }

    public static TextChannel getDefaultChannel() {
        return defaultChannel;
    }

    public static void setPlayerChannel(ServerPlayer player, @Nullable TextChannel channel, boolean notify) {
        if (channel == null) channel = defaultChannel;

        if (channel != defaultChannel) {
            PLAYER_CHANNELS.put(player.getUUID(), channel);
        } else {
            PLAYER_CHANNELS.remove(player.getUUID());
        }

        if (notify) player.sendMessage(new TextComponent("You are now speaking on the '" + channel + "' text channel").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC), player.getUUID());
    }

    public static TextChannel getPlayerChannel(ServerPlayer player) {
        return PLAYER_CHANNELS.getOrDefault(player.getUUID(), defaultChannel);
    }
}
