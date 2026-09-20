package rtx.byazen.api.modules.restrict;
import rtx.byazen.api.modules.restrict.Server;

public @interface ServerRule {
    public ServerRule.Mode mode();

    public Server[] servers();


    public static enum Mode {
        ONLY,
        BLOCK,
        HIDE;
    
    }
}

