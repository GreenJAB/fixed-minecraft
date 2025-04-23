package net.greenjab.fixedminecraft.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.text.Text;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.function.Consumer;

@Mixin(CreateWorldScreen.GameTab.class)
public class CreateWorldScreenMixin {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/world/WorldCreator;addListener(Ljava/util/function/Consumer;)V", ordinal = 2))
    private void addKeepInvButton(WorldCreator instance, Consumer<WorldCreator> listener,
                                   @Local(ordinal = 1) CyclingButtonWidget<Difficulty> cyclingButtonWidget2, @Local GridWidget.Adder adder) {
        instance.addListener( creator -> {
            cyclingButtonWidget2.setValue(instance.getDifficulty());
            cyclingButtonWidget2.active = !instance.isHardcore();
            cyclingButtonWidget2.setTooltip(Tooltip.of(instance.getDifficulty().getInfo()));
        });

        CyclingButtonWidget<Boolean> cyclingButtonWidget = adder.add(
                CyclingButtonWidget.onOffBuilder()
                        .tooltip(value -> Tooltip.of(Text.translatable("gamerule.keepInventory")))
                        .build(0, 0, 210, 20, Text.translatable("gamerule.keepInventory"),  (button, value) -> {
                            GameRules gameRules = instance.getGameRules();
                            gameRules.get(GameRules.KEEP_INVENTORY).set(value, null);
                        })
        );
        cyclingButtonWidget.setValue(instance.getGameRules().getBoolean(GameRules.KEEP_INVENTORY));
        instance.addListener( creator -> {
            cyclingButtonWidget.setValue(instance.getGameRules().getBoolean(GameRules.KEEP_INVENTORY));
            cyclingButtonWidget.active = !instance.isHardcore();
        });
    }
}
