package me.zipestudio.blockdithering.yacl;

public final class YACLSearchState {

    private static volatile String query = "";

    private YACLSearchState() {}

    public static void setQuery(String value) {
        query = value == null ? "" : value;
    }

    public static boolean isSearchActive() {
        return !query.isEmpty();
    }
}
