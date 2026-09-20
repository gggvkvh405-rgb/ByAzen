package rtx.kimiko.api.modules.impl.Visuals;
import rtx.kimiko.api.events.EventHandler;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import rtx.kimiko.api.events.impl.game.TickEvent;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;
import rtx.kimiko.api.modules.ModuleManager;
import rtx.kimiko.api.modules.impl.Visuals.customization.CrownRenderer;
import rtx.kimiko.api.modules.impl.Visuals.customization.CustomizationSyncClient;
import rtx.kimiko.api.modules.impl.Visuals.customization.KanekiKaguneRenderer;
import rtx.kimiko.api.modules.impl.Visuals.customization.RoyalWingsRenderer;
import rtx.kimiko.api.modules.impl.Visuals.customization.SeraphEyeWingsRenderer;
import rtx.kimiko.api.modules.impl.Visuals.customization.ShoulderGoatRenderer;
import rtx.kimiko.api.modules.settings.impl.BooleanSetting;
import rtx.kimiko.api.modules.settings.impl.ModeSetting;

public final class Customization
extends Module {
    private static final String HEAD_CROWN = "\u041a\u043e\u0440\u043e\u043d\u0430";
    private static final String HEAD_HAT = "\u0428\u0430\u043f\u043a\u0430";
    private static final String HEAD_NONE = "\u041d\u0435\u0442";
    private static final String SYNC_CROWN = "crown";
    private static final String SYNC_HAT = "hat";
    private static final String SYNC_NONE = "none";
    private static final String BODY_ROYAL = "\u041a\u043e\u0440\u043e\u043b\u0435\u0432\u0441\u043a\u0438\u0435 \u043a\u0440\u044b\u043b\u044c\u044f";
    private static final String BODY_KAGUNE = "\u041a\u0430\u0433\u0443\u043d\u0435 \u041a\u0430\u043d\u044d\u043a\u0438";
    private static final String BODY_SERAPH = "\u041a\u0440\u044b\u043b\u044c\u044f \u0421\u0435\u0440\u0430\u0444\u0438\u043c\u0430";
    private static final String SYNC_ROYAL = "royal";
    private static final String SYNC_KAGUNE = "kagune";
    private static final String SYNC_SERAPH = "seraph";
    private static final String SYNC_HOST = "2.26.229.40";
    private static final int SYNC_PORT = 32117;
    private static final long CONNECT_RETRY_MS = 5000L;
    private static final long HEARTBEAT_MS = 10000L;
    private static Customization instance;
    private final ModeSetting headAccessorySetting = new ModeSetting("\u0413\u043e\u043b\u043e\u0432\u043d\u043e\u0439 \u0443\u0431\u043e\u0440", "\u0412\u044b\u0431\u043e\u0440 \u0430\u043a\u0441\u0435\u0441\u0441\u0443\u0430\u0440\u0430 \u043d\u0430 \u0433\u043e\u043b\u043e\u0432\u0435.", "\u041a\u043e\u0440\u043e\u043d\u0430", "\u041a\u043e\u0440\u043e\u043d\u0430", "\u0428\u0430\u043f\u043a\u0430", "\u041d\u0435\u0442");
    private final BooleanSetting goatSetting = new BooleanSetting("\u041a\u043e\u0437\u043e\u0447\u043a\u0430", "\u041a\u043e\u0437\u043e\u0447\u043a\u0430 \u043d\u0430 \u043f\u043b\u0435\u0447\u0435.", true);
    private final BooleanSetting wingsSetting = new BooleanSetting("\u041a\u0440\u044b\u043b\u044c\u044f", "\u041c\u0435\u0445\u0430\u043d\u0438\u0447\u0435\u0441\u043a\u0438\u0435 \u043a\u0440\u044b\u043b\u044c\u044f \u0437\u0430 \u0441\u043f\u0438\u043d\u043e\u0439.", true);
    private final ModeSetting bodyModelSetting = new ModeSetting("\u041c\u043e\u0434\u0435\u043b\u044c", "\u0412\u044b\u0431\u043e\u0440 \u043c\u043e\u0434\u0435\u043b\u0438 \u0437\u0430 \u0441\u043f\u0438\u043d\u043e\u0439.", "\u041a\u043e\u0440\u043e\u043b\u0435\u0432\u0441\u043a\u0438\u0435 \u043a\u0440\u044b\u043b\u044c\u044f", "\u041a\u043e\u0440\u043e\u043b\u0435\u0432\u0441\u043a\u0438\u0435 \u043a\u0440\u044b\u043b\u044c\u044f", "\u041a\u0430\u0433\u0443\u043d\u0435 \u041a\u0430\u043d\u044d\u043a\u0438", "\u041a\u0440\u044b\u043b\u044c\u044f \u0421\u0435\u0440\u0430\u0444\u0438\u043c\u0430").visibleWhen(this.wingsSetting::getValue);
    private final CustomizationSyncClient syncClient = new CustomizationSyncClient();
    private long nextConnectAttemptAt;
    private long nextPushAt;
    private String publishedUsername = "";
    private String publishedHeadAccessory = "";
    private String publishedBodyModel = "";
    private boolean publishedWings;

    public Customization() {
        super("Customization", "\u041a\u0430\u0441\u0442\u043e\u043c\u0438\u0437\u0430\u0446\u0438\u044f \u0441\u043a\u0438\u043d\u0430: \u0433\u043e\u043b\u043e\u0432\u043d\u043e\u0439 \u0443\u0431\u043e\u0440, \u043a\u0440\u044b\u043b\u044c\u044f \u0438 \u043f\u0438\u0442\u043e\u043c\u0435\u0446.", Category.VISUALS);
        this.register(this.headAccessorySetting, this.goatSetting, this.wingsSetting, this.bodyModelSetting);
        instance = this;
    }

    public static Customization getInstance() {
        Customization customization = ModuleManager.get().get(Customization.class);
        return customization != null ? customization : instance;
    }

    public boolean hatEnabled() {
        return this.isEnabled() && this.headAccessorySetting.is(HEAD_HAT);
    }

    private boolean canRender(AbstractClientPlayerEntity abstractClientPlayerEntity) {
        return this.isEnabled() && this.mc.player != null && abstractClientPlayerEntity != null && (abstractClientPlayerEntity != this.mc.player || this.mc.options.getPerspective() != Perspective.FIRST_PERSON) && !abstractClientPlayerEntity.isInvisible() && !abstractClientPlayerEntity.isSpectator() && !abstractClientPlayerEntity.isBaby();
    }

    public void submitGoat(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int n) {
        ShoulderGoatRenderer.render((AbstractClientPlayerEntity)abstractClientPlayerEntity, (MatrixStack)matrixStack, (OrderedRenderCommandQueue)orderedRenderCommandQueue, (int)n);
    }

    @Override
    protected void onDisable() {
        ShoulderGoatRenderer.reset();
        this.syncClient.disconnect("disabled");
        this.publishedUsername = "";
        this.publishedBodyModel = "";
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        boolean bl;
        if (!tickEvent.isPre()) {
            return;
        }
        if (this.mc.player == null || this.mc.world == null) {
            if (this.syncClient.isConnected() || this.syncClient.isConnecting()) {
                this.syncClient.disconnect("no_world");
            }
            this.publishedUsername = "";
            return;
        }
        String string = this.mc.player.getGameProfile().name();
        if (string == null || string.isBlank()) {
            return;
        }
        if (!this.publishedUsername.isBlank() && !this.publishedUsername.equalsIgnoreCase(string)) {
            this.syncClient.disconnect("identity_changed");
            this.publishedUsername = "";
            this.nextConnectAttemptAt = 0L;
        }
        String string2 = this.selectedHeadAccessory();
        boolean bl2 = this.wingsSetting.getValue();
        String string3 = bl2 ? this.selectedBodyModel() : SYNC_NONE;
        this.syncClient.setLocalState(string, string2, bl2, string3);
        long l = System.currentTimeMillis();
        if (!this.syncClient.isConnected() && !this.syncClient.isConnecting() && l >= this.nextConnectAttemptAt) {
            this.syncClient.connect(SYNC_HOST, 32117);
            this.nextConnectAttemptAt = l + 5000L;
        }
        boolean bl3 = bl = !string.equalsIgnoreCase(this.publishedUsername) || !string2.equals(this.publishedHeadAccessory) || !string3.equals(this.publishedBodyModel) || bl2 != this.publishedWings;
        if (this.syncClient.isConnected() && (bl || l >= this.nextPushAt)) {
            this.syncClient.pushState();
            this.publishedUsername = string;
            this.publishedHeadAccessory = string2;
            this.publishedBodyModel = string3;
            this.publishedWings = bl2;
            this.nextPushAt = l + 10000L;
        }
    }

    @Override
    protected void onEnable() {
        this.nextConnectAttemptAt = 0L;
        this.nextPushAt = 0L;
    }

    public boolean wingsEnabledFor(AbstractClientPlayerEntity abstractClientPlayerEntity) {
        if (!this.canRender(abstractClientPlayerEntity)) {
            return false;
        }
        if (abstractClientPlayerEntity == this.mc.player) {
            return this.wingsSetting.getValue();
        }
        CustomizationSyncClient.RemoteState remoteState = this.syncClient.getRemoteState(abstractClientPlayerEntity.getGameProfile().name());
        return remoteState != null && remoteState.wings();
    }

    public boolean crownEnabled() {
        return this.isEnabled() && this.headAccessorySetting.is(HEAD_CROWN);
    }

    public void submitCrown(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int n) {
        CrownRenderer.submit((MatrixStack)matrixStack, (OrderedRenderCommandQueue)orderedRenderCommandQueue, (int)n, (float)this.headYOffset(abstractClientPlayerEntity));
    }

    private String bodyModelFor(AbstractClientPlayerEntity abstractClientPlayerEntity) {
        if (abstractClientPlayerEntity == this.mc.player) {
            return this.selectedBodyModel();
        }
        CustomizationSyncClient.RemoteState remoteState = this.syncClient.getRemoteState(abstractClientPlayerEntity.getGameProfile().name());
        return remoteState == null ? SYNC_ROYAL : remoteState.bodyModel();
    }

    public boolean crownEnabledFor(AbstractClientPlayerEntity abstractClientPlayerEntity) {
        if (!this.canRender(abstractClientPlayerEntity)) {
            return false;
        }
        if (abstractClientPlayerEntity == this.mc.player) {
            return this.headAccessorySetting.is(HEAD_CROWN);
        }
        CustomizationSyncClient.RemoteState remoteState = this.syncClient.getRemoteState(abstractClientPlayerEntity.getGameProfile().name());
        return remoteState != null && remoteState.crown();
    }

    private String selectedHeadAccessory() {
        if (this.headAccessorySetting.is(HEAD_CROWN)) {
            return SYNC_CROWN;
        }
        return this.headAccessorySetting.is(HEAD_HAT) ? SYNC_HAT : SYNC_NONE;
    }

    public boolean hatEnabledFor(AbstractClientPlayerEntity abstractClientPlayerEntity) {
        if (!this.canRender(abstractClientPlayerEntity)) {
            return false;
        }
        if (abstractClientPlayerEntity == this.mc.player) {
            return this.headAccessorySetting.is(HEAD_HAT);
        }
        CustomizationSyncClient.RemoteState remoteState = this.syncClient.getRemoteState(abstractClientPlayerEntity.getGameProfile().name());
        return remoteState != null && remoteState.hat();
    }

    private String selectedBodyModel() {
        if (this.bodyModelSetting.is(BODY_KAGUNE)) {
            return SYNC_KAGUNE;
        }
        return this.bodyModelSetting.is(BODY_SERAPH) ? SYNC_SERAPH : SYNC_ROYAL;
    }

    public boolean goatEnabledFor(AbstractClientPlayerEntity abstractClientPlayerEntity) {
        return this.canRender(abstractClientPlayerEntity) && abstractClientPlayerEntity == this.mc.player && this.goatSetting.getValue();
    }

    public boolean wingsEnabled() {
        return this.isEnabled() && this.wingsSetting.getValue();
    }

    private float headYOffset(AbstractClientPlayerEntity abstractClientPlayerEntity) {
        boolean bl;
        boolean bl2 = abstractClientPlayerEntity.getAbilities().flying;
        boolean bl3 = abstractClientPlayerEntity.isSwimming();
        boolean bl4 = bl = !abstractClientPlayerEntity.getEquippedStack(EquipmentSlot.HEAD).isEmpty();
        if (abstractClientPlayerEntity.isInSneakingPose() && !bl2 && !bl3) {
            return bl ? 0.38f : 0.28f;
        }
        return bl ? 0.48f : 0.34f;
    }

    public void submitWings(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int n, boolean bl, float f, float f2) {
        boolean bl2 = bl || abstractClientPlayerEntity.getAbilities().flying;
        String string = this.bodyModelFor(abstractClientPlayerEntity);
        if (SYNC_KAGUNE.equals(string)) {
            KanekiKaguneRenderer.render((AbstractClientPlayerEntity)abstractClientPlayerEntity, (MatrixStack)matrixStack, (OrderedRenderCommandQueue)orderedRenderCommandQueue, (int)n, (boolean)bl2, (float)f, (float)f2);
        } else if (SYNC_SERAPH.equals(string)) {
            SeraphEyeWingsRenderer.render((AbstractClientPlayerEntity)abstractClientPlayerEntity, (MatrixStack)matrixStack, (OrderedRenderCommandQueue)orderedRenderCommandQueue, (int)n, (boolean)bl2, (float)f, (float)f2);
        } else {
            RoyalWingsRenderer.render((MatrixStack)matrixStack, (OrderedRenderCommandQueue)orderedRenderCommandQueue, (int)n, (boolean)bl2, (float)f, (double)abstractClientPlayerEntity.getVelocity().y);
        }
    }
}

