package rtx.kimiko.api.mods.geckolib.loading.math.function.generic;
import rtx.kimiko.api.mods.geckolib.animation.state.ControllerState;
import rtx.kimiko.api.mods.geckolib.loading.math.MathValue;
import rtx.kimiko.api.mods.geckolib.loading.math.function.MathFunction;

public final class SinFunction
extends MathFunction {
    private final MathValue value;

    public SinFunction(MathValue ... mathValueArray) {
        super(mathValueArray);
        this.value = mathValueArray[0];
    }

    @Override
    public String getName() {
        return "math.sin";
    }

    @Override
    public double compute(ControllerState controllerState) {
        return Math.sin(this.value.get(controllerState) * 0.01745329238474369);
    }

    @Override
    public MathValue[] getArgs() {
        return new MathValue[]{this.value};
    }

    @Override
    public int getMinArgs() {
        return 1;
    }
}

