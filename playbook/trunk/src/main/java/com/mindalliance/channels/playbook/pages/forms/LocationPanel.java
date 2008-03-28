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
    WebMarkupContainer div;
    AutoCompleteTextField countryField;
    AutoCompleteTextField stateField;
    AutoCompleteTextField countyField;
    AutoCompleteTextField cityField;
    TextField streetField;
    TextField codeField;
    FeedbackPanel feedback;

    public LocationPanel(String id, Location loc) {
        super(id);    //To change body of overridden methods use File | Settings | File Templates.
        location = loc;
        load();
    }

    private void load() {
        feedback = new FeedbackPanel("locationFeedback", IFeedbackMessageFilter.ALL);
        div = new WebMarkupContainer("location");
        add(div);
        // Fields
        countryField = new AutoCompleteTextField("country", new PropertyModel(location, "country")) {
            protected Iterator getChoices(String input) {
                return countryIterator(input, 10);
            }
        };
        stateField = new AutoCompleteTextField("state", new PropertyModel(location, "state")) {
            protected Iterator getChoices(String input) {
                return stateIterator(input, location.getCountry(),  10);
            }

        };
        countyField = new AutoCompleteTextField("county", new PropertyModel(location, "county")) {
            protected Iterator getChoices(String input) {
                return countyIterator(input, location.getCountry(), location.getState(),  10);
            }

        };
        cityField = new AutoCompleteTextField("city", new PropertyModel(location, "city")) {
            protected Iterator getChoices(String input) {
                return cityIterator(input, location.getCountry(), location.getState(), location.getCounty(), 10);
            }
        };
        streetField = new TextField("street", new PropertyModel(location, "street"));
        streetField.setPersistent(false);
        codeField = new TextField("code", new PropertyModel(location, "code"));
        codeField.setPersistent(false);
       // Ajax
       // Country field
       countryField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // String countryName = countryField.getModelObjectAsString();
                // System.out.println("Updating country to " + countryName);
                location.setState("");
                location.setCounty("");
                location.setCity("");
                location.setStreet("");
                location.setCode("");
                target.addComponent(stateField);
                target.addComponent(countyField);
                target.addComponent(cityField);
                target.addComponent(streetField);
                target.addComponent(codeField);
                feedback.setModel(new FeedbackMessagesModel(feedback));
                target.addComponent(feedback);
            }
        });
        // countryField.add(GeoValidator.countryExists());
        div.add(countryField);
        // State field
        stateField.setOutputMarkupId(true);
        stateField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // String stateName = stateField.getModelObjectAsString();
                // System.out.println("Updating state to " + stateName);
                location.setCounty("");
                location.setCity("");
                location.setStreet("");
                location.setCode("");
                target.addComponent(countyField);
                target.addComponent(cityField);
                target.addComponent(streetField);
                target.addComponent(codeField);
                feedback.setModel(new FeedbackMessagesModel(feedback));
                target.addComponent(feedback);
            }
        });

//        stateField.add(GeoValidator.stateExists());
        div.add(stateField);
        // County field
        countyField.setOutputMarkupId(true);
        countyField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // String countyName = countyField.getModelObjectAsString();
                // System.out.println("Updating county to " + countyName);
                location.setCity("");
                location.setStreet("");
                location.setCode("");
                target.addComponent(cityField);
                target.addComponent(streetField);
                target.addComponent(codeField);
                feedback.setModel(new FeedbackMessagesModel(feedback));
                target.addComponent(feedback);
            }
        });
        div.add(countyField);
        // City field
        cityField.setOutputMarkupId(true);
        cityField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // String cityName = cityField.getModelObjectAsString();
                // System.out.println("Updating city to " + cityName);
                location.setStreet("");
                location.setCode("");
                target.addComponent(streetField);
                target.addComponent(codeField);
                feedback.setModel(new FeedbackMessagesModel(feedback));
                target.addComponent(feedback);
            }
        });
        div.add(cityField);
        // Street field
        streetField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // String streetName = streetField.getModelObjectAsString();
                // System.out.println("Updating street to " + streetName);
                feedback.setModel(new FeedbackMessagesModel(feedback));
                target.addComponent(feedback);
            }
        });
        div.add(streetField);
        // Code field
        codeField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // String streetName = streetField.getModelObjectAsString();
                 // System.out.println("Updating street to " + streetName);
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

    public void onDetach() {
        location.detach();
        super.onDetach();
    }

    public void refresh(Location location, AjaxRequestTarget target) {
        this.location.setFrom(location);
        target.addComponent(countryField);
        target.addComponent(stateField);
        target.addComponent(countyField);
        target.addComponent(cityField);
        target.addComponent(streetField);
        target.addComponent(codeField);
        feedback.setModel(new FeedbackMessagesModel(feedback));
        target.addComponent(feedback);
    }

    private boolean isValidCode(Location location) {
        String code = location.getCode();
        if (code != null && code.length() > 0) {
           return GeoService.validateCode(code, location.getCountry(), location.getState(), location.getCounty(), location.getCity());
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

    private Iterator countyIterator(String input, String countryName, String stateName, int max) {
        if (Strings.isEmpty(input)) {
            return Collections.EMPTY_LIST.iterator();
        }
        List choices = GeoService.findCandidateCountyNames(input, countryName, stateName, max);
        return choices.iterator();
    }

    private Iterator cityIterator(String input, String countryName, String stateName, String countyName, int max) {
        if (Strings.isEmpty(input)) {
            return Collections.EMPTY_LIST.iterator();
        }
        List choices = GeoService.findCandidateCityNames(input, countryName, stateName, countyName, max);
        return choices.iterator();
    }
}
