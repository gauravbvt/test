package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.ifm.taxonomy.EventType;
import com.mindalliance.channels.playbook.ifm.info.InformationNeed;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.form.CheckBox;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 28, 2008
 * Time: 8:34:37 AM
 */
public class InformationNeedPanel extends AbstractComponentPanel {

    protected InformationNeed informationNeed;
    protected InformationDefinitionPanel informationSpecPanel;
    protected TimingPanel deadlinePanel;
    protected CheckBox criticalField;

    public InformationNeedPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        informationNeed = (InformationNeed)getComponent();
        informationSpecPanel = new InformationDefinitionPanel("informationSpec", this, propPath + ".informationSpec", isReadOnly(), feedback);
        this.addReplaceable(informationSpecPanel);
        deadlinePanel = new TimingPanel("deadline", this, propPath+".deadline", isReadOnly(), feedback);
        addReplaceable(deadlinePanel);
        criticalField = new CheckBox("critical", new RefPropertyModel(getElement(), propPath+".critical"));
        addInputField(criticalField);
    }

}
