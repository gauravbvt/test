
package com.analystdb.data;



/**
 *  analystDB.ProjectFiles
 *  01/06/2013 20:47:04
 * 
 */
public class ProjectFiles {

    private Integer name;
    private Project project;
    private String description;
    private byte[] content;

    public Integer getName() {
        return name;
    }

    public void setName(Integer name) {
        this.name = name;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

}
