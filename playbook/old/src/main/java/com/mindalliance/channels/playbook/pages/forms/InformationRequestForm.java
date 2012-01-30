package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.flowAct.FlowActBasicTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.informationRequest.InformationRequestNeedTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.event.EventCauseTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.event.EventRiskTab;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 9, 2008
 * Time: 9:04:49 PM
 */
public class InformationRequestForm extends AbstractInformationActForm {

    private static final long serialVersionUID = 9047596381457317723L;

    public InformationRequestForm( String id, Ref element ) {
        super( id, element );
    }

    @Override
    void loadTabs() {
        tabs.add(
                new AbstractTab( new Model<String>( "Basic" ) ) {
                    private static final long serialVersionUID =
                            3415303899263050405L;

                    @Override
                    public Panel getPanel( String panelId ) {
                        return new FlowActBasicTab(
                                panelId, InformationRequestForm.this );
                    }
                } );
        tabs.add(
                new AbstractTab( new Model<String>( "Cause" ) ) {
                    private static final long serialVersionUID =
                            2923084347968266916L;

                    @Override
                    public Panel getPanel( String panelId ) {
                        return new EventCauseTab(
                                panelId, InformationRequestForm.this );
                    }
                } );
        tabs.add(
                new AbstractTab( new Model<String>( "Information need" ) ) {
                    private static final long serialVersionUID =
                            1498256053827363744L;

                    @Override
                    public Panel getPanel( String panelId ) {
                        return new InformationRequestNeedTab(
                                panelId, InformationRequestForm.this );
                    }
                } );
        tabs.add(
                new AbstractTab( new Model<String>( "Risk" ) ) {
                    private static final long serialVersionUID =
                            -1989200921379110225L;

                    @Override
                    public Panel getPanel( String panelId ) {
                        return new EventRiskTab(
                                panelId, InformationRequestForm.this );
                    }
                } );
    }
}
