package org.iot.dsa.dslink.swagger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSInt;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.DSValueType;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;
import org.iot.dsa.node.action.DSActionValues;
import org.iot.dsa.util.DSException;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.client.ApiResponse;

public class ApiAction extends DSAction {
    
    private ApiNode apiNode;
    private Method method;
    
    public ApiAction(ApiNode apiNode, Method method) {
        this.method = method;
        for (Parameter parameter: method.getParameters()) {
            System.out.println(parameter.getType() + "  " + parameter.getName());
            addParameter(parameter);
        }
        setResultType(ResultType.VALUES);
        addValueResult("StatusCode", DSValueType.NUMBER);
        addValueResult("Data", DSValueType.STRING);
    }
    
    @Override
    public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
        DSMap dsParameters = invocation.getParameters();
        
        Object[] args = new Object[method.getParameterCount()];
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < method.getParameterCount(); i++) {
            Parameter parameter = parameters[i];
            String name = parameter.getName();
            Class<?> type = parameter.getType();
            if (String.class.equals(type)) {
                args[i] = dsParameters.getString(name);
            } else if (Boolean.class.equals(type)) {
                args[i] = dsParameters.getBoolean(name);
            } else if (Integer.class.equals(type)) {
                args[i] = dsParameters.getLong(name);
            } else if (BigDecimal.class.equals(type)) {
                args[i] = BigDecimal.valueOf(dsParameters.getDouble(name));
            } else if (type.getName().startsWith("io.swagger.client.model")) {
                args[i] = constructDataParam(type, dsParameters);
            } else {
                DSException.throwRuntime(new RuntimeException("Unexpected parameter type"));
            }
        }
        
        try {
            ApiResponse<?> response = (ApiResponse<?>) method.invoke(apiNode.getApiInstance(), args);
            
            DSActionValues result = new DSActionValues(this);
            result.addResult(DSInt.valueOf(response.getStatusCode()));
            result.addResult(DSString.valueOf(response.getData()));
            return result;
            
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            DSException.throwRuntime(e);
            return null;
        }
    }
    
    private void addParameter(Parameter parameter) {
        String name = parameter.getName();
        Class<?> type = parameter.getType();
        if (String.class.equals(type)) {
            addParameter(name, DSValueType.STRING, null);
        } else if (Boolean.class.equals(type)) {
            addParameter(name, DSValueType.BOOL, null);
        } else if (Number.class.isAssignableFrom(type)) {
            addParameter(name, DSValueType.NUMBER, null);
        } else if (type.getName().startsWith("io.swagger.client.model")) {
            for (Method method: type.getMethods()) {
                if (method.isAnnotationPresent(ApiModelProperty.class)) {
                    String methName = method.getName();
                    if (!methName.startsWith("get")) {
                        DSException.throwRuntime(new RuntimeException("ApiModelProperty getter doesn't start with 'get': " + methName));
                    } else {
                        Class<?> propType = method.getReturnType();
                        if (String.class.equals(propType)) {
                            addParameter(methName.substring(3), DSValueType.STRING, null);
                        } else if (Number.class.isAssignableFrom(propType)) {
                            addParameter(methName.substring(3), DSValueType.NUMBER, null);
                        } else if (Boolean.class.equals(propType)) {
                            addParameter(methName.substring(3), DSValueType.BOOL, null);
                        }
                    }
                }
            }
        } else {
            DSException.throwRuntime(new RuntimeException("Unexpected parameter type"));
        }
    }
    
    private Object constructDataParam(Class<?> type, DSMap dsParameters) {
        try {
            Object instance = type.newInstance();
            for (Method method: type.getMethods()) {
                if (method.isAnnotationPresent(ApiModelProperty.class)) {
                    String methName = method.getName();
                    if (!methName.startsWith("get")) {
                        DSException.throwRuntime(new RuntimeException("ApiModelProperty getter doesn't start with 'get': " + methName));
                    } else {
                        Class<?> propType = method.getReturnType();
                        Method setter = type.getMethod(methName.replaceFirst("g", "s"), propType);
                        String name = methName.substring(3);
                        Object arg = null;
                        if (String.class.equals(propType)) {
                            arg = dsParameters.getString(name);
                        } else if (Boolean.class.equals(propType)) {
                            arg = dsParameters.getBoolean(name);
                        } else if (Integer.class.equals(propType)) {
                            arg = dsParameters.getLong(name);
                        } else if (BigDecimal.class.equals(propType)) {
                            arg = BigDecimal.valueOf(dsParameters.getDouble(name));
                        } else {
                            DSException.throwRuntime(new RuntimeException("Unexpected parameter type"));
                        }
                        setter.invoke(instance, arg);
                    }
                }
            }
            return instance;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            DSException.throwRuntime(e);
            return null;
        }
    }

}
