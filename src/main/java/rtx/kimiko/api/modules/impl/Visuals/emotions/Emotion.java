package rtx.kimiko.api.modules.impl.Visuals.emotions;

public enum Emotion {
    WAVE("\u041f\u043e\u043c\u0430\u0445\u0430\u0442\u044c", 3.6f) {
        @Override
        public void apply(EmotionPose pose, float progress) {
            float angle = (float) Math.sin(progress * 12.0f) * 0.6f;
            pose.rightArmX = -0.5f;
            pose.rightArmZ = angle;
        }
    },
    JERK("\u0424\u0430\u043f\u0430\u0442\u044c", 3.0f) {
        @Override
        public void apply(EmotionPose pose, float progress) {
            float offset = (float) Math.sin(progress * 16.0f) * 0.3f;
            pose.rightArmX = -0.8f + offset;
            pose.bodyX = 0.1f * offset;
        }
    },
    SHY("\u0421\u0442\u0435\u0441\u043d\u0435\u043d\u0438\u0435", 3.4f) {
        @Override
        public void apply(EmotionPose pose, float progress) {
            pose.headX = 0.3f;
            pose.leftArmX = -0.5f;
            pose.rightArmX = -0.5f;
        }
    },
    DANCE("\u0422\u0430\u043d\u0435\u0446", 4.2f) {
        @Override
        public void apply(EmotionPose pose, float progress) {
            float phase = (float) Math.sin(progress * 8.0f);
            pose.bodyY = phase * 0.4f;
            pose.leftArmX = phase * 0.6f;
            pose.rightArmX = -phase * 0.6f;
        }
    },
    CLAP("\u0410\u043f\u043b\u043e\u0434\u0438\u0441\u043c\u0435\u043d\u0442\u044b", 3.0f) {
        @Override
        public void apply(EmotionPose pose, float progress) {
            float clap = (float) Math.abs(Math.sin(progress * 14.0f)) * 0.4f;
            pose.leftArmX = -0.8f;
            pose.rightArmX = -0.8f;
            pose.leftArmY = clap;
            pose.rightArmY = -clap;
        }
    },
    BOW("\u041f\u043e\u043a\u043b\u043e\u043d", 4.2f) {
        @Override
        public void apply(EmotionPose pose, float progress) {
            float bow = (float) Math.sin(progress * Math.PI) * 0.8f;
            pose.bodyX = bow;
            pose.headX = bow * 0.5f;
        }
    },
    FACEPALM("\u0424\u0435\u0439\u0441\u043f\u0430\u043b\u043c", 3.4f) {
        @Override
        public void apply(EmotionPose pose, float progress) {
            pose.rightArmX = -1.6f;
            pose.rightArmY = -0.3f;
            pose.headX = 0.4f;
        }
    },
    POINT("\u0423\u043a\u0430\u0437\u0430\u0442\u044c", 2.6f) {
        @Override
        public void apply(EmotionPose pose, float progress) {
            pose.rightArmX = -1.4f;
        }
    },
    TWERK("\u0422\u0432\u0451\u0440\u043a", 3.6f) {
        @Override
        public void apply(EmotionPose pose, float progress) {
            float twerk = (float) Math.abs(Math.sin(progress * 12.0f)) * 0.5f;
            pose.bodyX = 0.4f;
            pose.bodyY = twerk;
        }
    };

    private final String displayName;
    private final float duration;

    Emotion(String name, float dur) {
        this.displayName = name;
        this.duration = dur;
    }

    public String displayName() {
        return this.displayName;
    }

    public float duration() {
        return this.duration;
    }

    public static String[] displayNames() {
        Emotion[] values = values();
        String[] names = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            names[i] = values[i].displayName;
        }
        return names;
    }

    public abstract void apply(EmotionPose pose, float progress);
}
