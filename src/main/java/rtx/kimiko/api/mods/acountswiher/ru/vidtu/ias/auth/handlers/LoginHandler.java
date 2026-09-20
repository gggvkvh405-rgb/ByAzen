package rtx.kimiko.api.mods.acountswiher.ru.vidtu.ias.auth.handlers;
import java.util.concurrent.CompletableFuture;
import rtx.kimiko.api.mods.acountswiher.ru.vidtu.ias.auth.LoginData;

public interface LoginHandler {
    public void error(Throwable var1);

    public boolean cancelled();

    public void stage(String var1, Object ... var2);

    public void success(LoginData var1, boolean var2);

    public CompletableFuture<String> password();
}

