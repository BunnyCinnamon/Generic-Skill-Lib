package arekkuusu.gsl.api.capability;

import arekkuusu.gsl.GSL;
import arekkuusu.gsl.api.GSLCapabilities;
import arekkuusu.gsl.api.capability.data.Affected;
import arekkuusu.gsl.api.registry.BehaviorType;
import arekkuusu.gsl.api.registry.data.BehaviorContext;
import arekkuusu.gsl.api.util.NBTHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
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

public class AffectedCapability implements ICapabilitySerializable<CompoundTag> {

    public static void init() {
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
    public CompoundTag serializeNBT() {
        return writeNBT(this);
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        readNBT(this, nbt);
    }

    //** NBT **//

    public CompoundTag writeNBT(AffectedCapability instance) {
        CompoundTag tag = new CompoundTag();
        save(tag, "queueRemove", instance.queueRemove);
        save(tag, "queueAdd", instance.queueAdd);
        save(tag, "active", instance.active.values());
        return tag;
    }

    private void save(CompoundTag tag, String name, Collection<Affected> collection) {
        var list = new ListTag();
        for (Affected value : collection) {
            CompoundTag nbt = new CompoundTag();
            NBTHelper.putString(nbt, "id", value.id);
            NBTHelper.setRegistry(nbt, "type", value.behavior.getType());
            NBTHelper.setNBT(nbt, "behavior", value.behavior.serializeNBT());
            NBTHelper.setNBT(nbt, "context", value.behaviorContext.serializeNBT());
            list.add(nbt);
        }
        tag.put(name, list);
    }

    public void readNBT(AffectedCapability instance, CompoundTag tag) {
        instance.queueRemove.clear();
        instance.queueAdd.clear();
        instance.active.clear();
        load(tag, "queueRemove", instance.queueRemove::add);
        load(tag, "queueAdd", instance.queueAdd::add);
        load(tag, "active", affected -> instance.active.put(affected.id, affected));
    }

    private void load(CompoundTag tag, String name, Consumer<Affected> consumer) {
        var list = NBTHelper.getNBTList(tag, name);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag nbt = list.getCompound(i);
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
                event.addCapability(KEY, new AffectedCapability());
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
