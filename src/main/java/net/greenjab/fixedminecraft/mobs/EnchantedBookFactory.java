package net.greenjab.fixedminecraft.mobs;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class EnchantedBookFactory implements TradeOffers.Factory {
    private final ItemStack itemStack;
    private final int cost;
    private final int experience;


    public EnchantedBookFactory(ItemStack itemStack, int cost, int experience) {
        this.itemStack = itemStack;
        this.cost = cost;
        this.experience = experience;
    }

    @Override
    public TradeOffer create(ServerWorld world, Entity entity, Random random) {

        return new TradeOffer(new TradedItem(Items.EMERALD, cost), Optional.of(new TradedItem(Items.BOOK)), itemStack, 3, this.experience, 0.2F);
    }
}
