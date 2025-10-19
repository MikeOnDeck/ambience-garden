package charmony.villager_tasks.common.features.villager_tasks.enums;

import net.minecraft.util.RandomSource;

public enum TaskModifier {
    Unspecified(0.0, 1.0, 1.0, false),
    Angry(0.5, 0.5, 0.75, false),
    Normal(1.0, 1.0, 1.0, false),
    Happy(1.5, 1.0, 1.5, false),
    Epic(2.0, 2.0, 4.0, true);

    private final double opinion;
    private final double minMultiplier;
    private final double maxMultiplier;
    private final boolean epic;

    TaskModifier(double opinion, double minMultiplier, double maxMultiplier, boolean epic) {
        this.opinion = opinion;
        this.minMultiplier = minMultiplier;
        this.maxMultiplier = maxMultiplier;
        this.epic = epic;
    }

    public double getMultiplier(RandomSource random) {
        return minMultiplier + (random.nextDouble() * (maxMultiplier - minMultiplier));
    }

    public double getOpinion() {
        return opinion;
    }

    public boolean isEpic() {
        return epic;
    }
}
