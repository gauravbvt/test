// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.model;

import com.mindalliance.sb.model.IncidentCapabilityPK;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect IncidentCapabilityPK_Roo_Json {
    
    public String IncidentCapabilityPK.toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
    
    public static IncidentCapabilityPK IncidentCapabilityPK.fromJsonToIncidentCapabilityPK(String json) {
        return new JSONDeserializer<IncidentCapabilityPK>().use(null, IncidentCapabilityPK.class).deserialize(json);
    }
    
    public static String IncidentCapabilityPK.toJsonArray(Collection<IncidentCapabilityPK> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static Collection<IncidentCapabilityPK> IncidentCapabilityPK.fromJsonArrayToIncidentCapabilityPKs(String json) {
        return new JSONDeserializer<List<IncidentCapabilityPK>>().use(null, ArrayList.class).use("values", IncidentCapabilityPK.class).deserialize(json);
    }
    
}