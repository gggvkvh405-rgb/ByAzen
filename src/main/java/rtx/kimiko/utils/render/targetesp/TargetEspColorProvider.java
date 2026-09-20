package rtx.kimiko.utils.render.targetesp;

@FunctionalInterface
public interface TargetEspColorProvider {
    int getColor(int index, float alpha);
}
