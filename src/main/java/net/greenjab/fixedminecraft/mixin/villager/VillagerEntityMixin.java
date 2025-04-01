package net.greenjab.fixedminecraft.mixin.villager;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.registry.ModTags;
import net.greenjab.fixedminecraft.mobs.EnchantedBookFactory;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityInteraction;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;
import java.util.Optional;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity {

    @Shadow
    public abstract void sleep(BlockPos pos);

    public VillagerEntityMixin(EntityType<? extends VillagerEntityMixin> entityType, World world) {
        super(entityType, world);
    }

    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;[Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList;"))
    private static ImmutableList<MemoryModuleType<?>> addModules(ImmutableList<MemoryModuleType<?>> original) {
        return ImmutableList.of(
                MemoryModuleType.HOME, MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, MemoryModuleType.MEETING_POINT,
                MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.VISIBLE_VILLAGER_BABIES,
                MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER,
                MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemoryModuleType.WALK_TARGET,
                MemoryModuleType.LOOK_TARGET, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.BREED_TARGET, MemoryModuleType.PATH,
                MemoryModuleType.DOORS_TO_CLOSE, MemoryModuleType.NEAREST_BED, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY,
                MemoryModuleType.NEAREST_HOSTILE, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleType.HIDING_PLACE,
                MemoryModuleType.HEARD_BELL_TIME, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.LAST_SLEPT,
                MemoryModuleType.LAST_WOKEN, MemoryModuleType.LAST_WORKED_AT_POI, MemoryModuleType.GOLEM_DETECTED_RECENTLY,
                MemoryModuleType.ROAR_TARGET, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT);
    }

    @Redirect(method = "summonGolem", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"))
    private int summon1GolemPerHostileMob(List<VillagerEntity> instance, @Local(argsOnly = true) int requiredCount) {
        if (instance.size()>=requiredCount) {
            VillagerEntity villagerEntity = (VillagerEntity) (Object) this;
            if (villagerEntity.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_HOSTILE)) {
                LivingEntity enemy = villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_HOSTILE).get();
                if (enemy.getCommandTags().contains("iron_golem")) {
                    return 0;
                }
                else {
                    if (eat(villagerEntity)) {
                        enemy.addCommandTag("iron_golem");
                    }
                    else {
                        return 0;
                    }
                }
            }
        }
        return instance.size();
    }

    @Unique
    private boolean eat(VillagerEntity villagerEntity) {
        for(int i = 0; i < villagerEntity.getInventory().size(); ++i) {
            ItemStack itemStack = villagerEntity.getInventory().getStack(i);
            if (!itemStack.isEmpty()) {
                Integer integer = VillagerEntity.ITEM_FOOD_VALUES.get(itemStack.getItem());
                if (integer != null) {
                    villagerEntity.getInventory().removeStack(i, 1);
                    return true;
                }
            }
        }
        return false;
    }

    @Inject(method = "onInteractionWith", at = @At(value = "INVOKE", target = "Lnet/minecraft/village/VillagerGossips;startGossip(Ljava/util/UUID;Lnet/minecraft/village/VillagerGossipType;I)V", ordinal = 2))
    private void rideCamel(EntityInteraction interaction, Entity entity, CallbackInfo ci){
        VillagerEntity villagerEntity = (VillagerEntity)(Object)this;
        if (villagerEntity.hasVehicle()) {
            Entity vehicle = villagerEntity.getVehicle();
            assert vehicle != null;
            if (vehicle.getType() == EntityType.CAMEL) {
                villagerEntity.stopRiding();
            }
        } else {
            if (entity.hasVehicle()) {
                Entity vehicle = entity.getVehicle();
                assert vehicle != null;
                if (vehicle.getType() == EntityType.CAMEL) {
                    List<Entity> passengers = vehicle.getPassengerList();
                    if (passengers.size() == 1) {
                        villagerEntity.startRiding(vehicle);
                    }
                }
            }
        }
    }

    @Inject(method = "sleep", at = @At("HEAD"), cancellable = true)
    private void requirePrivacy(BlockPos pos, CallbackInfo ci){
        VillagerEntity villagerEntity = (VillagerEntity)(Object)this;
        List<VillagerEntity> list = villagerEntity.getWorld().getEntitiesByClass(VillagerEntity.class, villagerEntity.getBoundingBox().expand(15, 5, 15), EntityPredicates.VALID_LIVING_ENTITY);
        int canSee = 0;
        for (VillagerEntity villager : list) {
            if (villager != villagerEntity) {
                if (!villager.isBaby()&&!villagerEntity.isBaby()) {
                    if (villagerEntity.canSee(villager)) canSee++;
                }
            }
        }
        if (canSee>1)ci.cancel();
        villagerEntity.getBrain().remember(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, 0);
    }

    @Inject(method = "talkWithVillager", at = @At("HEAD"))
    private void talktime(ServerWorld world, VillagerEntity villager, long time, CallbackInfo ci){
        VillagerEntity villagerEntity = (VillagerEntity)(Object)this;
        Optional<LivingEntity> lastVillager = villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.ROAR_TARGET);
        if (lastVillager!=null && lastVillager.isPresent()) {
            if (lastVillager.get().getUuid() != villager.getUuid()) {
                villagerEntity.getBrain().remember(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, 0);
                villagerEntity.getBrain().remember(MemoryModuleType.ROAR_TARGET, villager);
            }
        } else {
            villagerEntity.getBrain().remember(MemoryModuleType.ROAR_TARGET, villager);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void increaseStats(CallbackInfo ci){
        VillagerEntity villagerEntity = (VillagerEntity)(Object)this;

        Optional<Integer> sleepTime = villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT);
        if (sleepTime!=null && sleepTime.isPresent()) {
            villagerEntity.getBrain().remember(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, sleepTime.get()+1);
        } else {
            villagerEntity.getBrain().remember(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, 0);
        }
        Optional<Integer> gossipTime = villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT);
        if (gossipTime!=null && gossipTime.isPresent()) {
            villagerEntity.getBrain().remember(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, gossipTime.get()+1);
        } else {
            villagerEntity.getBrain().remember(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, 0);
        }
    }

    @Inject(method = "prepareOffersFor", at = @At("TAIL"))
    private void happiness(PlayerEntity player, CallbackInfo ci) {
        VillagerEntity villagerEntity = (VillagerEntity)(Object)this;
        Optional<Integer> sleepTime = villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT);
        int timeSinceSleep = 0;
        if (sleepTime!=null && sleepTime.isPresent()) {
            timeSinceSleep = sleepTime.get();
        } else {
            villagerEntity.getBrain().remember(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, 0);
        }
        Optional<Integer> gossipTime = villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT);
        int timeSinceGossip = 0;
        if (gossipTime!=null && gossipTime.isPresent()) {
            timeSinceGossip = gossipTime.get();
        } else {
            villagerEntity.getBrain().remember(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, 0);
        }
        if (timeSinceSleep > 48000) {
            for (TradeOffer tradeOffer : this.getOffers()) {
                tradeOffer.increaseSpecialPrice((int) Math.min(5*(timeSinceSleep - 48000) / 24000.0, 32));
            }
        }
        if (timeSinceGossip > 48000) {
            for (TradeOffer tradeOffer : this.getOffers()) {
                tradeOffer.increaseSpecialPrice((int) Math.min(5*(timeSinceGossip - 48000) / 24000.0, 32));
            }
        }
    }

    @ModifyVariable(method = "fillRecipes", at = @At("STORE"), ordinal = 0)
    private Int2ObjectMap<TradeOffers.Factory[]> newTrades(Int2ObjectMap<TradeOffers.Factory[]> iter, @Local VillagerData villagerData){
        if (villagerData.profession() == VillagerProfession.LIBRARIAN) {
            return new Int2ObjectOpenHashMap(ImmutableMap.builder()
                    .put(1,new TradeOffers.Factory[]{
                            new TradeOffers.BuyItemFactory(Items.PAPER, 24, 16, 2),
                            new TradeOffers.BuyItemFactory(Items.BOOK, 4, 12, 2),
                            new TradeOffers.SellItemFactory(Blocks.BOOKSHELF, 9, 1, 12, 1)
                    })
                    .put(2, new TradeOffers.Factory[]{
                            biomeBook(false, villagerData),
                            new TradeOffers.SellItemFactory(Blocks.CHISELED_BOOKSHELF, 1, 1, 12, 5)})
                    .put(3, new TradeOffers.Factory[]{
                            new TradeOffers.BuyItemFactory(Items.INK_SAC, 5, 12, 20),
                            new TradeOffers.SellItemFactory(Items.GLASS, 1, 4, 10),
                            new TradeOffers.SellItemFactory(Items.CLOCK, 5, 1, 15),
                            new TradeOffers.SellItemFactory(Items.COMPASS, 4, 1, 15),
                            new TradeOffers.SellItemFactory(Items.LANTERN, 4, 1, 10)})
                    .put(4, new TradeOffers.Factory[]{
                            anyBook(),
                            new TradeOffers.BuyItemFactory(Items.WRITABLE_BOOK, 2, 12, 30)})
                    .put(5, new TradeOffers.Factory[]{
                            biomeBook(true, villagerData),
                            new TradeOffers.SellItemFactory(Items.NAME_TAG, 20, 1, 30)}).build());
        }
        return iter;
    }

    @Unique
    private EnchantedBookFactory biomeBook(boolean master, VillagerData villagerData) {
        Object2ObjectMap<VillagerType,
                TagKey<Enchantment>> biomeEnchants =  new Object2ObjectOpenHashMap(ImmutableMap.builder()
                .put(VillagerType.DESERT, ModTags.DESERT_TRADES)
                .put(VillagerType.JUNGLE, ModTags.JUNGLE_TRADES)
                .put(VillagerType.PLAINS, ModTags.PLAINS_TRADES)
                .put(VillagerType.SAVANNA, ModTags.SAVANNA_TRADES)
                .put(VillagerType.SNOW, ModTags.SNOW_TRADES)
                .put(VillagerType.SWAMP, ModTags.SWAMP_TRADES)
                .put(VillagerType.TAIGA, ModTags.TAIGA_TRADES)
                .build());


        Random rn = this.getWorld().random;
        VillagerEntity villagerEntity = (VillagerEntity)(Object)this;
        Optional<RegistryEntry<Enchantment>> optional = villagerEntity.getWorld()
                .getRegistryManager()
                .getOrThrow(RegistryKeys.ENCHANTMENT)
                .getRandomEntry(biomeEnchants.get(villagerData.type()), random);
        int i = 0;
        while (i < 10) {
            i++;
            if (optional.isPresent()) {
                RegistryEntry<Enchantment> registryEntry = optional.get();
                Enchantment enchantment = registryEntry.value();
                int maxLevel = enchantment.getMaxLevel();
                if (!(maxLevel == 1 && master)) {
                    i=10;
                } else {
                    optional = villagerEntity.getWorld()
                            .getRegistryManager()
                            .getOrThrow(RegistryKeys.ENCHANTMENT)
                            .getRandomEntry(biomeEnchants.get(villagerData.type()), random);
                }
            }
        }

        int l;
        ItemStack itemStack;
        if (optional.isPresent()) {
            RegistryEntry<Enchantment> registryEntry = optional.get();
            Enchantment enchantment = registryEntry.value();
            int maxLevel = enchantment.getMaxLevel();
            int midLevel = (int)Math.ceil(maxLevel/2.0);
            int level = maxLevel==1?1:((master?midLevel+rn.nextInt(maxLevel-midLevel):rn.nextInt(midLevel))+1);
            itemStack = EnchantmentHelper.getEnchantedBookWith(new EnchantmentLevelEntry(registryEntry, level));
            l = 2 + random.nextInt(5 + level * 10) + 3 * level;
            if (registryEntry.isIn(EnchantmentTags.DOUBLE_TRADE_PRICE)) {
                l *= 2;
            }

            if (l > 64) {
                l = 64;
            }
        } else {
            l = 1;
            itemStack = new ItemStack(Items.BOOK);
        }

        return new EnchantedBookFactory(itemStack, l, master?30:10);
    }

    @Unique
    private EnchantedBookFactory anyBook() {

        Random rn = this.getWorld().random;
        VillagerEntity villagerEntity = (VillagerEntity)(Object)this;
        Optional<RegistryEntry<Enchantment>> optional = villagerEntity.getWorld()
                .getRegistryManager()
                .getOrThrow(RegistryKeys.ENCHANTMENT)
                .getRandomEntry(ModTags.ANY_TRADES, random);
        int l;
        ItemStack itemStack;
        if (optional.isPresent()) {
            RegistryEntry<Enchantment> registryEntry = optional.get();
            Enchantment enchantment = registryEntry.value();
            int maxLevel = enchantment.getMaxLevel();
            int level = maxLevel==1?1:(rn.nextInt(maxLevel)+1);
            itemStack = EnchantmentHelper.getEnchantedBookWith(new EnchantmentLevelEntry(registryEntry, level));
            l = 2 + random.nextInt(5 + level * 10) + 3 * level;
            if (registryEntry.isIn(EnchantmentTags.DOUBLE_TRADE_PRICE)) {
                l *= 2;
            }

            if (l > 64) {
                l = 64;
            }
        } else {
            l = 1;
            itemStack = new ItemStack(Items.BOOK);
        }

        return new EnchantedBookFactory(itemStack, l, 10);
    }

    @Unique
    EquipmentSlot[] EQUIPMENT_SLOT_ORDER = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};


    @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/MerchantEntity;onDeath(Lnet/minecraft/entity/damage/DamageSource;)V"))
    private void dropArmor(DamageSource damageSource, CallbackInfo ci) {
        VillagerEntity villagerEntity = (VillagerEntity) (Object) this;
        if (villagerEntity.getWorld() instanceof ServerWorld serverWorld) {
            for (ItemStack itemStack : FixedMinecraft.getArmor(villagerEntity)) {
                villagerEntity.dropStack(serverWorld, itemStack);
            }
            for (int i = 0; i < 4; i++) {
                villagerEntity.equipStack(EQUIPMENT_SLOT_ORDER[i], ItemStack.EMPTY);
                i++;
            }
        }
    }
}
