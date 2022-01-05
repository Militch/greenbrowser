package com.esiran.greenadmin.common.jsonrpci;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class MethodParams extends HashMap<String, Object> {
    public MethodParams(){

    }
    public void addAttribute(String attributeName, @Nullable Object attributeValue){
        Assert.notNull(attributeName, "Attribute name must not be null");
        this.put(attributeName, attributeValue);
    }
    public void addAllAttributes(@Nullable Map<String, ?> attributes){
        if (attributes != null) {
            this.putAll(attributes);
        }
    }
}
