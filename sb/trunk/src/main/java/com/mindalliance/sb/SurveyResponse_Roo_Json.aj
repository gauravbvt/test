// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb;

import com.mindalliance.sb.SurveyResponse;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect SurveyResponse_Roo_Json {
    
    public String SurveyResponse.toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
    
    public static SurveyResponse SurveyResponse.fromJsonToSurveyResponse(String json) {
        return new JSONDeserializer<SurveyResponse>().use(null, SurveyResponse.class).deserialize(json);
    }
    
    public static String SurveyResponse.toJsonArray(Collection<SurveyResponse> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static Collection<SurveyResponse> SurveyResponse.fromJsonArrayToSurveyResponses(String json) {
        return new JSONDeserializer<List<SurveyResponse>>().use(null, ArrayList.class).use("values", SurveyResponse.class).deserialize(json);
    }
    
}
