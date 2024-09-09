package net.greenjab.fixedminecraft.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;


@SuppressWarnings("unchecked")
@Mixin(EnchantmentScreen.class)
public class EnchantmentScreenMixin {

    @ModifyVariable(method = "render", at = @At("STORE"), ordinal = 6)
    private int lapiscost(int m, @Local(ordinal = 4) int power) {
        //System.out.print(power);
        return (int)Math.ceil(power/10.0);
    }

    //TODO 102: (k < l + 1) > Need to make button activate when there is enough lapis for that button, not just 123

    /*@Redirect(method = "drawBackground", at = @At(value = "FIELD", target = "L", opcode = Opcodes.GETFIELD))
    private int injected(ClientPlayerEntity instance) {
        return 12345;
    }*/

    //TODO 117: LEVEL_TEXTURES[l] > make the 123 icons based on (0-0.5)(0.5-1)(1-) of enchant capacity, not just 123

    /*@Redirect(method = "drawBackground", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/ingame/EnchantmentScreen;LEVEL_TEXTURES:[Lnet/minecraft/util/Identifier;"))
    private Identifier[] injected(@Local(ordinal = 6)int power) {
        Identifier[] TEXTURES = new Identifier[]{new Identifier("container/enchanting_table/level_1"), new Identifier("container/enchanting_table/level_2"), new Identifier("container/enchanting_table/level_3")};
        EnchantmentScreen screen = (EnchantmentScreen)(Object)this;
        //((EnchantmentScreenHandler)screen.handler)
        return TEXTURES;
    }*/

    /*@Redirect(method = "drawBackground", slice = @Slice(from = @At(
            value = "FIELD", args = {
            "stringValue=phantom_membrane"}, ordinal = 0, target = ""
    )), at = @At(
            value = "NEW",target = "(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/Item;", ordinal = 0 ))
    private Item edibleMembrane(Item.Settings settings) {
        return new PhantomMembraneItem((new Item.Settings()).maxCount(64).food(FoodComponents.CHORUS_FRUIT));
    }//*/

}
