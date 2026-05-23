package net.greenjab.fixedminecraft.registry.item;

import java.util.Optional;
import java.util.Set;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

public class EchoFruitItem extends Item {
    public EchoFruitItem(Properties settings) {
        super(settings);
    }

    @Override
    public @NonNull ItemStack finishUsingItem(@NonNull ItemStack stack, @NonNull Level world, @NonNull LivingEntity user) {
        super.finishUsingItem(stack, world, user);
        if (user instanceof ServerPlayer serverPlayerEntity) {
            Optional<GlobalPos> deathOpt = serverPlayerEntity.getLastDeathLocation();
            if (!world.isClientSide() && deathOpt.isPresent()) {
                GlobalPos deathPos = deathOpt.get();
                double d = deathPos.pos().getX();
                double e = deathPos.pos().getY();
                double f = deathPos.pos().getZ();
                if (serverPlayerEntity.isPassenger()) {
                    serverPlayerEntity.stopRiding();
                }

                Vec3 vec3d = serverPlayerEntity.position();
                ServerLevel serverWorld = serverPlayerEntity.level().getServer().getLevel(serverPlayerEntity.getLastDeathLocation().get().dimension());
                if (serverWorld!=null) {
                    if (serverPlayerEntity.teleportTo(serverWorld, d, e, f, Set.of(), serverPlayerEntity.getYRot(), serverPlayerEntity.getXRot(), true)) {
                        while (!serverWorld.noCollision(serverPlayerEntity) &&
                               serverPlayerEntity.getY() < serverWorld.getMaxY()) {
                            serverPlayerEntity.setPos(serverPlayerEntity.getX(),
                                    serverPlayerEntity.getY() + 1.0, serverPlayerEntity.getZ());
                        }
                        world.gameEvent(GameEvent.TELEPORT, vec3d, GameEvent.Context.of(serverPlayerEntity));
                        SoundEvent soundEvent = SoundEvents.CHORUS_FRUIT_TELEPORT;
                        SoundSource soundCategory = SoundSource.PLAYERS;

                        world.playSound(null, vec3d.x(), vec3d.y(), vec3d.z(), soundEvent, soundCategory);
                        user.resetFallDistance();
                    }
                }
            }
        }
        return stack;
    }
}
