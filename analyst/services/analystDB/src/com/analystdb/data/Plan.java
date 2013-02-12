
package com.analystdb.data;



/**
 *  analystDB.Plan
 *  02/10/2013 19:29:36
 * 
 */
public class Plan {

    private Long id;
    private Analysis analysis;
    private String description;
    private String name;
    private Boolean approved;
    private Integer version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Analysis getAnalysis() {
        return analysis;
    }

    public void setAnalysis(Analysis analysis) {
        this.analysis = analysis;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

}
