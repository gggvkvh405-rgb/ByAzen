/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1959
 *  net.minecraft.class_1972
 *  net.minecraft.class_310
 *  net.minecraft.class_6862
 *  net.minecraft.class_6908
 *  net.minecraft.class_746
 */
package ru.byazen.animation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.ToDoubleFunction;
import net.minecraft.class_1959;
import net.minecraft.class_1972;
import net.minecraft.class_310;
import net.minecraft.class_6862;
import net.minecraft.class_6908;
import net.minecraft.class_746;
import ru.byazen.misc.AnimationQuery;

public final class AnimationQueryRegistry {
    private static final Map<String, AnimationQuery> QUERIES = new LinkedHashMap<String, AnimationQuery>();

    private AnimationQueryRegistry() {
    }

    public static void registerDefaults() {
        if (!QUERIES.isEmpty()) {
            return;
        }
        AnimationQueryRegistry.register("byazen.sigmoid", 3, values -> AnimationQueryRegistry.sigmoid(values[0], values[1], values[2]));
        AnimationQueryRegistry.register("byazen.sigmoid_swing", 3, values -> AnimationQueryRegistry.sigmoid(values[2], values[0], values[1]));
        AnimationQueryRegistry.register("byazen.movewave_sin", 3, values -> AnimationQueryRegistry.movementWave(values, true));
        AnimationQueryRegistry.register("byazen.movewave_cos", 3, values -> AnimationQueryRegistry.movementWave(values, false));
        AnimationQueryRegistry.register("byazen.movewave", 3, values -> AnimationQueryRegistry.movementWave(values, true));
        AnimationQueryRegistry.register("byazen.linear_wave", 1, values -> AnimationQueryRegistry.triangleWave(values[0] + (((double[])values).length > 1 ? values[1] : 0.0)));
        AnimationQueryRegistry.register("byazen.lopsided_wave", 2, values -> AnimationQueryRegistry.lopsidedWave(values[0], values[1]));
        AnimationQueryRegistry.register("byazen.speed_vibration", 1, values -> values[0] * AnimationQueryRegistry.movementSpeed() * Math.sin(Math.toRadians(AnimationQueryRegistry.animationTime() * 1440.0)));
        AnimationQueryRegistry.register("byazen.interval", 2, values -> AnimationQueryRegistry.interval(values[0], values[1]));
        AnimationQueryRegistry.register("byazen.smoothclamp", 4, values -> AnimationQueryRegistry.smoothClamp(values[0], values[1], values[2], values[3]));
        AnimationQueryRegistry.register("byazen.smoothmin", 3, values -> AnimationQueryRegistry.smoothMinimum(values[0], values[1], values[2]));
        AnimationQueryRegistry.register("byazen.easeinback", 1, values -> AnimationQueryRegistry.easeInBack(values[0]));
        AnimationQueryRegistry.register("byazen.easeoutback", 1, values -> AnimationQueryRegistry.easeOutBack(values[0]));
        AnimationQueryRegistry.register("byazen.easeinoutback", 1, values -> AnimationQueryRegistry.easeInOutBack(values[0]));
        AnimationQueryRegistry.register("byazen.easeinoutquad", 1, values -> AnimationQueryRegistry.easeInOutPower(values[0], 2.0));
        AnimationQueryRegistry.register("byazen.easeinoutquint", 1, values -> AnimationQueryRegistry.easeInOutPower(values[0], 5.0));
        AnimationQueryRegistry.register("byazen.easeinoutsine", 1, values -> AnimationQueryRegistry.easeInOutSine(values[0]));
        AnimationQueryRegistry.register("byazen.random_integer", 2, values -> Math.floor(values[0] + Math.random() * (values[1] - values[0] + 1.0)));
        AnimationQueryRegistry.register("byazen.set_tick_step_value", 1, values -> values[0]);
        AnimationQueryRegistry.register("byazen.check_for_desert", 0, values -> AnimationQueryRegistry.isDesert());
        AnimationQueryRegistry.register("byazen.check_for_ocean", 0, values -> AnimationQueryRegistry.isInBiome((class_6862<class_1959>)class_6908.field_36509));
        AnimationQueryRegistry.register("byazen.check_for_nether", 0, values -> AnimationQueryRegistry.isInBiome((class_6862<class_1959>)class_6908.field_36518));
        AnimationQueryRegistry.register("byazen.check_for_theend", 0, values -> AnimationQueryRegistry.isInBiome((class_6862<class_1959>)class_6908.field_37394));
        AnimationQueryRegistry.register("byazen.check_for_biome", 1, values -> 0.0);
        AnimationQueryRegistry.register("Math.sin", 1, values -> Math.sin(Math.toRadians(values[0])));
        AnimationQueryRegistry.register("Math.cos", 1, values -> Math.cos(Math.toRadians(values[0])));
        AnimationQueryRegistry.register("bmath.cos", 1, values -> Math.cos(Math.toRadians(values[0])));
        AnimationQueryRegistry.register("query.is_crouching_smooth_easeoutsine", 1, values -> AnimationQueryRegistry.easeOutSine(AnimationQueryRegistry.crouching()));
        AnimationQueryRegistry.register("query.is_crouching_smooth_easeoutexpo", 1, values -> AnimationQueryRegistry.easeOutExpo(AnimationQueryRegistry.crouching()));
        AnimationQueryRegistry.register("query.is_in_water_smooth", 1, values -> AnimationQueryRegistry.inWater());
        AnimationQueryRegistry.register("query.is_in_water_smooth_easeoutsine", 1, values -> AnimationQueryRegistry.easeOutSine(AnimationQueryRegistry.inWater()));
        AnimationQueryRegistry.register("query.is_in_water_smooth_easeoutback", 2, values -> AnimationQueryRegistry.easeOutBack(AnimationQueryRegistry.inWater()));
        AnimationQueryRegistry.register("query.is_on_ground_smooth", 1, values -> AnimationQueryRegistry.onGround());
        AnimationQueryRegistry.register("query.is_on_ground_smooth_easeoutexpo", 1, values -> AnimationQueryRegistry.easeOutExpo(AnimationQueryRegistry.onGround()));
        AnimationQueryRegistry.register("query.body_x_rotation_smooth_easeinoutsine", 1, values -> AnimationQueryRegistry.easeInOutSine(AnimationQueryRegistry.bodyPitchFraction()));
    }

