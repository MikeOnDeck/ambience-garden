package svenhjol.charmony.api.secret_chests;

import java.util.List;

/**
 * Provider for secret chest definitions.
 */
public interface SecretChestDefinitionProvider {
    List<SecretChestDefinition> getSecretChestDefinitions();
}
