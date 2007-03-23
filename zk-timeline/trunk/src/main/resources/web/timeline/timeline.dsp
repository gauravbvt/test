<%--
timeline.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jan 19 21:07:55     2007, Created by Gu WeiXing
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/zk/core.dsp.tld" prefix="u" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}" z.type="timelinez.timeline.Timeline" z.orient="${self.orient}">
	<div id="${self.uuid}!timeline" style="height: ${self.height} ;width: ${self.width} ; border: 1px solid #aaa">
	</div>
	<c:forEach var="child" items="${self.children}">
		${u:redraw(child, null)}
	</c:forEach>
</div>
