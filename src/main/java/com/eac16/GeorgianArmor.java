package com.eac16;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;

public enum GeorgianArmor implements ArmorMaterial {
    MAIL("eac16:georgian_mail", new int[] {2,5,6,2}, 12, 0.0f),
    REINFORCED("eac16:georgian_reinforced", new int[] {2,6,7,3}, 10, 1.0f);
    private static final int[] BASE = {13,15,16,11};
    private final String id;
    private final int[] protection;
    private final int enchant;
    private final float tough;
    GeorgianArmor(String id, int[] protection, int enchant, float tough) {
        this.id=id;this.protection=protection;this.enchant=enchant;this.tough=tough;
    }
    private int idx(ArmorItem.Type t) {return switch(t) {case BOOTS -> 0;case LEGGINGS -> 1;case CHESTPLATE -> 2;case HELMET -> 3; default -> 0;};}
    @Override public int getDurabilityForType(ArmorItem.Type t) {return BASE[idx(t)]*17;}
    @Override public int getDefenseForType(ArmorItem.Type t) {return protection[idx(t)];}
    @Override public int getEnchantmentValue() {return enchant;}
    @Override public SoundEvent getEquipSound() {return SoundEvents.ARMOR_EQUIP_CHAIN;}
    @Override public Ingredient getRepairIngredient() {return Ingredient.of(Items.IRON_INGOT);}
    @Override public String getName() {return id;}
    @Override public float getToughness() {return tough;}
    @Override public float getKnockbackResistance() {return 0.0f;}
}
