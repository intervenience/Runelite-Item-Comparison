package com.intervenience.comparison;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Binder;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.swing.*;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.ClientPluginToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.util.ImageUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@PluginDescriptor(
        name = "Comparison",
        description = "Compare two items stats",
        tags = {"compare", "comparison"}
)
@Slf4j
public class ComparisonPlugin extends Plugin {

    /*
    redirect example: https://oldschool.runescape.wiki/api.php?action=parse&format=jsonfm&formatversion=2&prop=wikitext&page=Firecape
    working example: https://oldschool.runescape.wiki/api.php?action=parse&format=jsonfm&formatversion=2&prop=wikitext&page=Fire_cape
    on-error example: https://oldschool.runescape.wiki/api.php?action=parse&format=jsonfm&formatversion=2&prop=wikitext&page=invalid
     */

    //json format returns 1 line, jsonfm format returns a webpage with pretty json, which we don't want.
    protected static final String parseSearch =
            "https://oldschool.runescape.wiki/api.php?action=parse&format=json&formatversion=2&prop=wikitext&page=";

    @Inject
    @Nullable
    private Client client;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private ClientPluginToolbar clientPluginToolbar;

    private ComparisonPanel panel;

    public List<ComparisonItem> items = new ArrayList<ComparisonItem>();

    public List<String> searchStrings = Arrays.asList (
        "astab = ",
        "aslash = ",
        "acrush = ",
        "amagic = ",
        "arange = ",
        "dstab = ",
        "dslash = ",
        "dcrush = ",
        "dmagic = ",
        "drange = ",
        "|str = ",
        "rstr = ",
        "mdmg = ",
        "prayer = ");

    private NavigationButton navButton;

    @Override
    protected void startUp () {
        try {
            panel = new ComparisonPanel(this);
        } catch (Exception e) {
            System.out.println (e);
        }

        items.add (new ComparisonItem());
        items.add (new ComparisonItem());

        BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "ICON.png");

        /*//Temporary until icon is made
        Graphics g;
        BufferedImage i = new BufferedImage(64,64, BufferedImage.TYPE_INT_RGB);
        g = i.createGraphics();
        g.setColor(Color.white);
        g.fillRect (0,0,100,100);*/
        navButton = NavigationButton.builder()
                .tooltip("Item Comparison")
                .priority(6)
                .panel(panel)
                .icon (icon)
                .build();
        clientToolbar.addNavigation(navButton);
        //System.out.println("Finished startup of comparison plugin");
    }

    protected Runnable searchForItem (String name, int id) {
        Runnable runnable = () -> {
            if (name.length() > 0) {
                try {
                    //Find the correct page based on the search term, then query again for the wikitext.
                    URL url = new URL(String.format("%s%s", parseSearch, name.replaceAll("\\s+", "_")));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    InputStream is = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        try {
                            JsonParser parser = new JsonParser();
                            JsonObject json = (JsonObject) parser.parse (line);
                            // returns null if this does not exist.
                            if (json.get ("error") == null) {
                                JsonObject subJson = (JsonObject) parser.parse (json.get ("parse").toString());
                                String wikitext = subJson.get ("wikitext").toString();
                                if (wikitext.startsWith("\"#REDIRECT")) {
                                    searchForItem(wikitext.replace ("\"#REDIRECT [[", "").replace ("]]\"", ""), id);
                                } else {
                                    //try to find the bonuses
                                    String title = subJson.get ("title").toString().replace ("\"", "");
                                    //System.out.println(wikitext);

                                    try {
                                        ArrayList<Integer> temp = new ArrayList<Integer> ();

                                        for (int i = 0; i < searchStrings.size(); i++) {
                                            String searchTerm = searchStrings.get(i);
                                            boolean trim = false;
                                            System.out.println ("Search term = " + searchTerm);

                                            //pages that contain multiple degradation's (barrows/blowpipe/etc), will return -1 here
                                            if (trim = wikitext.indexOf (searchStrings.get(i)) == -1) {
                                                //iterate through numbers ^ while the index != -1
                                                //compare the resulting number against the previous highest until we are out
                                                int iterationHighestValue = 0;
                                                int iteration = 1;
                                                int foundIndex = wikitext.indexOf((searchStrings.get(i).replace (" = ", String.format ("%d = ", iteration))));
                                                //while we receive a valid result...
                                                while (foundIndex != -1) {
                                                    //this should be something like astab1 = ###|
                                                    String s = wikitext.substring(foundIndex, foundIndex + searchTerm.length() + 5);

                                                    //remove every character that isn't a digit or -, remove the first number (so it doesn't look like "1    ###")
                                                    //trim to remove leading/trailing whitespace, and convert to int
                                                    //if this value exceeds our previous highest recording value, replace it
                                                    int val = Integer.valueOf(s.replaceAll ("[^\\d -]", "").substring(1).trim());

                                                    //do abs value in case of negatives
                                                    iterationHighestValue = Math.abs (iterationHighestValue) > Math.abs (val) ? iterationHighestValue : val;

                                                    //increment our test value
                                                    iteration++;

                                                    //attempt to find another value
                                                    foundIndex = wikitext.indexOf((searchStrings.get(i).replace (" = ", String.format ("%d = ", iteration))));
                                                }
                                                //System.out.println ("Value we should be getting is " + iterationHighestValue);

                                                temp.add (iterationHighestValue);
                                            } else {
                                                int index = wikitext.indexOf (searchTerm);

                                                int value = 0;
                                                String tempString = wikitext.substring(index, index + searchTerm.length() + 5).replaceAll ("[^\\d -]", "");
                                                //System.out.println ("temp string = " + tempString);
                                                //System.out.println ("final string = " + tempString);
                                                value = Integer.valueOf(tempString.trim());
                                                //System.out.println ("value = " + value);
                                                temp.add (value);
                                            }
                                        }
                                        ComparisonItem item = new ComparisonItem(title,
                                                temp.get(0), temp.get(1),temp.get(2),temp.get(3),temp.get(4),temp.get(5),
                                                temp.get(6),temp.get(7),temp.get(8),temp.get(9),temp.get(10), temp.get(11),
                                                temp.get(12), temp.get(13));
                                        items.set(id, item);
                                        SwingUtilities.invokeLater (
                                            new Runnable () {
                                                public void run () {
                                                    panel.setLabelsText(id);
                                                }
                                            });

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        System.out.println ("Error finding values.\n" + e.getMessage());
                                    }
                                }
                            } else {
                                //show error message
                                System.out.println("Encountered an error reading the requested wiki page");
                                panel.raiseError(1);
                            }
                        } catch (Exception e) {
                            System.out.println (e);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("search for me " + e);
                }
            }
        };

        //We actually start the above here, so we don't block user inputs
        Thread t = new Thread (runnable, "");
        t.start();

        return null;
    }

    @Override
    protected void shutDown () {
        clientToolbar.removeNavigation(navButton);
    }

    @Override
    public void configure(Binder binder) {
        super.configure(binder);
    }

}
