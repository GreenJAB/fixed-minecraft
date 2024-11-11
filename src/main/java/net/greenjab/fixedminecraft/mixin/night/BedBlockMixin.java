package net.greenjab.fixedminecraft.mixin.night;

import net.greenjab.fixedminecraft.StatusEffects.InsomniaEffect;
import net.greenjab.fixedminecraft.StatusEffects.StatusRegistry;
import net.greenjab.fixedminecraft.registry.GameruleRegistry;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BeetrootsBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BedBlock.class)
public class BedBlockMixin {

    @Inject(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BedBlock;isBedWorking(Lnet/minecraft/world/World;)Z"), cancellable = true)
    private void notTired(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit,
                          CallbackInfoReturnable<ActionResult> cir){
        if (world.getGameRules().getBoolean(GameruleRegistry.INSTANCE.getInsomnia_Sleep_Requirement())) {
            if (!player.hasStatusEffect(StatusRegistry.INSTANCE.getINSOMNIA())) {
                player.sendMessage(Text.translatable("block.minecraft.bed.awake"), true);
                cir.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }
}
