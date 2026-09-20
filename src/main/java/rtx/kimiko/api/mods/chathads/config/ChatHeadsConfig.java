package rtx.kimiko.api.mods.chathads.config;
import java.util.Map;
import rtx.kimiko.api.mods.chathads.config.RenderPosition;
import rtx.kimiko.api.mods.chathads.config.SenderDetection;

public interface ChatHeadsConfig {
    public boolean drawShadow();

    public boolean smartHeuristics();

    public boolean detectNameAliases();

    public boolean offsetNonPlayerText();

    public SenderDetection senderDetection();

    public RenderPosition renderPosition();

    public void addNameAlias(String var1, String var2);

    public Map<String, String> getNameAliases();

    public float threeDeeNess();

    public void setThreeDeeNess(float var1);

    public boolean handleSystemMessages();
}

