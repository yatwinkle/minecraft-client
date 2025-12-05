package yatwinkle.client.service.setting.impl;

import yatwinkle.client.service.setting.AbstractOption;

public class KeyOption extends AbstractOption<Integer> {

    private int value;
    private boolean listening;

    public KeyOption(String id, String name, String description, int defaultKey) {
        super(id, name, description, defaultKey);
        this.value = defaultKey;
        this.listening = false;
    }

    @Override
    public Integer get() {
        return value;
    }

    public int getKey() {
        return value;
    }

    @Override
    protected void setValueInternal(Integer newValue) {
        int val = newValue != null ? newValue : 0;
        if (this.value == val) return;

        this.value = val;
        this.listening = false;
        notifyListeners(val);
    }

    public boolean matches(int key) {
        return value != 0 && value == key;
    }

    public boolean matchesMouse(int button) {
        return value != 0 && value == -(button + 100);
    }

    public void setListening(boolean listening) {
        this.listening = listening;
    }

    public boolean isListening() {
        return listening;
    }
}
