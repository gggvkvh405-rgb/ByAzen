package rtx.byazen.api.ui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.KeyInput;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import rtx.byazen.api.modules.impl.Visuals.PetStyle;
import rtx.byazen.utils.cosmetics.Cosmetics;
import rtx.byazen.utils.pets.PetStyleData;
import rtx.byazen.utils.profile.ProfileCovers;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.api.drags.Position;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.render2d.Render2D;
import rtx.byazen.utils.render.render2d.gif.GifRenderer;
import rtx.byazen.utils.sounds.SoundManager;

/**
 * Анимированные обложки профиля (идея №131 из IDEAS.md).
 * <p>
 * Каждый игрок показан карточкой с живой обложкой: мягкий градиент с плавающими бликами, который
 * действительно движется. Свою обложку можно выбрать одним кликом слева, справа видно, как она
 * выглядит в карточке — рядом имя, уровень Cosmetic-коллекции и уровень питомца.
 */
public final class ProfileCoverScreen
extends BaseScreen {

    private static final String FONT_BOLD = "montserrat-bold";
    private static final String FONT = "montserrat-medium";
    private static final float W = 520.0f;
    private static final float H = 268.0f;
    private static final float PAD = 14.0f;
    private static final float CARD_H = 74.0f;

    private final List<ProfileCovers.Cover> covers = new ArrayList<ProfileCovers.Cover>();
    private int selected;
    private int scroll;
    private float animation;
    private long lastFrameNanos;

    public ProfileCoverScreen() {
        super(Text.literal("Обложки профиля ByAzen"));
        this.covers.addAll(ProfileCovers.all());
        ProfileCovers.Cover own = ProfileCovers.own();
        this.selected = Math.max(0, this.covers.indexOf(own));
    }

    private static float panelX() {
        return (Position.screenWidth() - W) * 0.5f;
    }

    private static float panelY() {
        return (Position.screenHeight() - H) * 0.5f;
    }

    private static float listX() {
        return ProfileCoverScreen.panelX() + PAD;
    }

    private static float listY() {
        return ProfileCoverScreen.panelY() + PAD + 34.0f;
    }

    private static float listW() {
        return 250.0f;
    }

    private static float cardX() {
        return ProfileCoverScreen.listX() + listW() + 12.0f;
    }

    private static float cardY() {
        return ProfileCoverScreen.listY();
    }

    @Override
    protected void renderScreen(DrawContext drawContext, int mouseX, int mouseY, float deltaTicks) {
        float delta = this.deltaSeconds();
        this.animation += (1.0f - this.animation) * Math.min(1.0f, delta * 9.0f);
        float a = Math.max(0.0f, Math.min(1.0f, this.animation));
        float x = ProfileCoverScreen.panelX();
        float y = ProfileCoverScreen.panelY();

        Render2D.blur(x, y, W, H, 12.0f, 14.0f, a, -1);
        RectUtil.drawClientRect(x, y, W, H, 12.0f, a);
        Render2D.outline(x, y, W, H, 12.0f, 1.0f, ClientAccent.accentSoft(38.0f * a));

        Render2D.msdfText(FONT_BOLD, "Обложки профиля", x + PAD, y + PAD, 11.0f, ProfileCoverScreen.ink(a));
        Render2D.msdfText(FONT, "живые обложки ByAzen", x + PAD + Render2D.msdfWidth(FONT_BOLD, "Обложки профиля", 11.0f) + 8.0f,
                y + PAD + 3.0f, 7.4f, ProfileCoverScreen.sub(a));
        Render2D.msdfText(FONT_BOLD, "✕", x + W - PAD - 4.0f, y + 10.0f, 9.0f, ProfileCoverScreen.sub(a * 0.9f));

        Render2D.pushScissor(drawContext, ProfileCoverScreen.listX(), ProfileCoverScreen.listY(), listW(),
                ProfileCoverScreen.cardY() - ProfileCoverScreen.listY() + 150.0f);
        float rowY = ProfileCoverScreen.listY() - (float)this.scroll;
        for (int i = 0; i < this.covers.size(); ++i) {
            ProfileCovers.Cover cover = this.covers.get(i);
            boolean active = i == this.selected;
            boolean hovered = mouseX >= ProfileCoverScreen.listX() && mouseX <= ProfileCoverScreen.listX() + listW()
                    && (float)mouseY >= rowY && (float)mouseY <= rowY + CARD_H;
            Render2D.rect(ProfileCoverScreen.listX(), rowY, listW(), CARD_H, 7.0f,
                    active ? ProfileCoverScreen.selected(a) : hovered ? ProfileCoverScreen.hover(a) : ProfileCoverScreen.field(a));
            GifRenderer.draw(drawContext, ProfileCoverScreen.listX() + 6.0f, rowY + 6.0f, 104.0f, 62.0f, 6.0f, cover.path(), a);
            Render2D.rect(ProfileCoverScreen.listX() + 6.0f, rowY + 52.0f, 104.0f, 16.0f, 4.0f, ProfileCoverScreen.rgba(8, 10, 16, 120.0f * a));
            Render2D.msdfText(FONT_BOLD, cover.name, ProfileCoverScreen.listX() + 118.0f, rowY + 12.0f, 9.0f, ProfileCoverScreen.ink(a));
            Render2D.msdfText(FONT, ProfileCoverScreen.seasonText(cover), ProfileCoverScreen.listX() + 118.0f, rowY + 27.0f, 6.6f,
                    ProfileCoverScreen.sub(a * 0.9f));
            boolean own = ProfileCovers.own() == cover;
            Render2D.msdfText(FONT, own ? "выбрана" : "нажмите, чтобы поставить", ProfileCoverScreen.listX() + 118.0f, rowY + 42.0f,
                    6.4f, own ? ClientAccent.accent(a) : ProfileCoverScreen.sub(a * 0.6f));
            Render2D.msdfText(FONT, cosmetctLine(cover), ProfileCoverScreen.listX() + 118.0f, rowY + 56.0f, 6.2f,
                    ProfileCoverScreen.sub(a * 0.75f));
            rowY += CARD_H + 6.0f;
        }
        Render2D.popScissor(drawContext);

        this.renderCard(drawContext, a);

        Render2D.msdfText(FONT, "ЛКМ — выбрать обложку · ПКМ — посмотреть карточку · Esc — закрыть",
                x + PAD, y + H - PAD - 6.0f, 6.8f, ProfileCoverScreen.sub(a * 0.85f));
    }

    /** Карточка игрока с живой обложкой и подписью. */
    private void renderCard(DrawContext drawContext, float a) {
        float cx = ProfileCoverScreen.cardX();
        float cy = ProfileCoverScreen.cardY();
        float width = W - (cx - ProfileCoverScreen.panelX()) - PAD;
        ProfileCovers.Cover cover = this.current();
        if (cover == null) {
            return;
        }
        Render2D.rect(cx, cy, width, CARD_H + 46.0f, 8.0f, ProfileCoverScreen.field(a));
        GifRenderer.draw(drawContext, cx + 4.0f, cy + 4.0f, width - 8.0f, CARD_H, 8.0f, cover.path(), a);
        Render2D.rect(cx + 4.0f, cy + 44.0f, width - 8.0f, 34.0f, 8.0f, ProfileCoverScreen.rgba(8, 10, 16, 150.0f * a));

        String name = ProfileCoverScreen.playerName();
        Render2D.msdfText(FONT_BOLD, name, cx + 12.0f, cy + 48.0f, 10.0f, ProfileCoverScreen.ink(a));
        Render2D.msdfText(FONT, "уровень питомца " + PetStyleData.getLevel() + " · косметика " + Cosmetics.ownedCount() + "/"
                + Cosmetics.totalCount(), cx + 12.0f, cy + 62.0f, 6.6f, ProfileCoverScreen.sub(a * 0.9f));
        Render2D.circle(cx + width - 18.0f, cy + 60.0f, 6.0f, ClientAccent.accent(a));
        Render2D.msdfText(FONT_BOLD, "ByAzen", cx + width - 54.0f, cy + 57.0f, 6.6f, ProfileCoverScreen.sub(a * 0.9f));

        Render2D.msdfText(FONT, "Настроение питомца", cx + 4.0f, cy + CARD_H + 54.0f, 6.6f, ProfileCoverScreen.sub(a * 0.85f));
        float bar = width - 8.0f;
        Render2D.rect(cx + 4.0f, cy + CARD_H + 66.0f, bar, 5.0f, 2.5f, ProfileCoverScreen.rgba(255, 255, 255, 26.0f * a));
        Render2D.rect(cx + 4.0f, cy + CARD_H + 66.0f, bar * ((float)PetStyleData.getMood() / 100.0f), 5.0f, 2.5f,
                ClientAccent.accent(a));
    }

    private static String cosmetctLine(ProfileCovers.Cover cover) {
        if ("snow".equals(cover.season)) {
            return Cosmetics.seasonNow(rtx.byazen.utils.cosmetics.Cosmetic.FLAVOR_SNOW) ? "сезон: зима" : "набор «Зима»";
        }
        if ("neon".equals(cover.season)) {
            return Cosmetics.seasonNow(rtx.byazen.utils.cosmetics.Cosmetic.FLAVOR_NEON) ? "сезон: лето" : "набор «Неон»";
        }
        if ("space".equals(cover.season)) {
            return Cosmetics.seasonNow(rtx.byazen.utils.cosmetics.Cosmetic.FLAVOR_SPACE) ? "сезон: осень" : "набор «Космос»";
        }
        return "универсальная";
    }

    private static String seasonText(ProfileCovers.Cover cover) {
        return "обложка профиля ByAzen · " + cover.id;
    }

    private static String playerName() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return "ByAzen";
        }
        return client.player.getName().getString();
    }

    private ProfileCovers.Cover current() {
        return this.selected >= 0 && this.selected < this.covers.size() ? this.covers.get(this.selected) : null;
    }

    private float deltaSeconds() {
        long now = System.nanoTime();
        float delta = this.lastFrameNanos == 0L ? 0.016f : Math.min(0.1f, (float)(now - this.lastFrameNanos) / 1.0E9f);
        this.lastFrameNanos = now;
        return delta;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        float x = ProfileCoverScreen.panelX();
        float y = ProfileCoverScreen.panelY();
        float mouseX = Position.mouseX();
        float mouseY = Position.mouseY();
        if (click.button() == 0 && mouseX >= x + W - PAD - 14.0f && mouseX <= x + W - PAD
                && mouseY >= y + 8.0f && mouseY <= y + 24.0f) {
            this.closeScreen();
            return true;
        }
        if (click.button() == 0 && mouseX >= ProfileCoverScreen.listX()
                && mouseX <= ProfileCoverScreen.listX() + listW()) {
            float rowY = ProfileCoverScreen.listY() - (float)this.scroll;
            for (int i = 0; i < this.covers.size(); ++i) {
                if (mouseY >= rowY && mouseY <= rowY + CARD_H) {
                    this.selected = i;
                    ProfileCovers.setOwn(this.covers.get(i).id);
                    SoundManager.playSound(SoundManager.MODULE_ENABLE, 0.55f, 1.0f);
                    return true;
                }
                rowY += CARD_H + 6.0f;
            }
        }
        if (click.button() == 1) {
            PetStyle.openGames();
            return true;
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        float listBottom = ProfileCoverScreen.listY() + 150.0f;
        if (mouseX >= (double)ProfileCoverScreen.listX() && mouseX <= (double)(ProfileCoverScreen.listX() + listW())
                && mouseY >= (double)ProfileCoverScreen.listY() && mouseY <= (double)listBottom) {
            int max = Math.max(0, (int)((float)this.covers.size() * (CARD_H + 6.0f) - 150.0f));
            this.scroll = Math.max(0, Math.min(max, this.scroll - (int)(verticalAmount * 18.0)));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        int key = input.key();
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            this.closeScreen();
            return true;
        }
        if (key == GLFW.GLFW_KEY_DOWN && this.selected + 1 < this.covers.size()) {
            this.selected++;
            this.ensureVisible();
            return true;
        }
        if (key == GLFW.GLFW_KEY_UP && this.selected > 0) {
            this.selected--;
            this.ensureVisible();
            return true;
        }
        if (key == GLFW.GLFW_KEY_ENTER && this.current() != null) {
            ProfileCovers.setOwn(this.current().id);
            SoundManager.playSound(SoundManager.MODULE_ENABLE, 0.55f, 1.0f);
            return true;
        }
        return super.keyPressed(input);
    }

    private void ensureVisible() {
        float top = (float)this.selected * (CARD_H + 6.0f);
        if (top < (float)this.scroll) {
            this.scroll = (int)top;
        }
        if (top + CARD_H > (float)this.scroll + 150.0f) {
            this.scroll = (int)(top + CARD_H - 150.0f);
        }
    }

    private void closeScreen() {
        BaseScreen.beginClosingOverlay(this);
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.setScreen(null);
        }
    }

    private static int ink(float alpha) {
        return ProfileCoverScreen.rgba(233, 238, 248, 240.0f * alpha);
    }

    private static int sub(float alpha) {
        return ProfileCoverScreen.rgba(168, 176, 194, 230.0f * alpha);
    }

    private static int field(float alpha) {
        return ProfileCoverScreen.rgba(255, 255, 255, 14.0f * alpha);
    }

    private static int hover(float alpha) {
        return ProfileCoverScreen.rgba(255, 255, 255, 24.0f * alpha);
    }

    private static int selected(float alpha) {
        return ProfileCoverScreen.rgba(255, 255, 255, 34.0f * alpha);
    }

    private static int rgba(int r, int g, int b, float alpha) {
        int a = Math.max(0, Math.min(255, (int)alpha));
        return a << 24 | r << 16 | g << 8 | b;
    }

    /** Игроки рядом — их карточки с обложками можно листать. */
    public static List<String> nearby() {
        ArrayList<String> names = new ArrayList<String>();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.world == null) {
            return names;
        }
        for (PlayerEntity player : client.world.getPlayers()) {
            names.add(player.getName().getString());
        }
        return names;
    }
}
