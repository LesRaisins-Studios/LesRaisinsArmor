package me.xjqsh.lesraisinsarmor.item;

import com.github.mcmodderanchor.simplebedrockmodel.v1.client.model.BedrockArmorModel;
import com.github.mcmodderanchor.simplebedrockmodel.v1.client.renderer.GeoArmorRenderer;
import com.github.mcmodderanchor.simplebedrockmodel.v1.resource.BedrockModelResourceSet;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import me.xjqsh.lesraisinsarmor.LesRaisinsArmor;
import me.xjqsh.lesraisinsarmor.armor.AbstractArmorMaterial;
import me.xjqsh.lesraisinsarmor.armor.LrArmorMaterial;
import me.xjqsh.lesraisinsarmor.config.CommonConfig;
import me.xjqsh.lesraisinsarmor.resource.ArmorSuitIndex;
import me.xjqsh.lesraisinsarmor.resource.ArmorSuitManager;
import me.xjqsh.lesraisinsarmor.resource.data.ArmorPartData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class LrArmorItem extends ArmorItem {
    private final String suitIdf;
    private final @Nullable Supplier<MobEffect> suitEffect;
    private ArmorMaterial material;

    @Override
    public Rarity getRarity(ItemStack pStack) {
        return super.getRarity(pStack);
    }

    public LrArmorItem(String suitIdf, ArmorItem.Type slot, Item.Properties properties, @Nullable Supplier<MobEffect> suitEffect) {
        super(LrArmorMaterial.DEFAULT, slot, properties);
        this.suitIdf = suitIdf;
        this.suitEffect = suitEffect;
        this.material = LrArmorMaterial.DEFAULT;
    }

    public String getSuitIdf() {
        return suitIdf;
    }

    /**
     * 从 ArmorSuitManager 动态获取当前套装的索引
     * @return 套装索引，若数据未加载则返回 null
     */
    @Nullable
    public ArmorSuitIndex getSuitIndex() {
        return ArmorSuitManager.getInstance().getIndex(suitIdf);
    }

    /**
     * 获取当前部位的 ArmorPartData
     */
    @Nullable
    private ArmorPartData getPartData() {
        ArmorSuitIndex index = getSuitIndex();
        if (index != null) {
            return index.getPartData(this.type);
        }
        return null;
    }

    @NotNull
    @Override
    public ArmorMaterial getMaterial() {
        ArmorSuitIndex index = getSuitIndex();
        if (index != null) {
            // 确保 material 代理到当前物品
            if (!(material instanceof AbstractArmorMaterial)) {
                material = new AbstractArmorMaterial(this);
            }
            return material;
        }
        return super.getMaterial();
    }

    @Override
    public int getDefense() {
        ArmorPartData data = getPartData();
        return data != null ? data.getDefense() : 0;
    }

    @Override
    public float getToughness() {
        ArmorPartData data = getPartData();
        return data != null ? data.getToughness() : 0f;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return getMaxDurability();
    }

    public int getMaxDurability() {
        ArmorPartData data = getPartData();
        return data != null ? data.getMaxDurability() : 0;
    }

    public float getKnockbackResistance() {
        ArmorPartData data = getPartData();
        return data != null ? data.getKnockbackResistance() : 0f;
    }

    @Override
    public int getEnchantmentValue() {
        ArmorPartData data = getPartData();
        return data != null ? data.getEnchantmentValue() : 5;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return getMaxDamage(stack) > 0;
    }

    public boolean canBeDepleted() {
        return getMaxDurability() > 0;
    }

    @Override
    public @NotNull SoundEvent getEquipSound() {
        ArmorPartData data = getPartData();
        return data != null ? data.getEquipSound() : SoundEvents.ARMOR_EQUIP_LEATHER;
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack pToRepair, @NotNull ItemStack pRepair) {
        Ingredient ingredient = getIngredient();
        if (ingredient != null) {
            return ingredient.test(pRepair);
        }
        return false;
    }

    @Nullable
    public Ingredient getIngredient() {
        ArmorPartData data = getPartData();
        return data != null ? data.getRepairIngredient() : null;
    }

    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@NotNull EquipmentSlot pEquipmentSlot) {
        if (CommonConfig.enableArmorAttribute.get() && pEquipmentSlot == this.type.getSlot()) {
            ArmorPartData data = getPartData();
            if (data != null) {
                return data.getAttributes();
            }
        }
        return ImmutableMultimap.of();
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GeoArmorRenderer renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (this.renderer == null) {
                    BedrockArmorModel model = (BedrockArmorModel) BedrockModelResourceSet.getInstance()
                            .getModel(ResourceLocation.tryBuild("lrarmor", getSuitIdf() + ".geo"));
                    if (model == null) {
                        return original;
                    }
                    ResourceLocation texture = ResourceLocation.tryBuild("lrarmor",
                            "textures/item/armor/" + getSuitIdf() + ".png");
                    this.renderer = new GeoArmorRenderer(model, texture);
                }

                this.renderer.preparePose(livingEntity, itemStack, equipmentSlot, original);

                return this.renderer;
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level world, @Nonnull List<Component> list, @Nonnull TooltipFlag tooltipFlag) {
        if (Minecraft.getInstance().player != null) {
            Player player = Minecraft.getInstance().player;
            Component title = Component.translatable("tooltip.lramrmor.suit",
                    Component.translatable("suit.lrarmor." + this.suitIdf),
                    Component.literal(String.format("(%d/4)", getSuitCount(player, stack)))
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

    /**
     * 应用套装效果（无限时长，无倒计时显示）
     * 效果的自动移除由 SuitHandler 处理
     */
    public void applyEffect(Player player){
        if(suitEffect == null) return;
        MobEffect effect = suitEffect.get();
        if(effect != null && !player.hasEffect(effect)){
            player.addEffect(new MobEffectInstance(effect, -1));
        }
    }

    /**
     * 移除该套装的套装效果
     */
    public void removeEffect(Player player){
        if(suitEffect == null) return;
        MobEffect effect = suitEffect.get();
        if(effect != null && player.hasEffect(effect)){
            player.removeEffect(effect);
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
