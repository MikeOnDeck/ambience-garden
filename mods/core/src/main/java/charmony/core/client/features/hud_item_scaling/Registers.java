package charmony.core.client.features.hud_item_scaling;

import charmony.core.base.Setup;
import charmony.core.client.BaseHudRenderer;

import java.util.ArrayList;
import java.util.List;

public class Registers extends Setup<HudItemScaling> {
    private final List<BaseHudRenderer> hudRenderers = new ArrayList<>();

    public Registers(HudItemScaling feature) {
        super(feature);
    }

    public void add(BaseHudRenderer renderer) {
        if (feature().enabled()) {
            hudRenderers.add(renderer);
        }
    }

    public List<BaseHudRenderer> getHudRenderers() {
        return hudRenderers;
    }
}
