package rtx.byazen.api.mods.waveycapes;
import java.util.function.IntUnaryOperator;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;

public class NMSUtil {
    public static ModelPart[] buildCape(int n, int n2, IntUnaryOperator intUnaryOperator, IntUnaryOperator intUnaryOperator2) {
        ModelPart[] modelPartArray = new ModelPart[16];
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        for (int i = 0; i < 16; ++i) {
            modelPartData.addChild("customCape_" + i, ModelPartBuilder.create().uv(intUnaryOperator.applyAsInt(i), intUnaryOperator2.applyAsInt(i)).cuboid(-5.0f, (float)i, -1.0f, 10.0f, 1.0f, 1.0f, Dilation.NONE, 1.0f, 0.5f), ModelTransform.origin((float)0.0f, (float)0.0f, (float)0.0f));
        }
        ModelPart modelPart = modelPartData.createPart(n, n2);
        for (int i = 0; i < 16; ++i) {
            modelPartArray[i] = modelPart.getChild("customCape_" + i);
        }
        return modelPartArray;
    }
}

