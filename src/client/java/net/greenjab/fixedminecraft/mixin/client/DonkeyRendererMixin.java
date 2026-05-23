package net.greenjab.fixedminecraft.mixin.client;

import net.greenjab.fixedminecraft.models.CustomModelLayers;
import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.minecraft.client.model.animal.equine.DonkeyModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.DonkeyRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.state.DonkeyRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.animal.equine.AbstractChestedHorse;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DonkeyRenderer.class)
public abstract class DonkeyRendererMixin<T extends AbstractChestedHorse> {

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    public void addMuleArmorLayer(EntityRendererProvider.Context context, EquipmentClientInfo.LayerType saddleLayer,
                                  ModelLayerLocation saddleModel, DonkeyRenderer.Type adult, DonkeyRenderer.Type baby, CallbackInfo ci) {
        if (saddleLayer == EquipmentClientInfo.LayerType.MULE_SADDLE) {
            DonkeyRenderer<AbstractChestedHorse> current = (DonkeyRenderer<AbstractChestedHorse>)(Object)this;
            current.addLayer(
                    new SimpleEquipmentLayer<>(
                            current,
                            context.getEquipmentRenderer(),
                            EquipmentClientInfo.LayerType.HORSE_BODY,
                            this::getArmorStack,
                            new DonkeyModel(context.bakeLayer(CustomModelLayers.MULE_ARMOR)),
                            null,
                            2
                    )
            );
        }
    }

    @Unique
    private ItemStack getArmorStack(DonkeyRenderState donkeyEntityRenderState) {
        float data = donkeyEntityRenderState.wornHeadAnimationPos;
        ItemStack armor = getArmorItem(((int)(data)) % 10).getDefaultInstance();
        if (data>10) {
            armor.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        }
        if (data>20) {
            armor.set(DataComponents.REPAIR_COST, 1);
        }
        if (data-(int)data!=0) {
            armor.set(DataComponents.DYED_COLOR, new DyedItemColor((int)((data - (int)data) * 100000000)) );
        }
        return armor;
    }
    @Unique
    Item getArmorItem(int i) {
        if (i==1) return Items.LEATHER_HORSE_ARMOR;
        if (i==2) return ItemRegistry.CHAINMAIL_HORSE_ARMOR;
        if (i==3) return Items.COPPER_HORSE_ARMOR;
        if (i==4) return Items.IRON_HORSE_ARMOR;
        if (i==5) return Items.GOLDEN_HORSE_ARMOR;
        if (i==6) return Items.DIAMOND_HORSE_ARMOR;
        if (i==7) return Items.NETHERITE_HORSE_ARMOR;
        return Items.AIR;
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/animal/equine/AbstractChestedHorse;Lnet/minecraft/client/renderer/entity/state/DonkeyRenderState;F)V", at = @At("TAIL"))
    private void sendArmorData(T entity, DonkeyRenderState state, float partialTicks, CallbackInfo ci) {
        ItemStack armor = entity.getBodyArmorItem();
        state.wornHeadAnimationPos = 0;
        if (armor.isEmpty()) return;
        float data = getArmorValue(armor);
        if (armor.isEnchanted()) data+=10;
        if (armor.getComponents().has(DataComponents.REPAIR_COST)) {
            if (armor.getComponents().getOrDefault(DataComponents.REPAIR_COST, 0) ==1) data+=10;
        }
        if (armor.getComponents().has(DataComponents.DYED_COLOR)) {
            DyedItemColor colorData = armor.getComponents().get(DataComponents.DYED_COLOR);
            if (colorData!=null) data+=colorData.rgb()/100000000f;
        }
        state.wornHeadAnimationPos = data;
    }

    @Unique
    float getArmorValue(ItemStack item) {
        if (item.is(Items.LEATHER_HORSE_ARMOR)) return 1f;
        if (item.is(ItemRegistry.CHAINMAIL_HORSE_ARMOR)) return 2f;
        if (item.is(Items.COPPER_HORSE_ARMOR)) return 3f;
        if (item.is(Items.IRON_HORSE_ARMOR)) return 4f;
        if (item.is(Items.GOLDEN_HORSE_ARMOR)) return 5f;
        if (item.is(Items.DIAMOND_HORSE_ARMOR)) return 6f;
        if (item.is(Items.NETHERITE_HORSE_ARMOR)) return 7f;
        return 0f;
    }


}
