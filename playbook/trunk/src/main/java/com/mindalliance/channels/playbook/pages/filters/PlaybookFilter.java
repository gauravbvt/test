package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ifm.playbook.Playbook;
import com.mindalliance.channels.playbook.ifm.playbook.PlaybookElement;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Referenceable;
import com.mindalliance.channels.playbook.support.persistence.Mappable;
import com.mindalliance.channels.playbook.support.Mapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * ...
 */
public class PlaybookFilter extends Filter {

    private Ref playbook;

    public PlaybookFilter() {} // for persistency only

    public PlaybookFilter(Ref ref) {
        super("in playbook " + getPlaybook(ref));
        this.playbook = ref;
    }

    static Playbook getPlaybook(Ref playbookRef) {
        Playbook result = (Playbook) playbookRef.deref();
        playbookRef.detach();
        return result;
    }

    protected List<Filter> createChildren() {
        return Collections.emptyList();
    }

    public boolean match(Ref ref) {
        Referenceable object = ref.deref();
        if (object instanceof PlaybookElement) {
            PlaybookElement pe = (PlaybookElement) object;
            return playbook.equals(pe.getPlaybook());
        }
        return false;
    }

    protected boolean strictlyAllowsClass(Class<?> c) {
        return PlaybookElement.class.isAssignableFrom(c);
    }

    public Ref getPlaybook() {
        return playbook;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();
        map.put("playbook", (Object) Mapper.toPersistedValue( playbook ));
        return map;
    }

    public void initFromMap(Map map) {
        playbook = (Ref)Mapper.valueFromPersisted( map.get( "playbook" ));
        super.initFromMap(map);
    }
}
