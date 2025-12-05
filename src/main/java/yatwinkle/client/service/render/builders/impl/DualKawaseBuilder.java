package yatwinkle.client.service.render.builders.impl;

import yatwinkle.client.service.render.builders.AbstractBuilder;
import yatwinkle.client.service.render.builders.states.QuadColorState;
import yatwinkle.client.service.render.builders.states.QuadRadiusState;
import yatwinkle.client.service.render.builders.states.SizeState;
import yatwinkle.client.service.render.renderers.impl.BuiltDualKawase;

public final class DualKawaseBuilder extends AbstractBuilder<BuiltDualKawase> {
    private SizeState size;
    private QuadRadiusState radius;
    private QuadColorState color;
    private float smoothness;

    public DualKawaseBuilder size(SizeState size) {
        this.size = size;
        return this;
    }

    public DualKawaseBuilder radius(QuadRadiusState radius) {
        this.radius = radius;
        return this;
    }

    public DualKawaseBuilder color(QuadColorState color) {
        this.color = color;
        return this;
    }

    public DualKawaseBuilder smoothness(float smoothness) {
        this.smoothness = smoothness;
        return this;
    }

    @Override
    protected BuiltDualKawase _build() {
        return new BuiltDualKawase(
            this.size,
            this.radius,
            this.color,
            this.smoothness
        );
    }

    @Override
    protected void reset() {
        this.size = SizeState.NONE;
        this.radius = QuadRadiusState.NO_ROUND;
        this.color = QuadColorState.TRANSPARENT;
        this.smoothness = 1.0f;
    }
}