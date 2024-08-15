package me.xjqsh.lesraisinsarmor.resource.data;


import com.google.common.collect.ImmutableMap;
import me.xjqsh.lesraisinsarmor.resource.pojo.ArmorDataPOJO;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class ArmorData {
    private ImmutableMap<Attribute, AttributeModifier> attribute;
    private ArmorData(){}

    public static void fromData(ArmorDataPOJO pojo) {
    }
}
