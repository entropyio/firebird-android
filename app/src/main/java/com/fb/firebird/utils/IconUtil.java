package com.fb.firebird.utils;

import com.fb.firebird.R;

import java.util.HashMap;
import java.util.Map;

public class IconUtil {
    private static final Map<String, Integer> iconMap = new HashMap<String, Integer>() {{
        put("icon", R.drawable.icon);

        put("btc", R.drawable.btc);
        put("eth", R.drawable.eth);
        put("eos", R.drawable.eos);
        put("bch", R.drawable.bch);
        put("etc", R.drawable.etc);
        put("hc", R.drawable.hc);
        put("ht", R.drawable.ht);
        put("ont", R.drawable.ont);
        put("steem", R.drawable.steem);
        put("usdt", R.drawable.usdt);
        put("xrp", R.drawable.xrp);
    }};

    public static int getIcon(String icon) {
        if (iconMap.containsKey(icon)) {
            return iconMap.get(icon);
        }

        return iconMap.get("icon");
    }
}
