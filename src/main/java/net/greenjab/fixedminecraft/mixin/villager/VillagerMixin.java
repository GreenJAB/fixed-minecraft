package net.greenjab.fixedminecraft.mixin.villager;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.network.VillagerNeedsPayload;
import net.greenjab.fixedminecraft.registry.registries.MemoryRegistry;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager {

    @Shadow
    protected abstract void setUnhappy();

    @Shadow
    protected abstract void startTrading(Player player);

    @Shadow
    public abstract VillagerData getVillagerData();

    @Shadow
    private int foodLevel;

    @Shadow
    protected abstract void eatUntilFull();

    public VillagerMixin(EntityType<? extends VillagerMixin> entityType, Level world) {
        super(entityType, world);
    }

    @ModifyExpressionValue(method = "makeBrain", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/Brain$Provider;makeBrain(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/ai/Brain$Packed;)Lnet/minecraft/world/entity/ai/Brain;"
    ))
    private <E extends LivingEntity> Brain<E> addMemories(Brain<E> original){
        original.registerMemory(MemoryRegistry.TIME_SINCE_GOSSIP);
        original.registerMemory(MemoryRegistry.TIME_SINCE_SLEEP);
        original.registerMemory(MemoryRegistry.TIME_SINCE_WALK);
        original.registerMemory(MemoryRegistry.TIME_SINCE_EAT);
        original.registerMemory(MemoryRegistry.TIME_SINCE_SUN);
        return original;
    }

    @ModifyExpressionValue(method = "refreshBrain", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/Brain$Provider;makeBrain(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/ai/Brain$Packed;)Lnet/minecraft/world/entity/ai/Brain;"
    ))
    private <E extends LivingEntity> Brain<E> addMemories2(Brain<E> original){
        original.registerMemory(MemoryRegistry.TIME_SINCE_GOSSIP);
        original.registerMemory(MemoryRegistry.TIME_SINCE_SLEEP);
        original.registerMemory(MemoryRegistry.TIME_SINCE_WALK);
        original.registerMemory(MemoryRegistry.TIME_SINCE_EAT);
        original.registerMemory(MemoryRegistry.TIME_SINCE_SUN);
        return original;
    }

    @ModifyExpressionValue(method = "wantsToSpawnGolem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/villager/Villager;golemSpawnConditionsMet(J)Z"))
    private boolean dontNeedSleep(boolean original) {
        return true;
    }

    @Redirect(method = "spawnGolemIfNeeded", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"))
    private int summon1GolemPerHostileMob(List<Villager> instance, @Local(argsOnly = true) int villagersNeededToAgree) {
        if (instance.size() >= villagersNeededToAgree) {
            if (this.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_HOSTILE)) {
                LivingEntity enemy = this.getBrain().getMemoryInternal(MemoryModuleType.NEAREST_HOSTILE).get();
                if (enemy.entityTags().contains("iron_golem")) return 0;
                else {
                    if (tryEat()) enemy.addTag("iron_golem");
                    else return 0;
                }
            }
        }
        return instance.size();
    }

    @Inject(method = "onReputationEventFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/gossip/GossipContainer;add(Ljava/util/UUID;Lnet/minecraft/world/entity/ai/gossip/GossipType;I)V", ordinal = 2))
    private void rideCamel(ReputationEventType type, Entity source, CallbackInfo ci){
        if (this.isPassenger()) {
            Entity vehicle = this.getVehicle();
            assert vehicle != null;
            if (vehicle.getType() == EntityType.CAMEL) {
                this.stopRiding();
            }
        } else {
            if (source.isPassenger()) {
                Entity vehicle = source.getVehicle();
                assert vehicle != null;
                if (vehicle.getType() == EntityType.CAMEL) {
                    List<Entity> passengers = vehicle.getPassengers();
                    if (passengers.size() == 1) {
                        this.startRiding(vehicle);
                    }
                }
            }
        }
    }

    @Unique
    private boolean tryEat() {
        eatUntilFull();
        if (foodLevel<=0) return false;
        foodLevel--;
        return true;
    }

    @Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/Holder;)V", at = @At(
            "TAIL"
    ))
    private void startWithFood(EntityType<? extends Villager> entityType, Level level, Holder<VillagerType> type, CallbackInfo ci){
        foodLevel = 10;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void saveCustomData(ValueOutput output, CallbackInfo ci) {
        Optional<Integer> gossipTime = this.getBrain().getMemoryInternal(MemoryRegistry.TIME_SINCE_GOSSIP);
        if (gossipTime!=null && gossipTime.isPresent()) output.putInt("gossipTime", gossipTime.get());
        Optional<Integer> sleepTime = this.getBrain().getMemoryInternal(MemoryRegistry.TIME_SINCE_SLEEP);
        if (sleepTime!=null && sleepTime.isPresent()) output.putInt("sleepTime", sleepTime.get());
        Optional<Integer> walkTime = this.getBrain().getMemoryInternal(MemoryRegistry.TIME_SINCE_WALK);
        if (walkTime!=null && walkTime.isPresent()) output.putInt("walkTime", walkTime.get());
        Optional<Integer> eatTime = this.getBrain().getMemoryInternal(MemoryRegistry.TIME_SINCE_EAT);
        if (eatTime!=null && eatTime.isPresent()) output.putInt("eatTime", eatTime.get());
        Optional<Integer> sunTime = this.getBrain().getMemoryInternal(MemoryRegistry.TIME_SINCE_SUN);
        if (sunTime!=null && sunTime.isPresent()) output.putInt("sunTime", sunTime.get());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void loadCustomData(ValueInput input, CallbackInfo ci) {
        this.getBrain().setMemory(MemoryRegistry.TIME_SINCE_GOSSIP, input.getIntOr("gossipTime", 0));
        this.getBrain().setMemory(MemoryRegistry.TIME_SINCE_SLEEP, input.getIntOr("sleepTime", 0));
        this.getBrain().setMemory(MemoryRegistry.TIME_SINCE_WALK, input.getIntOr("walkTime", 0));
        this.getBrain().setMemory(MemoryRegistry.TIME_SINCE_EAT, input.getIntOr("eatTime", 0));
        this.getBrain().setMemory(MemoryRegistry.TIME_SINCE_SUN, input.getIntOr("sunTime", 0));
    }

    @Inject(method = "gossip", at = @At("HEAD"))
    private void resetGossipTime(ServerLevel level, Villager target, long timestamp, CallbackInfo ci){
        this.getBrain().setMemory(MemoryRegistry.TIME_SINCE_GOSSIP, 0);
    }
    @Inject(method = "eatUntilFull", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/SimpleContainer;removeItem(II)Lnet/minecraft/world/item/ItemStack;"))
    private void resetEatTime(CallbackInfo ci){
        this.getBrain().setMemory(MemoryRegistry.TIME_SINCE_EAT, 0);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void increaseStats(CallbackInfo ci){
        if (this.level() instanceof ServerLevel serverLevel && serverLevel.getGameRules().get(GameRules.ADVANCE_TIME) && !this.isBaby()) {
            this.getBrain().setMemory(MemoryRegistry.TIME_SINCE_GOSSIP, this.getBrain().getMemory(MemoryRegistry.TIME_SINCE_GOSSIP).orElse(0)+1);
            this.getBrain().setMemory(MemoryRegistry.TIME_SINCE_SLEEP, this.getBrain().getMemory(MemoryRegistry.TIME_SINCE_SLEEP).orElse(0)+1);
            this.getBrain().setMemory(MemoryRegistry.TIME_SINCE_WALK, this.getBrain().getMemory(MemoryRegistry.TIME_SINCE_WALK).orElse(0)+1);
            this.getBrain().setMemory(MemoryRegistry.TIME_SINCE_EAT, this.getBrain().getMemory(MemoryRegistry.TIME_SINCE_EAT).orElse(0)+1);
            this.getBrain().setMemory(MemoryRegistry.TIME_SINCE_SUN, this.getBrain().getMemory(MemoryRegistry.TIME_SINCE_SUN).orElse(0)+1);

            if (this.getBrain().getMemory(MemoryRegistry.TIME_SINCE_EAT).orElse(0)%24000==0) tryEat();
            if (this.getBrain().getMemory(MemoryRegistry.TIME_SINCE_SUN).orElse(0)%21==0)
                if (this.level().getBrightness(LightLayer.SKY, this.blockPosition())!=0)
                    this.getBrain().setMemory(MemoryRegistry.TIME_SINCE_SUN, 0);

            if (this.getDeltaMovement().horizontalDistanceSqr()>0)
                if (this.getBrain().getMemory(MemoryRegistry.TIME_SINCE_WALK).orElse(0)>1200)
                    if (this.getBrain().hasMemoryValue(MemoryModuleType.WALK_TARGET)) {
                        Vec3 v = this.getBrain().getMemory(MemoryModuleType.WALK_TARGET).get().getTarget().currentPosition();
                        if (v.distanceToSqr(this.position())>64)
                            this.getBrain().setMemory(MemoryRegistry.TIME_SINCE_WALK, 0);
                    }
        }
    }

    @Inject(method = "updateSpecialPrices", at = @At("TAIL"))
    private void happiness(Player player, CallbackInfo ci) {
        double add = 0;

        Optional<Integer> gossipTime = this.getBrain().getMemoryInternal(MemoryRegistry.TIME_SINCE_GOSSIP);
        if (gossipTime!=null && gossipTime.isPresent() && gossipTime.get() > 48000) add+=5*(gossipTime.get() - 48000) / 24000.0;
        Optional<Integer> sleepTime = this.getBrain().getMemoryInternal(MemoryRegistry.TIME_SINCE_SLEEP);
        if (sleepTime!=null && sleepTime.isPresent() && sleepTime.get() > 48000) add+=5*(sleepTime.get() - 48000) / 24000.0;
        Optional<Integer> walkTime = this.getBrain().getMemoryInternal(MemoryRegistry.TIME_SINCE_WALK);
        if (walkTime!=null && walkTime.isPresent() && walkTime.get() > 48000) add+=5*(walkTime.get() - 48000) / 24000.0;
        Optional<Integer> eatTime = this.getBrain().getMemoryInternal(MemoryRegistry.TIME_SINCE_EAT);
        if (eatTime!=null && eatTime.isPresent() && eatTime.get() > 48000) add+=5*(eatTime.get() - 48000) / 24000.0;
        Optional<Integer> sunTime = this.getBrain().getMemoryInternal(MemoryRegistry.TIME_SINCE_SUN);
        if (sunTime!=null && sunTime.isPresent() && sunTime.get() > 48000) add+=5*(sunTime.get() - 48000) / 24000.0;

        for (MerchantOffer tradeOffer : this.getOffers()) {
            tradeOffer.addToSpecialPriceDiff((int) add);
        }
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void msgPlayer(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.is(Items.VILLAGER_SPAWN_EGG) || !this.isAlive() || this.isTrading() || this.isSleeping()) return;

        if (player instanceof ServerPlayer serverPlayer) {
            if (this.isBaby()) {
                cancel(serverPlayer, "baby", cir); return; }

            if (this.getVillagerData().profession().is(VillagerProfession.NITWIT)) {
                cancel(serverPlayer, "nitwit", cir); return; }

            if (this.getBrain().getMemory(MemoryRegistry.TIME_SINCE_EAT).orElse(0) > 168000) {
                if (!tryEat())
                    cancel(serverPlayer, "very_hungry", cir); return; }

            if (this.getBrain().getMemory(MemoryRegistry.TIME_SINCE_SLEEP).orElse(0) > 168000) {
                cancel(serverPlayer, "very_tired", cir); return; }

            if (this.getBrain().getMemory(MemoryRegistry.TIME_SINCE_SUN).orElse(0) > 168000) {
                cancel(serverPlayer, "very_dark", cir); return; }

            if (this.getBrain().getMemory(MemoryRegistry.TIME_SINCE_WALK).orElse(0) > 168000) {
                cancel(serverPlayer, "very_lazy", cir); return; }

            if (this.getBrain().getMemory(MemoryRegistry.TIME_SINCE_GOSSIP).orElse(0) > 168000) {
                cancel(serverPlayer, "very_lonely", cir); return; }

            if (player.level().isDarkOutside()) {
                cancel(serverPlayer, "night", cir); return; }

            if (this.getOffers().isEmpty()) {
                cancel(serverPlayer, "unemployed", cir); return; }

            boolean sentChat = false;
            if (this.getBrain().getMemory(MemoryRegistry.TIME_SINCE_EAT).orElse(0)>48000)
                sentChat = warning(serverPlayer, "hungry");

            if (!sentChat && this.getBrain().getMemory(MemoryRegistry.TIME_SINCE_SLEEP).orElse(0)>48000)
                sentChat = warning(serverPlayer, "tired");

            if (!sentChat && this.getBrain().getMemory(MemoryRegistry.TIME_SINCE_SUN).orElse(0)>48000)
                sentChat = warning(serverPlayer, "dark");

            if (!sentChat && this.getBrain().getMemory(MemoryRegistry.TIME_SINCE_WALK).orElse(0)>48000)
                sentChat = warning(serverPlayer, "lazy");

            if (!sentChat && this.getBrain().getMemory(MemoryRegistry.TIME_SINCE_GOSSIP).orElse(0)>48000)
                sentChat = warning(serverPlayer, "lonely");

            if (!sentChat) ServerPlayNetworking.send(serverPlayer, new VillagerNeedsPayload(this.uuid, "trade"));

            player.awardStat(Stats.TALKED_TO_VILLAGER);
            this.startTrading(player);
        }

        cir.setReturnValue(InteractionResult.SUCCESS);
    }

    @Unique
    private boolean warning(ServerPlayer serverPlayer, String reason) {
        ServerPlayNetworking.send(serverPlayer, new VillagerNeedsPayload(this.uuid, reason));
        return true;
    }

    @Unique
    private void cancel(ServerPlayer serverPlayer, String reason, CallbackInfoReturnable<InteractionResult> cir) {
        this.setUnhappy();
        ServerPlayNetworking.send(serverPlayer, new VillagerNeedsPayload(this.uuid, reason));
        cir.setReturnValue(InteractionResult.CONSUME);
    }

    @Unique
    EquipmentSlot[] EQUIPMENT_SLOT_ORDER = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    @Inject(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/villager/AbstractVillager;die(Lnet/minecraft/world/damagesource/DamageSource;)V"))
    private void dropArmor(DamageSource source, CallbackInfo ci) {
        if (this.level() instanceof ServerLevel serverWorld) {
            for (ItemStack itemStack : FixedMinecraft.getArmor(this)) {
                this.spawnAtLocation(serverWorld, itemStack);
            }
            for (int i = 0; i < 4; i++) {
                this.setItemSlot(EQUIPMENT_SLOT_ORDER[i], ItemStack.EMPTY);
                i++;
            }
            for(int i = 0; i < this.getInventory().getContainerSize(); ++i) {
                ItemStack itemStack = this.getInventory().getItem(i);
                if (!itemStack.isEmpty()) {
                    this.spawnAtLocation(serverWorld, itemStack);
                }
            }
            this.getInventory().clearContent();
        }
    }
}
