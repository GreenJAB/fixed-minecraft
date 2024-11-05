package net.greenjab.fixedminecraft.mixin.mobs;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.BiomeSources;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Mixin(ArmorStandEntity.class)
public abstract class ArmorStandEntityMixin extends LivingEntity {
    @Shadow
    public abstract void setShowArms(boolean showArms);

    @Shadow
    public abstract boolean shouldShowArms();

    @Shadow
    public abstract Iterable<ItemStack> getHandItems();

    @Mutable
    @Shadow
    @Final
    private DefaultedList<ItemStack> heldItems;

    @Shadow
    public abstract void tick();

    public ArmorStandEntityMixin(EntityType<? extends ArmorStandEntityMixin> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "interactAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"))
    private boolean injected(ItemStack instance, @Local(argsOnly = true) PlayerEntity player, @Local(ordinal = 0)ItemStack itemStack) {
        if (player.getWorld() instanceof ServerWorld serverWorld) {
            List<RegistryKey<Biome>> biomes = List.of();
            Set<RegistryKey<Biome>> var10001 = Set.copyOf(biomes);
            Objects.requireNonNull(var10001);
            Predicate<RegistryEntry<Biome>> predicate = var10001::contains;
            Pair<BlockPos, RegistryEntry<Biome>> pair = serverWorld.locateBiome(predicate.negate(), player.getBlockPos(), 6400, 32, 64);
            if (pair != null) {
                BlockPos blockPos = pair.getFirst();
               ItemStack itemStacka = FilledMapItem.createMap(serverWorld, blockPos.getX(), blockPos.getZ(), (byte)2, true, true);
                FilledMapItem.fillExplorationMap(serverWorld, itemStacka);
                NbtCompound nbtCompound2 = itemStacka.getOrCreateSubNbt("display");
                nbtCompound2.putInt("MapColor", 7412448);
                itemStacka.setCustomName(Text.of("Mushroom Fields Explorer Map"));

                player.giveItemStack(itemStacka);
                //MapState.addDecorationsNbt(itemStack, blockPos, "+", this.iconType);
                //itemStacka.setCustomName(Text.translatable(this.nameKey));
                //return new TradeOffer(new ItemStack(Items.EMERALD, this.price), new ItemStack(Items.COMPASS), itemStack, this.maxUses, this.experience, 0.2F);
            }
        }
        if (!player.isSneaking()) {
            if (itemStack.isOf(Items.STICK)) {
                if (!this.shouldShowArms()) {
                    this.setShowArms(true);
                    if (!player.isCreative()) itemStack.decrement(1);
                    return true;
                }
            }
            if (itemStack.isOf(Items.SHEARS)) {
                if (this.shouldShowArms()) {
                    this.setShowArms(false);
                    if (!player.getAbilities().creativeMode) this.dropItem(Items.STICK);
                    if (!player.getAbilities().creativeMode) itemStack.damage(1, player.getWorld().random, (ServerPlayerEntity) player);
                    Iterable<ItemStack> hands = this.getHandItems();
                    hands.forEach((stack) -> {
                        if (!stack.isEmpty()) {
                            this.dropStack(stack);
                        }
                    });
                    this.heldItems = DefaultedList.ofSize(2, ItemStack.EMPTY);
                }
            }
        }
        return itemStack.isEmpty();
    }
    @Inject(method = "interactAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/decoration/ArmorStandEntity;equip(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/EquipmentSlot;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/Hand;)Z"), cancellable = true)
    private void notStick(PlayerEntity player, Vec3d hitPos, Hand hand, CallbackInfoReturnable<ActionResult> cir, @Local ItemStack itemStack){
        if (itemStack.isOf(Items.STICK)) {
            cir.setReturnValue(ActionResult.FAIL);
        }

    }
}
