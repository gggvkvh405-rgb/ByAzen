package rtx.byazen.api.ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import rtx.byazen.api.drags.Position;
import rtx.byazen.api.modules.impl.Visuals.CustomPet;
import rtx.byazen.api.modules.impl.Visuals.custompet.entity.CustomPetEntity;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.utils.cosmetics.CosmeticSounds;
import rtx.byazen.utils.pets.PetStyleData;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.render2d.Render2D;
import rtx.byazen.utils.sounds.Sounds;

/**
 * Мини-игры с питомцем (идея №128 из IDEAS.md).
 * <p>
 * Панель заботы: кормление, ласка, игрушка и команда «ко мне». Каждое действие поднимает настроение и
 * даёт опыт, за уровень питомец радостно пищит. Реакции на бой, тотем и смерть живут в модуле
 * «Pets 2.0», а здесь ими можно управлять вручную — и заодно переименовать питомца.
 */
public final class PetPlayScreen
extends BaseScreen {

    private static final String FONT_BOLD = "montserrat-bold";
    private static final String FONT = "montserrat-medium";
    private static final float W = 372.0f;
    private static final float H = 208.0f;
    private static final float PAD = 14.0f;
    private static final String[] ACTIONS = {"Покормить", "Погладить", "Игрушка", "Ко мне"};
    private static final String[] HINTS = {
            "Хрустящий корм: питомец жуёт и довольно крутит хвостом.",
            "Тёплая ласка: питомец мурчит и светится сильнее.",
            "Любимая игрушка: короткая весёлая погоня за мячиком.",
            "Питомец подбегает к вам и садится рядом."
    };

    private float openAnimation;
    private long lastFrameNanos;
    private int hovered = -1;
    private String status = "";
    private long statusUntil;
    private boolean renaming;
    private final StringBuilder name = new StringBuilder();

    public PetPlayScreen() {
        super(Text.literal("Питомец"));
        PetStyleData.ensureLoaded();
        this.name.append(PetStyleData.getName());
    }

    private static float panelX() {
        return (Position.screenWidth() - W) * 0.5f;
    }

    private static float panelY() {
        return (Position.screenHeight() - H) * 0.5f;
    }

    private static CustomPetEntity pet() {
        CustomPet module = CustomPet.getInstance();
        return module == null ? null : module.localPet();
    }

    @Override
    protected void renderScreen(DrawContext drawContext, int mouseX, int mouseY, float deltaTicks) {
        float delta = this.deltaSeconds();
        this.openAnimation += (1.0f - this.openAnimation) * Math.min(1.0f, delta * 9.0f);
        float a = Math.max(0.0f, Math.min(1.0f, this.openAnimation));
        float x = PetPlayScreen.panelX();
        float y = PetPlayScreen.panelY();

        Render2D.blur(x, y, W, H, 12.0f, 14.0f, a, -1);
        RectUtil.drawClientRect(x, y, W, H, 12.0f, a);
        Render2D.outline(x, y, W, H, 12.0f, 1.0f, ClientAccent.accentSoft(38.0f * a));
        Render2D.msdfText(FONT_BOLD, "Питомец", x + PAD, y + PAD, 11.0f, PetPlayScreen.ink(a));
        String level = "уровень " + PetStyleData.getLevel() + " · " + PetStyleData.getXp() + "/" + PetStyleData.xpForNextLevel() + " опыта";
        Render2D.msdfText(FONT, level, x + PAD + Render2D.msdfWidth(FONT_BOLD, "Питомец", 11.0f) + 8.0f, y + PAD + 3.0f, 7.6f,
                PetPlayScreen.sub(a));
        Render2D.msdfText(FONT_BOLD, "✕", x + W - PAD - 4.0f, y + 10.0f, 9.0f, PetPlayScreen.sub(a * 0.9f));

        // карточка питомца: иконка, имя, настроение
        float cardY = y + PAD + 24.0f;
        Render2D.rect(x + PAD, cardY, W - PAD * 2.0f, 46.0f, 8.0f, PetPlayScreen.listBackground(a));
        drawContext.getMatrices().pushMatrix();
        drawContext.getMatrices().translate(x + PAD + 8.0f, cardY + 7.0f);
        drawContext.getMatrices().scale(2.0f, 2.0f);
        drawContext.drawItem(new ItemStack(Items.BONE), 0, 0);
        drawContext.getMatrices().popMatrix();
        Render2D.msdfText(FONT_BOLD, PetStyleData.getName(), x + PAD + 48.0f, cardY + 8.0f, 9.0f, PetPlayScreen.ink(a));
        Render2D.msdfText(FONT, "Аксессуар: " + PetStyleData.getAccessory() + " · размер " + Math.round(PetStyleData.getScale() * 100.0f) + "%",
                x + PAD + 48.0f, cardY + 20.0f, 6.6f, PetPlayScreen.sub(a * 0.9f));
        float mood = (float)PetStyleData.getMood() / 100.0f;
        Render2D.rect(x + PAD + 48.0f, cardY + 31.0f, W - PAD * 2.0f - 56.0f, 4.0f, 2.0f, PetPlayScreen.field(a));
        Render2D.rect(x + PAD + 48.0f, cardY + 31.0f, (W - PAD * 2.0f - 56.0f) * mood, 4.0f, 2.0f,
                ClientAccent.accent(Math.round(120.0f + 130.0f * a)));
        Render2D.msdfText(FONT, "Настроение " + PetStyleData.getMood() + "%", x + PAD + 48.0f, cardY + 37.0f, 6.2f,
                PetPlayScreen.sub(a * 0.8f));

        // четыре действия
        float actionY = cardY + 56.0f;
        this.hovered = -1;
        for (int i = 0; i < ACTIONS.length; ++i) {
            float buttonX = x + PAD + (float)i * ((W - PAD * 2.0f + 6.0f) / 4.0f);
            float buttonW = (W - PAD * 2.0f - 18.0f) / 4.0f;
            boolean hover = mouseX >= buttonX && mouseX <= buttonX + buttonW && mouseY >= actionY && mouseY <= actionY + 20.0f;
            if (hover) {
                this.hovered = i;
            }
            Render2D.rect(buttonX, actionY, buttonW, 20.0f, 6.0f, hover ? PetPlayScreen.hover(a) : PetPlayScreen.field(a));
            Render2D.outline(buttonX, actionY, buttonW, 20.0f, 6.0f, 1.0f, ClientAccent.accentSoft((hover ? 90.0f : 45.0f) * a));
            float textWidth = Render2D.msdfWidth(FONT, ACTIONS[i], 7.2f);
            Render2D.msdfText(FONT, ACTIONS[i], buttonX + (buttonW - textWidth) * 0.5f, actionY + 6.5f, 7.2f,
                    PetPlayScreen.ink(a));
        }

        String hint = this.hovered >= 0 ? HINTS[this.hovered]
                : this.renaming ? "Введи новое имя и нажми Enter"
                : "Опыт и настроение сохраняются между запусками";
        Render2D.msdfText(FONT, hint, x + PAD, actionY + 30.0f, 6.8f, PetPlayScreen.sub(a * 0.9f));

        // поле имени
        float nameY = actionY + 46.0f;
        Render2D.rect(x + PAD, nameY, W - PAD * 2.0f, 22.0f, 6.0f,
                this.renaming ? PetPlayScreen.field(a) : PetPlayScreen.listBackground(a));
        String shown = this.renaming ? this.name.toString() + "|" : PetStyleData.getName() + "  (нажми «Имя», чтобы переименовать)";
        Render2D.msdfText(FONT, shown, x + PAD + 6.0f, nameY + 7.0f, 7.2f,
                this.renaming ? PetPlayScreen.ink(a) : PetPlayScreen.sub(a * 0.85f));
        Render2D.rect(x + PAD + 6.0f + Render2D.msdfWidth(FONT, "Имя", 6.6f) + 6.0f, nameY + 3.0f, 0.0f, 0.0f, 0.0f, 0);

        String footer = System.currentTimeMillis() < this.statusUntil ? this.status : "ЛКМ — действие · «Имя» — переименовать · Esc — закрыть";
        Render2D.msdfText(FONT, footer, x + PAD, y + H - PAD + 1.0f, 6.4f,
                System.currentTimeMillis() < this.statusUntil ? ClientAccent.accent(Math.round(210.0f * a)) : PetPlayScreen.sub(a * 0.7f));
        if (this.renaming) {
            Render2D.rect(x + W - PAD - 42.0f, y + H - PAD - 2.0f, 42.0f, 12.0f, 3.0f, PetPlayScreen.hover(a));
            Render2D.msdfText(FONT, "Имя", x + W - PAD - 36.0f, y + H - PAD + 1.0f, 6.4f, PetPlayScreen.ink(a));
        }
    }

    private float deltaSeconds() {
        long now = System.nanoTime();
        float delta = this.lastFrameNanos == 0L ? 0.016f : Math.min(0.1f, (float)(now - this.lastFrameNanos) / 1.0E9f);
        this.lastFrameNanos = now;
        return delta;
    }

    private void setStatus(String text) {
        this.status = text;
        this.statusUntil = System.currentTimeMillis() + 2600L;
    }

    /** Действие мини-игры: опыт, настроение и звук. */
    private void play(int index) {
        CustomPetEntity pet = PetPlayScreen.pet();
        switch (index) {
            case 0: {
                CosmeticSounds.play("pet_feed", 0.9f);
                if (pet != null) {
                    pet.requestReaction(1.1, 24, 0.18);
                }
                PetStyleData.changeMood(9);
                this.finishAction("Питомец накормлен и доволен");
                break;
            }
            case 1: {
                CosmeticSounds.play("pet_pet", 0.9f);
                if (pet != null) {
                    pet.requestReaction(1.3, 20, 0.32);
                    pet.playEmotionSound(1.25f);
                }
                PetStyleData.changeMood(7);
                this.finishAction("Питомец мурчит от ласки");
                break;
            }
            case 2: {
                CosmeticSounds.play("pet_toy", 0.9f);
                if (pet != null) {
                    pet.requestReaction(1.6, 30, 0.45);
                }
                PetStyleData.changeMood(11);
                this.finishAction("Игрушка брошена — питомец бежит за ней");
                break;
            }
            default: {
                Sounds.play("select_category");
                MinecraftClient client = MinecraftClient.getInstance();
                if (pet != null && client != null && client.player != null) {
                    Vec3d target = client.player.getEntityPos().add(client.player.getRotationVec(1.0f).multiply(-0.8));
                    pet.snapTo(target, client.player.getYaw() + 180.0f);
                    pet.requestReaction(1.0, 18, 0.25);
                    PetStyleData.changeMood(4);
                    this.finishAction("Питомец подбежал к вам");
                } else {
                    this.finishAction("Питомца сейчас нет рядом");
                }
                break;
            }
        }
    }

    private void finishAction(String message) {
        boolean leveled = PetStyleData.addXp(4);
        if (leveled) {
            CosmeticSounds.play("pet_level", 1.0f);
            this.setStatus("Новый уровень: " + PetStyleData.getLevel() + "!");
        } else {
            this.setStatus(message);
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        float mouseX = Position.mouseX();
        float mouseY = Position.mouseY();
        float x = PetPlayScreen.panelX();
        float y = PetPlayScreen.panelY();
        if (mouseX >= x + W - PAD - 12.0f && mouseX <= x + W - PAD + 6.0f && mouseY >= y + 8.0f && mouseY <= y + 24.0f) {
            Sounds.play("settings_close");
            this.close();
            return true;
        }
        if (this.renaming && mouseX >= x + W - PAD - 44.0f && mouseY >= y + H - PAD - 4.0f && mouseY <= y + H - PAD + 10.0f) {
            this.applyName();
            return true;
        }
        float actionY = y + PAD + 24.0f + 56.0f;
        for (int i = 0; i < ACTIONS.length; ++i) {
            float buttonX = x + PAD + (float)i * ((W - PAD * 2.0f + 6.0f) / 4.0f);
            float buttonW = (W - PAD * 2.0f - 18.0f) / 4.0f;
            if (mouseX >= buttonX && mouseX <= buttonX + buttonW && mouseY >= actionY && mouseY <= actionY + 20.0f) {
                this.play(i);
                return true;
            }
        }
        if (mouseX >= x + PAD && mouseX <= x + W - PAD && mouseY >= actionY + 46.0f && mouseY <= actionY + 68.0f) {
            this.renaming = true;
            Sounds.play("settings_open");
            this.setStatus("Введи имя и нажми Enter");
            return true;
        }
        if (mouseX < x || mouseX > x + W || mouseY < y || mouseY > y + H) {
            Sounds.play("settings_close");
            this.close();
        }
        return true;
    }

    private void applyName() {
        PetStyleData.setName(this.name.toString());
        this.renaming = false;
        this.setStatus("Теперь питомца зовут «" + PetStyleData.getName() + "»");
        Sounds.play("module_settings_close");
        PetStyleData.save();
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        int key = input.key();
        if (this.renaming) {
            if (key == 259 && this.name.length() > 0) {
                this.name.setLength(this.name.length() - 1);
                Sounds.play("search_typing");
                return true;
            }
            if (key == 257 || key == 335) {
                this.applyName();
                return true;
            }
            if (key == 256) {
                this.renaming = false;
                return true;
            }
            return true;
        }
        if (key == 256) {
            Sounds.play("settings_close");
            this.close();
            return true;
        }
        if (key >= 49 && key <= 52) {
            this.play(key - 49);
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean charTyped(CharInput input) {
        if (this.renaming && this.name.length() < 16) {
            this.name.append(new String(Character.toChars(input.codepoint())));
        }
        return true;
    }

    private static int rgba(int red, int green, int blue, float alpha) {
        int a = Math.max(0, Math.min(255, Math.round(alpha)));
        return a << 24 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | (blue & 0xFF);
    }

    private static int ink(float alpha) {
        return PetPlayScreen.rgba(233, 238, 248, 240.0f * alpha);
    }

    private static int sub(float alpha) {
        return PetPlayScreen.rgba(168, 176, 194, 230.0f * alpha);
    }

    private static int field(float alpha) {
        return PetPlayScreen.rgba(16, 18, 24, 190.0f * alpha);
    }

    private static int listBackground(float alpha) {
        return PetPlayScreen.rgba(14, 16, 21, 165.0f * alpha);
    }

    private static int hover(float alpha) {
        return PetPlayScreen.rgba(255, 255, 255, 26.0f * alpha);
    }

    @Override
    public void close() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.setScreen(null);
        }
    }
}
