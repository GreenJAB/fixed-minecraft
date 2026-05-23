package net.greenjab.fixedminecraft.registry.item;

import net.greenjab.fixedminecraft.registry.other.BrickEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class NewBrickItem extends Item implements ProjectileItem {

    public NewBrickItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    public @NonNull InteractionResult use(Level world, Player user, @NonNull InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        world.playSound(
                null,
                user.getX(),
                user.getY(),
                user.getZ(),
                SoundEvents.SNOWBALL_THROW,
                SoundSource.PLAYERS,
                0.5F,
                0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
        );
        if (world instanceof ServerLevel serverWorld) {
            Projectile.spawnProjectileFromRotation(BrickEntity::new, serverWorld, itemStack, user, 0.0F, 1.5f, 1.0F);
        }

        user.awardStat(Stats.ITEM_USED.get(this));
        itemStack.consume(1, user);
        return InteractionResult.SUCCESS;
    }

    @Override
    public @NonNull Projectile asProjectile(@NonNull Level world, Position pos, @NonNull ItemStack stack, @NonNull Direction direction) {
        return new Snowball(world, pos.x(), pos.y(), pos.z(), stack);
    }
}
