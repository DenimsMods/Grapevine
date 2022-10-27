package dev.denimred.grapevine.mixin;

import dev.denimred.grapevine.channels.GrapevineChannels;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.server.network.TextFilter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin implements ServerPlayerConnection, ServerGamePacketListener {
    @Shadow
    private int chatSpamTickCount;

    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow
    public ServerPlayer player;

    @Shadow
    public abstract void disconnect(Component textComponent);

    @Inject(method = "handleChat(Lnet/minecraft/server/network/TextFilter$FilteredText;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/TextFilter$FilteredText;getFiltered()Ljava/lang/String;", ordinal = 0), cancellable = true)
    private void onHandleChatBeforeBroadcast(TextFilter.FilteredText filteredText, CallbackInfo ci) {
        ci.cancel();

        GrapevineChannels.getPlayerChannel(player).send(player, filteredText);

        // Copied over from target method
        chatSpamTickCount += 20;
        if (chatSpamTickCount > 200 && !server.getPlayerList().isOp(player.getGameProfile()))
            disconnect(new TranslatableComponent("disconnect.spam"));
    }
}
