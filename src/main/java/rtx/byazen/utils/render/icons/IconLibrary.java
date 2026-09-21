package rtx.byazen.utils.render.icons;

import rtx.byazen.utils.render.render2d.Render2D;

/**
 * Векторная библиотека иконок ByAzen (идея №35 из IDEAS.md).
 * <p>
 * Никаких битмапов и пикселей: каждый значок — набор сглаженных фигур (прямоугольники со скруглением,
 * окружности и линии), поэтому он остаётся чётким при любом размере и масштабе интерфейса.
 */
public final class IconLibrary {

    /** Все доступные значки в порядке показа в настройках. */
    public static final String[] NAMES = {
            "Нет", "Солнце", "Луна", "Звезда", "Сердце", "Меч", "Щит", "Молния", "Часы", "Музыка",
            "Карта", "Огонь", "Лист", "Прицел", "Монета", "Кирка", "Капля", "Глаз"
    };

    private IconLibrary() {
    }

    public static void draw(String name, float x, float y, float size, int color) {
        if (name == null || size <= 0.0f || "Нет".equalsIgnoreCase(name)) {
            return;
        }
        String key = name.toLowerCase(java.util.Locale.ROOT);
        float centerX = x + size * 0.5f;
        float centerY = y + size * 0.5f;
        float stroke = Math.max(0.9f, size * 0.1f);
        switch (key) {
            case "солнце": {
                Render2D.circle(centerX, centerY, size * 0.24f, color);
                Render2D.circleOutline(centerX, centerY, size * 0.36f, stroke, color);
                for (int i = 0; i < 8; ++i) {
                    float angle = (float) i * 0.7853982f;
                    float cos = (float) Math.cos(angle);
                    float sin = (float) Math.sin(angle);
                    Render2D.line(centerX + cos * size * 0.4f, centerY + sin * size * 0.4f,
                            centerX + cos * size * 0.47f, centerY + sin * size * 0.47f, stroke, color);
                }
                break;
            }
            case "луна": {
                // Полумесяц: дуга из маленьких окружностей, без битмапов и пикселей.
                float radius = size * 0.34f;
                float dot = Math.max(1.1f, size * 0.1f);
                for (int i = 0; i <= 22; ++i) {
                    float angle = -2.2f + (float) i * 0.2f;
                    Render2D.circle(centerX + (float) Math.cos(angle) * radius, centerY + (float) Math.sin(angle) * radius, dot, color);
                }
                break;
            }
            case "звезда": {
                for (int i = 0; i < 5; ++i) {
                    float angle = -1.5707964f + (float) i * 1.2566371f;
                    float nextAngle = angle + 2.5132742f;
                    Render2D.line(centerX + (float) Math.cos(angle) * size * 0.46f, centerY + (float) Math.sin(angle) * size * 0.46f,
                            centerX + (float) Math.cos(nextAngle) * size * 0.46f, centerY + (float) Math.sin(nextAngle) * size * 0.46f,
                            stroke, color);
                }
                break;
            }
            case "сердце": {
                float radius = size * 0.2f;
                Render2D.circle(centerX - radius * 0.85f, centerY - radius * 0.5f, radius * 0.95f, color);
                Render2D.circle(centerX + radius * 0.85f, centerY - radius * 0.5f, radius * 0.95f, color);
                for (int i = 0; i < 9; ++i) {
                    float step = (float) i / 8.0f;
                    float halfWidth = radius * 1.85f * (1.0f - step);
                    Render2D.rect(centerX - halfWidth, centerY + radius * 0.1f + step * size * 0.3f,
                            halfWidth * 2.0f, size * 0.045f + 0.6f, 0.0f, color);
                }
                break;
            }
            case "меч": {
                Render2D.rect(centerX - stroke * 0.5f, y + size * 0.08f, stroke, size * 0.66f, stroke * 0.5f, color);
                Render2D.rect(x + size * 0.22f, y + size * 0.62f, size * 0.56f, stroke, stroke * 0.5f, color);
                Render2D.rect(centerX - stroke * 0.5f, y + size * 0.66f, stroke, size * 0.26f, stroke * 0.5f, color);
                break;
            }
            case "щит": {
                Render2D.rect(x + size * 0.18f, y + size * 0.12f, size * 0.64f, size * 0.4f, size * 0.16f, color);
                for (int i = 0; i < 8; ++i) {
                    float step = (float) i / 7.0f;
                    float halfWidth = size * 0.32f * (1.0f - step);
                    Render2D.rect(centerX - halfWidth, y + size * 0.5f + step * size * 0.34f,
                            halfWidth * 2.0f, size * 0.05f + 0.6f, 1.0f, color);
                }
                break;
            }
            case "молния": {
                for (int i = 0; i < 8; ++i) {
                    float step = (float) i / 7.0f;
                    Render2D.rect(x + size * (0.46f - step * 0.22f), y + size * (0.1f + step * 0.8f),
                            size * 0.24f, size * 0.12f, 1.0f, color);
                }
                break;
            }
            case "часы": {
                Render2D.circleOutline(centerX, centerY, size * 0.42f, stroke, color);
                Render2D.line(centerX, centerY, centerX, centerY - size * 0.24f, stroke, color);
                Render2D.line(centerX, centerY, centerX + size * 0.18f, centerY + size * 0.06f, stroke, color);
                break;
            }
            case "музыка": {
                Render2D.rect(x + size * 0.5f, y + size * 0.14f, stroke, size * 0.52f, stroke * 0.5f, color);
                Render2D.rect(x + size * 0.26f, y + size * 0.22f, stroke, size * 0.44f, stroke * 0.5f, color);
                Render2D.circle(x + size * 0.3f, y + size * 0.72f, size * 0.14f, color);
                Render2D.circle(x + size * 0.54f, y + size * 0.64f, size * 0.14f, color);
                break;
            }
            case "карта": {
                Render2D.rect(x + size * 0.12f, y + size * 0.16f, size * 0.32f, size * 0.62f, 1.5f, color);
                Render2D.rect(x + size * 0.5f, y + size * 0.12f, size * 0.32f, size * 0.62f, 1.5f, color);
                Render2D.line(x + size * 0.44f, y + size * 0.16f, x + size * 0.44f, y + size * 0.78f, stroke, color);
                break;
            }
            case "огонь": {
                for (int i = 0; i < 9; ++i) {
                    float step = (float) i / 8.0f;
                    float width = size * (0.36f - Math.abs(step - 0.4f) * 0.5f);
                    Render2D.rect(centerX - width * 0.5f, y + size * (0.16f + step * 0.68f), width, size * 0.09f, width * 0.4f, color);
                }
                break;
            }
            case "лист": {
                Render2D.circleOutline(centerX, centerY, size * 0.34f, stroke, color);
                Render2D.line(centerX - size * 0.24f, centerY + size * 0.24f, centerX + size * 0.24f, centerY - size * 0.24f, stroke, color);
                Render2D.line(centerX - size * 0.02f, centerY - size * 0.02f, centerX + size * 0.28f, centerY - size * 0.02f, stroke * 0.8f, color);
                break;
            }
            case "прицел": {
                Render2D.circleOutline(centerX, centerY, size * 0.36f, stroke, color);
                Render2D.line(centerX, y + size * 0.08f, centerX, y + size * 0.26f, stroke, color);
                Render2D.line(centerX, y + size * 0.74f, centerX, y + size * 0.92f, stroke, color);
                Render2D.line(x + size * 0.08f, centerY, x + size * 0.26f, centerY, stroke, color);
                Render2D.line(x + size * 0.74f, centerY, x + size * 0.92f, centerY, stroke, color);
                break;
            }
            case "монета": {
                Render2D.circle(centerX, centerY, size * 0.4f, color);
                Render2D.rect(centerX - size * 0.05f, centerY - size * 0.22f, size * 0.1f, size * 0.44f, 1.0f, 0x66000000);
                break;
            }
            case "кирка": {
                Render2D.rect(centerX - stroke * 0.5f, y + size * 0.2f, stroke, size * 0.68f, stroke * 0.5f, color);
                for (int i = 0; i < 9; ++i) {
                    float step = (float) i / 8.0f;
                    float offset = Math.abs(step - 0.5f) * size * 0.7f;
                    Render2D.rect(x + size * 0.1f + offset, y + size * (0.1f + step * 0.06f), size * 0.12f, size * 0.12f, 1.0f, color);
                }
                break;
            }
            case "капля": {
                Render2D.circle(centerX, centerY + size * 0.12f, size * 0.26f, color);
                for (int i = 0; i < 7; ++i) {
                    float step = (float) i / 6.0f;
                    float width = size * 0.22f * step;
                    Render2D.rect(centerX - width * 0.5f, y + size * (0.12f + step * 0.28f), width, size * 0.05f + 0.5f, 0.0f, color);
                }
                break;
            }
            case "глаз": {
                Render2D.circle(centerX, centerY, size * 0.3f, color);
                Render2D.circle(centerX, centerY, size * 0.14f, 0x66000000);
                for (int i = 0; i < 6; ++i) {
                    float step = (float) i / 5.0f;
                    float width = size * (0.5f - Math.abs(step - 0.5f) * 0.5f);
                    Render2D.rect(centerX - width, y + size * (0.2f + step * 0.6f), width * 2.0f, size * 0.035f + 0.4f, 0.0f, color);
                }
                break;
            }
            default: {
                Render2D.circleOutline(centerX, centerY, size * 0.36f, stroke, color);
                break;
            }
        }
    }
}
