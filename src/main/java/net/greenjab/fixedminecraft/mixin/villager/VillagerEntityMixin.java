package net.greenjab.fixedminecraft.mixin.villager;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityInteraction;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.EntityPredicates;
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
    private static ImmutableList<MemoryModuleType> addModules(ImmutableList<MemoryModuleType> original) {
        return ImmutableList.of(MemoryModuleType.HOME, MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, MemoryModuleType.MEETING_POINT, MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemoryModuleType.WALK_TARGET, MemoryModuleType.LOOK_TARGET, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.BREED_TARGET, MemoryModuleType.PATH, MemoryModuleType.DOORS_TO_CLOSE, MemoryModuleType.NEAREST_BED, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_HOSTILE, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleType.HIDING_PLACE, MemoryModuleType.HEARD_BELL_TIME, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.LAST_SLEPT, MemoryModuleType.LAST_WOKEN, MemoryModuleType.LAST_WORKED_AT_POI, MemoryModuleType.GOLEM_DETECTED_RECENTLY, MemoryModuleType.ROAR_TARGET);
    }

    @Redirect(method = "summonGolem", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"))
    private int summon1GolemPerHostileMob(List instance, @Local(argsOnly = true) int requiredCount) {
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
                Integer integer = (Integer) VillagerEntity.ITEM_FOOD_VALUES.get(itemStack.getItem());
                if (integer != null) {
                    villagerEntity.getInventory().removeStack(i, 1);
                    return true;
                }
            }
        }
        return false;
    }

    @Inject(method = "onInteractionWith", at = @At(value = "INVOKE", target = "Lnet/minecraft/village/VillagerGossips;startGossip(Ljava/util/UUID;Lnet/minecraft/village/VillageGossipType;I)V", ordinal = 2))
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
    }

    @Inject(method = "talkWithVillager", at = @At("HEAD"))
    private void talktime(ServerWorld world, VillagerEntity villager, long time, CallbackInfo ci){
        VillagerEntity villagerEntity = (VillagerEntity)(Object)this;
        Optional<LivingEntity> lastVillager = villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.ROAR_TARGET);
        if (lastVillager!=null) {
            if (lastVillager.isPresent()) {
                if (lastVillager.get().getUuid()!=villager.getUuid()){
                    villagerEntity.getBrain().remember(MemoryModuleType.LAST_WOKEN, time);
                    villagerEntity.getBrain().remember(MemoryModuleType.ROAR_TARGET, villager);
                }
            } else {
                villagerEntity.getBrain().remember(MemoryModuleType.ROAR_TARGET, villager);
            }
        } else {
            villagerEntity.getBrain().remember(MemoryModuleType.ROAR_TARGET, villager);
        }
    }
    @Inject(method = "wakeUp", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/brain/Brain;remember(Lnet/minecraft/entity/ai/brain/MemoryModuleType;Ljava/lang/Object;)V"), cancellable = true)
    private void dontRememberWakeUp(CallbackInfo ci) {
        ci.cancel();
    }
    @Inject(method = "prepareOffersFor", at = @At("TAIL"))
    private void happiness(PlayerEntity player, CallbackInfo ci) {
        VillagerEntity villagerEntity = (VillagerEntity)(Object)this;
        Long time = villagerEntity.getWorld().getTime();
        Optional<Long> sleepTime = villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.LAST_SLEPT);
        long timeSinceSleep = 0L;
        if (sleepTime!=null) {
            if (sleepTime.isPresent()) {
                timeSinceSleep = time - sleepTime.get();
            } else {
                villagerEntity.getBrain().remember(MemoryModuleType.LAST_SLEPT, time);
            }
        } else {
            villagerEntity.getBrain().remember(MemoryModuleType.LAST_SLEPT, time);
        }
        Optional<Long> gossipTime = villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.LAST_WOKEN);
        long timeSinceGossip = 0;
        if (gossipTime!=null) {
            if (gossipTime.isPresent()) {
                timeSinceGossip = time - gossipTime.get();
            } else {
                villagerEntity.getBrain().remember(MemoryModuleType.LAST_WOKEN, time);
            }
        } else {
            villagerEntity.getBrain().remember(MemoryModuleType.LAST_WOKEN, time);
        }
        if (timeSinceSleep>36000) {
            for (TradeOffer tradeOffer : this.getOffers()) {
                tradeOffer.increaseSpecialPrice((int) Math.min(5*(timeSinceSleep - 36000) / 24000.0, 32));
            }
        }
        if (timeSinceGossip>36000) {
            for (TradeOffer tradeOffer : this.getOffers()) {
                tradeOffer.increaseSpecialPrice((int) Math.min(5*(timeSinceGossip - 36000) / 24000.0, 32));
            }
        }
    }

    @ModifyVariable(method = "fillRecipes", at = @At("STORE"), ordinal = 0)
    private Int2ObjectMap<TradeOffers.Factory[]> newTrades(Int2ObjectMap<TradeOffers.Factory[]> iter, @Local VillagerData villagerData){
        if (villagerData.getProfession() == VillagerProfession.LIBRARIAN) {
            return new Int2ObjectOpenHashMap(ImmutableMap.builder()
                    .put(1,new TradeOffers.Factory[]{
                            new TradeOffers.BuyItemFactory(Items.PAPER, 24, 16, 2),
                            new TradeOffers.BuyItemFactory(Items.BOOK, 4, 12, 2),
                            new TradeOffers.SellItemFactory(Blocks.BOOKSHELF, 9, 1, 12, 1)
                    })

                    .put(2, new TradeOffers.Factory[]{
                            biomeBook(false, villagerData),
                            new TradeOffers.SellItemFactory(Items.LANTERN, 1, 1, 5)})

                    .put(3, new TradeOffers.Factory[]{
                            new TradeOffers.BuyItemFactory(Items.INK_SAC, 5, 12, 20),
                            new TradeOffers.SellItemFactory(Items.GLASS, 1, 4, 10),
                            new TradeOffers.SellItemFactory(Items.CLOCK, 5, 1, 15),
                            new TradeOffers.SellItemFactory(Items.COMPASS, 4, 1, 15)})

                    .put(4, new TradeOffers.Factory[]{
                            new TradeOffers.EnchantBookFactory(10),
                            new TradeOffers.BuyItemFactory(Items.WRITABLE_BOOK, 2, 12, 30)})

                    .put(5, new TradeOffers.Factory[]{
                            biomeBook(true, villagerData),
                            new TradeOffers.SellItemFactory(Items.NAME_TAG, 20, 1, 30)}).build());
        }
        return iter;
    }

    @Unique
    private TradeOffers.EnchantBookFactory biomeBook(boolean master, VillagerData villagerData) {
        Random rn = this.getWorld().random;
        Object2ObjectMap<VillagerType, Enchantment[]> biomeEnchants =  new Object2ObjectOpenHashMap(ImmutableMap.builder()
                .put(VillagerType.DESERT, new Enchantment[]{Enchantments.FIRE_PROTECTION, Enchantments.IMPALING, Enchantments.THORNS, Enchantments.EFFICIENCY, Enchantments.INFINITY})
                .put(VillagerType.JUNGLE, new Enchantment[]{Enchantments.FEATHER_FALLING, Enchantments.SWEEPING, Enchantments.POWER, Enchantments.UNBREAKING, Enchantments.CHANNELING})
                .put(VillagerType.PLAINS, new Enchantment[]{Enchantments.PROTECTION, Enchantments.SMITE, Enchantments.PUNCH, Enchantments.FIRE_ASPECT, Enchantments.MULTISHOT})
                .put(VillagerType.SAVANNA, new Enchantment[]{Enchantments.KNOCKBACK, Enchantments.SHARPNESS, Enchantments.DEPTH_STRIDER, Enchantments.BINDING_CURSE, Enchantments.LOYALTY})
                .put(VillagerType.SNOW, new Enchantment[]{Enchantments.AQUA_AFFINITY, Enchantments.QUICK_CHARGE, Enchantments.FROST_WALKER, Enchantments.LOOTING, Enchantments.SILK_TOUCH})
                .put(VillagerType.SWAMP, new Enchantment[]{Enchantments.PROJECTILE_PROTECTION, Enchantments.PIERCING, Enchantments.RESPIRATION, Enchantments.VANISHING_CURSE, Enchantments.MENDING})
                .put(VillagerType.TAIGA, new Enchantment[]{Enchantments.BLAST_PROTECTION, Enchantments.BANE_OF_ARTHROPODS, Enchantments.RIPTIDE, Enchantments.FORTUNE, Enchantments.FLAME})
                .build()); //lure, luck of the sea

        Enchantment[] enchants = biomeEnchants.get(villagerData.getType());
        boolean includeSpecial = master || enchants[enchants.length-1].getMaxLevel()!=1;
        Enchantment enchant = enchants[rn.nextInt(enchants.length+(includeSpecial?0:-1))];
        int maxLevel = enchant.getMaxLevel();
        int midLevel = (int)Math.ceil(maxLevel/2.0);
        int level = maxLevel==1?1:((master?midLevel+rn.nextInt(maxLevel-midLevel):rn.nextInt(midLevel))+1);
        return new TradeOffers.EnchantBookFactory(master?30:10, level, level, enchant);
    }
}
