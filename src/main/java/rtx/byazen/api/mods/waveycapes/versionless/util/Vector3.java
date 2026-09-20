package rtx.byazen.api.mods.waveycapes.versionless.util;
import rtx.byazen.api.mods.waveycapes.versionless.util.Mth;

public class Vector3 {
    public float x;
    public float y;
    public float z;

    public Vector3(float f, float f2, float f3) {
        this.x = f;
        this.y = f2;
        this.z = f3;
    }

    public Vector3() {
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof Vector3)) {
            return false;
        }
        Vector3 vector3 = (Vector3)object;
        if (!vector3.canEqual(this)) {
            return false;
        }
        if (Float.compare(this.x, vector3.x) != 0) {
            return false;
        }
        if (Float.compare(this.y, vector3.y) != 0) {
            return false;
        }
        return Float.compare(this.z, vector3.z) == 0;
    }

    public String toString() {
        return "Vector3(x=" + this.x + ", y=" + this.y + ", z=" + this.z + ")";
    }

    public int hashCode() {
        int n = 59;
        int n2 = 1;
        n2 = n2 * 59 + Float.floatToIntBits(this.x);
        n2 = n2 * 59 + Float.floatToIntBits(this.y);
        n2 = n2 * 59 + Float.floatToIntBits(this.z);
        return n2;
    }

    public Vector3 clone() {
        return new Vector3(this.x, this.y, this.z);
    }

    public void copy(Vector3 vector3) {
        this.x = vector3.x;
        this.y = vector3.y;
        this.z = vector3.z;
    }

    public Vector3 add(Vector3 vector3) {
        this.x += vector3.x;
        this.y += vector3.y;
        this.z += vector3.z;
        return this;
    }

    public Vector3 add(float f, float f2, float f3) {
        this.x += f;
        this.y += f2;
        this.z += f3;
        return this;
    }

    public Vector3 normalize() {
        float f = Mth.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        if (f < 1.0E-4f) {
            this.x = 0.0f;
            this.y = 0.0f;
            this.z = 0.0f;
        } else {
            this.x /= f;
            this.y /= f;
            this.z /= f;
        }
        return this;
    }

    public Vector3 div(float f) {
        this.x /= f;
        this.y /= f;
        this.z /= f;
        return this;
    }

    public Vector3 subtract(Vector3 vector3) {
        this.x -= vector3.x;
        this.y -= vector3.y;
        this.z -= vector3.z;
        return this;
    }

    public float sqrMagnitude() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public Vector3 cross(Vector3 vector3) {
        float f = this.x;
        float f2 = this.y;
        float f3 = this.z;
        float f4 = vector3.x;
        float f5 = vector3.y;
        float f6 = vector3.z;
        this.x = f2 * f6 - f3 * f5;
        this.y = f3 * f4 - f * f6;
        this.z = f * f5 - f2 * f4;
        return this;
    }

    public Vector3 mul(float f) {
        this.x *= f;
        this.y *= f;
        this.z *= f;
        return this;
    }

    public Vector3 rotateDegrees(float f) {
        float f2 = this.x;
        float f3 = this.y;
        f = (float)Math.toRadians(f);
        this.x = Mth.cos(f) * f2 - Mth.sin(f) * f3;
        this.y = Mth.sin(f) * f2 + Mth.cos(f) * f3;
        return this;
    }

    protected boolean canEqual(Object object) {
        return object instanceof Vector3;
    }
}

