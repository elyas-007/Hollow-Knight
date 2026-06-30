#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;     
uniform sampler2D u_mask;         

uniform float u_minU;
uniform float u_maxU;        
uniform float u_minV;             
uniform float u_maxV;             
uniform float u_soulPercentage;
uniform float u_glowWeight; 

void main() {
    float localX = (v_texCoords.x - u_minU) / (u_maxU - u_minU);
    float localY = (u_maxV - v_texCoords.y) / (u_maxV - u_minV);
    
    vec4 maskColor = texture2D(u_mask, vec2(localX, 1.0 - localY));

    float adjustedPercentage = 0.12 + (u_soulPercentage * 0.88);
    float shiftedLocalY = localY + (1.0 - adjustedPercentage);

    if (shiftedLocalY > 1.0 && u_glowWeight < 1.0) {
        if (u_glowWeight <= 0.0) {
            discard;
        }
    }

    float shiftedV = u_maxV - (shiftedLocalY * (u_maxV - u_minV));
    
    shiftedV = clamp(shiftedV, u_minV, u_maxV);

    vec4 liquidColor = texture2D(u_texture, vec2(v_texCoords.x, shiftedV));

    vec4 finalLiquid = v_color * vec4(liquidColor.rgb, liquidColor.a * maskColor.a);
    vec4 finalFullOrb = v_color * maskColor;

    gl_FragColor = mix(finalLiquid, finalFullOrb, u_glowWeight);
}