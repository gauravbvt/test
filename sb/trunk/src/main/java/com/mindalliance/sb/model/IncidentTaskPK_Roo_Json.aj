// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.model;

import com.mindalliance.sb.model.IncidentTaskPK;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect IncidentTaskPK_Roo_Json {
    
    public String IncidentTaskPK.toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
    
    public static IncidentTaskPK IncidentTaskPK.fromJsonToIncidentTaskPK(String json) {
        return new JSONDeserializer<IncidentTaskPK>().use(null, IncidentTaskPK.class).deserialize(json);
    }
    
    public static String IncidentTaskPK.toJsonArray(Collection<IncidentTaskPK> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static Collection<IncidentTaskPK> IncidentTaskPK.fromJsonArrayToIncidentTaskPKs(String json) {
        return new JSONDeserializer<List<IncidentTaskPK>>().use(null, ArrayList.class).use("values", IncidentTaskPK.class).deserialize(json);
    }
    
}