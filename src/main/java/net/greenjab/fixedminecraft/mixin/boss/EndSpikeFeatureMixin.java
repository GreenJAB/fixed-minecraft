package net.greenjab.fixedminecraft.mixin.boss;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EndSpikeFeature.class)
public class EndSpikeFeatureMixin /*extends Feature<EndSpikeFeatureConfig>*/ {

    /*public EndSpikeFeatureMixin(Codec<EndSpikeFeatureConfig> configCodec) {
        super(configCodec);
    }//*/

    /*@Inject(method = "generateSpike", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/feature/EndSpikeFeature$Spike;isGuarded()Z"))
    private void removeBowCheese(ServerWorldAccess world, Random random, EndSpikeFeatureConfig config, EndSpikeFeature.Spike spike,
                                 CallbackInfo ci) {
        if (spike.isGuarded()) {
            //EndSpikeFeature DFE = (EndSpikeFeature)(Object)this;
            //EndSpikeFeature.Spike DFE2 = (EndSpikeFeature.Spike)(Object)this;
            for (int i = 0;i<4;i++) {
                BlockPos.Mutable mutable = new BlockPos.Mutable();
                BlockState blockState = Blocks.IRON_BARS
                        .getDefaultState()
                        .with(PaneBlock.NORTH, Boolean.valueOf(i<2))
                        .with(PaneBlock.SOUTH, Boolean.valueOf(i>2))
                        .with(PaneBlock.WEST, Boolean.valueOf(i%2==0))
                        .with(PaneBlock.EAST, Boolean.valueOf(i%2==1));
                //this.setBlockState(world, mutable.set(spike.getCenterX() + 2*(i<2?1:-1), spike.getHeight() -1, spike.getCenterZ() + 2*(i%2==0?1:-1)), blockState);
            }
        }
    }*/

    @ModifyConstant(method = "generateSpike", constant = @Constant(intValue = 0, ordinal = 0))
    private int removeBowCheese(int constant) {
        return -1;
    }

    @ModifyArg(method = "generateSpike", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/gen/feature/EndSpikeFeature;setBlockState(Lnet/minecraft/world/ModifiableWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", ordinal = 2
    ), index = 2)
    private BlockState removeBowCheese2(BlockState blockState, @Local(ordinal = 3)int m, @Local(ordinal = 4)int n, @Local(ordinal = 5)int o) {
        //System.out.println(o + ", " + m + ", " + n);
        if (o==-1) {
            if (Math.abs(m) != Math.abs(n) ) {
                System.out.println("obsidian");
                return Blocks.OBSIDIAN.getDefaultState();
            }
        }
        return blockState;
    }

   /* @Override
    public boolean generate(FeatureContext<EndSpikeFeatureConfig> context) {
        return false;
    }//*/
}
