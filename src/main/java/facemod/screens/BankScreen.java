package facemod.screens;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class BankScreen extends Screen {
    private static final int SLOT_SIZE = 18;
    private static final int ROWS = 5;
    private static final int COLUMNS = 9;

    public BankScreen() {
        super(Text.literal("Bank"));
    }

    @Override
    protected void init() {

        ButtonWidget button1 = ButtonWidget.builder(Text.literal("Button 1"), button -> System.out.println("You clicked button1!"))
                .dimensions(width / 2 - 205, 20, 200, 20)
                .tooltip(Tooltip.of(Text.literal("Tooltip of button1")))
                .build();
        ButtonWidget button2 = ButtonWidget.builder(Text.literal("Button 2"), button -> System.out.println("You clicked button2!"))
                .dimensions(width / 2 + 5, 20, 200, 20)
                .tooltip(Tooltip.of(Text.literal("Tooltip of button2")))
                .build();

        addDrawableChild(button1);
        addDrawableChild(button2);

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawChestGrid(context, width / 2 - (COLUMNS * SLOT_SIZE) / 2, height / 2 - (ROWS * SLOT_SIZE) / 2);
    }

    private void drawChestGrid(DrawContext context, int startX, int startY) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                int x = startX + col * SLOT_SIZE;
                int y = startY + row * SLOT_SIZE;
                context.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, 0xFF000000);
                context.fill(x + 1, y + 1, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, 0xFF8B8B8B);
            }
        }
    }
}

