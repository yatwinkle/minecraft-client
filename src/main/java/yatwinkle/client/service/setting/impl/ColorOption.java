package yatwinkle.client.service.setting.impl;

import yatwinkle.client.service.setting.AbstractOption;

import java.awt.Color;

public class ColorOption extends AbstractOption<Color> {

    private Color value;

    public ColorOption(String id, String name, String description, Color defaultValue) {
        super(id, name, description, defaultValue);
        this.value = defaultValue;
    }

    public ColorOption(String id, String name, String description, int argb) {
        this(id, name, description, new Color(argb, true));
    }

    @Override
    public Color get() {
        return value;
    }

    public int getInt() {
        return value.getRGB();
    }

    @Override
    protected void setValueInternal(Color newValue) {
        if (newValue == null) return;

        if (this.value.equals(newValue)) return;

        this.value = newValue;
        notifyListeners(newValue);
    }

    public void setRed(int red) {
        setValueInternal(new Color(red, value.getGreen(), value.getBlue(), value.getAlpha()));
    }

    public void setGreen(int green) {
        setValueInternal(new Color(value.getRed(), green, value.getBlue(), value.getAlpha()));
    }

    public void setBlue(int blue) {
        setValueInternal(new Color(value.getRed(), value.getGreen(), blue, value.getAlpha()));
    }

    public void setAlpha(int alpha) {
        setValueInternal(new Color(value.getRed(), value.getGreen(), value.getBlue(), alpha));
    }

    public float[] getHSB() {
        return Color.RGBtoHSB(value.getRed(), value.getGreen(), value.getBlue(), null);
    }

    public float getHue() {
        return getHSB()[0];
    }

    public float getSaturation() {
        return getHSB()[1];
    }

    public float getBrightness() {
        return getHSB()[2];
    }

    public void setHue(float hue) {
        float[] hsb = getHSB();
        int rgb = Color.HSBtoRGB(hue, hsb[1], hsb[2]);
        Color newCol = new Color((rgb & 0x00FFFFFF) | (value.getAlpha() << 24), true);
        setValueInternal(newCol);
    }

    public void setSaturation(float saturation) {
        float[] hsb = getHSB();
        int rgb = Color.HSBtoRGB(hsb[0], saturation, hsb[2]);
        Color newCol = new Color((rgb & 0x00FFFFFF) | (value.getAlpha() << 24), true);
        setValueInternal(newCol);
    }

    public void setBrightness(float brightness) {
        float[] hsb = getHSB();
        int rgb = Color.HSBtoRGB(hsb[0], hsb[1], brightness);
        Color newCol = new Color((rgb & 0x00FFFFFF) | (value.getAlpha() << 24), true);
        setValueInternal(newCol);
    }
}
