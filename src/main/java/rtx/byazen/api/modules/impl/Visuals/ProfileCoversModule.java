package rtx.byazen.api.modules.impl.Visuals;

import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SelectSetting;
import rtx.byazen.api.ui.ProfileCoverScreen;
import rtx.byazen.utils.profile.ProfileCovers;

/**
 * Анимированные обложки профиля (идея №131 из IDEAS.md).
 * <p>
 * Обложка ByAzen — это настоящий GIF: мягкий градиент с плавающими бликами, который живёт в
 * карточке игрока. Здесь выбирается обложка, режим «сезонная обложка» (зимой — «Зима», в другие
 * месяцы — набор по сезону) и открывается экран с карточками.
 */
public final class ProfileCoversModule
extends Module {

    private static ProfileCoversModule instance;

    private final SelectSetting cover = this.register(new SelectSetting("Обложка", "Живая обложка профиля ByAzen.")
            .value(ProfileCoversModule.names()).selected("Неон"));
    private final BooleanSetting seasonal = this.register(new BooleanSetting("Сезонная обложка", "Зимой автоматически ставить «Зиму», в остальные месяцы — набор по сезону."));
    private final BooleanSetting animated = this.register(new BooleanSetting("Анимация", "Проигрывать обложку как анимацию, а не одним кадром.").setValue(true));
    private final ButtonSetting screen = this.register(new ButtonSetting("Обложки профиля", "Открыть экран с живыми обложками и карточкой игрока.").label("Открыть").onClick(ProfileCoversModule::open));

    public ProfileCoversModule() {
        super("Profile Covers", "Анимированные GIF-обложки профиля: выбор, сезонный режим, карточка игрока.", Category.VISUALS);
        instance = this;
        this.cover.setChangeListener(this::apply);
    }

    private static String[] names() {
        java.util.List<ProfileCovers.Cover> all = ProfileCovers.all();
        String[] names = new String[all.size()];
        for (int i = 0; i < all.size(); i++) {
            names[i] = all.get(i).name;
        }
        return names;
    }

    public static ProfileCoversModule getInstance() {
        return instance;
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    @Override
    protected void onEnable() {
        this.apply();
        ProfileCovers.ensureLoaded();
    }

    private void apply() {
        ProfileCovers.Cover chosen = null;
        String selected = this.cover.getSelected();
        for (ProfileCovers.Cover cover : ProfileCovers.all()) {
            if (cover.name.equals(selected)) {
                chosen = cover;
                break;
            }
        }
        if (chosen != null) {
            ProfileCovers.setOwn(chosen.id);
        }
    }

    /** Обложка, которая должна показываться прямо сейчас. */
    public ProfileCovers.Cover active() {
        if (this.seasonal.getValue()) {
            return ProfileCovers.seasonal();
        }
        return ProfileCovers.own();
    }

    public static boolean animated() {
        ProfileCoversModule module = ProfileCoversModule.instance;
        return module == null || module.animated.getValue();
    }

    public static void open() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }
        ProfileCoversModule module = ProfileCoversModule.instance;
        if (module != null && !module.isEnabled()) {
            module.enable();
        }
        client.setScreen(new ProfileCoverScreen());
    }
}
