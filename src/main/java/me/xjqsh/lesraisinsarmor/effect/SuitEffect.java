package me.xjqsh.lesraisinsarmor.effect;

import me.xjqsh.lesraisinsarmor.item.LrArmorItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

import javax.annotation.Nonnull;

public class SuitEffect extends Effect {
    private final String suitIdf;
    public SuitEffect(EffectType effectType, int color, String suitIdf) {
        super(effectType, color);
        this.suitIdf = suitIdf;
    }

    @Override
    public void applyEffectTick(@Nonnull LivingEntity entity, int amplifier){
        if(entity instanceof PlayerEntity){
            PlayerEntity player = (PlayerEntity) entity;
            if(LrArmorItem.getSuitCount(player, suitIdf) != 4){
                player.removeEffect(this);
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 10 == 0;
    }
}
