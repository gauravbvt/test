package com.mindalliance.channels.playbook.ifm
/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 21, 2008
 * Time: 11:42:26 AM
 */
class Participation {

    Reference user
    Reference project
    boolean analyst
    Reference person
    List<Reference> todos = new ArrayList<Todo>()[]

}