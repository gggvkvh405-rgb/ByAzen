package rtx.kimiko.api.mods.waveycapes.compat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.util.Identifier;

public final class PlayerWrapper {
    private final PlayerLikeEntity avatar;
    private final PlayerEntityRenderState renderState;
    private final Identifier capeTexture;
    private final boolean capeVisible;
    private final boolean localPlayer;

    public PlayerWrapper(PlayerLikeEntity avatar, PlayerEntityRenderState renderState, Identifier capeTexture, boolean capeVisible, boolean localPlayer) {
        this.avatar = avatar;
        this.renderState = renderState;
        this.capeTexture = capeTexture;
        this.capeVisible = capeVisible;
        this.localPlayer = localPlayer;
    }

    public PlayerWrapper(PlayerEntityRenderState state) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Entity e = mc.world != null ? mc.world.getEntityById(state.id) : null;
        this.avatar = e instanceof PlayerLikeEntity pl ? pl : null;
        this.renderState = state;
        this.capeTexture = state.skinTextures != null && state.skinTextures.cape() != null ? state.skinTextures.cape().id() : null;
        this.capeVisible = state.skinTextures != null && state.skinTextures.cape() != null && state.capeVisible && !state.invisible;
        this.localPlayer = mc.player != null && mc.player.getId() == state.id;
    }

    public PlayerLikeEntity getAvatar() {
        return this.avatar;
    }

    public PlayerEntityRenderState getRenderState() {
        return this.renderState;
    }

    public Identifier getCapeTexture() {
        return this.capeTexture;
    }

    public boolean isCapeVisible() {
        return this.capeVisible;
    }

    public boolean isLocalPlayer() {
        return this.localPlayer;
    }

    public boolean isPlayerInvisible() {
        return this.renderState != null && this.renderState.invisible;
    }

    public boolean hasElytraEquipped() {
        return this.renderState != null && this.renderState.glidingTicks > 0;
    }

    public boolean hasChestplateEquipped() {
        return false;
    }
}