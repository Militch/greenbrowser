package tech.xfs.xfschainexplorer.common.util;

public class UrlSafeB64 {
    public static String encode(String content){
        return content.replaceAll("\\+","*")
                .replaceAll("/","-")
                .replaceAll("=",".");
    }
    public static String decode(String content){
        return content.replaceAll("\\*","+")
                .replaceAll("-","/")
                .replaceAll("\\.","=");
    }
}
