package net.greenjab.fixedminecraft.mixin.night;

import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
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
        if (bait!=ItemStack.EMPTY) baitpower = bait.getComponents().get(ItemRegistry.BAIT_POWER).level();

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

        String[] tables = {"fish", "junk", "mid", "treasure"};

        Identifier lootTableId = FixedMinecraft.id("gameplay/fixed_fishing/" + tables[lootPool]);
        LootTable lootTable = FBE.getWorld().getServer()
                .getReloadableRegistries()
                .getLootTable(RegistryKey.of(RegistryKeys.LOOT_TABLE, lootTableId));

        LootWorldContext lootContextParameterSet = (new LootWorldContext.Builder((ServerWorld)FBE.getWorld())).add(LootContextParameters.ORIGIN, FBE.getPos()).add(LootContextParameters.TOOL, rod).add(LootContextParameters.THIS_ENTITY, FBE).luck(/*(float)this.luckOfTheSeaLevel +*/ playerEntity.getLuck()).build(LootContextTypes.FISHING);

        ObjectArrayList<ItemStack> loots = lootTable.generateLoot(lootContextParameterSet);
        if (loots.isEmpty()) return Items.DIRT.getDefaultStack();
        loot = loots.get(0);

        if (!playerEntity.getAbilities().creativeMode) bait.decrement(1);
        return loot;

    }

    @Unique
    private ItemStack getBait(PlayerEntity playerEntity) {
        ItemStack[] items = {playerEntity.getMainHandStack(), playerEntity.getOffHandStack()};
        for (ItemStack item : items) {
            if (item.getComponents().contains(ItemRegistry.BAIT_POWER)) return item;
        }
        for(int i = 0; i < playerEntity.getInventory().size(); ++i) {
            ItemStack item = playerEntity.getInventory().getStack(i);
            if (item.getComponents().contains(ItemRegistry.BAIT_POWER)) return item;
        }
        return ItemStack.EMPTY;
    }
}
