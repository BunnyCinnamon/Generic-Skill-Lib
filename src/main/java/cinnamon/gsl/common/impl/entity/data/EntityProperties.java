package cinnamon.gsl.common.impl.entity.data;

import cinnamon.gsl.api.GSLRegistries;
import cinnamon.gsl.api.capability.data.Affected;
import cinnamon.gsl.api.helper.NBTHelper;
import cinnamon.gsl.api.helper.TeamHelper;
import cinnamon.gsl.api.registry.data.BehaviorContext;
import cinnamon.gsl.common.impl.entity.Strategic;
import cinnamon.gsl.common.impl.entity.StrategicDimensions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Set;

public class EntityProperties {
    private final Set<EntityBehavior<? extends Strategic>> behaviors = Sets.newHashSet();
    private final List<Affected> effects = Lists.newArrayList();
    private StrategicDimensions.Type dimensionsType = StrategicDimensions.Type.CENTER;
    private TeamHelper.TeamSelector teamSelector = TeamHelper.TeamSelector.ANY;
    private float widthInitial;
    private float heightInitial;
    private float widthFinal;
    private float heightFinal;
    private int growthDelay;
    private int destroyDelay;
    private int duration;
    
    public Set<EntityBehavior<? extends Strategic>> getBehaviors() {
        return behaviors;
    }

    public void setBehavior(EntityBehavior<? extends Strategic> behavior) {
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

    public int getDestroyDelay() {
        return destroyDelay;
    }

    public void setDestroyDelay(int destroyDelay) {
        this.destroyDelay = destroyDelay;
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
        this.setDestroyDelay(pCompound.getInt("DestroyDelay"));
        this.setWidthInitial(pCompound.getFloat("Width"));
        this.setHeightInitial(pCompound.getFloat("Height"));
        this.setWidthFinal(pCompound.getFloat("mWidth"));
        this.setHeightFinal(pCompound.getFloat("mHeight"));
        this.setTeamSelector(NBTHelper.getEnum(TeamHelper.TeamSelector.class, pCompound, "TeamSelector"));
        this.setDimensionsType(NBTHelper.getEnum(StrategicDimensions.Type.class, pCompound, "DimensionsType"));
        if (pCompound.contains("Behaviors", 9)) {
            var listtag = pCompound.getList("Behaviors", new CompoundTag().getId());
            this.getBehaviors().clear();

            for (int i = 0; i < listtag.size(); ++i) {
                var tag = listtag.getCompound(i);
                setBehavior((EntityBehavior<? extends Strategic>) EntityBehaviorInstances.ENTRIES.get(tag.getInt("Strategy")));
            }
        }

        if (pCompound.contains("Effects", 9)) {
            var listtag = pCompound.getList("Effects", new CompoundTag().getId());
            this.effects.clear();

            for (int i = 0; i < listtag.size(); ++i) {
                var tag = listtag.getCompound(i);
                var affected = new Affected();
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
        pCompound.putInt("DestroyDelay", this.getDestroyDelay());
        pCompound.putFloat("Width", this.getWidthInitial());
        pCompound.putFloat("Height", this.getHeightInitial());
        pCompound.putFloat("mWidth", this.getWidthFinal());
        pCompound.putFloat("mHeight", this.getHeightFinal());
        NBTHelper.putEnum(pCompound, "TeamSelector", this.getTeamSelector());
        NBTHelper.putEnum(pCompound, "DimensionsType", this.getDimensionsType());

        if (!getBehaviors().isEmpty()) {
            var list = new ListTag();
            for (var strategy : getBehaviors()) {
                var tag = new CompoundTag();
                tag.putInt("Id", strategy.getId());
                list.add(tag);
            }
            pCompound.put("Behaviors", list);
        }

        if (!this.effects.isEmpty()) {
            var listtag = new ListTag();

            for (var affected : this.effects) {
                var tag = new CompoundTag();
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
