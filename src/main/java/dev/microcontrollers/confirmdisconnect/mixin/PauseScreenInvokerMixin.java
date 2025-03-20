package dev.microcontrollers.confirmdisconnect.mixin;

import net.minecraft.client.gui.screens.PauseScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PauseScreen.class)
public interface PauseScreenInvokerMixin {
    @Invoker
    void invokeOnDisconnect();
}
