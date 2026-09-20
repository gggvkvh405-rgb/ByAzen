package rtx.byazen.utils.discord.rpc.callbacks;
import com.sun.jna.Callback;
import rtx.byazen.utils.discord.rpc.utils.DiscordUser;

public interface ReadyCallback
extends Callback {
    public void apply(DiscordUser var1);
}

