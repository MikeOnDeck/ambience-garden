package charmony.api.rune_dictionary;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;

import java.util.List;

@SuppressWarnings("unused")
public interface RuneWordProvider {
    /**
     * Gets a list of registered object to register as rune words.
     * This method receives a reference to the finalized registry so that it can access all registered objects.
     *
     * @param registryAccess Reference to the finalized registry.
     * @return List of objects to register.
     */
    List<Identifier> getRuneWords(RegistryAccess registryAccess);
}
