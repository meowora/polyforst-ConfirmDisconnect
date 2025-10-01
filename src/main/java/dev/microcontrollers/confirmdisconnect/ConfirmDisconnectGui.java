package dev.microcontrollers.confirmdisconnect;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class ConfirmDisconnectGui extends GuiScreen {

    private final GuiScreen parentScreen;

    public ConfirmDisconnectGui(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(0, width / 2 - 100, height / 2, 206, 20, "Disconnect"));
        this.buttonList.add(new GuiButton(2, width / 2 - 100, height / 2 + 24, 206, 20, "Return"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.mc.fontRendererObj, "Would you like to disconnect?", width / 2, height / 2 - 24, -1);
        this.drawCenteredString(this.mc.fontRendererObj, "§eThis can be disabled in the config.", width / 2, height / 2 - 12, -1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void actionPerformed(GuiButton button)
    //#if FORGE
            throws IOException
    //#endif
    {
        switch (button.id) {
            case 0:
                button.enabled = false;
                this.mc.theWorld.sendQuittingDisconnectingPacket();
                this.mc.loadWorld(null);
                this.mc.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
                break;

            case 2:
                this.mc.displayGuiScreen(this.parentScreen);
                break;
        }

        super.actionPerformed(button);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode)
        //#if FORGE
            throws IOException
        //#endif
    {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            this.mc.displayGuiScreen(this.parentScreen);
        }

        super.keyTyped(typedChar, keyCode);
    }
}

