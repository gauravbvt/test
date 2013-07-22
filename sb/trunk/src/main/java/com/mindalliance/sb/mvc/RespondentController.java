package com.mindalliance.sb.mvc;

import com.mindalliance.sb.model.Respondent;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/lists/respondents")
@Controller
@RooWebScaffold(path = "lists/respondents", formBackingObject = Respondent.class)
public class RespondentController {
}
