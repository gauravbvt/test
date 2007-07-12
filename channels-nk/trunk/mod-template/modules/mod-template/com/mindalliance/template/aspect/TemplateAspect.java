package com.mindalliance.template.aspect;

import com.mindalliance.template.Template;

public class TemplateAspect implements IAspectTemplate {
	private Template t;

	public TemplateAspect(Template t) {
		this.t = t;
	}

	public Template getTemplate() {
		return t;
	}
}
