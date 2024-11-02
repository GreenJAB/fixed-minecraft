package net.greenjab.fixedminecraft.registry.screen

import net.minecraft.block.Blocks
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.RecipeInputInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket
import net.minecraft.potion.PotionUtil
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.Slot
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World


class FletchingScreenHandler @JvmOverloads constructor(
    syncId: Int, playerInventory: PlayerInventory,
    private val context: ScreenHandlerContext = ScreenHandlerContext.EMPTY
) :
    ScreenHandler(ScreenHandlerType.CRAFTING, syncId) {
    private val input: RecipeInputInventory = CraftingInventory(this, 3, 3)
    private val result = SimpleInventory(1)
    private val player: PlayerEntity = playerInventory.player
    fun Inv(): Inventory {
        val items = SimpleInventory(1)
        items.heldStacks[0] = ItemStack(Items.BLACK_STAINED_GLASS_PANE, 1).setCustomName(Text.of("."))
        return items
    }
    init {
        // this.addSlot(CraftingResultSlot(playerInventory.player, this.input, this.result, 0, 124, 35))
        this.addSlot(object : Slot(this.result, 0, 124, 35){
            override fun canInsert(stack: ItemStack?): Boolean {return false}
            override fun onTakeItem(player: PlayerEntity?, stack: ItemStack?) {
                this.onCrafted(stack)

                for (stack in input.heldStacks.indices) {

                    input.removeStack(stack, 1)
                    /*if (!stack.isEmpty) {
                        if (itemStack.isEmpty) {
                            input.setStack(i, itemStack2)
                        } else if (ItemStack.canCombine(itemStack, itemStack2)) {
                            itemStack2.increment(itemStack.count)
                            this.input.setStack(i, itemStack2)
                        } else if (!this.player.getInventory().insertStack(itemStack2)) {
                            this.player.dropItem(itemStack2, false)
                        }
                    }*/
                }
            }
        })

        this.addSlot(object : Slot(this.input, 0, 30+0 * 18, 17 + 0 * 18){
            override fun canInsert(stack: ItemStack?): Boolean {
                if (stack == null) return false
                return stack.isOf(Items.FLINT)
            }
        })
        this.addSlot(object : Slot(Inv(), 0, 30+1 * 18, 17 + 0 * 18){
            override fun canTakeItems(playerEntity: PlayerEntity?): Boolean {return false}})
        this.addSlot(object : Slot(Inv(), 0, 30+2 * 18, 17 + 0 * 18){
            override fun canTakeItems(playerEntity: PlayerEntity?): Boolean {return false}})

        this.addSlot(object : Slot(this.input, 3, 30+0 * 18, 17 + 1 * 18){
            override fun canInsert(stack: ItemStack?): Boolean {
                if (stack == null) return false
                return stack.isOf(Items.STICK)
            }
        })
        this.addSlot(object : Slot(Inv(), 0, 30+1 * 18, 17 + 1 * 18){
            override fun canTakeItems(playerEntity: PlayerEntity?): Boolean {return false}})

        this.addSlot(object : Slot(this.input, 5, 30+2 * 18, 17 + 1 * 18){
            override fun canInsert(stack: ItemStack?): Boolean {
                if (stack == null) return false
                return stack.isOf(Items.POTION) || stack.isOf(Items.GLOWSTONE)
            }
        })
        this.addSlot(object : Slot(this.input, 6, 30+0 * 18, 17 + 2 * 18){
            override fun canInsert(stack: ItemStack?): Boolean {
                if (stack == null) return false
                return stack.isOf(Items.FEATHER)
            }
        })

        this.addSlot(object : Slot(Inv(), 0, 30+1 * 18, 17 + 2 * 18){
            override fun canTakeItems(playerEntity: PlayerEntity?): Boolean {return false}})
        this.addSlot(object : Slot(Inv(), 0, 30+2 * 18, 17 + 2 * 18){
            override fun canTakeItems(playerEntity: PlayerEntity?): Boolean {return false}})

        var j: Int
        var i = 0
        while (i < 3) {
            j = 0
            while (j < 9) {
                this.addSlot(Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18))
                ++j
            }
            ++i
        }

        i = 0
        while (i < 9) {
            this.addSlot(Slot(playerInventory, i, 8 + i * 18, 142))
            ++i
        }
    }

    override fun onContentChanged(inventory: Inventory) {
        context.run { world: World, pos: BlockPos? ->
            updateResult(
                this,
                world,
                player,
                input,
                result
            )
        }
    }


    override fun onClosed(player: PlayerEntity) {
        super.onClosed(player)
        context.run { world: World?, pos: BlockPos? ->
            this.dropInventory(
                player,
                input
            )
        }
    }

    override fun canUse(player: PlayerEntity): Boolean {
        return canUse(this.context, player, Blocks.FLETCHING_TABLE)
    }

    override fun quickMove(player: PlayerEntity, slot: Int): ItemStack {
        var itemStack = ItemStack.EMPTY
        val slot2 = slots[slot]
        if (slot2 != null && slot2.hasStack()) {
            val itemStack2 = slot2.stack
            itemStack = itemStack2.copy()
            if (slot == 0) {
                context.run { world: World?, pos: BlockPos? ->
                    itemStack2.item.onCraftByPlayer(itemStack2, world, player)
                }
                if (!this.insertItem(itemStack2, 10, 46, true)) {
                    return ItemStack.EMPTY
                }

                slot2.onQuickTransfer(itemStack2, itemStack)
            } else if (slot >= 10 && slot < 46) {
                if (!this.insertItem(itemStack2, 1, 10, false)) {
                    if (slot < 37) {
                        if (!this.insertItem(itemStack2, 37, 46, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (!this.insertItem(itemStack2, 10, 37, false)) {
                        return ItemStack.EMPTY
                    }
                }
            } else if (!this.insertItem(itemStack2, 10, 46, false)) {
                return ItemStack.EMPTY
            }

            if (itemStack2.isEmpty) {
                slot2.stack = ItemStack.EMPTY
            } else {
                slot2.markDirty()
            }

            if (itemStack2.count == itemStack.count) {
                return ItemStack.EMPTY
            }

            slot2.onTakeItem(player, itemStack2)
            if (slot == 0) {
                player.dropItem(itemStack2, false)
            }
        }

        return itemStack
    }

    override fun canInsertIntoSlot(stack: ItemStack, slot: Slot): Boolean {
        return slot.inventory !== this.result && super.canInsertIntoSlot(stack, slot)
    }

    companion object {
        const val RESULT_ID: Int = 0
        private const val INPUT_START = 1
        private const val INPUT_END = 10
        private const val INVENTORY_START = 10
        private const val INVENTORY_END = 37
        private const val HOTBAR_START = 37
        private const val HOTBAR_END = 46
        protected fun updateResult(
            handler: ScreenHandler,
            world: World,
            player: PlayerEntity,
            craftingInventory: RecipeInputInventory,
            resultInventory: SimpleInventory
        ) {
            if (!world.isClient) {
                val serverPlayerEntity = player as ServerPlayerEntity
                var itemStack = ItemStack.EMPTY
                var b = !craftingInventory.getStack(0).isEmpty&&!craftingInventory.getStack(3).isEmpty&&!craftingInventory.getStack(6).isEmpty
                if (b) {
                    var extra = craftingInventory.getStack(5)
                    if (extra.isEmpty) itemStack = ItemStack(Items.ARROW, 8)
                    else if (extra.isOf(Items.GLOWSTONE)) itemStack = ItemStack(Items.SPECTRAL_ARROW, 8)
                    else {
                        val tippedArrow = ItemStack(Items.TIPPED_ARROW, 8)
                        PotionUtil.setPotion(tippedArrow, PotionUtil.getPotion(extra))
                        PotionUtil.setCustomPotionEffects(tippedArrow, PotionUtil.getCustomPotionEffects(extra))
                        itemStack = tippedArrow
                    }
                }

                resultInventory.setStack(0, itemStack)
                handler.setPreviousTrackedSlot(0, itemStack)
                serverPlayerEntity.networkHandler.sendPacket(
                    ScreenHandlerSlotUpdateS2CPacket(
                        handler.syncId,
                        handler.nextRevision(),
                        0,
                        itemStack
                    )
                )
            }
        }
    }
}
