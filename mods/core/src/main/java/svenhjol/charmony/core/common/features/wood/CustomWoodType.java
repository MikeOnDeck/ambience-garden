package svenhjol.charmony.core.common.features.wood;

import svenhjol.charmony.core.base.SidedFeature;

public abstract class CustomWoodType {
    protected final WoodRegistry registry;
    protected final WoodMaterial material;
    protected final SidedFeature feature;

    public CustomWoodType(WoodRegistry registry, WoodMaterial material) {
        this.registry = registry;
        this.material = material;
        this.feature = registry.feature();
    }

    public WoodMaterial material() {
        return material;
    }

    public SidedFeature feature() {
        return feature;
    }
}
