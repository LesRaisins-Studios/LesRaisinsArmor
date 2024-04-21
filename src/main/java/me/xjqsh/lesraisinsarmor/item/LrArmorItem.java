package me.xjqsh.lesraisinsarmor.item;

import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.item.GeoArmorItem;
import software.bernie.geckolib3.util.GeckoLibUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class LrArmorItem extends GeoArmorItem implements IAnimatable {
    protected static final AnimationBuilder IDLE_ANIM = new AnimationBuilder().addAnimation("misc.idle");
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private final String suitIdf;
    private boolean hideArm = true;
    public LrArmorItem(String suitIdf, ArmorMaterial armorMaterial, EquipmentSlotType slot, Properties properties) {
        super(armorMaterial, slot, properties);
        this.suitIdf = suitIdf;
    }
    public String getSuitIdf() {
        return suitIdf;
    }
    public boolean isHideArm(){
        return this.hideArm;
    }

    public void setHideArm(boolean hideArm) {
        this.hideArm = hideArm;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        return super.getAttributeModifiers(slot, stack);
    }

    private <T extends LrArmorItem> PlayState idleAnimController(final AnimationEvent<T> event) {
        event.getController().setAnimation(IDLE_ANIM);
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "Idle Controller",
                0, this::idleAnimController));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable World world,
                                @Nonnull List<ITextComponent> list, @Nonnull ITooltipFlag flag) {
        if (Minecraft.getInstance().player != null) {
            list.add(new StringTextComponent(String.format("Suit: %s (%s/4)", this.suitIdf, getSuitCount(Minecraft.getInstance().player))));
        }
    }

    public int getSuitCount(PlayerEntity player){
        int cnt = 0;
        for(ItemStack stack : player.getArmorSlots()){
            if(stack.getItem() instanceof LrArmorItem){
                LrArmorItem item = (LrArmorItem) stack.getItem();
                if(item.getSuitIdf().equals(this.suitIdf)){
                    cnt++;
                }
            }
        }
        return cnt;
    }
}