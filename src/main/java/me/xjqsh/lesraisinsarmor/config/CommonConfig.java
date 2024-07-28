package me.xjqsh.lesraisinsarmor.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
    public static ForgeConfigSpec.BooleanValue enableArmorSetEffect;
    public static ForgeConfigSpec.BooleanValue enableArmorAttribute;
    public static ForgeConfigSpec init() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("general");
        enableArmorSetEffect = builder.comment("Enable armor set effect").define("enableArmorSetEffect", true);
        enableArmorAttribute = builder.comment("Enable armor attribute").define("enableArmorAttribute", true);
        builder.pop();

        return builder.build();
    }
}
