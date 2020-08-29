package com.intervenience.comparison;

public class ComparisonItem {

    public String itemName;
    public int stabAtkBonus, slashAtkBonus, crushAtkBonus, mageAtkBonus, rangeAtkBonus;
    public int stabDefBonus, slashDefBonus, crushDefBonus, mageDefBonus, rangeDefBonus;

    public int strengthBonus, rangedStrengthBonus, mageStrengthBonus, prayerBonus;

    public ComparisonItem () { }

    public ComparisonItem (String itemName, int stabAtkBonus, int slashAtkBonus, int crushAtkBonus, int mageAtkBonus, int rangeAtkBonus, int stabDefBonus, int slashDefBonus, int crushDefBonus, int mageDefBonus, int rangeDefBonus, int strengthBonus, int rangedStrengthBonus, int mageStrengthBonus, int prayerBonus) {
        this.itemName = itemName;
        this.stabAtkBonus = stabAtkBonus;
        this.slashAtkBonus = slashAtkBonus;
        this.crushAtkBonus = crushAtkBonus;
        this.mageAtkBonus = mageAtkBonus;
        this.rangeAtkBonus = rangeAtkBonus;
        this.stabDefBonus = stabDefBonus;
        this.slashDefBonus = slashDefBonus;
        this.crushDefBonus = crushDefBonus;
        this.mageDefBonus = mageDefBonus;
        this.rangeDefBonus = rangeDefBonus;
        this.strengthBonus = strengthBonus;
        this.rangedStrengthBonus = rangedStrengthBonus;
        this.mageStrengthBonus = mageStrengthBonus;
        this.prayerBonus = prayerBonus;
    }

}
