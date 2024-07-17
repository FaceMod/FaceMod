package facemod.screens;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

import java.util.Objects;

public class BankScreen extends HandledScreen<ScreenHandler> {
    private static final int SLOT_SIZE = 18;
    private static final int ROWS = 2;
    private static final int COLUMNS = 3;
    private static final int GRID_WIDTH = COLUMNS * (SLOT_SIZE * 9 + 5) - 5;
    private static final int GRID_HEIGHT = ROWS * (SLOT_SIZE * 5 + 5) - 5;
    private static final int INVENTORY_ROWS = 4;
    private static final int INVENTORY_COLUMNS = 9;
    private static final int INVENTORY_WIDTH = INVENTORY_COLUMNS * SLOT_SIZE;
    private static final int INVENTORY_HEIGHT = INVENTORY_ROWS * SLOT_SIZE;
    ButtonWidget guildVaultButton = null;
    ButtonWidget personalVaultButton = null;


    public BankScreen(ScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        // Chest Grid
        int startX = (width - GRID_WIDTH) / 2;
        int startY = (height - GRID_HEIGHT) / 2;
        context.fillGradient(startX, startY, startX + GRID_WIDTH, startY + GRID_HEIGHT, 0xFF000000, 0xFF000000);

        // Player Inventory
        int inventoryStartX = (width - INVENTORY_WIDTH) / 2;
        int inventoryStartY = startY + GRID_HEIGHT + 5;
        context.fillGradient(inventoryStartX, inventoryStartY, inventoryStartX + INVENTORY_WIDTH, inventoryStartY + INVENTORY_HEIGHT, 0xFF000000, 0xFF000000);

        // Dark overlay for entire screen
        context.fillGradient(0, 0, width, height, 0x91000000, 0x91000000);

    }

    @Override
    protected void init() {
        System.out.println("Bank Screen");

        int startX = (width - GRID_WIDTH) / 2;
        int startY = (height - GRID_HEIGHT) / 2;

        int buttonY = startY + GRID_HEIGHT + 5;

        guildVaultButton = ButtonWidget.builder(Text.literal("PV"), button -> {
                    // Action for Guild Vault button
                })
                .dimensions(startX, buttonY, SLOT_SIZE, SLOT_SIZE)
                .tooltip(Tooltip.of(Text.literal("Personal Vault")))
                .build();

        personalVaultButton = ButtonWidget.builder(Text.literal("GV"), button -> {
                    // Action for Personal Vault button
                })
                .dimensions(startX + SLOT_SIZE, buttonY, SLOT_SIZE, SLOT_SIZE)
                .tooltip(Tooltip.of(Text.literal("Guild Vault")))
                .build();

        addDrawableChild(guildVaultButton);
        addDrawableChild(personalVaultButton);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        drawBackground(context,delta, mouseX,mouseY);

        int startX = (width - GRID_WIDTH) / 2;
        int startY = (height - GRID_HEIGHT) / 2;

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                drawChestGrid(context, startX + col * (SLOT_SIZE * 9 + 5), startY + row * (SLOT_SIZE * 5 + 5), mouseX, mouseY);
            }
        }


        int inventoryStartX = (width - INVENTORY_WIDTH) / 2;
        int inventoryStartY = startY + GRID_HEIGHT + 5;
        renderInventory(context, inventoryStartX, inventoryStartY, mouseX, mouseY);

        guildVaultButton.render(context, mouseX, mouseY, delta);
        personalVaultButton.render(context, mouseX, mouseY, delta);

    }

    private void drawChestGrid(DrawContext context, int startX, int startY, int mouseX, int mouseY) {
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 9; col++) {
                int x = startX + col * SLOT_SIZE;
                int y = startY + row * SLOT_SIZE;
                context.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, 0xFF000000);
                context.fill(x + 1, y + 1, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, 0xFF8B8B8B);

                if (mouseX >= x && mouseX <= x + SLOT_SIZE && mouseY >= y && mouseY <= y + SLOT_SIZE) {
                    context.fillGradient(x, y, x + SLOT_SIZE, y + SLOT_SIZE, 0x80FFFFFF, 0x80FFFFFF);
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


        for (int row = 0; row < INVENTORY_ROWS; row++) {
            for (int col = 0; col < INVENTORY_COLUMNS; col++) {
                int x = startX + col * SLOT_SIZE;
                int y = startY + row * SLOT_SIZE;

                context.fill(x, y, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, 0xFF000000);
                context.fill(x + 1, y + 1, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, 0xFF8B8B8B);

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
