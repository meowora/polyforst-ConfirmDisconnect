package dev.microcontrollers.confirmdisconnect.mixin;

import dev.microcontrollers.confirmdisconnect.ConfirmDisconnectScreen;
import dev.microcontrollers.confirmdisconnect.config.ConfirmDisconnectConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
//? if <1.21.6
/*import org.spongepowered.asm.mixin.Final;*/
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin<T extends LayoutElement> extends Screen {

    @Unique private static final ConfirmDisconnectConfig config = ConfirmDisconnectConfig.CONFIG.instance();
    @Unique private static boolean confirmDisconnect = false;
    @Unique private static int delayTicker;

    protected PauseScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = /*? if >=1.21.6 {*/ "disconnectFromWorld" /*?} else {*/ /*"onDisconnect" *//*?}*/, at = @At("HEAD"), cancellable = true)
    private /*? if >=1.21.6 {*/ static /*?}*/ void onDisconnect(/*? if >=1.21.6 {*/ Minecraft minecraft, Component component, /*?}*/ CallbackInfo ci) {
        if (minecraft.screen instanceof ConfirmDisconnectScreen) {
            return;
        }

        boolean isLocal = minecraft.isLocalServer();
        if (config.confirmEnabled && !confirmDisconnect && ((isLocal && config.enableInSingleplayer) || (!isLocal && config.enableInMultiplayer))) {
            delayTicker = 0;
            if (config.useDiscreteConfirmation) {
                PauseScreenAccessor accessor = (PauseScreenAccessor) minecraft.screen;
                if (accessor.getDisconnectButton() != null) {
                    accessor.getDisconnectButton().active = false;
                    accessor.getDisconnectButton().setMessage(Component.translatable("confirm-disconnect.are-you-sure-discrete"));
                    confirmDisconnect = true;
                }
            } else {
                minecraft.setScreen(new ConfirmDisconnectScreen(Component.empty(), minecraft.screen));
            }
            ci.cancel();
        }
    }

    @Inject(method = "createPauseMenu", at = @At("HEAD"))
    private void addCustomDisconnectButton(CallbackInfo ci) {
        confirmDisconnect = false;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickDelay(CallbackInfo ci) {
        if (delayTicker < 20 * config.confirmDelay) {
            delayTicker++;
        } else {
            if (minecraft.screen instanceof PauseScreen screen) {
                PauseScreenAccessor accessor = (PauseScreenAccessor) screen;
                accessor.getDisconnectButton().active = true;
            }
        }
    }
}
