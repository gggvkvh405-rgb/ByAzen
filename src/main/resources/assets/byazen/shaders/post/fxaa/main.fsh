#version 150

uniform sampler2D Source;

layout(std140) uniform FxaaParams {
    vec4 Settings; // x, y — размер текселя, z — режим качества, w — субпиксельная мягкость
    vec4 Limits;   // x — порог края, y — минимальный порог, z — сила сглаживания, w — резкость
    vec4 Extras;   // запас
};

in vec2 texCoord;
out vec4 fragColor;

float lumaOf(vec3 color) {
    return dot(color, vec3(0.299, 0.587, 0.114));
}

void main() {
    vec2 texel = vec2(Settings.x, Settings.y);
    int mode = int(Settings.z + 0.5);
    float subpixel = clamp(Settings.w, 0.0, 1.0);
    float threshold = max(Limits.x, 0.0);
    float thresholdMin = max(Limits.y, 0.0);
    float blend = clamp(Limits.z, 0.0, 1.0);
    float sharpen = max(Limits.w, 0.0);

    vec3 rgbM = texture(Source, texCoord).rgb;
    vec3 rgbN = texture(Source, texCoord + vec2(0.0, -texel.y)).rgb;
    vec3 rgbS = texture(Source, texCoord + vec2(0.0, texel.y)).rgb;
    vec3 rgbW = texture(Source, texCoord + vec2(-texel.x, 0.0)).rgb;
    vec3 rgbE = texture(Source, texCoord + vec2(texel.x, 0.0)).rgb;

    float lM = lumaOf(rgbM);
    float lN = lumaOf(rgbN);
    float lS = lumaOf(rgbS);
    float lW = lumaOf(rgbW);
    float lE = lumaOf(rgbE);

    float lMin = min(lM, min(min(lN, lS), min(lW, lE)));
    float lMax = max(lM, max(max(lN, lS), max(lW, lE)));
    float edge = lMax - lMin;

    vec3 result = rgbM;

    if (edge > max(thresholdMin, lMax * threshold)) {
        vec2 dir = vec2(-((lN + lS) - 2.0 * lM), (lW + lE) - 2.0 * lM);
        float dirReduce = max((lN + lS + lW + lE) * 0.25 * 0.0625, 1.0 / 128.0);
        float rcpDirMin = 1.0 / (min(abs(dir.x), abs(dir.y)) + dirReduce);
        dir = clamp(dir * rcpDirMin, vec2(-8.0), vec2(8.0)) * texel;

        vec3 rgbA = 0.5 * (texture(Source, texCoord + dir * (1.0 / 3.0 - 0.5)).rgb
                         + texture(Source, texCoord + dir * (2.0 / 3.0 - 0.5)).rgb);
        vec3 rgbB = rgbA * 0.5 + 0.25 * (texture(Source, texCoord + dir * -0.5).rgb
                                       + texture(Source, texCoord + dir * 0.5).rgb);
        float lB = lumaOf(rgbB);
        result = (lB < lMin || lB > lMax) ? rgbA : rgbB;

        if (mode >= 2) {
            vec2 cross = vec2(dir.y, -dir.x) * 0.5;
            vec3 rgbC = 0.25 * (texture(Source, texCoord + cross).rgb
                              + texture(Source, texCoord - cross).rgb
                              + texture(Source, texCoord + dir * 0.25).rgb
                              + texture(Source, texCoord - dir * 0.25).rgb);
            float lC = lumaOf(rgbC);
            if (lC >= lMin && lC <= lMax) {
                result = mix(result, rgbC, 0.45);
            }
        }

        if (mode == 3) {
            vec3 soft = 0.25 * (rgbN + rgbS + rgbW + rgbE);
            float lSoft = lumaOf(soft);
            if (lSoft >= lMin && lSoft <= lMax) {
                result = mix(result, soft, 0.35);
            }
        }

        result = mix(rgbM, result, blend);
    }

    if (subpixel > 0.0) {
        float detail = clamp(edge * 4.0, 0.0, 1.0);
        result = mix(result, rgbM, subpixel * 0.35 * detail);
    }

    if (sharpen > 0.0) {
        vec3 soft = 0.25 * (rgbN + rgbS + rgbW + rgbE);
        result = clamp(result + (result - soft) * sharpen, vec3(0.0), vec3(1.0));
    }

    fragColor = vec4(result, 1.0);
}
