package com.hollow.models.entities.Knight;

import com.hollow.models.LanguageManager;

public enum Charm {
    SOUL_CATCHER("Soul Catcher"),
    DASH_MASTER("Dash Master"),
    UNBREAKABLE_STRENGTH("Unbreakable Strength"),
    QUICK_SLASH("Quick Slash"),
    QUICK_FOCUS("Quick Focus"),
    HEAVY_BLOW("Heavy Blow"),
    SHARP_SHADOW("Sharp Shadow"),
    VOID_HEART("Void Heart");

    private final String title;

    Charm(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return LanguageManager.getInstance().get(this.name() + "_desc");
    }
}
