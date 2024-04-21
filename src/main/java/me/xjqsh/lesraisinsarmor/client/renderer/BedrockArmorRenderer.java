package me.xjqsh.lesraisinsarmor.client.renderer;

import me.xjqsh.lesraisinsarmor.item.LrArmorItem;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class BedrockArmorRenderer extends GeoArmorRenderer<LrArmorItem> {
    	public BedrockArmorRenderer() {
    		super(new BedrockArmorModel());

    		this.headBone = "armorHead";
    		this.bodyBone = "armorBody";
    		this.leftArmBone = "armorLeftArm";
    		this.rightArmBone = "armorRightArm";
    		this.leftLegBone = "armorLeftLeg";
			this.rightLegBone = "armorRightLeg";
    		this.leftBootBone = "armorLeftBoot";
    		this.rightBootBone = "armorRightBoot";

			leftArm.visible = false;
    	}
    }