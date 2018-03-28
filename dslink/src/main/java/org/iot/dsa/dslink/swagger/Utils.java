package org.iot.dsa.dslink.swagger;

import java.util.HashSet;
import java.util.Set;
// API IMPORTS GO HERE

public class Utils {
    
    private static Set<Class<?>> apiClasses;
    
    public static Set<Class<?>> getApiClasses() {
        if (apiClasses == null) {
            apiClasses = new HashSet<Class<?>>();
            // API CLASSES GO HERE
        }
        return apiClasses;
    }

}
