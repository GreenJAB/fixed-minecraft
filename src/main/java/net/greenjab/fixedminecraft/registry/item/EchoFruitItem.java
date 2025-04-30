package net.greenjab.fixedminecraft.registry.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.Optional;
import java.util.Set;

public class EchoFruitItem extends Item {
    public EchoFruitItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        super.finishUsing(stack, world, user);
        if (user instanceof ServerPlayerEntity serverPlayerEntity) {
            Optional<GlobalPos> deathOpt = serverPlayerEntity.getLastDeathPos();
            if (!world.isClient && deathOpt.isPresent()) {
                GlobalPos deathPos = deathOpt.get();
                double d = deathPos.pos().getX();
                double e = deathPos.pos().getY();
                double f = deathPos.pos().getZ();
                if (serverPlayerEntity.hasVehicle()) {
                    serverPlayerEntity.stopRiding();
                }

                Vec3d vec3d = serverPlayerEntity.getPos();
                ServerWorld serverWorld = serverPlayerEntity.server.getWorld(serverPlayerEntity.getLastDeathPos().get().dimension());
                if (serverPlayerEntity.teleport(serverWorld, d, e, f, Set.of(), serverPlayerEntity.getYaw(), serverPlayerEntity.getPitch(), true)) {
                    while (!serverWorld.isSpaceEmpty(serverPlayerEntity) && serverPlayerEntity.getY() < serverWorld.getTopYInclusive()) {
                        serverPlayerEntity.setPosition(serverPlayerEntity.getX(), serverPlayerEntity.getY() + 1.0, serverPlayerEntity.getZ());
                    }
                    world.emitGameEvent(GameEvent.TELEPORT, vec3d, GameEvent.Emitter.of(serverPlayerEntity));
                    SoundEvent soundEvent = SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT;
                    SoundCategory soundCategory = SoundCategory.PLAYERS;

                    world.playSound(null, vec3d.getX(), vec3d.getY(), vec3d.getZ(), soundEvent, soundCategory);
                    user.onLanding();

                }
            }
        }
        return stack;
    }
}
