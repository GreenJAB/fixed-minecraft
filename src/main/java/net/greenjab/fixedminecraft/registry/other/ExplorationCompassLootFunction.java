package net.greenjab.fixedminecraft.registry.other;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.greenjab.fixedminecraft.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

public class ExplorationCompassLootFunction extends LootItemConditionalFunction {
    public static final TagKey<Structure> DEFAULT_DESTINATION = ModTags.LODESTONE_COMPASS;
    public static final int DEFAULT_COLOR = 32767;
    public static final int DEFAULT_SEARCH_RADIUS = 50;
    public static final boolean DEFAULT_SKIP_EXISTING_CHUNKS = true;
    public static final MapCodec<ExplorationCompassLootFunction> CODEC = RecordCodecBuilder.mapCodec(
             instance -> commonFields(instance)
                     .and(
                            instance.group(
                                    TagKey.codec(Registries.STRUCTURE)
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
            List<LootItemCondition> conditions,
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
    public @NonNull Set<ContextKey<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.ORIGIN);
    }

    @Override
    public @NonNull MapCodec<? extends LootItemConditionalFunction> codec() {
        return CODEC;
    }

    @Override
    public @NonNull ItemStack run(ItemStack stack, @NonNull LootContext context) {
        if (stack.is(Items.COMPASS)) {
            Vec3 vec3d = context.getOptionalParameter(LootContextParams.ORIGIN);
            if (vec3d != null) {
                ServerLevel serverWorld = context.getLevel();
                BlockPos blockPos = serverWorld.findNearestMapStructure(this.destination, BlockPos.containing(vec3d), this.searchRadius, this.skipExistingChunks);
                if (blockPos != null) {
                    ItemStack itemStack = Items.COMPASS.getDefaultInstance();
                    itemStack.set(DataComponents.LODESTONE_TRACKER, new LodestoneTracker(Optional.of(GlobalPos.of(serverWorld.dimension(), blockPos.atY(-49))), true));
                    itemStack.set(DataComponents.DYED_COLOR, new DyedItemColor(this.color));
                    return itemStack;
                }
            }
        }
        return stack;
    }
}
