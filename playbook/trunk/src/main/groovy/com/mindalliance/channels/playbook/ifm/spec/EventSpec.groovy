package com.mindalliance.channels.playbook.ifm.spec

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.spec.Spec
import com.mindalliance.channels.playbook.ifm.spec.LocationSpec
import com.mindalliance.channels.playbook.ifm.Timing
import com.mindalliance.channels.playbook.ifm.info.Location
import com.mindalliance.channels.playbook.ifm.info.Information
import com.mindalliance.channels.playbook.ref.Bean

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 9:19:42 AM
*/
class EventSpec extends SpecImpl {

    List<Ref> eventTypes = []// the kinds of event (AND-ed)
    LocationSpec locationSpec = new LocationSpec() // a specified location...
    Location location = new Location() // or a specific location
    Ref causeEvent // caused by a specific event (if set) -- meaningful only within a playbook element

    Timing timing = new Timing(amount:0)// must have occurred in the last n hours, days etc.

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['locationSpecific'])
    }

    boolean isDefined() {
        return !eventTypes.isEmpty()
    }

    boolean isLocationSpecific() {
        return location.isDefined()
    }

    void setLocation(Location loc) {
        location = loc
        if (loc.isDefined()) locationSpec = new LocationSpec()
    }

    void setLocationSpec(LocationSpec spec) {
        locationSpec = spec
        if (spec.isDefined()) location = new Location()
    }

    // Matches against information about an event (an agent's perception of the event)
    public boolean doesMatch(Bean bean) {
        Information info = (Information)bean
        // matches if every spec-ed event types is implied by at least one event type in the info
        if (!eventTypes.every {set-> info.eventTypes.any {iet -> iet.implies(set)}}) return false
        // matches if given or spec-ed location matched by event's location
        if (isLocationSpecific()) {
            if (!info.event.location.isWithin(location)) return false
        }
        else {
            if (!locationSpec.matches(info.event.location)) return false
        }
        // same cause (if set)
        if (causeEvent && info.event.cause != causeEvent ) return false
        return true
    }

    public boolean narrows(Spec spec) {
        return false;  // TODO
    }
}