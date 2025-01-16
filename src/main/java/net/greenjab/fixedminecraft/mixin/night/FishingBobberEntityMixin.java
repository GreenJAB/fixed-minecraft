package net.greenjab.fixedminecraft.mixin.night;

import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.greenjab.fixedminecraft.registry.LoottableRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Objects;

@Mixin(FishingBobberEntity.class)
public class FishingBobberEntityMixin {

    @Shadow
    @Final
    private int luckBonus;

    @ModifyArg(method = "use", at = @At(value = "INVOKE", target ="Lnet/minecraft/loot/context/LootWorldContext$Builder;luck(F)Lnet/minecraft/loot/context/LootWorldContext$Builder;"))
    private float oneItem(float luck) {
        return 0;
    }

    @ModifyArg(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V"), index = 4)
    private ItemStack fishingItem(ItemStack loot, @Local(ordinal = 0, argsOnly = true) ItemStack rod) {
        FishingBobberEntity FBE = (FishingBobberEntity)(Object)this;
        PlayerEntity playerEntity = FBE.getPlayerOwner();
        assert playerEntity != null;
        ItemStack bait = getBait(playerEntity);
        int luck = this.luckBonus;

        World world = FBE.getWorld();
        if (playerEntity.hasStatusEffect(StatusEffects.CONDUIT_POWER))luck += 2;
        if (world.getLightLevel(LightType.SKY, FBE.getBlockPos())>10) {
            if (world.isRaining())luck+=2;
        }

        int baitpower = 0;
        if (bait.isOf(Items.SPIDER_EYE))baitpower=1;
        if (bait.isOf(Items.FERMENTED_SPIDER_EYE))baitpower=2;

        if (playerEntity.hasStatusEffect(StatusEffects.LUCK))
            baitpower += (playerEntity.getStatusEffect(StatusEffects.LUCK).getAmplifier()+1);

        if (world.isNight() && world.getMoonPhase()==0 && world.getLightLevel(LightType.SKY, FBE.getBlockPos())>10) baitpower++;

        //https://www.desmos.com/calculator/xgxywuavpe

        int chanceGood = Math.min(luck * baitpower + 3 * baitpower,100);
        int chanceFish = Math.max(40-chanceGood, 0);
        int chanceBad = Math.max(40-chanceGood*2, 0);
        int chanceMid = Math.max(100-chanceGood-chanceFish-chanceBad, 0);

        int rand = playerEntity.getWorld().random.nextInt(100);
        int lootPool = 0;
        if (rand>chanceFish) lootPool = 1;
        if (rand>chanceFish+chanceBad) lootPool = 2;
        if (rand>chanceFish+chanceBad+chanceMid) lootPool = 3;

        LootTable lootTable = switch (lootPool) {
            case 1 ->
                    //Objects.requireNonNull(playerEntity.getWorld().getServer()).getReloadableRegistries().getLootTable(Identifier.of("gameplay/fixed_fishing/junk"));
                    Objects.requireNonNull(playerEntity.getWorld().getServer()).getReloadableRegistries().getLootTable(LoottableRegistry.INSTANCE.getFISHING_JUNK());
            case 2 ->
                    Objects.requireNonNull(playerEntity.getWorld().getServer()).getReloadableRegistries().getLootTable(LoottableRegistry.INSTANCE.getFISHING_MID());
            case 3 ->
                    Objects.requireNonNull(playerEntity.getWorld().getServer()).getReloadableRegistries().getLootTable(LoottableRegistry.INSTANCE.getFISHING_TREASURE());
            default ->
                    Objects.requireNonNull(playerEntity.getWorld().getServer()).getReloadableRegistries().getLootTable(LoottableRegistry.INSTANCE.getFISHING_FISH());
        };

        LootWorldContext lootContextParameterSet = (new LootWorldContext.Builder((ServerWorld)FBE.getWorld())).add(LootContextParameters.ORIGIN, FBE.getPos()).add(LootContextParameters.TOOL, rod).add(LootContextParameters.THIS_ENTITY, FBE).luck(/*(float)this.luckOfTheSeaLevel +*/ playerEntity.getLuck()).build(LootContextTypes.FISHING);

        ObjectArrayList<ItemStack> loots = lootTable.generateLoot(lootContextParameterSet);
        loot = loots.get(0);

        if (!playerEntity.getAbilities().creativeMode) bait.decrement(1);
        return loot;
    }

    @Unique
    private ItemStack getBait(PlayerEntity playerEntity) {
        for (ItemStack item : playerEntity.getHandItems()) {
            if (item.isOf(Items.SPIDER_EYE) || item.isOf(Items.FERMENTED_SPIDER_EYE)) return item;
        }
        for(int i = 0; i < playerEntity.getInventory().size(); ++i) {
            ItemStack item = playerEntity.getInventory().getStack(i);
            if (item.isOf(Items.SPIDER_EYE)||item.isOf(Items.FERMENTED_SPIDER_EYE)) return item;
        }
        return ItemStack.EMPTY;
    }
}
