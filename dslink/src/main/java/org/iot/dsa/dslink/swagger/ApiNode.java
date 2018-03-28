package org.iot.dsa.dslink.swagger;

import java.lang.reflect.Method;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.util.DSException;
import io.swagger.client.ApiResponse;

public class ApiNode extends DSNode {
    
    private Class<?> apiClass;
    
    public ApiNode() {
    }
    
    public ApiNode(Class<?> apiClass) {
        this.apiClass = apiClass;
    }
    
    @Override
    protected void onStarted() {
        if (apiClass == null) {
            try {
                apiClass = Class.forName("io.swagger.client.api." + getName() + "Api");
            } catch (ClassNotFoundException e) {
                DSException.throwRuntime(e);
            }
        }
        for (Method method: apiClass.getMethods()) {
            if (ApiResponse.class.equals(method.getReturnType())) {
                System.out.println(apiClass.getName() + "   " + method.getName());
                String name = method.getName();
                String suff = "WithHttpInfo";
                if (name.endsWith(suff)) {
                    name = name.substring(0, name.length() - suff.length());
                    put(name, new ApiAction(this, method));
                } else {
                    DSException.throwRuntime(new RuntimeException("Unexpected method name: " + name));
                }
            }
        }
    }
    
    public Object getApiInstance() {
        //TODO fix this
        try {
            return apiClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            DSException.throwRuntime(e);
            return null;
        }
    }
    
    

}
