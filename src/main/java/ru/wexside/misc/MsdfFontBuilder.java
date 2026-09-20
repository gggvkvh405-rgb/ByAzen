/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 */
package ru.wexside.misc;

import com.google.gson.Gson;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import ru.wexside.misc.ClasspathResource;
import ru.wexside.misc.MsdfAtlas;
import ru.wexside.misc.MsdfFont;
import ru.wexside.misc.MsdfFontData;
import ru.wexside.misc.MsdfGlyph;
import ru.wexside.misc.MsdfGlyphData;
import ru.wexside.misc.MsdfMetrics;
import ru.wexside.misc.ResourceData;
import ru.wexside.misc.ResourceResolver;
import ru.wexside.misc.TextureResource;
import ru.wexside.util.MsdfFontRenderer;

public class MsdfFontBuilder {
    private String jsonPath;
    private String atlasPath;
    private String name = "unnamed";
    private final ResourceResolver router = new ResourceResolver("/assets/wexside", ClasspathResource::new);

    public MsdfFontBuilder process(String string) {
        this.atlasPath = string;
        return this;
    }

    public MsdfFontRenderer getMsdfFontRenderer() {
        if (this.jsonPath == null || this.atlasPath == null) {
            throw new IllegalStateException("MSDFRendererBuilder \u043d\u0435\u043e\u0431\u0445\u043e\u0434\u0438\u043c\u043e \u0443\u043a\u0430\u0437\u0430\u0442\u044c \u0438 json \u0438 atlas");
        }
        ResourceData callback = this.router.resolve(this.jsonPath);
        MsdfFontData msdfFontData = (MsdfFontData)new Gson().fromJson((Reader)new InputStreamReader(callback.openStream(), StandardCharsets.UTF_8), MsdfFontData.class);
        TextureResource texture2 = new TextureResource(this.router.resolve(this.atlasPath)).minFilter(9729).magFilter(9729).mipmaps(false).wrapS(33071).wrapT(33071);
        MsdfAtlas msdfAtlas = new MsdfAtlas(msdfFontData.atlas.distanceRange, msdfFontData.atlas.width, msdfFontData.atlas.height);
        MsdfMetrics msdfMetrics = new MsdfMetrics(msdfFontData.metrics.lineHeight, msdfFontData.metrics.ascender, msdfFontData.metrics.descender);
        Int2ObjectMap<MsdfGlyph> int2ObjectMap = MsdfFontBuilder.process3(msdfFontData, msdfAtlas);
        MsdfFont msdfFont = new MsdfFont(this.name, msdfAtlas, msdfMetrics, texture2, int2ObjectMap);
        return new MsdfFontRenderer(msdfFont);
    }

    public MsdfFontBuilder process2(String string) {
        this.name = string;
        return this;
    }

    public static MsdfFontBuilder getMsdfFontBuilder() {
        return new MsdfFontBuilder();
    }

    private static Int2ObjectMap<MsdfGlyph> process3(MsdfFontData msdfFontData, MsdfAtlas msdfAtlas) {
        Int2ObjectOpenHashMap int2ObjectOpenHashMap = new Int2ObjectOpenHashMap();
        for (MsdfGlyphData msdfGlyphData : msdfFontData.glyphs) {
            if (msdfGlyphData.planeBounds == null || msdfGlyphData.atlasBounds == null) continue;
            MsdfGlyph msdfGlyph = new MsdfGlyph(msdfGlyphData.unicode, msdfGlyphData.planeBounds.left, msdfGlyphData.planeBounds.top, msdfGlyphData.planeBounds.right, msdfGlyphData.planeBounds.bottom, msdfGlyphData.atlasBounds.left / msdfAtlas.getFloatType2(), 1.0f - msdfGlyphData.atlasBounds.top / msdfAtlas.getFloatType(), msdfGlyphData.atlasBounds.right / msdfAtlas.getFloatType2(), 1.0f - msdfGlyphData.atlasBounds.bottom / msdfAtlas.getFloatType(), msdfGlyphData.advance);
            int2ObjectOpenHashMap.put(msdfGlyphData.unicode, (Object)msdfGlyph);
        }
        return int2ObjectOpenHashMap;
    }

    public MsdfFontBuilder process4(String string) {
        this.jsonPath = string;
        return this;
    }
}

