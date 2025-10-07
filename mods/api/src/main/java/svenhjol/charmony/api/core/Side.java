package svenhjol.charmony.api.core;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

@SuppressWarnings("unused")
public enum Side implements StringRepresentable {
    Client,
    Common,
    Server;

    @SuppressWarnings("NullableProblems")
    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
