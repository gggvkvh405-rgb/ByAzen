package rtx.kimiko.api.modules.impl.Visuals;
import rtx.kimiko.api.events.EventHandler;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import rtx.kimiko.api.drags.Position;
import rtx.kimiko.api.events.impl.input.HotBarScrollEvent;
import rtx.kimiko.api.events.impl.input.MouseButtonEvent;
import rtx.kimiko.api.events.impl.input.MouseButtonEvent.Action;
import rtx.kimiko.api.events.impl.render.HudRenderEvent;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;
import rtx.kimiko.api.modules.ModuleManager;
import rtx.kimiko.utils.animations.Easings;
import rtx.kimiko.utils.animations.SmoothAnimation;
import rtx.kimiko.utils.render.post.shaderhands.ShaderHandsRenderer;
import rtx.kimiko.utils.storage.RepositoryStorage;

public final class ViewModel
extends Module {
    private static ViewModel instance;
    private float mainOffsetX;
    private float mainOffsetY;
    private float offOffsetX;
    private float offOffsetY;
    private float mainTargetX;
    private float mainTargetY;
    private float offTargetX;
    private float offTargetY;
    private float mainScaleTarget = 1.0f;
    private float offScaleTarget = 1.0f;
    private final SmoothAnimation mainScaleAnim = new SmoothAnimation();
    private final SmoothAnimation offScaleAnim = new SmoothAnimation();
    private static final double SCALE_ANIM_SECONDS = 0.2;
    private boolean loaded;
    private Hand activeHand;
    private long lastClickMs;
    private boolean holding;
    private float grabCursorX;
    private float grabCursorY;
    private float grabOffsetX;
    private float grabOffsetY;
    private float grabScale = 1.0f;
    private float grabPivotX;
    private float grabPivotY;
    private boolean grabPivotValid;
    private Matrix4f mainA;
    private Matrix4f offA;
    private float grabDepth = 0.72f;
    private float mainPivotX;
    private float mainPivotY;
    private float offPivotX;
    private float offPivotY;
    private boolean mainPivotValid;
    private boolean offPivotValid;
    private Arm lastMainArm;
    private static final float FALLBACK_DEPTH = 0.72f;
    private final SmoothAnimation outlineAnim = new SmoothAnimation();
    private Hand outlineHand;
    private static final double OUTLINE_IN_SECONDS = 0.22;
    private static final double OUTLINE_OUT_SECONDS = 0.3;
    private static final float DRAG_UNITS_PER_PIXEL = 3.2f;
    private static final float SCALE_STEP = 0.22f;
    private static final float SCALE_MIN = 0.2f;
    private static final float SCALE_MAX = 5.0f;
    private static final float FOLLOW = 0.45f;
    private static final long DOUBLE_CLICK_MS = 300L;
    private static final String SAVE_FILE = "viewmodel";
    private static final float CENTROID_X = 0.0f;
    private static final float CENTROID_Y = 0.0f;
    private static final float CENTROID_Z = 0.0f;

    public ViewModel() {
        super("ViewModel", "\u0414\u0432\u0430\u0436\u0434\u044b \u043a\u043b\u0438\u043a\u043d\u0438\u0442\u0435 \u043f\u043e \u0440\u0443\u043a\u0435 \u0432 \u0447\u0430\u0442\u0435, \u0447\u0442\u043e\u0431\u044b \u043f\u0435\u0440\u0435\u0442\u0430\u0449\u0438\u0442\u044c \u0435\u0451, \u0438 \u043a\u043e\u043b\u0451\u0441\u0438\u043a\u043e\u043c \u0438\u0437\u043c\u0435\u043d\u0438\u0442\u0435 \u0440\u0430\u0437\u043c\u0435\u0440.", Category.VISUALS);
        instance = this;
        this.outlineAnim.set(0.0);
        this.mainScaleAnim.set(1.0);
        this.offScaleAnim.set(1.0);
    }

    private void load() {
        if (this.loaded) {
            return;
        }
        this.loaded = true;
        JsonObject jsonObject = RepositoryStorage.readObject(SAVE_FILE);
        if (jsonObject == null) {
            return;
        }
        this.mainOffsetX = this.mainTargetX = ViewModel.readFloat(jsonObject, "mainX", 0.0f);
        this.mainOffsetY = this.mainTargetY = ViewModel.readFloat(jsonObject, "mainY", 0.0f);
        this.offOffsetX = this.offTargetX = ViewModel.readFloat(jsonObject, "offX", 0.0f);
        this.offOffsetY = this.offTargetY = ViewModel.readFloat(jsonObject, "offY", 0.0f);
        this.mainScaleTarget = ViewModel.clamp(ViewModel.readFloat(jsonObject, "mainScale", 1.0f), 0.2f, 5.0f);
        this.offScaleTarget = ViewModel.clamp(ViewModel.readFloat(jsonObject, "offScale", 1.0f), 0.2f, 5.0f);
        this.mainScaleAnim.set(this.mainScaleTarget);
        this.offScaleAnim.set(this.offScaleTarget);
    }

    private static float clamp(float f, float f2, float f3) {
        return Math.max(f2, Math.min(f3, f));
    }

    public static boolean apply(MatrixStack matrixStack, Hand hand) {
        float f;
        ViewModel viewModel = ViewModel.getInstance();
        if (viewModel == null) {
            return false;
        }
        viewModel.load();
        viewModel.handleMainHandSwap();
        if (!viewModel.isEnabled() || matrixStack == null || hand == null) {
            return false;
        }
        viewModel.captureTransform(matrixStack, hand);
        boolean bl = hand == Hand.MAIN_HAND;
        float f2 = bl ? viewModel.mainOffsetX : viewModel.offOffsetX;
        float f3 = f = bl ? viewModel.mainOffsetY : viewModel.offOffsetY;
        if (f2 == 0.0f && f == 0.0f) {
            return false;
        }
        matrixStack.translate(f2, f, 0.0f);
        return true;
    }

    public static ViewModel getInstance() {
        ViewModel viewModel = ModuleManager.get().get(ViewModel.class);
        return viewModel != null ? viewModel : instance;
    }

    private void save() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("mainX", (Number)Float.valueOf(this.mainTargetX));
        jsonObject.addProperty("mainY", (Number)Float.valueOf(this.mainTargetY));
        jsonObject.addProperty("offX", (Number)Float.valueOf(this.offTargetX));
        jsonObject.addProperty("offY", (Number)Float.valueOf(this.offTargetY));
        jsonObject.addProperty("mainScale", (Number)Float.valueOf(this.mainScaleTarget));
        jsonObject.addProperty("offScale", (Number)Float.valueOf(this.offScaleTarget));
        RepositoryStorage.write(SAVE_FILE, jsonObject);
    }

    private static float readFloat(JsonObject jsonObject, String string, float f) {
        try {
            return jsonObject.has(string) ? jsonObject.get(string).getAsFloat() : f;
        }
        catch (RuntimeException runtimeException) {
            return f;
        }
    }

    public static float handScale(Hand hand) {
        ViewModel viewModel = ViewModel.getInstance();
        if (viewModel == null) {
            return 1.0f;
        }
        viewModel.load();
        return hand == Hand.MAIN_HAND ? viewModel.mainScaleAnim.get() : viewModel.offScaleAnim.get();
    }

    private static float[] jacobianAt(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5) {
        if (f4 < 1.0f || f5 < 1.0f || f < 1.0E-4f) {
            return null;
        }
        float f6 = matrix4f.m00();
        float f7 = matrix4f.m01();
        float f8 = matrix4f.m03();
        float f9 = matrix4f.m10();
        float f10 = matrix4f.m11();
        float f11 = matrix4f.m13();
        float f12 = f4 * 0.5f;
        float f13 = f5 * 0.5f;
        float f14 = (f6 - f2 * f8) / f * f12;
        float f15 = -(f7 - f3 * f8) / f * f13;
        float f16 = (f9 - f2 * f11) / f * f12;
        float f17 = -(f10 - f3 * f11) / f * f13;
        return new float[]{f14, f15, f16, f17};
    }

    private static boolean inChat() {
        return MinecraftClient.getInstance().currentScreen instanceof ChatScreen;
    }

    public static boolean applyScale(MatrixStack matrixStack, Hand hand) {
        float f;
        ViewModel viewModel = ViewModel.getInstance();
        if (viewModel == null || !viewModel.isEnabled() || matrixStack == null || hand == null) {
            return false;
        }
        viewModel.load();
        viewModel.captureScalePivot(matrixStack, hand);
        boolean bl = hand == Hand.MAIN_HAND;
        float f2 = f = bl ? viewModel.mainScaleAnim.get() : viewModel.offScaleAnim.get();
        if (f == 1.0f) {
            return false;
        }
        matrixStack.translate(0.0f, 0.0f, 0.0f);
        matrixStack.scale(f, f, f);
        matrixStack.translate(-0.0f, -0.0f, -0.0f);
        return true;
    }

    private static float dist2(float f, float f2, float f3, float f4) {
        float f5 = f - f3;
        float f6 = f2 - f4;
        return f5 * f5 + f6 * f6;
    }

    @EventHandler
    private void onHud(HudRenderEvent hudRenderEvent) {
        boolean bl;
        boolean bl2;
        boolean bl3;
        this.load();
        this.handleMainHandSwap();
        boolean bl4 = bl3 = this.isEnabled() && ViewModel.inChat();
        if (!bl3) {
            this.activeHand = null;
            this.holding = false;
        }
        boolean bl5 = bl2 = bl3 && this.holding && this.activeHand != null;
        if (bl2) {
            this.outlineHand = this.activeHand;
        }
        this.outlineAnim.run(bl2 ? 1.0 : 0.0, bl2 ? 0.22 : 0.3, Easings.CUBIC_OUT, true);
        this.outlineAnim.update();
        if (!bl2 && this.outlineAnim.get() <= 0.001f) {
            this.outlineHand = null;
        }
        this.mainScaleAnim.update();
        this.offScaleAnim.update();
        if (bl3 && this.holding && this.activeHand != null) {
            float f;
            float f2;
            float f3;
            bl = this.activeHand == Hand.MAIN_HAND;
            Matrix4f matrix4f = bl ? this.mainA : this.offA;
            float f4 = Position.screenWidth();
            float f5 = Position.screenHeight();
            float f6 = f4 > 1.0f ? this.grabCursorX / f4 * 2.0f - 1.0f : 0.0f;
            float f7 = f5 > 1.0f ? 1.0f - this.grabCursorY / f5 * 2.0f : 0.0f;
            float[] fArray = matrix4f != null ? ViewModel.jacobianAt(matrix4f, this.grabDepth, f6, f7, f4, f5) : null;
            float f8 = bl ? this.mainScaleAnim.get() : this.offScaleAnim.get();
            float f9 = Position.mouseX();
            float f10 = Position.mouseY();
            float f11 = f3 = fArray != null ? fArray[0] * fArray[3] - fArray[2] * fArray[1] : 0.0f;
            if (fArray != null && Math.abs(f3) > 1.0E-4f) {
                float f12 = this.grabScale > 1.0E-4f ? f8 / this.grabScale : 1.0f;
                float f13 = this.grabPivotValid ? this.grabPivotX : this.grabCursorX;
                float f14 = this.grabPivotValid ? this.grabPivotY : this.grabCursorY;
                float f15 = f9 - f13 - f12 * (this.grabCursorX - f13);
                float f16 = f10 - f14 - f12 * (this.grabCursorY - f14);
                f2 = this.grabOffsetX + (fArray[3] * f15 - fArray[2] * f16) / f3;
                f = this.grabOffsetY + (-fArray[1] * f15 + fArray[0] * f16) / f3;
            } else {
                float f17 = Math.max(1.0f, Position.screenHeight());
                float f18 = 3.2f / f17;
                f2 = this.grabOffsetX + (f9 - this.grabCursorX) * f18;
                f = this.grabOffsetY - (f10 - this.grabCursorY) * f18;
            }
            if (bl) {
                this.mainTargetX = f2;
                this.mainTargetY = f;
            } else {
                this.offTargetX = f2;
                this.offTargetY = f;
            }
        }
        bl = bl3 && this.holding && this.activeHand == Hand.MAIN_HAND;
        boolean bl6 = bl3 && this.holding && this.activeHand == Hand.OFF_HAND;
        this.mainOffsetX += bl ? this.mainTargetX - this.mainOffsetX : (this.mainTargetX - this.mainOffsetX) * 0.45f;
        this.mainOffsetY += bl ? this.mainTargetY - this.mainOffsetY : (this.mainTargetY - this.mainOffsetY) * 0.45f;
        this.offOffsetX += bl6 ? this.offTargetX - this.offOffsetX : (this.offTargetX - this.offOffsetX) * 0.45f;
        this.offOffsetY += bl6 ? this.offTargetY - this.offOffsetY : (this.offTargetY - this.offOffsetY) * 0.45f;
    }

    @Override
    protected void onDisable() {
        this.activeHand = null;
        this.holding = false;
    }

    @EventHandler
    private void onMouse(MouseButtonEvent mouseButtonEvent) {
        if (!this.isEnabled() || !ViewModel.inChat() || mouseButtonEvent.button != 0) {
            return;
        }
        if (mouseButtonEvent.action == MouseButtonEvent.Action.PRESS) {
            long l = System.currentTimeMillis();
            boolean bl = l - this.lastClickMs <= 300L;
            this.lastClickMs = l;
            if (!bl) {
                return;
            }
            if (!ShaderHandsRenderer.isHandCoveredAt(Position.mouseX(), Position.mouseY(), Position.screenWidth(), Position.screenHeight())) {
                return;
            }
            this.activeHand = this.pickGrabbedHand(Position.mouseX(), Position.mouseY());
            this.holding = true;
            boolean bl2 = this.activeHand == Hand.MAIN_HAND;
            this.grabCursorX = Position.mouseX();
            this.grabCursorY = Position.mouseY();
            this.grabOffsetX = bl2 ? this.mainTargetX : this.offTargetX;
            this.grabOffsetY = bl2 ? this.mainTargetY : this.offTargetY;
            float f = ShaderHandsRenderer.handDepthAt(this.grabCursorX, this.grabCursorY, Position.screenWidth(), Position.screenHeight());
            this.grabDepth = f > 0.0f ? f : 0.72f;
            this.grabScale = bl2 ? this.mainScaleTarget : this.offScaleTarget;
            this.grabPivotValid = bl2 ? this.mainPivotValid : this.offPivotValid;
            this.grabPivotX = bl2 ? this.mainPivotX : this.offPivotX;
            this.grabPivotY = bl2 ? this.mainPivotY : this.offPivotY;
            mouseButtonEvent.cancel();
        } else if (mouseButtonEvent.action == MouseButtonEvent.Action.RELEASE) {
            this.holding = false;
            this.save();
        }
    }

    @EventHandler
    private void onScroll(HotBarScrollEvent hotBarScrollEvent) {
        float f;
        if (!this.isEnabled() || !ViewModel.inChat() || this.activeHand == null || hotBarScrollEvent.getVertical() == 0.0) {
            return;
        }
        float f2 = f = hotBarScrollEvent.getVertical() > 0.0 ? 0.22f : -0.22f;
        if (this.activeHand == Hand.MAIN_HAND) {
            this.mainScaleTarget = ViewModel.clamp(this.mainScaleTarget + f, 0.2f, 5.0f);
            this.mainScaleAnim.run((double)this.mainScaleTarget, 0.2, Easings.CUBIC_OUT);
        } else {
            this.offScaleTarget = ViewModel.clamp(this.offScaleTarget + f, 0.2f, 5.0f);
            this.offScaleAnim.run((double)this.offScaleTarget, 0.2, Easings.CUBIC_OUT);
        }
        this.save();
        hotBarScrollEvent.cancel();
    }

    private static boolean project(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float[] fArray) {
        Vector4f vector4f = matrix4f.transform(new Vector4f(f, f2, f3, 1.0f));
        if (Math.abs(vector4f.w) < 1.0E-6f) {
            return false;
        }
        float f6 = vector4f.x / vector4f.w;
        float f7 = vector4f.y / vector4f.w;
        fArray[0] = (f6 * 0.5f + 0.5f) * f4;
        fArray[1] = (1.0f - (f7 * 0.5f + 0.5f)) * f5;
        return true;
    }

    private void captureTransform(MatrixStack matrixStack, Hand hand) {
        float f = Position.screenWidth();
        float f2 = Position.screenHeight();
        if (f < 1.0f || f2 < 1.0f) {
            return;
        }
        Matrix4f matrix4f = ViewModel.handProjection().mul((Matrix4fc)RenderSystem.getModelViewMatrix()).mul((Matrix4fc)matrixStack.peek().getPositionMatrix());
        if (hand == Hand.MAIN_HAND) {
            this.mainA = matrix4f;
        } else {
            this.offA = matrix4f;
        }
    }

    private static Matrix4f handProjection() {
        Window window = MinecraftClient.getInstance().getWindow();
        float f = (float)window.getFramebufferWidth() / (float)Math.max(1, window.getFramebufferHeight());
        float f2 = 1.2217305f;
        return new Matrix4f().perspective(f2, f, 0.05f, 100.0f);
    }

    public static void beginHandFrame() {
        ViewModel viewModel = ViewModel.getInstance();
        if (viewModel != null) {
            viewModel.mainPivotValid = false;
            viewModel.offPivotValid = false;
            viewModel.load();
            viewModel.handleMainHandSwap();
        }
    }

    private void handleMainHandSwap() {
        ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
        if (clientPlayerEntity == null) {
            return;
        }
        Arm arm = clientPlayerEntity.getMainArm();
        if (this.lastMainArm == null) {
            this.lastMainArm = arm;
            return;
        }
        if (arm == this.lastMainArm) {
            return;
        }
        this.lastMainArm = arm;
        this.mainOffsetX = this.mainTargetX = -this.mainTargetX;
        this.offOffsetX = this.offTargetX = -this.offTargetX;
        this.save();
    }

    public static boolean wantsHandMask() {
        ViewModel viewModel = ViewModel.getInstance();
        return viewModel != null && viewModel.isEnabled() && ViewModel.inChat();
    }

    private void captureScalePivot(MatrixStack matrixStack, Hand hand) {
        float[] fArray;
        float f = Position.screenWidth();
        float f2 = Position.screenHeight();
        if (f < 1.0f || f2 < 1.0f) {
            return;
        }
        Matrix4f matrix4f = ViewModel.handProjection().mul((Matrix4fc)RenderSystem.getModelViewMatrix()).mul((Matrix4fc)matrixStack.peek().getPositionMatrix());
        if (!ViewModel.project(matrix4f, 0.0f, 0.0f, 0.0f, f, f2, fArray = new float[2])) {
            return;
        }
        if (hand == Hand.MAIN_HAND) {
            this.mainPivotX = fArray[0];
            this.mainPivotY = fArray[1];
            this.mainPivotValid = true;
        } else {
            this.offPivotX = fArray[0];
            this.offPivotY = fArray[1];
            this.offPivotValid = true;
        }
    }

    private Hand pickGrabbedHand(float f, float f2) {
        boolean bl = this.mainPivotValid;
        boolean bl2 = this.offPivotValid;
        if (bl && bl2) {
            return ViewModel.dist2(f, f2, this.mainPivotX, this.mainPivotY) <= ViewModel.dist2(f, f2, this.offPivotX, this.offPivotY) ? Hand.MAIN_HAND : Hand.OFF_HAND;
        }
        if (bl) {
            return Hand.MAIN_HAND;
        }
        if (bl2) {
            return Hand.OFF_HAND;
        }
        boolean bl3 = f >= Position.screenWidth() * 0.5f;
        ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
        Arm arm = clientPlayerEntity != null ? clientPlayerEntity.getMainArm() : (Arm)MinecraftClient.getInstance().options.getMainArm().getValue();
        boolean bl4 = arm == Arm.RIGHT;
        return bl3 == bl4 ? Hand.MAIN_HAND : Hand.OFF_HAND;
    }

    public void resetLayout() {
        this.loaded = true;
        this.holding = false;
        this.activeHand = null;
        this.mainOffsetY = 0.0f;
        this.mainOffsetX = 0.0f;
        this.mainTargetY = 0.0f;
        this.mainTargetX = 0.0f;
        this.offOffsetY = 0.0f;
        this.offOffsetX = 0.0f;
        this.offTargetY = 0.0f;
        this.offTargetX = 0.0f;
        this.offScaleTarget = 1.0f;
        this.mainScaleTarget = 1.0f;
        this.mainScaleAnim.run(1.0, 0.2, Easings.CUBIC_OUT);
        this.offScaleAnim.run(1.0, 0.2, Easings.CUBIC_OUT);
        this.save();
    }

    public static boolean suppressEatAnimation() {
        ViewModel viewModel = ViewModel.getInstance();
        return viewModel != null && viewModel.isEnabled();
    }

    public static float outlineAlpha(Hand hand) {
        ViewModel viewModel = ViewModel.getInstance();
        if (viewModel == null || viewModel.outlineHand != hand) {
            return 0.0f;
        }
        return Math.max(0.0f, Math.min(1.0f, viewModel.outlineAnim.get()));
    }
}

