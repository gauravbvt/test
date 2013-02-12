
package com.analystdb.data.output;

import com.analystdb.data.Issue;


/**
 * Generated for query "documentIssues" on 02/11/2013 20:47:46
 * 
 */
public class DocumentIssuesRtnType {

    private Long id;
    private Issue issue;
    private Long approaches;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

}
