package me.xjqsh.lesraisinsarmor.resource;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * 定义盔甲套装的"类型"，类似于 TACZ 的 CustomItemType
 * 包含配置文件路径映射和工厂函数
 */
public record ArmorSuitType(
        ResourceLocation id,
        FileToIdConverter configPathConverter,
        Gson gson,
        SuitIndexFactory factory
) {

    /**
     * 便捷构造函数：使用默认的 "armor_suits" 路径
     */
    public ArmorSuitType(ResourceLocation id, SuitIndexFactory factory, Gson gson) {
        this(id, FileToIdConverter.json("armor_suits"), gson, factory);
    }

    /**
     * 套装索引工厂接口
     * 从 JSON 数据创建 ArmorSuitIndex 实例
     */
    @FunctionalInterface
    public interface SuitIndexFactory {
        /**
         * 创建一个 ArmorSuitIndex 实例
         *
         * @param gson           Gson 实例
         * @param id             套装注册 ID
         * @param json           套装配置 JSON
         * @param registryAccess 注册表访问
         * @return ArmorSuitIndex 实例，或 null（创建失败）
         */
        @Nullable
        ArmorSuitIndex create(Gson gson, ResourceLocation id, JsonElement json,
                              RegistryAccess registryAccess);
    }

    /**
     * 创建对应的 ArmorSuitManager
     */
    public ArmorSuitManager createManager() {
        return new ArmorSuitManager(this, "ArmorSuitManager(" + id + ")");
    }
}
