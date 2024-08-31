package me.xjqsh.lesraisinsarmor.resource.data;

import com.google.gson.*;
import me.xjqsh.lesraisinsarmor.resource.ArmorDataManager;
import net.minecraft.Util;
import net.minecraft.world.item.ArmorItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class ArmorData {
    private final EnumMap<ArmorItem.Type, ArmorPartData> partData = new EnumMap<>(ArmorItem.Type.class);
    @Nullable
    public ArmorPartData getByType(@NotNull ArmorItem.Type type){
        return partData.get(type);
    }

    public <T> T getByType(@NotNull ArmorItem.Type type, T defaultValue, Function<ArmorPartData, T> getter){
        var data = this.getByType(type);
        if (data != null) {
            return getter.apply(data);
        }
        return defaultValue;
    }

    public static <T> T getByType(ArmorData data, @NotNull ArmorItem.Type type, T defaultValue, Function<ArmorPartData, T> getter){
        if (data==null) {
            return defaultValue;
        }
        return data.getByType(type, defaultValue, getter);
    }

    public static class Deserializer implements JsonDeserializer<ArmorData> {
        private static final EnumMap<ArmorItem.Type, UUID> ARMOR_MODIFIER_UUID_PER_TYPE = Util.make(new EnumMap<>(ArmorItem.Type.class), (enumMap) -> {
            enumMap.put(ArmorItem.Type.BOOTS, UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"));
            enumMap.put(ArmorItem.Type.LEGGINGS, UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"));
            enumMap.put(ArmorItem.Type.CHESTPLATE, UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"));
            enumMap.put(ArmorItem.Type.HELMET, UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150"));
        });

        @Override
        public ArmorData deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
            ArmorData data = new ArmorData();
            JsonObject object = jsonElement.getAsJsonObject();
            Map<String, JsonElement> map = object.asMap();
            for (ArmorItem.Type armorType : ArmorItem.Type.values()){
                if (map.containsKey(armorType.getName())) {
                    var struct = ArmorDataManager.GSON.fromJson(map.get(armorType.getName()), ArmorPartData.Struct.class);
                    UUID uuid = ARMOR_MODIFIER_UUID_PER_TYPE.get(armorType);
                    data.partData.put(armorType, ArmorPartData.fromJson(uuid, struct));
                }
            }
            return data;
        }
    }
}
