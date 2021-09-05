package arekkuusu.gsl.api.util;

import arekkuusu.gsl.api.GSLAPI;
import com.expression.parser.Parser;
import com.expression.parser.util.ParserResult;
import com.expression.parser.util.Point;
import it.unimi.dsi.fastutil.ints.Int2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ExpressionHelper {

    public static final Function<String, Expression> EXPRESSION_PARSER_SUPPLIER = ExpressionHelper::parse;
    public static final Function<ResourceLocation, Object2ObjectArrayMap<String, Expression>> EXPRESSION_FUNCTION_CACHE_SUPPLIER = (s) -> new Object2ObjectArrayMap<>();
    public static final Function<Tuple<ResourceLocation, String>, Int2DoubleArrayMap> EXPRESSION_CACHE_SUPPLIER = (s) -> new Int2DoubleArrayMap();
    public static final String EXPRESSION_REGEX = "^([\\[(])(\\d+ to \\d+|\\d+)([])]) -> f\\(([a-z#,]*)\\) = (.+)$";

    public static double evaluate(ResourceLocation location, String[] functions, int index, double... values) {
        double evaluationResult = 0D;
        Object2ObjectMap<String, Expression> expressionMap = GSLAPI.EXPRESSION_FUNCTION_CACHE.computeIfAbsent(location, ExpressionHelper.EXPRESSION_FUNCTION_CACHE_SUPPLIER);
        for (String function : functions) {
            Int2DoubleArrayMap map = GSLAPI.EXPRESSION_CACHE.computeIfAbsent(new Tuple<>(location, function), ExpressionHelper.EXPRESSION_CACHE_SUPPLIER);
            Expression expression = expressionMap.computeIfAbsent(function, EXPRESSION_PARSER_SUPPLIER);
            if(expression.matches(index)) {
                evaluationResult = map.computeIfAbsent(index, (i) -> evaluate(expression, values));
            }
        }
        return evaluationResult;
    }

    public static double evaluate(Expression expression, double... values) {
        Point[] points = new Point[expression.values.length];
        for (int i = 0; i < expression.values.length; i++) {
            points[i] = new Point(expression.values[i], String.valueOf(values[i]));
        }
        ParserResult parserResult = Parser.eval(expression.function, points);
        return parserResult.getValue();
    }

    private static Expression parse(String expression) {
        Pattern pattern = Pattern.compile(ExpressionHelper.EXPRESSION_REGEX);
        Matcher matcher = pattern.matcher(expression.trim());
        Expression.Bound lower = matcher.group(1).equals("[") ? Expression.Bound.INCLUDE : Expression.Bound.EXCLUDE;
        Expression.Bound upper = matcher.group(3).equals("]") ? Expression.Bound.INCLUDE : Expression.Bound.EXCLUDE;
        String indexesString = matcher.group(2);
        int lowerIndex;
        int upperIndex;
        if (indexesString.contains("to")) {
            String[] indexesSplitString = indexesString.split(" to ");
            lowerIndex = Integer.parseInt(indexesSplitString[0]);
            upperIndex = Integer.parseInt(indexesSplitString[1]);
        } else {
            upperIndex = lowerIndex = Integer.parseInt(indexesString);
        }
        String[] values = matcher.group(4).split(",");
        String function = matcher.group(5);
        return new Expression(lower, lowerIndex, upper, upperIndex, values, function);
    }

    public static class Expression {

        public final Bound lower;
        public final int lowerIndex;
        public final Bound upper;
        public final int upperIndex;
        public final String[] values;
        public final String function;

        public Expression(Bound lower, int lowerIndex, Bound upper, int upperIndex, String[] values, String function) {
            this.lower = lower;
            this.lowerIndex = lowerIndex;
            this.upper = upper;
            this.upperIndex = upperIndex;
            this.values = values;
            this.function = function;
        }

        public boolean matches(int index) {
            boolean leftMatch = lower == Bound.EXCLUDE ? index < lowerIndex : index <= lowerIndex;
            boolean rightMatch = upper == Bound.EXCLUDE ? index > upperIndex : index >= upperIndex;
            return leftMatch && rightMatch;
        }

        public enum Bound {
            INCLUDE,
            EXCLUDE
        }
    }
}
