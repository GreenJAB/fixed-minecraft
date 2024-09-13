package net.greenjab.fixedminecraft.mixin.misc;

import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Iterator;

@Mixin(FishingBobberEntity.class)
public class FishingBobberEntityMixin {

    @Shadow
    @Final
    private int luckOfTheSeaLevel;

    @ModifyArg(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V"), index = 4)
    private ItemStack fishingItem(ItemStack loot, @Local(ordinal = 0) ItemStack rod) {
        FishingBobberEntity FBE = (FishingBobberEntity)(Object)this;
        PlayerEntity playerEntity = FBE.getPlayerOwner();
        ItemStack bait = getBait(playerEntity);
        int luck = this.luckOfTheSeaLevel;
        if (playerEntity.hasStatusEffect(StatusEffects.LUCK))
            luck += 3*(playerEntity.getStatusEffect(StatusEffects.LUCK).getAmplifier()+1);
        //if (playerEntity.hasStatusEffect(StatusEffects.UNLUCK))
        //    luck -= 3*(playerEntity.getStatusEffect(StatusEffects.UNLUCK).getAmplifier()+1);

        int baitpower = 0;
        if (bait.isOf(Items.SPIDER_EYE))baitpower=1;
        if (bait.isOf(Items.FERMENTED_SPIDER_EYE))baitpower=2;


        int chanceGood = Math.min(luck * baitpower + 3 * baitpower,100);
        int chanceFish = Math.max(40-chanceGood, 0);
        int chanceBad = Math.max(40-chanceGood*2, 0);
        int chanceMid = Math.max(100-chanceGood-chanceFish-chanceBad, 0);

        int rand = playerEntity.getWorld().random.nextInt(100);
        int lootPool = 0;
        if (rand>chanceFish) lootPool = 1;
        if (rand>chanceFish+chanceBad) lootPool = 2;
        if (rand>chanceFish+chanceBad+chanceMid) lootPool = 3;

        LootTable lootTable;
        switch (lootPool){
            case 1: lootTable = playerEntity.getWorld().getServer().getLootManager().getLootTable(new Identifier("gameplay/fixed_fishing/junk")); break;
            case 2: lootTable = playerEntity.getWorld().getServer().getLootManager().getLootTable(new Identifier("gameplay/fixed_fishing/mid")); break;
            case 3: lootTable = playerEntity.getWorld().getServer().getLootManager().getLootTable(new Identifier("gameplay/fixed_fishing/treasure")); break;
            default: lootTable = playerEntity.getWorld().getServer().getLootManager().getLootTable(new Identifier("gameplay/fixed_fishing/fish"));
        }

        LootContextParameterSet lootContextParameterSet = (new LootContextParameterSet.Builder((ServerWorld)FBE.getWorld())).add(LootContextParameters.ORIGIN, FBE.getPos()).add(LootContextParameters.TOOL, rod).add(LootContextParameters.THIS_ENTITY, FBE).luck((float)this.luckOfTheSeaLevel + playerEntity.getLuck()).build(LootContextTypes.FISHING);

        ObjectArrayList<ItemStack> loots = lootTable.generateLoot(lootContextParameterSet);
        loot = loots.get(0);

        if (!playerEntity.getAbilities().creativeMode) bait.decrement(1);
        return loot;
    }

    private ItemStack getBait(PlayerEntity playerEntity) {
        Iterator handItems = playerEntity.getHandItems().iterator();
        while (handItems.hasNext()) {
            ItemStack item = (ItemStack)handItems.next();
            if (item.isOf(Items.SPIDER_EYE)||item.isOf(Items.FERMENTED_SPIDER_EYE)) return item;
        }
        for(int i = 0; i < playerEntity.getInventory().size(); ++i) {
            ItemStack item = playerEntity.getInventory().getStack(i);
            if (item.isOf(Items.SPIDER_EYE)||item.isOf(Items.FERMENTED_SPIDER_EYE)) return item;
        }
        return ItemStack.EMPTY;
    }
}
