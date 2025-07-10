package net.greenjab.fixedminecraft.mixin.other;

import net.greenjab.fixedminecraft.registry.ModTags;
import net.greenjab.fixedminecraft.registry.registries.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BundleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

import static net.greenjab.fixedminecraft.registry.registries.BlockRegistry.CLEAN_COMPASS;
import static net.greenjab.fixedminecraft.registry.registries.BlockRegistry.CLEAN_SIMPLE;

@Mixin(CauldronBehavior.class)
public interface CauldronBehaviorMixin {

    @Shadow
    CauldronBehavior.CauldronBehaviorMap WATER_CAULDRON_BEHAVIOR = null;

    @Inject(method = "registerBehavior", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 9))
    private static void removeDye(CallbackInfo ci) {
        Map<Item, CauldronBehavior> map = WATER_CAULDRON_BEHAVIOR.map();
        map.put(Items.COMPASS, CLEAN_COMPASS);

        map.put(Items.WHITE_STAINED_GLASS, CLEAN_SIMPLE);
        map.put(Items.WHITE_STAINED_GLASS_PANE, CLEAN_SIMPLE);
        map.put(Items.WHITE_TERRACOTTA, CLEAN_SIMPLE);

        map.put(Items.LIGHT_GRAY_WOOL, CLEAN_SIMPLE);
        map.put(Items.LIGHT_GRAY_CARPET, CLEAN_SIMPLE);
        map.put(Items.LIGHT_GRAY_BED, CLEAN_SIMPLE);
        map.put(Items.LIGHT_GRAY_STAINED_GLASS, CLEAN_SIMPLE);
        map.put(Items.LIGHT_GRAY_STAINED_GLASS_PANE, CLEAN_SIMPLE);
        map.put(Items.LIGHT_GRAY_TERRACOTTA, CLEAN_SIMPLE);

        map.put(Items.GRAY_WOOL, CLEAN_SIMPLE);
        map.put(Items.GRAY_CARPET, CLEAN_SIMPLE);
        map.put(Items.GRAY_BED, CLEAN_SIMPLE);
        map.put(Items.GRAY_STAINED_GLASS, CLEAN_SIMPLE);
        map.put(Items.GRAY_STAINED_GLASS_PANE, CLEAN_SIMPLE);
        map.put(Items.GRAY_TERRACOTTA, CLEAN_SIMPLE);

        map.put(Items.BLACK_WOOL, CLEAN_SIMPLE);
        map.put(Items.BLACK_CARPET, CLEAN_SIMPLE);
        map.put(Items.BLACK_BED, CLEAN_SIMPLE);
        map.put(Items.BLACK_STAINED_GLASS, CLEAN_SIMPLE);
        map.put(Items.BLACK_STAINED_GLASS_PANE, CLEAN_SIMPLE);
        map.put(Items.BLACK_TERRACOTTA, CLEAN_SIMPLE);

        map.put(Items.BROWN_WOOL, CLEAN_SIMPLE);
        map.put(Items.BROWN_CARPET, CLEAN_SIMPLE);
        map.put(Items.BROWN_BED, CLEAN_SIMPLE);
        map.put(Items.BROWN_STAINED_GLASS, CLEAN_SIMPLE);
        map.put(Items.BROWN_STAINED_GLASS_PANE, CLEAN_SIMPLE);
        map.put(Items.BROWN_TERRACOTTA, CLEAN_SIMPLE);

        map.put(Items.RED_WOOL, CLEAN_SIMPLE);
        map.put(Items.RED_CARPET, CLEAN_SIMPLE);
        map.put(Items.RED_BED, CLEAN_SIMPLE);
        map.put(Items.RED_STAINED_GLASS, CLEAN_SIMPLE);
        map.put(Items.RED_STAINED_GLASS_PANE, CLEAN_SIMPLE);
        map.put(Items.RED_TERRACOTTA, CLEAN_SIMPLE);

        map.put(Items.ORANGE_WOOL, CLEAN_SIMPLE);
        map.put(Items.ORANGE_CARPET, CLEAN_SIMPLE);
        map.put(Items.ORANGE_BED, CLEAN_SIMPLE);
        map.put(Items.ORANGE_STAINED_GLASS, CLEAN_SIMPLE);
        map.put(Items.ORANGE_STAINED_GLASS_PANE, CLEAN_SIMPLE);
        map.put(Items.ORANGE_TERRACOTTA, CLEAN_SIMPLE);

        map.put(Items.YELLOW_WOOL, CLEAN_SIMPLE);
        map.put(Items.YELLOW_CARPET, CLEAN_SIMPLE);
        map.put(Items.YELLOW_BED, CLEAN_SIMPLE);
        map.put(Items.YELLOW_STAINED_GLASS, CLEAN_SIMPLE);
        map.put(Items.YELLOW_STAINED_GLASS_PANE, CLEAN_SIMPLE);
        map.put(Items.YELLOW_TERRACOTTA, CLEAN_SIMPLE);

        map.put(Items.LIME_WOOL, CLEAN_SIMPLE);
        map.put(Items.LIME_CARPET, CLEAN_SIMPLE);
        map.put(Items.LIME_BED, CLEAN_SIMPLE);
        map.put(Items.LIME_STAINED_GLASS, CLEAN_SIMPLE);
        map.put(Items.LIME_STAINED_GLASS_PANE, CLEAN_SIMPLE);
        map.put(Items.LIME_TERRACOTTA, CLEAN_SIMPLE);

        map.put(Items.GREEN_WOOL, CLEAN_SIMPLE);
        map.put(Items.GREEN_CARPET, CLEAN_SIMPLE);
        map.put(Items.GREEN_BED, CLEAN_SIMPLE);
        map.put(Items.GREEN_STAINED_GLASS, CLEAN_SIMPLE);
        map.put(Items.GREEN_STAINED_GLASS_PANE, CLEAN_SIMPLE);
        map.put(Items.GREEN_TERRACOTTA, CLEAN_SIMPLE);

        map.put(Items.CYAN_WOOL, CLEAN_SIMPLE);
        map.put(Items.CYAN_CARPET, CLEAN_SIMPLE);
        map.put(Items.CYAN_BED, CLEAN_SIMPLE);
        map.put(Items.CYAN_STAINED_GLASS, CLEAN_SIMPLE);
        map.put(Items.CYAN_STAINED_GLASS_PANE, CLEAN_SIMPLE);
        map.put(Items.CYAN_TERRACOTTA, CLEAN_SIMPLE);

        map.put(Items.LIGHT_BLUE_WOOL, CLEAN_SIMPLE);
        map.put(Items.LIGHT_BLUE_CARPET, CLEAN_SIMPLE);
        map.put(Items.LIGHT_BLUE_BED, CLEAN_SIMPLE);
        map.put(Items.LIGHT_BLUE_STAINED_GLASS, CLEAN_SIMPLE);
        map.put(Items.LIGHT_BLUE_STAINED_GLASS_PANE, CLEAN_SIMPLE);
        map.put(Items.LIGHT_BLUE_TERRACOTTA, CLEAN_SIMPLE);

        map.put(Items.BLUE_WOOL, CLEAN_SIMPLE);
        map.put(Items.BLUE_CARPET, CLEAN_SIMPLE);
        map.put(Items.BLUE_BED, CLEAN_SIMPLE);
        map.put(Items.BLUE_STAINED_GLASS, CLEAN_SIMPLE);
        map.put(Items.BLUE_STAINED_GLASS_PANE, CLEAN_SIMPLE);
        map.put(Items.BLUE_TERRACOTTA, CLEAN_SIMPLE);

        map.put(Items.PURPLE_WOOL, CLEAN_SIMPLE);
        map.put(Items.PURPLE_CARPET, CLEAN_SIMPLE);
        map.put(Items.PURPLE_BED, CLEAN_SIMPLE);
        map.put(Items.PURPLE_STAINED_GLASS, CLEAN_SIMPLE);
        map.put(Items.PURPLE_STAINED_GLASS_PANE, CLEAN_SIMPLE);
        map.put(Items.PURPLE_TERRACOTTA, CLEAN_SIMPLE);

        map.put(Items.MAGENTA_WOOL, CLEAN_SIMPLE);
        map.put(Items.MAGENTA_CARPET, CLEAN_SIMPLE);
        map.put(Items.MAGENTA_BED, CLEAN_SIMPLE);
        map.put(Items.MAGENTA_STAINED_GLASS, CLEAN_SIMPLE);
        map.put(Items.MAGENTA_STAINED_GLASS_PANE, CLEAN_SIMPLE);

        map.put(Items.PINK_WOOL, CLEAN_SIMPLE);
        map.put(Items.PINK_CARPET, CLEAN_SIMPLE);
        map.put(Items.PINK_BED, CLEAN_SIMPLE);
        map.put(Items.PINK_STAINED_GLASS, CLEAN_SIMPLE);
        map.put(Items.PINK_STAINED_GLASS_PANE, CLEAN_SIMPLE);
        map.put(Items.PINK_TERRACOTTA, CLEAN_SIMPLE);

    }

}
