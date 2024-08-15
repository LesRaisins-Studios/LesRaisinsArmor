package me.xjqsh.lesraisinsarmor.resource;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import me.xjqsh.lesraisinsarmor.LesRaisinsArmor;
import me.xjqsh.lesraisinsarmor.resource.pojo.ArmorDataPOJO;
import me.xjqsh.lesraisinsarmor.util.LocUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;
import java.util.function.Predicate;

public class ArmorDataManager extends SimplePreparableReloadListener<Map<ResourceLocation, ArmorDataPOJO>> {
    private static final Predicate<ResourceLocation> FILTER = (rl) -> rl.getPath().endsWith(".json");
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .setPrettyPrinting()
            .create();
    private static ArmorDataManager INSTANCE;
    public static ArmorDataManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ArmorDataManager();
        }
        return INSTANCE;
    }

    @NotNull
    @Override
    protected Map<ResourceLocation, ArmorDataPOJO> prepare(@NotNull ResourceManager manager, @NotNull ProfilerFiller pProfiler) {
        ImmutableMap.Builder<ResourceLocation, ArmorDataPOJO> builder = new ImmutableMap.Builder<>();
        manager.listResources("armor_data", FILTER).forEach((rl, resource) -> {
            try(var reader = resource.openAsReader()) {
                ResourceLocation configLocation = LocUtil.fromFile(rl);
                var config = GSON.fromJson(reader, ArmorDataPOJO.class);
                if (config != null && configLocation != null) {
                    builder.put(configLocation, config);
                }
            }  catch (IOException | JsonSyntaxException | JsonIOException e) {
                LesRaisinsArmor.LOGGER.warn("Failed to load armor config {}!", rl, e);
            }
        });
        return builder.build();
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, ArmorDataPOJO> pObject, @NotNull ResourceManager manager, @NotNull ProfilerFiller pProfiler) {

    }
    @NotNull
    @Override
    public String getName() {
        return "lr_armor_data";
    }
}
