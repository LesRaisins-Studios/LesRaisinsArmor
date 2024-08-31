package me.xjqsh.lesraisinsarmor.resource.data;


import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;

import java.util.UUID;

public class ArmorPartData {
    private ImmutableMultimap<Attribute, AttributeModifier> attributes;
    private Ingredient repairIngredient;
    private SoundEvent equipSound;
    private int enchantmentValue;
    private int maxDurability;
    private int defense = 0;
    private int toughness = 0;
    private int knockbackResistance = 0;

    public Multimap<Attribute, AttributeModifier> getAttributes() {
        return attributes;
    }

    public Ingredient getRepairIngredient() {
        return repairIngredient;
    }

    public SoundEvent getEquipSound() {
        return equipSound;
    }

    public int getEnchantmentValue() {
        return enchantmentValue;
    }

    public int getMaxDurability() {
        return maxDurability;
    }

    public int getDefense() {
        return defense;
    }

    public int getToughness() {
        return toughness;
    }

    public int getKnockbackResistance() {
        return knockbackResistance;
    }

    public static ArmorPartData fromJson(UUID uuid, ArmorPartData.Struct rawData) {
        ArmorPartData data = new ArmorPartData();
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier",
                rawData.defense, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness",
                rawData.toughness, AttributeModifier.Operation.ADDITION));
        if (rawData.knockbackResistance > 0) {
            builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, "Armor knockback resistance",
                    rawData.knockbackResistance, AttributeModifier.Operation.ADDITION));
        }
        data.attributes = builder.build();
        try {
            data.repairIngredient = CraftingHelper.getIngredient(rawData.repairIngredient, true);
        } catch (JsonParseException ignore) {}
        data.equipSound = SoundEvent.createVariableRangeEvent(rawData.sound);
        data.enchantmentValue = rawData.enchantmentValue;
        data.maxDurability = rawData.maxDurability;
        data.defense = rawData.defense;
        data.toughness = rawData.toughness;
        data.knockbackResistance = rawData.knockbackResistance;

        return data;
    }

    public static class Struct {
        private int defense = 0;
        private int toughness = 0;
        private int knockbackResistance = 0;
        private int enchantmentValue = 5;
        private int maxDurability = 128;
        private ResourceLocation sound = new ResourceLocation("item.armor.equip_leather");
        private JsonElement repairIngredient = null;
    }
}
