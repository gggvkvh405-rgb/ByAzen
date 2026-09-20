#version 150

layout(std140) uniform ShockwaveParams {
    vec4 ShockCenterProgress;
    vec4 ShockScreenSize;
};

uniform sampler2D Sampler0;

out vec4 fragColor;

#define PI 3.14159265359

#define force 0.08
#define aberrationOffset 0.006
#define flashIntensity 2.5

float easeInOutSine(float t) {
    return 0.5 - 0.5 * cos(t * PI);
}

void main() {
    vec2 screenUV = gl_FragCoord.xy / ShockScreenSize.xy;
    vec2 center = ShockCenterProgress.xy;
    float progress = ShockCenterProgress.z;
    float strength = ShockCenterProgress.w;

    if (progress >= 0.999 || strength <= 0.001) {
        discard;
    }

    float thickness = max(ShockScreenSize.z, 0.001);
    float feathering = max(ShockScreenSize.w, 0.001);

    float aspectRatio = ShockScreenSize.x / ShockScreenSize.y;
    vec2 aspectVec = vec2(aspectRatio, 1.0);

    vec2 delta = (screenUV - center) * aspectVec;
    float dist = length(delta);

    vec2 maxCorner = max(center, vec2(1.0) - center) * aspectVec;
    float maxRadius = length(maxCorner) + thickness + feathering + 0.25;

    float pos = easeInOutSine(progress) * maxRadius;

    float innerBound = smoothstep(
        pos - thickness - feathering,
        pos - thickness,
        dist
    );
    float outerBound = smoothstep(
        pos - feathering,
        pos,
        dist
    );
    float shapeMask = innerBound - outerBound;

    if (shapeMask <= 0.002) {
        discard;
    }

    vec2 r = (dist > 1e-5) ? (delta / dist) : vec2(0.0);
    vec2 displacement = (r / aspectVec) * force;

    vec2 uvR = screenUV - (displacement - vec2(aberrationOffset)) * shapeMask;
    vec2 uvG = screenUV - displacement * shapeMask;
    vec2 uvB = screenUV - (displacement + vec2(aberrationOffset)) * shapeMask;

    float rChannel = texture(Sampler0, uvR).r;
    float gChannel = texture(Sampler0, uvG).g;
    float bChannel = texture(Sampler0, uvB).b;

    vec3 color = vec3(rChannel, gChannel, bChannel);

    float invPost = max(0.0, 1.0 - (pos / maxRadius));
    float flashOpacity = invPost * invPost * invPost * invPost;
    float flashMask = 1.0 - innerBound;
    color += flashIntensity * color * flashMask * flashOpacity;

    fragColor = vec4(color, shapeMask * strength);
}
