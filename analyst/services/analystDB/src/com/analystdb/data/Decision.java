
package com.analystdb.data;

import java.util.HashSet;
import java.util.Set;


/**
 *  analystDB.Decision
 *  03/26/2013 11:09:00
 * 
 */
public class Decision {

    private Integer id;
    private Interview interview;
    private String name;
    private String description;
    private Byte importance;
    private Set<com.analystdb.data.DecisionFlow> decisionFlows = new HashSet<com.analystdb.data.DecisionFlow>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Interview getInterview() {
        return interview;
    }

    public void setInterview(Interview interview) {
        this.interview = interview;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Byte getImportance() {
        return importance;
    }

    public void setImportance(Byte importance) {
        this.importance = importance;
    }

    public Set<com.analystdb.data.DecisionFlow> getDecisionFlows() {
        return decisionFlows;
    }

    public void setDecisionFlows(Set<com.analystdb.data.DecisionFlow> decisionFlows) {
        this.decisionFlows = decisionFlows;
    }

}
