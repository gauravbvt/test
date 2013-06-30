// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.model;

import com.mindalliance.sb.model.SharingIssuePK;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect SharingIssuePK_Roo_Json {
    
    public String SharingIssuePK.toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
    
    public static SharingIssuePK SharingIssuePK.fromJsonToSharingIssuePK(String json) {
        return new JSONDeserializer<SharingIssuePK>().use(null, SharingIssuePK.class).deserialize(json);
    }
    
    public static String SharingIssuePK.toJsonArray(Collection<SharingIssuePK> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static Collection<SharingIssuePK> SharingIssuePK.fromJsonArrayToSharingIssuePKs(String json) {
        return new JSONDeserializer<List<SharingIssuePK>>().use(null, ArrayList.class).use("values", SharingIssuePK.class).deserialize(json);
    }
    
}
