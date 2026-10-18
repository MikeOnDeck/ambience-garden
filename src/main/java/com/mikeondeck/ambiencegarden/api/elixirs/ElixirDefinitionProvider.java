package com.mikeondeck.ambiencegarden.api.elixirs;

import java.util.List;

@SuppressWarnings("unused")
public interface ElixirDefinitionProvider {
    List<ElixirDefinition> getElixirDefinitions();
}
