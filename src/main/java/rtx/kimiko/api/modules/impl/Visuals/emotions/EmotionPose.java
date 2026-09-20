package rtx.kimiko.api.modules.impl.Visuals.emotions;

public class EmotionPose {
    public float headX, headY, headZ;
    public float bodyX, bodyY, bodyZ;
    public float leftArmX, leftArmY, leftArmZ;
    public float rightArmX, rightArmY, rightArmZ;
    public float leftLegX, leftLegY, leftLegZ;
    public float rightLegX, rightLegY, rightLegZ;

    public void reset() {
        headX = headY = headZ = 0.0f;
        bodyX = bodyY = bodyZ = 0.0f;
        leftArmX = leftArmY = leftArmZ = 0.0f;
        rightArmX = rightArmY = rightArmZ = 0.0f;
        leftLegX = leftLegY = leftLegZ = 0.0f;
        rightLegX = rightLegY = rightLegZ = 0.0f;
    }
}
