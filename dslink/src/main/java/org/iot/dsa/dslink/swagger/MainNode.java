package org.iot.dsa.dslink.swagger;

import org.iot.dsa.dslink.DSMainNode;
import org.iot.dsa.util.DSException;

/**
 * The root and only node of this link.
 *
 * @author Aaron Hansen
 */
public class MainNode extends DSMainNode {

    
    public MainNode() {
    }

    
    /**
     * Defines the permanent children of this node type, their existence is guaranteed in all
     * instances.  This is only ever called once per, type per process.
     */
    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        for (Class<?> apiClass: Utils.getApiClasses()) {
            String name = apiClass.getSimpleName();
            if (name.endsWith("Api")) {
                name = name.substring(0, name.length() - 3);
                declareDefault(name, new ApiNode(apiClass)).setTransient(true);
            } else {
                DSException.throwRuntime(new RuntimeException("Api class " + name + " doesn't end with 'Api'"));
            }
        }
    }

}
