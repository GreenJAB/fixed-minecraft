package net.greenjab.fixedminecraft.mixin;

import net.greenjab.fixedminecraft.registry.BlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

//@Debug(export = true)
@Mixin(TreeConfiguredFeatures.class)
public class TreeConfiguredFeaturesMixin {

    //doesnt work
    /*@ModifyArg(method = "bootstrap", slice = @Slice(from = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lnet/minecraft/world/gen/feature/TreeConfiguredFeatures;AZALEA_TREE:Lnet/minecraft/registry/RegistryKey;")),
               at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/stateprovider/BlockStateProvider;of(Lnet/minecraft/block/Block;)Lnet/minecraft/world/gen/stateprovider/SimpleBlockStateProvider;",ordinal = 0),index = 0)
    private static Block azaleaLog(Block block) {
        System.out.println("AAA");
        System.out.println(block.toString());
        return BlockRegistry.AZALEA_LOG;
    }*/

    @Redirect(method = "bootstrap", slice = @Slice(from = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lnet/minecraft/world/gen/feature/TreeConfiguredFeatures;AZALEA_TREE:Lnet/minecraft/registry/RegistryKey;")),
               at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/stateprovider/BlockStateProvider;of(Lnet/minecraft/block/Block;)Lnet/minecraft/world/gen/stateprovider/SimpleBlockStateProvider;",ordinal = 0))
    private static SimpleBlockStateProvider azaleaLog(Block block) {
        System.out.println("AAA");
        System.out.println(block.toString());
        return SimpleBlockStateProvider.of(BlockRegistry.AZALEA_LOG);
    }

}
