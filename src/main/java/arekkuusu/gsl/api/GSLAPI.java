package arekkuusu.gsl.api;

import arekkuusu.gsl.api.util.Expression;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import it.unimi.dsi.fastutil.ints.Int2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;

import java.util.concurrent.TimeUnit;

public class GSLAPI {

    public static final Cache<ResourceLocation, Object2ObjectMap<String, Expression.FunctionInfo>> EXPRESSION_FUNCTION_CACHE = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();
    public static final Cache<Tuple<ResourceLocation, String>, Int2DoubleArrayMap> EXPRESSION_CACHE = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();
    public static boolean defaultHumanTeam = true;
    public static boolean defaultAnimalTeam = false;
}
