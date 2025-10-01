package dev.microcontrollers.confirmdisconnect.mixins;

import dev.microcontrollers.confirmdisconnect.ConfirmDisconnectConfig;
import dev.microcontrollers.confirmdisconnect.ConfirmDisconnectGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngameMenu.class)
public class GuiIngameMenuMixin {

    @Inject(method = "actionPerformed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;isIntegratedServerRunning()Z"), cancellable = true)
    public void actionPreformed(GuiButton button, CallbackInfo ci) {
        if (ConfirmDisconnectConfig.mainToggle) {
            Minecraft.getMinecraft().displayGuiScreen(new ConfirmDisconnectGui(Minecraft.getMinecraft().currentScreen));
            ci.cancel();
        }
    }

}
