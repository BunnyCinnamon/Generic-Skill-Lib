package arekkuusu.gsl.api.util;

import arekkuusu.gsl.api.GSLCapabilities;
import arekkuusu.gsl.api.capability.data.Affected;
import arekkuusu.gsl.api.capability.data.Skilled;
import arekkuusu.gsl.api.registry.Skill;
import arekkuusu.gsl.api.registry.data.SerDes;
import net.minecraft.entity.LivingEntity;

public final class GSLHelper {

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

    public static <T extends SerDes> void triggerSkillOn(LivingEntity entity, Skill<T> skill) {
        GSLCapabilities.skill(entity).ifPresent(c -> {
            Skilled skilled = c.skills.get(skill);
            if (skilled != null)
                //noinspection unchecked
                skill.use(entity, (T) skilled.context);
        });
    }

    public static void applyEffectOn(LivingEntity entity, Affected affected) {
        GSLCapabilities.effect(entity).filter(c -> c.queueAdd.add(affected));
    }

    public static void unapplyEffectOn(LivingEntity entity, Affected affected) {
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
}
