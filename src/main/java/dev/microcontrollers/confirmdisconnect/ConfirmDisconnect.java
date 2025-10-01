package dev.microcontrollers.confirmdisconnect;

//#if FORGE
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "confirm-disconnect", version = "@MOD_VERSION")
//#endif
public class ConfirmDisconnect
        //#if FABRIC
        //$$ implements net.fabricmc.api.ClientModInitializer
        //#endif
{
    //#if FORGE
    @Mod.EventHandler
    public void onInitializeClient(FMLInitializationEvent event) {
        if (!event.getSide().isClient()) {
            return;
        }
    //#else
    //$$ @Override
    //$$ public void onInitializeClient() {
    //#endif
        ConfirmDisconnectConfig.INSTANCE.preload();
    }

}
