package dev.denimred.grapevine;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.denimred.grapevine.channels.GrapevineChannels;
import dev.denimred.grapevine.command.GrapevineCommands;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Grapevine {
    public static final String MOD_ID = "grapevine";
    public static final Logger LOGGER = LogManager.getLogger("Grapevine");

    public static void init() {
        CommandRegistrationEvent.EVENT.register(GrapevineCommands::register);
        PlayerEvent.PLAYER_JOIN.register(player -> GrapevineChannels.setPlayerChannel(player, null, true));
        PlayerEvent.PLAYER_QUIT.register(player -> GrapevineChannels.setPlayerChannel(player, null, false));
    }
}
