
package com.analystdb.data.output;

import com.analystdb.data.IssueCategory;


/**
 * Generated for query "issueCategoryCounts" on 01/25/2013 16:28:50
 * 
 */
public class IssueCategoryCountsRtnType {

    private IssueCategory category;
    private String name;
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

    public Long getIssues() {
        return issues;
    }

    public void setIssues(Long issues) {
        this.issues = issues;
    }

}
