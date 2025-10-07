package svenhjol.charmony.core.base;


import java.util.function.BooleanSupplier;

public abstract class Setup<F extends SidedFeature> {
    private final F feature;

    public Setup(F feature) {
        this.feature = feature;
        this.feature.mod().addCheckStep(feature, check());
        this.feature.mod().addBootStep(feature, boot());
    }

    public F feature() {
        return this.feature;
    }

    public Log log() {
        return this.feature.log();
    }

    public BooleanSupplier check() {
        return () -> true;
    }

    public Runnable boot() {
        return () -> {};
    }
}
