package cinnamon.gsl.api.helper;

import cinnamon.gsl.api.GSLCapabilities;
import cinnamon.gsl.api.GSLChannel;
import cinnamon.gsl.api.capability.data.Affected;
import cinnamon.gsl.api.registry.Effect;
import cinnamon.gsl.api.registry.Skill;
import cinnamon.gsl.api.registry.data.SerDes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class GSLHelper {

    public static <T extends SerDes> void triggerSkillOn(LivingEntity entity, Skill<T> skill) {
        GSLCapabilities.skill(entity).ifPresent(c -> {
            var skilled = c.skills.get(skill);
            if (skilled != null)
                //noinspection unchecked
                skill.use(entity, (T) skilled.context);
        });
    }

    public static <T extends SerDes> void triggerSkillOn(ItemStack itemStack, LivingEntity entity, Skill<T> skill) {
        GSLCapabilities.skill(itemStack).ifPresent(c -> {
            var skilled = c.skills.get(skill);
            if (skilled != null)
                //noinspection unchecked
                skill.use(entity, (T) skilled.context);
        });
    }

    public static <T extends SerDes> void applySkillOn(LivingEntity entity, Skill<T> skill) {
        GSLCapabilities.skill(entity).ifPresent(c -> {
            c.add(skill);
        });
    }

    public static <T extends SerDes> void unapplySkillOn(LivingEntity entity, Skill<T> skill) {
        GSLCapabilities.skill(entity).ifPresent(c -> {
            c.remove(skill);
        });
    }

    public static void applyEffectOn(LivingEntity entity, Affected affected) {
        if(!entity.level.isClientSide()) {
            GSLChannel.sendEffectAddSync(entity, affected);
        }
        GSLCapabilities.effect(entity).filter(c -> c.queueAdd.add(affected));
    }

    public static void unapplyEffectOn(LivingEntity entity, Affected affected) {
        if(!entity.level.isClientSide()) {
            GSLChannel.sendEffectRemoveSync(entity, affected);
        }
        GSLCapabilities.effect(entity).filter(c -> c.queueRemove.add(affected));
    }

    public static void unapplyEffectWithIdOn(LivingEntity entity, String id) {
        GSLCapabilities.effect(entity).map(c -> c.active.get(id)).ifPresent(affected -> {
            unapplyEffectOn(entity, affected);
        });
    }

    public static boolean isEffectWithIdOn(LivingEntity entity, String id) {
        return GSLCapabilities.effect(entity).map(c -> c.active.containsKey(id)).orElse(false);
    }

    public static boolean isEffectOn(LivingEntity entity, Affected affected) {
        return GSLCapabilities.effect(entity).map(c -> c.active.containsKey(affected.id)).orElse(false);
    }

    public static boolean isSkillOn(LivingEntity entity, Skill<?> skill) {
        return GSLCapabilities.skill(entity).map(c -> c.skills.containsKey(skill)).orElse(false);
    }

    public static <T extends Effect> T oneEffectWithIdOn(LivingEntity entity, String id) {
        //noinspection unchecked
        return (T) GSLCapabilities.effect(entity).map(c -> c.active.get(id)).map(p -> p.behaviorContext.effect).orElse(null);
    }
}
