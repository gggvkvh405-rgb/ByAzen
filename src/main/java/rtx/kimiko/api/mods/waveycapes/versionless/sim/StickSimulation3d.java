package rtx.kimiko.api.mods.waveycapes.versionless.sim;
import java.util.ArrayList;
import java.util.List;
import rtx.kimiko.api.mods.waveycapes.versionless.sim.BasicSimulation;
import rtx.kimiko.api.mods.waveycapes.versionless.util.CapePoint;
import rtx.kimiko.api.mods.waveycapes.versionless.util.Mth;
import rtx.kimiko.api.mods.waveycapes.versionless.util.Vector3;

public class StickSimulation3d
implements BasicSimulation {
    public List<StickSimulation3d.Point> points = new ArrayList<StickSimulation3d.Point>();
    public List<StickSimulation3d.Stick> sticks = new ArrayList<StickSimulation3d.Stick>();
    public Vector3 gravityDirection = new Vector3(0.0f, -1.0f, 0.0f);
    public float gravity = 0.0f;
    public int numIterations = 3;
    private float maxBend = 20.0f;
    public boolean sneaking = false;

    @Override
    public boolean empty() {
        return this.sticks.isEmpty();
    }

    @Override
    public boolean init(int n) {
        if (this.points.size() != n) {
            this.points.clear();
            this.sticks.clear();
            for (int i = 0; i < n; ++i) {
                StickSimulation3d.Point point = new StickSimulation3d.Point();
                point.position.y = -i;
                point.position.x = -i;
                point.locked = i == 0;
                this.points.add(point);
                if (i <= 0) continue;
                this.sticks.add(new StickSimulation3d.Stick(this.points.get(i - 1), point, 1.0f));
            }
            return true;
        }
        return false;
    }

    private double getAngle(Vector3 vector3, Vector3 vector32, Vector3 vector33) {
        float f = vector32.x - vector3.x;
        float f2 = vector32.y - vector3.y;
        float f3 = vector32.x - vector33.x;
        float f4 = vector32.y - vector33.y;
        float f5 = f * f3 + f2 * f4;
        float f6 = f * f4 - f2 * f3;
        double d = Mth.atan2(f6, f5);
        return d * 180.0 / Math.PI;
    }

    private Vector3 getReplacement(Vector3 vector3, Vector3 vector32, double d) {
        Vector3 vector33 = ((Vector3)vector3.clone()).subtract(vector32);
        vector33.rotateDegrees((float)d).add(vector3);
        return vector33;
    }

    @Override
    public float getGravity() {
        return this.gravity;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CapePoint> getPoints() {
        return (List<CapePoint>) (List<?>)(Object)this.points;
    }

    @Override
    public boolean isSneaking() {
        return this.sneaking;
    }

    @Override
    public void setGravity(float f) {
        this.gravity = f;
    }

    @Override
    public void simulate() {
        this.applyGravity();
        this.preventClipping();
        this.preventSelfClipping();
        this.applyMotion();
        this.preventSelfClipping();
        this.preventHardBends();
        this.limitLength();
    }

    @Override
    public void applyMovement(Vector3 vector3) {
        this.points.get((int)0).prevPosition.copy(this.points.get((int)0).position);
        this.points.get((int)0).position.add(vector3);
    }

    @Override
    public void setSneaking(boolean bl) {
        this.sneaking = bl;
    }

    @Override
    public void setGravityDirection(Vector3 vector3) {
        this.gravityDirection = vector3;
    }

    private void applyMotion() {
        for (int i = 0; i < this.numIterations; ++i) {
            for (int j = this.sticks.size() - 1; j >= 0; --j) {
                StickSimulation3d.Stick stick = this.sticks.get(j);
                Vector3 vector3 = ((Vector3)stick.pointA.position.clone()).add(stick.pointB.position).div(2.0f);
                Vector3 vector32 = ((Vector3)stick.pointA.position.clone()).subtract(stick.pointB.position).normalize();
                if (!stick.pointA.locked) {
                    stick.pointA.position = ((Vector3)vector3.clone()).add(((Vector3)vector32.clone()).mul(stick.length / 2.0f));
                }
                if (stick.pointB.locked) continue;
                stick.pointB.position = ((Vector3)vector3.clone()).subtract(((Vector3)vector32.clone()).mul(stick.length / 2.0f));
            }
        }
    }

    private void applyGravity() {
        float f = 0.05f;
        Vector3 vector3 = ((Vector3)(Object)this.gravityDirection.clone()).mul(this.gravity * f);
        Vector3 vector32 = new Vector3(0.0f, 0.0f, 0.0f);
        for (StickSimulation3d.Point point : this.points) {
            if (point.locked) continue;
            vector32.copy(point.position);
            point.position.add(vector3);
            point.prevPosition.copy(vector32);
        }
    }

    private void limitLength() {
        for (int i = 0; i < this.sticks.size(); ++i) {
            StickSimulation3d.Stick stick = this.sticks.get(i);
            Vector3 vector3 = ((Vector3)stick.pointA.position.clone()).subtract(stick.pointB.position).normalize();
            if (stick.pointB.locked) continue;
            stick.pointB.position = ((Vector3)stick.pointA.position.clone()).subtract(vector3.mul(stick.length));
        }
    }

    private void preventClipping() {
        StickSimulation3d.Point point = this.points.get(0);
        for (int i = 1; i < this.points.size(); ++i) {
            float f;
            float f2;
            StickSimulation3d.Point point2 = this.points.get(i);
            if (point2.position.x - point.position.x > 0.0f) {
                point2.position.x = point.position.x;
            }
            if ((f2 = point.position.z - point2.position.z) > (f = (float)i / (float)this.points.size() * ((float)i / (float)this.points.size()) * 5.0f)) {
                point2.position.z = point.position.z - f;
            }
            if (!(f2 < -f)) continue;
            point2.position.z = point.position.z + f;
        }
    }

    private void preventHardBends() {
        for (int i = 1; i < this.points.size() - 2; ++i) {
            Vector3 vector3;
            double d = this.getAngle(this.points.get((int)i).position, this.points.get((int)(i - 1)).position, this.points.get((int)(i + 1)).position);
            if (d < (double)(-this.maxBend)) {
                this.points.get((int)(i + 1)).position = vector3 = this.getReplacement(this.points.get((int)i).position, this.points.get((int)(i - 1)).position, -this.maxBend * 2.0f);
            }
            if (!(d > (double)this.maxBend)) continue;
            this.points.get((int)(i + 1)).position = vector3 = this.getReplacement(this.points.get((int)i).position, this.points.get((int)(i - 1)).position, this.maxBend * 2.0f);
        }
    }

    private void preventSelfClipping() {
        boolean bl = false;
        int n = 0;
        do {
            bl = false;
            for (int i = 0; i < this.points.size(); ++i) {
                for (int j = i + 1; j < this.points.size(); ++j) {
                    StickSimulation3d.Point point = this.points.get(i);
                    StickSimulation3d.Point point2 = this.points.get(j);
                    Vector3 vector3 = ((Vector3)point.position.clone()).subtract(point2.position);
                    if (!((double)vector3.sqrMagnitude() < 0.99)) continue;
                    bl = true;
                    ++n;
                    vector3.normalize();
                    Vector3 vector32 = ((Vector3)point.position.clone()).add(point2.position).div(2.0f);
                    if (!point.locked) {
                        point.position = ((Vector3)vector32.clone()).add(((Vector3)vector3.clone()).mul(0.5f));
                    }
                    if (point2.locked) continue;
                    point2.position = ((Vector3)vector32.clone()).subtract(((Vector3)vector3.clone()).mul(0.5f));
                }
            }
        } while (bl && n < 32);
    }


    public static class Point
    implements CapePoint {
        public Vector3 position = new Vector3(0.0f, 0.0f, 0.0f);
        public Vector3 prevPosition = new Vector3(0.0f, 0.0f, 0.0f);
        public boolean locked;
    
        @Override
        public float getLerpX(float f) {
            return Mth.lerp(f, this.prevPosition.x, this.position.x);
        }
    
        @Override
        public float getLerpY(float f) {
            return Mth.lerp(f, this.prevPosition.y, this.position.y);
        }
    
        @Override
        public float getLerpZ(float f) {
            return Mth.lerp(f, this.prevPosition.z, this.position.z);
        }
    }
    
    public static class Stick {
        public StickSimulation3d.Point pointA;
        public StickSimulation3d.Point pointB;
        public float length;
    
        public Stick(StickSimulation3d.Point point, StickSimulation3d.Point point2, float f) {
            this.pointA = point;
            this.pointB = point2;
            this.length = f;
        }
    }
}

