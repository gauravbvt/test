package com.mindalliance.channels.playbook.pages.forms;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.feedback.FeedbackMessagesModel;
import org.apache.wicket.feedback.IFeedbackMessageFilter;

import java.util.*;

import com.mindalliance.channels.playbook.ifm.Location;
import com.mindalliance.channels.playbook.geo.GeoService;
import com.mindalliance.channels.playbook.geo.Area;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 24, 2008
 * Time: 3:26:36 PM
 */
public class LocationPanel extends Panel {

    Location location;

    public LocationPanel(String id, Location loc) {
        super(id);    //To change body of overridden methods use File | Settings | File Templates.
        location = (Location)loc.copy();
        load();
    }

    private void load() {
        final FeedbackPanel feedback = new FeedbackPanel("locationFeedback", IFeedbackMessageFilter.ALL);
        final WebMarkupContainer div = new WebMarkupContainer("panel");
        add(div);
        // Country
        final TextField codeField = new TextField("code", new PropertyModel(location, "code"));
        final AutoCompleteTextField countryField = new AutoCompleteTextField("country", new PropertyModel(location, "country")) {
            protected Iterator getChoices(String input) {
                return countryIterator(input, 10);
            }
        };
        final AutoCompleteTextField stateField = new AutoCompleteTextField("state", new PropertyModel(location, "state")) {
            protected Iterator getChoices(String input) {
                return stateIterator(input, countryField.getModelObjectAsString(),  10);
            }

        };
        final AutoCompleteTextField cityField = new AutoCompleteTextField("city", new PropertyModel(location, "city")) {
            protected Iterator getChoices(String input) {
                return cityIterator(input, countryField.getModelObjectAsString(), stateField.getModelObjectAsString(), 10);
            }
        };

       countryField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // String countryName = countryField.getModelObjectAsString();
                // System.out.println("Updating country to " + countryName);
                stateField.setModelObject("");
                target.addComponent(stateField);
                cityField.setModelObject("");
                target.addComponent(cityField);
                feedback.setModel(new FeedbackMessagesModel(feedback));
                target.addComponent(feedback);
            }
        });

//        countryField.add(GeoValidator.countryExists());
        div.add(countryField);
        // State field
        stateField.setOutputMarkupId(true);
        stateField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // String stateName = stateField.getModelObjectAsString();
                // System.out.println("Updating state to " + stateName);
                cityField.setModelObject("");
                target.addComponent(cityField);
                feedback.setModel(new FeedbackMessagesModel(feedback));
                target.addComponent(feedback);
            }
        });

//        stateField.add(GeoValidator.stateExists());
        div.add(stateField);
        // City field
        cityField.setOutputMarkupId(true);
        cityField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                feedback.setModel(new FeedbackMessagesModel(feedback));
                target.addComponent(feedback);
            }
        });
        div.add(cityField);
        // Street field
        final TextField streetField = new TextField("street", new PropertyModel(location, "street"));
        div.add(streetField);
        // Code field
        codeField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                feedback.setModel(new FeedbackMessagesModel(feedback));
                target.addComponent(feedback);
            }
        });
        div.add(codeField);
        // Feedback
        feedback.setOutputMarkupId(true);
        add(feedback);
        add(new AjaxLink("verify", new Model(location)) {
            public void onClick(AjaxRequestTarget target) {
                try {
                   Location loc = (Location)getModelObject();
                   Area area = loc.getArea(false);  // don't cache it (won't serialize)
                   if (area.isUnknown()) {
                       error("Unknown location");
                   }
                    else if (area.isAmbiguous()) {
                       error("Ambiguous location");
                   }
                    else {
                       info("Known location");
                       if (isValidCode(loc)) {
                          info("Code is valid");
                       }
                       else {
                          error("Code not validated");
                       }
                   }
                }
                catch (Exception e) {
                    System.err.println(e); // TODO Log this
                    error("Could not verify location due to system error");
                }
                target.addComponent(feedback);
            }
        });
    }

    // Get modified copy of original location
    public Location getLocation() {
        return location;
    }

    private boolean isValidCode(Location location) {
        String code = location.getCode();
        if (code != null && code.length() > 0) {
           return GeoService.validateCode(code, location.getCountry(), location.getState(), location.getCity());
        }
        else {
            return true;
        }
    }

    private Iterator countryIterator(String input, int max) {
        if (Strings.isEmpty(input)) {
            return Collections.EMPTY_LIST.iterator();
        }
        List choices = GeoService.findCandidateCountryNames(input, max);
        return choices.iterator();
    }

    private Iterator stateIterator(String input, String countryName, int max) {
        if (Strings.isEmpty(input)) {
            return Collections.EMPTY_LIST.iterator();
        }
        List choices = GeoService.findCandidateStateNames(input, countryName, max);
        return choices.iterator();
    }

    private Iterator cityIterator(String input, String countryName, String stateName, int max) {
        if (Strings.isEmpty(input)) {
            return Collections.EMPTY_LIST.iterator();
        }
        List choices = GeoService.findCandidateCityNames(input, countryName, stateName, max);
        return choices.iterator();
    }
}
