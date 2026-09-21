package rtx.byazen.api.drags;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.HudGroupsModule;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.render2d.Render2D;

/**
 * Общие подложки для групп виджетов (идея №31 из IDEAS.md): виджеты, стоящие в одной колонке
 * (или строке) и близко друг к другу, получают один общий блок с общим фоном и обводкой.
 */
public final class HudGroupRenderer {

    private HudGroupRenderer() {
    }

    public static void render(DrawContext drawContext, List<Draggable> elements) {
        HudGroupsModule module = ModuleManager.get().get(HudGroupsModule.class);
        if (module == null || !module.isEnabled()) {
            return;
        }
        List<Draggable> visible = new ArrayList<Draggable>();
        for (Draggable draggable : elements) {
            if (draggable == null || !draggable.isVisible() || !draggable.isInteractive()) {
                continue;
            }
            if (draggable.width() <= 0.0f || draggable.height() <= 0.0f) {
                continue;
            }
            visible.add(draggable);
        }
        if (visible.size() < 2) {
            return;
        }
        boolean horizontal = module.direction.is("Горизонтально");
        float tolerance = Math.max(1.0f, module.tolerance.getValue());
        float clusterGap = Math.max(4.0f, module.gap.getValue() + 34.0f);
        visible.sort(horizontal ? Comparator.comparingDouble(HudGroupRenderer::x) : Comparator.comparingDouble(HudGroupRenderer::y));

        List<Draggable> cluster = new ArrayList<Draggable>();
        float clusterKey = 0.0f;
        float lastEdge = 0.0f;
        for (Draggable draggable : visible) {
            float key = horizontal ? HudGroupRenderer.y(draggable) : HudGroupRenderer.x(draggable);
            float edge = horizontal ? HudGroupRenderer.y(draggable) + draggable.height() : HudGroupRenderer.y(draggable) + draggable.height();
            if (cluster.isEmpty()) {
                cluster.add(draggable);
                clusterKey = key;
                lastEdge = edge;
                continue;
            }
            float leadEdge = horizontal ? HudGroupRenderer.x(draggable) : HudGroupRenderer.y(draggable);
            float mainEdge = horizontal ? HudGroupRenderer.x(cluster.get(cluster.size() - 1)) + cluster.get(cluster.size() - 1).width()
                    : HudGroupRenderer.y(cluster.get(cluster.size() - 1)) + cluster.get(cluster.size() - 1).height();
            boolean aligned = Math.abs(key - clusterKey) <= tolerance;
            boolean near = leadEdge - mainEdge <= clusterGap;
            if (aligned && near) {
                cluster.add(draggable);
                lastEdge = Math.max(lastEdge, edge);
                continue;
            }
            HudGroupRenderer.drawGroup(drawContext, module, cluster);
            cluster.clear();
            cluster.add(draggable);
            clusterKey = key;
            lastEdge = edge;
        }
        HudGroupRenderer.drawGroup(drawContext, module, cluster);
    }

    private static void drawGroup(DrawContext drawContext, HudGroupsModule module, List<Draggable> cluster) {
        if (cluster.size() < 2) {
            return;
        }
        float left = Float.MAX_VALUE;
        float top = Float.MAX_VALUE;
        float right = -Float.MAX_VALUE;
        float bottom = -Float.MAX_VALUE;
        for (Draggable draggable : cluster) {
            left = Math.min(left, HudGroupRenderer.x(draggable));
            top = Math.min(top, HudGroupRenderer.y(draggable));
            right = Math.max(right, HudGroupRenderer.x(draggable) + draggable.width());
            bottom = Math.max(bottom, HudGroupRenderer.y(draggable) + draggable.height());
        }
        float padding = module.padding.getValue();
        left -= padding;
        top -= padding;
        right += padding;
        bottom += padding;
        float radius = Math.max(4.0f, module.radius.getValue());
        Render2D.beginFrame(drawContext);
        if (module.backdrop.getValue()) {
            RectUtil.drawClientRect(left, top, right - left, bottom - top, radius, module.alpha.getValue() / 100.0f);
        }
        else {
            Render2D.rect(left, top, right - left, bottom - top, radius,
                    ClientAccent.rgba(0x0C0D14, 96.0f * module.alpha.getValue() / 100.0f));
        }
        if (module.frame.getValue()) {
            Render2D.outline(left, top, right - left, bottom - top, radius, 0.8f,
                    ClientAccent.accentSoft(60.0f * module.alpha.getValue() / 100.0f));
        }
        if (module.separators.getValue()) {
            for (int i = 0; i < cluster.size() - 1; ++i) {
                Draggable current = cluster.get(i);
                float lineY = HudGroupRenderer.y(current) + current.height() + module.gap.getValue() * 0.5f + 2.0f;
                Render2D.rect(left + padding * 0.6f, lineY, (right - left) - padding * 1.2f, 0.8f, 0.4f,
                        ClientAccent.accentSoft(34.0f * module.alpha.getValue() / 100.0f));
            }
        }
        Render2D.flush();
    }

    private static float x(Draggable draggable) {
        try {
            return draggable.getX();
        }
        catch (Throwable throwable) {
            return 0.0f;
        }
    }

    private static float y(Draggable draggable) {
        try {
            return draggable.getY();
        }
        catch (Throwable throwable) {
            return 0.0f;
        }
    }
}
