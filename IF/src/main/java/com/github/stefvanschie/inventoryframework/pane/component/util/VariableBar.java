package com.github.stefvanschie.inventoryframework.pane.component.util;

import com.github.stefvanschie.inventoryframework.gui.InventoryComponent;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.Flippable;
import com.github.stefvanschie.inventoryframework.pane.Orientable;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A variable bar for UI elements that require some sort of bar
 *
 * @since 0.5.0
 */
public abstract class VariableBar extends Pane implements Orientable, Flippable  {

    /**
     * The green and the red parts of the slider
     */
    @NotNull
    protected final OutlinePane fillPane, backgroundPane;

    /**
     * The value this slider is at. This is a value between 0 and 1 (both inclusive).
     */
    protected float value;

    /**
     * The orientation of the slider
     */
    @NotNull
    protected Orientation orientation;

    /**
     * Whether the pane is flipped horizontally or vertically
     */
    protected boolean flipHorizontally, flipVertically;

    protected VariableBar(int length, int height) {
        super(length, height);

        this.value = 0F;
        this.orientation = Orientation.HORIZONTAL;

        this.fillPane = new OutlinePane(0, 0, length, height);
        this.backgroundPane = new OutlinePane(0, 0, length, height);

        this.fillPane.addItem(new GuiItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5),
            event -> event.setCancelled(true)));
        this.backgroundPane.addItem(new GuiItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15),
            event -> event.setCancelled(true)));

        this.fillPane.setRepeat(true);
        this.backgroundPane.setRepeat(true);

        this.fillPane.setVisible(false);
    }

    protected VariableBar(int x, int y, int length, int height, @NotNull Priority priority) {
        this(length, height);

        setX(x);
        setY(y);

        setPriority(priority);
    }

    protected VariableBar(int x, int y, int length, int height) {
        this(x, y, length, height, Priority.NORMAL);
    }

    /**
     * Sets the value of this bar. The value has to be in (0,1). If not, this method will throw an
     * {@link IllegalArgumentException}.
     *
     * @param value the new value.
     * @throws IllegalArgumentException when the value is out of range
     * @since 0.9.5
     */
    protected void setValue(float value) {
        if (value < 0 || value > 1) {
            throw new IllegalArgumentException("Value is out of range (0,1)");
        }

        this.value = value;

        if (orientation == Orientation.HORIZONTAL) {
            int length = Math.round(getLength() * value);
            boolean positiveLength = length != 0;

            this.fillPane.setVisible(positiveLength);

            if (positiveLength) {
                this.fillPane.setLength(length);
            }

            if (flipHorizontally) {
                this.fillPane.setX(getLength() - this.fillPane.getLength());
            }
        } else if (orientation == Orientation.VERTICAL) {
            int height = Math.round(getHeight() * value);
            boolean positiveHeight = height != 0;

            this.fillPane.setVisible(positiveHeight);

            if (positiveHeight) {
                this.fillPane.setHeight(height);
            }

            if (flipVertically) {
                this.fillPane.setY(getHeight() - this.fillPane.getHeight());
            }
        } else {
            throw new UnsupportedOperationException("Unknown orientation");
        }
    }

    @Override
    public void setLength(int length) {
        super.setLength(length);

        if (orientation == Orientation.HORIZONTAL) {
            int fillLength = Math.round(length * value);
            boolean positiveLength = fillLength != 0;

            this.fillPane.setVisible(positiveLength);

            if (positiveLength) {
                this.fillPane.setLength(fillLength);
            }

            if (flipHorizontally) {
                this.fillPane.setX(getLength() - this.fillPane.getLength());
            }
        } else if (orientation == Orientation.VERTICAL) {
            this.fillPane.setLength(length);
        } else {
            throw new UnsupportedOperationException("Unknown orientation");
        }

        this.backgroundPane.setLength(length);
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);

        if (orientation == Orientation.HORIZONTAL) {
            this.fillPane.setHeight(height);
        } else if (orientation == Orientation.VERTICAL) {
            int fillHeight = Math.round(height * value);
            boolean positiveHeight = fillHeight != 0;

            this.fillPane.setVisible(positiveHeight);

            if (positiveHeight) {
                this.fillPane.setHeight(fillHeight);
            }

            if (flipVertically) {
                this.fillPane.setY(getHeight() - this.fillPane.getHeight());
            }
        } else {
            throw new UnsupportedOperationException("Unknown orientation");
        }

        this.backgroundPane.setHeight(height);
    }

    /**
     * Applies the contents of this variable bar onto the provided copy of this variable bar. This variable bar will not
     * be modified.
     *
     * @param copy the copy of the variable bar
     * @since 0.6.2
     */
    protected void applyContents(@NotNull VariableBar copy) {
        copy.x = x;
        copy.y = y;
        copy.length = length;
        copy.height = height;
        copy.setPriority(getPriority());

        copy.setVisible(isVisible());
        copy.onClick = onClick;

        copy.setFillItem(fillPane.getItems().get(0).copy());
        copy.setBackgroundItem(backgroundPane.getItems().get(0).copy());

        copy.value = value;
        copy.orientation = orientation;

        copy.flipHorizontally = flipHorizontally;
        copy.flipVertically = flipVertically;

        copy.uuid = uuid;
    }

    @Override
    public void setOrientation(@NotNull Orientation orientation) {
        this.orientation = orientation;

        if (orientation == Orientation.HORIZONTAL) {
            int fillLength = Math.round(getLength() * value);
            boolean positiveLength = fillLength != 0;

            fillPane.setVisible(fillLength != 0);

            if (positiveLength) {
                fillPane.setLength(fillLength);
            }

            fillPane.setHeight(getHeight());
        } else if (orientation == Orientation.VERTICAL) {
            int fillHeight = Math.round(getHeight() * value);
            boolean positiveHeight = fillHeight != 0;

            fillPane.setVisible(fillHeight != 0);
            fillPane.setLength(getLength());

            if (positiveHeight) {
                fillPane.setHeight(fillHeight);
            }
        } else {
            throw new IllegalArgumentException("Unknown orientation");
        }
    }

    @Override
    public void display(@NotNull InventoryComponent inventoryComponent, int paneOffsetX, int paneOffsetY, int maxLength,
                        int maxHeight) {
        int newPaneOffsetX = paneOffsetX + getX();
        int newPaneOffsetY = paneOffsetY + getY();
        int newMaxLength = Math.min(maxLength, getLength());
        int newMaxHeight = Math.min(maxHeight, getHeight());

        this.backgroundPane.display(inventoryComponent, newPaneOffsetX, newPaneOffsetY, newMaxLength, newMaxHeight);
        this.fillPane.display(inventoryComponent, newPaneOffsetX, newPaneOffsetY, newMaxLength, newMaxHeight);
    }

    /**
     * Sets the fill item (foreground)
     *
     * @param item the new item
     * @since 0.5.0
     */
    public void setFillItem(@NotNull GuiItem item) {
        fillPane.clear();

        fillPane.addItem(item);
    }

    /**
     * Sets the background item
     *
     * @param item the new item
     * @since 0.5.0
     */
    public void setBackgroundItem(@NotNull GuiItem item) {
        backgroundPane.clear();

        backgroundPane.addItem(item);
    }

    @NotNull
    @Override
    public Collection<GuiItem> getItems() {
        return getPanes().stream().flatMap(pane -> pane.getItems().stream()).collect(Collectors.toSet());
    }

    @NotNull
    @Override
    public Collection<Pane> getPanes() {
        return Stream.of(this.fillPane, this.backgroundPane).collect(Collectors.toSet());
    }

    @Override
    public void flipHorizontally(boolean flipHorizontally) {
        this.flipHorizontally = flipHorizontally;
    }

    @Override
    public void flipVertically(boolean flipVertically) {
        this.flipVertically = flipVertically;
    }

    @NotNull
    @Override
    public Orientation getOrientation() {
        return orientation;
    }

    @Override
    public boolean isFlippedHorizontally() {
        return flipHorizontally;
    }

    @Override
    public boolean isFlippedVertically() {
        return flipVertically;
    }

    @Override
    public void clear() {}
}
