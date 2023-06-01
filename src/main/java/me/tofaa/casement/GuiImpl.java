package me.tofaa.casement;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.trait.InventoryEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

final class GuiImpl extends Inventory implements Gui {

    static {
        EventNode<InventoryEvent> node = EventNode.type("casement", EventFilter.INVENTORY);
        node.addListener(InventoryPreClickEvent.class, event -> {
            Inventory inventory = event.getInventory();

            if(!(inventory instanceof GuiImpl gui)) return;
            event.setCancelled(true);

            GuiButton button = gui.getButtonAt(event.getSlot());
            if(button != null && button.clickHandler() != null) {
                button.clickHandler().accept(gui, event.getPlayer(), event.getClickType());
            }

            if(gui.getClickHandler() != null) gui.getClickHandler().accept(event);
        });
        node.addListener(InventoryCloseEvent.class, event -> {
            Inventory inventory = event.getInventory();

            if(!(inventory instanceof GuiImpl gui)) return;

            if(gui.getCloseHandler() != null)
                gui.getCloseHandler().accept(event);
        });
        MinecraftServer.getGlobalEventHandler().addChild(node);
    }

    private final Gui parent;
    private final Consumer<InventoryPreClickEvent> clickHandler;
    private final Consumer<InventoryCloseEvent> closeHandler;
    private final Map<Integer, GuiButton> buttonsMap;

    GuiImpl(
            @Nullable Gui parent,
            @NotNull InventoryType type,
            @NotNull Component title,
            @NotNull HashMap<Integer, GuiButton> buttonsMap,
            @Nullable Consumer<InventoryPreClickEvent> clickHandler,
            @Nullable Consumer<InventoryCloseEvent> closeHandler,
            @Nullable GuiButton filler
    ) {
        super(type, title);
        this.parent = parent;
        this.clickHandler = clickHandler;
        this.closeHandler = closeHandler;
        this.buttonsMap = buttonsMap;
        this.buttonsMap.forEach((slot, button) -> setItemStack(slot, button.icon()));
        if (filler != null) {
            for (int i = 0; i < type.getSize(); i++) {
                if (!buttonsMap.containsKey(i)) {
                    setItemStack(i, filler.icon());
                    buttonsMap.put(i, filler);
                }
            }
        }
    }

    @Nullable @Override
    public Gui getParent() {
        return parent;
    }

    @Override
    public void openParent(@NotNull Player player) {
        if (parent != null) {
            close(player);
            parent.open(player);
        }
    }

    @Override
    public @Nullable Consumer<InventoryPreClickEvent> getClickHandler() {
        return clickHandler;
    }

    @Override
    public @Nullable Consumer<InventoryCloseEvent> getCloseHandler() {
        return closeHandler;
    }

    @Override
    public void setButton(int slot, @NotNull GuiButton button) {
        Check.notNull(button, "button cannot be null");
        buttonsMap.put(slot, button);
        setItemStack(slot, button.icon());
    }

    @Override
    public void open(@NotNull Player player) {
        player.openInventory(this);
    }

    @Override
    public void close(@NotNull Player player) {
        if (player.getOpenInventory() == this) {
            player.closeInventory();
        }
    }

    @Override
    public void refresh() {
        buttonsMap.forEach((slot, button) -> setItemStack(slot, button.icon()));
    }

    @Override
    public @Nullable GuiButton getButtonAt(int slot) {
        return buttonsMap.get(slot);
    }


    static class Builder implements Gui.Builder {

        private Gui parent;
        private Component title;
        private InventoryType type;
        private final Map<Integer, GuiButton> buttons = new HashMap<>();
        private int nextButton;
        private Consumer<InventoryPreClickEvent> clickHandler;
        private Consumer<InventoryCloseEvent> closeHandler;
        private GuiButton fillBlanks;

        Builder(@NotNull InventoryType type, @NotNull Component title) {
            nextButton = 0;
            this.title = title;
            this.type = type;
        }

        @Override
        public Gui.@NotNull Builder withTitle(@NotNull Component title) {
            this.title = title;
            return this;
        }

        @Override
        public Gui.@NotNull Builder withButton(@NotNull GuiButton button) {
            if(nextButton > type.getSize() - 1) throw new UnsupportedOperationException();
            buttons.put(nextButton, button);
            nextButton++;
            return this;
        }

        @Override
        public Gui.@NotNull Builder withButton(int slot, @Nullable GuiButton button) {
            if (button == null) {
                buttons.remove(slot);
                return this;
            }
            if(slot > type.getSize() - 1) throw new UnsupportedOperationException();
            buttons.put(slot, button);
            nextButton = slot + 1;
            return this;
        }

        @Override
        public Gui.@NotNull Builder withType(@NotNull InventoryType type) {
            this.type = type;
            return this;
        }

        @Override
        public Gui.@NotNull Builder withClickHandler(@Nullable Consumer<InventoryPreClickEvent> handler) {
            this.clickHandler = handler;
            return this;
        }

        @Override
        public Gui.@NotNull Builder withCloseHandler(@Nullable Consumer<InventoryCloseEvent> handler) {
            this.closeHandler = handler;
            return this;
        }

        @Override
        public Gui.@NotNull Builder withFiller(@NotNull GuiButton button) {
            Check.notNull(button, "button cannot be null");
            this.fillBlanks = button;
            return this;
        }

        @Override
        public Gui.@NotNull Builder withParent(@Nullable Gui parent) {
            this.parent = parent;
            return this;
        }

        @Override
        public @NotNull Gui build() {
            return new GuiImpl(parent, type, title, new HashMap<>(buttons), clickHandler, closeHandler, fillBlanks);
        }
    }


}
