package dev.denimred.grapevine.channels;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.TextFilter.FilteredText;

import javax.annotation.Nullable;

public abstract class TextChannel {
    public abstract String getName();

    public abstract void send(ServerPlayer sender, FilteredText message);

    public abstract String getShorthand();

    @Nullable
    public abstract ChatFormatting[] getFormatting();

    protected Component format(ServerPlayer sender, String message) {
        MutableComponent root = new TextComponent("").withStyle(s -> {
            ChatFormatting[] formatting = getFormatting();
            if (formatting != null) return s.applyFormats(formatting);
            return s;
        });
        Component channel = new TextComponent("[" + getShorthand() + "]").withStyle(s -> s.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent("Sent on the '" + getName() + "' channel"))));
        Component senderName = new TextComponent("<").append(sender.getDisplayName()).append(">");
        return root.append(channel).append(" ").append(senderName).append(" ").append(message);
    }

    @Override
    public String toString() {
        return getName();
    }
}
