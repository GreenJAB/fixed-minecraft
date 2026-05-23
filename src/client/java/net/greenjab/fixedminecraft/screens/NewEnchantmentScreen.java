package net.greenjab.fixedminecraft.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.greenjab.fixedminecraft.registry.other.NewEnchantmentMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EnchantmentNames;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class NewEnchantmentScreen extends AbstractContainerScreen< NewEnchantmentMenu> {
    private static final Identifier[] ENABLED_LEVEL_SPRITES = new Identifier[]{
            Identifier.withDefaultNamespace("container/enchanting_table/level_1"),
            Identifier.withDefaultNamespace("container/enchanting_table/level_2"),
            Identifier.withDefaultNamespace("container/enchanting_table/level_3")
    };
    private static final Identifier[] DISABLED_LEVEL_SPRITES = new Identifier[]{
            Identifier.withDefaultNamespace("container/enchanting_table/level_1_disabled"),
            Identifier.withDefaultNamespace("container/enchanting_table/level_2_disabled"),
            Identifier.withDefaultNamespace("container/enchanting_table/level_3_disabled")
    };
    private static final Identifier ENCHANTMENT_SLOT_DISABLED_SPRITE = Identifier.withDefaultNamespace("container/enchanting_table/enchantment_slot_disabled");
    private static final Identifier ENCHANTMENT_SLOT_HIGHLIGHTED_SPRITE = Identifier.withDefaultNamespace(
            "container/enchanting_table/enchantment_slot_highlighted"
    );
    private static final Identifier ENCHANTMENT_SLOT_SPRITE = Identifier.withDefaultNamespace("container/enchanting_table/enchantment_slot");
    private static final Identifier ENCHANTING_TABLE_LOCATION = Identifier.withDefaultNamespace("textures/gui/container/enchanting_table.png");
    private static final Identifier ENCHANTING_BOOK_LOCATION = Identifier.withDefaultNamespace("textures/entity/enchantment/enchanting_table_book.png");
    private final RandomSource random = RandomSource.create();
    private BookModel bookModel;
    public float flip;
    public float oFlip;
    public float flipT;
    public float flipA;
    public float open;
    public float oOpen;
    private ItemStack last = ItemStack.EMPTY;

    public  NewEnchantmentScreen(final  NewEnchantmentMenu menu, final Inventory inventory, final Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.bookModel = new BookModel(this.minecraft.getEntityModels().bakeLayer(ModelLayers.BOOK));
    }

    @Override
    public void containerTick() {
        super.containerTick();
        assert this.minecraft.player != null;
        this.minecraft.player.experienceDisplayStartTick = this.minecraft.player.tickCount;
        this.tickBook();
    }

    @Override
    public boolean mouseClicked(final @NonNull MouseButtonEvent event, final boolean doubleClick) {
        int xo = (this.width - this.imageWidth) / 2;
        int yo = (this.height - this.imageHeight) / 2;

        for (int i = 0; i < 3; i++) {
            double xx = event.x() - (xo + 60);
            double yy = event.y() - (yo + 16 + 18 * i);
            if (xx >= 0.0 && yy >= 0.0 && xx < 108.0 && yy < 18.0) {
                assert this.minecraft.player != null;
                if (this.menu.clickMenuButton(this.minecraft.player, i)) {
                    assert this.minecraft.gameMode != null;
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, i);
                    return true;
                }
            }
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public void extractBackground(final @NonNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        int xo = (this.width - this.imageWidth) / 2;
        int yo = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, ENCHANTING_TABLE_LOCATION, xo, yo, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
        this.extractBook(graphics, xo, yo);
        EnchantmentNames.getInstance().initSeed(this.menu.getEnchantmentSeed());
        int lapisCount = this.menu.getLapisCount();

        for (int i = 0; i < 3; i++) {
            int leftPos = xo + 60;
            int leftPosText = leftPos + 20;
            int cost = this.menu.costs[i];
            if (cost == 0) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_SLOT_DISABLED_SPRITE, leftPos, yo + 16 + 18 * i, 108, 18);
            } else {
                String costText = cost + "";
                int textWidth = 70 - this.font.width(costText);
                FormattedText message = EnchantmentNames.getInstance().getRandomName(this.font, textWidth);
                int col = -9937334;
                if ((lapisCount < Mth.ceil(cost / 10.0) || this.minecraft.player.experienceLevel < cost) && !this.minecraft.player.hasInfiniteMaterials()) {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_SLOT_DISABLED_SPRITE, leftPos, yo + 16 + 18 * i, 108, 18);
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, DISABLED_LEVEL_SPRITES[Math.clamp(this.menu.icon[i], 0, 2)], leftPos + 1, yo + 17 + 18 * i, 16, 16);
                    graphics.textWithWordWrap(this.font, message, leftPosText, yo + 18 + 18 * i, textWidth, ARGB.opaque((col & 16711422) >> 1), false);
                    col = -12550384;
                } else {
                    int xx = mouseX - (xo + 60);
                    int yy = mouseY - (yo + 16 + 18 * i);
                    if (xx >= 0 && yy >= 0 && xx < 108 && yy < 18) {
                        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_SLOT_HIGHLIGHTED_SPRITE, leftPos, yo + 16 + 18 * i, 108, 18);
                        graphics.requestCursor(CursorTypes.POINTING_HAND);
                        col = -128;
                    } else {
                        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_SLOT_SPRITE, leftPos, yo + 16 + 18 * i, 108, 18);
                    }

                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ENABLED_LEVEL_SPRITES[Math.clamp(this.menu.icon[i], 0, 2)], leftPos + 1, yo + 17 + 18 * i, 16, 16);
                    graphics.textWithWordWrap(this.font, message, leftPosText, yo + 18 + 18 * i, textWidth, col, false);
                    col = -8323296;
                }

                graphics.text(this.font, costText, leftPosText + 70 - this.font.width(costText), yo + 18 + 18 * i + 7, col);
            }
        }
    }

    private void extractBook(final GuiGraphicsExtractor graphics, final int left, final int top) {
        float a = this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        float open = Mth.lerp(a, this.oOpen, this.open);
        float flip = Mth.lerp(a, this.oFlip, this.flip);
        int x0 = left + 14;
        int y0 = top + 14;
        int x1 = x0 + 38;
        int y1 = y0 + 31;
        graphics.book(this.bookModel, ENCHANTING_BOOK_LOCATION, 40.0F, open, flip, x0, y0, x1, y1);
    }

    @Override
    public void extractRenderState(final @NonNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float ignored) {
        float a = this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        super.extractRenderState(graphics, mouseX, mouseY, a);
        assert this.minecraft.player != null;
        boolean infiniteMaterials = this.minecraft.player.hasInfiniteMaterials();
        int lapis = this.menu.getLapisCount();

        for (int i = 0; i < 3; i++) {
            int minLevel = this.menu.costs[i];
            if (minLevel>0) {
                int enchantLevel = this.menu.icon[i];
                int cost = Mth.ceil(minLevel / 10.0);
                if (this.isHovering(60, 16 + 18 * i, 108, 17, mouseX, mouseY) && enchantLevel >= 0) {
                    List<Component> texts = Lists.newArrayList();

                    /*ItemEnchantments enchantments = this.menu.getSlot(i+2).getItem().getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
                    for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
                        Holder<Enchantment> enchantment = entry.getKey();
                        texts.add(
                                Component.translatable("container.enchant.clue", Enchantment.getFullname(enchantment, enchantments.getLevel(enchantment)))
                                        .withStyle(ChatFormatting.WHITE)
                        );
                        break;
                    }*/

                    if (!infiniteMaterials) {
                        //texts.add(CommonComponents.EMPTY);
                        if (this.minecraft.player.experienceLevel < minLevel) {
                            texts.add(Component.translatable("container.enchant.level.requirement", this.menu.costs[i]).withStyle(ChatFormatting.RED));
                        } else {
                            MutableComponent lapisCost;
                            if (cost == 1) lapisCost = Component.translatable("container.enchant.lapis.one");
                            else lapisCost = Component.translatable("container.enchant.lapis.many", cost);
                            texts.add(lapisCost.withStyle(lapis >= cost ? ChatFormatting.GRAY : ChatFormatting.RED));

                            MutableComponent levelCost;
                            if (cost == 1) levelCost = Component.translatable("container.enchant.level.one");
                            else levelCost = Component.translatable("container.enchant.level.many", cost);
                            texts.add(levelCost.withStyle(ChatFormatting.GRAY));
                        }
                    }

                    graphics.setComponentTooltipForNextFrame(this.font, texts, mouseX, mouseY);
                    break;
                }
            }
        }
    }

    public void tickBook() {
        ItemStack current = this.menu.getSlot(0).getItem();
        if (!ItemStack.matches(current, this.last)) {
            this.last = current;

            do {
                this.flipT = this.flipT + (this.random.nextInt(4) - this.random.nextInt(4));
            } while (this.flip <= this.flipT + 1.0F && this.flip >= this.flipT - 1.0F);
        }

        this.oFlip = this.flip;
        this.oOpen = this.open;
        boolean shouldBeOpen = false;

        for (int i = 0; i < 3; i++) {
            if (this.menu.costs[i] != 0) {
                shouldBeOpen = true;
                break;
            }
        }

        if (shouldBeOpen) {
            this.open += 0.2F;
        } else {
            this.open -= 0.2F;
        }

        this.open = Mth.clamp(this.open, 0.0F, 1.0F);
        float diff = (this.flipT - this.flip) * 0.4F;
        float max = 0.2F;
        diff = Mth.clamp(diff, -max, max);
        this.flipA = this.flipA + (diff - this.flipA) * 0.9F;
        this.flip = this.flip + this.flipA;
    }
}
