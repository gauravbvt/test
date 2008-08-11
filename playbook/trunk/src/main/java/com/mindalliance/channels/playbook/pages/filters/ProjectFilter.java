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
    private static final long serialVersionUID = -1842485671488024904L;

    //-------------------------
    public ProjectFilter() {
        project = null;
    }
    public ProjectFilter(Ref ref) {
        super("... in project " + getProject(ref));
        setInclusion( true );
        project = ref;
    }

    static Project getProject(Ref projectRef) {
        Project result = (Project) projectRef.deref();
        projectRef.detach();
        return result;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();
        map.put("project", (Object) Mapper.toPersistedValue( project ));
        return map;
    }

    @Override
    public void initFromMap( Map<String,Object> map ) {
        setProject( (Ref)Mapper.valueFromPersisted( map.get( "project" )));
        super.initFromMap( map );
    }

    @Override
    protected List<Filter> createChildren( boolean selectionState ) {
        return Collections.emptyList();
    }

    @Override
    public boolean isMatching( Ref object ) {
        Referenceable r = object.deref();
        boolean result = false;
        if ( r instanceof InProject ) {
            InProject ip = (InProject) r;
            result = getProject().equals( ip.getProject() );
        }

        return result;
    }

    @Override
    protected boolean allowsClassLocally( Class<?> c ) {
        return InProject.class.isAssignableFrom( c );
    }

    public Ref getProject() {
        return project;
    }

    public void setProject( Ref project ) {
        this.project = project;
    }
}
