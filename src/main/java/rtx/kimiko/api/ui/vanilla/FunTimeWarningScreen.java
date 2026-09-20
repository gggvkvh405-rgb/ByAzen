package rtx.kimiko.api.ui.vanilla;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.CookieStorage;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;

public class FunTimeWarningScreen extends Screen {
    private final Screen parent;
    private final ServerAddress serverAddress;
    private final ServerInfo serverData;
    private final boolean quickPlay;
    private final CookieStorage transferState;

    public FunTimeWarningScreen(Screen parent, ServerAddress serverAddress, ServerInfo serverData, boolean quickPlay, CookieStorage transferState) {
        super(Text.literal("FunTime Warning"));
        this.parent = parent;
        this.serverAddress = serverAddress;
        this.serverData = serverData;
        this.quickPlay = quickPlay;
        this.transferState = transferState;
    }
}
