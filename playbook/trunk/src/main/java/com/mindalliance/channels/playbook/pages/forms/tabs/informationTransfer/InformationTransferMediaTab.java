package com.mindalliance.channels.playbook.pages.forms.tabs.informationTransfer;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 9, 2008
 * Time: 9:54:42 PM
 */
public class InformationTransferMediaTab extends AbstractFormTab {

    DynamicFilterTree mediumTypeChoice;

    public InformationTransferMediaTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        mediumTypeChoice = new DynamicFilterTree("mediumType", new RefPropertyModel(getElement(), "mediumType"),
                                                   new RefQueryModel(getProject(), new Query("findAllTypes", "MediumType")),
                                                   SINGLE_SELECTION) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                Ref selection = mediumTypeChoice.getNewSelection();
                setProperty("mediumType", selection);
            }
        };
        addReplaceable(mediumTypeChoice);
    }

}
