package rtx.kimiko.api.modules.impl.Visuals;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;
import rtx.kimiko.api.modules.ModuleManager;
import rtx.kimiko.api.modules.settings.impl.BooleanSetting;
import rtx.kimiko.utils.animations.DecelerateValue;

public final class BetterMinecraft
extends Module {
    private static final Identifier SATURATION_FULL_SPRITE = Identifier.ofVanilla((String)"hud/food_full");
    private static final Identifier SATURATION_HALF_SPRITE = Identifier.ofVanilla((String)"hud/food_half");
    private final BooleanSetting chatAnimations = this.register(new BooleanSetting("\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u0438 \u0447\u0430\u0442\u0430", "\u0410\u043d\u0438\u043c\u0438\u0440\u0443\u0435\u0442 \u043d\u043e\u0432\u044b\u0435 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f \u0438 \u043e\u0442\u043a\u0440\u044b\u0442\u0438\u0435 \u043f\u043e\u043b\u044f \u0432\u0432\u043e\u0434\u0430 \u0447\u0430\u0442\u0430.", true));
    private final BooleanSetting tabAnimation = this.register(new BooleanSetting("\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f \u0442\u0430\u0431\u0430", "\u041f\u043b\u0430\u0432\u043d\u043e\u0435 \u043f\u043e\u044f\u0432\u043b\u0435\u043d\u0438\u0435 \u0438 \u0437\u0430\u043a\u0440\u044b\u0442\u0438\u0435 \u0441\u043f\u0438\u0441\u043a\u0430 \u0438\u0433\u0440\u043e\u043a\u043e\u0432.", true));
    private final BooleanSetting inventoryAnimation = this.register(new BooleanSetting("\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u044f", "\u041f\u043b\u0430\u0432\u043d\u044b\u0439 \u0432\u044b\u0435\u0437\u0434 \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u044f \u0438 \u043a\u043e\u043d\u0442\u0435\u0439\u043d\u0435\u0440\u043e\u0432 \u043f\u0440\u0438 \u043e\u0442\u043a\u0440\u044b\u0442\u0438\u0438.", true));
    private final BooleanSetting itemMoveAnimation = this.register(new BooleanSetting("\u041f\u0435\u0440\u0435\u0442\u0430\u0441\u043a\u0438\u0432\u0430\u043d\u0438\u0435 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u043e\u0432", "\u041f\u0440\u0435\u0434\u043c\u0435\u0442\u044b \u043f\u043b\u0430\u0432\u043d\u043e \u043f\u0435\u0440\u0435\u043b\u0435\u0442\u0430\u044e\u0442 \u0432 \u043d\u043e\u0432\u044b\u0439 \u0441\u043b\u043e\u0442 \u043f\u0440\u0438 \u043f\u0435\u0440\u0435\u043a\u043b\u0430\u0434\u044b\u0432\u0430\u043d\u0438\u0438 \u0432\u043e \u0432\u0441\u0435\u0445 \u044d\u043a\u0440\u0430\u043d\u0430\u0445.", true));
    private final BooleanSetting hotbarAnimation = this.register(new BooleanSetting("\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f \u0445\u043e\u0442\u0431\u0430\u0440\u0430", "\u041f\u043b\u0430\u0432\u043d\u043e \u043f\u0435\u0440\u0435\u043c\u0435\u0449\u0430\u0435\u0442 \u0440\u0430\u043c\u043a\u0443 \u0432\u044b\u0431\u0440\u0430\u043d\u043d\u043e\u0439 \u044f\u0447\u0435\u0439\u043a\u0438 \u0432\u043b\u0435\u0432\u043e \u0438 \u0432\u043f\u0440\u0430\u0432\u043e.", true));
    private final BooleanSetting hotbarChatLift = this.register(new BooleanSetting("\u0425\u043e\u0442\u0431\u0430\u0440 \u043f\u0440\u0438 \u0447\u0430\u0442\u0435", "\u041f\u043b\u0430\u0432\u043d\u043e \u043f\u043e\u0434\u043d\u0438\u043c\u0430\u0435\u0442 \u0445\u043e\u0442\u0431\u0430\u0440 \u043f\u0440\u0438 \u043e\u0442\u043a\u0440\u044b\u0442\u0438\u0438 \u0447\u0430\u0442\u0430 \u0438 \u043e\u043f\u0443\u0441\u043a\u0430\u0435\u0442 \u043f\u0440\u0438 \u0437\u0430\u043a\u0440\u044b\u0442\u0438\u0438.", true));
    private final BooleanSetting saturationDisplay = this.register(new BooleanSetting("\u041e\u0442\u043e\u0431\u0440\u0430\u0436\u0435\u043d\u0438\u0435 \u043d\u0430\u0441\u044b\u0449\u0435\u043d\u043d\u043e\u0441\u0442\u0438", "\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0435\u0442 \u0442\u0435\u043a\u0443\u0449\u0435\u0435 \u043d\u0430\u0441\u044b\u0449\u0435\u043d\u0438\u0435 \u043d\u0430\u0434 \u0432\u0430\u043d\u0438\u043b\u044c\u043d\u043e\u0439 \u0441\u0442\u0440\u043e\u043a\u043e\u0439 \u0435\u0434\u044b.", true));
    private static final float CHAT_LIFT_PX = 14.0f;
    private static final long HOTBAR_STALE_NANOS = 250000000L;
    private static final DecelerateValue chatLift = new DecelerateValue(260);
    private static final DecelerateValue hotbarSelection = new DecelerateValue(180);
    private static long hotbarSelectionFrameNanos;
    private static long inventoryOpenTime;

    public BetterMinecraft() {
        super("Better Minecraft", "\u041d\u0435\u0431\u043e\u043b\u044c\u0448\u0438\u0435 \u0432\u0438\u0437\u0443\u0430\u043b\u044c\u043d\u044b\u0435 \u0443\u043b\u0443\u0447\u0448\u0435\u043d\u0438\u044f \u0432\u0430\u043d\u0438\u043b\u044c\u043d\u043e\u0433\u043e \u0440\u0435\u043d\u0434\u0435\u0440\u0430.", Category.VISUALS);
    }

    public static boolean chatAnimationsEnabled() {
        BetterMinecraft betterMinecraft = ModuleManager.get().get(BetterMinecraft.class);
        return betterMinecraft != null && betterMinecraft.shouldAnimateChat();
    }

    public static float inventorySlideOffset() {
        if (!BetterMinecraft.inventoryAnimationEnabled()) {
            return 0.0f;
        }
        if (inventoryOpenTime == 0L) {
            inventoryOpenTime = System.currentTimeMillis();
        }
        float f = Math.min(1.0f, (float)(System.currentTimeMillis() - inventoryOpenTime) / 350.0f);
        float f2 = 1.70158f;
        float f3 = f2 + 1.0f;
        float f4 = f - 1.0f;
        float f5 = 1.0f + f3 * f4 * f4 * f4 + f2 * f4 * f4;
        return -50.0f * (1.0f - f5);
    }

    public static void markInventoryOpen() {
        inventoryOpenTime = System.currentTimeMillis();
    }

    public static boolean itemMoveAnimationEnabled() {
        BetterMinecraft betterMinecraft = ModuleManager.get().get(BetterMinecraft.class);
        return betterMinecraft != null && betterMinecraft.shouldAnimateItemMove();
    }

    public static boolean inventoryAnimationEnabled() {
        BetterMinecraft betterMinecraft = ModuleManager.get().get(BetterMinecraft.class);
        return betterMinecraft != null && betterMinecraft.shouldAnimateInventory();
    }

    public boolean shouldRenderCapeWaves() {
        return this.isEnabled();
    }

    public static boolean tabAnimationEnabled() {
        BetterMinecraft betterMinecraft = ModuleManager.get().get(BetterMinecraft.class);
        return betterMinecraft != null && betterMinecraft.shouldAnimateTab();
    }

    public boolean shouldAnimateHotbar() {
        return this.isEnabled() && this.hotbarAnimation.getValue();
    }

    public boolean shouldLiftHotbarOnChat() {
        return this.isEnabled() && this.hotbarChatLift.getValue();
    }

    public boolean shouldAnimateTab() {
        return this.isEnabled() && this.tabAnimation.getValue();
    }

    public static boolean hotbarAnimationEnabled() {
        BetterMinecraft betterMinecraft = ModuleManager.get().get(BetterMinecraft.class);
        return betterMinecraft != null && betterMinecraft.shouldAnimateHotbar();
    }

    public boolean shouldDisplaySaturation() {
        return this.isEnabled() && this.saturationDisplay.getValue();
    }

    public static boolean hotbarChatLiftEnabled() {
        BetterMinecraft betterMinecraft = ModuleManager.get().get(BetterMinecraft.class);
        return betterMinecraft != null && betterMinecraft.shouldLiftHotbarOnChat();
    }

    public static boolean capeWavesEnabled() {
        BetterMinecraft betterMinecraft = ModuleManager.get().get(BetterMinecraft.class);
        return betterMinecraft != null && betterMinecraft.shouldRenderCapeWaves();
    }

    public boolean shouldAnimateInventory() {
        return this.isEnabled() && this.inventoryAnimation.getValue();
    }

    public static int animateHotbarSelectionX(int n) {
        long l = System.nanoTime();
        long l2 = l - hotbarSelectionFrameNanos;
        hotbarSelectionFrameNanos = l;
        if (!BetterMinecraft.hotbarAnimationEnabled() || l2 <= 0L || l2 > 250000000L) {
            hotbarSelection.snap(n);
            return n;
        }
        return Math.round(hotbarSelection.update(n));
    }

    public boolean shouldAnimateChat() {
        return this.isEnabled() && this.chatAnimations.getValue();
    }

    public boolean shouldAnimateItemMove() {
        return this.isEnabled() && this.itemMoveAnimation.getValue();
    }

    public static void renderSaturation(DrawContext drawContext, PlayerEntity playerEntity, int n, int n2) {
        float f;
        BetterMinecraft betterMinecraft = ModuleManager.get().get(BetterMinecraft.class);
        if (betterMinecraft == null || !betterMinecraft.shouldDisplaySaturation()) {
            return;
        }
        float f2 = playerEntity.getHungerManager().getSaturationLevel();
        int n3 = 9;
        for (int i = 0; i < 10 && !((f = f2 - (float)i * 2.0f) <= 0.0f); ++i) {
            Identifier identifier = f > 1.0f ? SATURATION_FULL_SPRITE : SATURATION_HALF_SPRITE;
            int n4 = n2 - i * 8 - 10 + (10 - n3) / 2;
            drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, n4, n - n3 - 1, n3, n3);
        }
    }

    public static float chatHotbarLiftOffset() {
        boolean bl = BetterMinecraft.hotbarChatLiftEnabled() && MinecraftClient.getInstance().currentScreen instanceof ChatScreen;
        return chatLift.update(bl ? 14.0f : 0.0f);
    }
}

