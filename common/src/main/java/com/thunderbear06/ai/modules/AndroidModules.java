package com.thunderbear06.ai.modules;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.entity.android.BaseAndroidEntity;

public class AndroidModules {
    public final NavigationModule navigationModule;
    public final MiningModule miningModule;
    public final SensorModule sensorModule;
    public final InteractionModule interactionModule;

    public AndroidModules(BaseAndroidEntity android, AndroidBrain brain) {
        this.navigationModule = new NavigationModule(android, brain);
        this.miningModule = new MiningModule(android, brain);
        this.sensorModule = new SensorModule(android, brain, android.getEntitySearchRadius(), android.getBlockSearchRadius());
        this.interactionModule = new InteractionModule(android, brain);
    }
}
