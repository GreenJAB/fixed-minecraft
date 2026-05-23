package net.greenjab.fixedminecraft.mixin.night;

import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.MoonPhase;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin {

    @Shadow
    @Final
    private int luck;

    @ModifyArg(method = "retrieve", at = @At(value = "INVOKE", target ="Lnet/minecraft/world/level/storage/loot/LootParams$Builder;withLuck(F)Lnet/minecraft/world/level/storage/loot/LootParams$Builder;"))
    private float oneItem(float luck) {
        return 0;
    }

    @ModifyArg(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)V"), index = 4)
    private ItemStack fishingItem(ItemStack loot, @Local(argsOnly = true, ordinal = 0) ItemStack rod) {
        FishingHook FBE = (FishingHook)(Object)this;
        Player playerEntity = FBE.getPlayerOwner();
        assert playerEntity != null;
        ItemStack bait = getBait(playerEntity);
        int luck = this.luck;

        Level world = FBE.level();
        if (playerEntity.hasEffect(MobEffects.CONDUIT_POWER))luck += 2;
        if (world.getBrightness(LightLayer.SKY, FBE.blockPosition())>10) {
            if (world.isRaining())luck+=2;
        }

        int baitpower = 0;
        if (bait!=ItemStack.EMPTY) baitpower = bait.getComponents().get(ItemRegistry.BAIT_POWER).level();

        if (playerEntity.hasEffect(MobEffects.LUCK))
            baitpower += (playerEntity.getEffect(MobEffects.LUCK).getAmplifier()+1);

        MoonPhase moonPhase = (world).environmentAttributes().getValue(EnvironmentAttributes.MOON_PHASE, playerEntity.blockPosition());
        if (world.isDarkOutside() && moonPhase.index() == 0 && world.getBrightness(LightLayer.SKY, FBE.blockPosition())>10) baitpower++;

        //https://www.desmos.com/calculator/xgxywuavpe

        int chanceGood = Math.min(luck * baitpower + 3 * baitpower,100);
        int chanceFish = Math.max(40-chanceGood, 0);
        int chanceBad = Math.max(40-chanceGood*2, 0);
        int chanceMid = Math.max(100-chanceGood-chanceFish-chanceBad, 0);

        int rand = playerEntity.level().getRandom().nextInt(100);
        int lootPool = 0;
        if (rand>chanceFish) lootPool = 1;
        if (rand>chanceFish+chanceBad) lootPool = 2;
        if (rand>chanceFish+chanceBad+chanceMid) lootPool = 3;
        if (playerEntity.hasEffect(MobEffects.CONDUIT_POWER) && lootPool ==1)lootPool=0;

        String[] tables = {"fish", "junk", "mid", "treasure"};

        Identifier lootTableId = FixedMinecraft.id("gameplay/fixed_fishing/" + tables[lootPool]);
        LootTable lootTable = FBE.level().getServer()
                .reloadableRegistries()
                .getLootTable(ResourceKey.create(Registries.LOOT_TABLE, lootTableId));

        LootParams lootContextParameterSet = (new LootParams.Builder((ServerLevel)FBE.level())).withParameter(LootContextParams.ORIGIN, FBE.position()).withParameter(LootContextParams.TOOL, rod).withParameter(LootContextParams.THIS_ENTITY, FBE).withLuck(/*(float)this.luckOfTheSeaLevel +*/ playerEntity.getLuck()).create(LootContextParamSets.FISHING);

        ObjectArrayList<ItemStack> loots = lootTable.getRandomItems(lootContextParameterSet);
        if (loots.isEmpty()) return Items.DIRT.getDefaultInstance();
        loot = loots.getFirst();

        if (!playerEntity.hasInfiniteMaterials()) bait.shrink(1);
        return loot;

    }

    @Unique
    private ItemStack getBait(Player playerEntity) {
        ItemStack[] items = {playerEntity.getMainHandItem(), playerEntity.getOffhandItem()};
        for (ItemStack item : items) {
            if (item.getComponents().has(ItemRegistry.BAIT_POWER)) return item;
        }
        for(int i = 0; i < playerEntity.getInventory().getContainerSize(); ++i) {
            ItemStack item = playerEntity.getInventory().getItem(i);
            if (item.getComponents().has(ItemRegistry.BAIT_POWER)) return item;
        }
        return ItemStack.EMPTY;
    }
}
