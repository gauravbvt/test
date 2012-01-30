package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.ifm.playbook.Playbook;
import com.mindalliance.channels.playbook.ifm.project.Project;
import com.mindalliance.channels.playbook.ifm.taxonomy.Taxonomy;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.AbstractPlaybookPanel;
import com.mindalliance.channels.playbook.ref.Bean;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.RefUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

import java.io.Serializable;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved. Proprietary and Confidential. User: jf Date: Mar 28,
 * 2008 Time: 3:15:33 PM
 */
// Not much to abstract...
public abstract class AbstractComponentPanel extends AbstractPlaybookPanel {

    public static final boolean SINGLE_SELECTION = true;

    protected AbstractPlaybookPanel parentPanel;
    protected String propPath;// path to the element's property which value is the component to be edited
    // protected WebMarkupContainer div;

    private Serializable editedComponent;
    private static final long serialVersionUID = -8991995817484671424L;

    protected AbstractComponentPanel( String id, AbstractPlaybookPanel parentPanel, String propPath ) {
        super( id );
        this.parentPanel = parentPanel;
        this.propPath = propPath;
        load();
        init();
    }

    @Override
    public FeedbackPanel getFeedback() {
        return parentPanel.getFeedback();
    }

    // ElementPanel

    public Ref getElement() {
        return parentPanel.getElement();
    }

    public Object getObject() {
        return getComponent();
    }

    public Serializable getComponent() {
        if ( editedComponent == null ) {
            editedComponent = (Serializable) RefUtils.get( getElement(), propPath );
        }
        return editedComponent;
    }

    public Object getParentObject() {
        return parentPanel.getObject();
    }

    public void elementChanged( String propPath, AjaxRequestTarget target ) {
        parentPanel.elementChanged( propPath, target );
    }

    public void addOtherElement( Ref otherElement ) {
        parentPanel.addOtherElement( otherElement );
    }

    public AbstractElementForm getTopElementPanel() {
        return parentPanel.getTopElementPanel();
    }

    public boolean isProjectPanel() {
        return parentPanel.isProjectPanel();
    }

    public boolean isTaxonomyPanel() {
        return parentPanel.isTaxonomyPanel();
    }

    public boolean isReadOnly() {
        return parentPanel.isReadOnly();
    }

    public boolean isPlaybookPanel() {
        return parentPanel.isPlaybookPanel();
    }

    public Project getProject() {
        return parentPanel.getProject();
    }

    public Taxonomy getTaxonomy() {
        return parentPanel.getTaxonomy();
    }

    public Playbook getPlaybook() {
        return parentPanel.getPlaybook();
    }

    public Ref getScope() {
        return parentPanel.getScope();
    }

    public void edit( Ref ref, AjaxRequestTarget target ) {
        parentPanel.edit( ref, target );
    }

    // end ElementPanel

    protected void beforeLoad() {
        // do nothing
    }

    protected void load() {
        /*  div = new WebMarkupContainer("component");
        div.setOutputMarkupId(true);
        add(div);*/
    }

    @Override
    public void onDetach() {
        try {
            Object component = getComponent();
            if ( component != null && component instanceof Bean )
                ( (Bean) component ).detach();
        } catch ( RuntimeException e ) {
            Logger.getLogger( getClass() ).error( "Error detaching " + getElement() + "'s " + propPath );
            throw e;
        }
        super.onDetach();
    }

    @Override
    protected void addReplaceable( Component component ) {
        addReplaceableTo( component, this );
    }

    protected void addReplaceableTo( Component component, MarkupContainer container ) {
        component.setOutputMarkupId( true );
        container.addOrReplace( component );
        if ( isReadOnly() )
            component.setEnabled( false );
    }

    protected void setProperty( String property, Object value ) { 
        RefUtils.set( getElement(), getPropertyPath(property), value );
    }

    protected void setProperty( String property, Object value, AjaxRequestTarget target ) {
        setProperty( property, value );
        elementChanged( getPropertyPath(property), target );
    }

    protected Object getProperty( String property ) {
        String path = propPath.isEmpty() ? property : (propPath + '.' + property);
        return RefUtils.get( getElement(), getPropertyPath(property) );
    }

    private String getPropertyPath(String property) {
       return propPath.isEmpty() ? property : (propPath + '.' + property);
    }
}
