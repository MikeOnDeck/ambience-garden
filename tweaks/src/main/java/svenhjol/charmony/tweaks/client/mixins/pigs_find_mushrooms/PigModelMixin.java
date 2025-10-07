package svenhjol.charmony.tweaks.client.mixins.pigs_find_mushrooms;

import net.minecraft.client.model.PigModel;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.PigRenderState;
import net.minecraft.world.entity.animal.Pig;
import org.spongepowered.asm.mixin.Mixin;
import svenhjol.charmony.core.base.Environment;
import svenhjol.charmony.tweaks.client.features.pigs_find_mushrooms.PigsFindMushrooms;

/**
 * Extend the pig model with additional animation methods.
 */
@Mixin(PigModel.class)
public abstract class PigModelMixin extends QuadrupedModel<PigRenderState> {
    protected PigModelMixin(ModelPart modelPart) {
        super(modelPart);
    }

    @Override
    public void setupAnim(PigRenderState state) {
        super.setupAnim(state);
        if (!Environment.usesCharmonyServer()) return;

        var handlers = PigsFindMushrooms.feature().handlers;
        var age = state.ageScale;

        if (!(handlers.pig instanceof Pig mob)) return;

        this.head.y = 12.0f + handlers.getHeadEatPositionScale(mob, age) * 6.0f;
        this.head.xRot = handlers.getHeadEatAngleScale(mob, age);
    }
}
