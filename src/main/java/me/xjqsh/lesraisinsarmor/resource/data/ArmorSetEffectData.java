package me.xjqsh.lesraisinsarmor.resource.data;

import com.google.gson.*;
import me.xjqsh.lesraisinsarmor.effect.SuitEffect;
import net.minecraft.world.effect.MobEffectCategory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 套装效果的 JSON 数据定义
 * 从套装配置文件的 setEffect 节点反序列化
 */
public class ArmorSetEffectData {
    private final int color;
    private final List<AttributeModifierData> attributes;

    public ArmorSetEffectData(int color, List<AttributeModifierData> attributes) {
        this.color = color;
        this.attributes = Collections.unmodifiableList(new ArrayList<>(attributes));
    }

    public int getColor() {
        return color;
    }

    public List<AttributeModifierData> getAttributes() {
        return attributes;
    }

    /**
     * 根据套装 ID 创建一个 SuitEffect 实例
     * 属性修改器在创建时注入
     */
    public SuitEffect createEffect(String suitIdf) {
        SuitEffect effect = new SuitEffect(MobEffectCategory.BENEFICIAL, color, suitIdf);
        for (AttributeModifierData attr : attributes) {
            effect.addAttributeModifier(
                    attr.getAttribute(),
                    attr.getUuid().toString(),
                    attr.getAmount(),
                    attr.getOperation()
            );
        }
        return effect;
    }

    public static class Deserializer implements JsonDeserializer<ArmorSetEffectData> {
        @Override
        public ArmorSetEffectData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            int color = 0x000000;
            if (obj.has("color")) {
                JsonElement colorElem = obj.get("color");
                if (colorElem.isJsonPrimitive() && colorElem.getAsJsonPrimitive().isString()) {
                    String hex = colorElem.getAsString();
                    if (hex.startsWith("0x") || hex.startsWith("0X")) {
                        color = (int) Long.parseLong(hex.substring(2), 16);
                    } else if (hex.startsWith("#")) {
                        color = (int) Long.parseLong(hex.substring(1), 16);
                    } else {
                        color = (int) Long.parseLong(hex, 16);
                    }
                } else {
                    color = obj.get("color").getAsInt();
                }
            }

            List<AttributeModifierData> attributes = new ArrayList<>();
            if (obj.has("attributes")) {
                JsonArray attrsArray = obj.getAsJsonArray("attributes");
                for (JsonElement attrElem : attrsArray) {
                    attributes.add(context.deserialize(attrElem, AttributeModifierData.class));
                }
            }

            return new ArmorSetEffectData(color, attributes);
        }
    }
}
