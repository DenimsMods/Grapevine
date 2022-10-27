package dev.denimred.grapevine.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.denimred.grapevine.Grapevine;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static dev.denimred.grapevine.Grapevine.MOD_ID;

@Mod(MOD_ID)
public final class GrapevineForge {
    public GrapevineForge() {
        EventBuses.registerModEventBus(MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        Grapevine.init();
    }
}
