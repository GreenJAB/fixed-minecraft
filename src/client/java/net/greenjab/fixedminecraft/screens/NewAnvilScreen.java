package net.greenjab.fixedminecraft.screens;

import net.greenjab.fixedminecraft.FixedMinecraftClient;
import net.greenjab.fixedminecraft.FixedMinecraftEnchantmentHelper;
import net.greenjab.fixedminecraft.registry.other.NewAnvilMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.NonNull;

import java.awt.*;

public class NewAnvilScreen extends ItemCombinerScreen<NewAnvilMenu> {
    private static final Identifier TEXT_FIELD_SPRITE = Identifier.withDefaultNamespace("container/anvil/text_field");
    private static final Identifier TEXT_FIELD_DISABLED_SPRITE = Identifier.withDefaultNamespace("container/anvil/text_field_disabled");
    private static final Identifier ERROR_SPRITE = Identifier.withDefaultNamespace("container/anvil/error");
    private static final Identifier NETHERITE_ERROR_SPRITE = Identifier.withDefaultNamespace("container/anvil/netherite_error");
    private static final Identifier ANVIL_LOCATION = Identifier.withDefaultNamespace("textures/gui/container/anvil.png");
    private static final Identifier NETHERITE_ANVIL_LOCATION = Identifier.withDefaultNamespace("textures/gui/container/netherite_anvil.png");
    private EditBox name;
    private final Player player;

    public NewAnvilScreen(final NewAnvilMenu menu, final Inventory inventory, final Component title) {
        super(menu, inventory, title, ANVIL_LOCATION);
        this.player = inventory.player;
        this.titleLabelX = 60;
    }

    @Override
    protected void subInit() {
        int xo = (this.width - this.imageWidth) / 2;
        int yo = (this.height - this.imageHeight) / 2;
        this.name = new EditBox(this.font, xo + 62, yo + 22, 103, 12, Component.translatable("container.repair"));
        this.name.setCanLoseFocus(false);
        this.name.setTextColor(-1);
        this.name.setTextColorUneditable(-1);
        this.name.setInvertHighlightedTextColor(false);
        this.name.setBordered(false);
        this.name.setMaxLength(50);
        this.name.setResponder(this::onNameChanged);
        this.name.setValue("");
        this.addRenderableWidget(this.name);
        this.name.setEditable(this.menu.getSlot(0).hasItem());
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.minecraft.player.experienceDisplayStartTick = this.minecraft.player.tickCount;
    }

    @Override
    protected void setInitialFocus() {
        this.setInitialFocus(this.name);
    }

    @Override
    public void resize(final int width, final int height) {
        String oldEdit = this.name.getValue();
        this.init(width, height);
        this.name.setValue(oldEdit);
    }

    @Override
    public boolean keyPressed(final KeyEvent event) {
        if (event.isEscape()) {
            this.minecraft.player.closeContainer();
            return true;
        } else {
            return this.name.keyPressed(event) || this.name.canConsumeInput() || super.keyPressed(event);
        }
    }

    private void onNameChanged(final String name) {
        Slot slot = this.menu.getSlot(0);
        if (slot.hasItem()) {
            String newName = name;
            if (!slot.getItem().has(DataComponents.CUSTOM_NAME) && name.equals(slot.getItem().getHoverName().getString())) {
                newName = "";
            }

            if (this.menu.setItemName(newName)) {
                this.minecraft.player.connection.send(new ServerboundRenameItemPacket(newName));
            }
        }
    }

    private int barPos(int x, int isc) {return 60 + Math.min((int) ((168 - 60) * (x / (isc + 0.0f))), 168 - 60);}

