package net.greenjab.fixedminecraft.mixin.map_book;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookIdCountsState;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookState;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookStateManager;
import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.component.type.MapPostProcessingComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.screen.CartographyTableScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.BiConsumer;

@Mixin(CartographyTableScreenHandler.class)
public class CartographyTableScreenHandlerMixin {

    @Shadow
    @Final
    private CraftingResultInventory resultInventory;

    @ModifyArg(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V",
               at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/CartographyTableScreenHandler;addSlot(Lnet/minecraft/screen/slot/Slot;)Lnet/minecraft/screen/slot/Slot;", ordinal = 1), index = 0)
    private Slot bookInSecondSlot(Slot par1){
        CartographyTableScreenHandler CTSH = (CartographyTableScreenHandler)(Object)this;
        return new Slot(CTSH.inventory, 1, 15, 52) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(Items.PAPER) || stack.isOf(Items.MAP) || stack.isOf(Items.GLASS_PANE) || stack.isOf(Items.BOOK);
            }
        };
    }

    @ModifyArg(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V",
               at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/CartographyTableScreenHandler;addSlot(Lnet/minecraft/screen/slot/Slot;)Lnet/minecraft/screen/slot/Slot;", ordinal = 2), index = 0)
    private Slot MapBookCraft(Slot par1, @Local(argsOnly = true) ScreenHandlerContext context){
        CartographyTableScreenHandler CTSH = (CartographyTableScreenHandler)(Object)this;
        return new Slot(this.resultInventory, 2, 145, 39) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                if (player instanceof ServerPlayerEntity) {
                    if (CTSH.slots.get(0).getStack().isOf(Items.FILLED_MAP)) {
                        if (CTSH.slots.get(1).getStack().isOf(Items.BOOK)) {
                            if (stack.isOf(ItemRegistry.MAP_BOOK)) {
                                int i = createMapBookState(stack, player.getServer());
                                MapBookState state = MapBookStateManager.INSTANCE.getMapBookState(player.getServer(), i);
                                if (state != null) {
                                    state.addMapID(CTSH.slots.get(0).getStack().get(DataComponentTypes.MAP_ID).id());
                                }
                            }
                        }
                    }
                }

                CTSH.slots.get(0).takeStack(1);
                CTSH.slots.get(1).takeStack(1);
                stack.getItem().onCraftByPlayer(stack, player);
                context.run( (world, pos) -> {
                    long l = world.getTime();
                    if (CTSH.lastTakeResultTime != l) {
                        world.playSound(null, pos, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        CTSH.lastTakeResultTime = l;
                    }
                });
                super.onTakeItem(player, stack);
            }
        };
    }

    @Redirect(method = "updateResult", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/screen/ScreenHandlerContext;run(Ljava/util/function/BiConsumer;)V"
    ))
    private void cartogrophyTableMapBook(ScreenHandlerContext instance, BiConsumer<World, BlockPos> function, @Local(argsOnly = true, ordinal = 0) ItemStack map,  @Local(argsOnly = true, ordinal = 1) ItemStack item,  @Local(argsOnly = true, ordinal = 2) ItemStack oldResult) {
        CartographyTableScreenHandler CTSH = (CartographyTableScreenHandler)(Object)this;
        instance.run(/* method_17382 */ (world, pos) -> {

            if (map.isOf(ItemRegistry.MAP_BOOK)) {
                if (item.isOf(Items.BOOK)) {
                    this.resultInventory.setStack(2, map.copyWithCount(2));
                    CTSH.sendContentUpdates();
                }
            } else {
                MapState mapState = FilledMapItem.getMapState(map, world);
                if (mapState != null) {
                    ItemStack itemStack4;
                    if (item.isOf(Items.PAPER) && !mapState.locked && mapState.scale < 4) {
                        itemStack4 = map.copyWithCount(1);
                        itemStack4.set(DataComponentTypes.MAP_POST_PROCESSING, MapPostProcessingComponent.SCALE);
                        CTSH.sendContentUpdates();
                    }
                    else if (item.isOf(Items.GLASS_PANE) && !mapState.locked) {
                        itemStack4 = map.copyWithCount(1);
                        itemStack4.set(DataComponentTypes.MAP_POST_PROCESSING, MapPostProcessingComponent.LOCK);
                        CTSH.sendContentUpdates();
                    }
                    else if (item.isOf(Items.BOOK) && !mapState.locked) {
                        itemStack4 = ItemRegistry.MAP_BOOK.getDefaultStack();
                        CTSH.sendContentUpdates();
                    }
                    else {
                        if (!item.isOf(Items.MAP)) {
                            this.resultInventory.removeStack(2);
                            CTSH.sendContentUpdates();
                            return;
                        }

                        itemStack4 = map.copyWithCount(2);
                        CTSH.sendContentUpdates();
                    }

                    if (!ItemStack.areEqual(itemStack4, oldResult)) {
                        this.resultInventory.setStack(2, itemStack4);
                        CTSH.sendContentUpdates();
                    }
                }
            }
        });
    }

    @Redirect(method = "quickMove",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z", ordinal = 0))
    private boolean bookQuickMove(ItemStack instance, Item item){
        return instance.isOf(Items.PAPER) || instance.isOf(Items.BOOK);
    }

    @Redirect(method = "quickMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;getStack()Lnet/minecraft/item/ItemStack;"))
    private ItemStack quickMapBookCraft(Slot instance, @Local(argsOnly = true) PlayerEntity player) {
        ItemStack stack = instance.getStack();
        CartographyTableScreenHandler CTSH = (CartographyTableScreenHandler)(Object)this;
        if (player instanceof ServerPlayerEntity) {
            if (CTSH.slots.get(0).getStack().isOf(Items.FILLED_MAP)) {
                if (CTSH.slots.get(1).getStack().isOf(Items.BOOK)) {
                    if (stack.isOf(ItemRegistry.MAP_BOOK)) {
                        if (Math.min(CTSH.slots.get(0).getStack().getCount(), CTSH.slots.get(1).getStack().getCount())==1) {
                            int i = createMapBookState(stack, player.getServer());
                            MapBookState state = MapBookStateManager.INSTANCE.getMapBookState(player.getServer(), i);
                            if (state != null) {
                                state.addMapID(CTSH.slots.get(0).getStack().get(DataComponentTypes.MAP_ID).id());
                            }
                        }else{
                            return ItemStack.EMPTY;
                        }
                    }
                }
            }
        }
        return stack;
    }

    @Unique
    private int allocateMapBookId(MinecraftServer server) {

        MapBookIdCountsState counts = server.getOverworld().getPersistentStateManager().getOrCreate(
                MapBookIdCountsState.persistentStateType
        );
        int i = counts.get();
        MapBookStateManager.INSTANCE.putMapBookState(server, i, new MapBookState());
        return i;
    }

    @Unique
    private int createMapBookState(ItemStack stack, MinecraftServer server) {
        int i = this.allocateMapBookId(server);
        stack.set(DataComponentTypes.MAP_ID, new MapIdComponent(i));
        return i;
    }
}
