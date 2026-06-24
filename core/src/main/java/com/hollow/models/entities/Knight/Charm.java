package com.hollow.models.entities.Knight;

public enum Charm {
    SOUL_CATCHER("Soul Catcher", "Increases the amount of Soul gained per successful Nail strike on enemies."),
    DASH_MASTER("Dash Master", "Allows the player to dash at much shorter intervals."),
    UNBREAKABLE_STRENGTH("Unbreakable Strength", "Strengthens the knight, increasing the damage dealt to enemies through regular Nail strikes."),
    QUICK_SLASH("Quick Slash", "Greatly increases attack speed with the Nail and reduces the cooldown after each regular strike."),
    QUICK_FOCUS("Quick Focus", "Increases the focus speed for recovering health, shortening the time the player must remain stationary to heal."),
    HEAVY_BLOW("Heavy Blow", "Increases the knockback force of strikes, causing enemies to be thrown further back after receiving damage."),
    SHARP_SHADOW("Sharp Shadow", "When dashing through enemies, deals damage to them while the player takes no damage. Also increases dash length by 20%."),
    VOID_HEART("Void Heart", "Upgrades the game's abilities. Damage dealt by abilities is increased by 50%, and the upgraded animation for each ability is activated when equipped.");

    private final String title;
    private final String description;

    Charm(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
}
