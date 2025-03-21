package dev.microcontrollers.confirmdisconnect.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfirmDisconnectConfig {
    public static final ConfigClassHandler<ConfirmDisconnectConfig> CONFIG = ConfigClassHandler.createBuilder(ConfirmDisconnectConfig.class)
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve("confirmdisconnect.json"))
                    .build())
            .build();

    @SerialEntry public boolean confirmEnabled = true;
    @SerialEntry public boolean confirmOnLeft = false;

    public static Screen configScreen(Screen parent) {
        return YetAnotherConfigLib.create(CONFIG, ((defaults, config, builder) -> builder
                .title(Component.translatable("confirm-disconnect.confirm-disconnect"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("confirm-disconnect.confirm-disconnect"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("confirm-disconnect.confirm-enabled"))
                                .description(OptionDescription.of(Component.translatable("confirm-disconnect.confirm-enabled.description")))
                                .binding(defaults.confirmEnabled, () -> config.confirmEnabled, newVal -> config.confirmEnabled = newVal)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("confirm-disconnect.confirm-on-left"))
                                .description(OptionDescription.of(Component.translatable("confirm-disconnect.confirm-on-left.description")))
                                .binding(defaults.confirmOnLeft, () -> config.confirmOnLeft, newVal -> config.confirmOnLeft = newVal)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .build())
        )).generateScreen(parent);
    }
}
