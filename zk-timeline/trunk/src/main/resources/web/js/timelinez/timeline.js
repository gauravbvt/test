/* timeline.js

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jan 4 16:19:47     2007, Created by Gu WeiXing
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
//setup env
zk.load("ext.timeline.api.zkTimeline-api");
//zk.load("http://simile.mit.edu/timeline/api/timeline-api.js");//has a problem in IE
////
zkTimeline = {};
///
//invert the array
zkTimeline.invert = function (list){
	for(i=0;i<(list.length/2);i++){
		var temp=list[i];
		list[i]=list[list.length-1-i];
		list[list.length-1-i]=temp;
	}

};



/** Init (and re-init) a timeline. */
zkTimeline.init = function (cmp) {
	//setup orient
	var value=getZKAttr(cmp,"orient");
	if("horizontal"==value)
		cmp.orient = Timeline.HORIZONTAL;
	else 
		cmp.orient = Timeline.VERTICAL;
	//invert bandInfos array
	if(cmp.bandInfos) zkTimeline.invert(cmp.bandInfos);
	//set layout for syncWith band
	for(i=0;i<cmp.bandInfos.length;i++){
		cmp.bandInfos[i].bandIndex=i;
		if(cmp.bandInfos[i].syncWith && cmp.zones) {
			cmp.bandInfos[i].eventPainter.setLayout(cmp.bandInfos[parseInt(cmp.bandInfos[i].syncWith)].eventPainter.getLayout());
		}
	}
	
	//create a simile timeline instance

	zkTimeline.initTimeline(cmp);
	cmp.instance  = Timeline.create(document.getElementById(cmp.id+"!timeline"), cmp.bandInfos,cmp.orient);

	for(i=0;i<cmp.bandInfos.length;i++){
	//set uuid for band
		cmp.instance.getBand(i).uuid=cmp.bandInfos[i].uuid;
		//add scroll listener to band
			var doScroll=function(band){
				if(band.currentMinVisiableDate>band.getMinVisibleDate()){
					 var uuid=band.uuid;
					var val=[
						band.getMinVisibleDate().toGMTString(),//min
						band.currentMaxVisiableDate.toGMTString(),//max
					];
					band.currentMinVisiableDate=band.getMinVisibleDate();
					zkau.send({uuid:uuid,cmd:"onBandScroll",data:val},5);
				}
				if(band.currentMaxVisiableDate<band.getMaxVisibleDate()){
					var uuid=band.uuid;
					var val=[
						band.currentMinVisiableDate.toGMTString(),//min
						band.getMaxVisibleDate().toGMTString(),//max
					];
					band.currentMaxVisiableDate=band.getMaxVisibleDate();
					zkau.send({uuid:uuid,cmd:"onBandScroll",data:val},5);
				}
				//var uuid=band.uuid;
				//var val=[
				//	band.currentMinVisiableDate.toGMTString(),//min
				//	band.currentMaxVisiableDate.toGMTString(),//max
				//];
				//zkau.send({uuid:uuid,cmd:"onBandScroll",data:val},5);
				
			};
		cmp.instance.getBand(i).addOnScrollListener(doScroll);
		if(cmp.instance.getBand(i).currentMinVisiableDate==null)
			cmp.instance.getBand(i).currentMinVisiableDate=cmp.instance.getBand(i).getMinVisibleDate();
		if(cmp.instance.getBand(i).currentMaxVisiableDate==null)
			cmp.instance.getBand(i).currentMaxVisiableDate=cmp.instance.getBand(i).getMaxVisibleDate();
		var val=[
			cmp.instance.getBand(i).currentMinVisiableDate.toGMTString(),//min
			cmp.instance.getBand(i).currentMaxVisiableDate.toGMTString()//max
		];
		zkau.send({uuid:cmp.instance.getBand(i).uuid,cmd:"onBandScroll",data:val},5);
	}
	
	//load events from .xml document
	for(var s in cmp.eventSources){
		//alert(s);
		Timeline.loadXML(s,
				function(xml, url) {
					for(i=0;i<cmp.bandInfos.length;i++){
						if(cmp.bandInfos[i].eventSourceUrl==url){
							cmp.bandInfos[i].eventSource.loadXML(xml,url);
						}
					}
			});
	}
};
/** Cleanup a timeline called before element being removed. */
zkTimeline.cleanup = function (cmp) {
	cmp.instance=null;
	cmp.bandInfos=null;
	cmp.eventSources=null;
};

