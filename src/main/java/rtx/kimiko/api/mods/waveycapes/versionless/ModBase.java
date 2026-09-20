package rtx.kimiko.api.mods.waveycapes.versionless;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rtx.kimiko.api.mods.waveycapes.versionless.config.Config;
import rtx.kimiko.api.mods.waveycapes.versionless.nms.MinecraftPlayer;
import rtx.kimiko.api.mods.waveycapes.versionless.util.Vector3;

public abstract class ModBase {
    public static final Logger LOGGER = LogManager.getLogger((String)"WaveyCapes");
    public static Config config;
    public static boolean simulationBroken;
    private final File settingsFile = new File("config", "waveycapes.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static ModBase INSTANCE;

    static {
        simulationBroken = false;
    }

    public void init() {
        INSTANCE = this;
        if (this.settingsFile.exists()) {
            try {
                config = (Config)(Object)this.gson.fromJson(new String(Files.readAllBytes(this.settingsFile.toPath()), StandardCharsets.UTF_8), Config.class);
            }
            catch (Exception exception) {
                System.out.println("Error while loading config! Creating a new one!");
                exception.printStackTrace();
            }
        }
        if (config == null) {
            config = new Config();
            this.writeConfig();
        } else if (ModBase.config.configVersion == 1) {
            ModBase.config.configVersion = 2;
            if (ModBase.config.gravity < 0) {
                ModBase.config.gravity *= -1;
            }
            this.writeConfig();
        }
    }

    public void writeConfig() {
        if (this.settingsFile.exists()) {
            this.settingsFile.delete();
        }
        try {
            Files.write(this.settingsFile.toPath(), this.gson.toJson((Object)config).getBytes(StandardCharsets.UTF_8), new OpenOption[0]);
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }

    protected static boolean doesClassExist(String string) {
        try {
            if (Class.forName(string) != null) {
                return true;
            }
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        return false;
    }

    public abstract void initSupportHooks();

    public static ModBase getINSTANCE() {
        return INSTANCE;
    }

    public abstract Vector3 applyModAnimations(MinecraftPlayer var1, Vector3 var2);
}

