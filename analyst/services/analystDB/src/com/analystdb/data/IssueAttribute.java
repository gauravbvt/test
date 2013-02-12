
package com.analystdb.data;



/**
 *  analystDB.IssueAttribute
 *  02/10/2013 19:29:36
 * 
 */
public class IssueAttribute {

    private Long id;
    private IssueComment issueComment;
    private String name;
    private String val;
    private Integer version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IssueComment getIssueComment() {
        return issueComment;
    }

    public void setIssueComment(IssueComment issueComment) {
        this.issueComment = issueComment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

}