/** Called by the server to set the attribute. */
zkTimeline.setAttr = function (cmp, name, value) {
	//alert(name+"="+value);
	if (cmp) {
		switch (name) {
		case "z.width":
			var timeline=$e(cmp.id+"!timeline");
			timeline.style.width=value;
			cmp.instance.layout();
			return true;
		case "z.height":
			var timeline=document.getElementById(cmp.id+"!timeline");
			timeline.style.height=value;
			cmp.instance.layout();
			return true;
		case "z.orientation":
			if("horizontal"==value)
				cmp.orient = Timeline.HORIZONTAL;
			else 
				cmp.orient = Timeline.VERTICAL;
			return true;
		case "z.filter":
			var matcher=value;
			matcher.replace(/^\s+/, '').replace(/\s+$/, '');
          	var	regex=new RegExp(matcher,"i");
        	var filterMatcher = function(evt) {
            	return (regex.test(evt._text) || regex.test(evt._description));
        	};
			for(i=0;i<cmp.instance.getBandCount();i++){
				cmp.instance.getBand(i).getEventPainter().setFilterMatcher(filterMatcher);
			}
			cmp.instance.paint();
			return true;
			
		case "z.highlight":
			var matchers=eval('('+value+')');
			
   			var regexes = [];
     		for (var x = 0; x < matchers.length; x++) {
     			var input=matchers[x];
     			var text2 = input.replace(/^\s+/, '').replace(/\s+$/, '');
            	if(text2.length>0)
            		regexes.push(new RegExp(text2, "i"));
            	else 
            		regexes.push(null);
    		}
    		
    		var highlightMatcher = function(evt) {
    			
        		var text = evt.getText();
        		var description = evt.getDescription();
        		var id = evt._id
        		for (var x = 0; x < regexes.length; x++) {
            		var regex = regexes[x];
            		
            		if (regex != null && (regex.test(text) || regex.test(description))) {
                		return x;
            		}
        		}
        		return -1;
    		};
			for(i=0;i<cmp.instance.getBandCount();i++){
				cmp.instance.getBand(i).getEventPainter().setHighlightMatcher(highlightMatcher);
			}
			cmp.instance.paint();
			return true;		
		case "z.clearFilter":
			//alert("clearFilter");
			for(i=0;i<cmp.instance.getBandCount();i++){
				cmp.instance.getBand(i).getEventPainter().setFilterMatcher(null);
			}
			cmp.instance.paint();
			return true;
		case "z.clearHighlight":
			for(i=0;i<cmp.instance.getBandCount();i++){
				cmp.instance.getBand(i).getEventPainter().setHighlightMatcher(null);
			}
			cmp.instance.paint();
			return true;
		case "z.select":
			var matchers=eval('('+value+')');
			
   			var ids = [];
     		for (var x = 0; x < matchers.length; x++) {
     			var input=matchers[x];
     			ids.push(input);
    		}
    		zkTimeline.select(cmp,ids);
			return true;		
				
		}
	}
	return false;

};
/////
zkTimeline.showLoadingMessage=function(uuid){
	var timeline=$e(uuid);
	timeline.instance.showLoadingMessage();
};
zkTimeline.hideLoadingMessage=function(uuid){
	var timeline=$e(uuid);
	timeline.instance.hideLoadingMessage();
};
////

zkBandInfo={};

