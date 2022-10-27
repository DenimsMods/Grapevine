package dev.denimred.grapevine.fabric;

import dev.denimred.grapevine.Grapevine;
import net.fabricmc.api.ModInitializer;

public final class GrapevineFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Grapevine.init();
    }
}
