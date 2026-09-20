layout(std140) uniform ThemeWaveParams {
    vec4 kThemeData[65];
};

vec2 kimikoScreenSize() {
    return max(kThemeData[2].xy, vec2(1.0));
}

float kimikoGuiScale() {
    return max(kThemeData[2].w, 0.0001);
}

int kimikoClientStopCount() {
    return clamp(int(kThemeData[0].x + 0.5), 1, 6);
}

float kimikoClientPhase() {
    return kThemeData[0].y;
}

float kimikoClientStyleId() {
    return kThemeData[0].z;
}

float kimikoClientSweep() {
    return kThemeData[0].w;
}

float kimikoClientPrevStyleId() {
    return kThemeData[1].x;
}

float kimikoClientClosed() {
    return clamp(kThemeData[1].y, 0.0, 1.0);
}

float kimikoClientScrollPhase() {
    return kThemeData[1].z;
}

bool kimikoWaveActive() {
    return kThemeData[1].w > 0.5;
}

vec2 kimikoFragXYFromUV(vec2 uv) {
    return uv * kimikoScreenSize();
}

vec2 kimikoFragXYMapped(vec2 local, vec4 map) {
    return map.xy + local * map.z;
}

vec2 kimikoFragXYMappedFlipY(vec2 local, vec4 map) {
    return vec2(map.x + local.x * map.z, map.y - local.y * map.z);
}

float kimikoThemeLayerCoverage(int layer, vec2 fragXY) {
    vec4 wave = kThemeData[3 + layer];
    if (wave.w < 0.5) {
        return 0.0;
    }
    if (wave.w > 1.5) {
        return clamp(wave.z, 0.0, 1.0);
    }
    vec2 res = max(kThemeData[2].xy, vec2(1.0));
    float aspect = res.x / res.y;
    vec2 d = (fragXY / res - wave.xy) * vec2(aspect, 1.0);
    float wf = max(kThemeData[2].z, 0.0005);
    return 1.0 - smoothstep(wave.z - wf, wave.z + wf, length(d));
}

vec2 kThemeCovXY = vec2(-1.0e18);
float kThemeCov0 = 0.0;
float kThemeCov1 = 0.0;
float kThemeCov2 = 0.0;
float kThemeCov3 = 0.0;
float kThemeCov4 = 0.0;
float kThemeCov5 = 0.0;

void kimikoThemeCoverageCache(vec2 fragXY) {
    if (kThemeCovXY == fragXY) {
        return;
    }
    kThemeCovXY = fragXY;
    kThemeCov0 = kimikoThemeLayerCoverage(0, fragXY);
    kThemeCov1 = kimikoThemeLayerCoverage(1, fragXY);
    kThemeCov2 = kimikoThemeLayerCoverage(2, fragXY);
    kThemeCov3 = kimikoThemeLayerCoverage(3, fragXY);
    kThemeCov4 = kimikoThemeLayerCoverage(4, fragXY);
    kThemeCov5 = kimikoThemeLayerCoverage(5, fragXY);
}

vec3 kimikoThemeSlot(int slot, vec2 fragXY) {
    kimikoThemeCoverageCache(fragXY);
    vec3 c = kThemeData[9 + slot].rgb;
    c = mix(c, kThemeData[17 + slot].rgb, kThemeCov0);
    c = mix(c, kThemeData[25 + slot].rgb, kThemeCov1);
    c = mix(c, kThemeData[33 + slot].rgb, kThemeCov2);
    c = mix(c, kThemeData[41 + slot].rgb, kThemeCov3);
    c = mix(c, kThemeData[49 + slot].rgb, kThemeCov4);
    c = mix(c, kThemeData[57 + slot].rgb, kThemeCov5);
    return c;
}

vec3 kimikoClientPrimary(vec2 fragXY) {
    return kimikoThemeSlot(0, fragXY);
}

vec3 kimikoClientSecondary(vec2 fragXY) {
    return kimikoThemeSlot(1, fragXY);
}

vec3 kimikoClientStop(int index, vec2 fragXY) {
    return kimikoThemeSlot(2 + clamp(index, 0, 5), fragXY);
}

vec3 kimikoClientPaletteColor(float t, vec2 fragXY) {
    int count = kimikoClientStopCount();
    if (count <= 1) {
        return kimikoClientStop(0, fragXY);
    }
    float f = clamp(t, 0.0, 1.0) * float(count - 1);
    int i = clamp(int(floor(f)), 0, count - 1);
    int j = min(i + 1, count - 1);
    float frac = clamp(f - float(i), 0.0, 1.0);
    frac = frac * frac * (3.0 - 2.0 * frac);
    return mix(kimikoClientStop(i, fragXY), kimikoClientStop(j, fragXY), frac);
}

vec3 kimikoClientPaletteLoop(float t, vec2 fragXY) {
    int count = kimikoClientStopCount();
    if (count <= 1) {
        return kimikoClientStop(0, fragXY);
    }
    float f = fract(t) * float(count);
    int i1 = clamp(int(floor(f)), 0, count - 1);
    float u = clamp(f - float(i1), 0.0, 1.0);

    int i0 = i1 - 1;
    if (i0 < 0) i0 += count;
    int i2 = i1 + 1;
    if (i2 >= count) i2 -= count;
    int i3 = i1 + 2;
    if (i3 >= count) i3 -= count;

    vec3 c0 = kimikoClientStop(i0, fragXY);
    vec3 c1 = kimikoClientStop(i1, fragXY);
    vec3 c2 = kimikoClientStop(i2, fragXY);
    vec3 c3 = kimikoClientStop(i3, fragXY);

    float u2 = u * u;
    float u3 = u2 * u;
    vec3 cyclicCol = 0.5 * ((2.0 * c1)
                      + (-c0 + c2) * u
                      + (2.0 * c0 - 5.0 * c1 + 4.0 * c2 - c3) * u2
                      + (-c0 + 3.0 * c1 - 3.0 * c2 + c3) * u3);

    float tri = 0.5 - 0.5 * cos(6.2831853 * fract(t));
    vec3 mirrorCol = kimikoClientPaletteColor(tri, fragXY);

    return clamp(mix(mirrorCol, cyclicCol, kimikoClientClosed()), 0.0, 1.0);
}
