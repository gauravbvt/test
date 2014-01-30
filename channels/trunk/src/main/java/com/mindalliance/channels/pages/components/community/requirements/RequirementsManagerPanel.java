package com.mindalliance.channels.pages.components.community.requirements;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.pages.Releaseable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Requirements manager panel.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/9/13
 * Time: 1:49 PM
 */
public class RequirementsManagerPanel extends AbstractUpdatablePanel implements Releaseable {

    private AjaxTabbedPanel<ITab> tabbedPanel;

    /**
     * Identifiables locked after initialization.
     */
    private Set<Identifiable> lockedIdentifiables = new HashSet<Identifiable>();

    private boolean tabHelpRequested;

    public RequirementsManagerPanel( String id, Set<Long> expansions ) {
        super( id, null, expansions );
        init();
    }

    private void init() {
        addTabPanel();
    }

    private void addTabPanel() {
        tabbedPanel = new AjaxTabbedPanel<ITab>( "tabs", getTabs() ) {
            @Override
            protected void onAjaxUpdate( AjaxRequestTarget target ) {
                tabHelpRequested = true;
                Change change = Change.helpTopic( getUserRoleId(), getTabSectionId(), getTabTopicId() );
                update( target, change );
            }
        };
        tabbedPanel.setOutputMarkupId( true );
        addOrReplace( tabbedPanel );
    }

    @Override
    public String getUserRoleId() {
        return "participant";
    }

    public String getTabSectionId() {
        return "requirements-page";
    }

    public String getTabTopicId() {
        if ( !tabHelpRequested ) {
            return "about-requirements-page";
        } else {
            int tab = tabbedPanel.getSelectedTab();
            switch ( tab ) {
                case 0:
                    return "defining-requirement";
                case 1:
                    return "analyzing-requirements";
                default:
                    return null;
            }
        }
    }


    private List<ITab> getTabs() {
        List<ITab> tabs = new ArrayList<ITab>();
        tabs.add( new AbstractTab( new Model<String>( "Definitions" ) ) {
            public Panel getPanel( String id ) {
                return new RequirementDefinitionsPanel( id, getExpansions() );
            }
        } );
        tabs.add( new AbstractTab( new Model<String>( "Analysis" ) ) {
            public Panel getPanel( String id ) {
                return new RequirementsAnalysisPanel( id, getExpansions() );
            }
        } );
        return tabs;
    }

    public void updateContent( AjaxRequestTarget target ) {
        // do nothing
    }

    ////


    /**
     * Release locks acquired after initialization.
     */
    @Override
    public void release() {
        for ( Identifiable identifiable : lockedIdentifiables ) {
            getCommander().releaseAnyLockOn( getUser().getUsername(), identifiable );
        }
        lockedIdentifiables = new HashSet<Identifiable>();
    }

    /**
     * Release any lock on an identifiable.
     *
     * @param identifiable an identifiable
     */
    @Override
    public void releaseAnyLockOn( Identifiable identifiable ) {
        getCommander().releaseAnyLockOn( getUser().getUsername(), identifiable );
        lockedIdentifiables.remove( identifiable );
    }

    /**
     * Release any lock on an identifiable.
     *
     * @param identifiable an identifiable
     */
    @Override
    public void requestLockOn( Identifiable identifiable ) {
        getCommander().requestLockOn( getUser().getUsername(), identifiable );
        lockedIdentifiables.add( identifiable );
    }

}
