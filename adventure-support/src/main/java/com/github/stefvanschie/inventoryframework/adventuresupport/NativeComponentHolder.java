package com.github.stefvanschie.inventoryframework.adventuresupport;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A {@link ComponentHolder} implementation for platforms where Adventure is natively supported.
 * Adventure components are directly passed to the Bukkit (Paper) API.
 *
 * @see ForeignComponentHolder
 * @since 0.10.0
 */
class NativeComponentHolder extends ComponentHolder {
    
    /**
     * Creates and initializes a new instance.
     *
     * @param value the Adventure component this instance should wrap
     * @since 0.10.0
     */
    NativeComponentHolder(@NotNull Component value) {
        super(value);
    }

    @Override
    public @NotNull String asLegacyString() {
        return value.toString(); // TODO: Needs to be done correctly
    }

    @NotNull
    @Contract(pure = true)
    @Override
    public Inventory asInventoryTitle(InventoryHolder holder, InventoryType type) {
        return Bukkit.createInventory(holder, type, value.toString()); // TODO: Needs to be done correctly
    }

    @NotNull
    @Contract(pure = true)
    @Override
    public Inventory asInventoryTitle(InventoryHolder holder, int size) {
        return Bukkit.createInventory(holder, size, value.toString()); // TODO: Needs to be done correctly
    }

    @Override
    public void asItemDisplayName(ItemMeta meta) {
        meta.setDisplayName(value.toString()); // TODO: Needs to be done correctly
    }
    
    @Override
    public void asItemLoreAtEnd(ItemMeta meta) {
        List<String> lore = meta.hasLore()
                ? Objects.requireNonNull(meta.getLore()) // TODO: Needs to be done correctly
                : new ArrayList<>();
        lore.add(value.toString()); // TODO: Needs to be done correctly
        meta.setLore(lore);
    }
}
