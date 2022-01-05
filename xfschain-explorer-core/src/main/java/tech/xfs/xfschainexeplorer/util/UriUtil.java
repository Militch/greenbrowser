package tech.xfs.xfschainexeplorer.util;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;


public class UriUtil {
    private static final Pattern QUERY_PARAM_PATTERN = Pattern.compile("([^&=]+)(=?)([^&]+)?");
    private static String decode(final String encoded) {
        return Optional.ofNullable(encoded)
                .map(e -> URLDecoder.decode(e, StandardCharsets.UTF_8))
                .orElse(null);
    }
    public static List<Map.Entry<String, String>> parseQueryParams(String query){
        return Pattern.compile("&")
                .splitAsStream(query)
                .map(s -> Arrays.copyOf(s.split("=", 2), 2))
                .map(o -> Map.entry(decode(o[0]), decode(o[1])))
                .collect(toList());

    }

    public static Map<String,String> n(String query){
        if (query == null || query.length() == 0){
            return new HashMap<>();
        }
        Matcher matcher = QUERY_PARAM_PATTERN.matcher(query);
        Map<String,String> map = new HashMap<>();
        while(matcher.find()) {
            String name = matcher.group(1);
            String eq = matcher.group(2);
            String value = matcher.group(3);
            map.put(name, value != null ? value : "");
        }
        return map;
    }
}
