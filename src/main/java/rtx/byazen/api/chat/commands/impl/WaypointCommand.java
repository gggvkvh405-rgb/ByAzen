package rtx.byazen.api.chat.commands.impl;

import java.util.Arrays;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import rtx.byazen.api.chat.commands.Command;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.WaypointsModule;
import rtx.byazen.api.nav.Waypoint;
import rtx.byazen.api.nav.WaypointStore;

/**
 * Путевые точки из чата (идея №72 из IDEAS.md):
 * {@code waypoint add Дом}, {@code waypoint list}, {@code waypoint go Дом}, {@code waypoint remove Дом}.
 */
public final class WaypointCommand
extends Command {

    public WaypointCommand() {
        super("waypoint", "Путевые точки: добавить, список, дойти, удалить", "wp", "точки", "метки");
    }

    @Override
    public void execute(String label, String[] args) {
        WaypointStore store = WaypointStore.get();
        if (args.length == 0) {
            this.list();
            this.logDirect("Пример: waypoint add Дом — сохранить точку там, где вы стоите.", Formatting.DARK_GRAY);
            return;
        }
        String sub = args[0].toLowerCase();
        if (sub.equals("add") || sub.equals("добавить") || sub.equals("set")) {
            if (args.length < 2) {
                this.usage();
                return;
            }
            String name = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            WaypointsModule module = ModuleManager.get().get(WaypointsModule.class);
            if (module != null) {
                module.saveCurrent(name);
            }
            else {
                Vec3d pos = WaypointStore.playerPos();
                if (pos == null) {
                    this.logDirect("Нет игрока в мире — точку сохранить нельзя.", Formatting.RED);
                    return;
                }
                store.put(new Waypoint(name, Math.floor(pos.x) + 0.5, Math.floor(pos.y), Math.floor(pos.z) + 0.5,
                        WaypointStore.dimensionId(), 0x5AA9FF, ""));
                this.logDirect("Точка «" + name + "» сохранена.", Formatting.GREEN);
            }
            return;
        }
        if (sub.equals("remove") || sub.equals("del") || sub.equals("удалить")) {
            if (args.length < 2) {
                this.usage();
                return;
            }
            String name = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            if (store.remove(name)) {
                this.logDirect("Точка «" + name + "» удалена.", Formatting.GREEN);
            }
            else {
                this.logDirect("Точки «" + name + "» нет.", Formatting.RED);
            }
            return;
        }
        if (sub.equals("go") || sub.equals("дойти") || sub.equals("tp")) {
            if (args.length < 2) {
                this.usage();
                return;
            }
            String name = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            Waypoint waypoint = store.find(name);
            if (waypoint == null) {
                this.logDirect("Точки «" + name + "» нет. Список: waypoint list", Formatting.RED);
                return;
            }
            WaypointsModule module = ModuleManager.get().get(WaypointsModule.class);
            if (module != null && module.isEnabled()) {
                module.travelTo(waypoint);
            }
            else {
                store.setActive(waypoint.name());
                this.logDirect("Цель: «" + waypoint.name() + "» • " + Math.round(waypoint.x()) + " / "
                        + Math.round(waypoint.y()) + " / " + Math.round(waypoint.z()), Formatting.GREEN);
            }
            return;
        }
        if (sub.equals("clear") || sub.equals("очистить")) {
            int count = store.size();
            store.clear();
            this.logDirect("Удалено точек: " + count, Formatting.GREEN);
            return;
        }
        if (sub.equals("list") || sub.equals("список")) {
            this.list();
            return;
        }
        this.usage();
    }

    private void list() {
        WaypointStore store = WaypointStore.get();
        if (store.size() == 0) {
            this.logDirect("Путевых точек пока нет. Добавьте: waypoint add Дом", Formatting.GRAY);
            return;
        }
        Vec3d here = WaypointStore.playerPos();
        this.logDirect("Путевые точки (" + store.size() + "):", Formatting.GRAY);
        for (Waypoint point : store.nearest(0)) {
            StringBuilder line = new StringBuilder(" • ").append(point.name())
                    .append(" — ").append(Math.round(point.x())).append(" / ").append(Math.round(point.y())).append(" / ").append(Math.round(point.z()))
                    .append(" • ").append(point.shortDimension());
            if (here != null) {
                line.append(" • ").append(Math.round(point.distanceTo(here))).append(" м");
            }
            if (point.name().equalsIgnoreCase(store.activeName())) {
                line.append(" • цель");
            }
            this.logDirect(line.toString(), Formatting.GRAY);
        }
    }
}
