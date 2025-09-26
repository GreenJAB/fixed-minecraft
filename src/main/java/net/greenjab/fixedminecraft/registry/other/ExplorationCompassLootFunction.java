package net.greenjab.fixedminecraft.registry.other;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import net.greenjab.fixedminecraft.registry.ModTags;
import net.greenjab.fixedminecraft.registry.registries.StatusRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapDecorationType;
import net.minecraft.item.map.MapDecorationTypes;
import net.minecraft.item.map.MapState;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.ExplorationMapLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
import net.minecraft.world.gen.structure.Structure;

public class ExplorationCompassLootFunction extends ConditionalLootFunction {
    public static final TagKey<Structure> DEFAULT_DESTINATION = ModTags.LODESTONE_COMPASS;
    public static final int DEFAULT_COLOR = 32767;
    public static final int DEFAULT_SEARCH_RADIUS = 50;
    public static final boolean DEFAULT_SKIP_EXISTING_CHUNKS = true;
    public static final MapCodec<ExplorationCompassLootFunction> CODEC = RecordCodecBuilder.mapCodec(
             instance -> addConditionsField(instance)
                     .and(
                            instance.group(
                                    TagKey.unprefixedCodec(RegistryKeys.STRUCTURE)
                                            .optionalFieldOf("destination", DEFAULT_DESTINATION)
                                            .forGetter( function -> function.destination),
                                    Codec.INT.optionalFieldOf("color", DEFAULT_COLOR).forGetter( function -> function.color),
                                    Codec.INT.optionalFieldOf("search_radius", DEFAULT_SEARCH_RADIUS).forGetter( function -> function.searchRadius),
                                    Codec.BOOL.optionalFieldOf("skip_existing_chunks", DEFAULT_SKIP_EXISTING_CHUNKS).forGetter( function -> function.skipExistingChunks)
                            )
                    )
                    .apply(instance, ExplorationCompassLootFunction::new)
    );
    private final TagKey<Structure> destination;
    private final int color;
    private final int searchRadius;
    private final boolean skipExistingChunks;

    ExplorationCompassLootFunction(
            List<LootCondition> conditions,
            TagKey<Structure> destination,
            int color,
            int searchRadius,
            boolean skipExistingChunks
    ) {
        super(conditions);
        this.destination = destination;
        this.color = color;
        this.searchRadius = searchRadius;
        this.skipExistingChunks = skipExistingChunks;
    }

    @Override
    public LootFunctionType<ExplorationCompassLootFunction> getType() {
        return StatusRegistry.EXPLORATION_COMPASS;
    }

    @Override
    public Set<ContextParameter<?>> getAllowedParameters() {
        return Set.of(LootContextParameters.ORIGIN);
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        if (stack.isOf(Items.COMPASS)) {
            Vec3d vec3d = context.get(LootContextParameters.ORIGIN);
            if (vec3d != null) {
                ServerWorld serverWorld = context.getWorld();
                BlockPos blockPos = serverWorld.locateStructure(this.destination, BlockPos.ofFloored(vec3d), this.searchRadius, this.skipExistingChunks);
                if (blockPos != null) {
                    ItemStack itemStack = Items.COMPASS.getDefaultStack();
                    itemStack.set(DataComponentTypes.LODESTONE_TRACKER, new LodestoneTrackerComponent(Optional.of(GlobalPos.create(serverWorld.getRegistryKey(), blockPos.withY(-49))), true));
                    itemStack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(this.color, true));
                    return itemStack;
                }
            }
        }
        return stack;
    }

    public static ExplorationCompassLootFunction.Builder builder() {
        return new ExplorationCompassLootFunction.Builder();
    }

    public static class Builder extends ConditionalLootFunction.Builder<ExplorationCompassLootFunction.Builder> {
        private int color = 32767;
        private TagKey<Structure> destination = ExplorationMapLootFunction.DEFAULT_DESTINATION;
        private int searchRadius = 50;
        private boolean skipExistingChunks = true;

        protected ExplorationCompassLootFunction.Builder getThisBuilder() {
            return this;
        }

        public ExplorationCompassLootFunction.Builder withColor(int color) {
            this.color = color;
            return this;
        }

        public ExplorationCompassLootFunction.Builder withDestination(TagKey<Structure> destination) {
            this.destination = destination;
            return this;
        }

        public ExplorationCompassLootFunction.Builder searchRadius(int searchRadius) {
            this.searchRadius = searchRadius;
            return this;
        }

        public ExplorationCompassLootFunction.Builder withSkipExistingChunks(boolean skipExistingChunks) {
            this.skipExistingChunks = skipExistingChunks;
            return this;
        }

        @Override
        public LootFunction build() {
            return new ExplorationCompassLootFunction(this.getConditions(), this.destination, this.color, this.searchRadius, this.skipExistingChunks);
        }
    }
}
