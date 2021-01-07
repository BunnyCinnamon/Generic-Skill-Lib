package arekkuusu.gsl.api.capability;

import arekkuusu.gsl.GSL;
import arekkuusu.gsl.api.GSLCapabilities;
import arekkuusu.gsl.api.capability.data.Skilled;
import arekkuusu.gsl.api.registry.Skill;
import arekkuusu.gsl.api.util.NBTHelper;
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
import java.util.Map;
import java.util.function.Consumer;

public class SkilledCapability implements ICapabilitySerializable<CompoundNBT>, Capability.IStorage<SkilledCapability> {

    public static void init() {
        CapabilityManager.INSTANCE.register(SkilledCapability.class, new SkilledCapability(), SkilledCapability::new);
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
    public CompoundNBT serializeNBT() {
        return (CompoundNBT) GSLCapabilities.SKILLED_ENTITY.getStorage().writeNBT(GSLCapabilities.SKILLED_ENTITY, this, null);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        GSLCapabilities.SKILLED_ENTITY.getStorage().readNBT(GSLCapabilities.SKILLED_ENTITY, this, null, nbt);
    }

    //** NBT **//

    @Nullable
    @Override
    public INBT writeNBT(Capability<SkilledCapability> capability, SkilledCapability instance, Direction side) {
        CompoundNBT tag = new CompoundNBT();
        save(tag, "skills", instance.skills.values());
        return tag;
    }

    private void save(CompoundNBT tag, String name, Collection<Skilled> collection) {
        ListNBT list = new ListNBT();
        for (Skilled value : collection) {
            CompoundNBT nbt = new CompoundNBT();
            NBTHelper.setRegistry(nbt, "skill", value.skill);
            NBTHelper.setNBT(nbt, "context", value.context.serializeNBT());
            list.add(nbt);
        }
        tag.put(name, list);
    }

    @Override
    public void readNBT(Capability<SkilledCapability> capability, SkilledCapability instance, Direction side, INBT nbt) {
        CompoundNBT tag = (CompoundNBT) nbt;
        instance.skills.clear();
        load(tag, "skills", v -> instance.skills.put(v.skill, v));
    }

    private void load(CompoundNBT tag, String name, Consumer<Skilled> consumer) {
        ListNBT list = NBTHelper.getNBTList(tag, name);
        for (int i = 0; i < list.size(); i++) {
            CompoundNBT nbt = list.getCompound(i);
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
                event.addCapability(KEY, GSLCapabilities.SKILLED_ENTITY.getDefaultInstance());
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
