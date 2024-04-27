package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

@Mixin(EnchantmentScreenHandler.class)
public abstract class EnchantmentScreenHandlerMixin {

    @Shadow
    protected abstract List<EnchantmentLevelEntry> generateEnchantments(ItemStack stack, int slot, int level);

    @Shadow
    @Final
    public int[] enchantmentId;

    @Shadow
    @Final
    private Random random;

    @Shadow
    @Final
    public int[] enchantmentLevel;

    @Shadow
    @Final
    public int[] enchantmentPower;

    @Inject(method = "generateEnchantments", at = @At("RETURN"), cancellable = true)
    private void onlyGiveOneEnchantment(ItemStack stack, int slot, int level, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        if (cir.getReturnValue().isEmpty()) return;
        cir.setReturnValue(List.of(cir.getReturnValue().get(0)));
    }

    @ModifyArg(method = "onContentChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandlerContext;run(Ljava/util/function/BiConsumer;)V"))
    private BiConsumer<World, BlockPos> generateEntries(BiConsumer<World, BlockPos> function, @Local ItemStack itemStack) {
        return ((world, blockPos) -> {
            int bookShelfCount = 0;
            for(BlockPos p : EnchantingTableBlock.POWER_PROVIDER_OFFSETS) {
                if (EnchantingTableBlock.canAccessPowerProvider(world, blockPos, p)) {
                    bookShelfCount++;
                }
            }
            bookShelfCount = Math.max(bookShelfCount, 15);

            for (int i=0; i<3; i++) {
                List<EnchantmentLevelEntry> enchantments = this.generateEnchantments(itemStack, i, FixedMinecraftEnchantmentHelper.POWER_WHEN_MAX_LEVEL * (bookShelfCount/15));
                EnchantmentLevelEntry enchantmentLevelEntry = enchantments.get(this.random.nextInt(enchantments.size()));
                this.enchantmentId[i] = Registries.ENCHANTMENT.getRawId(enchantmentLevelEntry.enchantment); // the one that's being displayed
                this.enchantmentLevel[i] = enchantmentLevelEntry.level;

                // calculate EnchantmentPower
                int enchantmentPower = 0;
                for (EnchantmentLevelEntry entry : enchantments) {
                    enchantmentPower += FixedMinecraftEnchantmentHelper.getEnchantmentPower(entry.enchantment, entry.level);
                }

                this.enchantmentPower[i] = enchantmentPower;
            }

            System.out.println("Custom generated Entries: ");
            System.out.println("enchantmentIds =" + Arrays.toString(this.enchantmentId));
            System.out.println("enchantmentLevels =" + Arrays.toString(this.enchantmentLevel));
            System.out.println("enchantmentPowers =" + Arrays.toString(this.enchantmentPower));
            System.out.println("----------");
            // for (int i=0; i<3; i++) {
            //     System.out.println("enchantmentId=" + this.enchantmentId[i]);
            //     System.out.println("enchantmentLevel=" + this.enchantmentLevel[i]);
            //     System.out.println("enchantmentPower=" + this.enchantmentPower[i]);
            // }

            ((EnchantmentScreenHandler) (Object) this).sendContentUpdates();
        });
    }

    @ModifyVariable(method = "onButtonClick", at = @At("STORE"), ordinal = 1)
    // both lines do the same :)
    // private int setCost(int cost, PlayerEntity playerEntity, int slotId) {
    private int setCost(int cost, @Local(argsOnly = true) int slotId) {
        System.out.println("modify variable - cost: " + cost + " slotId: " + slotId);
        return this.enchantmentPower[slotId];
    }
}
