package yatwinkle.client.service.render.renderers.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import org.joml.Matrix4f;
import yatwinkle.client.service.render.builders.states.QuadColorState;
import yatwinkle.client.service.render.builders.states.QuadRadiusState;
import yatwinkle.client.service.render.builders.states.SizeState;
import yatwinkle.client.service.render.providers.ResourceProvider;
import yatwinkle.client.service.render.renderers.IRenderer;
import yatwinkle.client.service.render.util.BlurRenderer;

public record BuiltDualKawase(
        SizeState size,
        QuadRadiusState radius,
        QuadColorState color,
        float smoothness
    ) implements IRenderer {

    private static final ShaderProgramKey PASSTHROUGH = new ShaderProgramKey(ResourceProvider.getShaderIdentifier("passthrough"),
        VertexFormats.POSITION_COLOR, Defines.EMPTY);
    public static BlurRenderer blur = new BlurRenderer();

    @Override
    public void render(Matrix4f matrix, float x, float y, float z) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();

        float width = this.size.width(), height = this.size.height();
        ShaderProgram shader = RenderSystem.setShader(PASSTHROUGH);
        shader.getUniform("uSize").set(width, height);
        shader.getUniform("uRadius").set(this.radius.radius1(), this.radius.radius2(),
            this.radius.radius3(), this.radius.radius4());
        shader.getUniform("uSmooth").set(this.smoothness);

        shader.addSamplerTexture("uTexture", blur.getBlurredTexture());

        BufferBuilder builder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        builder.vertex(matrix, x, y, z).color(this.color.color1());
        builder.vertex(matrix, x, y + height, z).color(this.color.color2());
        builder.vertex(matrix, x + width, y + height, z).color(this.color.color3());
        builder.vertex(matrix, x + width, y, z).color(this.color.color4());

        BufferRenderer.drawWithGlobalProgram(builder.end());

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

}