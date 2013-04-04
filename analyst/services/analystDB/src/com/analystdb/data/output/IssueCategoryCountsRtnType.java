
package com.analystdb.data.output;

import com.analystdb.data.IssueCategory;


/**
 * Generated for query "issueCategoryCounts" on 04/04/2013 13:41:27
 * 
 */
public class IssueCategoryCountsRtnType {

    private IssueCategory category;
    private String name;
    private Long resources;
    private Long issues;

    public IssueCategory getCategory() {
        return category;
    }

    public void setCategory(IssueCategory category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getResources() {
        return resources;
    }

    public void setResources(Long resources) {
        this.resources = resources;
    }

    public Long getIssues() {
        return issues;
    }

    public void setIssues(Long issues) {
        this.issues = issues;
    }

}
