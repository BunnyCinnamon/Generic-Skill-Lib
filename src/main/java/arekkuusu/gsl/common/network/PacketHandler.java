package arekkuusu.gsl.common.network;

import arekkuusu.gsl.GSL;
import arekkuusu.gsl.api.GSLChannel;
import arekkuusu.gsl.api.network.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

public final class PacketHandler {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(GSL.ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        int id = 0;
        INSTANCE.registerMessage(id++, SyncSkillsPacket.class, SyncSkillsPacket::encoding, SyncSkillsPacket::decoding, SyncSkillsPacket::handle);
        INSTANCE.registerMessage(id++, SyncEffectsPacket.class, SyncEffectsPacket::encoding, SyncEffectsPacket::decoding, SyncEffectsPacket::handle);
        INSTANCE.registerMessage(id++, SyncEffectsTrackingPacket.class, SyncEffectsTrackingPacket::encoding, SyncEffectsTrackingPacket::decoding, SyncEffectsTrackingPacket::handle);
        INSTANCE.registerMessage(id++, SyncSkillAddPacket.class, SyncSkillAddPacket::encoding, SyncSkillAddPacket::decoding, SyncSkillAddPacket::handle);
        INSTANCE.registerMessage(id++, SyncSkillRemovePacket.class, SyncSkillRemovePacket::encoding, SyncSkillRemovePacket::decoding, SyncSkillRemovePacket::handle);
        INSTANCE.registerMessage(id++, SyncEffectAddPacket.class, SyncEffectAddPacket::encoding, SyncEffectAddPacket::decoding, SyncEffectAddPacket::handle);
        INSTANCE.registerMessage(id++, SyncEffectRemovePacket.class, SyncEffectRemovePacket::encoding, SyncEffectRemovePacket::decoding, SyncEffectRemovePacket::handle);
        INSTANCE.registerMessage(id++, RequestSkillUsePacket.class, RequestSkillUsePacket::encoding, RequestSkillUsePacket::decoding, RequestSkillUsePacket::handle);
        GSLChannel.INSTANCE = INSTANCE;
    }
}
