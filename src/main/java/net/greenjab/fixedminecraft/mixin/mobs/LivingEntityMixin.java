package net.greenjab.fixedminecraft.mixin.mobs;

import net.greenjab.fixedminecraft.data.ModTags;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@SuppressWarnings("unchecked")
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Redirect(method = "getGroup", at = @At(value = "FIELD",
                                            target = "Lnet/minecraft/entity/EntityGroup;DEFAULT:Lnet/minecraft/entity/EntityGroup;"//, opcode = Opcodes.GETFIELD
    ))
    private EntityGroup moreArthropods(){
        LivingEntity LE = (LivingEntity)(Object)this;
        EntityType<?> EntityType = LE.getType();
        if (EntityType.isIn(ModTags.INSTANCE.getARTHROPODS())) {return EntityGroup.ARTHROPOD;}
        return EntityGroup.DEFAULT;
    }
}
