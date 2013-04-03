
package com.analystdb.data.output;

import com.analystdb.data.DocumentCategory;


/**
 * Generated for query "documentCategoryIssueCounts" on 04/03/2013 19:27:05
 * 
 */
public class DocumentCategoryIssueCountsRtnType {

    private DocumentCategory category;
    private String phase;
    private Long issues;

    public DocumentCategory getCategory() {
        return category;
    }

    public void setCategory(DocumentCategory category) {
        this.category = category;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public Long getIssues() {
        return issues;
    }

    public void setIssues(Long issues) {
        this.issues = issues;
    }

}
