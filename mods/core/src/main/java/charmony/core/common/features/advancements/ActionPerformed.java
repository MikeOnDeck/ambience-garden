package charmony.core.common.features.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class ActionPerformed extends SimpleCriterionTrigger<ActionPerformed.TriggerInstance> {
    public void trigger(Identifier action, ServerPlayer player) {
        this.trigger(player, conditions -> conditions.matches(action));
    }

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public record TriggerInstance(Identifier action, Optional<ContextAwarePredicate> player)
        implements net.minecraft.advancements.criterion.SimpleCriterionTrigger.SimpleInstance {

        /**
         * @see net.minecraft.advancements.criterion.LootTableTrigger
         * Similar implementation with player.
         */
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("action")
                .forGetter(TriggerInstance::action),
            EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player")
                .forGetter(TriggerInstance::player)
        ).apply(instance, TriggerInstance::new));

        public boolean matches(Identifier action) {
            return this.action.equals(action);
        }

        @Override
        public Optional<ContextAwarePredicate> player() {
            return this.player;
        }
    }
}
