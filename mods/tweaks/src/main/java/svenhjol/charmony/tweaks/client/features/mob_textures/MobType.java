package svenhjol.charmony.tweaks.client.features.mob_textures;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import svenhjol.charmony.core.Charmony;

import java.util.Locale;

public enum MobType implements StringRepresentable {
    SNOW_GOLEM("snow_golem"),
    WANDERING_TRADER("wandering_trader");

    private final ResourceLocation vanillaTexture;

    MobType(String vanillaTexture) {
        this.vanillaTexture = ResourceLocation.parse("textures/entity/" + vanillaTexture + ".png");
    }

    public ResourceLocation vanillaTexture() {
        return vanillaTexture;
    }

    public ResourceLocation customTexture(String texture) {
        return ResourceLocation.tryBuild(Charmony.ID, "textures/entity/" + this.getSerializedName() + "/" + texture + ".png");
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}