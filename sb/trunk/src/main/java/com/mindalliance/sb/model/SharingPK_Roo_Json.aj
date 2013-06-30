// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.model;

import com.mindalliance.sb.model.SharingPK;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect SharingPK_Roo_Json {
    
    public String SharingPK.toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
    
    public static SharingPK SharingPK.fromJsonToSharingPK(String json) {
        return new JSONDeserializer<SharingPK>().use(null, SharingPK.class).deserialize(json);
    }
    
    public static String SharingPK.toJsonArray(Collection<SharingPK> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static Collection<SharingPK> SharingPK.fromJsonArrayToSharingPKs(String json) {
        return new JSONDeserializer<List<SharingPK>>().use(null, ArrayList.class).use("values", SharingPK.class).deserialize(json);
    }
    
}
