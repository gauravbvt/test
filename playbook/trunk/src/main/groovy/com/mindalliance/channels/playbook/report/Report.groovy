package com.mindalliance.channels.playbook.report

import com.mindalliance.channels.playbook.ifm.Tab
import groovy.xml.MarkupBuilder
import com.mindalliance.channels.playbook.support.PlaybookSession
import com.mindalliance.channels.playbook.ifm.User

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 14, 2008
 * Time: 2:54:50 PM
 */
abstract class Report {

    static final String CONFIDENTIAL = "Confidential"

    Tab tab

    Report(Tab tab) {
        this.tab = tab
    }

    /*
    element report {
        element header {
            element title {text},
            element context {text},
            element user {text}
            },
            element date {text}
        }
        element body {...}
        element footer {
            element confidential {text}
            element proprietary {text}
        }
    }
     */

    String build() {
        StringWriter writer = new StringWriter()
        MarkupBuilder xml = new MarkupBuilder(writer)
        xml.report {
            buildHeader(xml)
            buildBody(xml)
            buildFooter(xml)
        }
        return writer.toString()
    }

    void buildHeader(MarkupBuilder xml) {
        xml.header {
            xml.title(this.title)
            xml.context(tab.getName())
            xml.user(getUser().name)
        }
    }

    void buildFooter(MarkupBuilder xml) {
        xml.footer {
            xml.confidential(CONFIDENTIAL)
            xml.proprietary (getCopyrightNotice())
        }
    }

    private User getUser() {
        return (User) PlaybookSession.current().getUser().deref()
    }

    private String getCopyrightNotice() {
        return "Copyright (C) ${String.format('%tY', Calendar.instance)} Mind-Alliance Systems. All Rights Reserved."
    }

    abstract void buildBody(MarkupBuilder xml)

    abstract String getTitle()



}