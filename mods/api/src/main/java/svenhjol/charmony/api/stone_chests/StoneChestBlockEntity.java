package svenhjol.charmony.api.stone_chests;

@SuppressWarnings("unused")
public interface StoneChestBlockEntity {
    void lock();

    void unlock();

    boolean isLocked();

    void setCustomDefinition(String customDefinition);

    String getCustomDefinition();

    StoneChestMaterial getMaterial();
}
