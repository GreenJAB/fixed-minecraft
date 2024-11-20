package net.greenjab.fixedminecraft.registry

import net.greenjab.fixedminecraft.registry.block.CopperRailBlock
import net.greenjab.fixedminecraft.registry.block.NetheriteAnvilBlock
import net.greenjab.fixedminecraft.registry.block.OxidizableRailBlock
import net.greenjab.fixedminecraft.registry.entity.BrickEntity
import net.minecraft.block.Blocks
import net.minecraft.block.DispenserBlock
import net.minecraft.block.Oxidizable
import net.minecraft.block.dispenser.ProjectileDispenserBehavior
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.Registries.BLOCK
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.Util
import net.minecraft.util.math.Position
import net.minecraft.world.World

object BlockRegistry {
    val NETHERITE_ANVIL = block(Blocks.NETHERITE_BLOCK, ::NetheriteAnvilBlock) {
        strength(15.0F, 1200.0F)
        sounds(BlockSoundGroup.ANVIL)
        pistonBehavior(PistonBehavior.BLOCK)
    }
    val CHIPPED_NETHERITE_ANVIL = block(Blocks.NETHERITE_BLOCK, ::NetheriteAnvilBlock) {
        strength(15.0F, 1200.0F)
        sounds(BlockSoundGroup.ANVIL)
        pistonBehavior(PistonBehavior.BLOCK)
    }
    val DAMAGED_NETHERITE_ANVIL = block(Blocks.NETHERITE_BLOCK, ::NetheriteAnvilBlock) {
        strength(15.0F, 1200.0F)
        sounds(BlockSoundGroup.ANVIL)
        pistonBehavior(PistonBehavior.BLOCK)
    }
    @JvmField val COPPER_RAIL = block(Blocks.POWERED_RAIL, { OxidizableRailBlock(Oxidizable.OxidationLevel.UNAFFECTED, it) })
    @JvmField val EXPOSED_COPPER_RAIL = block(Blocks.POWERED_RAIL, { OxidizableRailBlock(Oxidizable.OxidationLevel.EXPOSED, it) })
    @JvmField val WEATHERED_COPPER_RAIL = block(Blocks.POWERED_RAIL, { OxidizableRailBlock(Oxidizable.OxidationLevel.WEATHERED, it) })
    @JvmField val OXIDIZED_COPPER_RAIL = block(Blocks.POWERED_RAIL, { OxidizableRailBlock(Oxidizable.OxidationLevel.OXIDIZED, it) })

    @JvmField val WAXED_COPPER_RAIL = block(Blocks.POWERED_RAIL, ::CopperRailBlock)
    @JvmField val WAXED_EXPOSED_COPPER_RAIL = block(Blocks.POWERED_RAIL, ::CopperRailBlock)
    @JvmField val WAXED_WEATHERED_COPPER_RAIL = block(Blocks.POWERED_RAIL, ::CopperRailBlock)
    @JvmField val WAXED_OXIDIZED_COPPER_RAIL = block(Blocks.POWERED_RAIL, ::CopperRailBlock)


    fun register() {
        BLOCK.register("netherite_anvil", NETHERITE_ANVIL)
        BLOCK.register("chipped_netherite_anvil", CHIPPED_NETHERITE_ANVIL)
        BLOCK.register("damaged_netherite_anvil", DAMAGED_NETHERITE_ANVIL)

        BLOCK.register("copper_rail", COPPER_RAIL)
        BLOCK.register("exposed_copper_rail", EXPOSED_COPPER_RAIL)
        BLOCK.register("weathered_copper_rail", WEATHERED_COPPER_RAIL)
        BLOCK.register("oxidized_copper_rail", OXIDIZED_COPPER_RAIL)
        BLOCK.register("waxed_copper_rail", WAXED_COPPER_RAIL)
        BLOCK.register("waxed_exposed_copper_rail", WAXED_EXPOSED_COPPER_RAIL)
        BLOCK.register("waxed_weathered_copper_rail", WAXED_WEATHERED_COPPER_RAIL)
        BLOCK.register("waxed_oxidized_copper_rail", WAXED_OXIDIZED_COPPER_RAIL)

        DispenserBlock.registerBehavior(Items.BRICK, object : ProjectileDispenserBehavior() {
            override fun createProjectile(world: World, position: Position, stack: ItemStack): ProjectileEntity {
                return Util.make(
                    BrickEntity(world, position.x, position.y, position.z)
                ) { entity: BrickEntity ->
                    entity.setItem(stack)
                } as ProjectileEntity
            }
        })
        DispenserBlock.registerBehavior(Items.NETHER_BRICK, object : ProjectileDispenserBehavior() {
            override fun createProjectile(world: World, position: Position, stack: ItemStack): ProjectileEntity {
                return Util.make(
                    BrickEntity(world, position.x, position.y, position.z)
                ) { entity: BrickEntity ->
                    entity.setItem(stack)
                } as ProjectileEntity
            }
        })
    }
}
