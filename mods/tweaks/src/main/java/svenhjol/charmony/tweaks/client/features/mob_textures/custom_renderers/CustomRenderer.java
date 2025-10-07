package svenhjol.charmony.tweaks.client.features.mob_textures.custom_renderers;

import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.tweaks.client.features.mob_textures.Handlers;
import svenhjol.charmony.tweaks.client.features.mob_textures.MobTextures;
import svenhjol.charmony.tweaks.client.features.mob_textures.Registers;

public interface CustomRenderer {
    Handlers handlers = Mod.getSidedFeature(MobTextures.class).handlers;
    Registers registers = Mod.getSidedFeature(MobTextures.class).registers;
}
