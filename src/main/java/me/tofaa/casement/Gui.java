package me.tofaa.casement;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface Gui {

    static @NotNull Builder builder(@NotNull InventoryType type, @NotNull Component title) {
        Check.notNull(type, "type cannot be null");
        Check.notNull(title, "title cannot be null");
        return new GuiImpl.Builder(type, title);
    }


    @Nullable Gui getParent();

    @NotNull InventoryType getInventoryType();

    @Nullable Consumer<InventoryPreClickEvent> getClickHandler();

    @Nullable Consumer<InventoryCloseEvent> getCloseHandler();

    void setButton(int slot, @NotNull GuiButton button);

    void open(@NotNull Player player);

    void close(@NotNull Player player);

    void refresh();

    void openParent(@NotNull Player player);

    @Nullable GuiButton getButtonAt(int slot);

    interface Builder {

        @NotNull Builder withParent(@Nullable Gui parent);

        @NotNull Builder withTitle(@NotNull Component title);

        @NotNull Builder withType(@NotNull InventoryType type);

        @NotNull Builder withButton(int slot, @Nullable GuiButton button);

        @NotNull Builder withButton(@NotNull GuiButton button);

        @NotNull Builder withClickHandler(@Nullable Consumer<InventoryPreClickEvent> handler);

        @NotNull Builder withCloseHandler(@Nullable Consumer<InventoryCloseEvent> handler);

        @NotNull Builder withFiller(@NotNull GuiButton button);

        @NotNull Gui build();

    }

}
