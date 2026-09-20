package rtx.kimiko.utils.discord.rpc.callbacks;
import com.sun.jna.Callback;
import rtx.kimiko.utils.discord.rpc.utils.DiscordUser;

public interface JoinRequestCallback
extends Callback {
    public void apply(DiscordUser var1);
}

