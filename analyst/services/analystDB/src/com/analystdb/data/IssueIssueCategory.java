
package com.analystdb.data;



/**
 *  analystDB.IssueIssueCategory
 *  01/06/2013 20:47:04
 * 
 */
public class IssueIssueCategory {

    private IssueIssueCategoryId id;
    private Issue issue;
    private IssueCategory issueCategory;

    public IssueIssueCategoryId getId() {
        return id;
    }

    public void setId(IssueIssueCategoryId id) {
        this.id = id;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public IssueCategory getIssueCategory() {
        return issueCategory;
    }

    public void setIssueCategory(IssueCategory issueCategory) {
        this.issueCategory = issueCategory;
    }

}
