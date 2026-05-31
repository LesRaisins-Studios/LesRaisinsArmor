package me.xjqsh.lesraisinsarmor.resource;

import me.xjqsh.lesraisinsarmor.resource.data.ArmorPartData;
import me.xjqsh.lesraisinsarmor.resource.data.ArmorSetEffectData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * 盔甲套装的数据索引，类似于 TACZ 的 CustomItemIndex<ArmorSuitData>
 * 每个套装对应一个索引实例，包含所有 4 个部位的属性数据和套装元数据
 *
 * <p>这是一个不可变对象，在 ArmorSuitManager.apply() 时创建</p>
 */
public class ArmorSuitIndex {
    private final ResourceLocation id;
    private final Component name;
    private final int sort;
    @Nullable
    private final String tooltip;
    private final Map<ArmorItem.Type, ArmorPartData> parts;
    @Nullable
    private final ArmorSetEffectData setEffect;

    public ArmorSuitIndex(ResourceLocation id, Component name, int sort,
                          @Nullable String tooltip,
                          Map<ArmorItem.Type, ArmorPartData> parts,
                          @Nullable ArmorSetEffectData setEffect) {
        this.id = id;
        this.name = name;
        this.sort = sort;
        this.tooltip = tooltip;
        this.parts = Collections.unmodifiableMap(new EnumMap<>(parts));
        this.setEffect = setEffect;
    }

    /**
     * 获取套装 ID（如 lrarmor:attacker）
     */
    @NotNull
    public ResourceLocation getId() {
        return id;
    }

    /**
     * 获取套装显示名称
     */
    @NotNull
    public Component getName() {
        return name;
    }

    /**
     * 获取排序值（越小越靠前）
     */
    public int getSort() {
        return sort;
    }

    /**
     * 获取描述文本翻译键（可能为 null）
     */
    @Nullable
    public String getTooltip() {
        return tooltip;
    }

    /**
     * 获取指定部位的盔甲数据
     *
     * @param type 盔甲部位类型
     * @return 该部位的数据，如果未配置则返回 null
     */
    @Nullable
    public ArmorPartData getPartData(@NotNull ArmorItem.Type type) {
        return parts.get(type);
    }

    /**
     * 获取所有部位的盔甲数据
     */
    @NotNull
    public Map<ArmorItem.Type, ArmorPartData> getAllParts() {
        return parts;
    }

    /**
     * 获取套装效果定义（可能为 null，表示无套装效果）
     */
    @Nullable
    public ArmorSetEffectData getSetEffect() {
        return setEffect;
    }

    /**
     * 判断此套装是否有套装效果
     */
    public boolean hasSetEffect() {
        return setEffect != null;
    }

    @Override
    public String toString() {
        return "ArmorSuitIndex{" + "id=" + id + ", sort=" + sort + '}';
    }
}
