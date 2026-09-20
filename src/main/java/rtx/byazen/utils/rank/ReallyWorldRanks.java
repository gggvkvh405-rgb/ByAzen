package rtx.byazen.utils.rank;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import rtx.byazen.utils.network.Network;

public final class ReallyWorldRanks {
    private static final Map<Character, String> KEYS = new LinkedHashMap<Character, String>();
    private static final Map<String, String> LABELS = new LinkedHashMap<String, String>();
    private static final Map<String, Character> LABEL_TO_GLYPH = new LinkedHashMap<String, Character>();
    private static final Set<Character> GLYPHS = new HashSet<Character>();
    private static final List<String> LABEL_ORDER = new ArrayList<String>();

    private ReallyWorldRanks() {
    }

    static {
        ReallyWorldRanks.rank('\ua500', "player");
        ReallyWorldRanks.rank('\ua504', "hero");
        ReallyWorldRanks.rank('\ua508', "titan");
        ReallyWorldRanks.rank('\ua512', "avenger");
        ReallyWorldRanks.rank('\ua516', "overlord");
        ReallyWorldRanks.rank('\ua520', "magister");
        ReallyWorldRanks.rank('\ua524', "imperator");
        ReallyWorldRanks.rank('\ua528', "dragon");
        ReallyWorldRanks.rank('\ua532', "bull");
        ReallyWorldRanks.rank('\ua552', "rabbit");
        ReallyWorldRanks.rank('\ua536', "tiger");
        ReallyWorldRanks.rank('\ua544', "dracula");
        ReallyWorldRanks.rank('\ua556', "bunny");
        ReallyWorldRanks.rank('\ua540', "hydra");
        ReallyWorldRanks.rank('\ua541', "god");
        ReallyWorldRanks.rank('\ua548', "cobra");
        ReallyWorldRanks.rank('\ua545', "vampire");
        ReallyWorldRanks.rank('\ua549', "pegas");
        ReallyWorldRanks.rank('\ua501', "media");
        ReallyWorldRanks.rank('\ua505', "yt");
        ReallyWorldRanks.rank('\ua560', "d.helper");
        ReallyWorldRanks.rank('\ua509', "helper");
        ReallyWorldRanks.rank('\ua513', "ml.moder");
        ReallyWorldRanks.rank('\ua517', "moder");
        ReallyWorldRanks.rank('\ua521', "moder+");
        ReallyWorldRanks.rank('\ua525', "st.moder");
        ReallyWorldRanks.rank('\ua529', "gl.moder");
        ReallyWorldRanks.rank('\ua533', "ml.admin");
        ReallyWorldRanks.rank('\ua537', "admin");
        ReallyWorldRanks.label("player", "Player");
        ReallyWorldRanks.label("hero", "Hero");
        ReallyWorldRanks.label("titan", "Titan");
        ReallyWorldRanks.label("avenger", "Avenger");
        ReallyWorldRanks.label("overlord", "Overlord");
        ReallyWorldRanks.label("magister", "Magister");
        ReallyWorldRanks.label("imperator", "Imperator");
        ReallyWorldRanks.label("dragon", "Dragon");
        ReallyWorldRanks.label("bull", "Bull");
        ReallyWorldRanks.label("rabbit", "Rabbit");
        ReallyWorldRanks.label("tiger", "Tiger");
        ReallyWorldRanks.label("dracula", "Dracula");
        ReallyWorldRanks.label("bunny", "Bunny");
        ReallyWorldRanks.label("hydra", "Hydra");
        ReallyWorldRanks.label("god", "GOD");
        ReallyWorldRanks.label("cobra", "Cobra");
        ReallyWorldRanks.label("vampire", "Vampire");
        ReallyWorldRanks.label("pegas", "Pegas");
        ReallyWorldRanks.label("media", "Media");
        ReallyWorldRanks.label("yt", "YT");
        ReallyWorldRanks.label("d.helper", "D.Helper");
        ReallyWorldRanks.label("helper", "Helper");
        ReallyWorldRanks.label("ml.moder", "Ml.Moder");
        ReallyWorldRanks.label("moder", "Moder");
        ReallyWorldRanks.label("moder+", "Moder+");
        ReallyWorldRanks.label("st.moder", "St.Moder");
        ReallyWorldRanks.label("gl.moder", "Gl.Moder");
        ReallyWorldRanks.label("ml.admin", "Ml.Admin");
        ReallyWorldRanks.label("admin", "Admin");
        for (Map.Entry<Character, String> entry : KEYS.entrySet()) {
            char c = entry.getKey().charValue();
            String string = LABELS.get(entry.getValue());
            if (string == null) continue;
            GLYPHS.add(Character.valueOf(c));
            if (LABEL_TO_GLYPH.containsKey(string)) continue;
            LABEL_TO_GLYPH.put(string, Character.valueOf(c));
            LABEL_ORDER.add(string);
        }
    }

