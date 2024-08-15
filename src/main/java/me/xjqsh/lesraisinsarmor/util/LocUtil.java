package me.xjqsh.lesraisinsarmor.util;

import net.minecraft.resources.ResourceLocation;

public class LocUtil {
    public static ResourceLocation fromFile(ResourceLocation rl) {
        return ResourceLocation.tryBuild(rl.getNamespace(), rl.getPath().substring(14).replace(".json", ""));
    }
}
