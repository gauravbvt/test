
package com.analystdb.data;

import java.util.HashSet;
import java.util.Set;


/**
 *  analystDB.IssueComment
 *  02/10/2013 19:29:36
 * 
 */
public class IssueComment {

    private Long id;
    private Interview interview;
    private Issue issue;
    private String description;
    private Boolean applicableToMe;
    private Boolean fixed;
    private Integer version;
    private Set<com.analystdb.data.IssueCommentFlows> issueCommentFlowses = new HashSet<com.analystdb.data.IssueCommentFlows>();
    private Set<com.analystdb.data.IssueAttribute> issueAttributes = new HashSet<com.analystdb.data.IssueAttribute>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Interview getInterview() {
        return interview;
    }

    public void setInterview(Interview interview) {
        this.interview = interview;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getApplicableToMe() {
        return applicableToMe;
    }

    public void setApplicableToMe(Boolean applicableToMe) {
        this.applicableToMe = applicableToMe;
    }

    public Boolean getFixed() {
        return fixed;
    }

    public void setFixed(Boolean fixed) {
        this.fixed = fixed;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Set<com.analystdb.data.IssueCommentFlows> getIssueCommentFlowses() {
        return issueCommentFlowses;
    }

    public void setIssueCommentFlowses(Set<com.analystdb.data.IssueCommentFlows> issueCommentFlowses) {
        this.issueCommentFlowses = issueCommentFlowses;
    }

    public Set<com.analystdb.data.IssueAttribute> getIssueAttributes() {
        return issueAttributes;
    }

    public void setIssueAttributes(Set<com.analystdb.data.IssueAttribute> issueAttributes) {
        this.issueAttributes = issueAttributes;
    }

}
