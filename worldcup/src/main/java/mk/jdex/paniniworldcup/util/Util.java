package mk.jdex.paniniworldcup.util;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;


public class Util {

    private static boolean hasField(Class<?> klass, String fieldName) {
        try {
            klass.getDeclaredField(fieldName);
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }


    public static int getStringResourceByName(Context context, String name) {
        return getResourceByName(context, name, "string");
    }

    public static int getDrawabaleResourceByName(Context context, String name) {
        return getResourceByName(context, name, "drawable");
    }

    private static int getResourceByName(Context context, String name, String defType) {
        if (name != null) {
            return context.getResources().getIdentifier(name, defType, context.getPackageName());
        }
        return 0;
    }

    public static void printMemoryInfo() {
//            LOGD("MemoryInfo", "-----");
//            LOGD("MemoryInfo", "Heap: " + ((float) Runtime.getRuntime().totalMemory() / 1024 / 1024) + " MB");
//            LOGD("MemoryInfo", "Free: " + ((float) Runtime.getRuntime().freeMemory() / 1024 / 1024) + " MB");
//            LOGD("MemoryInfo", "MaxHeap: " + ((float) Runtime.getRuntime().maxMemory() / 1024 / 1024) + " MB");
//            LOGD("MemoryInfo", "-----");
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean hasJellyBeanMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    public static boolean hasJellyBeanMR2() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
