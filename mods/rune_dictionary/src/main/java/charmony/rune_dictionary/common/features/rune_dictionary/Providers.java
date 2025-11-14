package charmony.rune_dictionary.common.features.rune_dictionary;

import charmony.api.Api;
import charmony.api.rune_dictionary.RuneWordProvider;
import charmony.core.base.Setup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class Providers extends Setup<RuneDictionary> implements RuneWordProvider {
    public Providers(RuneDictionary feature) {
        super(feature);
        Api.registerProvider(this);
    }

    /**
     * Add all registered structures and biomes to the dictionary.
     *
     * @param registryAccess Finalized registry access.
     * @return List of all resourcelocations for registered structures and biomes.
     */
    @Override
    public List<Identifier> getRuneWords(RegistryAccess registryAccess) {
        var biomes = registryAccess
            .lookup(Registries.BIOME)
            .map(reg -> reg.keySet().stream().toList())
            .orElse(List.of());

        var structures = registryAccess
            .lookup(Registries.STRUCTURE)
            .map(r -> r.keySet().stream().toList())
            .orElse(List.of());

        List<Identifier> out = new ArrayList<>();
        out.addAll(structures);
        out.addAll(biomes);

        return out;
    }
}