zkBandInfo.init = function (cmp) {

	//get parent -->timeline
	var timeline=$e(getZKAttr(cmp,"pid"));
	//get bandInfos
	if(!timeline.bandInfos) 
		timeline.bandInfos=new Array();
	//get eventSource
	if(!timeline.eventSource) 
		timeline.eventSource=new Array();
	//wether show event text
	var showEventText=false;
	if(getZKAttr(cmp,"showEventText")=="true") 
		showEventText=true;
	//create bandinfo
	if(cmp.zones) {
		zkTimeline.invert(cmp.zones);
		cmp.bandinfo=timeline.bandInfos[timeline.bandInfos.length]=
    		Timeline.createHotZoneBandInfo({
    			zones: 			cmp.zones,
    			showEventText:	showEventText,
    			trackHeight:	parseFloat(getZKAttr(cmp,"trackHeight")),
    			trackGap:		parseFloat(getZKAttr(cmp,"trackGap")),    	
    			timeZone:		parseInt(getZKAttr(cmp,"timeZone")),
    			eventSource:    cmp.eventSource=new Timeline.DefaultEventSource(),
        		date:           getZKAttr(cmp,"date"),
        		width:          getZKAttr(cmp,"width"), 
        		intervalUnit:   parseInt(getZKAttr(cmp,"intervalUnit")), 
        		intervalPixels: parseInt(getZKAttr(cmp,"intervalPixels"))
    		});
	}else{
		cmp.bandinfo=timeline.bandInfos[timeline.bandInfos.length]=
    		Timeline.createBandInfo({
    			showEventText:	showEventText,
    			trackHeight:	parseFloat(getZKAttr(cmp,"trackHeight")),
    			trackGap:		parseFloat(getZKAttr(cmp,"trackGap")),    	
    			timeZone:		parseInt(getZKAttr(cmp,"timeZone")),
    			eventSource:    cmp.eventSource=new Timeline.DefaultEventSource(),
        		date:           getZKAttr(cmp,"date"),
        		width:          getZKAttr(cmp,"width"), 
        		intervalUnit:   parseInt(getZKAttr(cmp,"intervalUnit")), 
        		intervalPixels: parseInt(getZKAttr(cmp,"intervalPixels"))
    		});

	}
	//wether highlighting current zone
	var highlight=false;
	if(getZKAttr(cmp,"highlight")=="true") 
		highlight=true;
	//get syncWith band 
	var syncWith=getZKAttr(cmp,"syncWith");
	if(syncWith){
		cmp.bandinfo.syncWith=syncWith;
		cmp.bandinfo.highlight=highlight;
	}
	//setup eventSourceUrl for this band
	cmp.bandinfo.eventSourceUrl=getZKAttr(cmp,"eventSourceUrl");
	if(timeline.eventSources==null) timeline.eventSources={};
	if(cmp.bandinfo.eventSourceUrl) timeline.eventSources[cmp.bandinfo.eventSourceUrl]=cmp.bandinfo.eventSourceUrl;
	
	cmp.bandinfo.uuid=cmp.id;
	
};
//////
zkBandInfo.loadXML = function(timeline,band,url){
	Timeline.loadXML(url,
		function(xml, url) {
			band.bandinfo.eventSource.loadXML(xml,url);
		});
};

