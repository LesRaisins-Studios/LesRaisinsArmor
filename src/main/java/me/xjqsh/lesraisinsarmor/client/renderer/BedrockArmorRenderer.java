package me.xjqsh.lesraisinsarmor.client.renderer;

import com.github.mcmodderanchor.simplebedrockmodel.v1.client.model.BedrockArmorModel;
import com.github.mcmodderanchor.simplebedrockmodel.v1.client.renderer.GeoArmorRenderer;
import net.minecraft.resources.ResourceLocation;

public class BedrockArmorRenderer extends GeoArmorRenderer {
    public BedrockArmorRenderer(BedrockArmorModel model, ResourceLocation texture) {
        super(model, texture);
    }
}
