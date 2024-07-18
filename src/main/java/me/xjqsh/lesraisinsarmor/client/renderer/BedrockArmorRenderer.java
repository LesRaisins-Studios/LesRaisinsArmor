package me.xjqsh.lesraisinsarmor.client.renderer;

import me.xjqsh.lesraisinsarmor.LesRaisinsArmor;
import me.xjqsh.lesraisinsarmor.item.LrArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class BedrockArmorRenderer extends GeoArmorRenderer<LrArmorItem> {
	public BedrockArmorRenderer(String suit) {
		super(new DefaultedItemGeoModel<>(new ResourceLocation(LesRaisinsArmor.MOD_ID, "armor/"+suit)));
	}
}