package dev.microcontrollers.confirmdisconnect;

import dev.microcontrollers.confirmdisconnect.mixin.PauseScreenInvokerMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfirmDisconnectScreen extends Screen {
    public Screen parent;

    public ConfirmDisconnectScreen(Component title, Screen parent) {
        super(title);
        this.parent = parent;
    }

    @Override
    protected void init() {
        Button cancelWidget = Button.builder(Component.translatable("confirm-disconnect.back"), (btn) -> onClose()).bounds((width >> 1) - 125, (height >> 1) - 34, 100, 20).build();

        boolean isLocal = Minecraft.getInstance().isLocalServer();
        Button disconnectWidget = Button.builder(isLocal ? Component.translatable("confirm-disconnect.quit-local") : Component.translatable("confirm-disconnect.disconnect-multiplayer"), (btn) -> {
            assert this.minecraft != null;
            PauseScreen pauseScreen = (PauseScreen) parent;
            this.minecraft.getReportingContext().draftReportHandled(this.minecraft, this, ((PauseScreenInvokerMixin)pauseScreen)::invokeOnDisconnect, true);
        }).bounds((width >> 1) + 25, (height >> 1) - 34, 100, 20).build();

        this.addRenderableWidget(cancelWidget);
        this.addRenderableWidget(disconnectWidget);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        boolean isLocal = Minecraft.getInstance().isLocalServer();
        graphics.drawCenteredString(this.font, isLocal ? Component.translatable("confirm-disconnect.are-you-sure-local") : Component.translatable("confirm-disconnect.are-you-sure-multiplayer"), width >> 1, 40, 0xFFFFFFFF);
    }

    @Override
    public void onClose() {
        assert this.minecraft != null;
        this.minecraft.setScreen(this.parent);
    }
}
