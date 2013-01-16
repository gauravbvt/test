
package com.analystdb.data;

import java.util.HashSet;
import java.util.Set;


/**
 *  analystDB.Approach
 *  01/06/2013 20:47:04
 * 
 */
public class Approach {

    private Long id;
    private Interview interview;
    private String name;
    private String description;
    private Integer version;
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

    public Set<com.analystdb.data.IssueApproach> getIssueApproachs() {
        return issueApproachs;
    }

    public void setIssueApproachs(Set<com.analystdb.data.IssueApproach> issueApproachs) {
        this.issueApproachs = issueApproachs;
    }

}