/** Called by the server to set the attribute. */
zkBandInfo.setAttr = function (cmp, name, value) {
		//alert(name+"="+value);
		var timeline=$e(getZKAttr(cmp,"pid"));
		if(timeline.instance){
			switch (name) {
			case "z.width":
				setZKAttr(cmp,"width",value);
				return true;
			case "z.date":
				setZKAttr(cmp,"date",value);
				return true;
			case "z.showEventText":
				setZKAttr(cmp,"showEventText",value);
				return true;
			case "z.intervalUnit":
				setZKAttr(cmp,"intervalUnit",value);
				return true;
			case "z.intervalPixels":
				setZKAttr(cmp,"intervalPixels",value);
				return true;
			case "z.trackHeight":
				setZKAttr(cmp,"trackHeight",value);
				return true;
			case "z.trackGap":
				setZKAttr(cmp,"trackGap",value);
				return true;
			case "z.highlight":
				setZKAttr(cmp,"highlight",value);
				return true;
			case "z.syncWith":
				setZKAttr(cmp,"syncWith",value);
				return true;
			case "z.timeZone":
				setZKAttr(cmp,"timeZone",value);
				return true;
			case "z.removeHighlightDecorator":
				var params=eval('('+value+')');
					for(var d in cmp.bandinfo.decorators){
						if(d._id==params.id) 
							cmp.bandinfo.decorators.remove(d);
					}
					timeline.instance.paint();
				return true;
			case "z.eventSourceUrl":
				timeline.instance.showLoadingMessage();
				cmp.bandinfo.eventSourceUrl=value;
				if(timeline.eventSources==null) timeline.eventSources={};
				if(cmp.bandinfo.eventSourceUrl) timeline.eventSources[cmp.bandinfo.eventSourceUrl]=cmp.bandinfo.eventSourceUrl;
				cmp.bandinfo.eventSource.clear();
				if(cmp.dynaEvents!=null){//add dynamic event to event source 
					for(i=0;i<cmp.dynaEvents.length;i++){
						var e=cmp.dynaEvents[i];
				    	cmp.bandinfo.eventSource.add(e);
   						if( cmp.bandinfo.eventPainter.getLayout()._tracks==null)
   							cmp.bandinfo.eventPainter.getLayout()._tracks=[];//for IE
    					
					}
					cmp.bandinfo.eventPainter.getLayout()._layout();
				}
				zkBandInfo.loadXML(timeline,cmp,value);
				timeline.instance.hideLoadingMessage();
				return true;
			}//end switch
		}//end if
	return false;

};
//////
zkBandInfo.scrollToCenter=function(uuid,dateString){
	var band=$e(uuid);
	var timeline=$e(getZKAttr(band,"pid"));
	var currentDate=new Date(Date.parse(dateString));
	timeline.instance.getBand(band.bandinfo.bandIndex).scrollToCenter(currentDate);
};
//////
zkBandInfo.addOccurEvent=function(uuid,params){
	var band=$e(uuid);
	var timeline=$e(getZKAttr(band,"pid"));

	var p=[];
	p[0]=params;
	zkBandInfo.addManyOccurEvent(uuid,p);

};
/////
zkBandInfo.removeOccurEvent=function(uuid,eventId){
	var band=$e(uuid);
	var timeline=$e(getZKAttr(band,"pid"));

	if(band.bandinfo.eventSource._events==null) return;

	var iter=band.bandinfo.eventSource._events.getAllIterator();
 	while(iter.hasNext()){
 		var e=iter.next();
 		if(e==null) continue;
 		if(e._id==eventId){
 			band.bandinfo.eventSource._events._events.remove(e);
 		}
 	}

	timeline.instance.paint();

};
zkBandInfo.parseDateTime=function(dateString){
	if(dateString==null) return null;
    try {
        return new Date(Date.parse(dateString));
    } catch (e) {
    	
        return null;
    }
}
//////new a event
zkBandInfo.newEvent=function(cmp,params){
		var evt=new Timeline.DefaultEventSource.Event(
			zkBandInfo.parseDateTime(params.start),
			zkBandInfo.parseDateTime(params.end),
			zkBandInfo.parseDateTime(params.latestStart),
			zkBandInfo.parseDateTime(params.earliestEnd),
			params.duration,
			params.text,
			params.description,
			cmp.eventSource._resolveRelativeURL(params.image, ""),
			cmp.eventSource._resolveRelativeURL(params.link, ""),
			cmp.eventSource._resolveRelativeURL(params.icon, ""),
			params.color,
			params.textColor
		);
	evt._id=params.id;

	return evt;
};
//add a event to band
zkBandInfo.addManyOccurEvent=function(uuid,data){
	var band=$e(uuid);
	var timeline=$e(getZKAttr(band,"pid"));
	if(data.length==0) return;
	timeline.instance.showLoadingMessage();
    var events=[];
    if(band.dynaEvents==null) band.dynaEvents=[];
    for(i=0;i<data.length;i++){
		var evt=zkBandInfo.newEvent(band,data[i]);
    	band.dynaEvents[band.dynaEvents.length]=evt;
    	events[events.length]=evt;
	}

    band.bandinfo.eventSource.addMany(events);
   	if( band.bandinfo.eventPainter.getLayout()._tracks==null)
   		band.bandinfo.eventPainter.getLayout()._tracks=[];//for IE
    band.bandinfo.eventPainter.getLayout()._layout();
    timeline.instance.hideLoadingMessage();
	//return evt;
};
//////
zkBandInfo.addHighlightDecorator=function(uuid,params){
	var band=$e(uuid);
	var timeline=$e(getZKAttr(band,"pid"));

	if(band.bandinfo.decorators==null) {
		band.bandinfo.decorators=[];
		timeline.instance.getBand(band.bandinfo.bandIndex)._decorators=band.bandinfo.decorators;
	}
	if(params.HighlightDecoratorName=="SpanHighlightDecorator"){
		var shd=new Timeline.SpanHighlightDecorator({
			startDate:			params.startDate,
			endDate:			params.endDate,
			color:				params.color,
			opacity:			parseInt(params.opacity),
			startLabel:			params.startLabel,
			endLabel:			params.endLabel,
			theme:				Timeline.getDefaultTheme()
		});

		band.bandinfo.decorators[band.bandinfo.decorators.length]=shd;
		shd.initialize(timeline.instance.getBand(band.bandinfo.bandIndex),timeline.instance);
		shd._id=params.id;
		shd.paint();

	}else{
		var phd= new Timeline.PointHighlightDecorator({
			date:				params.date,
			color:				params.color,
			opacity:			parseInt(params.opacity),
			theme:				Timeline.getDefaultTheme()
			});
		band.bandinfo.decorators[band.bandinfo.decorators.length]=phd;
		phd.initialize(timeline.instance.getBand(band.bandinfo.bandIndex),timeline.instance);
		phd._id=params.id;
		phd.paint();

	}//end if


};

