
package com.analystdb.data;

import java.util.HashSet;
import java.util.Set;


/**
 *  analystDB.Issue
 *  03/26/2013 11:09:00
 * 
 */
public class Issue {

    private Long id;
    private Project project;
    private Integer sequence;
    private String name;
    private String description;
    private Integer version;
    private Set<com.analystdb.data.IssueApproach> issueApproachs = new HashSet<com.analystdb.data.IssueApproach>();
    private Set<com.analystdb.data.IssueComment> issueComments = new HashSet<com.analystdb.data.IssueComment>();
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

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
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

    public Set<com.analystdb.data.IssueApproach> getIssueApproachs() {
        return issueApproachs;
    }

    public void setIssueApproachs(Set<com.analystdb.data.IssueApproach> issueApproachs) {
        this.issueApproachs = issueApproachs;
    }

    public Set<com.analystdb.data.IssueComment> getIssueComments() {
        return issueComments;
    }

    public void setIssueComments(Set<com.analystdb.data.IssueComment> issueComments) {
        this.issueComments = issueComments;
    }

    public Set<com.analystdb.data.IssueIssueCategory> getIssueIssueCategories() {
        return issueIssueCategories;
    }

    public void setIssueIssueCategories(Set<com.analystdb.data.IssueIssueCategory> issueIssueCategories) {
        this.issueIssueCategories = issueIssueCategories;
    }

}
