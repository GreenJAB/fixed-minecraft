package net.greenjab.fixedminecraft.mixin.client;


import net.greenjab.fixedminecraft.models.MuleArmorFeatureRenderer;
import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.minecraft.client.render.entity.AbstractDonkeyEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.DonkeyEntityRenderState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractDonkeyEntityRenderer.class)
public class AbstractDonkeyEntityRendererMixin <T extends AbstractDonkeyEntity> {

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    public void addMuleArmorLayer(EntityRendererFactory.Context context, AbstractDonkeyEntityRenderer.Type type, CallbackInfo ci) {
        if (type == AbstractDonkeyEntityRenderer.Type.MULE) {
            AbstractDonkeyEntityRenderer<AbstractDonkeyEntity> current = ((AbstractDonkeyEntityRenderer<AbstractDonkeyEntity>)(Object)this);
            current.addFeature(new MuleArmorFeatureRenderer(current, context.getEntityModels(), context.getEquipmentRenderer()));
        }
    }

    @Inject(method = "updateRenderState(Lnet/minecraft/entity/passive/AbstractDonkeyEntity;Lnet/minecraft/client/render/entity/state/DonkeyEntityRenderState;F)V", at = @At("TAIL"))
    private void sendArmorData(T abstractDonkeyEntity, DonkeyEntityRenderState donkeyEntityRenderState, float f, CallbackInfo ci) {
        ItemStack armor = abstractDonkeyEntity.getBodyArmor();
        donkeyEntityRenderState.headItemAnimationProgress = 0;
        if (armor.isEmpty()) return;
        float data = getArmorValue(armor);
        if (armor.hasEnchantments()) data+=10;
        if (armor.getComponents().contains(DataComponentTypes.REPAIR_COST)) {
            if (armor.getComponents().get(DataComponentTypes.REPAIR_COST).intValue() ==1) data+=10;
        }
        if (armor.getComponents().contains(DataComponentTypes.DYED_COLOR)) {
            DyedColorComponent colorData = armor.getComponents().get(DataComponentTypes.DYED_COLOR);
            if (colorData!=null) data+=colorData.rgb()/100000000f;
        }
        donkeyEntityRenderState.headItemAnimationProgress = data;
    }

    @Unique
    float getArmorValue(ItemStack item) {
        if (item.isOf(Items.LEATHER_HORSE_ARMOR)) return 1f;
        if (item.isOf(ItemRegistry.CHAINMAIL_HORSE_ARMOR)) return 2f;
        if (item.isOf(Items.IRON_HORSE_ARMOR)) return 3f;
        if (item.isOf(Items.GOLDEN_HORSE_ARMOR)) return 4f;
        if (item.isOf(Items.DIAMOND_HORSE_ARMOR)) return 5f;
        if (item.isOf(ItemRegistry.NETHERITE_HORSE_ARMOR)) return 6f;
        return 0f;
    }
}