    @Override
    protected void extractLabels(final @NonNull GuiGraphicsExtractor graphics, final int xm, final int ym) {
        super.extractLabels(graphics, xm, ym);
        int cost = this.menu.getCost();


        ItemStack ItemInput1 = ItemStack.EMPTY;
        ItemStack ItemOutput = ItemStack.EMPTY;
        if (this.menu.getSlot(0).hasItem()) {
            ItemInput1 = this.menu.getSlot(0).getItem();
        }
        if (this.menu.getSlot(2).hasItem()) {
            ItemOutput = this.menu.getSlot(2).getItem();
        }
        int capacity = this.menu.getCapacity();
        int InputCost;

        int OutputCost = cost;

        if (ItemInput1 != ItemStack.EMPTY && capacity>0) {
            InputCost = FixedMinecraftEnchantmentHelper.getOccupiedEnchantmentCapacity(ItemInput1, false);
            if (!this.menu.getSlot(1).hasItem()) {
                OutputCost = InputCost;
            }
            if (!ItemOutput.isEmpty()) {
                if (!ItemOutput.isEnchanted() && !ItemInput1.is(Items.ENCHANTED_BOOK)) OutputCost = InputCost;
                if (FixedMinecraftEnchantmentHelper.getOccupiedEnchantmentCapacity(ItemOutput, false) == 0) OutputCost = InputCost;
                if (NewAnvilMenu.AnvilMsg.byID(this.menu.getText()) == NewAnvilMenu.AnvilMsg.REPAIR) OutputCost = InputCost;
            }
            graphics.fill(60, 37, barPos(InputCost, capacity), 41, new Color(39, 174, 53).hashCode());
            if (InputCost > capacity) {
                if (this.menu.isNetherite()) {
                    graphics.fill(60, 37, barPos(InputCost - capacity, capacity), 41, new Color(0, 0, 255).hashCode());
                } else {
                    graphics.fill(60, 37, barPos(InputCost - capacity, capacity), 41, new Color(255, 0, 0).hashCode());
                }
            }
            if (OutputCost!=InputCost) {
                if (OutputCost > capacity) {
                    graphics.fill(barPos(InputCost, capacity), 37, 168, 41, new Color(0, 255, 0).hashCode());

                    if (this.menu.isNetherite()) {
                        graphics.fill(60, 37, barPos(OutputCost - capacity, capacity), 41, new Color(0, 0, 255).hashCode());
                        if (OutputCost < InputCost) {
                            graphics.fill(Math.max(barPos(OutputCost - capacity, capacity), 60), 37, barPos(InputCost - capacity, capacity), 41, new Color(205, 0, 0).hashCode());
                        }
                    } else {
                        graphics.fill(60, 37, barPos(OutputCost - capacity, capacity), 41, new Color(255, 0, 0).hashCode());
                    }
                } else {
                    if (OutputCost > InputCost) {
                        graphics.fill(barPos(InputCost, capacity), 37, barPos(OutputCost, capacity), 41, new Color(0, 255, 0).hashCode());
                    } else {
                        graphics.fill(60, 37, barPos(InputCost, capacity), 41, new Color(39, 174, 53).hashCode());
                        if (FixedMinecraftEnchantmentHelper.getOccupiedEnchantmentCapacity(ItemOutput, false) == 0) OutputCost = 0;
                        graphics.fill(Math.max(barPos(OutputCost, capacity), 60), 37, barPos(InputCost, capacity), 41, new Color(205, 0, 0).hashCode());
                    }
                }
            }
        }

        for (int i = 5; i < capacity; i+=5) {
            graphics.fill(barPos(i, capacity) - 1, 38, barPos(i, capacity), 40, new Color(255, 255, 255).hashCode());
        }


        if (this.menu.getText() > 0) {
            int color = -40864;
            NewAnvilMenu.AnvilMsg msg = NewAnvilMenu.AnvilMsg.byID(this.menu.getText());
            Component line = msg.includeCost?Component.translatable("container.anvil."+msg.lang, cost):
                    Component.translatable("container.anvil."+msg.lang);
            if (this.menu.getSlot(2).hasItem()&&this.menu.getSlot(2).mayPickup(this.player)) {
                color = -8323296;
            }

            int tx = this.imageWidth - 8 - this.font.width(line) - 2;
            int ty = 69;
            if (FixedMinecraftClient.usingCustomContainers())ty-=2;
            graphics.fill(tx - 2, ty-2, this.imageWidth - 8, ty+10, 1325400064);
            graphics.text(this.font, line, tx, ty, color);
        }
    }

    @Override
    public void extractBackground(final @NonNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(RenderPipelines.GUI_TEXTURED, menu.isNetherite()?NETHERITE_ANVIL_LOCATION:ANVIL_LOCATION, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
        this.extractErrorIcon(graphics, this.leftPos, this.topPos);
        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED, this.menu.getSlot(0).hasItem() ? TEXT_FIELD_SPRITE : TEXT_FIELD_DISABLED_SPRITE, this.leftPos + 59, this.topPos + 18, 110, 16
        );
    }

    @Override
    protected void extractErrorIcon(final @NonNull GuiGraphicsExtractor graphics, final int xo, final int yo) {
        if ((this.menu.getSlot(0).hasItem() || this.menu.getSlot(1).hasItem()) && !this.menu.getSlot(this.menu.getResultSlot()).hasItem()) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, menu.isNetherite()?NETHERITE_ERROR_SPRITE:ERROR_SPRITE, xo + 99, yo + 45, 28, 21);
        }
    }

    @Override
    public void slotChanged(final @NonNull AbstractContainerMenu container, final int slotIndex, final @NonNull ItemStack itemStack) {
        if (slotIndex == 0) {
            this.name.setValue(itemStack.isEmpty() ? "" : itemStack.getHoverName().getString());
            this.name.setEditable(!itemStack.isEmpty());
            this.setFocused(this.name);
        }
    }
}