    private static MutableText walk(Text text, String string) {
        MutableText mutableText;
        TextContent textContent = text.getContent();
        if (textContent instanceof PlainTextContent.Literal) {
            PlainTextContent.Literal literal = (PlainTextContent.Literal)textContent;
            mutableText = ReallyWorldRanks.rewriteLiteral(literal.string(), text.getStyle(), string);
        } else {
            mutableText = text.copyContentOnly();
        }
        for (Text text2 : text.getSiblings()) {
            mutableText.append((Text)ReallyWorldRanks.walk(text2, string));
        }
        return mutableText;
    }

    private static void rank(char c, String string) {
        KEYS.put(Character.valueOf(c), string);
    }

    private static void label(String string, String string2) {
        LABELS.put(string, string2);
    }

    public static Text applySelfRank(Text text, String string) {
        if (text == null || string == null || !Network.isReallyWorld()) {
            return text;
        }
        Character c = LABEL_TO_GLYPH.get(string);
        if (c == null || !ReallyWorldRanks.containsGlyph(text.getString())) {
            return text;
        }
        return ReallyWorldRanks.walk(text, String.valueOf(c.charValue()));
    }

    public static boolean glyphPrecedesNick(String string, String string2) {
        if (string == null || string2 == null || string2.isEmpty() || GLYPHS.isEmpty()) {
            return false;
        }
        for (int i = 0; i < string.length(); ++i) {
            int n;
            int n2;
            if (!GLYPHS.contains(Character.valueOf(string.charAt(i)))) continue;
            for (n2 = i + 1; n2 < string.length() && Character.isWhitespace(string.charAt(n2)); ++n2) {
            }
            if (!string.regionMatches(true, n2, string2, 0, string2.length()) || (n = n2 + string2.length()) < string.length() && ReallyWorldRanks.isNameChar(string.charAt(n))) continue;
            return true;
        }
        return false;
    }

    private static MutableText rewriteLiteral(String string, Style style, String string2) {
        StringBuilder stringBuilder = new StringBuilder(string.length());
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            if (GLYPHS.contains(Character.valueOf(c))) {
                stringBuilder.append(string2);
                continue;
            }
            stringBuilder.append(c);
        }
        return Text.literal((String)stringBuilder.toString()).setStyle(style);
    }

    private static boolean containsGlyph(String string) {
        for (int i = 0; i < string.length(); ++i) {
            if (!GLYPHS.contains(Character.valueOf(string.charAt(i)))) continue;
            return true;
        }
        return false;
    }

    public static Text stripGlyphs(Text text) {
        if (text == null || GLYPHS.isEmpty() || !ReallyWorldRanks.containsGlyph(text.getString())) {
            return text;
        }
        return ReallyWorldRanks.trimLeading((Text)ReallyWorldRanks.walk(text, ""));
    }

    private static MutableText trimLeading(Text text) {
        return ReallyWorldRanks.trimWalk(text, new boolean[]{true});
    }

    public static List<String> orderedLabels() {
        return LABEL_ORDER;
    }

    private static boolean isNameChar(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    private static MutableText trimWalk(Text text, boolean[] blArray) {
        MutableText mutableText;
        TextContent textContent = text.getContent();
        if (textContent instanceof PlainTextContent.Literal) {
            PlainTextContent.Literal literal = (PlainTextContent.Literal)textContent;
            String string = literal.string();
            if (blArray[0] && !string.isEmpty()) {
                int n;
                for (n = 0; n < string.length() && Character.isWhitespace(string.charAt(n)); ++n) {
                }
                if (!(string = string.substring(n)).isEmpty()) {
                    blArray[0] = false;
                }
            }
            mutableText = Text.literal((String)string).setStyle(text.getStyle());
        } else {
            mutableText = text.copyContentOnly();
            blArray[0] = false;
        }
        for (Text sibling : text.getSiblings()) {
            mutableText.append(ReallyWorldRanks.trimWalk(sibling, blArray));
        }
        return mutableText;
    }
}

