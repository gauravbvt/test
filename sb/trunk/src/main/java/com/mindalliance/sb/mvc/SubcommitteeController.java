package com.mindalliance.sb.mvc;

import com.mindalliance.sb.model.Subcommittee;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/subcommittees")
@Controller
@RooWebScaffold(path = "subcommittees", formBackingObject = Subcommittee.class, delete = false, create = false)
public class SubcommitteeController {
}
