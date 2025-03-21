package dev.microcontrollers.confirmdisconnect.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.microcontrollers.confirmdisconnect.ConfirmDisconnectScreen;
import dev.microcontrollers.confirmdisconnect.config.ConfirmDisconnectConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PauseScreen.class)
public class PauseScreenMixin {
    @WrapOperation(method = /*? if fabric {*/ "method_19836" /*?} else {*/ /*"lambda$createPauseMenu$9" *//*?}*/, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/chat/report/ReportingContext;draftReportHandled(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/gui/screens/Screen;Ljava/lang/Runnable;Z)V"))
    private void ad(ReportingContext instance, Minecraft minecraft, Screen screen, Runnable quitter, boolean quitToTitle, Operation<Void> original) {
        if (ConfirmDisconnectConfig.CONFIG.instance().confirmEnabled) {
            Minecraft.getInstance().setScreen(new ConfirmDisconnectScreen(Component.empty(), screen));
        } else {
            original.call(instance, minecraft, screen, quitter, quitToTitle);
        }
    }
}