////////
zkBandInfo.removeHighlightDecorator=function(uuid,decoratorId){
	var band=$e(uuid);
	var timeline=$e(getZKAttr(band,"pid"));
	if(band.bandinfo.decorators==null) return;
	for(i=0;i<band.bandinfo.decorators.length;i++){
		var d=band.bandinfo.decorators[i];
		if(d._id==decoratorId){ 
			band.bandinfo.decorators.remove(d);
			timeline.instance.getBand(band.bandinfo.bandIndex).removeLayerDiv(d._layerDiv);
		}
	}
	timeline.instance.getBand(band.bandinfo.bandIndex).paint();

};


////
zkHotZone={};

zkHotZone.init = function(cmp){

	var bandinfo=$e(getZKAttr(cmp,"pid"));
	if(!bandinfo.zones) 
		bandinfo.zones=new Array();
	
	bandinfo.zones[bandinfo.zones.length]={
		start:		getZKAttr(cmp,"start"),
		end:		getZKAttr(cmp,"end"),
		magnify:	parseInt(getZKAttr(cmp,"magnify")),
		unit:		parseInt(getZKAttr(cmp,"unit")),
		multiple:	parseInt(getZKAttr(cmp,"multiple"))
	};
	
};

/** Called by the server to set the attribute. */
zkHotZone.setAttr = function (cmp, name, value) {

		switch (name) {
			case "z.start":
			 	setZKAttr(cmp,"start",value);
				return true;
			case "z.end":
				setZKAttr(cmp,"end",value);
				return true;
			case "z.unit":
				setZKAttr(cmp,"unit",value);
				return true;
			case "z.magnify":
				setZKAttr(cmp,"magnify",value);
				return true;
			case "z.multiple":
				setZKAttr(cmp,"multiple",value);
				return true;
				
		}
	return false;

};


zkTimeline.initTimeline = function(cmp) {
//// Modifications to timeline to support selection
	Timeline._Band.prototype._onShiftClick = Timeline._Band.prototype._onMouseDown;
	Timeline._Band.prototype._onMouseDown = function(innerFrame, evt, target) {
	    this.closeBubble();
	    if (evt.shiftKey) {
	    	this._onShiftClick(innerFrame, evt, target);
	    }
	};

	Timeline.DurationEventPainter.prototype._onShiftClickInstantEvent = Timeline.DurationEventPainter.prototype._onClickInstantEvent;
	Timeline.DurationEventPainter.prototype._onShiftClickDurationEvent = Timeline.DurationEventPainter.prototype._onClickDurationEvent;
	Timeline.DurationEventPainter.prototype._onClickOverride = function(duration, icon, domEvt, evt) {
    	var tl = this._timeline;
    	var id = evt._id;
    	var sel = new Array();
    	domEvt.cancelBubble = true;
    	if (domEvt.altKey || domEvt.metaKey || domEvt.ctrlKey) {
    		var prev = tl.selection;
    		var selected = false;
    		if (prev != null) {
	    		for (i = 0 ; i < prev.length ; i++) {
	    			if (prev[i] != id) {
	    				sel.push(prev[i]);
	    			} else {
	    				selected = true;
	    			}
	    		}
    		}
    		if (!selected) {
    			sel.push(id);
    		}
    	} else {
    		sel.push(evt._id);
    		if (domEvt.shiftKey) {
    			if (duration) {
    				this._onShiftClickDurationEvent(icon, domEvt, evt);
    			} else {
    				this._onShiftClickInstantEvent(icon, domEvt, evt);
    			}
    		}
    	}
    	zkTimeline.select(cmp, sel);
	};
	Timeline.DurationEventPainter.prototype._onClickInstantEvent = function(icon, domEvt, evt) {
		this._onClickOverride(false, icon, domEvt, evt);
	};
	Timeline.DurationEventPainter.prototype._onClickDurationEvent = function(icon, domEvt, evt) {
		this._onClickOverride(true, icon, domEvt, evt);
	};

};

zkTimeline.select = function(cmp, ids) {
	var timeline = cmp.instance;
	timeline.selection = ids;
	var highlightMatcher = function(evt) {
		var id = evt._id
		for (var x = 0; x < ids.length; x++) {
    		if (ids[x] != null && ids[x]==id) {
        		return 0;
    		}
		}
		return -1;
	};
	for(i=0;i<timeline.getBandCount();i++){
		timeline.getBand(i).getEventPainter().setHighlightMatcher(highlightMatcher);
	}
	timeline.paint();
	zkau.send({uuid:cmp.id,cmd:"onSelectEvent",data:ids},5);
	return true;	
};

zkTimeline.clearSelection = function(cmp) {
	this.select(cmp, new Array());
}
