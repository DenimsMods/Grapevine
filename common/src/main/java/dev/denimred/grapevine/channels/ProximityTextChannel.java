package dev.denimred.grapevine.channels;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.TextFilter.FilteredText;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.UUID;

public class ProximityTextChannel extends TextChannel {
    protected final String name;
    protected final int range;
    protected final int rangeSqr;
    protected final char symbol;
    protected final ChatFormatting[] formatting;

    public ProximityTextChannel(String name, int range, char symbol, ChatFormatting... formatting) {
        if (range <= 0) throw new IllegalArgumentException("Range must be >0");
        this.name = name;
        this.range = range;
        rangeSqr = range * range;
        this.symbol = symbol;
        this.formatting = formatting;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getRange() {
        return range;
    }

    public int getRangeSqr() {
        return rangeSqr;
    }

    public String getShorthand() {
        return String.valueOf(symbol);
    }

    public ChatFormatting[] getFormatting() {
        return Arrays.copyOf(formatting, formatting.length);
    }

    @Override
    public void send(ServerPlayer sender, FilteredText message) {
        int count = 0;

        Component filtered = message.getFiltered().isEmpty() ? TextComponent.EMPTY : format(sender, message.getFiltered());
        Component raw = format(sender, message.getRaw());

        Vec3 srcPos = sender.getEyePosition();
        UUID uuid = sender.getUUID();

        for (ServerPlayer listener : sender.getLevel().players()) {
            if (sender.is(listener)) continue;
            Vec3 dstPos = listener.getEyePosition();
            if (srcPos.distanceToSqr(dstPos) > rangeSqr) continue;
            if (!listener.isSpectator()) count++;
            listener.sendMessage(sender.shouldFilterMessageTo(listener) ? filtered : raw, uuid);
        }

        if (count > 0) {
            sender.sendMessage(raw, uuid);
        } else {
            Component details = new TextComponent("Nobody heard you... Try changing channels with ").withStyle(ChatFormatting.RED).append(new TextComponent("/gv <channel>").withStyle(ChatFormatting.GOLD));
            MutableComponent root = new TextComponent("");
            MutableComponent warning = new TextComponent("").withStyle(ChatFormatting.RED).withStyle(s -> s.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, details)));
            sender.sendMessage(root.append(warning.append("[").append(new TextComponent("\u26A0").withStyle(ChatFormatting.GOLD)).append("] ")).append(raw), uuid);
        }
    }
}
