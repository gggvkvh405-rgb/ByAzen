package rtx.byazen.api.mods.geckolib.loading.math.value;
import rtx.byazen.api.mods.geckolib.animation.state.ControllerState;
import rtx.byazen.api.mods.geckolib.loading.math.MathValue;

public record Constant(double value) implements MathValue
{
    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    @Override
    public double get(ControllerState controllerState) {
        return this.value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }
}

