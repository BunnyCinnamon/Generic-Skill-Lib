package arekkuusu.gsl.api.capability;

import arekkuusu.gsl.GSL;
import arekkuusu.gsl.api.GSLCapabilities;
import arekkuusu.gsl.api.capability.data.Skilled;
import arekkuusu.gsl.api.registry.Skill;
import arekkuusu.gsl.api.util.NBTHelper;
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
import java.util.Map;
import java.util.function.Consumer;

public class SkilledCapability implements ICapabilitySerializable<CompoundTag> {

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new Handler());
    }

    public final Map<Skill<?>, Skilled> skills = Maps.newLinkedHashMap();

    public void add(Skill<?> skill) {
        Skilled skilled = new Skilled();
        skilled.skill = skill;
        skilled.context = skilled.skill.create();
        this.skills.put(skill, skilled);
    }

    public void remove(Skill<?> skill) {
        this.skills.remove(skill);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return GSLCapabilities.SKILLED_ENTITY.orEmpty(cap, LazyOptional.of(() -> this));
    }

    @Override
    @Nonnull
    public CompoundTag serializeNBT() {
        return writeNBT(this);
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        readNBT(this, nbt);
    }

    //** NBT **//

    public CompoundTag writeNBT(SkilledCapability instance) {
        CompoundTag tag = new CompoundTag();
        save(tag, "skills", instance.skills.values());
        return tag;
    }

    private void save(CompoundTag tag, String name, Collection<Skilled> collection) {
        var list = new ListTag();
        for (Skilled value : collection) {
            CompoundTag nbt = new CompoundTag();
            NBTHelper.setRegistry(nbt, "skill", value.skill);
            NBTHelper.setNBT(nbt, "context", value.context.serializeNBT());
            list.add(nbt);
        }
        tag.put(name, list);
    }

    public void readNBT(SkilledCapability instance, CompoundTag tag) {
        instance.skills.clear();
        load(tag, "skills", v -> instance.skills.put(v.skill, v));
    }

    private void load(CompoundTag tag, String name, Consumer<Skilled> consumer) {
        var list = NBTHelper.getNBTList(tag, name);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag nbt = list.getCompound(i);
            Skilled skilled = new Skilled();
            skilled.skill = NBTHelper.getRegistry(nbt, "skill", Skill.class);
            skilled.context = skilled.skill.create();
            skilled.context.deserializeNBT(NBTHelper.getNBTTag(nbt, "context"));
            consumer.accept(skilled);
        }
    }

    public static class Handler {
        private static final ResourceLocation KEY = new ResourceLocation(GSL.ID, "skilled");

        @SubscribeEvent
        public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof LivingEntity)
                event.addCapability(KEY, new SkilledCapability());
        }

        @SubscribeEvent
        public void clonePlayer(PlayerEvent.Clone event) {
            event.getPlayer().getCapability(GSLCapabilities.SKILLED_ENTITY, null).ifPresent(first -> {
                event.getOriginal().getCapability(GSLCapabilities.SKILLED_ENTITY, null).ifPresent(second -> {
                    first.deserializeNBT(second.serializeNBT());
                });
            });
        }
    }
}