    public static Map<String, AnimationQuery> queries() {
        AnimationQueryRegistry.registerDefaults();
        return Map.copyOf(QUERIES);
    }

    private static void register(String name, int argumentCount, ToDoubleFunction<double[]> function) {
        QUERIES.putIfAbsent(name, new AnimationQuery(argumentCount, function));
    }

    private static class_746 player() {
        return class_310.method_1551().field_1724;
    }

    private static double animationTime() {
        class_310 client = class_310.method_1551();
        if (client.field_1687 == null) {
            return 0.0;
        }
        return (float)client.field_1687.method_75260() + client.method_61966().method_60637(false);
    }

    private static double movementSpeed() {
        class_746 player = AnimationQueryRegistry.player();
        if (player == null) {
            return 0.0;
        }
        return Math.min(1.0, player.method_18798().method_37267() * 4.0);
    }

    private static double crouching() {
        class_746 player = AnimationQueryRegistry.player();
        return player != null && player.method_5715() ? 1.0 : 0.0;
    }

    private static double inWater() {
        class_746 player = AnimationQueryRegistry.player();
        return player != null && player.method_5799() ? 1.0 : 0.0;
    }

    private static double onGround() {
        class_746 player = AnimationQueryRegistry.player();
        return player != null && player.method_24828() ? 1.0 : 0.0;
    }

    private static double bodyPitchFraction() {
        class_746 player = AnimationQueryRegistry.player();
        return player == null ? 0.0 : Math.clamp(((double)player.method_36455() + 90.0) / 180.0, 0.0, 1.0);
    }

