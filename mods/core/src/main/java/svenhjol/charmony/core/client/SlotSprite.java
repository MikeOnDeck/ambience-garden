package svenhjol.charmony.core.client;

import net.minecraft.resources.ResourceLocation;
import svenhjol.charmony.api.core.Sprite;
import svenhjol.charmony.core.Charmony;

@SuppressWarnings("unused")
public enum SlotSprite implements Sprite {
    Slot(ResourceLocation.fromNamespaceAndPath(Charmony.ID, "item_container/slot"), 18, 18),
    SelectedSlot(ResourceLocation.fromNamespaceAndPath(Charmony.ID, "item_container/selected_slot"), 18, 18);

    private final ResourceLocation sprite;
    private final int width;
    private final int height;

    SlotSprite(final ResourceLocation resourceLocation, final int width, final int height) {
        this.sprite = resourceLocation;
        this.width = width;
        this.height = height;
    }

    @Override
    public ResourceLocation sprite() {
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
