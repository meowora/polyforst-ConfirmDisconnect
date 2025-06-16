package dev.microcontrollers.confirmdisconnect.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
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
    @SerialEntry public boolean useDiscreteConfirmation = false;
    @SerialEntry public boolean enableInSingleplayer = true;
    @SerialEntry public boolean enableInMultiplayer = true;
    @SerialEntry public double confirmDelay = 0;
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
                                .name(Component.translatable("confirm-disconnect.use-discrete-confirmation"))
                                .description(OptionDescription.of(Component.translatable("confirm-disconnect.use-discrete-confirmation.description")))
                                .binding(defaults.useDiscreteConfirmation, () -> config.useDiscreteConfirmation, newVal -> config.useDiscreteConfirmation = newVal)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("confirm-disconnect.enable-in-singleplayer"))
                                .description(OptionDescription.of(Component.translatable("confirm-disconnect.enable-in-singleplayer.description")))
                                .binding(defaults.enableInSingleplayer, () -> config.enableInSingleplayer, newVal -> config.enableInSingleplayer = newVal)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("confirm-disconnect.enable-in-multiplayer"))
                                .description(OptionDescription.of(Component.translatable("confirm-disconnect.enable-in-multiplayer.description")))
                                .binding(defaults.enableInMultiplayer, () -> config.enableInMultiplayer, newVal -> config.enableInMultiplayer = newVal)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Double>createBuilder()
                                .name(Component.translatable("confirm-disconnect.delay"))
                                .description(OptionDescription.of(Component.translatable("confirm-disconnect.delay.description")))
                                .binding(0.0, () -> config.confirmDelay, newVal -> config.confirmDelay = newVal)
                                .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                                        .formatValue(value -> Component.literal(String.format("%,.1f", value) + "s"))
                                        .range(0.0, 10.0)
                                        .step(0.1))
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
