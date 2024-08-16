package me.xjqsh.lesraisinsarmor.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import me.xjqsh.lesraisinsarmor.armor.LrArmorMaterial;
import me.xjqsh.lesraisinsarmor.client.renderer.BedrockArmorRenderer;
import me.xjqsh.lesraisinsarmor.config.CommonConfig;
import me.xjqsh.lesraisinsarmor.resource.ArmorDataManager.ArmorDataSupplier;
import me.xjqsh.lesraisinsarmor.resource.data.ArmorData;
import me.xjqsh.lesraisinsarmor.resource.data.ArmorPartData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class LrArmorItem extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final String suitIdf;
    private final @Nullable Supplier<MobEffect> suitEffect;
    private ArmorData armorData;

    public LrArmorItem(String suitIdf, ArmorItem.Type slot, Item.Properties properties, @Nullable Supplier<MobEffect> suitEffect) {
        super(LrArmorMaterial.DEFAULT, slot, properties);
        this.suitIdf = suitIdf;
        this.suitEffect = suitEffect;
    }
    public String getSuitIdf() {
        return suitIdf;
    }

    public void setArmorData(ArmorDataSupplier supplier){
        this.armorData = supplier.get();
    }

    @Override
    public int getDefense() {
        return ArmorData.getByType(this.armorData, this.type, 0, ArmorPartData::getDefense);
    }

    @Override
    public float getToughness() {
        return ArmorData.getByType(this.armorData, this.type, 0, ArmorPartData::getToughness);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return ArmorData.getByType(this.armorData, this.type, 128, ArmorPartData::getMaxDurability);
    }

    @Override
    public int getEnchantmentValue() {
        return ArmorData.getByType(this.armorData, this.type, 5, ArmorPartData::getEnchantmentValue);
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack pToRepair, @NotNull ItemStack pRepair) {
        var ingredient = ArmorData.getByType(this.armorData, this.type, null, ArmorPartData::getRepairIngredient);
        if (ingredient != null) {
            ingredient.test(pRepair);
        }
        return false;
    }

    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@NotNull EquipmentSlot pEquipmentSlot) {
        return CommonConfig.enableArmorAttribute.get() ?
                ArmorData.getByType(this.armorData, this.type, ImmutableMultimap.of(), ArmorPartData::getAttributes) :
                ImmutableMultimap.of();
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (this.renderer == null)
                    this.renderer = new BedrockArmorRenderer(getSuitIdf());

                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);

                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, 20, state -> {
            state.setAnimation(DefaultAnimations.IDLE);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level world, @Nonnull List<Component> list, @Nonnull TooltipFlag tooltipFlag) {
        if (Minecraft.getInstance().player != null) {
            Player player = Minecraft.getInstance().player;
            Component title = Component.translatable("tooltip.lramrmor.suit",
                    Component.translatable("suit.lrarmor." + this.suitIdf),
                    Component.literal(String.format("(%d/4)",getSuitCount(player, stack)))
            ).withStyle(ChatFormatting.GRAY);
            list.add(title);

            boolean flag = true;
            LrArmorItem item = (LrArmorItem) stack.getItem();
            ItemStack equipItem = player.getItemBySlot(item.getEquipmentSlot());
            if(!stack.equals(equipItem)){
                flag = false;
            }

            for(ArmorItem.Type slot : ArmorItem.Type.values()){
                MutableComponent part = Component.translatable("item.lrarmor." + this.suitIdf + "_" + slot.getName());

                if(flag && isPartEquipped(player, slot)) {
                    part.withStyle(ChatFormatting.GREEN);
                }else {
                    part.withStyle(ChatFormatting.GRAY);
                }

                list.add(part);
            }

        }
    }
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "lrarmor:textures/item/armor/" + this.suitIdf + ".png";
    }

    public void applyEffect(Player player){
        if(suitEffect == null) return;
        MobEffect effect = suitEffect.get();
        if(effect != null){
            player.addEffect(new MobEffectInstance(effect, 250));
        }
    }

    public boolean isPartEquipped(Player player, ArmorItem.Type slot){
        ItemStack equipItem = player.getItemBySlot(slot.getSlot());
        if(equipItem.getItem() instanceof LrArmorItem item){
            return item.getSuitIdf().equals(this.suitIdf);
        }
        return false;
    }

    public int getSuitCount(Player player, @Nonnull ItemStack stack){
        if(!(stack.getItem() instanceof LrArmorItem item)) return 0;

        ItemStack equipItem = player.getItemBySlot(item.getEquipmentSlot());
        if(!stack.equals(equipItem)){
            return 0;
        }

        return getSuitCount(player);
    }

    public int getSuitCount(Player player){
        return getSuitCount(player, this.suitIdf);
    }

    public static int getSuitCount(Player player, String suitIdf){
        int cnt = 0;

        for(ArmorItem.Type slot : ArmorItem.Type.values()){
            ItemStack stack1 = player.getItemBySlot(slot.getSlot());
            if(stack1.getItem() instanceof LrArmorItem item1){
                if(item1.getSuitIdf().equals(suitIdf)){
                    cnt++;
                }
            }
        }
        return cnt;
    }
}