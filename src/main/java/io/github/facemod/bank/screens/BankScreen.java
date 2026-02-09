package io.github.facemod.bank.screens;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.sync.ComponentChangesHash;
import net.minecraft.screen.sync.ItemStackHash;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;

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

    // Locked Vaults
    private static final Set<Integer> lockedTabs = new HashSet<>();

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

    // Pick Items
    private static ItemStack cursorStack = ItemStack.EMPTY;
    private static boolean isDraggingItem = false;
    private static int cursorItemSourceTab = -1;

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

        if (!cursorStack.isEmpty() && isDraggingItem) {
            context.drawItem(cursorStack, mouseX - 8, mouseY - 8);
            context.drawStackOverlay(textRenderer, cursorStack, mouseX - 8, mouseY - 8);
        }
    }

    private void drawChestGrid(DrawContext context, int startX, int startY, int mouseX, int mouseY, int offsetX, int offsetY) {
        int tabIndex = offsetX + (offsetY * 3);
        List<ItemStack> items = allTabItems.get(tabIndex);

        if(items == null){
            items = Collections.emptyList();
        }Identifier INVENTORY_BACKGROUND = Identifier.of("minecraft", "textures/gui/container/generic_54.png");

        // Top + Grid
        context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                INVENTORY_BACKGROUND,
                startX - 7 + (offsetX * 10),
                startY - 7 + (offsetY * 10),
                0f, 12f, 256, 97,
                256, 256
        );

        // Check if this tab is locked
        boolean isTabLocked = lockedTabs.contains(tabIndex);

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
                        context.drawStackOverlay(textRenderer, stack, x + 1, y + 1);

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
                    Matrix3x2fStack stack = context.getMatrices();
                    stack.pushMatrix();

                    int qmX = startX + 9 * SLOT_SIZE + (offsetX * 10) + SLOT_SIZE / 2;
                    int qmY = startY + 2 * SLOT_SIZE + (offsetY * 10) + SLOT_SIZE / 2;

                    float scale = 5.0f;
                    stack.mul(new Matrix3x2f().scale(scale, scale));

                    stack.mul(new Matrix3x2f().translate((qmX / scale) - 20.5f, (qmY / scale) - 4f));

                    context.drawText(textRenderer, "?", 0, 0, 0xFFFFFFFF, false);

                    stack.popMatrix();if (mouseX >= qmX - (50 * scale - 25) && mouseX <= qmX + (50 * scale - 100)/4 && mouseY >= qmY - (50 * scale - 100)/3 && mouseY <= qmY + (50 * scale - 100)/3) {
                        context.drawTooltip(textRenderer, Text.literal("Choose §6Personal §fVault or §aGuild §fVault"), mouseX, mouseY);
                    }
                }
            }
        }

        // Draw lock icon if tab is locked
        if (isTabLocked) {
            Matrix3x2fStack stack = context.getMatrices();
            stack.pushMatrix();

            int lockX = startX + 9 * SLOT_SIZE + (offsetX * 10) + SLOT_SIZE / 2;
            int lockY = startY + 2 * SLOT_SIZE + (offsetY * 10) + SLOT_SIZE / 2;

            float scale = 5.0f;
            stack.mul(new Matrix3x2f().scale(scale, scale));
            stack.mul(new Matrix3x2f().translate((lockX / scale) - 20.5f, (lockY / scale) - 4f));

            context.drawText(textRenderer, "🔒", 0, 0, 0xFFFFFFFF, false);

            stack.popMatrix();
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
                RenderPipelines.GUI_TEXTURED,
                INVENTORY_BACKGROUND, // Texture
                startX - 7,
                startY - 3,
                0, 80, // UVs are cropping the image starting from the top left, in this case we start 80 pixels down
                256, 87,// Width and height should be pretty standard, but if not well google it.
                256, 256// The texture dimensions
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
                    context.drawStackOverlay(textRenderer, inventory.getStack(index), slotX, slotY);

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
        currentTab = 0;
        doTabSwitch = true;
        processNextTab();
    }

    private void processNextTab() {
        if (currentTab >= MAX_TABS) {
            currentTab = 0;
            return;
        }

        final int tab = currentTab++;
        enqueueTask(() -> {
            switchToTab(tab);
            try {
                Thread.sleep(100); // This delay still happens in the executor
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            processNextTab(); // Recursively continue after this task finishes
        });
    }

    public boolean isHovered(int mouseX, int mouseY, int x, int y){
        return (mouseX >= x && mouseX <= x + SLOT_SIZE && mouseY >= y && mouseY <= y + SLOT_SIZE);
    }

    private void switchToTab(int tabIndex) {
        if (tabIndex >= 0 && tabIndex < MAX_TABS) {
            if (lockedTabs.contains(tabIndex)) {
                System.out.println("Skipping locked tab " + tabIndex);
                return;
            }

            // Check if the tab slot itself indicates it's locked
            if (tabIndex < handler.slots.size()) {
                Slot tabSlot = handler.slots.get(tabIndex);
                ItemStack tabItem = tabSlot.getStack();

                if (!tabItem.isEmpty()) {
                    Identifier model = tabItem.get(DataComponentTypes.ITEM_MODEL);
                    if (LOCKED_VAULT_MODEL.equals(model)) {
                        lockedTabs.add(tabIndex);
                        System.out.println("Tab " + tabIndex + " is locked (detected from tab slot)");
                        return;
                    }
                }
            }

            assert MinecraftClient.getInstance().player != null;
            int ping = Objects.requireNonNull(Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).getPlayerListEntry(
                    MinecraftClient.getInstance().player.getUuid()
            )).getLatency();

            enqueueTask(() -> {
                sendClickSlotPacket(tabIndex);

                try {
                    int delay = Math.max(150 + (ping * 2), 200);
                    Thread.sleep(delay);
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
        // Check if clicking on a tab while holding an item
        if (!cursorStack.isEmpty() && button == 0) {
            int tabClicked = getTabAtPosition((int) mouseX, (int) mouseY);
            if (tabClicked != -1) {
                switchToTabWithItem(tabClicked);
                return true;
            }
        }

        // Handle slot clicks in the bank grid
        if (button == 0 || button == 1) {
            int startX = (width - GRID_WIDTH) / 2;
            int startY = (height - GRID_HEIGHT) / 2;

            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLUMNS; col++) {
                    int gridX = startX + (col) * (SLOT_SIZE * 9 + 5) + 2;
                    int gridY = (startY + (row) * (SLOT_SIZE * 5 + 5) + 2) - 100;

                    for (int slotRow = 0; slotRow < 5; slotRow++) {
                        for (int slotCol = 0; slotCol < 9; slotCol++) {
                            int x = gridX + slotCol * SLOT_SIZE + (col * 10);
                            int y = gridY + slotRow * SLOT_SIZE + (row * 10);

                            if (isHovered((int) mouseX, (int) mouseY, x + 1, y + 1)) {
                                int tabIndex = col + (row * 3);
                                int slotIndex = slotCol + slotRow * 9;
                                handleSlotClick(tabIndex, slotIndex, button == 1);
                                return true;
                            }
                        }
                    }
                }
            }
            // Handle inventory clicks
            int inventoryStartX = (width - INVENTORY_WIDTH) / 2 + 12;
            int inventoryStartY = startY + GRID_HEIGHT + 5 - 75;

            if (handleInventoryClick((int) mouseX, (int) mouseY, inventoryStartX, inventoryStartY, button == 1)) {
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean handleInventoryClick(int mouseX, int mouseY, int startX, int startY, boolean isRightClick) {
        if (client == null || client.player == null) {
            return false;
        }

        PlayerInventory inventory = client.player.getInventory();

        for (int row = 0; row < INVENTORY_ROWS; row++) {
            for (int col = 0; col < INVENTORY_COLUMNS; col++) {
                int x = startX + col * SLOT_SIZE;
                int y = startY + row * SLOT_SIZE + 1;

                if (row == INVENTORY_ROWS - 1) {
                    y += 4;
                }

                if (isHovered(mouseX, mouseY, x + 1, y + 1)) {
                    int index;
                    if (row == INVENTORY_ROWS - 1) {
                        index = col;
                    } else {
                        index = col + (row + 1) * INVENTORY_COLUMNS;
                    }

                    handleInventorySlotClick(inventory, index, isRightClick);
                    return true;
                }
            }
        }

        return false;
    }

    private void handleInventorySlotClick(PlayerInventory inventory, int slotIndex, boolean isRightClick) {
        if (slotIndex >= inventory.size()) {
            return;
        }

        ItemStack inventoryItem = inventory.getStack(slotIndex);
        int serverSlotIndex;
        if (slotIndex < 9) {
            // Hotbar (slots 0-8 in inventory map to slots 81-89 in server)
            serverSlotIndex = 54 + 27 + slotIndex;
        } else {
            // Main inventory (slots 9-35 in inventory map to slots 54-80 in server)
            serverSlotIndex = 54 + (slotIndex - 9);
        }

        System.out.println("Player slot: " + slotIndex + " -> Server slot: " + serverSlotIndex);

        if (cursorStack.isEmpty()) {
            // Picking up item from inventory
            if (!inventoryItem.isEmpty()) {
                cursorItemSourceTab = -1; // Mark as coming from inventory, not a bank tab

                if (isRightClick) {
                    int halfCount = (inventoryItem.getCount() + 1) / 2;
                    cursorStack = inventoryItem.copy();
                    cursorStack.setCount(halfCount);
                    inventoryItem.decrement(halfCount);
                    if (inventoryItem.getCount() <= 0) {
                        inventory.setStack(slotIndex, ItemStack.EMPTY);
                    }
                } else {
                    cursorStack = inventoryItem.copy();
                    inventory.setStack(slotIndex, ItemStack.EMPTY);
                }
                isDraggingItem = true;

                // Send pickup packet to server
                enqueueTask(() -> {
                    sendSlotClickToServer(serverSlotIndex, isRightClick);
                });
            }
        } else {
            // Placing item into inventory (existing logic)
            final ItemStack cursorCopy = cursorStack.copy();
            final ItemStack inventoryItemCopy = inventoryItem.copy();

            enqueueTask(() -> {
                int currentRevision = handler.getRevision();
                sendSlotClickToServerWithRevision(serverSlotIndex, isRightClick, currentRevision);

                MinecraftClient.getInstance().execute(() -> {
                    if (inventoryItemCopy.isEmpty()) {
                        if (isRightClick) {
                            ItemStack singleItem = cursorCopy.copy();
                            singleItem.setCount(1);
                            inventory.setStack(slotIndex, singleItem);
                            cursorStack.decrement(1);
                            if (cursorStack.getCount() <= 0) {
                                cursorStack = ItemStack.EMPTY;
                                isDraggingItem = false;
                                cursorItemSourceTab = -1;
                            }
                        } else {
                            inventory.setStack(slotIndex, cursorCopy.copy());
                            cursorStack = ItemStack.EMPTY;
                            isDraggingItem = false;
                            cursorItemSourceTab = -1;
                        }
                    } else if (ItemStack.areItemsAndComponentsEqual(cursorCopy, inventoryItemCopy)) {
                        ItemStack currentInventoryItem = inventory.getStack(slotIndex);
                        int spaceLeft = currentInventoryItem.getMaxCount() - currentInventoryItem.getCount();
                        int toTransfer = isRightClick ? 1 : Math.min(spaceLeft, cursorStack.getCount());
                        if (toTransfer > 0 && spaceLeft > 0) {
                            currentInventoryItem.increment(toTransfer);
                            cursorStack.decrement(toTransfer);
                            if (cursorStack.getCount() <= 0) {
                                cursorStack = ItemStack.EMPTY;
                                isDraggingItem = false;
                                cursorItemSourceTab = -1;
                            }
                        }
                    } else if (!isRightClick) {
                        ItemStack temp = inventory.getStack(slotIndex).copy();
                        inventory.setStack(slotIndex, cursorCopy.copy());
                        cursorStack = temp;
                    }
                });
            });
        }
    }

    private void handleSlotClick(int tabIndex, int slotIndex, boolean isRightClick) {
        List<ItemStack> items = allTabItems.get(tabIndex);
        if (items == null || slotIndex >= items.size()) {
            return;
        }

        ItemStack clickedItem = items.get(slotIndex);

        if (cursorStack.isEmpty()) {
            // Picking up item
            if (!clickedItem.isEmpty()) {
                cursorItemSourceTab = tabIndex;

                // Update local state optimistically
                if (isRightClick) {
                    int halfCount = (clickedItem.getCount() + 1) / 2;
                    cursorStack = clickedItem.copy();
                    cursorStack.setCount(halfCount);

                    clickedItem.setCount(clickedItem.getCount() - halfCount);
                    if (clickedItem.getCount() <= 0) {
                        items.set(slotIndex, ItemStack.EMPTY);
                    }
                } else {
                    cursorStack = clickedItem.copy();
                    items.set(slotIndex, ItemStack.EMPTY);
                }
                isDraggingItem = true;

                // Sync with server: switch to tab and send click packet
                final int serverSlotIndex = 9 + slotIndex;
                enqueueTask(() -> {
                    switchToTabAndClick(tabIndex, serverSlotIndex, isRightClick);
                });
            }
        } else {
            // Placing item back in bank
            cursorItemSourceTab = tabIndex;

            if (clickedItem.isEmpty()) {
                if (isRightClick) {
                    // Place one item
                    ItemStack singleItem = cursorStack.copy();
                    singleItem.setCount(1);
                    items.set(slotIndex, singleItem);

                    cursorStack.decrement(1);
                    if (cursorStack.getCount() <= 0) {
                        cursorStack = ItemStack.EMPTY;
                        isDraggingItem = false;
                        cursorItemSourceTab = -1;
                    }
                } else {
                    // Place full stack
                    items.set(slotIndex, cursorStack.copy());
                    cursorStack = ItemStack.EMPTY;
                    isDraggingItem = false;
                    cursorItemSourceTab = -1;
                }
            } else if (ItemStack.areItemsAndComponentsEqual(cursorStack, clickedItem)) {
                // Stacking items
                if (isRightClick) {
                    // Add one item to stack
                    if (clickedItem.getCount() < clickedItem.getMaxCount()) {
                        clickedItem.increment(1);
                        cursorStack.decrement(1);
                        if (cursorStack.getCount() <= 0) {
                            cursorStack = ItemStack.EMPTY;
                            isDraggingItem = false;
                            cursorItemSourceTab = -1;
                        }
                    }
                } else {
                    // Merge stacks
                    int spaceLeft = clickedItem.getMaxCount() - clickedItem.getCount();
                    int toTransfer = Math.min(spaceLeft, cursorStack.getCount());

                    clickedItem.increment(toTransfer);
                    cursorStack.decrement(toTransfer);
                    if (cursorStack.getCount() <= 0) {
                        cursorStack = ItemStack.EMPTY;
                        isDraggingItem = false;
                        cursorItemSourceTab = -1;
                    }
                }
            } else {
                // Swap items (left click only)
                if (!isRightClick) {
                    ItemStack temp = clickedItem.copy();
                    items.set(slotIndex, cursorStack.copy());
                    cursorStack = temp;
                }
            }

            final int serverSlotIndex = 9 + slotIndex;
            enqueueTask(() -> {
                switchToTabAndClick(tabIndex, serverSlotIndex, isRightClick);
            });
        }
    }

    private int getTabAtPosition(int mouseX, int mouseY) {
        int startX = (width - GRID_WIDTH) / 2;
        int startY = (height - GRID_HEIGHT) / 2;

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                int gridX = startX + (col) * (SLOT_SIZE * 9 + 5);
                int gridY = (startY + (row) * (SLOT_SIZE * 5 + 5)) - 100;

                // Check if mouse is in the tab area
                if (mouseX >= gridX && mouseX < gridX + (SLOT_SIZE * 9) &&
                        mouseY >= gridY && mouseY < gridY + (SLOT_SIZE * 5)) {
                    return col + (row * 3);
                }
            }
        }
        return -1;
    }

    private void switchToTabAndClick(int tabIndex, int serverSlotIndex, boolean isRightClick) {
        // Send tab switch packet
        sendClickSlotPacket(tabIndex);

        try {
            // Wait for server to switch tabs
            int ping = Objects.requireNonNull(Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler())
                    .getPlayerListEntry(MinecraftClient.getInstance().player.getUuid())).getLatency();
            int delay = Math.max(150 + (ping * 2), 200);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Now send the actual click on the item slot
        sendSlotClickToServer(serverSlotIndex, isRightClick);
    }

    private void switchToTabWithItem(int tabIndex) {
        // Store cursor item temporarily
        ItemStack tempCursor = cursorStack.copy();

        // Switch to new tab
        switchToTab(tabIndex);

        // Restore cursor item after switch
        enqueueTask(() -> {
            try {
                Thread.sleep(200); // Wait for tab switch
                cursorStack = tempCursor;
                isDraggingItem = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void sendSlotClickToServerWithRevision(int slotIndex, boolean isRightClick, int revision) {
        if (client == null || client.getNetworkHandler() == null) {
            return;
        }

        ComponentChangesHash.ComponentHasher hasher = client.getNetworkHandler().getComponentHasher();
        Int2ObjectArrayMap<ItemStackHash> modifiedStacks = new Int2ObjectArrayMap<>();

        for (int i = 0; i < handler.slots.size(); i++) {
            modifiedStacks.put(i, ItemStackHash.fromItemStack(handler.slots.get(i).getStack(), hasher));
        }

        ItemStackHash carriedItemHash = ItemStackHash.fromItemStack(handler.getCursorStack(), hasher);

        Packet<?> packet = new ClickSlotC2SPacket(
                syncId,
                revision,  // Use the passed revision
                (short) slotIndex,
                isRightClick ? (byte) 1 : (byte) 0,
                SlotActionType.PICKUP,
                modifiedStacks,
                carriedItemHash
        );

        client.getNetworkHandler().sendPacket(packet);
    }

    private void sendSlotClickToServer(int slotIndex, boolean isRightClick) {
        if (client == null || client.getNetworkHandler() == null) {
            return;
        }

        ComponentChangesHash.ComponentHasher hasher = client.getNetworkHandler().getComponentHasher();
        Int2ObjectArrayMap<ItemStackHash> modifiedStacks = new Int2ObjectArrayMap<>();

        for (int i = 0; i < handler.slots.size(); i++) {
            modifiedStacks.put(i, ItemStackHash.fromItemStack(handler.slots.get(i).getStack(), hasher));
        }

        ItemStackHash carriedItemHash = ItemStackHash.fromItemStack(handler.getCursorStack(), hasher);

        Packet<?> packet = new ClickSlotC2SPacket(
                syncId,
                handler.getRevision(),
                (short) slotIndex,
                isRightClick ? (byte) 1 : (byte) 0,
                SlotActionType.PICKUP,
                modifiedStacks,
                carriedItemHash
        );

        client.getNetworkHandler().sendPacket(packet);
    }

    private void sendClickSlotPacket(int slotIndex) {
        if (client == null || client.getNetworkHandler() == null) {
            return;
        }

        ComponentChangesHash.ComponentHasher hasher = client.getNetworkHandler().getComponentHasher();

        Int2ObjectArrayMap<ItemStackHash> modifiedStacks = new Int2ObjectArrayMap<>();

        for (int i = 0; i < handler.slots.size(); i++) {
            modifiedStacks.put(i, ItemStackHash.fromItemStack(handler.slots.get(i).getStack(), hasher));
        }

        ItemStackHash carriedItemHash = ItemStackHash.fromItemStack(handler.getCursorStack(), hasher);

        Packet<?> packet = new ClickSlotC2SPacket(
                syncId,                        // syncId
                0,                            // revision
                (short) slotIndex,            // slot index as short
                (byte) 0,                     // button (left-click)
                SlotActionType.PICKUP,        // action type
                modifiedStacks,               // map of modified stacks
                carriedItemHash               // carried item hash
        );

        client.getNetworkHandler().sendPacket(packet);
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

        boolean hasItems = items.stream().anyMatch(stack -> !stack.isEmpty());
        if (hasItems || tabIndex == 0) {
            allTabItems.put(tabIndex, items);
            lockedTabs.remove(tabIndex); // Ensure it's not marked as locked
        } else {
            System.err.println("Warning: Tab " + tabIndex + " appears empty, might have been skipped");
        }
    }

    private static final Identifier LOCKED_VAULT_MODEL =
            Identifier.of("faceland", "icons/bank/vault_page_locked");

    @Override
    public void close() {
        super.close();
        executorService.shutdown();
        lastUpdateTime = 0;
        tooltipItemIndex = -1;
        allTabItems.clear();
        lockedTabs.clear();

        cursorStack = ItemStack.EMPTY;
        isDraggingItem = false;
        cursorItemSourceTab = -1;
    }}
