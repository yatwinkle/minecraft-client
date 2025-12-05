package yatwinkle.client.service.render.builders;

import yatwinkle.client.service.render.builders.impl.DualKawaseBuilder;
import yatwinkle.client.service.render.builders.impl.BorderBuilder;
import yatwinkle.client.service.render.builders.impl.RectangleBuilder;
import yatwinkle.client.service.render.builders.impl.TextureBuilder;
import yatwinkle.client.service.render.builders.impl.TextBuilder;;

public final class Builder {

    private static final RectangleBuilder RECTANGLE_BUILDER = new RectangleBuilder();
    private static final BorderBuilder BORDER_BUILDER = new BorderBuilder();
    private static final TextureBuilder TEXTURE_BUILDER = new TextureBuilder();
    private static final TextBuilder TEXT_BUILDER = new TextBuilder();
    private static final DualKawaseBuilder DUAL_KAWASE_BUILDER = new DualKawaseBuilder();

    public static RectangleBuilder rectangle() {
        return RECTANGLE_BUILDER;
    }

    public static BorderBuilder border() {
        return BORDER_BUILDER;
    }

    public static TextureBuilder texture() {
        return TEXTURE_BUILDER;
    }

    public static TextBuilder text() {
        return TEXT_BUILDER;
    }

    public static DualKawaseBuilder blur() {
        return DUAL_KAWASE_BUILDER;
    }
}