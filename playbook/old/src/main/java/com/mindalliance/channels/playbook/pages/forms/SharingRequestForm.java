package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.flowAct.FlowActBasicTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.sharingRequest.SharingRequestProtocolTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.event.EventRiskTab;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 16, 2008
 * Time: 2:05:18 PM
 */
public class SharingRequestForm extends AbstractInformationActForm {

    private static final long serialVersionUID = 5178388214398411136L;

    public SharingRequestForm( String id, Ref element ) {
        super( id, element );
    }

    @Override
    void loadTabs() {
        tabs.add(
                new AbstractTab( new Model<String>( "Basic" ) ) {
                    private static final long serialVersionUID =
                            -2464090334126795424L;

                    @Override
                    public Panel getPanel( String panelId ) {
                        return new FlowActBasicTab(
                                panelId, SharingRequestForm.this );
                    }
                } );
        tabs.add(
                new AbstractTab( new Model<String>( "Protocol" ) ) {
                    private static final long serialVersionUID =
                            -1929224681236782663L;

                    @Override
                    public Panel getPanel( String panelId ) {
                        return new SharingRequestProtocolTab(
                                panelId, SharingRequestForm.this );
                    }
                } );
        tabs.add(
                new AbstractTab( new Model<String>( "Risk" ) ) {
                    private static final long serialVersionUID =
                            -7300797998988029256L;

                    @Override
                    public Panel getPanel( String panelId ) {
                        return new EventRiskTab(
                                panelId, SharingRequestForm.this );
                    }
                } );
    }
}
