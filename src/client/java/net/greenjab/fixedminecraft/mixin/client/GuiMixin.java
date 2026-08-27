package net.greenjab.fixedminecraft.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.FixedMinecraftClient;
import net.greenjab.fixedminecraft.hud.HUDOverlayHandler;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.clock.ClockManager;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.timeline.Timelines;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow @Final private Minecraft minecraft;
    @Shadow protected abstract void extractSlot(GuiGraphicsExtractor graphics, int x, int y, DeltaTracker deltaTracker, Player player,
                                        ItemStack itemStack, int seed);
    @Shadow private int displayHealth;
    @Shadow @org.jspecify.annotations.Nullable protected abstract LivingEntity getPlayerVehicleWithHealth();
    @Shadow protected abstract int getVehicleMaxHearts(@org.jspecify.annotations.Nullable LivingEntity vehicle);

    @Inject(slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=food")),
            at = @At(value = "net.greenjab.fixedminecraft.mixin.util.BeforeInc", args = "intValue=-10", ordinal = 0),
            method = "extractPlayerHealth")
    private void renderFoodPost(GuiGraphicsExtractor graphics, CallbackInfo ci) {
         HUDOverlayHandler.onRender(graphics);
     }

    @ModifyExpressionValue(method = "extractPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;getVehicleMaxHearts(Lnet/minecraft/world/entity/LivingEntity;)I"))
    private int renderFoodOnMount1(int constant) {
        return 0;
    }

    @ModifyArg(method = "extractPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;extractAirBubbles(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/entity/player/Player;III)V"), index = 2)
    private int renderFoodOnMount2(int constant) {
        return this.getVehicleMaxHearts(this.getPlayerVehicleWithHealth());
    }

    @ModifyExpressionValue(method = "extractVehicleHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;guiHeight()I"))
    private int renderFoodOnMount3(int constant) {
        if (Minecraft.getInstance().player.isCreative()&&!(this.minecraft.options.keyPlayerList.isDown())) return constant;
        return constant -10;
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

    @WrapOperation(method = "extractPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;extractArmor(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/entity/player/Player;IIII)V"))
    private void renderArmorItems(GuiGraphicsExtractor graphics, Player player, int yLineBase, int numHealthRows, int healthRowHeight, int xLeft, Operation<Void> original){
        if (FixedMinecraftClient.itemArmorHud.get() || this.minecraft.options.keyPlayerList.isDown()) {
            Minecraft client = Minecraft.getInstance();
            assert client.player != null;
            ArrayList<ItemStack> armor = FixedMinecraft.getArmorBypass(client.player);
            int yLineArmor = yLineBase - (numHealthRows - 1) * healthRowHeight - 10 - 6;
            for (int n = 0; n < armor.size(); n++) {
                int n2 = armor.size() - n - 1;
                ItemStack stack = armor.get(n2);
                int o = xLeft + n * 20 + 3;
                if (!stack.isEmpty()) {
                    graphics.item(player, stack, o, yLineArmor, n);
                    graphics.itemDecorations(client.font, stack, o, yLineArmor);
                }
            }
            if (player.getControlledVehicle() instanceof LivingEntity entity) {
                ItemStack stack = entity.equipment.get(EquipmentSlot.BODY);
                int o = xLeft + 80 + 3;
                if (!stack.isEmpty()) {
                    graphics.item(player, stack, o, yLineArmor, 4);
                    graphics.itemDecorations(client.font, stack, o, yLineArmor);
                }
            }
        } else original.call(graphics, player, yLineBase, numHealthRows, healthRowHeight, xLeft);
    }

    @Unique private static final String[] names = {"full_moon", "waning_gibbous", "third_quarter", "waning_crescent", "new_moon", "waxing_crescent", "first_quarter", "waxing_gibbous"};
    @Unique private static final String[] icons = {"fish", "null", "farm", "null", "looting", "null", "fortune", "null"};

    @Inject(method = "extractHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;canHurtPlayer()Z"))
    private void timeAndLocation(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        if (player.isSpectator()) return;
        assert client.player != null;
        boolean clock = player.getMainHandItem().is(Items.CLOCK);
        boolean compass = player.getMainHandItem().is(Items.COMPASS);
        boolean daylight = player.getMainHandItem().is(Items.DAYLIGHT_DETECTOR);
        if (!clock && !compass && !daylight) {
            clock = player.getOffhandItem().is(Items.CLOCK);
            compass = player.getOffhandItem().is(Items.COMPASS);
            daylight = player.getOffhandItem().is(Items.DAYLIGHT_DETECTOR);
        }
        if (clock||compass||daylight) {
            String string;
            if (clock) {
                if (player.isCrouching()) {
                    AtomicReference<String> s = new AtomicReference<>("");
                    ClockManager clockManager = client.level.clockManager();
                    client.level.registryAccess()
                            .get(Timelines.OVERWORLD_DAY)
                            .ifPresent(timeline -> s.set(Component.translatable("world.day", (timeline.value()).getPeriodCount(clockManager)).getString()));
                    string = s.get();
                } else {
                    int time = (int) ((player.level().getOverworldClockTime()+6000)%24000);
                    int hour = time/1000;
                    int min = ((time%1000)*60)/1000;
                    assert client.level != null;
                    int moon =client.level.environmentAttributes().getValue(EnvironmentAttributes.MOON_PHASE, player.blockPosition()).index();
                    string = (hour<10?"0":"") + hour + ":" + (min<10?"0":"") + min + " | ";
                    if (!player.level().isDarkOutside() || !player.level().canSeeSky(player.blockPosition())) string = string+"§7";
                    string = string + Component.translatable("world.moon." + names[moon]).getString();
                    string = string + Component.translatable("world.moon." + icons[moon]).getString();
                }
            } else if (compass) {
                if (player.isCrouching()) {
                    string = String.format(Locale.ROOT, "%.1f / %.1f", Mth.wrapDegrees(player.getYRot()), Mth.wrapDegrees(player.getXRot()));
                } else string = getDirection(player.getYRot()) + " | " + player.getBlockX() + ", " + player.getBlockY() + ", " + player.getBlockZ();
            } else {
                string = Component.translatable("world.light", client.level.getBrightness(LightLayer.SKY, player.blockPosition()), client.level.getBrightness(LightLayer.BLOCK, player.blockPosition())).getString();
            }

            int top = graphics.guiHeight() - 39 - 10;
            int health = this.getVehicleMaxHearts(this.getPlayerVehicleWithHealth());
            if (health != 0) top -= this.getHeartRows(health) * 10;
            if (player.isEyeInFluid(FluidTags.WATER) || player.getAirSupply() < player.getMaxAirSupply())  top -= 10;
            int left = graphics.guiWidth() / 2 + 91 - 72 - 9;

            if ((player.isCreative()&&!this.minecraft.options.keyPlayerList.isDown())) {
                top = graphics.guiHeight() - 39;
                if (!(client.player.connection.getWaypointManager().hasWaypoints() ||
                      player.getMainHandItem().getComponents().has(DataComponents.MAP_ID) || player.getOffhandItem().getComponents().has(DataComponents.MAP_ID) ||
                    client.player.jumpableVehicle() != null)) {
                    top+=6;
                }
                left = graphics.guiWidth() / 2 - 91;
                if (health == 0) left +=91- (client.font.width(string))/2;
            }
            graphics.text(client.font, string, left, top, -1, true);
        }
    }

    @Unique private String getDirection(float yaw) {
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

    @Unique @Nullable private Player getCameraPlayer() {
        return Minecraft.getInstance().getCameraEntity() instanceof Player playerEntity ? playerEntity : null;
    }

    @Unique private int getHeartRows(int heartCount) {
        return Mth.ceil(heartCount / 10.0);
    }

    @WrapOperation(method = "extractItemHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getOffhandItem()Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack noNetheriteFix(Player instance, Operation<ItemStack> original) {return instance.equipment.get(EquipmentSlot.OFFHAND); }

    @Unique private static final Identifier SLOTS_TEXTURE = FixedMinecraft.id("inv_scroll");

    @Inject(method = "extractItemHotbar", at = @At(value = "INVOKE",
                                                   target = "Lnet/minecraft/client/gui/Gui;extractSlot(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IILnet/minecraft/client/DeltaTracker;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;I)V", ordinal = 0))
    private void previewInv(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci, @Local Player player, @Local(ordinal = 4) int i, @Local(ordinal = 5) int x, @Local(ordinal = 6) int y, @Local(ordinal = 3) int seed) {
        if (this.minecraft.options.keyPlayerList.isDown()){
            float maxHealth = Math.max((float)player.getAttributeValue(Attributes.MAX_HEALTH), this.displayHealth);
            int totalAbsorption = Mth.ceil(player.getAbsorptionAmount());
            int numHealthRows = Mth.ceil((maxHealth + totalAbsorption) / 2.0F / 10.0F);
            int healthRowHeight = Math.max(10 - (numHealthRows - 2), 3);
            int left = numHealthRows*healthRowHeight;
            AtomicBoolean armour = new AtomicBoolean(false);
            FixedMinecraft.getArmorBypass(player).forEach(stack -> {if (!stack.isEmpty()) armour.set(true);});
            if (player.getControlledVehicle() instanceof LivingEntity entity && !entity.equipment.get(EquipmentSlot.BODY).isEmpty()) armour.set(true);
            if (armour.get()) left+=15;
            int right = 10+10*getHeartRows(this.getVehicleMaxHearts(this.getPlayerVehicleWithHealth()));
            if (player.getMainHandItem().is(Items.CLOCK)||player.getMainHandItem().is(Items.COMPASS)||player.getMainHandItem().is(Items.DAYLIGHT_DETECTOR)
                ||player.getOffhandItem().is(Items.CLOCK)||player.getOffhandItem().is(Items.COMPASS)||player.getOffhandItem().is(Items.DAYLIGHT_DETECTOR)) right += 10;
            if ((player.isEyeInFluid(FluidTags.WATER) || player.getAirSupply() < player.getMaxAirSupply())) right += 10;
            y-=70+Math.max(left,right);
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOTS_TEXTURE, 20, 60, 0, 0, x-2, y-2, 20, 60, ARGB.white(0.6f));

            this.extractSlot(graphics, x, y, deltaTracker, player, player.getInventory().getItem(i+9), seed);
            this.extractSlot(graphics, x, y+20, deltaTracker, player, player.getInventory().getItem(i+18), seed);
            this.extractSlot(graphics, x, y+40, deltaTracker, player, player.getInventory().getItem(i+27), seed);
        }
    }
}
