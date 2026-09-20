/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_243
 */
package ru.wexside.prediction;

import java.util.List;
import net.minecraft.class_243;
import ru.wexside.prediction.ProjectileImpact;

public record TrajectoryPrediction(List<class_243> points, ProjectileImpact impact, int flightTicks) {
}

