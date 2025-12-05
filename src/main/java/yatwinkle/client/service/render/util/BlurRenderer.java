package yatwinkle.client.service.render.util;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntDoubleImmutablePair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.*;
import yatwinkle.client.service.render.providers.ResourceProvider;

public class BlurRenderer {
    private static final ShaderProgramKey BLUR_UP = new ShaderProgramKey(
            ResourceProvider.getShaderIdentifier("blur_up"),
            VertexFormats.POSITION,
            Defines.EMPTY
    );

    private static final ShaderProgramKey BLUR_DOWN = new ShaderProgramKey(
            ResourceProvider.getShaderIdentifier("blur_down"),
            VertexFormats.POSITION,
            Defines.EMPTY
    );

    private final IntDoubleImmutablePair[] strengths = new IntDoubleImmutablePair[]{
            IntDoubleImmutablePair.of(1, 1.25), // LVL 1
            IntDoubleImmutablePair.of(1, 2.25), // LVL 2
            IntDoubleImmutablePair.of(2, 2.0),  // LVL 3
            IntDoubleImmutablePair.of(2, 3.0),  // LVL 4
            IntDoubleImmutablePair.of(2, 4.25), // LVL 5
            IntDoubleImmutablePair.of(3, 2.5),  // LVL 6
            IntDoubleImmutablePair.of(3, 3.25), // LVL 7
            IntDoubleImmutablePair.of(3, 4.25), // LVL 8
            IntDoubleImmutablePair.of(3, 5.5),  // LVL 9
            IntDoubleImmutablePair.of(4, 3.25), // LVL 10
            IntDoubleImmutablePair.of(4, 4.0),  // LVL 11
            IntDoubleImmutablePair.of(4, 5.0),  // LVL 12
            IntDoubleImmutablePair.of(4, 6.0),  // LVL 13
            IntDoubleImmutablePair.of(4, 7.25), // LVL 14
            IntDoubleImmutablePair.of(4, 8.25), // LVL 15
            IntDoubleImmutablePair.of(5, 4.5),  // LVL 16
            IntDoubleImmutablePair.of(5, 5.25), // LVL 17
            IntDoubleImmutablePair.of(5, 6.25), // LVL 18
            IntDoubleImmutablePair.of(5, 7.25), // LVL 19
            IntDoubleImmutablePair.of(5, 8.5)   // LVL 20
    };

    private final Framebuffer[] fbos = new Framebuffer[6];
    private boolean isInitialized;

    private void renderToFbo(Framebuffer targetFbo, int sourceTex, ShaderProgramKey shaderKey, float offset) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();

        targetFbo.bind();
        targetFbo.setViewport();

        ShaderProgram shader = RenderSystem.setShader(shaderKey);
        shader.getUniform("uHalfTexelSize").set(
                0.5f / targetFbo.width,
                0.5f / targetFbo.height
        );
        shader.getUniform("uOffset").set(offset);

        shader.addSamplerTexture("uTexture", sourceTex);

        BufferBuilder builder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.BLIT_SCREEN);
        builder.vertex(-1.0F, -1.0F, 0.0F);
        builder.vertex(1.0F, -1.0F, 0.0F);
        builder.vertex(1.0F, 1.0F, 0.0F);
        builder.vertex(-1.0F, 1.0F, 0.0F);

        BufferRenderer.drawWithGlobalProgram(builder.end());

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    public void onRenderAfterWorld() {
        if (!isInitialized) {
            for (int i = 0; i < fbos.length; i++) {
                if (fbos[i] == null) {
                    fbos[i] = new Framebuffer(1 / Math.pow(2, i));
                }
            }

            isInitialized = true;
        }

        IntDoubleImmutablePair strength = strengths[10];
        int iterations = strength.leftInt();
        float offset = (float) strength.rightDouble();

        renderToFbo(fbos[0], MinecraftClient.getInstance().getFramebuffer().getColorAttachment(), BLUR_DOWN, offset);

        for (int i = 0; i < iterations; i++) {
            renderToFbo(fbos[i + 1], fbos[i].texture, BLUR_DOWN, offset);
        }

        for (int i = iterations; i >= 1; i--) {
            renderToFbo(fbos[i - 1], fbos[i].texture, BLUR_UP, offset);
        }

        fbos[0].unbind();
    }

    public void onResolutionChanged() {
        for (int i = 0; i < fbos.length; i++) {
            if (fbos[i] != null) {
                fbos[i].resize();
            } else {
                fbos[i] = new Framebuffer(1 / Math.pow(2, i));
            }
        }
    }

    public int getBlurredTexture() {
        return fbos[0].texture;
    }
}