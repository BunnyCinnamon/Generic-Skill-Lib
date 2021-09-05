package arekkuusu.gsl.api;

import arekkuusu.gsl.api.util.ExpressionHelper;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;

import java.util.Map;

public class GSLAPI {

    public static final Map<ResourceLocation, Object2ObjectMap<String, ExpressionHelper.Expression>> EXPRESSION_FUNCTION_CACHE = Maps.newHashMap();
    public static final Map<Tuple<ResourceLocation, String>, Int2DoubleArrayMap> EXPRESSION_CACHE = Maps.newHashMap();
    public static boolean defaultHumanTeam = true;
    public static boolean defaultAnimalTeam = false;
}
