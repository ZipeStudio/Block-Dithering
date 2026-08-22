package me.zipestudio.blockdithering.yacl;

import net.minecraft.client.Minecraft;

public final class YACLSearchState {

    private static volatile String query = "";

    private YACLSearchState() {}

    public static void setQuery(String value) {
        query = value == null ? "" : value;
    }

    public static boolean isSearchActive() {
        return !query.isEmpty();
    }

    public static boolean isLeafyScreen() {
        //? if >=26.2 {
        return Minecraft.getInstance().gui.screen() instanceof LeafyYaclScreen;
        //?} else {
        /*return Minecraft.getInstance().screen instanceof LeafyYaclScreen;
        *///?}
    }
}
