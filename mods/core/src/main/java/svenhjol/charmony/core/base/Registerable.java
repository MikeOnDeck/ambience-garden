package svenhjol.charmony.core.base;

import java.util.function.Supplier;

public final class Registerable<R> implements Supplier<R> {
    private final Supplier<R> supplier;
    private R instance;

    public Registerable(SidedFeature feature, Supplier<R> supplier) {
        this.supplier = supplier;
        feature.mod().addRegisterStep(feature, this::get);
    }

    public R get() {
        if (instance == null) {
            instance = supplier.get();
        }
        return instance;
    }
}
