package rtx.kimiko.api.drags.hud;
import rtx.kimiko.api.events.EventHandler;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import rtx.kimiko.api.drags.Position;
import rtx.kimiko.api.events.EventBus;
import rtx.kimiko.api.events.impl.render.HudRenderEvent;
import rtx.kimiko.api.modules.ModuleManager;
import rtx.kimiko.api.modules.impl.Interface.InfoModule;
import rtx.kimiko.api.modules.impl.Utils.StreamerMode;
import rtx.kimiko.utils.network.Network;
import rtx.kimiko.utils.render.render2d.Render2D;

public final class InfoHud {
    private static final String FONT = "montserrat-semibold";
    private static final float SIZE = 7.0f;
    private static final float MARGIN = 5.0f;
    private static final float LINE_GAP = 3.0f;
    private static final int LABEL_COLOR = -3618608;
    private static final int LEFT_VALUE_COLOR = -11665561;
    private static final int RIGHT_VALUE_COLOR = -45747;
    private static final int OUTLINE_COLOR = -16777216;
    private static final float OUTLINE_OFFSET = 0.2f;
    private static final float[][] OUTLINE_DIRS = new float[][]{{-1.0f, -1.0f}, {0.0f, -1.0f}, {1.0f, -1.0f}, {-1.0f, 0.0f}, {1.0f, 0.0f}, {-1.0f, 1.0f}, {0.0f, 1.0f}, {1.0f, 1.0f}};

    public InfoHud() {
        EventBus.get().subscribe(this);
    }

    private static float width(InfoHud.Seg[] segArray) {
        float f = 0.0f;
        for (InfoHud.Seg seg : segArray) {
            f += Render2D.msdfWidth(FONT, seg.text, 7.0f);
        }
        return f;
    }

    private static int ping(MinecraftClient minecraftClient, PlayerEntity playerEntity) {
        if (minecraftClient.getNetworkHandler() == null) {
            return 0;
        }
        PlayerListEntry playerListEntry = minecraftClient.getNetworkHandler().getPlayerListEntry(playerEntity.getUuid());
        return playerListEntry == null ? 0 : Math.max(0, playerListEntry.getLatency());
    }

    @EventHandler
    private void onHud(HudRenderEvent hudRenderEvent) {
        InfoModule infoModule = ModuleManager.get().get(InfoModule.class);
        if (infoModule == null || !infoModule.isEnabled()) {
            return;
        }
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
        if (clientPlayerEntity == null || minecraftClient.world == null) {
            return;
        }
        float f = Position.screenWidth();
        float f2 = Position.screenHeight();
        float f3 = 10.0f;
        float f4 = f2 - 5.0f - 7.0f;
        float f5 = f4 - f3;
        DrawContext drawContext = hudRenderEvent.getGraphics();
        Render2D.beginFrame(drawContext);
        InfoHud.Seg[] segArray = new InfoHud.Seg[]{new InfoHud.Seg("BPS ", -3618608), new InfoHud.Seg(InfoHud.bps((PlayerEntity)clientPlayerEntity), -11665561)};
        InfoHud.Seg[] segArray2 = new InfoHud.Seg[]{new InfoHud.Seg("XYZ ", -3618608), new InfoHud.Seg(InfoHud.coords(clientPlayerEntity.getX(), clientPlayerEntity.getY(), clientPlayerEntity.getZ()), -11665561), new InfoHud.Seg("  " + InfoHud.otherDim(minecraftClient, (PlayerEntity)clientPlayerEntity), -3618608)};
        InfoHud.drawSegs(segArray, 5.0f, f5);
        InfoHud.drawSegs(segArray2, 5.0f, f4);
        InfoHud.Seg[] segArray3 = new InfoHud.Seg[]{new InfoHud.Seg("Ping ", -3618608), new InfoHud.Seg(InfoHud.ping(minecraftClient, (PlayerEntity)clientPlayerEntity) + "ms", -45747)};
        InfoHud.Seg[] segArray4 = new InfoHud.Seg[]{new InfoHud.Seg("FPS ", -3618608), new InfoHud.Seg(Integer.toString(minecraftClient.getCurrentFps()), -45747)};
        InfoHud.Seg[] segArray5 = new InfoHud.Seg[]{new InfoHud.Seg("TPS ", -3618608), new InfoHud.Seg(String.format(Locale.ROOT, "%.1f", Float.valueOf(Network.getTPS())), -45747)};
        float f6 = f4 - f3;
        InfoHud.drawSegs(segArray4, f - 5.0f - InfoHud.width(segArray4), f6 - f3);
        InfoHud.drawSegs(segArray5, f - 5.0f - InfoHud.width(segArray5), f6);
        InfoHud.drawSegs(segArray3, f - 5.0f - InfoHud.width(segArray3), f4);
        Render2D.flush();
    }

    private static String otherDim(MinecraftClient minecraftClient, PlayerEntity playerEntity) {
        if (StreamerMode.hideCoords()) {
            return "[#, #, #]";
        }
        boolean bl = minecraftClient.world.getDimension().coordinateScale() > 1.0;
        double d = bl ? 8.0 : 0.125;
        int n = (int)Math.floor(playerEntity.getX() * d);
        int n2 = (int)Math.floor(playerEntity.getZ() * d);
        return "[" + n + ", " + (int)Math.floor(playerEntity.getY()) + ", " + n2 + "]";
    }

    private static void drawSegs(InfoHud.Seg[] segArray, float f, float f2) {
        for (float[] fArray : OUTLINE_DIRS) {
            float f3 = f;
            for (InfoHud.Seg seg : segArray) {
                Render2D.msdfText(FONT, seg.text, f3 + fArray[0] * 0.2f, f2 + fArray[1] * 0.2f, 7.0f, -16777216);
                f3 += Render2D.msdfWidth(FONT, seg.text, 7.0f);
            }
        }
        float f4 = f;
        for (InfoHud.Seg seg : segArray) {
            Render2D.msdfText(FONT, seg.text, f4, f2, 7.0f, seg.color);
            f4 += Render2D.msdfWidth(FONT, seg.text, 7.0f);
        }
    }

    private static String bps(PlayerEntity playerEntity) {
        double d = playerEntity.getX() - playerEntity.lastRenderX;
        double d2 = playerEntity.getZ() - playerEntity.lastRenderZ;
        double d3 = Math.sqrt(d * d + d2 * d2) * 20.0;
        return String.format(Locale.ROOT, "%.2f", d3);
    }

    private static String coords(double d, double d2, double d3) {
        if (StreamerMode.hideCoords()) {
            return "#, #, #";
        }
        return (int)Math.floor(d) + ", " + (int)Math.floor(d2) + ", " + (int)Math.floor(d3);
    }

    public static float reservedRightHeight() {
        return 36.0f;
    }


    public record Seg(String text, int color) {
}
}

