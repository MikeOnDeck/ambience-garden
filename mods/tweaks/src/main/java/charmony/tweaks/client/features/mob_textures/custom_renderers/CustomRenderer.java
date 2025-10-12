package charmony.tweaks.client.features.mob_textures.custom_renderers;

import charmony.core.base.Mod;
import charmony.tweaks.client.features.mob_textures.Handlers;
import charmony.tweaks.client.features.mob_textures.MobTextures;
import charmony.tweaks.client.features.mob_textures.Registers;

public interface CustomRenderer {
    Handlers handlers = Mod.getSidedFeature(MobTextures.class).handlers;
    Registers registers = Mod.getSidedFeature(MobTextures.class).registers;
}
