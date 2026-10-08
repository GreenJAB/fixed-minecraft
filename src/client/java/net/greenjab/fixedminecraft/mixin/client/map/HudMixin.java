package net.greenjab.fixedminecraft.mixin.client.map;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.FixedMinecraftClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.waypoints.WaypointStyleAssets;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(Hud.class)
public abstract class HudMixin {
    @Shadow private int toolHighlightTimer;

    @ModifyExpressionValue(method = "nextContextualInfoState", at = @At(
             value = "INVOKE",
             target = "Lnet/minecraft/client/waypoints/ClientWaypointManager;hasWaypoints()Z"
     ))
     private boolean renderMapWayPoints(boolean original) {
        Minecraft client = Minecraft.getInstance();
        assert client.player != null;
        LocalPlayer player = client.player;
        if (player.getMainHandItem().getComponents().has(DataComponents.MAP_ID) ||
            player.getOffhandItem().getComponents().has(DataComponents.MAP_ID)) return true;
        if (FixedMinecraft.gameRules.global_locator_bar) return original;

        AtomicBoolean hasWaypoint = new AtomicBoolean(false);
        assert client.getCameraEntity() != null;
        client.player.connection.getWaypointManager().forEachWaypoint(client.getCameraEntity(), (waypoint) -> {
            if (!(Boolean)waypoint.id().left().map((uuid) -> uuid.equals(client.getCameraEntity().getUUID())).orElse(false)) {
                if ((waypoint.icon().style != WaypointStyleAssets.DEFAULT)) hasWaypoint.set(true);
            }
        });
        return hasWaypoint.get();
     }


    @WrapOperation(method = "extractSelectedItemName", at =
    @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getHoverName()Lnet/minecraft/network/chat/Component;"))
    private Component renderLocatorBarIconName(ItemStack instance, Operation<Component> original) {
        if (!Objects.equals(FixedMinecraftClient.locatorBarName, Component.empty()) && this.toolHighlightTimer<10) return FixedMinecraftClient.locatorBarName;
        return original.call(instance);
    }
    @WrapOperation(method = "extractSelectedItemName", at =
    @At(value = "FIELD", target = "Lnet/minecraft/client/gui/Hud;toolHighlightTimer:I", opcode = Opcodes.GETFIELD))
    private int renderLocatorBarIconNameTimer1(Hud instance, Operation<Integer> original) {
        if (FixedMinecraftClient.locatorBarName == null ) FixedMinecraftClient.locatorBarName = Component.empty();
        if (!Objects.equals(FixedMinecraftClient.locatorBarName, Component.empty()) && this.toolHighlightTimer<10) return 40;
        return original.call(instance);
    }
    @WrapOperation(method = "extractSelectedItemName", at =
    @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"))
    private boolean renderLocatorBarIconNameTimer2(ItemStack instance, Operation<Boolean> original) {
        if (!Objects.equals(FixedMinecraftClient.locatorBarName, Component.empty()) && this.toolHighlightTimer<10) return false;
        return original.call(instance);
    }
    @Inject(method = "extractSelectedItemName", at = @At(value = "TAIL"))
    private void renderLocatorBarIconNameClear(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        FixedMinecraftClient.locatorBarName = Component.empty();
    }
}
