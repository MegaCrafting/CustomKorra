package com.projectkorra.projectkorra;

import java.util.Arrays;
import org.bukkit.ChatColor;

import com.projectkorra.projectkorra.SubElement;
import com.projectkorra.projectkorra.airbending.AirMethods;
import com.projectkorra.projectkorra.chiblocking.ChiMethods;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.firebending.FireMethods;
import com.projectkorra.projectkorra.scarecrow.ScareMethods;
import com.projectkorra.projectkorra.snowman.SnowMethods;
import com.projectkorra.projectkorra.sunshine.SunshineMethods;
import com.projectkorra.projectkorra.waterbending.WaterMethods;

public enum Element {
    Air(AirMethods.getAirColor(), AirMethods.getAirSubColor()),
    Water(WaterMethods.getWaterColor(), WaterMethods.getWaterSubColor()),
    Earth(EarthMethods.getEarthColor(), EarthMethods.getEarthSubColor()),
    Fire(FireMethods.getFireColor(), FireMethods.getFireSubColor()),
    Chi(ChiMethods.getChiColor(), ChiMethods.getChiColor()),
    Snowman(SnowMethods.getSnowColor(), SnowMethods.getSnowColor()),
    Scarecrow(ScareMethods.getScareColor(), ScareMethods.getScareColor()),
    Sunshine(SunshineMethods.getSunshineColor(), SunshineMethods.getSunshineColor());
    
    private ChatColor color;
    private ChatColor subcolor;

    private Element(ChatColor mainColor, ChatColor subColor) {
        this.color = mainColor;
        this.subcolor = subColor;
    }

    public ChatColor getChatColor() {
        return this.color;
    }

    public ChatColor getSubColor() {
        return this.subcolor;
    }

    public SubElement[] getSubElements() {
        if (this == Air) {
            return new SubElement[]{SubElement.Flight, SubElement.SpiritualProjection};
        }
        if (this == Water) {
            return new SubElement[]{SubElement.Bloodbending, SubElement.Icebending, SubElement.Plantbending, SubElement.Healing};
        }
        if (this == Fire) {
            return new SubElement[]{SubElement.Combustion, SubElement.Lightning};
        }
        if (this == Earth) {
            return new SubElement[]{SubElement.Sandbending, SubElement.Metalbending, SubElement.Lavabending};
        }
        return new SubElement[0];
    }

    public static Element getType(String string) {
        Element[] arrelement = Element.values();
        int n = arrelement.length;
        int n2 = 0;
        while (n2 < n) {
            Element element = arrelement[n2];
            if (element.toString().equalsIgnoreCase(string)) {
                return element;
            }
            ++n2;
        }
        return null;
    }

    public static Element getType(int index) {
        if (index == -1) {
            return null;
        }
        return Arrays.asList(Element.values()).get(index);
    }

    public static Element getFromChatColor(ChatColor color) {
        Element[] arrelement = Element.values();
        int n = arrelement.length;
        int n2 = 0;
        while (n2 < n) {
            Element element = arrelement[n2];
            if (element.getChatColor().equals((Object)color) || element.getSubColor().equals((Object)color)) {
                return element;
            }
            ++n2;
        }
        return null;
    }
}

