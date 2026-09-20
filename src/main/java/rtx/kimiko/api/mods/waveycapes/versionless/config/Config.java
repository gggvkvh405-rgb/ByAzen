package rtx.kimiko.api.mods.waveycapes.versionless.config;
import rtx.kimiko.api.mods.waveycapes.versionless.CapeMovement;
import rtx.kimiko.api.mods.waveycapes.versionless.CapeStyle;
import rtx.kimiko.api.mods.waveycapes.versionless.WindMode;

public class Config {
    public int configVersion = 2;
    public WindMode windMode = WindMode.NONE;
    public CapeStyle capeStyle = CapeStyle.SMOOTH;
    public CapeMovement capeMovement = CapeMovement.BASIC_SIMULATION_3D;
    public int gravity = 25;
    public int heightMultiplier = 6;
    public int straveMultiplier = 2;
}

