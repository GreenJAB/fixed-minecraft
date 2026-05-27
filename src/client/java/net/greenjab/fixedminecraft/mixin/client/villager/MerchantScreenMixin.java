package net.greenjab.fixedminecraft.mixin.client.villager;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.FixedMinecraftClient;
import net.greenjab.fixedminecraft.util.CustomContainerTextureHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin {
    @Unique
    private static final Identifier CHEST_SLOTS_TEXTURE = Identifier.parse("container/horse/chest_slots");

    @Inject(method = "extractBackground", at = @At(value = "TAIL"))
    private void armorSlotBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci,
                                     @Local(ordinal = 2) int xo,
                                     @Local(ordinal = 3) int yo) {
        MerchantScreen MS = (MerchantScreen) (Object)this;
        int traderLevel = MS.getMenu().getTraderLevel();
        int yOff = FixedMinecraftClient.usingCustomContainers() ? -6:0;
        for (int k = 5 - traderLevel; k < 4; k++) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, CHEST_SLOTS_TEXTURE, 90, 54, 0, 0, xo + 250 - 1 + 2, yo + 8 + k * 18 - 1 +yOff, 18, 18);
        }
    }

    @Redirect(method = "extractLabels", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;"
    ))
    private MutableComponent reverseProfessionSkillTitle(String key, Object[] args) {
        MerchantScreen MS = (MerchantScreen) (Object)this;
        int traderLevel = MS.getMenu().getTraderLevel();
        if (MS.getTitle().getContents() instanceof TranslatableContents)
            return Component.translatable("merchant.title", Component.translatable("merchant.level." + traderLevel), MS.getTitle());
        return (MutableComponent) MS.getTitle();
    }

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void setUpTypeProfessionVillagerUI(CallbackInfo ci) {
        MerchantScreen MS = (MerchantScreen) (Object)this;
        ((CustomContainerTextureHolder) MS).fixedminecraft$setCustomTexture("");
        if (!FixedMinecraftClient.usingCustomContainers()) return;

        double d = 10f;
        double e = d*d;
        Player player = Minecraft.getInstance().player;
        Vec3 vec3d = Objects.requireNonNull(player).getLightProbePosition(1.0f);
        Vec3 vec3d2 = player.getViewVector(1.0F);
        Vec3 vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
        AABB box = player.getBoundingBox().expandTowards(vec3d2.scale(d)).inflate(1.0, 1.0, 1.0);
        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(player, vec3d, vec3d3, box, (entityx) -> !entityx.isSpectator() && entityx.isPickable(), e);

        if (entityHitResult != null) {
            if (entityHitResult.getEntity() instanceof Villager villager) {
                String[] type = villager.getVillagerData().type().getRegisteredName().split(":");
                if (!Objects.equals(type[0], "minecraft")) return;
                String[] profession = villager.getVillagerData().profession().getRegisteredName().split(":");
                if (!Objects.equals(profession[0], "minecraft")) return;
                ((CustomContainerTextureHolder) MS).fixedminecraft$setCustomTexture("/" + type[1] + "/" + profession[1]);
            } else if (entityHitResult.getEntity() instanceof WanderingTrader) {
                ((CustomContainerTextureHolder) MS).fixedminecraft$setCustomTexture("/wandering_trader");
            }
        }
    }
    @ModifyArg(method = "extractBackground", at = @At(value = "INVOKE",
                                                      target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIII)V"
    ), index = 1)
    private Identifier useTypeProfessionVillagerUI(Identifier texture) {
        MerchantScreen MS = (MerchantScreen) (Object)this;
        if (!((CustomContainerTextureHolder) MS).fixedminecraft$getCustomTexture().isEmpty())
            return Identifier.withDefaultNamespace("textures/gui/container/villager" + ((CustomContainerTextureHolder) MS).fixedminecraft$getCustomTexture() + ".png");
        return texture;
    }
}
