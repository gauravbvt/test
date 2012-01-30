package com.mindalliance.channels.playbook.pages.forms.tabs.system;

import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.tabs.resource.ResourceIdentityTab;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.MarkupContainer;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2008
 * Time: 9:59:15 AM
 */
public class SystemIdentityTab extends ResourceIdentityTab {

    private DynamicFilterTree adminPositionTree;
    private static final long serialVersionUID = -4933477828541778293L;

    public SystemIdentityTab( String id, AbstractElementForm elementForm ) {
        super( id, elementForm );
    }

    @Override
    protected void load() {
        super.load();

        MarkupContainer organizationLink = new AjaxLink( "organizationLink" ) {
            private static final long serialVersionUID = -362300226534862551L;

            @Override
            public void onClick( AjaxRequestTarget target ) {
                edit( (Ref) getProperty( "organization" ), target );
            }
        };
        organizationLink.add( new Label(
            "organizationName",
            new RefPropertyModel( getElement(), "organization.name" ) ) );
        addReplaceable( organizationLink );

        addInputField( new TextArea<String>(
            "instructions",
            new RefPropertyModel<String>( getElement(), "instructions" ) ) );

        adminPositionTree = new DynamicFilterTree(
                "adminPosition",
                new RefPropertyModel( getElement(), "adminPosition" ),
                new RefPropertyModel<Serializable>(
                        getElement(),
                        "organization.positions",
                        new ArrayList<Ref>() ),
                SINGLE_SELECTION ) {
            private static final long serialVersionUID = -1117702529285434233L;

            @Override
            public void onFilterSelect(
                    AjaxRequestTarget target, Filter filter ) {
                setProperty(
                        "adminPosition", adminPositionTree.getNewSelection() );
            }
        };
        addReplaceable( adminPositionTree );
    }
}
