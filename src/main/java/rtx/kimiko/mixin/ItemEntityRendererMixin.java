package rtx.kimiko.mixin;
import java.util.HashMap;
import java.util.WeakHashMap;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.entity.state.ItemEntityRenderState;
import net.minecraft.client.render.entity.state.ItemStackEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.joml.Quaternionfc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.kimiko.api.modules.impl.Visuals.ItemPhysics;

@Mixin(net.minecraft.client.render.entity.ItemEntityRenderer.class)

public abstract class ItemEntityRendererMixin {
    @Unique
    private static final WeakHashMap<ItemEntityRenderState, Boolean> kimiko_groundStateMap = new WeakHashMap();
    @Unique
    private static final HashMap<Integer, Integer> kimiko_groundHoldMap = new HashMap();
    @Unique
    private ItemEntityRenderState kimiko_currentState;

    @Inject(method="updateRenderState", at={@At(value="HEAD")}, require = 0)
    private void kimiko_captureGroundState(ItemEntity entity, ItemEntityRenderState state, float tickDelta, CallbackInfo ci) {
        kimiko_groundStateMap.put(state, ItemEntityRendererMixin.kimiko_resolveGroundState(entity, entity.isOnGround()));
    }

    @Redirect(method="render", at=@At(value="INVOKE", target="Lnet/minecraft/client/util/math/MatrixStack;method_46416(FFF)V", ordinal=0), require = 0)
    private void kimiko_translate(MatrixStack matrices, float x, float y, float z, ItemEntityRenderState state, MatrixStack matricesArg, OrderedRenderCommandQueue submitNodeCollector, CameraRenderState cameraRenderState) {
        this.kimiko_currentState = state;
        boolean enabled = ItemEntityRendererMixin.kimiko_isItemPhysicsEnabled();
        boolean stableGround = kimiko_groundStateMap.getOrDefault(state, false);
        if ((enabled || stableGround) && state.itemRenderState != null) {
            Box box = state.itemRenderState.getModelBoundingBox();
            matrices.translate(x, -((float)box.minY) + 0.0625f, z);
        } else {
            matrices.translate(x, y, z);
        }
    }

    @Redirect(method="render", at=@At(value="INVOKE", target="Lnet/minecraft/client/render/entity/ItemEntityRenderer;method_72986(Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;ILnet/minecraft/class_10428;Lnet/minecraft/class_5819;Lnet/minecraft/class_238;)V"), require = 0)
    private void kimiko_renderPhysics(MatrixStack matrices, OrderedRenderCommandQueue submitNodeCollector, int light, ItemStackEntityRenderState stackState, Random random, Box box) {
        if (ItemEntityRendererMixin.kimiko_isItemPhysicsEnabled() && this.kimiko_currentState != null) {
            boolean scaled;
            float age = this.kimiko_currentState.age;
            float offset = this.kimiko_currentState.uniqueOffset;
            boolean onGround = kimiko_groundStateMap.getOrDefault(this.kimiko_currentState, false);
            float scale = ItemEntityRendererMixin.kimiko_groundItemScale();
            boolean bl = scaled = Math.abs(scale - 1.0f) > 0.001f;
            if (scaled) {
                matrices.translate(0.0f, 0.0625f, 0.0f);
                matrices.scale(scale, scale, scale);
                matrices.translate(0.0f, -0.0625f, 0.0f);
            }
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotation(-ItemEntity.getRotation((float)age, (float)offset)));
            if (onGround) {
                matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
                matrices.translate(0.0f, -((float)box.getLengthY() / 2.0f) + 0.0625f, 0.0f);
            } else {
                matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees((age * 15.0f + offset * 360.0f) % 360.0f));
            }
        }
        ItemEntityRenderer.render((MatrixStack)matrices, (OrderedRenderCommandQueue)submitNodeCollector, (int)light, (ItemStackEntityRenderState)stackState, (Random)random, (Box)box);
    }

    @Unique
    private static boolean kimiko_isItemPhysicsEnabled() {
        ItemPhysics itemPhysics = ItemPhysics.getInstance();
        return itemPhysics != null && itemPhysics.isEnabled() && itemPhysics.isNormalMode();
    }

    @Unique
    private static float kimiko_groundItemScale() {
        ItemPhysics itemPhysics = ItemPhysics.getInstance();
        return itemPhysics != null ? itemPhysics.groundItemScale() : 1.0f;
    }

    @Unique
    private static boolean kimiko_resolveGroundState(ItemEntity entity, boolean onGround) {
        boolean lowMotion;
        int entityId = entity.getId();
        if (onGround) {
            kimiko_groundHoldMap.put(entityId, 8);
            ItemEntityRendererMixin.kimiko_trimGroundMap();
            return true;
        }
        Integer holdTicks = kimiko_groundHoldMap.get(entityId);
        if (holdTicks == null || holdTicks <= 0) {
            return false;
        }
        Vec3d velocity = entity.getVelocity();
        boolean bl = lowMotion = Math.abs(velocity.x) <= 0.08 && Math.abs(velocity.y) <= 0.08 && Math.abs(velocity.z) <= 0.08;
        if (!lowMotion) {
            kimiko_groundHoldMap.remove(entityId);
            return false;
        }
        if (holdTicks == 1) {
            kimiko_groundHoldMap.remove(entityId);
        } else {
            kimiko_groundHoldMap.put(entityId, holdTicks - 1);
        }
        return true;
    }

    @Unique
    private static void kimiko_trimGroundMap() {
        if (kimiko_groundHoldMap.size() > 4096) {
            kimiko_groundHoldMap.clear();
        }
    }
}

