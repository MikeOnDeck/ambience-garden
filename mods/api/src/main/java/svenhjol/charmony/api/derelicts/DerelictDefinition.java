package svenhjol.charmony.api.derelicts;

import net.minecraft.util.StringRepresentable;

public interface DerelictDefinition extends StringRepresentable {
    String name();

    @Override
    default String getSerializedName() {
        return name();
    }
}
