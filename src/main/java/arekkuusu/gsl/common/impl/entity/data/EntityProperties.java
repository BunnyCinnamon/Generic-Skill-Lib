package arekkuusu.gsl.common.impl.entity.data;

import arekkuusu.gsl.api.GSLRegistries;
import arekkuusu.gsl.api.capability.data.Affected;
import arekkuusu.gsl.api.helper.NBTHelper;
import arekkuusu.gsl.api.helper.TeamHelper;
import arekkuusu.gsl.api.registry.data.BehaviorContext;
import arekkuusu.gsl.common.impl.entity.Strategic;
import arekkuusu.gsl.common.impl.entity.StrategicDimensions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Set;

public class EntityProperties<T extends Strategic> {
    private final Set<EntityBehavior<? extends Strategic>> behaviors = Sets.newHashSet();
    private final List<Affected> effects = Lists.newArrayList();
    private StrategicDimensions.Type dimensionsType = StrategicDimensions.Type.CENTER;
    private TeamHelper.TeamSelector teamSelector = TeamHelper.TeamSelector.ANY;
    private float widthInitial;
    private float heightInitial;
    private float widthFinal;
    private float heightFinal;
    private int growthDelay;
    private int duration;
    
    public Set<EntityBehavior<? extends Strategic>> getBehaviors() {
        return behaviors;
    }

    public void setBehavior(EntityBehavior<T> behavior) {
        this.behaviors.add(behavior);
    }

    public StrategicDimensions.Type getDimensionsType() {
        return dimensionsType;
    }

    public void setDimensionsType(StrategicDimensions.Type type) {
        this.dimensionsType = type;
    }

    public TeamHelper.TeamSelector getTeamSelector() {
        return teamSelector;
    }

    public void setTeamSelector(TeamHelper.TeamSelector teamSelector) {
        this.teamSelector = teamSelector;
    }

    public float getWidthInitial() {
        return widthInitial;
    }

    public void setWidthInitial(float widthInitial) {
        this.widthInitial = widthInitial;
    }

    public float getHeightInitial() {
        return heightInitial;
    }

    public void setHeightInitial(float heightInitial) {
        this.heightInitial = heightInitial;
    }

    public float getWidthFinal() {
        return widthFinal;
    }

    public void setWidthFinal(float widthFinal) {
        this.widthFinal = widthFinal;
    }

    public float getHeightFinal() {
        return heightFinal;
    }

    public void setHeightFinal(float heightFinal) {
        this.heightFinal = heightFinal;
    }

    public int getGrowthDelay() {
        return growthDelay;
    }

    public void setGrowthDelay(int growthDelay) {
        this.growthDelay = growthDelay;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<Affected> getEffects() {
        return effects;
    }

    public void setEffect(Affected affected) {
        this.effects.add(affected);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        this.setDuration(pCompound.getInt("Duration"));
        this.setGrowthDelay(pCompound.getInt("GrowthDelay"));
        this.setWidthInitial(pCompound.getFloat("Width"));
        this.setHeightInitial(pCompound.getFloat("Height"));
        this.setWidthFinal(pCompound.getFloat("mWidth"));
        this.setHeightFinal(pCompound.getFloat("mHeight"));
        this.setTeamSelector(NBTHelper.getEnum(TeamHelper.TeamSelector.class, pCompound, "TeamSelector"));
        this.setDimensionsType(NBTHelper.getEnum(StrategicDimensions.Type.class, pCompound, "DimensionsType"));
        if (pCompound.contains("Behaviors", 9)) {
            ListTag listtag = pCompound.getList("Behaviors", Tag.TAG_COMPOUND);
            this.getBehaviors().clear();

            for (int i = 0; i < listtag.size(); ++i) {
                CompoundTag tag = listtag.getCompound(i);
                setBehavior((EntityBehavior<T>) EntityBehaviorInstances.ENTRIES.get(tag.getInt("Strategy")));
            }
        }

        if (pCompound.contains("Effects", 9)) {
            ListTag listtag = pCompound.getList("Effects", Tag.TAG_COMPOUND);
            this.effects.clear();

            for (int i = 0; i < listtag.size(); ++i) {
                CompoundTag tag = listtag.getCompound(i);
                Affected affected = new Affected();
                affected.id = tag.getString("Id");
                affected.behavior = GSLRegistries.BEHAVIOR_TYPES.getValue(new ResourceLocation(tag.getString("Resource"))).create();
                affected.behavior.deserializeNBT(tag.getCompound("Behavior"));
                affected.behaviorContext = new BehaviorContext();
                affected.behaviorContext.deserializeNBT(tag.getCompound("BehaviorContext"));
                this.setEffect(affected);
            }
        }
    }
    
    public void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putInt("Duration", this.getDuration());
        pCompound.putInt("GrowthDelay", this.getGrowthDelay());
        pCompound.putFloat("Width", this.getWidthInitial());
        pCompound.putFloat("Height", this.getHeightInitial());
        pCompound.putFloat("mWidth", this.getWidthFinal());
        pCompound.putFloat("mHeight", this.getHeightFinal());
        NBTHelper.setEnum(pCompound, "TeamSelector", this.getTeamSelector());
        NBTHelper.setEnum(pCompound, "DimensionsType", this.getDimensionsType());

        if (!getBehaviors().isEmpty()) {
            ListTag listtag = new ListTag();
            for (EntityBehavior<?> strategy : getBehaviors()) {
                CompoundTag tag = new CompoundTag();
                tag.putInt("Id", strategy.getId());
                listtag.add(tag);
            }
            pCompound.put("Behaviors", listtag);
        }

        if (!this.effects.isEmpty()) {
            ListTag listtag = new ListTag();

            for (Affected affected : this.effects) {
                CompoundTag tag = new CompoundTag();
                tag.putString("Id", affected.id);
                tag.putString("Resource", affected.behavior.getType().getRegistryName().toString());
                tag.put("Behavior", affected.behavior.serializeNBT());
                tag.put("BehaviorContext", affected.behaviorContext.serializeNBT());
                listtag.add(tag);
            }

            pCompound.put("Effects", listtag);
        }
    }
}
