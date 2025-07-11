package com.thunderbear06.config;

public class CCAndroidsConfig implements ConfigLoader.ConfigState {
    public boolean DebugLogging = false;

    public byte CompsForConstruction = 8;
    public byte IngotsForConstruction = 10;

    public float CompsDroppedOnDeathPercentage = 0.5f;
    public float IngotsDroppedOnDeathPercentage = 0.5f;

    public float AndroidMaxHealth = 20.0f;
    public float AndroidDamage = 1.0f;
    public float AndroidSpeed = 0.6f;
    public float AndroidArmor = 0.0f;

    public float AdvAndroidMaxHealth = 25.0f;
    public float AdvAndroidDamage = 3.0f;
    public float AdvAndroidSpeed = 0.9f;
    public float AdvAndroidArmor = 3.0f;

    public float ComAndroidMaxHealth = 25.0f;
    public float ComAndroidDamage = 3.0f;
    public float ComAndroidSpeed = 0.9f;
    public float ComAndroidArmor = 3.0f;

    public float RogueMaxHealth = 25.0f;
    public float RogueDamage = 5.0f;
    public float RogueSpeed = 0.6f;
    public float RogueArmor = 5.0f;

    public boolean RoguesSpawnNaturally = true;
    public boolean RoguesSpawnWithTools = true;
}
