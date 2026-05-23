package net.greenjab.fixedminecraft.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.function.Consumer;

@Mixin(CreateWorldScreen.GameTab.class)
public abstract class CreateWorldScreenMixin {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/worldselection/WorldCreationUiState;addListener(Ljava/util/function/Consumer;)V", ordinal = 2))
    private void addKeepInvButton(WorldCreationUiState instance, Consumer<WorldCreationUiState> action,
                                  @Local(ordinal = 1) CycleButton<Difficulty> difficultyButton,
                                  @Local GridLayout.RowHelper helper) {
        instance.addListener(_ -> {
            difficultyButton.setValue(instance.getDifficulty());
            difficultyButton.active = !instance.isHardcore();
            difficultyButton.setTooltip(Tooltip.create(instance.getDifficulty().getInfo()));
        });
        GameRules gameRules = instance.getGameRules();
        CycleButton<Boolean> cyclingButtonWidget = helper.addChild(
                CycleButton.onOffBuilder(gameRules.get(GameRules.KEEP_INVENTORY))
                        .withCustomNarration( button -> button.createDefaultNarrationMessage().append("\n").append(Component.translatable("gamerule.minecraft.keep_inventory")))
                        .withTooltip(_ -> Tooltip.create(Component.translatable("gamerule.minecraft.keep_inventory")))
                        .create(0, 0, 210, 20, Component.translatable("gamerule.minecraft.keep_inventory"),
                                (_, value) -> gameRules.set(GameRules.KEEP_INVENTORY, value, null))
        );
        cyclingButtonWidget.setValue(instance.getGameRules().get(GameRules.KEEP_INVENTORY));
        instance.addListener(_ -> {
            cyclingButtonWidget.setValue(instance.getGameRules().get(GameRules.KEEP_INVENTORY));
            cyclingButtonWidget.active = !instance.isHardcore();
        });
    }
}
