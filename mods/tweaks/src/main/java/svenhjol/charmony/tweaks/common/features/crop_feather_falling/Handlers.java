package svenhjol.charmony.tweaks.common.features.crop_feather_falling;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.helpers.EnchantmentsHelper;

public class Handlers extends Setup<CropFeatherFalling> {
    public Handlers(CropFeatherFalling feature) {
        super(feature);
    }

    public boolean shouldPreventCropDamage(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return false;
        }

        var shouldPrevent = EnchantmentsHelper.featherFallingLevel(livingEntity) > 0;

        // Do advancement.
        if (shouldPrevent && livingEntity instanceof ServerPlayer player) {
            feature().advancements.preventedCropDamage(player);
        }

        return shouldPrevent;
    }
}
