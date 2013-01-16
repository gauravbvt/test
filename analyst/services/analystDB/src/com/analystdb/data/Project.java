
package com.analystdb.data;

import java.util.HashSet;
import java.util.Set;


/**
 *  analystDB.Project
 *  01/06/2013 20:47:04
 * 
 */
public class Project {

    private Long id;
    private String name;
    private String description;
    private Integer version;
    private Set<com.analystdb.data.IssueCategory> issueCategories = new HashSet<com.analystdb.data.IssueCategory>();
    private Set<com.analystdb.data.Analysis> analysises = new HashSet<com.analystdb.data.Analysis>();
    private Set<com.analystdb.data.ProjectFiles> projectFileses = new HashSet<com.analystdb.data.ProjectFiles>();
    private Set<com.analystdb.data.Issue> issues = new HashSet<com.analystdb.data.Issue>();
    private Set<com.analystdb.data.Resource> resources = new HashSet<com.analystdb.data.Resource>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Set<com.analystdb.data.IssueCategory> getIssueCategories() {
        return issueCategories;
    }

    public void setIssueCategories(Set<com.analystdb.data.IssueCategory> issueCategories) {
        this.issueCategories = issueCategories;
    }

    public Set<com.analystdb.data.Analysis> getAnalysises() {
        return analysises;
    }

    public void setAnalysises(Set<com.analystdb.data.Analysis> analysises) {
        this.analysises = analysises;
    }

    public Set<com.analystdb.data.ProjectFiles> getProjectFileses() {
        return projectFileses;
    }

    public void setProjectFileses(Set<com.analystdb.data.ProjectFiles> projectFileses) {
        this.projectFileses = projectFileses;
    }

    public Set<com.analystdb.data.Issue> getIssues() {
        return issues;
    }

    public void setIssues(Set<com.analystdb.data.Issue> issues) {
        this.issues = issues;
    }

    public Set<com.analystdb.data.Resource> getResources() {
        return resources;
    }

    public void setResources(Set<com.analystdb.data.Resource> resources) {
        this.resources = resources;
    }

}
