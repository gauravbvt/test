
package com.analystdb.data;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;


/**
 *  analystDB.Approach
 *  03/26/2013 11:09:00
 * 
 */
public class Approach {

    private Long id;
    private Interview interview;
    private String name;
    private String description;
    private Integer version;
    private Date start;
    private Date end;
    private Boolean approved;
    private Integer cost;
    private Set<com.analystdb.data.IssueApproach> issueApproachs = new HashSet<com.analystdb.data.IssueApproach>();

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

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Set<com.analystdb.data.IssueApproach> getIssueApproachs() {
        return issueApproachs;
    }

    public void setIssueApproachs(Set<com.analystdb.data.IssueApproach> issueApproachs) {
        this.issueApproachs = issueApproachs;
    }

}
