// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.surveygizmo;

import com.mindalliance.sb.surveygizmo.SurveyOption;
import com.mindalliance.sb.surveygizmo.SurveyQuestion;
import java.util.List;
import java.util.Map;

privileged aspect SurveyQuestion_Roo_JavaBean {
    
    public int SurveyQuestion.getId() {
        return this.id;
    }
    
    public void SurveyQuestion.setId(int id) {
        this.id = id;
    }
    
    public String SurveyQuestion.get_type() {
        return this._type;
    }
    
    public void SurveyQuestion.set_type(String _type) {
        this._type = _type;
    }
    
    public String SurveyQuestion.get_subtype() {
        return this._subtype;
    }
    
    public void SurveyQuestion.set_subtype(String _subtype) {
        this._subtype = _subtype;
    }
    
    public Map<String, String> SurveyQuestion.getTitle() {
        return this.title;
    }
    
    public void SurveyQuestion.setTitle(Map<String, String> title) {
        this.title = title;
    }
    
    public String SurveyQuestion.getShortname() {
        return this.shortname;
    }
    
    public void SurveyQuestion.setShortname(String shortname) {
        this.shortname = shortname;
    }
    
    public String SurveyQuestion.getVarname() {
        return this.varname;
    }
    
    public void SurveyQuestion.setVarname(String varname) {
        this.varname = varname;
    }
    
    public List<Object> SurveyQuestion.getDescription() {
        return this.description;
    }
    
    public void SurveyQuestion.setDescription(List<Object> description) {
        this.description = description;
    }
    
    public boolean SurveyQuestion.isHas_showhide_deps() {
        return this.has_showhide_deps;
    }
    
    public void SurveyQuestion.setHas_showhide_deps(boolean has_showhide_deps) {
        this.has_showhide_deps = has_showhide_deps;
    }
    
    public boolean SurveyQuestion.isComment() {
        return this.comment;
    }
    
    public void SurveyQuestion.setComment(boolean comment) {
        this.comment = comment;
    }
    
    public Map<String, Object> SurveyQuestion.getProperties() {
        return this.properties;
    }
    
    public void SurveyQuestion.setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
    
    public List<SurveyOption> SurveyQuestion.getOptions() {
        return this.options;
    }
    
    public void SurveyQuestion.setOptions(List<SurveyOption> options) {
        this.options = options;
    }
    
    public List<Integer> SurveyQuestion.getSub_question_skus() {
        return this.sub_question_skus;
    }
    
    public void SurveyQuestion.setSub_question_skus(List<Integer> sub_question_skus) {
        this.sub_question_skus = sub_question_skus;
    }
    
}