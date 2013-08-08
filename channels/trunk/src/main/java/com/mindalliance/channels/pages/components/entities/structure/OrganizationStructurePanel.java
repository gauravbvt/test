package com.mindalliance.channels.pages.components.entities.structure;

import com.mindalliance.channels.core.model.Hierarchical;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.guide.Guidable;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Organization structure panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/6/12
 * Time: 2:04 PM
 */
public class OrganizationStructurePanel extends AbstractCommandablePanel implements Guidable {

    /**
     * Prefix DOM identifier for org chart element.
     */
    private static final String PREFIX_DOM_IDENTIFIER = ".entity";

    private WebMarkupContainer tabContainer;

    public OrganizationStructurePanel( String id, IModel<Organization> model, Set<Long> expansions, String prefixDomIdentifier ) {
        super( id, model, expansions );
        init();
    }

    @Override
    public String getHelpSectionId() {
        return "profiling";
    }

    @Override
    public String getHelpTopicId() {
        return "profiling-organization";
    }

    private void init() {
        addTabPanel();
    }

    private void addTabPanel() {
        tabContainer = new WebMarkupContainer( "tabContainer" );
        tabContainer.setOutputMarkupId( true );
        makeVisible( tabContainer, getOrganization().isActual() );
        addOrReplace( tabContainer );
        tabContainer.add( new AjaxTabbedPanel<ITab>( "tabs", getTabs() ) );
    }

    private List<ITab> getTabs() {
        List<ITab> tabs = new ArrayList<ITab>();
        if ( getOrganization().isActual() ) {
            tabs.add( new AbstractTab( new Model<String>( "Jobs" ) ) {
                public Panel getPanel( String id ) {
                    return new JobsPanel(
                            id,
                            new PropertyModel<Organization>( OrganizationStructurePanel.this, "organization" ),
                            getExpansions() );
                }
            } );
            tabs.add( new AbstractTab( new Model<String>( "Chart" ) ) {
                public Panel getPanel( String id ) {
                    return new HierarchyPanel(
                            id,
                            new PropertyModel<Hierarchical>( OrganizationStructurePanel.this, "organization" ),
                            getExpansions(),
                            PREFIX_DOM_IDENTIFIER );
                }
            } );
        }
        return tabs;
    }

   public Organization getOrganization() {
       return (Organization)getModel().getObject();
   }

}
