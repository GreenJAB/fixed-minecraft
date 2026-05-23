package net.greenjab.fixedminecraft.mixin.mobs;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.zombie.Husk;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.gameevent.GameEvent;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Zombie.class)
public abstract class ZombieMixin extends Monster {
    public ZombieMixin(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/entity/monster/zombie/Zombie;conversionTime:I",
            ordinal = 0,
            opcode = Opcodes.GETFIELD
    ))
    private void sand(CallbackInfo ci){
        Zombie ZE = (Zombie)(Object)this;
        if (ZE instanceof Husk){
            if (ZE.level().getRandom().nextInt(30)==0) {
                if (!this.level().isClientSide() && this.isAlive()){
                    this.playSound(SoundEvents.SAND_BREAK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                    this.spawnAtLocation((ServerLevel) this.level(), Items.SAND);
                    this.gameEvent(GameEvent.ENTITY_PLACE);
                }
            }
        }
    }

    @Redirect(method = "killedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getDifficulty()Lnet/minecraft/world/Difficulty;"))
    private Difficulty villagerNoDie(ServerLevel instance){
        return Difficulty.HARD;
    }

    @ModifyArg(method = "killedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/zombie/Zombie;convertVillagerToZombieVillager(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/npc/villager/Villager;)Z"), index = 1)
    private Villager villagerIntoNitwit(Villager villager, @Local(argsOnly = true) ServerLevel level){
        if (level.getDifficulty() == Difficulty.NORMAL || level.getDifficulty() == Difficulty.HARD) {
            if (level.getDifficulty() == Difficulty.HARD) {
                villager.setVillagerData(villager.getVillagerData().withProfession(BuiltInRegistries.VILLAGER_PROFESSION.getOrThrow(VillagerProfession.NITWIT)));
            } else {
                if (this.random.nextBoolean()) {
                    villager.setVillagerData(villager.getVillagerData().withProfession(BuiltInRegistries.VILLAGER_PROFESSION.getOrThrow(VillagerProfession.NITWIT)));
                }
            }
        }
        return villager;
    }

    @Inject(method = "setBaby", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;addTransientModifier(Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;)V"))
        private void halfBabyHealth(boolean baby, CallbackInfo ci) {
        Zombie ZE = (Zombie) (Object) this;
        float newHealth = (float) (ZE.getAttributeBaseValue(Attributes.MAX_HEALTH)/2.0f);
        if (newHealth>=10) {
            ZE.getAttribute(Attributes.MAX_HEALTH).setBaseValue(newHealth);
            ZE.setHealth(newHealth);
        }
    }

    @Inject(method = "populateDefaultEquipmentSlots", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextFloat()F"),
            cancellable = true
    )
    private void moreWeapons(RandomSource random, DifficultyInstance difficulty, CallbackInfo ci) {
        float diff = 0.01f;
        if (this.level().getDifficulty() == Difficulty.HARD) diff = 0.1f;
        if (this.level().getDifficulty() == Difficulty.NORMAL) diff = 0.03f;
        if (this.level().getBiome(this.blockPosition()).is(Biomes.PALE_GARDEN)) diff*=2;
        if (random.nextFloat() < diff) {
            int i = random.nextInt(5);
            int j = random.nextInt(2);
            if (random.nextFloat() < 2*diff)  j++;
            if (random.nextFloat() < diff)  j++;

            Zombie ZE = (Zombie) (Object) this;
            if (ZE instanceof Husk) j = -1;
            //if (ZE instanceof DrownedEntity) j = -2;

            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(getEquipmentForHand(i,j)));
        }
        ci.cancel();
    }

    @Unique
    private Item getEquipmentForHand(int equipmentType, int equipmentLevel) {
        switch (equipmentType) {
            case 0:
                if (equipmentLevel == 0) {
                    return Items.WOODEN_SWORD;
                } else if (equipmentLevel == 1) {
                    return Items.STONE_SWORD;
                } else if (equipmentLevel == 2) {
                    return Items.IRON_SWORD;
                } else if (equipmentLevel == 3) {
                    return Items.DIAMOND_SWORD;
                } else if (equipmentLevel == -1) {
                    return Items.GOLDEN_SWORD;
                }
            case 1:
                if (equipmentLevel == 0) {
                    return Items.WOODEN_AXE;
                } else if (equipmentLevel == 1) {
                    return Items.STONE_AXE;
                } else if (equipmentLevel == 2) {
                    return Items.IRON_AXE;
                } else if (equipmentLevel == 3) {
                    return Items.DIAMOND_AXE;
                } else if (equipmentLevel == -1) {
                    return Items.GOLDEN_AXE;
                }
            case 2:
                if (equipmentLevel == 0) {
                    return Items.WOODEN_SHOVEL;
                } else if (equipmentLevel == 1) {
                    return Items.STONE_SHOVEL;
                } else if (equipmentLevel == 2) {
                    return Items.IRON_SHOVEL;
                } else if (equipmentLevel == 3) {
                    return Items.DIAMOND_SHOVEL;
                } else if (equipmentLevel == -1) {
                    return Items.GOLDEN_SHOVEL;
                }
            case 3:
                if (equipmentLevel == 0) {
                    return Items.WOODEN_PICKAXE;
                } else if (equipmentLevel == 1) {
                    return Items.STONE_PICKAXE;
                } else if (equipmentLevel == 2) {
                    return Items.IRON_PICKAXE;
                } else if (equipmentLevel == 3) {
                    return Items.DIAMOND_PICKAXE;
                } else if (equipmentLevel == -1) {
                    return Items.GOLDEN_PICKAXE;
                }
            case 4:
                if (equipmentLevel == 0) {
                    return Items.WOODEN_HOE;
                } else if (equipmentLevel == 1) {
                    return Items.STONE_HOE;
                } else if (equipmentLevel == 2) {
                    return Items.IRON_HOE;
                } else if (equipmentLevel == 3) {
                    return Items.DIAMOND_HOE;
                } else if (equipmentLevel == -1) {
                    return Items.GOLDEN_HOE;
                }
            default:
                return Items.AIR;
        }
    }
}
