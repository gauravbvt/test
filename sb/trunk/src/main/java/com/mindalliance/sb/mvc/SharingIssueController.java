package com.mindalliance.sb.mvc;

import com.mindalliance.sb.model.SharingIssue;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/lists/sharingissues")
@Controller
@RooWebScaffold(path = "lists/sharingissues", formBackingObject = SharingIssue.class)
public class SharingIssueController {
}
