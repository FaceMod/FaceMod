package io.github.facemod.bank.screens;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class BankScreen extends HandledScreen<ScreenHandler> {
    // Tooltip and Sync IDs
    private int tooltipItemIndex = -1;
    private static int syncId = -1;

    // Slot and Grid Dimensions
    private static final int SLOT_SIZE = 18;
    private static final int ROWS = 3;
    private static final int COLUMNS = 3;
    private static final int GRID_WIDTH = COLUMNS * (SLOT_SIZE * 9 + 5) - 5;
    private static final int GRID_HEIGHT = ROWS * (SLOT_SIZE * 5 + 5) - 5;

    // Inventory Dimensions
    private static final int INVENTORY_ROWS = 4;
    private static final int INVENTORY_COLUMNS = 9;
    private static final int INVENTORY_WIDTH = INVENTORY_COLUMNS * SLOT_SIZE;

    // Tab Management
    private static final int MAX_TABS = 9;
    private static boolean doTabSwitch = false;
    private int currentTab = 0;

    // Timing and Updates
    private static long lastUpdateTime = 0;
    private static final long UPDATE_INTERVAL = 60000; // 1000ms = 1s

    // Item Storage
    private static final Map<Integer, List<ItemStack>> allTabItems = new HashMap<>();
    private static ItemStack pickedUpItem = ItemStack.EMPTY;
    private static int pickedUpSlotIndex = -1;


    // UI Components
    ButtonWidget guildVaultButton = null;
    ButtonWidget personalVaultButton = null;

    // State Flags and Task Management
    private boolean buttonPressed = false;
    private final Queue<Runnable> taskQueue = new LinkedBlockingQueue<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static ScreenHandler handler = null;
    private boolean isProcessing = false;


    public ClientPlayerInteractionManager interactionManager = null;

    public BankScreen(ScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        BankScreen.syncId = handler.syncId;
        BankScreen.handler = handler;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        // Dark overlay for entire screen
        context.fillGradient(0, 0, width, height, 0x91000000, 0x91000000);
    }

    @Override
    protected void init() {
        doTabSwitch = true;

        interactionManager = Objects.requireNonNull(client).interactionManager;

        int startX = ((width - GRID_WIDTH) / 2) - 4;
        int startY = ((height - GRID_HEIGHT) / 2) - 75;

        int buttonY = startY + GRID_HEIGHT + 5;

        personalVaultButton = ButtonWidget.builder(Text.literal("PV"), button -> {
                    // Action for Personal Vault button
                    if (!buttonPressed) {
                        buttonPressed = true;
                        interactionManager.clickSlot(syncId, 12, 0, SlotActionType.PICKUP, client.player);
                        personalVaultButton.active = false;
                    } else {
                        //TODO: Implement swapping logic
                        System.out.println("Coming Soon");
                    }
                })
                .dimensions(startX, buttonY, SLOT_SIZE, SLOT_SIZE)
                .tooltip(Tooltip.of(Text.literal("Personal Vault")))
                .build();

        guildVaultButton = ButtonWidget.builder(Text.literal("GV"), button -> {
                    // Action for Guild Vault button
                    if (!buttonPressed) {
                        buttonPressed = true;
                        interactionManager.clickSlot(syncId, 16, 0, SlotActionType.PICKUP, client.player);
                        guildVaultButton.active = false;
                    } else {
                        //TODO: Implement swapping logic
                        System.out.println("Coming Soon");
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
        drawBackground(context, delta, mouseX, mouseY);

        //super.render(context,mouseX,mouseY,delta); // <-- Uncomment to view original inventory

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime >= UPDATE_INTERVAL) {
            if(this.title != null && !this.title.getString().contains("拴")) {
                updateTabs();
                lastUpdateTime = currentTime;
            }
        }

        int startX = (width - GRID_WIDTH) / 2;
        int startY = (height - GRID_HEIGHT) / 2;

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                drawChestGrid(context, startX + (col) * (SLOT_SIZE * 9 + 5) + 2, (startY + (row) * (SLOT_SIZE * 5 + 5) + 2) - 100, mouseX, mouseY, col, row);
            }
        }

        int inventoryStartX = (width - INVENTORY_WIDTH) / 2;
        int inventoryStartY = startY + GRID_HEIGHT + 5;
        renderInventory(context, inventoryStartX + 12, inventoryStartY - 75, mouseX, mouseY);

        guildVaultButton.render(context, mouseX, mouseY, delta);
        personalVaultButton.render(context, mouseX, mouseY, delta);
    }

    private void drawChestGrid(DrawContext context, int startX, int startY, int mouseX, int mouseY, int offsetX, int offsetY) {
        int tabIndex = offsetX + (offsetY * 3);
        List<ItemStack> items = allTabItems.get(tabIndex);

        if(items == null){
            items = Collections.emptyList();
        }

        Identifier INVENTORY_BACKGROUND = Identifier.of("minecraft", "textures/gui/container/generic_54.png");

        // Top + Grid
        context.drawTexture(
                id -> RenderLayer.getGui(),
                INVENTORY_BACKGROUND,
                startX - 7 + (offsetX * 10),
                startY - 7 + (offsetY * 10),
                0, 10, 256, 97,
                256, 256
        );

        // Bottom
        context.drawTexture(
                id -> RenderLayer.getGui(),
                INVENTORY_BACKGROUND,
                startX - 7 + (offsetX * 10),
                startY + 90 + (offsetY * 10),
                0, 10, 256, 97,
                256, 256
        );

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 9; col++) {
                int x = startX + col * SLOT_SIZE + (offsetX * 10);
                int y = startY + row * SLOT_SIZE + (offsetY * 10);
                context.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, 0x00000000);
                context.fill(x + 1, y + 1, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, 0x00000000);

                int index = col + row * 9;
                if(!this.title.getString().contains("拴")) {
                    if (index < items.size()) {
                        ItemStack stack = items.get(index);
                        context.drawItem(stack, x + 1, y + 1);
                        //context.drawItemInSlot(textRenderer, stack, x + 1, y + 1); <-- Method removed replacement nessecary

                        if (isHovered(mouseX,mouseY,x+1,y+1)) {
                            context.fillGradient(x, y, x + SLOT_SIZE, y + SLOT_SIZE, 0x80FFFFFF, 0x80FFFFFF);
                            if (!stack.getItem().getName().getString().equals("Air") && (tooltipItemIndex == -1 || index == tooltipItemIndex)) {
                                tooltipItemIndex = index;
                                context.drawItemTooltip(Objects.requireNonNull(this.client).textRenderer, stack, mouseX, mouseY);

                            } else if (stack.getItem().getName().getString().equals("Air") || index != tooltipItemIndex){
                                tooltipItemIndex = -1;
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
        context.drawTexture(
                id -> RenderLayer.getGui(),
                INVENTORY_BACKGROUND,
                startX - 7,
                startY - 3,
                0, 80,
                256, 85,
                256, 256
        );

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
                    //context.drawItemInSlot(textRenderer, inventory.getStack(index), slotX, slotY); //TODO: Find Alternative Method

                    if (isHovered(mouseX,mouseY,slotX,slotY)) {
                        context.fillGradient(x, y, x + SLOT_SIZE, y + SLOT_SIZE, 0x80FFFFFF, 0x80FFFFFF);
                        if (!inventory.getStack(index).getName().getString().equals("Air") && (tooltipItemIndex == -1 || index == tooltipItemIndex)) {
                            tooltipItemIndex = index;
                            context.drawItemTooltip(Objects.requireNonNull(this.client).textRenderer, inventory.getStack(index), mouseX, mouseY);

                        } else if (inventory.getStack(index).getName().getString().equals("Air") || index != tooltipItemIndex){
                            tooltipItemIndex = -1;
                        }
                    }
                }
            }
        }
    }

    private void updateTabs() {
        if (currentTab < MAX_TABS) {
            switchToTab(currentTab);
            currentTab++;
            updateTabs();
        } else {
            currentTab = 0;
        }
    }

    public boolean isHovered(int mouseX, int mouseY, int x, int y){
        return (mouseX >= x && mouseX <= x + SLOT_SIZE && mouseY >= y && mouseY <= y + SLOT_SIZE);
    }

    private void switchToTab(int tabIndex) {
        if (tabIndex >= 0 && tabIndex < MAX_TABS) {
            enqueueTask(() -> {
                    sendClickSlotPacket(tabIndex);

                    try {
                        Thread.sleep(100); // <-- Recommended is 100, anything below has a small chance of skipping less than 50 and will skip pages -Spade
                    } catch (InterruptedException e) {
                        System.err.println("sleep failed");
                    }

                    if (doTabSwitch) {
                        collectItemsFromCurrentTab(tabIndex);
                        doTabSwitch = false;
                    }
            });
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        System.out.println("mouseClicked");
        if (button == 0) {
            int startX = (width - GRID_WIDTH) / 2;
            int startY = (height - GRID_HEIGHT) / 2;
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLUMNS; col++) {
                    int x = startX + col * SLOT_SIZE;
                    int y = startY + row * SLOT_SIZE;
                    if (isHovered((int) mouseX, (int) mouseY, x, y)) {
                        handleSlotClick(row, col);
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void handleSlotClick(int row, int col) {
        int clickedSlotIndex = row * COLUMNS + col;
        ItemStack clickedItem = getItemStackFromSlotIndex(clickedSlotIndex);
        System.out.println(clickedItem);

        if (pickedUpItem.isEmpty()) {
            pickedUpItem = clickedItem;
            pickedUpSlotIndex = clickedSlotIndex;
            setItemStackInSlotIndex(clickedSlotIndex, ItemStack.EMPTY);
        } else {
            if (clickedItem.isEmpty()) {
                setItemStackInSlotIndex(clickedSlotIndex, pickedUpItem);
            } else {
                setItemStackInSlotIndex(clickedSlotIndex, pickedUpItem);
            }
            pickedUpItem = ItemStack.EMPTY;
            pickedUpSlotIndex = -1;
        }

        //TODO: Create client-server item sync
        //syncSlotChange(pickedUpSlotIndex, clickedSlotIndex);
    }

    private ItemStack getItemStackFromSlotIndex(int slotIndex) {
        int tab = slotIndex / (ROWS * COLUMNS);
        int slot = slotIndex % (ROWS * COLUMNS);

        List<ItemStack> items = allTabItems.get(tab);
        if (items != null && slot < items.size()) {
            return items.get(slot);
        }
        return ItemStack.EMPTY;
    }

    private void setItemStackInSlotIndex(int slotIndex, ItemStack itemStack) {
        int tab = slotIndex / (ROWS * COLUMNS);
        int slot = slotIndex % (ROWS * COLUMNS);

        List<ItemStack> items = allTabItems.get(tab);
        if (items != null && slot < items.size()) {
            items.set(slot, itemStack);
        }
    }

    private void syncSlotChange(int fromSlot, int toSlot) {
        if (client == null || client.getNetworkHandler() == null) {
            return;
        }

        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();

        Int2ObjectArrayMap<ItemStack> modifiedStacks = new Int2ObjectArrayMap<>();

        for (int i = 0; i < handler.slots.size(); i++) {
            modifiedStacks.put(i, handler.slots.get(i).getStack());
        }

        Packet<?> packet = new ClickSlotC2SPacket(
                syncId,
                0,
                fromSlot,
                0,
                SlotActionType.PICKUP,
                handler.slots.get(fromSlot).getStack(),
                modifiedStacks
        );
        networkHandler.sendPacket(packet);

        packet = new ClickSlotC2SPacket(
                syncId,
                0,
                toSlot,
                0,
                SlotActionType.PICKUP,
                handler.slots.get(toSlot).getStack(),
                modifiedStacks
        );
        networkHandler.sendPacket(packet);
    }




    private void sendClickSlotPacket(int slotIndex) {
        if (client == null || client.getNetworkHandler() == null) {
            return;
        }

        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();

        Int2ObjectArrayMap<ItemStack> modifiedStacks = new Int2ObjectArrayMap<>();

        for (int i = 0; i < handler.slots.size(); i++) {
            modifiedStacks.put(i, handler.slots.get(i).getStack());
        }

        Packet<?> packet = new ClickSlotC2SPacket(
                syncId,                // Synchronization ID
                0,                    // Revision value
                slotIndex,           // The index of the slot being interacted with
                0,                  // Button value (0 = LEFT_MOUSE, 1 = RIGHT_MOUSE)
                SlotActionType.PICKUP,        // Action type
                handler.slots.get(slotIndex).getStack(), // Item stack being interacted with
                modifiedStacks         // Map of all stacks on page.
        );
        networkHandler.sendPacket(packet);
    }

    private synchronized void enqueueTask(Runnable task) {
        taskQueue.add(task);
        if (!isProcessing) {
            isProcessing = true;
            processNextTask();
        }
    }

    private synchronized void processNextTask() {
        Runnable task = taskQueue.poll();
        if (task != null) {
            executorService.submit(() -> {
                try {
                    task.run();
                } finally {
                    processNextTask();
                }
            });
        } else {
            isProcessing = false;
        }
    }

    private void collectItemsFromCurrentTab(int tabIndex) {
        List<ItemStack> items = new ArrayList<>();

        for (int j = 9; j < handler.slots.size(); j++) {
            Slot slot = handler.slots.get(j);
            items.add(slot.getStack());
        }

        allTabItems.put(tabIndex, items);
    }


    @Override
    public void close() {
        super.close();
        executorService.shutdown();
        lastUpdateTime = 0;
        tooltipItemIndex = -1;
        allTabItems.clear();
    }
}
