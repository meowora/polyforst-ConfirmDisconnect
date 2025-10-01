package dev.microcontrollers.confirmdisconnect;

import org.polyfrost.oneconfig.api.config.v1.Config;
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch;

public class ConfirmDisconnectConfig extends Config {

    public static final ConfirmDisconnectConfig INSTANCE = new ConfirmDisconnectConfig();
    @Switch(title = "Main Toggle")
    public static boolean mainToggle = false;

    public ConfirmDisconnectConfig() {
        super("confirm_disconnect.json", "Confirm Disconnect", Category.OTHER);
    }
}
