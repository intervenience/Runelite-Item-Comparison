package com.intervenience.comparison;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import lombok.extern.slf4j.Slf4j;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.ui.components.materialtabs.MaterialTab;
import net.runelite.client.ui.components.materialtabs.MaterialTabGroup;
import net.runelite.client.util.ImageUtil;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class ComparisonPanel extends PluginPanel {

    protected LinkedHashMap<String, ImageIcon> imageIcons = new LinkedHashMap<String, ImageIcon> () {{
        put ("Atk Stab", new ImageIcon(ImageUtil.getResourceStreamFromClass(ComparisonPlugin.class, "ASTAB_ICON.png")));
        put ("Atk Slash", new ImageIcon(ImageUtil.getResourceStreamFromClass(ComparisonPlugin.class, "ASLASH_ICON.png")));
        put ("Atk Crush", new ImageIcon(ImageUtil.getResourceStreamFromClass(ComparisonPlugin.class, "ACRUSH_ICON.png")));
        put ("Atk Magic", new ImageIcon(ImageUtil.getResourceStreamFromClass(ComparisonPlugin.class, "AMAGE_ICON.png")));
        put ("Atk Range", new ImageIcon(ImageUtil.getResourceStreamFromClass(ComparisonPlugin.class, "ARANGE_ICON.png")));
        put ("Def Stab", new ImageIcon(ImageUtil.getResourceStreamFromClass(ComparisonPlugin.class, "DSTAB_ICON.png")));
        put ("Def Slash", new ImageIcon(ImageUtil.getResourceStreamFromClass(ComparisonPlugin.class, "DSLASH_ICON.png")));
        put ("Def Crush", new ImageIcon(ImageUtil.getResourceStreamFromClass(ComparisonPlugin.class, "DCRUSH_ICON.png")));
        put ("Def Magic", new ImageIcon(ImageUtil.getResourceStreamFromClass(ComparisonPlugin.class, "DMAGE_ICON.png")));
        put ("Def Range", new ImageIcon(ImageUtil.getResourceStreamFromClass(ComparisonPlugin.class, "DRANGE_ICON.png")));
        put ("Strength", new ImageIcon(ImageUtil.getResourceStreamFromClass(ComparisonPlugin.class, "STR_ICON.png")));
        put ("Ranged Str", new ImageIcon(ImageUtil.getResourceStreamFromClass(ComparisonPlugin.class, "RSTR_ICON.png")));
        put ("Magic Dmg", new ImageIcon(ImageUtil.getResourceStreamFromClass(ComparisonPlugin.class, "MDMG_ICON.png")));
        put ("Prayer", new ImageIcon(ImageUtil.getResourceStreamFromClass(ComparisonPlugin.class, "PRAYICON.png")));
    }};

    protected static Color defaultColour = new Color (128,128, 128);
    protected static final Color weakerColour = new Color (181,0, 9);
    protected static final Color strongerColour = new Color (14, 170,0);

    protected ArrayList<MaterialTab> leftMaterialTabs = new ArrayList<MaterialTab>();
    protected ArrayList<MaterialTab> rightMaterialTabs = new ArrayList<MaterialTab>();

    protected ComparisonPlugin comparisonPlugin;

    protected IconTextField search1, search2;
    protected MaterialTabGroup tabGroup;

    @Inject
    public ComparisonPanel (ComparisonPlugin comparisonPlugin) throws Exception {
        super ();
        System.out.println("Search for this");
        this.comparisonPlugin = comparisonPlugin;

        setBorder (new EmptyBorder(10,10,10,10));
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setLayout (new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;

        JLabel title = new JLabel("Compare Items");
        add (title, c);
        c.gridy++;

        search1 = new IconTextField();
        initialiseSearchBars(search1, 0);
        add (search1, c);
        c.gridy++;
        search2 = new IconTextField();
        initialiseSearchBars(search2, 1);

        add (search2, c);
        c.gridy++;

        tabGroup = new MaterialTabGroup();
        tabGroup.setLayout(new GridLayout(0,3,5,2));

        for (Map.Entry<String, ImageIcon> icon : imageIcons.entrySet()) {
            MaterialTab leftTab = new MaterialTab ("-", tabGroup, null);
            leftTab.removeMouseListener(leftTab.getMouseListeners()[1]);
            leftTab.removeMouseListener(leftTab.getMouseListeners()[0]);

            leftMaterialTabs.add (leftTab);
            tabGroup.addTab (leftTab);

            MaterialTab tab = new MaterialTab(icon.getValue(), tabGroup, null);
            tab.setPreferredSize(new Dimension(0, 25));
            tab.setToolTipText(icon.getKey());
            tabGroup.addTab (tab);

            MaterialTab rightTab = new MaterialTab ("-", tabGroup, null);
            rightTab.removeMouseListener(rightTab.getMouseListeners()[1]);
            rightTab.removeMouseListener(rightTab.getMouseListeners()[0]);
            rightMaterialTabs.add (rightTab);
            tabGroup.addTab (rightTab);
        }

        add (tabGroup, c);
        c.gridy++;
    }

    private void initialiseSearchBars (IconTextField textField, int id) {
        textField.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH - 20, 30));
        textField.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        textField.setMinimumSize(new Dimension (0,30));
        textField.addActionListener(e -> lookup (textField, id));
        textField.addClearListener(clear (textField));
        textField.setIcon(IconTextField.Icon.SEARCH);
    }

    private Runnable clear (IconTextField textField) {
        textField.setIcon(IconTextField.Icon.SEARCH);
        return null;
    }

    private void lookup (IconTextField textField, int id) {
        textField.setIcon(IconTextField.Icon.LOADING);
        comparisonPlugin.searchForItem(textField.getText(), id);
    }

    public void setLabelsText (int textFieldId) {
        switch (textFieldId) {
            case 0:
                search1.setIcon (IconTextField.Icon.SEARCH);
                search1.setText(comparisonPlugin.items.get(0).itemName);
                //TODO: Figure out how to iterate through this
                leftMaterialTabs.get(0).setText(String.valueOf(comparisonPlugin.items.get(0).stabAtkBonus));
                leftMaterialTabs.get(1).setText(String.valueOf(comparisonPlugin.items.get(0).slashAtkBonus));
                leftMaterialTabs.get(2).setText(String.valueOf(comparisonPlugin.items.get(0).crushAtkBonus));
                leftMaterialTabs.get(3).setText(String.valueOf(comparisonPlugin.items.get(0).mageAtkBonus));
                leftMaterialTabs.get(4).setText(String.valueOf(comparisonPlugin.items.get(0).rangeAtkBonus));
                leftMaterialTabs.get(5).setText(String.valueOf(comparisonPlugin.items.get(0).stabDefBonus));
                leftMaterialTabs.get(6).setText(String.valueOf(comparisonPlugin.items.get(0).slashDefBonus));
                leftMaterialTabs.get(7).setText(String.valueOf(comparisonPlugin.items.get(0).crushDefBonus));
                leftMaterialTabs.get(8).setText(String.valueOf(comparisonPlugin.items.get(0).mageDefBonus));
                leftMaterialTabs.get(9).setText(String.valueOf(comparisonPlugin.items.get(0).rangeDefBonus));
                leftMaterialTabs.get(10).setText(String.valueOf(comparisonPlugin.items.get(0).strengthBonus));
                leftMaterialTabs.get(11).setText(String.valueOf(comparisonPlugin.items.get(0).rangedStrengthBonus));
                leftMaterialTabs.get(12).setText(String.valueOf(comparisonPlugin.items.get(0).mageStrengthBonus));
                leftMaterialTabs.get(13).setText(String.valueOf(comparisonPlugin.items.get(0).prayerBonus));
                break;
            case 1:
                search2.setIcon (IconTextField.Icon.SEARCH);
                search2.setText(comparisonPlugin.items.get(1).itemName);
                rightMaterialTabs.get(0).setText(String.valueOf(comparisonPlugin.items.get(1).stabAtkBonus));
                rightMaterialTabs.get(1).setText(String.valueOf(comparisonPlugin.items.get(1).slashAtkBonus));
                rightMaterialTabs.get(2).setText(String.valueOf(comparisonPlugin.items.get(1).crushAtkBonus));
                rightMaterialTabs.get(3).setText(String.valueOf(comparisonPlugin.items.get(1).mageAtkBonus));
                rightMaterialTabs.get(4).setText(String.valueOf(comparisonPlugin.items.get(1).rangeAtkBonus));
                rightMaterialTabs.get(5).setText(String.valueOf(comparisonPlugin.items.get(1).stabDefBonus));
                rightMaterialTabs.get(6).setText(String.valueOf(comparisonPlugin.items.get(1).slashDefBonus));
                rightMaterialTabs.get(7).setText(String.valueOf(comparisonPlugin.items.get(1).crushDefBonus));
                rightMaterialTabs.get(8).setText(String.valueOf(comparisonPlugin.items.get(1).mageDefBonus));
                rightMaterialTabs.get(9).setText(String.valueOf(comparisonPlugin.items.get(1).rangeDefBonus));
                rightMaterialTabs.get(10).setText(String.valueOf(comparisonPlugin.items.get(1).strengthBonus));
                rightMaterialTabs.get(11).setText(String.valueOf(comparisonPlugin.items.get(1).rangedStrengthBonus));
                rightMaterialTabs.get(12).setText(String.valueOf(comparisonPlugin.items.get(1).mageStrengthBonus));
                rightMaterialTabs.get(13).setText(String.valueOf(comparisonPlugin.items.get(1).prayerBonus));
                break;
        }
        compareStats ();
    }

    protected void compareStats () {
        boolean leftIsDefaultOverride = leftMaterialTabs.get(0).getText() == "-";
        boolean rightIsDefaultOverride = rightMaterialTabs.get(0).getText() == "-";

        for (int i = 0; i < leftMaterialTabs.size(); i++) {
            int left = leftIsDefaultOverride == true ? 0 : Integer.valueOf(leftMaterialTabs.get(i).getText());
            int right = rightIsDefaultOverride == true ? 0 : Integer.valueOf(rightMaterialTabs.get(i).getText());

            if (left > right) {
                leftMaterialTabs.get(i).setForeground(strongerColour);
                rightMaterialTabs.get(i).setForeground(weakerColour);
            } else if (left < right) {
                leftMaterialTabs.get(i).setForeground(weakerColour);
                rightMaterialTabs.get(i).setForeground(strongerColour);
            } else {
                leftMaterialTabs.get(i).setForeground(defaultColour);
                rightMaterialTabs.get(i).setForeground(defaultColour);
            }
        }
    }

    protected void raiseError (int textFieldId) {
        switch (textFieldId) {
            case 1:
                search1.setIcon(IconTextField.Icon.ERROR);
                break;
            case 2:
                search2.setIcon(IconTextField.Icon.ERROR);
                break;
        }
    }

    @Override
    public void onActivate () {
        super.onActivate();
    }

}
