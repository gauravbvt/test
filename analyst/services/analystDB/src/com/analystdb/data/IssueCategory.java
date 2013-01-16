
package com.analystdb.data;

import java.util.HashSet;
import java.util.Set;


/**
 *  analystDB.IssueCategory
 *  01/06/2013 20:47:04
 * 
 */
public class IssueCategory {

    private Long id;
    private Project project;
    private String name;
    private Integer version;
    private Set<com.analystdb.data.IssueIssueCategory> issueIssueCategories = new HashSet<com.analystdb.data.IssueIssueCategory>();

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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Set<com.analystdb.data.IssueIssueCategory> getIssueIssueCategories() {
        return issueIssueCategories;
    }

    public void setIssueIssueCategories(Set<com.analystdb.data.IssueIssueCategory> issueIssueCategories) {
        this.issueIssueCategories = issueIssueCategories;
    }

}