    private static double isInBiome(class_6862<class_1959> tag) {
        class_310 client = class_310.method_1551();
        class_746 player = client.field_1724;
        return client.field_1687 != null && player != null && client.field_1687.method_23753(player.method_24515()).method_40220(tag) ? 1.0 : 0.0;
    }

    private static double isDesert() {
        class_310 client = class_310.method_1551();
        class_746 player = client.field_1724;
        return client.field_1687 != null && player != null && client.field_1687.method_23753(player.method_24515()).method_40225(class_1972.field_9424) ? 1.0 : 0.0;
    }

    private static double interval(double period, double activeFraction) {
        if (period <= 0.0) {
            return 0.0;
        }
        double position = AnimationQueryRegistry.animationTime() % period;
        return position < period * activeFraction ? 1.0 : 0.0;
    }

    private static double movementWave(double[] values, boolean sine) {
        double angle = AnimationQueryRegistry.playerYaw() * values[0] + values[2];
        double wave = sine ? Math.sin(Math.toRadians(angle)) : Math.cos(Math.toRadians(angle));
        return wave * values[1] * AnimationQueryRegistry.movementSpeed();
    }

    private static double playerYaw() {
        class_746 player = AnimationQueryRegistry.player();
        return player == null ? 0.0 : (double)player.method_73188();
    }

    private static double lopsidedWave(double angle, double peakPercent) {
        double phase = (angle % 360.0 + 360.0) % 360.0 / 360.0;
        double split = Math.clamp(peakPercent / 100.0, 0.01, 0.99);
        double normalized = phase < split ? phase / split * 0.5 : 0.5 + (phase - split) / (1.0 - split) * 0.5;
        return Math.sin(normalized * Math.PI * 2.0);
    }

    private static double triangleWave(double angle) {
        double phase = (angle % 360.0 + 360.0) % 360.0 / 360.0;
        return phase < 0.5 ? phase * 2.0 : 2.0 - phase * 2.0;
    }

    private static double smoothClamp(double value, double minimum, double maximum, double smoothing) {
        if (smoothing <= 0.0) {
            return Math.clamp(value, minimum, maximum);
        }
        return AnimationQueryRegistry.smoothMinimum(-AnimationQueryRegistry.smoothMinimum(-value, -minimum, smoothing), maximum, smoothing);
    }

    private static double smoothMinimum(double first, double second, double smoothing) {
        if (smoothing <= 0.0) {
            return Math.min(first, second);
        }
        double factor = Math.clamp(0.5 + 0.5 * (second - first) / smoothing, 0.0, 1.0);
        return first * factor + second * (1.0 - factor) - smoothing * factor * (1.0 - factor);
    }

    private static double easeOutSine(double value) {
        return Math.sin(value * Math.PI / 2.0);
    }

    private static double easeOutExpo(double value) {
        return value >= 1.0 ? 1.0 : 1.0 - Math.pow(2.0, -10.0 * value);
    }

    private static double easeInOutSine(double value) {
        return -(Math.cos(Math.PI * value) - 1.0) / 2.0;
    }

    private static double easeInBack(double value) {
        return value * value * (2.70158 * value - 1.70158);
    }

    private static double easeOutBack(double value) {
        double shifted = value - 1.0;
        return 1.0 + shifted * shifted * (2.70158 * shifted + 1.70158);
    }

    private static double easeInOutBack(double value) {
        double factor = 2.5949095;
        return value < 0.5 ? Math.pow(2.0 * value, 2.0) * ((factor + 1.0) * 2.0 * value - factor) / 2.0 : (Math.pow(2.0 * value - 2.0, 2.0) * ((factor + 1.0) * (2.0 * value - 2.0) + factor) + 2.0) / 2.0;
    }

    private static double easeInOutPower(double value, double power) {
        return value < 0.5 ? Math.pow(2.0, power - 1.0) * Math.pow(value, power) : 1.0 - Math.pow(-2.0 * value + 2.0, power) / 2.0;
    }

    private static double sigmoid(double value, double amplitude, double steepness) {
        return amplitude * (2.0 / (1.0 + Math.exp(-steepness * value)) - 1.0);
    }
}

