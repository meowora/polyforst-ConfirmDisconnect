package dev.microcontrollers.confirmdisconnect;

import com.google.common.collect.Lists;
import dev.microcontrollers.confirmdisconnect.config.ConfirmDisconnectConfig;
import dev.microcontrollers.confirmdisconnect.mixin.PauseScreenInvokerMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Iterator;
import java.util.List;

public class ConfirmDisconnectScreen extends Screen {
    public Screen parent;
    private int delayTicker;
    private final List<Button> disconnectButtons = Lists.newArrayList();

    public ConfirmDisconnectScreen(Component title, Screen parent) {
        super(title);
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.delayTicker = 0;
        this.disconnectButtons.clear();
        GridLayout gridLayout = new GridLayout();
        gridLayout.defaultCellSetting().padding(24, 4, 24, 0);
        GridLayout.RowHelper rowHelper = gridLayout.createRowHelper(2);

        boolean isLocal = Minecraft.getInstance().isLocalServer();
        boolean isConfirmOnLeft = ConfirmDisconnectConfig.CONFIG.instance().confirmOnLeft;

        Button backButton = Button.builder(Component.translatable("confirm-disconnect.back"), (button) -> onClose())
                .width(100).build();

        Button disconnectButton = Button.builder(isLocal ?
                Component.translatable("confirm-disconnect.quit-local") :
                Component.translatable("confirm-disconnect.disconnect-multiplayer"), (btn) -> {
            assert this.minecraft != null;
            PauseScreen pauseScreen = (PauseScreen) parent;
            this.minecraft.getReportingContext().draftReportHandled(this.minecraft, this, ((PauseScreenInvokerMixin)pauseScreen)::invokeOnDisconnect, true);
        }).width(100).build();

        if (isConfirmOnLeft) {
            rowHelper.addChild(disconnectButton, 1, gridLayout.newCellSettings().paddingTop(100));
            rowHelper.addChild(backButton, 1, gridLayout.newCellSettings().paddingTop(100));
        } else {
            rowHelper.addChild(backButton, 1, gridLayout.newCellSettings().paddingTop(100));
            rowHelper.addChild(disconnectButton, 1, gridLayout.newCellSettings().paddingTop(100));
        }

        this.disconnectButtons.add(backButton);
        this.disconnectButtons.add(disconnectButton);
        this.setButtonsActive(false);

        gridLayout.arrangeElements();
        FrameLayout.alignInRectangle(gridLayout, 0, 0, this.width, this.height, 0.5F, 0.25F);
        gridLayout.visitWidgets(this::addRenderableWidget);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        boolean isLocal = Minecraft.getInstance().isLocalServer();
        graphics.drawCenteredString(this.font, isLocal ? Component.translatable("confirm-disconnect.are-you-sure-local") : Component.translatable("confirm-disconnect.are-you-sure-multiplayer"), width >> 1, 40, 0xFFFFFFFF);
    }

    public void tick() {
        super.tick();
        if (this.delayTicker >= 20 * ConfirmDisconnectConfig.CONFIG.instance().confirmDelay) {
            this.setButtonsActive(true);
        }
        ++this.delayTicker;
    }

    private void setButtonsActive(boolean active) {
        Button button;
        for(Iterator<Button> var2 = this.disconnectButtons.iterator(); var2.hasNext(); button.active = active) {
            button = var2.next();
        }
    }

    @Override
    public void onClose() {
        assert this.minecraft != null;
        this.minecraft.setScreen(this.parent);
    }
}
