package net.greenjab.fixedminecraft.config;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.FixedMinecraftClient;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class SodiumCompat implements ConfigEntryPoint {
    @Override
    public void registerConfigLate(ConfigBuilder builder) {
        builder.registerOwnModOptions()
                .setName("Jab's Fixed Mod")
                .addPage(builder.createOptionPage()
                        .setName(Component.literal("Options"))
                        .addOption(builder.createBooleanOption(Identifier.parse(FixedMinecraft.NAMESPACE + ":sodium_armor_hud"))
                                .setName(Component.translatable("options.itemArmorHud"))
                                .setStorageHandler(()->{})
                                .setBinding(FixedMinecraftClient.itemArmorHud::set, FixedMinecraftClient.itemArmorHud::get)
                                .setTooltip(Component.literal("Show armor pieces instead of points"))
                                .setDefaultValue(true)
                        )
                        .addOption(builder.createBooleanOption(Identifier.parse(FixedMinecraft.NAMESPACE + ":sodium_fixed_fog"))
                                .setName(Component.translatable("options.jabsFixedFog"))
                                .setStorageHandler(()->{})
                                .setBinding(FixedMinecraftClient.jabsFixedFog::set, FixedMinecraftClient.jabsFixedFog::get)
                                .setTooltip(Component.literal("Disable new 1.21.6 fog"))
                                .setDefaultValue(true)
                        )
                        .addOption(builder.createBooleanOption(Identifier.parse(FixedMinecraft.NAMESPACE + ":sodium_villagers_speak"))
                                .setName(Component.translatable("options.chat.villagersSpeak"))
                                .setStorageHandler(()->{})
                                .setBinding(FixedMinecraftClient.villagersSpeak::set, FixedMinecraftClient.villagersSpeak::get)
                                .setTooltip(Component.literal("Villagers speaking in chat"))
                                .setDefaultValue(true)
                        )
                );
    }
}
