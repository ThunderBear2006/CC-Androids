package com.thunderbear06.ai.modules;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.entity.android.BaseAndroidEntity;

public abstract class AbstractAndroidModule {
    public final BaseAndroidEntity android;
    public final AndroidBrain brain;

    public AbstractAndroidModule(BaseAndroidEntity owner, AndroidBrain brain) {
        this.android = owner;
        this.brain = brain;
    }
}
