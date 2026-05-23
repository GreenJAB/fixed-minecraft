package net.greenjab.fixedminecraft.registry.registries;

import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.registry.other.FletchingMenu;
import net.greenjab.fixedminecraft.registry.other.NewAnvilMenu;
import net.greenjab.fixedminecraft.registry.other.NewEnchantmentMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public class MenuRegistry {

    public static final MenuType<FletchingMenu> FLETCHING_SCREEN_HANDLER =
            Registry.register(
                    BuiltInRegistries.MENU,
                    FixedMinecraft.id("fletching"),
                    new MenuType<>(FletchingMenu::new, FeatureFlags.VANILLA_SET)
            );
    public static final MenuType<NewEnchantmentMenu> NEW_ENCHANTMENT_SCREEN_HANDLER =
            Registry.register(
                    BuiltInRegistries.MENU,
                    FixedMinecraft.id("new_enchantment"),
                    new MenuType<>(NewEnchantmentMenu::new, FeatureFlags.VANILLA_SET)
            );
    public static final MenuType<NewAnvilMenu> NEW_ANVIL_SCREEN_HANDLER =
            Registry.register(
                    BuiltInRegistries.MENU,
                    FixedMinecraft.id("new_anvil"),
                    new MenuType<>(NewAnvilMenu::new, FeatureFlags.VANILLA_SET)
            );

    public static void registerMenus() {
        System.out.println("register Menus");
    }
}
