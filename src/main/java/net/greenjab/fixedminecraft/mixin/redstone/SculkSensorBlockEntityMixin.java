package net.greenjab.fixedminecraft.mixin.redstone;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.greenjab.fixedminecraft.util.AmethystSculkSensorAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CalibratedSculkSensorBlock;
import net.minecraft.world.level.block.entity.SculkSensorBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.function.ToIntFunction;
import static net.minecraft.world.level.gameevent.vibrations.VibrationSystem.getResonanceEventByFrequency;

@Mixin(SculkSensorBlockEntity.VibrationUser.class)
public abstract class SculkSensorBlockEntityMixin {

    @Shadow @Final protected BlockPos blockPos;

    @Inject(method = "canReceiveVibration", at = @At( value = "HEAD"), cancellable = true)
    private void setAmethystBlockAlreadyResonated(ServerLevel level, BlockPos pos, Holder<GameEvent> event,
                                                  GameEvent.@Nullable Context context, CallbackInfoReturnable<Boolean> cir) {
        BlockState sculkState = level.getBlockState(this.blockPos);
        if (sculkState.is(Blocks.CALIBRATED_SCULK_SENSOR) && sculkState.getValue(AmethystSculkSensorAccessor.AMETHYST)) {
            if (getCalibratedGameEventFrequency(event) == 1) return;
            cir.setReturnValue(false);
        }
    }

    @WrapOperation(method = "onReceiveVibration", at =
    @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/SculkSensorBlockEntity;setLastVibrationFrequency(I)V"))
    private void test(SculkSensorBlockEntity instance, int lastVibrationFrequency, Operation<Void> original,
                      @Local(argsOnly = true) ServerLevel level,
                      @Local(argsOnly = true) Holder<GameEvent> event) {
        BlockState state = instance.getBlockState();
        if (state.is(Blocks.CALIBRATED_SCULK_SENSOR)) {
            Direction direction = (state.getValue(CalibratedSculkSensorBlock.FACING)).getOpposite();
            if (level.getSignal(instance.getBlockPos().relative(direction), direction) != 0) {
                int eventFrequency = getCalibratedGameEventFrequency(event);
                original.call(instance, eventFrequency);
                return;
            }
        }
        original.call(instance, lastVibrationFrequency);
    }

    @Unique ToIntFunction<ResourceKey<GameEvent>> CALLIBRATED_VIBRATION_FREQUENCY_FOR_EVENT = Util.make(new Reference2IntOpenHashMap<>(), /* lambda$static$0 */ map -> {
        map.defaultReturnValue(0);
        for (int i = 1; i <= 15; i++) map.put(getResonanceEventByFrequency(i), 1);
        map.put(GameEvent.STEP.key(), 2);
        map.put(GameEvent.SWIM.key(), 3);
        map.put(GameEvent.FLAP.key(), 4);
        map.put(GameEvent.PROJECTILE_LAND.key(), 2);
        map.put(GameEvent.HIT_GROUND.key(), 3);
        map.put(GameEvent.SPLASH.key(), 4);
        map.put(GameEvent.ITEM_INTERACT_FINISH.key(), 2);
        map.put(GameEvent.PROJECTILE_SHOOT.key(), 3);
        map.put(GameEvent.INSTRUMENT_PLAY.key(), 4);
        map.put(GameEvent.ENTITY_ACTION.key(), 2);
        map.put(GameEvent.ELYTRA_GLIDE.key(), 3);
        map.put(GameEvent.UNEQUIP.key(), 4);
        map.put(GameEvent.ENTITY_DISMOUNT.key(), 2);
        map.put(GameEvent.EQUIP.key(), 3);
        map.put(GameEvent.ENTITY_INTERACT.key(), 2);
        map.put(GameEvent.SHEAR.key(), 3);
        map.put(GameEvent.ENTITY_MOUNT.key(), 4);
        map.put(GameEvent.ENTITY_DAMAGE.key(), 2);
        map.put(GameEvent.DRINK.key(), 2);
        map.put(GameEvent.EAT.key(), 3);
        map.put(GameEvent.CONTAINER_CLOSE.key(), 2);
        map.put(GameEvent.BLOCK_CLOSE.key(), 3);
        map.put(GameEvent.BLOCK_DEACTIVATE.key(), 4);
        map.put(GameEvent.BLOCK_DETACH.key(), 5);
        map.put(GameEvent.CONTAINER_OPEN.key(), 2);
        map.put(GameEvent.BLOCK_OPEN.key(), 3);
        map.put(GameEvent.BLOCK_ACTIVATE.key(), 4);
        map.put(GameEvent.BLOCK_ATTACH.key(), 5);
        map.put(GameEvent.PRIME_FUSE.key(), 6);
        map.put(GameEvent.NOTE_BLOCK_PLAY.key(), 7);
        map.put(GameEvent.BLOCK_CHANGE.key(), 2);
        map.put(GameEvent.BLOCK_DESTROY.key(), 2);
        map.put(GameEvent.FLUID_PICKUP.key(), 3);
        map.put(GameEvent.BLOCK_PLACE.key(), 2);
        map.put(GameEvent.FLUID_PLACE.key(), 3);
        map.put(GameEvent.ENTITY_PLACE.key(), 2);
        map.put(GameEvent.LIGHTNING_STRIKE.key(), 3);
        map.put(GameEvent.TELEPORT.key(), 4);
        map.put(GameEvent.ENTITY_DIE.key(), 2);
        map.put(GameEvent.EXPLODE.key(), 3);
    });

    @Unique int getCalibratedGameEventFrequency(final Holder<GameEvent> event) {
        return event.unwrapKey().map(this::getCalibratedGameEventFrequency).orElse(0);
    }
    @Unique int getCalibratedGameEventFrequency(final ResourceKey<GameEvent> event) {
        return CALLIBRATED_VIBRATION_FREQUENCY_FOR_EVENT.applyAsInt(event);
    }
}
