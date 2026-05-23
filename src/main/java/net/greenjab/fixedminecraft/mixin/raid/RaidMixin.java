package net.greenjab.fixedminecraft.mixin.raid;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Raid.class)
public abstract class RaidMixin {
    @ModifyExpressionValue(method = "spawnGroup", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/entity/raid/Raid$RaiderType;entityType:Lnet/minecraft/world/entity/EntityType;", ordinal = 0,
            opcode = Opcodes.GETFIELD
    ))
    private EntityType<? extends Raider> replaceWithIllusioner(EntityType<? extends Raider> original){
        if (original == EntityType.EVOKER) {
            if (Math.random()<0.6666) {
                return EntityType.ILLUSIONER;
            }
        }
        return original;
    }

    @ModifyExpressionValue(method = "spawnGroup", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/entity/EntityType;EVOKER:Lnet/minecraft/world/entity/EntityType;",
            opcode = Opcodes.GETSTATIC
    ))
    private EntityType<? extends Raider> replaceWithIllusionerRavager(EntityType<? extends Raider> original){
        if (Math.random()<0.6666) {
            return EntityType.ILLUSIONER;
        }
        return original;
    }
}
