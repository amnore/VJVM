package vjvm.utils;

import lombok.val;

import java.util.regex.Pattern;

public class ClassPathUtil {
    public static String findJavaPath() {
        var p = Pattern.compile("(\\d+\\.\\d+)\\.\\d+_\\d+");
        var m = p.matcher(System.getProperty("java.version"));
        assert m.find();
        assert Double.parseDouble(m.group(1)) <= 1.81;
        return System.getProperty("sun.boot.class.path");
    }
}
