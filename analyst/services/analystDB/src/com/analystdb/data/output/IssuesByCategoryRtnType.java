
package com.analystdb.data.output;

import com.analystdb.data.Issue;


/**
 * Generated for query "issuesByCategory" on 02/11/2013 20:47:46
 * 
 */
public class IssuesByCategoryRtnType {

    private Long key;
    private Issue issue;
    private Long approaches;
    private Long flows;
    private Long documents;

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public Long getApproaches() {
        return approaches;
    }

    public void setApproaches(Long approaches) {
        this.approaches = approaches;
    }

    public Long getFlows() {
        return flows;
    }

    public void setFlows(Long flows) {
        this.flows = flows;
    }

    public Long getDocuments() {
        return documents;
    }

    public void setDocuments(Long documents) {
        this.documents = documents;
    }

}
