package me.xjqsh.lesraisinsarmor.init;

import me.xjqsh.lesraisinsarmor.LesRaisinsArmor;
import me.xjqsh.lesraisinsarmor.effect.SuitEffect;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class ModEffects {
    public static final Effect TOUGH_EFFECT = new SuitEffect(EffectType.BENEFICIAL,0x000000, "attacker")
            .addAttributeModifier(Attributes.ARMOR_TOUGHNESS,"62E0C0F3-D94E-3821-BF1C-B5F17CB7F971",
                    15, Operation.ADDITION)
            .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE,"0BCE518E-9D05-CB4A-C435-C68BEEE57650",
                    0.25d, Operation.ADDITION);

    public static final Effect LIGHT_LEG_EFFECT = new SuitEffect(EffectType.BENEFICIAL,0x000000, "scout")
            .addAttributeModifier(Attributes.MOVEMENT_SPEED,"FC303D2A-7C7F-FFAB-2B4E-A5EF09BB2AF1",
                    0.025, Operation.ADDITION);
    public static final Effect HEAVY_EFFECT = new SuitEffect(EffectType.BENEFICIAL,0x000000, "defender")
            .addAttributeModifier(Attributes.MOVEMENT_SPEED,"37DAE46A-CB52-2E2C-71FC-4AE7348B0B8D",
                    -0.02, Operation.ADDITION);
    public static final DeferredRegister<Effect> REGISTER = DeferredRegister.create(ForgeRegistries.POTIONS, LesRaisinsArmor.MOD_ID);
    public static final RegistryObject<Effect> TOUGH = REGISTER.register("tough",()-> TOUGH_EFFECT);
    public static final RegistryObject<Effect> LIGHT_LEG = REGISTER.register("light_leg",()-> LIGHT_LEG_EFFECT);
    public static final RegistryObject<Effect> HEAVY_ARMOR = REGISTER.register("heavy_armor",()-> HEAVY_EFFECT);

}
