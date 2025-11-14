package charmony.core.client;

import charmony.api.core.Sprite;
import charmony.core.Charmony;
import net.minecraft.resources.Identifier;

@SuppressWarnings("unused")
public enum SlotSprite implements Sprite {
    Slot(Identifier.fromNamespaceAndPath(Charmony.ID, "item_container/slot"), 18, 18),
    SelectedSlot(Identifier.fromNamespaceAndPath(Charmony.ID, "item_container/selected_slot"), 18, 18);

    private final Identifier sprite;
    private final int width;
    private final int height;

    SlotSprite(final Identifier Identifier, final int width, final int height) {
        this.sprite = Identifier;
        this.width = width;
        this.height = height;
    }

    @Override
    public Identifier sprite() {
        return sprite;
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }
}
