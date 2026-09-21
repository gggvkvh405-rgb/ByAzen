/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1799
 *  org.joml.Matrix4f
 */
package ru.byazen.misc;

import java.util.List;
import net.minecraft.class_1799;
import org.joml.Matrix4f;
import ru.byazen.ByAzenClient;
import ru.byazen.item.ItemBadge;
import ru.byazen.misc.BoxEspSettings;
import ru.byazen.misc.EntityEquipmentOverlayRenderer;
import ru.byazen.misc.EspBoxDecorationRenderer;
import ru.byazen.misc.NameTagSettings;
import ru.byazen.misc.WorldBoxSettings;
import ru.byazen.model.esp.EspRelation;
import ru.byazen.model.esp.EspTargetType;
import ru.byazen.ui.GuiBounds;
import ru.byazen.util.EspBoxRenderer;
import ru.byazen.util.EspFeatureRegistry;
import ru.byazen.util.GuiDrawApi;

public final class EntityEspOverlayRenderer {
    private final EspBoxDecorationRenderer espBoxDecorationRenderer;
    private final EspBoxRenderer espBoxRenderer = new EspBoxRenderer();
    private final EntityEquipmentOverlayRenderer entityEquipmentOverlayRenderer;

    public EntityEspOverlayRenderer() {
        this.espBoxDecorationRenderer = new EspBoxDecorationRenderer();
        this.entityEquipmentOverlayRenderer = new EntityEquipmentOverlayRenderer();
    }

    public void process(EspTargetType espTargetType, EspRelation espRelation, List<class_1799> list, float f) {
        EspFeatureRegistry espFeatures = ByAzenClient.getEspFeatureRegistry();
        NameTagSettings talisman = espFeatures == null || espTargetType == null ? null : espFeatures.getNameTagSettings(espTargetType, espRelation);
        boolean bl = talisman != null && talisman.isEnabled() && talisman.areItemsVisible();
        this.entityEquipmentOverlayRenderer.member11505(bl ? list : List.of(), f);
    }

    public void process2(GuiDrawApi drawApi, Matrix4f matrix4f, GuiBounds bounds2, EspTargetType espTargetType, EspRelation espRelation, float f, String string, int n, int n2, ItemBadge itemBadge, String string2) {
        NameTagSettings talisman;
        BoxEspSettings rectangle;
        EspFeatureRegistry espFeatures = ByAzenClient.getEspFeatureRegistry();
        if (espFeatures == null || espTargetType == null) {
            return;
        }
        WorldBoxSettings dotted = espFeatures.getWorldBoxSettings(espTargetType, espRelation);
        if (dotted != null && dotted.isEnabled()) {
            this.espBoxRenderer.member1871(drawApi, matrix4f, bounds2, dotted, f);
        }
        if ((rectangle = espFeatures.getBox2dSettings(espTargetType, espRelation)) != null && rectangle.isEnabled()) {
            this.espBoxDecorationRenderer.member9165(drawApi, matrix4f, bounds2, rectangle);
        }
        if ((talisman = espFeatures.getNameTagSettings(espTargetType, espRelation)) != null && talisman.isEnabled()) {
            this.entityEquipmentOverlayRenderer.member2669(drawApi, matrix4f, bounds2, talisman, string, n, n2, itemBadge, string2);
        }
    }

    public void update() {
        this.entityEquipmentOverlayRenderer.member5307();
    }
}

