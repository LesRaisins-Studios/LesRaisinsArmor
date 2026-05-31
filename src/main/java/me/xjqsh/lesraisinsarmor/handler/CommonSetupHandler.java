package me.xjqsh.lesraisinsarmor.handler;

import com.google.gson.*;
import me.xjqsh.lesraisinsarmor.LesRaisinsArmor;
import me.xjqsh.lesraisinsarmor.network.NetworkHandler;
import me.xjqsh.lesraisinsarmor.resource.ArmorSuitIndex;
import me.xjqsh.lesraisinsarmor.resource.ArmorSuitManager;
import me.xjqsh.lesraisinsarmor.resource.ArmorSuitType;
import me.xjqsh.lesraisinsarmor.resource.data.ArmorPartData;
import me.xjqsh.lesraisinsarmor.resource.data.ArmorSetEffectData;
import me.xjqsh.lesraisinsarmor.resource.data.AttributeModifierData;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.EnumMap;
import java.util.UUID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonSetupHandler {

    /**
     * 套装索引的 Gson 实例，配置了所有需要的类型适配器
     */
    public static final Gson SUIT_GSON = new GsonBuilder()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .registerTypeAdapter(ArmorSetEffectData.class, new ArmorSetEffectData.Deserializer())
            .registerTypeAdapter(AttributeModifierData.class, new AttributeModifierData.Deserializer())
            .setPrettyPrinting()
            .create();

    /**
     * 盔甲部位对应的属性修改器 UUID（与 Minecraft 原版保持一致）
     */
    private static final EnumMap<ArmorItem.Type, UUID> ARMOR_MODIFIER_UUID_PER_TYPE = Util.make(
            new EnumMap<>(ArmorItem.Type.class), (enumMap) -> {
                enumMap.put(ArmorItem.Type.BOOTS, UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"));
                enumMap.put(ArmorItem.Type.LEGGINGS, UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"));
                enumMap.put(ArmorItem.Type.CHESTPLATE, UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"));
                enumMap.put(ArmorItem.Type.HELMET, UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150"));
            });

    @SubscribeEvent
    public static void onSetupEvent(FMLCommonSetupEvent event) {
        event.enqueueWork(NetworkHandler::init);
        event.enqueueWork(CommonSetupHandler::initArmorSuitType);
    }

    /**
     * 初始化 ArmorSuitType 和 ArmorSuitManager
     * 定义套装 JSON 配置的解析工厂
     */
    private static void initArmorSuitType() {
        ArmorSuitType type = new ArmorSuitType(
                ResourceLocation.tryBuild(LesRaisinsArmor.MOD_ID, "armor_suit"),
                (gson, id, json, registryAccess) -> {
                    JsonObject obj = json.getAsJsonObject();

                    // 解析名称（翻译键）
                    Component name;
                    if (obj.has("name")) {
                        name = Component.translatable(obj.get("name").getAsString());
                    } else {
                        name = Component.translatable("suit." + id.getNamespace() + "." + id.getPath() + ".name");
                    }

                    // 解析排序
                    int sort = obj.has("sort") ? obj.get("sort").getAsInt() : 0;

                    // 解析描述文本键
                    String tooltip = null;
                    if (obj.has("tooltip")) {
                        tooltip = obj.get("tooltip").getAsString();
                    }

                    // 解析套装效果
                    ArmorSetEffectData setEffect = null;
                    if (obj.has("setEffect")) {
                        setEffect = gson.fromJson(obj.get("setEffect"), ArmorSetEffectData.class);
                    }

                    // 解析四个部位的盔甲数据
                    EnumMap<ArmorItem.Type, ArmorPartData> parts = new EnumMap<>(ArmorItem.Type.class);
                    if (obj.has("parts")) {
                        JsonObject partsObj = obj.getAsJsonObject("parts");
                        for (ArmorItem.Type armorType : ArmorItem.Type.values()) {
                            String slotName = armorType.getName();
                            if (partsObj.has(slotName)) {
                                JsonObject partJson = partsObj.getAsJsonObject(slotName);
                                // 手动反序列化 ArmorPartData.Struct 然后创建 ArmorPartData
                                ArmorPartData partData = parsePartData(gson, partJson, armorType);
                                if (partData != null) {
                                    parts.put(armorType, partData);
                                }
                            }
                        }
                    }

                    return new ArmorSuitIndex(id, name, sort, tooltip, parts, setEffect);
                },
                SUIT_GSON
        );

        ArmorSuitManager.init(type);
    }

    /**
     * 从 JSON 对象解析单个部位的 ArmorPartData
     */
    private static ArmorPartData parsePartData(Gson gson, JsonObject json, ArmorItem.Type armorType) {
        ArmorPartData.Struct struct = gson.fromJson(json, ArmorPartData.Struct.class);
        UUID uuid = ARMOR_MODIFIER_UUID_PER_TYPE.get(armorType);
        return ArmorPartData.fromJson(uuid, struct);
    }
}
