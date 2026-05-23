package net.greenjab.fixedminecraft.registry.other;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public class LibrarianBookLootFunction extends LootItemConditionalFunction {
    private final boolean master;
    private final Optional<HolderSet<Enchantment>> options;
    public static final MapCodec<LibrarianBookLootFunction> CODEC = RecordCodecBuilder.mapCodec(
            i -> commonFields(i)
                    .and(
                            i.group(
                                    RegistryCodecs.homogeneousList(Registries.ENCHANTMENT).optionalFieldOf("options").forGetter( f -> f.options),
                                    Codec.BOOL.optionalFieldOf("master", false).forGetter( f -> f.master)
                            )
                    ).apply(i, LibrarianBookLootFunction::new)
    );

    private LibrarianBookLootFunction(final List<LootItemCondition> predicates,
                                      final Optional<HolderSet<Enchantment>> options, final boolean master) {
        super(predicates);
        this.master = master;
        this.options = options;
    }

    @Override
    public @NonNull Set<ContextKey<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.ADDITIONAL_COST_COMPONENT_ALLOWED);
    }

    @Override
    public @NonNull MapCodec<LibrarianBookLootFunction> codec() {
        return CODEC;
    }

    @Override
    public @NonNull ItemStack run(final @NonNull ItemStack itemStack, final @NonNull LootContext context) {
        return !master ? firstBook(context) : masterBook(context);
    }

    @Unique
    private ItemStack firstBook(LootContext context) {
        RandomSource rn = context.getRandom();

        Stream<Holder<Enchantment>> compatibleEnchantmentsStream = (this.options
                .map(HolderSet::stream)
                .orElseGet( () -> context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).listElements().map(Function.identity())));
        List<Holder<Enchantment>> compatibleEnchantments = compatibleEnchantmentsStream.toList();
        Optional<Holder<Enchantment>> optional = Util.getRandomSafe(compatibleEnchantments, rn);

        int i = 0;
        while (i < 10) {
            i++;
            if (optional.isPresent()) {
                Holder<Enchantment> registryEntry = optional.get();
                Enchantment enchantment = registryEntry.value();
                if (enchantment.getMaxLevel() != 1 || registryEntry.is(EnchantmentTags.CURSE)) {
                    i=10;
                } else {
                    optional = Util.getRandomSafe(compatibleEnchantments, rn);
                }
            }
        }

        ItemStack itemStack = new ItemStack(Items.BOOK);
        if (optional.isPresent()) {
            Holder<Enchantment> registryEntry = optional.get();
            Enchantment enchantment = registryEntry.value();
            int maxLevel = enchantment.getMaxLevel();
            int midLevel = Mth.ceil(maxLevel / 2.0);
            int level = maxLevel==1?1:UniformInt.of(1,midLevel).sample(rn);
            itemStack = EnchantmentHelper.createBook(new EnchantmentInstance(registryEntry, level));
            if (context.hasParameter(LootContextParams.ADDITIONAL_COST_COMPONENT_ALLOWED)) {
                itemStack.set(DataComponents.ADDITIONAL_TRADE_COST, 2 + rn.nextInt(5 + level * 10) + 3 * level);
            }
        }

        return itemStack;
    }

    @Unique
    private ItemStack masterBook(LootContext context) {
        RandomSource rn = context.getRandom();

        Stream<Holder<Enchantment>> compatibleEnchantmentsStream = (this.options
                .map(HolderSet::stream)
                .orElseGet( () -> context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).listElements().map(Function.identity())));
        List<Holder<Enchantment>> compatibleEnchantments = compatibleEnchantmentsStream.toList();
        Optional<Holder<Enchantment>> optional = Util.getRandomSafe(compatibleEnchantments, rn);

        HashMap<Holder<Enchantment>, Float> possibleEnchantCount = new HashMap<>();
        compatibleEnchantments.forEach(enchant -> possibleEnchantCount.put(enchant, 0.1f));
        List<Villager> list = context.getLevel().getEntitiesOfClass(Villager.class, AABB.unitCubeFromLowerCorner(context.getOptionalParameter(LootContextParams.ORIGIN)).inflate(32), EntitySelector.LIVING_ENTITY_STILL_ALIVE);
        for (Villager villager2 : list) {
            if (villager2.getVillagerData()
                    .profession().is(VillagerProfession.LIBRARIAN)) {
                ItemStack eBook = getBook(villager2);
                if (eBook.is(Items.ENCHANTED_BOOK)) {
                    for (Holder<Enchantment> e : EnchantmentHelper.getEnchantmentsForCrafting(eBook).keySet()) {
                        if (possibleEnchantCount.containsKey(e)) {
                            possibleEnchantCount.put(e, possibleEnchantCount.get(e) + 1);
                        }
                    }
                }
            }
        }
        possibleEnchantCount.replaceAll((e, v) -> e.is(EnchantmentTags.CURSE) ? 0: 1 / v);
        float max = 0;
        for (float f : possibleEnchantCount.values()) max += f;
        float rand = rn.nextFloat() * max;

        if (max != 0) {
            for (Holder<Enchantment> ee : possibleEnchantCount.keySet()) {
                rand -= possibleEnchantCount.get(ee);
                if (rand <= 0) {
                    optional = Optional.ofNullable(ee);
                    break;
                }
            }
        }

        ItemStack itemStack = new ItemStack(Items.BOOK);
        if (optional.isPresent()) {
            Holder<Enchantment> registryEntry = optional.get();
            Enchantment enchantment = registryEntry.value();
            int maxLevel = enchantment.getMaxLevel();
            int midLevel =  Mth.ceil(maxLevel / 2.0);
            int level = maxLevel == 1 ? 1 : UniformInt.of(midLevel+1,maxLevel).sample(rn);
            itemStack = EnchantmentHelper.createBook(new EnchantmentInstance(registryEntry, level));
            if (context.hasParameter(LootContextParams.ADDITIONAL_COST_COMPONENT_ALLOWED)) {
                itemStack.set(DataComponents.ADDITIONAL_TRADE_COST, 2 + rn.nextInt(5 + level * 10) + 3 * level);
            }
        }

        return itemStack;
    }

    @Unique
    private static ItemStack getBook(Villager villager2) {
        ItemStack eBook = ItemStack.EMPTY;
        if (villager2.getVillagerData().level()==5) {
            if (!villager2.getOffers().isEmpty() && villager2.getOffers().getLast().getResult().is(Items.ENCHANTED_BOOK)) {
                eBook = villager2.getOffers().getLast().getResult();
            }
        }
        return eBook;
    }

}
