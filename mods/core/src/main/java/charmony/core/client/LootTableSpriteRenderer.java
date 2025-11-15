package charmony.core.client;

import charmony.core.Charmony;
import charmony.core.client.renderers.SpriteRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

@SuppressWarnings("unused")
public class LootTableSpriteRenderer extends SpriteRenderer {
    private static final int WIDTH = 16;
    private static final int HEIGHT = 16;

    public LootTableSpriteRenderer(Identifier id) {
        super(id);
    }

    @Override
    public int width() {
        return WIDTH;
    }

    @Override
    public int height() {
        return HEIGHT;
    }

    public Identifier getTexture() {
        return Charmony.id("loot_table/" + id.getNamespace() + "/" + id.getPath());
    }

    @Override
    public Component getName() {
        return Component.translatableWithFallback("loot_table." + id.getNamespace() + "." + id.getPath().replace("/", "."), niceLootTableName(id));
    }

    protected String niceLootTableName(Identifier id) {
        var path = id.getPath();

        // Split path into fragments based on "/".
        var fragments = path.split("/");

        // If the last fragment is a single word, combine it with the second-to-last fragment.
        if (fragments.length >= 2) {
            var lastFragment = fragments[fragments.length - 1];
            if (!lastFragment.contains("_") && !lastFragment.contains("-")) {
                path = fragments[fragments.length - 2] + "_" + lastFragment;
            }
        }

        // Generate a nicely formatted name from the last part of the path.
        var nice = path.replace("_", " ").replace("-", " ");
        return nice.substring(0, 1).toUpperCase() + nice.substring(1);
    }
}
