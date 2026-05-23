package net.greenjab.fixedminecraft.registry.item;

import net.greenjab.fixedminecraft.registry.registries.MobEffectRegistry;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class NewPhantomMembraneItem extends Item {
    public NewPhantomMembraneItem(Properties settings) {
        super(settings);
    }

    @Override
    public @NonNull ItemStack finishUsingItem(@NonNull ItemStack stack, @NonNull Level world, @NonNull LivingEntity user) {
        super.finishUsingItem(stack, world, user);
        if (user instanceof ServerPlayer serverPlayerEntity) {
            if (!serverPlayerEntity.hasEffect(MobEffectRegistry.INSOMNIA)) {
                serverPlayerEntity.connection.send(
                        new ClientboundGameEventPacket(
                                ClientboundGameEventPacket.GUARDIAN_ELDER_EFFECT,
                                2f
                        )
                );
            }
            serverPlayerEntity.addEffect(new MobEffectInstance(MobEffectRegistry.INSOMNIA, -1, 0, true, false, true));
            serverPlayerEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 400));
        }
        return stack;
    }
}
