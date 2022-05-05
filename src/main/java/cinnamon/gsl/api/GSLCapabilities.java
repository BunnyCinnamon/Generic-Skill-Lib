package cinnamon.gsl.api;

import cinnamon.gsl.api.capability.AffectedCapability;
import cinnamon.gsl.api.capability.SkilledCapability;
import cinnamon.gsl.api.util.EmptyImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public final class GSLCapabilities {

    @CapabilityInject(SkilledCapability.class)
    public static final Capability<SkilledCapability> SKILLED = EmptyImpl.mkEmpty();
    @CapabilityInject(AffectedCapability.class)
    public static final Capability<AffectedCapability> AFFECTED = EmptyImpl.mkEmpty();

    public static LazyOptional<SkilledCapability> skill(@Nullable Entity entity) {
        return entity != null ? entity.getCapability(SKILLED, null) : LazyOptional.empty();
    }

    public static LazyOptional<SkilledCapability> skill(@Nullable ItemStack itemStack) {
        return itemStack != null ? itemStack.getCapability(SKILLED, null) : LazyOptional.empty();
    }

    public static LazyOptional<AffectedCapability> effect(@Nullable Entity entity) {
        return entity != null ? entity.getCapability(AFFECTED, null) : LazyOptional.empty();
    }

    public static LazyOptional<AffectedCapability> effect(@Nullable ItemStack itemStack) {
        return itemStack != null ? itemStack.getCapability(AFFECTED, null) : LazyOptional.empty();
    }
}
