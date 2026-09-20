package rtx.kimiko.api.modules.impl.Visuals.customization;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;
import rtx.kimiko.api.modules.impl.Visuals.Customization;
import rtx.kimiko.api.modules.impl.Visuals.customization.FlugerHatModel;

public class CustomizationLayer<S extends PlayerEntityRenderState, M extends EntityModel<S>>
extends FeatureRenderer<S, M> {
    public CustomizationLayer(FeatureRendererContext<S, M> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int n, S s, float f, float f2) {
        Entity entity;
        Customization customization = Customization.getInstance();
        if (customization == null || !customization.isEnabled() || ((PlayerEntityRenderState)s).invisible || ((PlayerEntityRenderState)s).baby) {
            return;
        }
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient.world == null || !((entity = minecraftClient.world.getEntityById(((PlayerEntityRenderState)s).id)) instanceof AbstractClientPlayerEntity)) {
            return;
        }
        AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)entity;
        boolean bl = customization.crownEnabledFor(abstractClientPlayerEntity);
        boolean bl2 = customization.hatEnabledFor(abstractClientPlayerEntity);
        boolean bl3 = customization.wingsEnabledFor(abstractClientPlayerEntity);
        boolean bl4 = customization.goatEnabledFor(abstractClientPlayerEntity);
        if (!(bl || bl2 || bl3 || bl4)) {
            return;
        }
        EntityModel entityModel = this.getContextModel();
        if (bl || bl2) {
            matrixStack.push();
            entityModel.getRootPart().applyTransform(matrixStack);
            ((ModelWithHead)entityModel).applyTransform(matrixStack);
            if (bl2) {
                FlugerHatModel.render((MatrixStack)matrixStack, (OrderedRenderCommandQueue)orderedRenderCommandQueue, (int)n);
            }
            if (bl) {
                matrixStack.push();
                matrixStack.multiply((Quaternionfc)RotationAxis.NEGATIVE_Z.rotationDegrees(180.0f));
                matrixStack.multiply((Quaternionfc)RotationAxis.NEGATIVE_Y.rotationDegrees(90.0f));
                customization.submitCrown(abstractClientPlayerEntity, matrixStack, orderedRenderCommandQueue, n);
                matrixStack.pop();
            }
            matrixStack.pop();
        }
        if ((bl3 || bl4) && entityModel instanceof BipedEntityModel) {
            BipedEntityModel bipedEntityModel = (BipedEntityModel)entityModel;
            matrixStack.push();
            entityModel.getRootPart().applyTransform(matrixStack);
            bipedEntityModel.body.applyTransform(matrixStack);
            if (bl3) {
                customization.submitWings(abstractClientPlayerEntity, matrixStack, orderedRenderCommandQueue, n, ((PlayerEntityRenderState)s).isGliding, ((PlayerEntityRenderState)s).limbSwingAmplitude, ((PlayerEntityRenderState)s).handSwingProgress);
            }
            if (bl4) {
                customization.submitGoat(abstractClientPlayerEntity, matrixStack, orderedRenderCommandQueue, n);
            }
            matrixStack.pop();
        }
    }
}

