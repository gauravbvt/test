package com.mindalliance.channels.playbook.pages.forms.tabs.taxonomy;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.ifm.Channels;
import com.mindalliance.channels.playbook.ifm.taxonomy.TaxonomyParticipation;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 10, 2008
 * Time: 4:04:00 PM
 */
public class TaxonomyParticipationTab extends AbstractFormTab {

    DynamicFilterTree participatingUsersTree;

    public TaxonomyParticipationTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();

        participatingUsersTree = new DynamicFilterTree("participations", new RefPropertyModel(getElement(), "participatingUsers"),
                                                       new RefPropertyModel(Channels.instance(), "users")) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> selectedUsers = participatingUsersTree.getNewSelections();
                List<Ref> participations = (List<Ref>)getProperty("participations");
                // Delete obsolete participations
                for (Ref participation: participations) {
                    Ref participatingUser = (Ref)RefUtils.get(participation, "user");
                    if (!selectedUsers.contains(participatingUser)) {
                        participation.begin();
                        RefUtils.remove(getElement(), "participations", participation);
                        participation.delete();
                    }
                    else {
                        selectedUsers.remove(participatingUser);
                    }
                }
                // Create and add new participations
                for (Ref user : selectedUsers) { // now only new participations
                    TaxonomyParticipation mp = new TaxonomyParticipation();
                    mp.persist();
                    mp.setUser(user);
                    RefUtils.add(getElement(), "participations", mp);
                }
            }
        };
        addReplaceable(participatingUsersTree);
    }

}
