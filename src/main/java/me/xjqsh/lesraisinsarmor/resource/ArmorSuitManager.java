package me.xjqsh.lesraisinsarmor.resource;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import me.xjqsh.lesraisinsarmor.LesRaisinsArmor;
import me.xjqsh.lesraisinsarmor.resource.data.ArmorPartData;
import me.xjqsh.lesraisinsarmor.util.LocUtil;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ArmorItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * 套装数据的管理器，类似于 TACZ 的 CustomIndexManager
 * 负责从数据包加载套装 JSON 配置，创建和管理 ArmorSuitIndex 实例
 *
 * <p>在数据包加载/重载时刷新索引</p>
 */
public class ArmorSuitManager extends SimplePreparableReloadListener<Map<ResourceLocation, JsonElement>> {

    private static final Predicate<ResourceLocation> FILTER = (rl) -> rl.getPath().endsWith(".json");

    private static ArmorSuitManager INSTANCE;

    public static ArmorSuitManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ArmorSuitManager(null, "ArmorSuitManager");
        }
        return INSTANCE;
    }

    /**
     * 初始化单例实例
     * 在 CommonHandler 中调用，设置正确的 ArmorSuitType
     */
    public static void init(ArmorSuitType type) {
        INSTANCE = new ArmorSuitManager(type, "ArmorSuitManager(" + type.id() + ")");
    }

    private final ArmorSuitType type;
    private final Map<ResourceLocation, ArmorSuitIndex> indexes;
    private Map<ResourceLocation, String> networkCache;
    private RegistryAccess registryAccess;

    ArmorSuitManager(@Nullable ArmorSuitType type, @NotNull String marker) {
        this.type = type;
        this.indexes = new HashMap<>();
        this.networkCache = Collections.emptyMap();
    }

    public void setRegistryAccess(RegistryAccess registryAccess) {
        this.registryAccess = registryAccess;
    }

    /**
     * 获取用于网络传输的缓存数据
     */
    @NotNull
    public Map<ResourceLocation, String> getNetworkCache() {
        return networkCache;
    }

    @Override
    @NotNull
    public String getName() {
        return "lr_armor_suit_manager";
    }

    @Override
    @NotNull
    protected Map<ResourceLocation, JsonElement> prepare(@NotNull ResourceManager manager, @NotNull ProfilerFiller profiler) {
        ImmutableMap.Builder<ResourceLocation, JsonElement> builder = new ImmutableMap.Builder<>();
        ImmutableMap.Builder<ResourceLocation, String> cache = new ImmutableMap.Builder<>();

        // 扫描 data/<ns>/armor_suits/ 目录
        manager.listResources("armor_suits", FILTER).forEach((rl, resource) -> {
            try (var reader = resource.open()) {
                ResourceLocation configLocation = LocUtil.fromFile(rl, "armor_suits");
                String json = IOUtils.toString(reader, StandardCharsets.UTF_8);
                JsonElement element = type != null ? type.gson().fromJson(json, JsonElement.class) : null;
                if (element != null && configLocation != null) {
                    builder.put(configLocation, element);
                    cache.put(configLocation, json);
                }
            } catch (IOException | JsonSyntaxException | JsonIOException e) {
                LesRaisinsArmor.LOGGER.warn("Failed to load armor suit config {}!", rl, e);
            }
        });

        networkCache = cache.build();
        return builder.build();
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager manager, @NotNull ProfilerFiller profiler) {
        indexes.clear();

        if (type == null) {
            LesRaisinsArmor.LOGGER.warn("ArmorSuitType is null, skipping apply");
            return;
        }

        for (var entry : map.entrySet()) {
            ResourceLocation id = entry.getKey();
            JsonElement element = entry.getValue();
            try {
                ArmorSuitIndex index = type.factory().create(type.gson(), id, element, registryAccess);
                if (index != null) {
                    indexes.put(id, index);
                    LesRaisinsArmor.LOGGER.debug("Loaded armor suit: {}", id);
                }
            } catch (Exception e) {
                LesRaisinsArmor.LOGGER.warn("Failed to create armor suit index {}!", id, e);
            }
        }

        LesRaisinsArmor.LOGGER.info("Loaded {} armor suits", indexes.size());
    }

    /**
     * 根据套装 ID 查找 ArmorSuitIndex
     *
     * @param id 套装资源位置（如 lrarmor:attacker）
     * @return 对应的索引，如果未找到返回 null
     */
    @Nullable
    public ArmorSuitIndex getIndex(@NotNull ResourceLocation id) {
        return indexes.get(id);
    }

    /**
     * 根据套装 ID 字符串查找 ArmorSuitIndex
     *
     * @param suitIdf 套装标识符（如 "attacker"，命名空间默认 lrarmor）
     * @return 对应的索引，如果未找到返回 null
     */
    @Nullable
    public ArmorSuitIndex getIndex(@NotNull String suitIdf) {
        ResourceLocation id = ResourceLocation.tryParse(suitIdf);
        if (id == null) {
            id = ResourceLocation.tryBuild(LesRaisinsArmor.MOD_ID, suitIdf);
        } else if (id.getNamespace().equals("minecraft")) {
            // 如果命名空间是默认的 minecraft，说明原始字符串没有指定命名空间
            // 用 mod 的命名空间补全
            id = ResourceLocation.tryBuild(LesRaisinsArmor.MOD_ID, id.getPath());
        }
        return indexes.get(id);
    }

    /**
     * 获取所有已加载的套装索引（不可变视图）
     */
    @NotNull
    @UnmodifiableView
    public Map<ResourceLocation, ArmorSuitIndex> getAll() {
        return Collections.unmodifiableMap(indexes);
    }

    /**
     * 获取某个套装中指定部位的 ArmorPartData
     *
     * @param suitIdf 套装标识符
     * @param type    盔甲部位类型
     * @return 部位数据，未找到返回 null
     */
    @Nullable
    public ArmorPartData getPartData(@NotNull String suitIdf, @NotNull ArmorItem.Type type) {
        ArmorSuitIndex index = getIndex(suitIdf);
        return index != null ? index.getPartData(type) : null;
    }

    /**
     * 客户端从网络数据重建索引
     */
    @OnlyIn(Dist.CLIENT)
    public static void fromNetwork(Map<ResourceLocation, String> networkCache) {
        ArmorSuitManager manager = getInstance();
        if (manager.type == null) {
            LesRaisinsArmor.LOGGER.warn("ArmorSuitType is null, cannot load from network");
            return;
        }

        Map<ResourceLocation, ArmorSuitIndex> clientIndexes = new HashMap<>();
        for (var entry : networkCache.entrySet()) {
            try {
                JsonElement element = manager.type.gson().fromJson(entry.getValue(), JsonElement.class);
                ArmorSuitIndex index = manager.type.factory().create(
                        manager.type.gson(), entry.getKey(), element, null);
                if (index != null) {
                    clientIndexes.put(entry.getKey(), index);
                }
            } catch (JsonSyntaxException e) {
                LesRaisinsArmor.LOGGER.warn("Failed to load armor suit from network {}!", entry.getKey(), e);
            }
        }

        manager.indexes.clear();
        manager.indexes.putAll(clientIndexes);
        manager.networkCache = networkCache;
    }
}
