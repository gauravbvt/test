
package com.analystdb.data;

import java.util.HashSet;
import java.util.Set;


/**
 *  analystDB.Resource
 *  01/06/2013 20:47:04
 * 
 */
public class Resource {

    private Long id;
    private Project project;
    private String name;
    private String description;
    private Integer version;
    private Set<com.analystdb.data.Interview> interviews = new HashSet<com.analystdb.data.Interview>();
    private Set<com.analystdb.data.ResourceAttribute> resourceAttributes = new HashSet<com.analystdb.data.ResourceAttribute>();

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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Set<com.analystdb.data.Interview> getInterviews() {
        return interviews;
    }

    public void setInterviews(Set<com.analystdb.data.Interview> interviews) {
        this.interviews = interviews;
    }

    public Set<com.analystdb.data.ResourceAttribute> getResourceAttributes() {
        return resourceAttributes;
    }

    public void setResourceAttributes(Set<com.analystdb.data.ResourceAttribute> resourceAttributes) {
        this.resourceAttributes = resourceAttributes;
    }

}
