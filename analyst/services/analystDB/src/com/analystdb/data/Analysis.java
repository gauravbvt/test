
package com.analystdb.data;

import java.util.HashSet;
import java.util.Set;


/**
 *  analystDB.Analysis
 *  02/10/2013 19:29:36
 * 
 */
public class Analysis {

    private Long id;
    private Project project;
    private String description;
    private String name;
    private Integer version;
    private Set<com.analystdb.data.Plan> plans = new HashSet<com.analystdb.data.Plan>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Set<com.analystdb.data.Plan> getPlans() {
        return plans;
    }

    public void setPlans(Set<com.analystdb.data.Plan> plans) {
        this.plans = plans;
    }

}
