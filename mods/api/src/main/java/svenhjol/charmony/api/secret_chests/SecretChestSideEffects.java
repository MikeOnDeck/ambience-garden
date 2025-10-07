package svenhjol.charmony.api.secret_chests;

import net.minecraft.util.StringRepresentable;

@SuppressWarnings("unused")
public enum SecretChestSideEffects implements StringRepresentable {
    Nothing("nothing"),
    SpawnOverworldMonsters("spawn_overworld_monsters"),
    SpawnNetherMonsters("spawn_nether_monsters"),
    SpawnEndMonsters("spawn_end_monsters"),
    Explode("explode"),
    GiveBadEffect("give_bad_effect");

    private final String name;

    SecretChestSideEffects(String name) {
        this.name = name;
    }

    public static SecretChestSideEffects getOrDefault(String name) {
        SecretChestSideEffects sideEffect;
        try {
            sideEffect = SecretChestSideEffects.valueOf(name);
        } catch (IllegalArgumentException e) {
            sideEffect = Nothing;
        }
        return sideEffect;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
