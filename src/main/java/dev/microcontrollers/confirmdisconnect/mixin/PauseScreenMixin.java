package dev.microcontrollers.confirmdisconnect.mixin;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.microcontrollers.confirmdisconnect.ConfirmDisconnectScreen;
import dev.microcontrollers.confirmdisconnect.config.ConfirmDisconnectConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;

@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin<T extends LayoutElement> extends Screen {
    //? if >=1.21.6 {
    @Shadow
    public static void disconnectFromWorld(Minecraft par1, Component par2) { }
    //?} else {
    /*@Shadow protected abstract void onDisconnect();
    @Shadow @Final private static Component RETURN_TO_MENU;
    *///?}

    @Unique private final ConfirmDisconnectConfig config = ConfirmDisconnectConfig.CONFIG.instance();
    @Unique private boolean confirmDisconnect = false;
    @Unique private int delayTicker;
    @Unique private final List<Button> disconnectButtons = Lists.newArrayList();

    protected PauseScreenMixin(Component title) {
        super(title);
    }

    @WrapOperation(method = "createPauseMenu", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;I)Lnet/minecraft/client/gui/layouts/LayoutElement;"))
    private LayoutElement removeDisconnectButton(GridLayout.RowHelper instance, LayoutElement child, int occupiedColumns, Operation<T> original) {
        if (config.useDiscreteConfirmation) {
            boolean isLocal = Minecraft.getInstance().isLocalServer();
            if ((isLocal && config.enableInSingleplayer) || (!isLocal && config.enableInMultiplayer)) {
                return null;
            }
        }
        return original.call(instance, child, occupiedColumns);
    }

    @SuppressWarnings("RedundantCast")
    @Inject(method = "createPauseMenu", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/GridLayout;arrangeElements()V"))
    private void addCustomDisconnectButton(CallbackInfo ci, @Local GridLayout gridLayout, @Local GridLayout.RowHelper rowHelper) {
        if (!config.useDiscreteConfirmation) return;
        this.confirmDisconnect = false;
        this.delayTicker = 0;
        this.disconnectButtons.clear();

        assert this.minecraft != null;
        Component component = this.minecraft.isLocalServer() ? /*? if >=1.21.6 {*/ CommonComponents.GUI_RETURN_TO_MENU /*?} else {*/ /*RETURN_TO_MENU *//*?}*/ : CommonComponents.GUI_DISCONNECT;

        Button disconnectButton = Button.builder(component, (button) -> {
            if (confirmDisconnect) {
                this.minecraft.getReportingContext().draftReportHandled(this.minecraft, (Screen) (Object) this, /*? if >=1.21.6 {*/ () -> disconnectFromWorld(this.minecraft, ClientLevel.DEFAULT_QUIT_MESSAGE) /*?} else {*/ /*this::onDisconnect *//*?}*/, true);
            } else {
                confirmDisconnect = true;
                button.setMessage(Component.translatable("confirm-disconnect.are-you-sure-discrete"));
                this.setButtonsActive(false);
            }
        }).width(204).build();

        rowHelper.addChild(disconnectButton, 2);

        this.disconnectButtons.add(disconnectButton);
    }

    @WrapOperation(method = /*? if fabric {*/ "method_19836" /*?} else {*/ /*"lambda$createPauseMenu$9" *//*?}*/, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/chat/report/ReportingContext;draftReportHandled(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/gui/screens/Screen;Ljava/lang/Runnable;Z)V"))
    private void addConfirmationScreen(ReportingContext instance, Minecraft minecraft, Screen screen, Runnable quitter, boolean quitToTitle, Operation<Void> original) {
        if (config.confirmEnabled && (config.enableInSingleplayer || config.enableInMultiplayer)) {
            boolean isLocal = Minecraft.getInstance().isLocalServer();
            if ((isLocal && config.enableInSingleplayer) || (!isLocal && config.enableInMultiplayer)) {
                Minecraft.getInstance().setScreen(new ConfirmDisconnectScreen(Component.empty(), screen));
            } else {
                original.call(instance, minecraft, screen, quitter, quitToTitle);
            }
        } else {
            original.call(instance, minecraft, screen, quitter, quitToTitle);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickDelay(CallbackInfo ci) {
        if (this.delayTicker < 20 * config.confirmDelay) {
            this.delayTicker++;
        } else {
            this.setButtonsActive(true);
        }
    }

    @Unique
    private void setButtonsActive(boolean active) {
        Button button;
        for(Iterator<Button> var2 = this.disconnectButtons.iterator(); var2.hasNext(); button.active = active) {
            button = var2.next();
        }
    }
}
