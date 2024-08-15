package me.xjqsh.lesraisinsarmor.client.resource;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import me.xjqsh.lesraisinsarmor.LesRaisinsArmor;
import me.xjqsh.lesraisinsarmor.client.resource.armor.ArmorRenderConfig;
import me.xjqsh.lesraisinsarmor.util.LocUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class ArmorRenderConfigManager implements ResourceManagerReloadListener {
    private static final Predicate<ResourceLocation> FILTER = (rl) -> rl.getPath().endsWith(".json");
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .setPrettyPrinting()
            .create();
    private static ArmorRenderConfigManager INSTANCE;
    public static ArmorRenderConfigManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ArmorRenderConfigManager();
        }
        return INSTANCE;
    }

    private Map<ResourceLocation, ArmorRenderConfig> configMap = new HashMap<>();

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager manager) {
        ImmutableMap.Builder<ResourceLocation, ArmorRenderConfig> builder = new ImmutableMap.Builder<>();
        manager.listResources("armor_display", FILTER).forEach((rl,resource) -> {
            try(var reader = resource.openAsReader()) {
                ResourceLocation configLocation = LocUtil.fromFile(rl);
                var config = GSON.fromJson(reader, ArmorRenderConfig.class);
                if (config != null && configLocation != null) {
                    builder.put(configLocation, config);
                }
            }  catch (IOException | JsonSyntaxException | JsonIOException e) {
                LesRaisinsArmor.LOGGER.warn("Failed to load armor display config!", e);
            }
        });
        configMap = builder.build();
    }

    public ArmorRenderConfig getConfig(ResourceLocation rl) {
        return configMap.get(rl);
    }
}
