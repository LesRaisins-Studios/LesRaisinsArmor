package me.xjqsh.lesraisinsarmor.effect;

import me.xjqsh.lesraisinsarmor.item.LrArmorItem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class SuitEffect extends MobEffect {
    /** 所有已创建的套装效果实例（用于遍历移除） */
    private static final Set<MobEffect> ALL_SUIT_EFFECTS = Collections.synchronizedSet(new HashSet<>());

    public static Set<MobEffect> getAllSuitEffects() {
        return Collections.unmodifiableSet(ALL_SUIT_EFFECTS);
    }

    private final String suitIdf;

    public SuitEffect(MobEffectCategory effectType, int color, String suitIdf) {
        super(effectType, color);
        this.suitIdf = suitIdf;
        ALL_SUIT_EFFECTS.add(this);
    }

    public String getSuitIdf() {
        return suitIdf;
    }

    @Override
    public void applyEffectTick(@Nonnull LivingEntity entity, int amplifier) {
        if (entity instanceof Player player) {
            if (LrArmorItem.getSuitCount(player, suitIdf) != 4) {
                player.removeEffect(this);
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 10 == 0;
    }

    /**
     * 注册客户端效果渲染扩展
     * <p>套装效果显示为无限时长，无倒计时</p>
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(@Nonnull Consumer<IClientMobEffectExtensions> consumer) {
        consumer.accept(new IClientMobEffectExtensions() {
            @Override
            public boolean isVisibleInInventory(@Nonnull MobEffectInstance instance) {
                return true;
            }

            @Override
            public boolean isVisibleInGui(@Nonnull MobEffectInstance instance) {
                return true;
            }

            /**
             * 在物品栏效果界面中渲染描述文本，替代默认的倒计时
             */
            @Override
            public boolean renderInventoryText(@Nonnull MobEffectInstance instance,
                                               @Nonnull EffectRenderingInventoryScreen<?> screen,
                                               @Nonnull GuiGraphics guiGraphics,
                                               int x, int y, int blitOffset) {
                Component text = Component.translatable("effect.lrarmor.suit_set",
                        Component.translatable("suit.lrarmor." + suitIdf));
                guiGraphics.drawString(screen.getMinecraft().font, text,
                        x + 10 + 18, y + 6, 0xAAAAAA);
                return true;
            }
        });
    }
}
