package me.xjqsh.lesraisinsarmor.item;

import com.google.common.collect.Multimap;
import me.xjqsh.lesraisinsarmor.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
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
import java.util.function.Supplier;

public class LrArmorItem extends GeoArmorItem implements IAnimatable {
    protected static final AnimationBuilder IDLE_ANIM = new AnimationBuilder().addAnimation("misc.idle");
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private final String suitIdf;
    private boolean hideArm = true;
    private @Nullable Supplier<Effect> suitEffect;
    public LrArmorItem(String suitIdf, ArmorMaterial armorMaterial, EquipmentSlotType slot, Properties properties, @Nullable Supplier<Effect> suitEffect) {
        super(armorMaterial, slot, properties);
        this.suitIdf = suitIdf;
        this.suitEffect = suitEffect;
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
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> list, @Nonnull ITooltipFlag tooltipFlag) {
        if (Minecraft.getInstance().player != null) {
            PlayerEntity player = Minecraft.getInstance().player;
            ITextComponent title = new TranslationTextComponent("tooltip.lramrmor.suit",
                    new TranslationTextComponent("suit.lrarmor." + this.suitIdf),
                    new StringTextComponent(String.format("(%d/4)",getSuitCount(player, stack)))
            ).withStyle(TextFormatting.GRAY);
            list.add(title);

            boolean flag = true;
            LrArmorItem item = (LrArmorItem) stack.getItem();
            ItemStack equipItem = player.getItemBySlot(item.getSlot());
            if(!stack.equals(equipItem)){
                flag = false;
            }

            for(EquipmentSlotType slot : ModItems.armorSlots){
                TranslationTextComponent part = new TranslationTextComponent("item.lrarmor." + this.suitIdf + "_" + slot.getName());

                if(flag && isPartEquipped(player, slot)) {
                    part.withStyle(TextFormatting.GREEN);
                }else {
                    part.withStyle(TextFormatting.GRAY);
                }

                list.add(part);
            }

        }
    }

    public void applyEffect(PlayerEntity player){
        if(suitEffect == null) return;
        Effect effect = suitEffect.get();
        if(effect != null){
            player.addEffect(new EffectInstance(effect, 250));
        }
    }

    public boolean isPartEquipped(PlayerEntity player, EquipmentSlotType slot){
        ItemStack equipItem = player.getItemBySlot(slot);
        if(equipItem.getItem() instanceof LrArmorItem){
            LrArmorItem item = (LrArmorItem) equipItem.getItem();
            return item.getSuitIdf().equals(this.suitIdf);
        }
        return false;
    }

    public int getSuitCount(PlayerEntity player, @Nonnull ItemStack stack){
        if(!(stack.getItem() instanceof LrArmorItem)) return 0;

        LrArmorItem item = (LrArmorItem) stack.getItem();
        ItemStack equipItem = player.getItemBySlot(item.getSlot());
        if(!stack.equals(equipItem)){
            return 0;
        }

        return getSuitCount(player);
    }

    public int getSuitCount(PlayerEntity player){
        return getSuitCount(player, this.suitIdf);
    }

    public static int getSuitCount(PlayerEntity player, String suitIdf){
        int cnt = 0;

        for(EquipmentSlotType slot : ModItems.armorSlots){
            ItemStack stack1 = player.getItemBySlot(slot);
            if(stack1.getItem() instanceof LrArmorItem){
                LrArmorItem item1 = (LrArmorItem) stack1.getItem();
                if(item1.getSuitIdf().equals(suitIdf)){
                    cnt++;
                }
            }
        }
        return cnt;
    }
}