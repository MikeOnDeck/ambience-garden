package svenhjol.charmony.tweaks.common.features.animal_reviving;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record Data(CompoundTag tag) {
    public static final Codec<Data> CODEC = Codec.withAlternative(CompoundTag.CODEC, TagParser.FLATTENED_CODEC)
        .xmap(Data::new, data -> data.tag);

    public static final StreamCodec<ByteBuf, Data> STREAM_CODEC = ByteBufCodecs.COMPOUND_TAG
        .map(Data::new, data -> data.tag);

    public static Data of(CompoundTag tag) {
        return new Data(tag.copy());
    }

    public CompoundTag copy() {
        return this.tag.copy();
    }
}
