package net.greenjab.fixedminecraft.mixin.client;

import net.greenjab.fixedminecraft.hud.HUDOverlayHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.FixedMinecraftClient;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;

import java.util.ArrayList;

/** Credit: Squeek502 */
@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Unique
    String[] names = {"full_moon", "waning_gibbous", "third_quarter", "waning_crescent", "new_moon", "waxing_crescent", "first_quarter", "waxing_gibbous"};

    @Inject(
             slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=food")),
             at = @At(value = "net.greenjab.fixedminecraft.mixin.util.BeforeInc", args = "intValue=-10", ordinal = 0),
             method = "renderStatusBars"
     )
     private void renderFoodPost(DrawContext context, CallbackInfo info) {
         HUDOverlayHandler.onRender(context);
     }



    @Redirect(method = "renderStatusBars", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/InGameHud;renderArmor(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;IIII)V"
    ))
    private void renderArmorItems(DrawContext context, PlayerEntity player, int i, int j, int k, int x){
        if (FixedMinecraftClient.newArmorHud.getValue()) {
            MinecraftClient client = MinecraftClient.getInstance();
            ArrayList<ItemStack> armor = FixedMinecraft.getArmorBypass(client.player);

            int m = i - (j - 1) * k - 10-6;

            for (int n = 0; n < armor.size(); n++) {
                int n2 = armor.size()-n-1;
                ItemStack stack = armor.get(n2);
                int o = (int) (x + n * 8 * 2.5) + 3;

                if (!stack.isEmpty()) {

                    context.drawItem(player, stack, o, m, n);
                    context.drawStackOverlay(client.textRenderer, stack, o, m);
                }

            }
        } else {
            int l = player.getArmor();
            if (l > 0) {
                int m = i - (j - 1) * k - 10;

                for(int n = 0; n < 10; ++n) {
                    int o = x + n * 8;
                    if (n * 2 + 1 < l) {
                        context.drawGuiTexture(RenderLayer::getGuiTextured, Identifier.ofVanilla("hud/armor_full"), o, m, 9, 9);
                    }

                    if (n * 2 + 1 == l) {
                        context.drawGuiTexture(RenderLayer::getGuiTextured, Identifier.ofVanilla("hud/armor_half"), o, m, 9, 9);
                    }

                    if (n * 2 + 1 > l) {
                        context.drawGuiTexture(RenderLayer::getGuiTextured, Identifier.ofVanilla("hud/armor_empty"), o, m, 9, 9);
                    }
                }

            }
        }
    }

    @Inject(method = "renderMainHud", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;hasStatusBars()Z"))
    private void timeAndLocation(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {

        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;


        boolean clock = player.getMainHandStack().isOf(Items.CLOCK);
        boolean compass = player.getMainHandStack().isOf(Items.COMPASS);
        if (!clock && !compass) {
            clock = player.getOffHandStack().isOf(Items.CLOCK);
            compass = player.getOffHandStack().isOf(Items.COMPASS);
        }

        if (clock||compass) {
            String string = "";
            if (clock) {
                int time = (int) ((player.clientWorld.getTimeOfDay()+6000)%24000);
                int hour = time/1000;
                int min = ((time%1000)*60)/1000;

                int moon = player.clientWorld.getMoonPhase();
                Text moonPhase = Text.translatable("world.moon." + names[moon]);

                string= (hour<10?"0":"") + hour + ":" + (min<10?"0":"") + min + " | " + moonPhase.getString();
            } else {
                string = player.getBlockX() + ", " + player.getBlockY() + ", " + player.getBlockZ();
            }

            int m = context.getScaledWindowWidth() / 2 + 91;
            int r = context.getScaledWindowHeight() - 39 - 10;
            int health = this.getHeartCount(this.getRiddenEntity());
            if (health != 0) {
                int i = this.getHeartRows(health) - 1;
                r = r - i * 10;
            }
            if (player.isSubmergedIn(FluidTags.WATER) || player.getAir() < player.getMaxAir())  r -= 10;

            int left = m - (10 - 1) * 8 - 9;
            int top = r;

            if (player.isCreative() || player.isSpectator()) {
                top = context.getScaledWindowHeight() - 39+6;
                left = context.getScaledWindowWidth() / 2 - 91;
                if (health == 0) left +=91- (client.textRenderer.getWidth(string))/2;
            }

            context.drawText(client.textRenderer, string, left, top, /*8453920*/-1, true);


        }
    }


    @Unique
    @Nullable
    private PlayerEntity getCameraPlayer() {
        return MinecraftClient.getInstance().getCameraEntity() instanceof PlayerEntity playerEntity ? playerEntity : null;
    }

    @Unique
    @Nullable
    private LivingEntity getRiddenEntity() {
        PlayerEntity playerEntity = this.getCameraPlayer();
        if (playerEntity != null) {
            Entity entity = playerEntity.getVehicle();
            if (entity == null) {
                return null;
            }

            if (entity instanceof LivingEntity) {
                return (LivingEntity)entity;
            }
        }

        return null;
    }

    @Unique
    private int getHeartCount(@Nullable LivingEntity entity) {
        if (entity != null && entity.isLiving()) {
            float f = entity.getMaxHealth();
            int i = (int)(f + 0.5F) / 2;
            if (i > 30) {
                i = 30;
            }

            return i;
        } else {
            return 0;
        }
    }

    @Unique
    private int getHeartRows(int heartCount) {
        return (int)Math.ceil(heartCount / 10.0);
    }

    @Redirect(method = "renderHotbar", at = @At(value = "INVOKE",
                                                target = "Lnet/minecraft/entity/player/PlayerEntity;getOffHandStack()Lnet/minecraft/item/ItemStack;"
    ))
    private ItemStack noNetheriteFix(PlayerEntity instance) {return instance.equipment.get(EquipmentSlot.OFFHAND); }
}
