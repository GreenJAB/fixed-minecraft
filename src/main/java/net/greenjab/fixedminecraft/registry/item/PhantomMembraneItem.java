package net.greenjab.fixedminecraft.registry.item;

import net.greenjab.fixedminecraft.registry.registries.GameruleRegistry;
import net.greenjab.fixedminecraft.registry.registries.StatusRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class PhantomMembraneItem extends Item {
    public PhantomMembraneItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        super.finishUsing(stack, world, user);
        if (user instanceof ServerPlayerEntity serverPlayerEntity) {
            if (!serverPlayerEntity.hasStatusEffect(StatusRegistry.INSOMNIA)) {
                serverPlayerEntity.networkHandler.sendPacket(
                        new GameStateChangeS2CPacket(
                                GameStateChangeS2CPacket.ELDER_GUARDIAN_EFFECT,
                                2f
                        )
                );
            }
            serverPlayerEntity.addStatusEffect(new StatusEffectInstance(StatusRegistry.INSOMNIA, -1, 0, true, false, true));
            serverPlayerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 400));
        }
        return stack;
    }
}
