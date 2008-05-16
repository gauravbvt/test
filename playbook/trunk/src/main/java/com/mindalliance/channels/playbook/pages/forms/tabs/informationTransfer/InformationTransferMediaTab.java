package com.mindalliance.channels.playbook.pages.forms.tabs.informationTransfer;

import com.mindalliance.channels.playbook.pages.forms.tabs.informationAct.InformationActBasicTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.model.Model;

import java.util.List;
import java.util.ArrayList;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 9, 2008
 * Time: 9:54:42 PM
 */
public class InformationTransferMediaTab extends InformationActBasicTab {

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
                RefUtils.set(getElement(), "mediumType", selection);
            }
        };
        addReplaceable(mediumTypeChoice);
    }

}
