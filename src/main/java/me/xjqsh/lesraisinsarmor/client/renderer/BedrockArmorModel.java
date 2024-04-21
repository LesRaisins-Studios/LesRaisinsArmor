package me.xjqsh.lesraisinsarmor.client.renderer;

import me.xjqsh.lesraisinsarmor.item.LrArmorItem;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BedrockArmorModel extends AnimatedGeoModel<LrArmorItem> {

	@Override
	public ResourceLocation getModelLocation(LrArmorItem item) {
		ResourceLocation name = item.getRegistryName();
		if (name == null) {
			return ModelBakery.MISSING_MODEL_LOCATION;
		}
		return new ResourceLocation(name.getNamespace(),"geo/" + item.getSuitIdf() + ".geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(LrArmorItem item) {
		ResourceLocation name = item.getRegistryName();
		if (name == null) {
			return ModelBakery.MISSING_MODEL_LOCATION;
		}
		return new ResourceLocation(name.getNamespace(),"textures/model/" + item.getSuitIdf() + ".png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(LrArmorItem item) {
		ResourceLocation name = item.getRegistryName();
		if (name == null) {
			return null;
		}
		return new ResourceLocation(name.getNamespace(),"animations/" + item.getSuitIdf() + ".animation.json");
	}
}