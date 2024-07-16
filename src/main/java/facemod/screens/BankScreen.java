package facemod.screens;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class BankScreen extends Screen {
    private static final int SLOT_SIZE = 18;
    private static final int ROWS = 2; // 2 rows of chest grids
    private static final int COLUMNS = 3; // 3 columns of chest grids
    private static final int GRID_WIDTH = COLUMNS * (SLOT_SIZE * 9 + 5) - 5; // Total width of grids including spacing
    private static final int GRID_HEIGHT = ROWS * (SLOT_SIZE * 5 + 5) - 5; // Total height of grids including spacing

    public BankScreen() {
        super(Text.literal("Bank"));
    }

    @Override
    protected void init() {
        System.out.println("Bank Screen");
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        // Calculate the starting position to center the grids
        int startX = (width - GRID_WIDTH) / 2;
        int startY = (height - GRID_HEIGHT) / 2;

        // Draw the chest grids
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                drawChestGrid(context, startX + col * (SLOT_SIZE * 9 + 5), startY + row * (SLOT_SIZE * 5 + 5));
            }
        }
    }

    private void drawChestGrid(DrawContext context, int startX, int startY) {
        for (int row = 0; row < 5; row++) { // Each chest grid is 5 rows
            for (int col = 0; col < 9; col++) { // Each chest grid is 9 columns
                int x = startX + col * SLOT_SIZE;
                int y = startY + row * SLOT_SIZE;
                context.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, 0xFF000000);
                context.fill(x + 1, y + 1, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, 0xFF8B8B8B);
            }
        }
    }
}
