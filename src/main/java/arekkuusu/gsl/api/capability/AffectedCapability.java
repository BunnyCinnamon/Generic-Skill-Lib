package arekkuusu.gsl.api.capability;

import arekkuusu.gsl.GSL;
import arekkuusu.gsl.api.GSLCapabilities;
import arekkuusu.gsl.api.capability.data.Affected;
import arekkuusu.gsl.api.registry.BehaviorType;
import arekkuusu.gsl.api.registry.data.BehaviorContext;
import arekkuusu.gsl.api.util.NBTHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AffectedCapability implements ICapabilitySerializable<CompoundNBT>, Capability.IStorage<AffectedCapability> {

    public static void init() {
        CapabilityManager.INSTANCE.register(AffectedCapability.class, new AffectedCapability(), AffectedCapability::new);
        MinecraftForge.EVENT_BUS.register(new Handler());
    }

    public final List<Affected> queueRemove = Lists.newLinkedList();
    public final List<Affected> queueAdd = Lists.newLinkedList();
    public final Map<String, Affected> active = Maps.newHashMap();

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return GSLCapabilities.AFFECTED_ENTITY.orEmpty(cap, LazyOptional.of(() -> this));
    }

    @Nonnull
    @Override
    public CompoundNBT serializeNBT() {
        return (CompoundNBT) GSLCapabilities.AFFECTED_ENTITY.getStorage().writeNBT(GSLCapabilities.AFFECTED_ENTITY, this, null);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        GSLCapabilities.AFFECTED_ENTITY.getStorage().readNBT(GSLCapabilities.AFFECTED_ENTITY, this, null, nbt);
    }

    //** NBT **//

    @Nullable
    @Override
    public INBT writeNBT(Capability<AffectedCapability> capability, AffectedCapability instance, Direction side) {
        CompoundNBT tag = new CompoundNBT();
        save(tag, "queueRemove", instance.queueRemove);
        save(tag, "queueAdd", instance.queueAdd);
        save(tag, "active", instance.active.values());
        return tag;
    }

    private void save(CompoundNBT tag, String name, Collection<Affected> collection) {
        ListNBT list = new ListNBT();
        for (Affected value : collection) {
            CompoundNBT nbt = new CompoundNBT();
            NBTHelper.putString(nbt, "id", value.id);
            NBTHelper.setRegistry(nbt, "type", value.behavior.getType());
            NBTHelper.setNBT(nbt, "behavior", value.behavior.serializeNBT());
            NBTHelper.setNBT(nbt, "context", value.behaviorContext.serializeNBT());
            list.add(nbt);
        }
        tag.put(name, list);
    }

    @Override
    public void readNBT(Capability<AffectedCapability> capability, AffectedCapability instance, Direction side, INBT nbt) {
        CompoundNBT tag = (CompoundNBT) nbt;
        instance.queueRemove.clear();
        instance.queueAdd.clear();
        instance.active.clear();
        load(tag, "queueRemove", instance.queueRemove::add);
        load(tag, "queueAdd", instance.queueAdd::add);
        load(tag, "active", affected -> instance.active.put(affected.id, affected));
    }

    private void load(CompoundNBT tag, String name, Consumer<Affected> consumer) {
        ListNBT list = NBTHelper.getNBTList(tag, name);
        for (int i = 0; i < list.size(); i++) {
            CompoundNBT nbt = list.getCompound(i);
            Affected affected = new Affected();
            affected.id = NBTHelper.getString(nbt, "id");
            affected.behavior = NBTHelper.getRegistry(nbt, "type", BehaviorType.class).create();
            affected.behavior.deserializeNBT(NBTHelper.getNBTTag(nbt, "behavior"));
            affected.behaviorContext = new BehaviorContext(NBTHelper.getNBTTag(nbt, "context"));
            consumer.accept(affected);
        }
    }

    public static class Handler {
        private static final ResourceLocation KEY = new ResourceLocation(GSL.ID, "affected");

        @SubscribeEvent
        public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof LivingEntity)
                event.addCapability(KEY, GSLCapabilities.AFFECTED_ENTITY.getDefaultInstance());
        }

        @SubscribeEvent
        public void clonePlayer(PlayerEvent.Clone event) {
            event.getPlayer().getCapability(GSLCapabilities.AFFECTED_ENTITY, null).ifPresent(first -> {
                event.getOriginal().getCapability(GSLCapabilities.AFFECTED_ENTITY, null).ifPresent(second -> {
                    first.deserializeNBT(second.serializeNBT());
                });
            });
        }
    }
}
