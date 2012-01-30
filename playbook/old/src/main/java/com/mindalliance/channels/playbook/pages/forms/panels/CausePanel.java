package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.pages.forms.AbstractPlaybookPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 16, 2008
 * Time: 1:03:23 PM
 */
public class CausePanel extends AbstractComponentPanel {

    protected DynamicFilterTree causeTree;
    protected TimingPanel delayPanel;

    public CausePanel(String id, AbstractPlaybookPanel parentPanel, String propPath) {
        super(id, parentPanel, propPath);
    }

    protected void load() {
        super.load();
        causeTree = new DynamicFilterTree("trigger", new RefPropertyModel(getElement(), propPath+".trigger"),
                                            new RefQueryModel(getPlaybook(), new Query("findCandidateCauses", getElement())),
                                            SINGLE_SELECTION) {
             public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                  Ref selected = causeTree.getNewSelection();
                  setProperty("trigger", selected);
              }
         };
         addReplaceable(causeTree);
         delayPanel = new TimingPanel("delay", this, propPath+".delay");
         addReplaceable(delayPanel);
    }
}
