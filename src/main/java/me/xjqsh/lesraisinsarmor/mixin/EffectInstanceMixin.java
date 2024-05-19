package me.xjqsh.lesraisinsarmor.mixin;

import me.xjqsh.lesraisinsarmor.effect.SuitEffect;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin(EffectInstance.class)
public abstract class EffectInstanceMixin {
    @Shadow public abstract Effect getEffect();

    @Shadow @Final private Effect effect;

    @Inject(method = "isNoCounter", at = @At("HEAD"), cancellable = true)
    public void isNoCounter(CallbackInfoReturnable<Boolean> cir) {
        if(this.effect instanceof SuitEffect) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
