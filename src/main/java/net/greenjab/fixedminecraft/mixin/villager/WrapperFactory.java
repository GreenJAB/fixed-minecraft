package net.greenjab.fixedminecraft.mixin.villager;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerType;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public record WrapperFactory(Map<VillagerType, TradeOffers.Factory> typeToFactory) implements TradeOffers.Factory {
    public WrapperFactory(Map<VillagerType, TradeOffers.Factory> typeToFactory) {
        this.typeToFactory = typeToFactory;
    }

    public static WrapperFactory of(TradeOffers.Factory factory, VillagerType... types) {
        return new WrapperFactory((Map) Arrays.stream(types).collect(Collectors.toMap((type) -> {
            return type;
        }, (type) -> {
            return factory;
        })));
    }

    @Nullable
    public TradeOffer create(Entity entity, Random random) {
        if (entity instanceof VillagerDataContainer villagerDataContainer) {
            VillagerType villagerType = villagerDataContainer.getVillagerData().getType();
            TradeOffers.Factory factory = (TradeOffers.Factory)this.typeToFactory.get(villagerType);
            return factory == null ? null : factory.create(entity, random);
        } else {
            return null;
        }
    }

    public Map<VillagerType, TradeOffers.Factory> typeToFactory() {
        return this.typeToFactory;
    }
}
