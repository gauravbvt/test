package com.mindalliance.channels.playbook.ifm.project

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.IfmElement
import com.mindalliance.channels.playbook.support.PlaybookSession
import org.apache.wicket.Session

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 19, 2008
* Time: 2:10:46 PM
*/
class Project extends IfmElement {

    String name = ''
    String description = ''
    List<Ref> resources = []
    List<Ref> scenarios = []
    List<Ref> modelElements = []
    List<Ref> analysisElements = []

    static Ref currentProject() {
        PlaybookSession session = (PlaybookSession) Session.get()
        return session.project
    }

    String toString() { name }

    Ref findResourceNamed(String type, String name) {
        Ref res = (Ref) resources.find {res ->
            res.type == type && res.name.equalsIgnoreCase(name)
        }
        return res
    }

    Ref findScenarioNamed(String type, String name) {
        Ref sc = (Ref) scenarios.find {sc ->
            sc.type == type && sc.name.equalsIgnoreCase(name)
        }
        return res
    }

    List<Ref> findAllResourcesOfType(String type) {
        return (List<Ref>)resources.findAll {res -> res.type == type}
    }

}