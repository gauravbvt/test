package com.mindalliance.channels.graph;

import java.util.List;
import java.util.ArrayList;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 21, 2008
 * Time: 10:45:02 AM
 */
public class DOTAttribute {

    String name;
    String value;

    DOTAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append('=');
        sb.append("\"");
        sb.append(value);
        sb.append("\"");
        return sb.toString();        
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<DOTAttribute> asList() {
        List<DOTAttribute> list = new ArrayList<DOTAttribute>();
        list.add(this);
        return list;
    }

    static public List<DOTAttribute> emptyList() {
        return new ArrayList<DOTAttribute>();
    }

}
