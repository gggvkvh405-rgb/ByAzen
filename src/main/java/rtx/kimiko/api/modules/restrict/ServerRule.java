package rtx.kimiko.api.modules.restrict;
import rtx.kimiko.api.modules.restrict.Server;

public @interface ServerRule {
    public ServerRule.Mode mode();

    public Server[] servers();


    public static enum Mode {
        ONLY,
        BLOCK,
        HIDE;
    
    }
}

