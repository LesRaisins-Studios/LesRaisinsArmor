package me.xjqsh.lesraisinsarmor.resource.data;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Type;
import java.util.UUID;

/**
 * 属性修改器的 JSON 数据定义
 * 参照 TACZ 的 AttributeModifierMap 体系，将属性修改器配置数据驱动化
 */
public class AttributeModifierData {
    private final Attribute attribute;
    private final UUID uuid;
    private final double amount;
    private final AttributeModifier.Operation operation;

    public AttributeModifierData(Attribute attribute, UUID uuid, double amount, AttributeModifier.Operation operation) {
        this.attribute = attribute;
        this.uuid = uuid;
        this.amount = amount;
        this.operation = operation;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public UUID getUuid() {
        return uuid;
    }

    public double getAmount() {
        return amount;
    }

    public AttributeModifier.Operation getOperation() {
        return operation;
    }

    public AttributeModifier toModifier(String name) {
        return new AttributeModifier(uuid, name, amount, operation);
    }

    public static class Deserializer implements JsonDeserializer<AttributeModifierData> {
        @Override
        public AttributeModifierData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            ResourceLocation attributeId = context.deserialize(obj.get("attribute"), ResourceLocation.class);
            Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(attributeId);
            if (attribute == null) {
                throw new JsonParseException("Unknown attribute: " + attributeId);
            }
            UUID uuid = UUID.fromString(obj.get("uuid").getAsString());
            double amount = obj.get("amount").getAsDouble();
            String opStr = obj.get("operation").getAsString();
            AttributeModifier.Operation operation = switch (opStr) {
                case "addition" -> AttributeModifier.Operation.ADDITION;
                case "multiply_base" -> AttributeModifier.Operation.MULTIPLY_BASE;
                case "multiply_total" -> AttributeModifier.Operation.MULTIPLY_TOTAL;
                default -> throw new JsonParseException("Unknown attribute modifier operation: " + opStr);
            };
            return new AttributeModifierData(attribute, uuid, amount, operation);
        }
    }
}
