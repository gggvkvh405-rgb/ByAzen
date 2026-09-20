package rtx.kimiko.api.modules.impl.Utils.guishare;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.util.math.Vec3d;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.ui.window.GuiShatterAnimation;

public class RemoteGuiPanel {
    private GuiShareRemoteState state;
    private RemoteTheme theme;
    private final Set<String> enabledSet = new HashSet<>();
    private boolean closed;

    public RemoteGuiPanel(GuiShareRemoteState state) {
        this.state = state;
        this.theme = RemoteTheme.fromState(state != null ? state.theme() : null);
        if (state != null && state.enabledModules() != null) {
            enabledSet.addAll(state.enabledModules());
        }
    }

    public void update(GuiShareRemoteState newState) {
        this.state = newState;
        this.theme = RemoteTheme.fromState(newState != null ? newState.theme() : null);
        this.enabledSet.clear();
        if (newState != null && newState.enabledModules() != null) {
            enabledSet.addAll(newState.enabledModules());
        }
    }

    public void markClosed() {
        this.closed = true;
    }

    public boolean isFinished() {
        return this.closed;
    }

    public GuiShareRemoteState state() { return state; }
    public RemoteTheme theme() { return theme; }
    public Set<String> enabledSet() { return enabledSet; }

    public float animScale() { return 1.0f; }
    public boolean closeActive() { return false; }
    public float closeAlpha() { return 1.0f; }
    public float contentAlpha() { return 1.0f; }
    public Category contentCategory() {
        if (state == null || state.category() == null) return Category.VISUALS;
        try {
            return Category.valueOf(state.category().toUpperCase());
        } catch (Exception e) {
            return Category.VISUALS;
        }
    }
    public Category targetCategory() { return contentCategory(); }
    public float categoryAnimT() { return 1.0f; }
    public float categoryAnimT(Category cat) { return 1.0f; }
    public float categoryT() { return 1.0f; }
    public float eventsHeaderT() { return 1.0f; }
    public float eventsSubT() { return 1.0f; }
    public float eventsSubT(int sub) { return 1.0f; }
    public float modulesHeaderT() { return 1.0f; }
    public float placeholderT() { return 0.0f; }
    public String popupDisplayed() { return state != null ? state.popupModule() : null; }
    public float popupHoldX() { return state != null ? state.popupX() : 0.0f; }
    public float popupHoldY() { return state != null ? state.popupY() : 0.0f; }
    public List<GuiSharePopupRow> popupRows() { return state != null ? state.popupRows() : List.of(); }
    public float popupT() { return (state != null && state.popupModule() != null && !state.popupModule().isEmpty()) ? 1.0f : 0.0f; }
    public void resolveAnchor() {}
    public void resolveAnchor(Vec3d anchor) {}
    public Vec3d resolvedAnchor() { return state != null ? state.anchor() : Vec3d.ZERO; }
    public float resolvedPitch() { return state != null ? state.pitch() : 0.0f; }
    public float resolvedYaw() { return state != null ? state.yaw() : 0.0f; }
    public float smoothPitch() { return resolvedPitch(); }
    public float smoothYaw() { return resolvedYaw(); }
    public float smoothScroll() { return state != null ? state.listScroll() : 0.0f; }
    public float smoothEventsScroll() { return state != null ? state.eventsScroll() : 0.0f; }
    public float smoothThemesScroll() { return state != null ? state.themesScroll() : 0.0f; }
    public float smoothPopupScroll() { return state != null ? state.popupScroll() : 0.0f; }
    public float rowAppear(int index) { return 1.0f; }
    public void markAppearFrameDone() {}
    public void updateSmoothing(long time) {}

    public boolean isRenderable() {
        return this.state != null;
    }

    public boolean shatterActive() { return false; }
    public float shatterProgressForRender() { return 0.0f; }
    public float[] shatterRect() { return new float[]{0, 0, 430, 290}; }
    public GuiShatterAnimation.State shatterState() { return null; }
}