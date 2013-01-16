
package com.analystdb.data;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;


/**
 *  analystDB.Interview
 *  01/06/2013 20:47:04
 * 
 */
public class Interview {

    private Long id;
    private Resource resource;
    private Date scheduled;
    private String notes;
    private Boolean done;
    private Integer version;
    private Set<com.analystdb.data.Approach> approachs = new HashSet<com.analystdb.data.Approach>();
    private Set<com.analystdb.data.IssueComment> issueComments = new HashSet<com.analystdb.data.IssueComment>();
    private Set<com.analystdb.data.Flow> flows = new HashSet<com.analystdb.data.Flow>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Date getScheduled() {
        return scheduled;
    }

    public void setScheduled(Date scheduled) {
        this.scheduled = scheduled;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Set<com.analystdb.data.Approach> getApproachs() {
        return approachs;
    }

    public void setApproachs(Set<com.analystdb.data.Approach> approachs) {
        this.approachs = approachs;
    }

    public Set<com.analystdb.data.IssueComment> getIssueComments() {
        return issueComments;
    }

    public void setIssueComments(Set<com.analystdb.data.IssueComment> issueComments) {
        this.issueComments = issueComments;
    }

    public Set<com.analystdb.data.Flow> getFlows() {
        return flows;
    }

    public void setFlows(Set<com.analystdb.data.Flow> flows) {
        this.flows = flows;
    }

}
