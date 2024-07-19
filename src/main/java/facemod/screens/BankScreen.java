package facemod.screens;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class BankScreen extends HandledScreen<ScreenHandler> {
    private static final int SLOT_SIZE = 18;
    private static final int ROWS = 3;
    private static final int COLUMNS = 3;
    private static final int GRID_WIDTH = COLUMNS * (SLOT_SIZE * 9 + 5) - 5;
    private static final int GRID_HEIGHT = ROWS * (SLOT_SIZE * 5 + 5) - 5;
    private static final int INVENTORY_ROWS = 4;
    private static final int INVENTORY_COLUMNS = 9;
    private static final int INVENTORY_WIDTH = INVENTORY_COLUMNS * SLOT_SIZE;
    ButtonWidget guildVaultButton = null;
    ButtonWidget personalVaultButton = null;
    private boolean buttonPressed = false;
    public ClientPlayerInteractionManager interactionManager = null;


    public BankScreen(ScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        // Dark overlay for entire screen
        context.fillGradient(0, 0, width, height, 0x91000000, 0x91000000);
    }

    @Override
    protected void init() {
        interactionManager = Objects.requireNonNull(client).interactionManager;

        int startX = ((width - GRID_WIDTH) / 2) - 5;
        int startY = ((height - GRID_HEIGHT) / 2) - 75;

        int buttonY = startY + GRID_HEIGHT + 5;

        personalVaultButton = ButtonWidget.builder(Text.literal("PV"), button -> {
                    // Action for Personal Vault button
                    if (!buttonPressed) {
                        buttonPressed = true;
                        interactionManager.clickSlot(handler.syncId, 12, 0, SlotActionType.PICKUP, client.player);
                        personalVaultButton.active = false;
                    } else {
                        //TODO: Implement swapping logic
                    }
                })
                .dimensions(startX, buttonY, SLOT_SIZE, SLOT_SIZE)
                .tooltip(Tooltip.of(Text.literal("Personal Vault")))
                .build();

        guildVaultButton = ButtonWidget.builder(Text.literal("GV"), button -> {
                    // Action for Guild Vault button
                    if (!buttonPressed) {
                        buttonPressed = true;
                        interactionManager.clickSlot(handler.syncId, 16, 0, SlotActionType.PICKUP, client.player);
                        guildVaultButton.active = false;
                    } else {
                        //TODO: Implement swapping logic
                    }
                })
                .dimensions(startX + SLOT_SIZE, buttonY, SLOT_SIZE, SLOT_SIZE)
                .tooltip(Tooltip.of(Text.literal("Guild Vault")))
                .build();

        addDrawableChild(guildVaultButton);
        addDrawableChild(personalVaultButton);

        if(this.title != null && this.title.getString().contains("拽")){
            buttonPressed = true;
            personalVaultButton.active = false;
        }

        if(this.title != null && this.title.getString().contains("抭")) {
            buttonPressed = true;
            guildVaultButton.active = false;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        drawBackground(context,delta, mouseX,mouseY);

        int startX = (width - GRID_WIDTH) / 2;
        int startY = (height - GRID_HEIGHT) / 2;

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                drawChestGrid(context, startX + (col) * (SLOT_SIZE * 9 + 5)+2, (startY + (row) * (SLOT_SIZE * 5 + 5)+2)-100, mouseX, mouseY, col, row);
            }
        }

        int inventoryStartX = (width - INVENTORY_WIDTH) / 2;
        int inventoryStartY = startY + GRID_HEIGHT + 5;
        renderInventory(context, inventoryStartX+12, inventoryStartY-75, mouseX, mouseY);

        guildVaultButton.render(context, mouseX, mouseY, delta);
        personalVaultButton.render(context, mouseX, mouseY, delta);

        //super.render(context,mouseX,mouseY,delta);

    }

    private void drawChestGrid(DrawContext context, int startX, int startY, int mouseX, int mouseY, int offsetX, int offsetY) {
        Identifier INVENTORY_BACKGROUND = Identifier.of("minecraft", "textures/gui/container/generic_54.png");

        // Top + Grid
        context.drawTexture(INVENTORY_BACKGROUND, startX - 7 + (offsetX * 10), startY - 7 + (offsetY * 10), 0, 10, 256, 97);

        // Bottom
        context.drawTexture(INVENTORY_BACKGROUND, startX - 7 + (offsetX * 10), startY + 90 + (offsetY * 10), 0, 125, 256, 5);

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 9; col++) {
                int x = startX + col * SLOT_SIZE + (offsetX * 10);
                int y = startY + row * SLOT_SIZE + (offsetY * 10);
                context.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, 0x00000000);
                context.fill(x + 1, y + 1, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, 0x00000000);

                int index = col + row * 9;


                if (!this.title.getString().contains("拴")) {
                    if (index < handler.slots.size()) {
                        Slot slot = handler.slots.get(index);
                        int slotX = x + 1;
                        int slotY = y + 1;

                        context.drawItem(slot.getStack(), slotX, slotY);
                        context.drawItemInSlot(textRenderer, slot.getStack(), slotX, slotY);

                        if (mouseX >= slotX && mouseX <= slotX + SLOT_SIZE && mouseY >= slotY && mouseY <= slotY + SLOT_SIZE) {
                            context.fillGradient(x, y, x + SLOT_SIZE, y + SLOT_SIZE, 0x80FFFFFF, 0x80FFFFFF);
                            if (!slot.getStack().getItem().getName().getString().equals("Air")) {
                                context.drawItemTooltip(Objects.requireNonNull(this.client).textRenderer, slot.getStack(), mouseX, mouseY);
                            }
                        }
                    }
                } else {
                    MatrixStack stack = context.getMatrices();
                    stack.push();

                    int qmX = startX + 9 * SLOT_SIZE + (offsetX * 10) + SLOT_SIZE / 2;
                    int qmY = startY + 2 * SLOT_SIZE + (offsetY * 10) + SLOT_SIZE / 2;

                    float scale = 5.0f;
                    stack.scale(scale, scale, 1.0f);

                    stack.translate((qmX / scale) - 20.5, (qmY / scale)-4, 256);

                    context.drawText(textRenderer, "?", 0, 0, 0xFF000000, false);

                    stack.pop();
                    if (mouseX >= qmX - (50 * scale - 25) && mouseX <= qmX + (50 * scale - 100)/4 && mouseY >= qmY - (50 * scale - 100)/3 && mouseY <= qmY + (50 * scale - 100)/3) {
                        context.drawTooltip(textRenderer, Text.literal("Choose §6Personal §fVault or §aGuild §fVault"), mouseX, mouseY);
                    }
                }
            }
        }
    }

    private void renderInventory(DrawContext context, int startX, int startY, int mouseX, int mouseY) {
        PlayerInventory inventory = null;
        if (this.client != null && this.client.player != null) {
            inventory = this.client.player.getInventory();
        } else {
            System.err.println("inventory should not be null");
        }

        Identifier INVENTORY_BACKGROUND = Identifier.of("minecraft", "textures/gui/container/inventory.png");
        context.drawTexture(INVENTORY_BACKGROUND, startX-7, startY-3, 0,79,256,175);

        for (int row = 0; row < INVENTORY_ROWS; row++) {
            for (int col = 0; col < INVENTORY_COLUMNS; col++) {
                int x = startX + col * SLOT_SIZE;
                int y = startY + row * SLOT_SIZE + 1;

                if (row == INVENTORY_ROWS - 1) {
                    y += 4;
                }

                context.fill(x, y, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, 0x00000000);
                context.fill(x + 1, y + 1, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, 0x00000000);

                int index;
                if (row == INVENTORY_ROWS - 1) {
                    // hot bar
                    index = col;
                } else {
                    // inventory
                    index = col + (row + 1) * INVENTORY_COLUMNS;
                }

                if (index < Objects.requireNonNull(inventory).size()) {
                    int slotX = x + 1;
                    int slotY = y + 1;

                    context.drawItem(inventory.getStack(index), slotX, slotY);
                    context.drawItemInSlot(textRenderer, inventory.getStack(index), slotX, slotY);

                    if (mouseX >= slotX && mouseX <= slotX + SLOT_SIZE && mouseY >= slotY && mouseY <= slotY + SLOT_SIZE) {
                        context.fillGradient(x, y, x + SLOT_SIZE, y + SLOT_SIZE, 0x80FFFFFF, 0x80FFFFFF);
                        if (!inventory.getStack(index).getItem().getName().getString().equals("Air")) {
                            context.drawItemTooltip(this.client.textRenderer, inventory.getStack(index), mouseX, mouseY);
                        }
                    }
                }
            }
        }
    }

}
