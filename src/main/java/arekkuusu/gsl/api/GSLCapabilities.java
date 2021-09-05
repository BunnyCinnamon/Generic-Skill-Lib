package arekkuusu.gsl.api;

import arekkuusu.gsl.api.capability.AffectedCapability;
import arekkuusu.gsl.api.capability.SkilledCapability;
import arekkuusu.gsl.api.util.EmptyImpl;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public final class GSLCapabilities {

    @CapabilityInject(SkilledCapability.class)
    public static final Capability<SkilledCapability> SKILLED_ENTITY = EmptyImpl.mkEmpty();
    @CapabilityInject(AffectedCapability.class)
    public static final Capability<AffectedCapability> AFFECTED_ENTITY = EmptyImpl.mkEmpty();

    public static LazyOptional<SkilledCapability> skill(@Nullable Entity entity) {
        return entity != null ? entity.getCapability(SKILLED_ENTITY, null) : LazyOptional.empty();
    }

    public static LazyOptional<AffectedCapability> effect(@Nullable Entity entity) {
        return entity != null ? entity.getCapability(AFFECTED_ENTITY, null) : LazyOptional.empty();
    }
}
