package me.tofaa.casement;

import me.tofaa.casement.util.TriConsumer;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record GuiButton(@NotNull ItemStack icon, @Nullable TriConsumer<Gui, Player, ClickType> clickHandler) {
}
