package me.xjqsh.lesraisinsarmor.init;

import me.xjqsh.lesraisinsarmor.LesRaisinsArmor;
import me.xjqsh.lesraisinsarmor.effect.SuitEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class ModEffects {
    public static final MobEffect TOUGH_EFFECT = new SuitEffect(MobEffectCategory.BENEFICIAL,0x000000, "attacker")
            .addAttributeModifier(Attributes.ARMOR_TOUGHNESS,"62E0C0F3-D94E-3821-BF1C-B5F17CB7F971",
                    15, Operation.ADDITION)
            .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE,"0BCE518E-9D05-CB4A-C435-C68BEEE57650",
                    0.25d, Operation.ADDITION);
    public static final MobEffect LIGHT_LEG_EFFECT = new SuitEffect(MobEffectCategory.BENEFICIAL,0x000000, "scout")
            .addAttributeModifier(Attributes.MOVEMENT_SPEED,"FC303D2A-7C7F-FFAB-2B4E-A5EF09BB2AF1",
                    0.025, Operation.ADDITION);
    public static final MobEffect HEAVY_EFFECT = new SuitEffect(MobEffectCategory.BENEFICIAL,0x000000, "defender")
            .addAttributeModifier(Attributes.MOVEMENT_SPEED,"37DAE46A-CB52-2E2C-71FC-4AE7348B0B8D",
                    -0.02, Operation.ADDITION);
    public static final MobEffect RESCUE_EFFECT = new SuitEffect(MobEffectCategory.BENEFICIAL,0x000000, "medical");
    public static final DeferredRegister<MobEffect> REGISTER = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, LesRaisinsArmor.MOD_ID);
    public static final RegistryObject<MobEffect> TOUGH = REGISTER.register("tough",()-> TOUGH_EFFECT);
    public static final RegistryObject<MobEffect> LIGHT_LEG = REGISTER.register("light_leg",()-> LIGHT_LEG_EFFECT);
    public static final RegistryObject<MobEffect> HEAVY_ARMOR = REGISTER.register("heavy_armor",()-> HEAVY_EFFECT);
    public static final RegistryObject<MobEffect> RESCUE = REGISTER.register("rescue",()-> RESCUE_EFFECT);
    public static final RegistryObject<MobEffect> RESCUE_COOLDOWN = REGISTER.register("rescue_cooldown",()->
            new MobEffect(MobEffectCategory.HARMFUL,0x000000){});
}
