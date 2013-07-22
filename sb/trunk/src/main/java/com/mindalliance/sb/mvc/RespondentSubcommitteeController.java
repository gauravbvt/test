package com.mindalliance.sb.mvc;

import com.mindalliance.sb.model.RespondentSubcommittee;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/lists/respondentsubcommittees")
@Controller
@RooWebScaffold(path = "lists/respondentsubcommittees", formBackingObject = RespondentSubcommittee.class)
public class RespondentSubcommitteeController {
}
