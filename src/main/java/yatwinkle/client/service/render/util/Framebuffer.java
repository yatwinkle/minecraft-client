package yatwinkle.client.service.render.util;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL32C.*;

public class Framebuffer {
    private int id;
    public int texture;
    public double sizeMulti = 1;
    public int width, height;

    public Framebuffer(double sizeMulti) {
        this.sizeMulti = sizeMulti;
        init();
    }

    private void init() {
        id = GlStateManager.glGenFramebuffers();
        bind();

        texture = GlStateManager._genTexture();
        GlStateManager._activeTexture(GL_TEXTURE0);
        GlStateManager._bindTexture(texture);

        GlStateManager._pixelStore(GL_UNPACK_SWAP_BYTES, GL_FALSE);
        GlStateManager._pixelStore(GL_UNPACK_LSB_FIRST, GL_FALSE);
        GlStateManager._pixelStore(GL_UNPACK_ROW_LENGTH, 0);
        GlStateManager._pixelStore(GL_UNPACK_IMAGE_HEIGHT, 0);
        GlStateManager._pixelStore(GL_UNPACK_SKIP_ROWS, 0);
        GlStateManager._pixelStore(GL_UNPACK_SKIP_PIXELS, 0);
        GlStateManager._pixelStore(GL_UNPACK_SKIP_IMAGES, 0);
        GlStateManager._pixelStore(GL_UNPACK_ALIGNMENT, 4);

        GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        width = (int) (MinecraftClient.getInstance().getWindow().getFramebufferWidth() * sizeMulti);
        height = (int) (MinecraftClient.getInstance().getWindow().getFramebufferHeight() * sizeMulti);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, (ByteBuffer) null);
        GlStateManager._glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);

        unbind();
    }

    public void bind() {
        GlStateManager._glBindFramebuffer(GL_FRAMEBUFFER, id);
    }

    public void setViewport() {
        GlStateManager._viewport(0, 0, width, height);
    }

    public void unbind() {
        MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
    }

    public void resize() {
        GlStateManager._glDeleteFramebuffers(id);
        GlStateManager._deleteTexture(texture);

        init();
    }
}