package com.mcwcapsule.VJVM.utils;

import lombok.val;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class ClassPathUtil {
    public static String findJavaPath() {
        val p = Pattern.compile("(\\d+\\.\\d+)\\.\\d+_\\d+");
        val m = p.matcher(System.getProperty("java.version"));
        assert m.find();
        assert Double.parseDouble(m.group(1)) <= 1.81;

        // find java path
        // Oracle jdk not tested
        // only linux supported
        try {
            val r = Runtime.getRuntime();
            val po = r.exec("whereis java");
            assert po.waitFor() == 0;
            val reader = new BufferedReader(new InputStreamReader(po.getInputStream()));
            val pathp = Pattern.compile("((?:\\w|-|/)+jvm(?:\\w|-|/)+)/bin/java");
            val l = reader.readLine();
            val pathm = pathp.matcher(l);
            if (!pathm.find())
                throw new Error(l);
            System.err.println(pathm.group(1));
            return pathm.group(1) + "/jre/lib/rt.jar";
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}
