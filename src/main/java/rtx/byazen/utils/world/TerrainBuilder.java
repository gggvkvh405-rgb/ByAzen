package rtx.byazen.utils.world;

import java.util.Arrays;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

/**
 * Построение картинки мини-карты (идея №71 из IDEAS.md).
 * <p>
 * Работа разбита на порции: за один тик считается несколько строк, поэтому кадр не проседает
 * даже при большом радиусе. Считаются высоты рельефа, по ним — мягкое освещение склонов.
 */
public final class TerrainBuilder {

    private static final int ROWS_PER_STEP = 20;
    private static final int MAX_SIZE = 320;

    private int size;
    private int radius;
    private float yaw;
    private int row;
    private int[] pixels = new int[0];
    private float[] heights = new float[0];
    private boolean running;
    private boolean changed;

    public boolean running() {
        return this.running;
    }

    public boolean changed() {
        return this.changed;
    }

    public int[] pixels() {
        return this.pixels;
    }

    public int size() {
        return this.size;
    }

    /** Начинает новый проход: размер в пикселях, радиус в блоках и угол поворота карты. */
    public void begin(int size, int radius, float yaw) {
        int clamped = Math.max(16, Math.min(MAX_SIZE, size));
        if (this.pixels.length != clamped * clamped) {
            this.pixels = new int[clamped * clamped];
            this.heights = new float[clamped * clamped];
        }
        else {
            Arrays.fill(this.pixels, 0);
        }
        this.size = clamped;
        this.radius = Math.max(8, radius);
        this.yaw = yaw;
        this.row = 0;
        this.running = true;
        this.changed = true;
    }

    /** Считает очередную порцию строк. Возвращает true, когда проход завершён. */
    public boolean step(ClientWorld world, double playerX, double playerY, double playerZ) {
        if (!this.running || world == null) {
            this.running = false;
            return true;
        }
        int n = this.size;
        int r = n / 2;
        double yawRad = Math.toRadians(this.yaw);
        float fx = (float)(-Math.sin(yawRad));
        float fz = (float)Math.cos(yawRad);
        float rx = -fz;
        float rz = fx;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int end = Math.min(n, this.row + ROWS_PER_STEP);
        for (int iz = this.row; iz < end; ++iz) {
            float sz = (float)(iz - r);
            for (int ix = 0; ix < n; ++ix) {
                float sx = (float)(ix - r);
                double offsetX = (double)(sx * rx - sz * fx);
                double offsetZ = (double)(sx * rz - sz * fz);
                int blockX = (int)Math.floor(playerX + offsetX);
                int blockZ = (int)Math.floor(playerZ + offsetZ);
                int top = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockX, blockZ);
                int y = top - 1;
                int index = iz * n + ix;
                if (y < world.getBottomY()) {
                    this.pixels[index] = 0xFF0B0D12;
                    this.heights[index] = (float)world.getBottomY();
                    continue;
                }
                mutable.set(blockX, y, blockZ);
                BlockState state = world.getBlockState(mutable);
                float height = (float)y;
                this.heights[index] = height;
                int base = MinimapColors.base(state);
                float shade = 0.86f + (height - (float)playerY) * 0.018f;
                if (ix > 0 && iz > 0) {
                    float previous = this.heights[(iz - 1) * n + ix - 1];
                    shade += (previous - height) * 0.05f;
                }
                if (state.isOf(net.minecraft.block.Blocks.WATER)) {
                    shade = Math.min(shade, 1.0f);
                }
                if (MinimapColors.isOre(state)) {
                    base = MinimapColors.mix(base, rtx.byazen.api.ui.theme.ClientAccent.accentOpaque(), 0.35f);
                }
                this.pixels[index] = MinimapColors.shade(base, shade);
            }
        }
        this.row = end;
        this.changed = true;
        if (this.row >= n) {
            this.running = false;
            return true;
        }
        return false;
    }

    public void cancel() {
        this.running = false;
    }
}
