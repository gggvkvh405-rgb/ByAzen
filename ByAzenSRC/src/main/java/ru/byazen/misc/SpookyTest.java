/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import java.util.HashMap;
import java.util.Map;
import ru.byazen.misc.HumanizedRotationStrategy;
import ru.byazen.misc.RandomizedRotationStrategy;
import ru.byazen.misc.RotationStrategy;
import ru.byazen.misc.RotationStrategyFactory;
import ru.byazen.misc.SmoothRotationStrategy;
import ru.byazen.util.RandomizedDirectRotationStrategy;
import ru.byazen.util.SpookyRotationStrategy;

public final class SpookyTest
implements RotationStrategyFactory {
    private final Map<String, RotationStrategy> field12 = new HashMap<String, RotationStrategy>();

    public void update() {
        this.field12.put("Simple", new SmoothRotationStrategy());
        this.field12.put("FT Snap", new HumanizedRotationStrategy());
        this.field12.put("RW", new RandomizedRotationStrategy());
        this.field12.put("Spooky", new RandomizedDirectRotationStrategy());
        this.field12.put("Spooky Test", new SpookyRotationStrategy());
    }

    @Override
    public RotationStrategy process(String string) {
        return this.field12.getOrDefault(string, this.field12.get("Simple"));
    }
}

