package rtx.byazen.api.mods.geckolib.loading.math.function.round;
import rtx.byazen.api.mods.geckolib.animation.state.ControllerState;
import rtx.byazen.api.mods.geckolib.loading.math.MathValue;
import rtx.byazen.api.mods.geckolib.loading.math.function.MathFunction;

public final class TruncateFunction
extends MathFunction {
    private final MathValue value;

    public TruncateFunction(MathValue ... mathValueArray) {
        super(mathValueArray);
        this.value = mathValueArray[0];
    }

    @Override
    public String getName() {
        return "math.trunc";
    }

    @Override
    public double compute(ControllerState controllerState) {
        return (long)this.value.get(controllerState);
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

