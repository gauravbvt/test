package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.pages.forms.tabs.policy.PolicyAboutTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.policy.PolicyPartiesTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.policy.PolicyRestrictionsTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.policy.PolicySharingTab;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 10, 2008
 * Time: 8:23:41 PM
 */
public class PolicyForm extends AbstractProjectElementForm {

    private static final long serialVersionUID = -794408183749234922L;

    public PolicyForm( String id, Ref element ) {
        super( id, element );
    }

    @Override
    void loadTabs() {
        tabs.add(
                new AbstractTab( new Model<String>( "About" ) ) {
                    private static final long serialVersionUID =
                            5779009284923090569L;

                    @Override
                    public Panel getPanel( String panelId ) {
                        return new PolicyAboutTab( panelId, PolicyForm.this );
                    }
                } );
        tabs.add(
                new AbstractTab( new Model<String>( "Parties" ) ) {
                    private static final long serialVersionUID =
                            883238166724063966L;

                    @Override
                    public Panel getPanel( String panelId ) {
                        return new PolicyPartiesTab( panelId, PolicyForm.this );
                    }
                } );
        tabs.add(
                new AbstractTab( new Model<String>( "Information" ) ) {
                    private static final long serialVersionUID =
                            -2237114344696235376L;

                    @Override
                    public Panel getPanel( String panelId ) {
                        return new PolicySharingTab( panelId, PolicyForm.this );
                    }
                } );
        tabs.add(
                new AbstractTab( new Model<String>( "Restrictions" ) ) {
                    private static final long serialVersionUID =
                            -1986681500100503446L;

                    @Override
                    public Panel getPanel( String panelId ) {
                        return new PolicyRestrictionsTab(
                                panelId, PolicyForm.this );
                    }
                } );
    }
}
