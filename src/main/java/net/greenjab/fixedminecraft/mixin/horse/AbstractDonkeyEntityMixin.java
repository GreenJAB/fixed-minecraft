package net.greenjab.fixedminecraft.mixin.horse;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.MuleEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractDonkeyEntity.class)
public class AbstractDonkeyEntityMixin extends AbstractHorseEntity {
    protected AbstractDonkeyEntityMixin(EntityType<? extends AbstractHorseEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "getInventoryColumns", at = @At("HEAD"), cancellable = true)
    private void muleLessColoumns(CallbackInfoReturnable<Integer> cir){
        if ((AbstractDonkeyEntity)(Object)this instanceof MuleEntity) {
            cir.setReturnValue(3);
            cir.cancel();
        }
    }
    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void saveArmor(NbtCompound nbt, CallbackInfo ci){
        if (!this.items.getStack(1).isEmpty()) {
            nbt.put("ArmorItem", this.items.getStack(1).writeNbt(new NbtCompound()));
        }
    }
    @Inject(method = "readCustomDataFromNbt", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/passive/AbstractDonkeyEntity;updateSaddle()V"
    ))
    private void loadArmor(NbtCompound nbt, CallbackInfo ci){
        if (nbt.contains("ArmorItem", NbtElement.COMPOUND_TYPE)) {
            ItemStack itemStack = ItemStack.fromNbt(nbt.getCompound("ArmorItem"));
            if (!itemStack.isEmpty() && this.isHorseArmor(itemStack)) {
                this.items.setStack(1, itemStack);
            }
        }
    }

    @Override
    public EntityView method_48926() {
        return null;
    }

    @ModifyConstant(method = "getInventorySize", constant = @Constant(intValue = 17))
    private int muleLessChestSpace(int constant){
        if ((AbstractDonkeyEntity)(Object)this instanceof MuleEntity) {
            return 11;
        }
        return constant;
    }
}
