package com.mindalliance.sb.mvc;

import com.mindalliance.sb.model.SystemIssue;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/lists/systemissues")
@Controller
@RooWebScaffold(path = "lists/systemissues", formBackingObject = SystemIssue.class)
public class SystemIssueController {
}
