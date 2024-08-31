package me.xjqsh.lesraisinsarmor.resource;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import me.xjqsh.lesraisinsarmor.LesRaisinsArmor;
import me.xjqsh.lesraisinsarmor.item.LrArmorItem;
import me.xjqsh.lesraisinsarmor.resource.data.ArmorData;
import me.xjqsh.lesraisinsarmor.resource.data.ArmorPartData;
import me.xjqsh.lesraisinsarmor.util.LocUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ArmorItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ArmorDataManager extends SimplePreparableReloadListener<Map<ResourceLocation, ArmorData>> {
    private static final Predicate<ResourceLocation> FILTER = (rl) -> rl.getPath().endsWith(".json");
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .registerTypeAdapter(ArmorData.class, new ArmorData.Deserializer())
            .setPrettyPrinting()
            .create();
    private static ArmorDataManager INSTANCE;
    public static ArmorDataManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ArmorDataManager();
        }
        return INSTANCE;
    }
    private Map<ResourceLocation, String> networkCache;

    public Map<ResourceLocation, String> getNetworkCache() {
        return networkCache;
    }

    @NotNull
    @Override
    public String getName() {
        return "lr_armor_data";
    }

    @NotNull
    @Override
    protected Map<ResourceLocation, ArmorData> prepare(@NotNull ResourceManager manager, @NotNull ProfilerFiller pProfiler) {
        ImmutableMap.Builder<ResourceLocation, ArmorData> builder = new ImmutableMap.Builder<>();
        ImmutableMap.Builder<ResourceLocation, String> cache = new ImmutableMap.Builder<>();
        manager.listResources("armor_data", FILTER).forEach((rl, resource) -> {
            try(var reader = resource.open()) {
                ResourceLocation configLocation = LocUtil.fromFile(rl, "armor_data");
                String json = IOUtils.toString(reader, StandardCharsets.UTF_8);
                var config = GSON.fromJson(json, ArmorData.class);
                if (config != null && configLocation != null) {
                    builder.put(configLocation, config);
                    cache.put(configLocation, json);
                }
            }  catch (IOException | JsonSyntaxException | JsonIOException e) {
                LesRaisinsArmor.LOGGER.warn("Failed to load armor config {}!", rl, e);
            }
        });
        networkCache = cache.build();
        return builder.build();
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, ArmorData> map, @NotNull ResourceManager manager, @NotNull ProfilerFiller pProfiler) {
        for(var entry : map.entrySet()) {
            for (ArmorItem.Type type : ArmorItem.Type.values()) {
                ResourceLocation rl = ResourceLocation.tryBuild(entry.getKey().getNamespace(),
                        entry.getKey().getPath() + "_" + type.getName());
                if (rl == null) continue;
                if(ForgeRegistries.ITEMS.getValue(rl) instanceof LrArmorItem item) {
                    item.setArmorData(new ArmorDataSupplier(entry.getValue()));
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void fromNetwork(Map<ResourceLocation, String> networkCache) {
        for (var entry : networkCache.entrySet()) {
            try {
                ArmorData armorData = GSON.fromJson(entry.getValue(), ArmorData.class);
                if (armorData == null) continue;
                for (ArmorItem.Type type : ArmorItem.Type.values()) {
                    ResourceLocation rl = ResourceLocation.tryBuild(entry.getKey().getNamespace(),
                            entry.getKey().getPath() + "_" + type.getName());
                    if (rl == null) continue;
                    if(ForgeRegistries.ITEMS.getValue(rl) instanceof LrArmorItem item) {
                        item.setArmorData(new ArmorDataSupplier(armorData));
                    }
                }
            } catch (JsonSyntaxException e) {
                LesRaisinsArmor.LOGGER.warn("Failed to load armor config from network {}!", entry.getKey(), e);
            }
        }
    }

    /**
     * 这个类只是用来注明ArmorData应该由数据包读取而不是手动构造
     */
    public static class ArmorDataSupplier implements Supplier<ArmorData> {
        private final ArmorData armorData;
        private ArmorDataSupplier(@NotNull ArmorData armorData) {
            this.armorData = armorData;
        }
        @Override
        public ArmorData get() {
            return armorData;
        }
    }

}
