package net.greenjab.fixedminecraft.registry.other;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;
import net.minecraft.world.gen.structure.Structure;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SellCompassFactory implements TradeOffers.Factory {
    private final int price;
    private final TagKey<Structure> structure;
    private final int maxUses;
    private final int experience;

    public SellCompassFactory(int price, TagKey<Structure> structure, int maxUses, int experience) {
        this.price = price;
        this.structure = structure;
        this.maxUses = maxUses;
        this.experience = experience;
    }

    @Nullable
    @Override
    public TradeOffer create(Entity entity, Random random) {

        if (entity.getWorld() instanceof ServerWorld serverWorld) {
            BlockPos blockPos = serverWorld.locateStructure(this.structure, entity.getBlockPos(), 100, true);
            if (blockPos != null) {
                ItemStack itemStack = Items.COMPASS.getDefaultStack();
                itemStack.set(DataComponentTypes.LODESTONE_TRACKER, new LodestoneTrackerComponent(Optional.of(GlobalPos.create(entity.getWorld().getRegistryKey(), blockPos.withY(-49))), true));
                return new TradeOffer(
                        new TradedItem(Items.EMERALD, this.price), Optional.of(new TradedItem(Items.COMPASS)), itemStack, this.maxUses, this.experience, 0.2F
                );
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
