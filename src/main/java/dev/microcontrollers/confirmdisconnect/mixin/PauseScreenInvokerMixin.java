package dev.microcontrollers.confirmdisconnect.mixin;

import net.minecraft.client.gui.screens.PauseScreen;
import org.spongepowered.asm.mixin.Mixin;
//? if <1.21.6
/*import org.spongepowered.asm.mixin.gen.Invoker;*/

@Mixin(PauseScreen.class)
public interface PauseScreenInvokerMixin {
    //? if <1.21.6 {
    /*@Invoker
    void invokeOnDisconnect();
    *///?}
}
