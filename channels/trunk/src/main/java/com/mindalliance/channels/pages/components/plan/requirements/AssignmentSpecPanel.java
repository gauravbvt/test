package com.mindalliance.channels.pages.components.plan.requirements;

import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Assignment spec panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/3/11
 * Time: 3:20 PM
 */
public class AssignmentSpecPanel extends AbstractCommandablePanel {

    private final boolean isBeneficiary;

    public AssignmentSpecPanel( String id, IModel<Requirement> iModel, boolean isBeneficiary ) {
        super( id, iModel );
        this.isBeneficiary = isBeneficiary;
        init();
    }

    private void init() {
        addTabsPanel();
    }

    private void addTabsPanel() {
        AjaxTabbedPanel tabbedPanel = new AjaxTabbedPanel<ITab>( "tabs", getTabs() );
        tabbedPanel.setOutputMarkupId( true );
        addOrReplace( tabbedPanel );
    }

    private List<ITab> getTabs() {
         List<ITab> tabs = new ArrayList<ITab>();
         tabs.add( new AbstractTab( new Model<String>( "Who" ) ) {
             public Panel getPanel( String id ) {
                 return new RequiredWhoPanel( id, new Model<Requirement>( getRequirement() ), isBeneficiary );
             }
         } );
         tabs.add( new AbstractTab( new Model<String>( "When" ) ) {
             public Panel getPanel( String id ) {
                 return new RequiredWhenPanel( id, new Model<Requirement>( getRequirement() ), isBeneficiary  );
             }
         } );
        tabs.add( new AbstractTab( new Model<String>( "How many" ) ) {
            public Panel getPanel( String id ) {
                return new CardinalityRequiredPanel(
                        id,
                        new Model<Requirement>( getRequirement() ),
                        getProperty() + ".cardinality" );
            }
        } );
         return tabs;
     }


    private Requirement.AssignmentSpec getAssignmentSpec() {
        return isBeneficiary
                ? getRequirement().getBeneficiarySpec()
                : getRequirement().getCommitterSpec();
    }

    private String getProperty() {
        return isBeneficiary ? "beneficiarySpec" : "committerSpec";
    }

    private Requirement getRequirement() {
        return (Requirement)getModel().getObject();
    }

}
