package net.greenjab.fixedminecraft.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.FixedMinecraftClient;
import net.greenjab.fixedminecraft.hud.HUDOverlayHandler;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookItem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.MoonPhase;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.ArrayList;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    protected abstract void extractSlot(GuiGraphicsExtractor graphics, int x, int y, DeltaTracker deltaTracker, Player player,
                                        ItemStack itemStack, int seed);

    @Inject(slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=food")),
             at = @At(value = "net.greenjab.fixedminecraft.mixin.util.BeforeInc", args = "intValue=-10", ordinal = 0),
             method = "extractPlayerHealth")
    private void renderFoodPost(GuiGraphicsExtractor graphics, CallbackInfo ci) {
         HUDOverlayHandler.onRender(graphics);
     }

    @ModifyExpressionValue(method = "extractHotbarAndDecorations", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;canHurtPlayer()Z"))
    private boolean renderCreativeHealthAndArmour(boolean original){
        return original || (this.minecraft.options.keyPlayerList.isDown()&&!getCameraPlayer().isSpectator());
    }

    @ModifyExpressionValue(method = "extractHotbarAndDecorations", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;hasExperience()Z"))
    private boolean renderCreativeExperience(boolean original){
        return original || (this.minecraft.options.keyPlayerList.isDown()&&!getCameraPlayer().isSpectator());
    }
    @ModifyExpressionValue(method = "nextContextualInfoState", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;hasExperience()Z"))
    private boolean renderCreativeExperience2(boolean original){
        return original || (this.minecraft.options.keyPlayerList.isDown()&&!getCameraPlayer().isSpectator());
    }


    @Redirect(method = "extractPlayerHealth", at = @At(
             value = "INVOKE",
             target = "Lnet/minecraft/client/gui/Gui;extractArmor(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/entity/player/Player;IIII)V"))
    private void renderArmorItems(GuiGraphicsExtractor graphics, Player player, int yLineBase, int numHealthRows, int healthRowHeight, int xLeft){
         if (FixedMinecraftClient.newArmorHud.get()) {
             Minecraft client = Minecraft.getInstance();
             assert client.player != null;
             ArrayList<ItemStack> armor = FixedMinecraft.getArmorBypass(client.player);

             int yLineArmor = yLineBase - (numHealthRows - 1) * healthRowHeight - 10 - 6;

             for (int n = 0; n < armor.size(); n++) {
                 int n2 = armor.size() - n - 1;
                 ItemStack stack = armor.get(n2);
                 int o = (int) (xLeft + n * 8 * 2.5) + 3;

                 if (!stack.isEmpty()) {
                     graphics.item(player, stack, o, yLineArmor, n);
                     graphics.itemDecorations(client.font, stack, o, yLineArmor);
                 }

             }
         } else {
             int l = player.getArmorValue();
             if (l > 0) {
                 int m = yLineBase - (numHealthRows - 1) * healthRowHeight - 10;

                 for(int n = 0; n < 10; ++n) {
                     int o = xLeft + n * 8;
                     if (n * 2 + 1 < l)
                         graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.withDefaultNamespace("hud/armor_full"), o, m, 9, 9);

                     if (n * 2 + 1 == l)
                         graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.withDefaultNamespace("hud/armor_half"), o, m, 9, 9);

                     if (n * 2 + 1 > l)
                         graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.withDefaultNamespace("hud/armor_empty"), o, m, 9, 9);
                 }
             }
         }
     }

    @Unique
    private static String[] names = {"full_moon", "waning_gibbous", "third_quarter", "waning_crescent", "new_moon", "waxing_crescent", "first_quarter", "waxing_gibbous"};

    @Inject(method = "extractHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;canHurtPlayer()Z"))
    private void timeAndLocation(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
         Minecraft client = Minecraft.getInstance();
         LocalPlayer player = client.player;
         if (player.isSpectator()) return;
         assert client.player != null;
         boolean clock = player.getMainHandItem().is(Items.CLOCK);
         boolean compass = player.getMainHandItem().is(Items.COMPASS);
         if (!clock && !compass) {
             clock = player.getOffhandItem().is(Items.CLOCK);
             compass = player.getOffhandItem().is(Items.COMPASS);
         }

         if (clock||compass) {
             String string;
             if (clock) {
                 int time = (int) ((player.level().getOverworldClockTime()+6000)%24000);
                 int hour = time/1000;
                 int min = ((time%1000)*60)/1000;

                 assert client.level != null;
                 MoonPhase moonPhase =client.level.environmentAttributes().getValue(EnvironmentAttributes.MOON_PHASE, player.blockPosition());
                 int moon = moonPhase.index();
                 Component textMoonPhase = Component.translatable("world.moon." + names[moon]);
                 if (player.level().isDarkOutside() && player.level().canSeeSky(player.blockPosition()))
                    string= (hour<10?"0":"") + hour + ":" + (min<10?"0":"") + min + " | " + textMoonPhase.getString();
                 else string= (hour<10?"0":"") + hour + ":" + (min<10?"0":"") + min + " | §7" + textMoonPhase.getString();
             } else {
                 string = getDirection(player.getYRot()) + " | " + player.getBlockX() + ", " + player.getBlockY() + ", " + player.getBlockZ();
             }

             int m = graphics.guiWidth() / 2 + 91;
             int r = graphics.guiHeight() - 39 - 10;
             int health = this.getHeartCount(this.getRiddenEntity());
             if (health != 0) {
                 int i = this.getHeartRows(health) - 1;
                 r = r - i * 10;
             }
             if (player.isEyeInFluid(FluidTags.WATER) || player.getAirSupply() < player.getMaxAirSupply())  r -= 10;

             int left = m - (10 - 1) * 8 - 9;
             int top = r;

             if ((player.isCreative()&&!this.minecraft.options.keyPlayerList.isDown())) {
                 top = graphics.guiHeight() - 39;
                 if (!(client.player.connection.getWaypointManager().hasWaypoints() ||
                       player.getMainHandItem().getItem() instanceof MapBookItem ||
                       player.getOffhandItem().getItem() instanceof MapBookItem ||
                       player.getMainHandItem().getItem() instanceof MapItem ||
                       player.getOffhandItem().getItem() instanceof MapItem ||
                       client.player.jumpableVehicle() != null)) {
                     top+=6;
                 }
                 left = graphics.guiWidth() / 2 - 91;
                 if (health == 0) left +=91- (client.font.width(string))/2;
             }

             graphics.text(client.font, string, left, top, -1, true);
         }
     }

    @Unique
    private String getDirection(float yaw) {
        while (yaw<0) yaw+=360;
        yaw+=360+180;
        yaw%=360;
        yaw-=22.5f;
        int dirID = 0;
        String[] dirs = {"N","NE","E","SE","S","SW","W","NW","N"};
        while (yaw>0){
            yaw-=45;
            dirID++;
        }
        return dirs[dirID];
    }


    @Unique
    @Nullable
    private Player getCameraPlayer() {
        return Minecraft.getInstance().getCameraEntity() instanceof Player playerEntity ? playerEntity : null;
    }

    @Unique
    @Nullable
    private LivingEntity getRiddenEntity() {
        Player playerEntity = this.getCameraPlayer();
        if (playerEntity == null) {
            Entity entity = playerEntity.getVehicle();
            if (entity instanceof LivingEntity) return (LivingEntity)entity;
        }
        return null;
    }

    @Unique
    private int getHeartCount(@Nullable LivingEntity entity) {
        if (entity != null && entity.showVehicleHealth()) {
            float f = entity.getMaxHealth();
            int i = (int)(f + 0.5F) / 2;
            if (i > 30) i = 30;
            return i;
        } else return 0;
    }

    @Unique
    private int getHeartRows(int heartCount) {
        return Mth.ceil(heartCount / 10.0);
    }

    @Redirect(method = "extractItemHotbar", at = @At(value = "INVOKE",
                                                     target = "Lnet/minecraft/world/entity/player/Player;getOffhandItem()Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack noNetheriteFix(Player instance) {return instance.equipment.get(EquipmentSlot.OFFHAND); }

    @Unique
    private static final Identifier SLOTS_TEXTURE = FixedMinecraft.id("inv_scroll");

    @Inject(method = "extractItemHotbar", at = @At(value = "INVOKE",
                                                   target = "Lnet/minecraft/client/gui/Gui;extractSlot(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IILnet/minecraft/client/DeltaTracker;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;I)V", ordinal = 0))
    private void previewInv(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci, @Local Player player, @Local(ordinal = 4) int i, @Local(ordinal = 5) int x, @Local(ordinal = 6) int y, @Local(ordinal = 3) int seed) {
        if (this.minecraft.options.keyPlayerList.isDown()){
            y=y-95;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOTS_TEXTURE, 20, 60, 0, 0, x-2, y-2, 20, 60, ARGB.white(0.6f));

            this.extractSlot(graphics, x, y, deltaTracker, player, player.getInventory().getItem(i+9), seed);
            this.extractSlot(graphics, x, y+20, deltaTracker, player, player.getInventory().getItem(i+18), seed);
            this.extractSlot(graphics, x, y+40, deltaTracker, player, player.getInventory().getItem(i+27), seed);
        }
    }

}
