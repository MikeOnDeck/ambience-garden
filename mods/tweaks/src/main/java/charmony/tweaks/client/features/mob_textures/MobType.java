package charmony.tweaks.client.features.mob_textures;

import charmony.core.Charmony;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum MobType implements StringRepresentable {
    SNOW_GOLEM("snow_golem"),
    WANDERING_TRADER("wandering_trader");

    private final Identifier vanillaTexture;

    MobType(String vanillaTexture) {
        this.vanillaTexture = Identifier.parse("textures/entity/" + vanillaTexture + ".png");
    }

    public Identifier vanillaTexture() {
        return vanillaTexture;
    }

    public Identifier customTexture(String texture) {
        return Identifier.tryBuild(Charmony.ID, "textures/entity/" + this.getSerializedName() + "/" + texture + ".png");
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}