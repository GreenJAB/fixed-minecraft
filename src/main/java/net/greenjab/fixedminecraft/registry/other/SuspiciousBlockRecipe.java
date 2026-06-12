package net.greenjab.fixedminecraft.registry.other;


import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.NormalCraftingRecipe;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jspecify.annotations.NonNull;
import java.util.List;
import java.util.stream.Stream;

public class SuspiciousBlockRecipe extends NormalCraftingRecipe {
    public static final MapCodec<SuspiciousBlockRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
           i -> i.group(
                            Recipe.CommonInfo.MAP_CODEC.forGetter(o -> o.commonInfo),
                            CraftingRecipe.CraftingBookInfo.MAP_CODEC.forGetter(o -> o.bookInfo),
                            Ingredient.CODEC.fieldOf("base").forGetter(o -> o.base),
                            Ingredient.CODEC.fieldOf("inside").forGetter(o -> o.inside),
                            ItemStackTemplate.CODEC.fieldOf("result").forGetter(o -> o.result)
                    )
                    .apply(i, SuspiciousBlockRecipe::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, SuspiciousBlockRecipe> STREAM_CODEC = StreamCodec.composite(
            Recipe.CommonInfo.STREAM_CODEC,
            o -> o.commonInfo,
            CraftingRecipe.CraftingBookInfo.STREAM_CODEC,
            o -> o.bookInfo,
            Ingredient.CONTENTS_STREAM_CODEC,
            o -> o.base,
            Ingredient.CONTENTS_STREAM_CODEC,
            o -> o.inside,
            ItemStackTemplate.STREAM_CODEC,
            o -> o.result,
            SuspiciousBlockRecipe::new
    );
    public static final RecipeSerializer<SuspiciousBlockRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);
    private final Ingredient base;
    private final Ingredient inside;
    private final ItemStackTemplate result;

    public SuspiciousBlockRecipe(
            final Recipe.CommonInfo commonInfo,
            final CraftingRecipe.CraftingBookInfo bookInfo,
            final Ingredient target,
            final Ingredient dye,
            final ItemStackTemplate result
    ) {
        super(commonInfo, bookInfo);
        this.base = target;
        this.inside = dye;
        this.result = result;
    }

    public boolean matches(final CraftingInput input, final @NonNull Level level) {
        if (input.ingredientCount() != 2) {
            return false;
        } else {
            boolean hasBase = false;
            boolean hasInside = false;

            for (int slot = 0; slot < input.size(); slot++) {
                ItemStack itemStack = input.getItem(slot);
                if (!itemStack.isEmpty()) {
                    if (this.base.test(itemStack)) {
                        if (hasBase) hasInside = true;
                        hasBase = true;
                    } else {
                        if (this.inside.test(itemStack)) return false;
                        hasInside = true;
                    }
                }
            }

            return hasInside && hasBase;
        }
    }

    public @NonNull ItemStack assemble(final CraftingInput input) {
        ItemStack baseStack = ItemStack.EMPTY;
        ItemStack insideStack = ItemStack.EMPTY;

        for (int slot = 0; slot < input.size(); slot++) {
            ItemStack itemStack = input.getItem(slot);
            if (!itemStack.isEmpty()) {
                if (this.base.test(itemStack)) {
                    if (!baseStack.isEmpty()) insideStack = itemStack;
                    baseStack = itemStack;
                } else {
                    if (this.inside.test(itemStack)) return ItemStack.EMPTY;
                    insideStack = itemStack;
                }
            }
        }

        if (!baseStack.isEmpty() && !insideStack.isEmpty()) {
            ItemStack result = this.result.create();
            CompoundTag tag = new CompoundTag();
            RegistryOps<Tag> ops = FixedMinecraft.SERVER.reloadableRegistries().lookup().createSerializationContext(NbtOps.INSTANCE);
            tag.store("item", ItemStack.CODEC, ops, insideStack);
            result.set(DataComponents.BLOCK_ENTITY_DATA, TypedEntityData.of(BlockEntityType.BRUSHABLE_BLOCK, tag));
            return result;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public @NonNull RecipeSerializer<SuspiciousBlockRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    protected @NonNull PlacementInfo createPlacementInfo() {
        return PlacementInfo.create(List.of(this.base, this.inside));
    }

    @Override
    public @NonNull List<RecipeDisplay> display() {
        return List.of(
                new ShapelessCraftingRecipeDisplay(
                        Stream.of(base).map(Ingredient::display).toList(),
                        new SlotDisplay.ItemStackSlotDisplay(this.result),
                        new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)
                )
        );
    }

    public @NonNull NonNullList<ItemStack> getRemainingItems(final CraftingInput input) {
        return NonNullList.withSize(input.size(), ItemStack.EMPTY);
    }
}
