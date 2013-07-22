package com.mindalliance.sb.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(versionField = "", table = "mission_area")
@RooDbManaged(automaticallyDelete = true)
@JsonFilter("csvFilter")
@JsonPropertyOrder({ "name", "description", "coreCapabilities" })
public class MissionArea {
}
