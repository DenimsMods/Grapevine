package dev.denimred.grapevine.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import dev.denimred.grapevine.channels.GrapevineChannels;
import dev.denimred.grapevine.channels.TextChannel;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static dev.denimred.grapevine.Grapevine.MOD_ID;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class GrapevineCommands {
    public static final String MESSAGE = "message";
    public static final String GV = "gv";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection selection) {
        var root = literal(MOD_ID);

        for (TextChannel channel : GrapevineChannels.getChannels()) {
            root.then(literal(channel.getName())
                    .then(msgChannel(channel))
                    .executes(switchChannel(channel)));
        }

        var rootCmdNode = dispatcher.register(root);

        dispatcher.register(literal(GV).redirect(rootCmdNode));
    }

    private static Command<CommandSourceStack> switchChannel(TextChannel channel) {
        return context -> {
            CommandSourceStack source = context.getSource();
            ServerPlayer player = source.getPlayerOrException();
            GrapevineChannels.setPlayerChannel(player, channel, player.acceptsSuccess());
            return SINGLE_SUCCESS;
        };
    }

    private static ArgumentBuilder<CommandSourceStack, ?> msgChannel(TextChannel channel) {
        return argument(MESSAGE, greedyString()).executes(context -> {
            ServerPlayer player = context.getSource().getPlayerOrException();
            String raw = getString(context, MESSAGE);
            player.getTextFilter().processStreamMessage(raw).thenAcceptAsync(text -> channel.send(player, text));
            return SINGLE_SUCCESS;
        });
    }
}
