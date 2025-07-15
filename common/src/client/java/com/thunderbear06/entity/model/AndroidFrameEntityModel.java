package com.thunderbear06.entity.model;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.entity.android.frame.AndroidFrame;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;

public class AndroidFrameEntityModel extends PlayerEntityModel<AndroidFrame> {
    public AndroidFrameEntityModel(ModelPart root) {
        super(root, false);
    }

    @Override
    public void animateModel(AndroidFrame livingEntity, float f, float g, float h) {
        byte maxComps = CCAndroids.CONFIG.CompsForConstruction;
        byte maxIngots = CCAndroids.CONFIG.IngotsForConstruction;

        int comps = livingEntity.getComponentsNeeded();
        int ingots = livingEntity.getIngotsNeeded();

        this.leftArm.visible = comps <= maxComps * 0.75;
        this.rightArm.visible = comps <= maxComps * 0.5;
        this.hat.visible = comps == 0;
        this.jacket.visible = ingots <= maxIngots * 0.8;
        this.leftSleeve.visible = ingots <= maxIngots * 0.6;
        this.rightSleeve.visible = ingots <= maxIngots * 0.4;
        this.leftPants.visible = ingots <= maxIngots * 0.2;
        this.rightPants.visible = ingots == 0;

        super.animateModel(livingEntity, f, g, h);
    }

    @Override
    public void setAngles(AndroidFrame livingEntity, float f, float g, float h, float i, float j) {}
}
