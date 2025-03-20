package dev.microcontrollers.confirmdisconnect;

import dev.microcontrollers.confirmdisconnect.config.ConfirmDisconnectConfig;
//? if fabric
import net.fabricmc.api.ModInitializer;
//? if neoforge {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
*///?}


//? if neoforge
/*@Mod(value = "confirmdisconnect", dist = Dist.CLIENT)*/
public class ConfirmDisconnect /*? if fabric {*/ implements ModInitializer /*?}*/ {
    //? if fabric {
    @Override
    public void onInitialize() {
        ConfirmDisconnectConfig.CONFIG.load();
    }
    //?}

    //? if neoforge {
    /*public ConfirmDisconnect() {
        ConfirmDisconnectConfig.CONFIG.load();
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (client, parent) -> ConfirmDisconnectConfig.configScreen(parent));
    }
	*///?}
}
