package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ifm.InProject;
import com.mindalliance.channels.playbook.ifm.project.Project;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Referenceable;
import com.mindalliance.channels.playbook.support.Mapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * ...
 */
public class ProjectFilter extends Filter {

    private Ref project;

    //-------------------------
    public ProjectFilter() {
        super();
    }
    public ProjectFilter(Ref ref) {
        super("in project " + getProject(ref));
        this.project = ref;
    }

    static Project getProject(Ref projectRef) {
        Project result = (Project) projectRef.deref();
        projectRef.detach();
        return result;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();
        map.put("project", (Object) Mapper.toPersistedValue( project ));
        return map;
    }

    public void initFromMap( Map map ) {
        project = (Ref)Mapper.valueFromPersisted( map.get( "project" ));
        super.initFromMap( map );
    }

    protected List<Filter> createChildren() {
        return Collections.emptyList();
    }

    public boolean match( Ref object ) {
        Referenceable r = object.deref();
        if ( r instanceof InProject ) {
            InProject ip = (InProject) r;
            return getProject().equals( ip.getProject() );
        }
        else
            return false;
    }

    protected boolean strictlyAllowsClass( Class<?> c ) {
        return InProject.class.isAssignableFrom( c );
    }

    public Ref getProject() {
        return project;
    }

    public void setProject( Ref project ) {
        this.project = project;
    }
}
