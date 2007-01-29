/**
 * This file is licensed under the agreement you accepted during installation 
 * of this software package. If you did not agree to any license, you cannot 
 * use, copy or re-distribute this software and source file unless you have 
 * written permission from JGraph Ltd stating otherwise.
 */

var mxClient={
VERSION:'0.9.9.8',
IS_IE:navigator.appName.toUpperCase()=='MICROSOFT INTERNET EXPLORER',
IS_IE7:navigator.appName.toUpperCase()=='MICROSOFT INTERNET EXPLORER'&&navigator.userAgent.indexOf('MSIE 7'),
IS_NS:navigator.appName=='Netscape',
IS_FF2:navigator.userAgent.indexOf('Firefox/2')>=0,
IS_OP:navigator.appName=='Opera',
IS_SVG:navigator.userAgent.indexOf('Firefox/1.5')>=0||navigator.userAgent.indexOf('Firefox/2')>=0||navigator.userAgent.indexOf('Minefield/3')>=0||navigator.userAgent.indexOf('Camino/1')>=0||navigator.userAgent.indexOf('Opera/9.0')>=0,
IS_VML:navigator.appName.toUpperCase()=='MICROSOFT INTERNET EXPLORER',
IS_CANVAS:navigator.appName=='Netscape',
IS_MAC:navigator.userAgent.toUpperCase().indexOf('MACINTOSH')>0,
IS_FADE_MENU:false,
IS_FADE_WINDOW:false,
IS_FADE_RUBBERBAND:false,
IS_LOCAL:document.location.href.indexOf('http://')!=0&&document.location.href.indexOf('https://')!=0,
isBrowserSupported:function(){
return mxClient.IS_VML||mxClient.IS_SVG;
},
link:function(rel,href,doc){
doc=doc||document;
var head=doc.getElementsByTagName('head')[0];
var link=doc.createElement('link');
link.setAttribute('rel',rel);
link.setAttribute('href',href);
link.setAttribute('charset','ISO-8859-1');
link.setAttribute('type','text/css');
head.appendChild(link);
},
include:function(src,type){
type=type||'text/javascript';
if(mxClient.IS_IE){
document.write('<script src="'+src+'"></script>');
}else{
var head=document.getElementsByTagName('head')[0];
var script=document.createElement('script');
script.setAttribute('type',type);
script.setAttribute('src',src);
head.appendChild(script);
}
}
}
mxClient.basePath=(typeof(mxBasePath)!='undefined')?mxBasePath:'';
mxClient.imageBasePath=mxClient.basePath+'images/';
if(typeof(mxLanguage)!='undefined')
{
mxClient.language=mxLanguage;
}
else
{
mxClient.language=(mxClient.IS_IE)?navigator.userLanguage:navigator.language;
var dash=mxClient.language.indexOf('-');
if(dash>0)
{
mxClient.language=mxClient.language.substring(0,dash);
}
}
mxClient.link('stylesheet',mxClient.basePath+'css/common.css');
if(mxClient.IS_IE){
mxClient.link('stylesheet',mxClient.basePath+'css/explorer.css');
}
if(mxClient.IS_NS){
}

var mxLog={
TRACE:false,
DEBUG:true,
WARN:true,
buffer:'',
init:function(){
if(mxLog.window==null){
var title=mxResources.get('console')+' - mxGraph '+mxClient.VERSION;
mxLog.div=document.createElement('div');
mxLog.textarea=document.createElement('textarea');
mxLog.textarea.setAttribute('cols','48');
mxLog.textarea.setAttribute('rows','6');
mxLog.textarea.setAttribute('readOnly','true');
mxLog.textarea.value=mxLog.buffer;
mxLog.div.appendChild(mxLog.textarea);
mxUtils.br(mxLog.div);


mxLog.addButton('Info',function(evt){
mxLog.writeln(mxUtils.toString(navigator));
});
mxLog.addButton('DOM',function(evt){
var content=mxUtils.getInnerHtml(document.body);
mxLog.debug(content);
});
mxLog.addButton('Trace',function(evt){
mxLog.TRACE=!mxLog.TRACE;
if(mxLog.TRACE){
mxLog.debug('Tracing enabled');
}else{
mxLog.debug('Tracing disabled');
}
});
mxLog.addButton('Copy',function(evt){
try{
mxUtils.copy(mxLog.textarea.value);
}catch(err){
alert(err);
}
});
mxLog.addButton('Show',function(evt){
try{
mxUtils.popup(mxLog.textarea.value);
}catch(err){
alert(err);
}
});
mxLog.addButton('Clear',function(evt){
mxLog.textarea.value='';
});
var w=document.body.clientWidth;
var h=document.body.clientHeight;
mxLog.window=new mxWindow(title,mxLog.div,w-320,h-210 ,300 );
mxLog.window.setCloseAction(function(){
mxLog.window.setVisible(false);
});
}
},
addButton:function(text,funct){
var button=document.createElement('button');
mxUtils.write(button,text);
mxEvent.addListener(button,'click',funct);
mxLog.div.appendChild(button);
},
isVisible:function(){
if(mxLog.window!=null){
return mxLog.window.isVisible();
}
return false;
},
setVisible:function(visible){
if(mxLog.window==null){
mxLog.init();
}
if(mxLog.window!=null){
mxLog.window.setVisible(visible);
}
},
enter:function(string){
if(mxLog.TRACE){
mxLog.writeln('Entering '+string);
return new Date().getTime();
}
},
leave:function(string,t0){
if(mxLog.TRACE){
var dt=(t0!=0)?' ('+(new Date().getTime()-t0)+' ms)':'';
mxLog.writeln('Leaving '+string+dt);
}
},
debug:function(string){
if(mxLog.DEBUG){
mxLog.writeln(string);
}
},
warn:function(string){
if(mxLog.WARN){
mxLog.writeln(string);
}
},
write:function(string){
if(mxLog.textarea!=null){
mxLog.textarea.value=mxLog.textarea.value+string;
mxLog.textarea.scrollTop=mxLog.textarea.scrollHeight;
}else{
mxLog.buffer+=string;
}
},
writeln:function(string){
mxLog.write(string+'\n');
}
}

var mxResources={
resources:new Array(),
add:function(basename){
var lan=mxClient.language;
try{
var req=mxUtils.load(basename+'.properties');
if(req.isReady()){
mxResources.parse(req.request.responseText);
}
}catch(e){

}
try{
var req=mxUtils.load(basename+'_'+lan+'.properties');
if(req.isReady()){
mxResources.parse(req.request.responseText);
}
}catch(e){

}
},
parse:function(text){
var lines=text.split('\n');
for(var i=0;i<lines.length;i++){
var index=lines[i].indexOf('=');
if(index>0){
var key=lines[i].substring(0,index);
var idx=lines[i].length;
if(lines[i].charCodeAt(idx-1)==13){
idx--;
}
var value=lines[i].substring(index+1,idx);
mxResources.resources[key]=unescape(value);
}
}
},
get:function(key){
return mxResources.resources[key];
}
}

{
function mxPoint(x,y)
{
this.x=(x!=null)?x:0;
this.y=(y!=null)?y:0;
}
mxPoint.prototype.x=null;
mxPoint.prototype.y=null;
mxPoint.prototype.clone=function()
{
return new mxPoint(this.x,this.y);
}
}

{
function mxRectangle(x,y,width,height)
{
this.x=(x!=null)?x:0;
this.y=(y!=null)?y:0;
this.width=(width!=null)?width:0;
this.height=(height!=null)?height:0;
}
mxRectangle.prototype.x=null;
mxRectangle.prototype.y=null;
mxRectangle.prototype.width=null;
mxRectangle.prototype.height=null;
mxRectangle.prototype.clone=function(){
return new mxRectangle(this.x,this.y,this.width,this.height);
}
}

var mxUtils=
{
errorImage:mxClient.imageBasePath+'error.gif',
eval:function(expr)
{
var result=null;
if(expr.indexOf('function')>=0&&(mxClient.IS_IE||mxClient.IS_OP))
{
eval('var f='+expr);
result=f;
}
else
{
result=eval(expr);
}
return result;
},
selectSingleNode:function(doc,xpath)
{
if(mxClient.IS_IE)
{
return doc.selectSingleNode(xpath);
}
else
{
var result=doc.evaluate(xpath,doc,null,XPathResult.ANY_TYPE,null);
return result.iterateNext();
}
},
getFunctionName:function(f)
{
var str=null;
if(f!=null)
{
if(mxClient.IS_NS)
{
str=f.name;
}
else
{
var tmp=f.toString();
var idx1=9;
while(tmp.charAt(idx1)==' ')
{
idx1++;
}
var idx2=tmp.indexOf('(',idx1);
str=tmp.substring(idx1,idx2);
}
}
return str;
},
indexOf:function(array,obj)
{
if(array!=null&&obj!=null)
{
for(var i=0;i<array.length;i++)
{
if(array[i]==obj)
{
return i;
}
}
}
return-1;
},
getChildNodes:function(node,nodeType)
{
nodeType=nodeType||1;
var children=new Array();
var tmp=node.firstChild;
while(tmp!=null)
{
if(tmp.nodeType==nodeType)
{
children.push(tmp);
}
tmp=tmp.nextSibling;
}
return children;
},
createXmlDocument:function()
{
var doc=null;
if(document.implementation&&document.implementation.createDocument)
{
doc=document.implementation.createDocument("","",null);
}
else if(window.ActiveXObject)
{
doc=new ActiveXObject("Microsoft.XMLDOM");
}
return doc;
},
parseXml:function(xml)
{
var result=null;
if(mxClient.IS_IE)
{
result=mxUtils.createXmlDocument();
result.async="false";
result.loadXML(xml)
}
else
{
var parser=new DOMParser();
result=parser.parseFromString(xml,"text/xml");
}
return result;
},
getPrettyXml:function(node,tab,indent)
{
var result='';
if(node!=null)
{
tab=tab||'  ';
indent=indent||'';
if(node.nodeType==3)
{
result+=node.nodeValue;
}
else
{
result+=indent+'<'+node.nodeName;
var attrs=node.attributes;
if(attrs!=null)
{
for(var i=0;i<attrs.length;i++)
{
var val=mxUtils.htmlEntities(attrs[i].nodeValue);
result+=' '+attrs[i].nodeName+'="'+val+'"';
}
}
var tmp=node.firstChild;
if(tmp!=null)
{
result+='>\n';
while(tmp!=null)
{
result+=mxUtils.getPrettyXml(tmp,tab,indent+tab);
tmp=tmp.nextSibling;
}
result+=indent+'</'+node.nodeName+'>\n';
}
else
{
result+='/>\n';
}
}
}
return result;
},
htmlEntities:function(s)
{
s=s.replace(/</g,'&lt;');
s=s.replace(/>/g,'&gt;');
s=s.replace(/\n/g,'&#xa;');
s=s.replace(/"/g,'&quot;');
return s;
},
getXml:function(node,linefeed)
{
var xml='';
if(node!=null)
{
xml=node.xml;
if(xml==null)
{
if(mxClient.IS_IE)
{
xml=node.innerHTML;
}
else
{
var xmlSerializer=new XMLSerializer();
xml=xmlSerializer.serializeToString(node);
}
}
else
{
xml=xml.replace(/\r\n\t[\t]*/g,'').replace(/>\r\n/g,'>').replace(/\r\n/g,'\n');
}
}
linefeed=linefeed||'&#xa;';
xml=xml.replace(/\n/g,linefeed);
return xml;
},
getTextContent:function(node)
{
if(node!=null)
{
if(mxClient.IS_IE)
{
if(node.firstChild!=null)
{
return node.firstChild.nodeValue;
}
}
else
{
if(node.textContent!=null)
{
return node.textContent;
}
}
}
return '';
},
getInnerHtml:function(node)
{
if(node!=null)
{
if(mxClient.IS_IE)
{
return node.innerHTML;
}
else
{
var serializer=new XMLSerializer();
return serializer.serializeToString(node);
}
}
return '';
},
write:function(parent,string)
{
var text=document.createTextNode(string);
if(parent!=null)
{
parent.appendChild(text);
}
return text;
},
writeln:function(parent,string)
{
var text=document.createTextNode(string);
if(parent!=null)
{
parent.appendChild(text);
parent.appendChild(document.createElement('br'));
}
return text;
},
br:function(parent)
{
var br=document.createElement('br');
if(parent!=null)
{
parent.appendChild(br);
}
return br;
},
para:function(parent,text)
{
var p=document.createElement('p');
mxUtils.write(p,text);
if(parent!=null)
{
parent.appendChild(p);
}
return p;
},
linkAction:function(parent,text,editor,action,pad)
{
var a=mxUtils.link(parent,text,function()
{
editor.execute(action)
},pad);
return a;
},
linkInvoke:function(parent,text,editor,functName,arg,pad)
{
var a=mxUtils.link(parent,text,function()
{
editor[functName](arg)
},pad);
return a;
},
link:function(parent,text,funct,pad)
{
var a=document.createElement('span');
a.style.color='blue';
a.style.textDecoration='underline';
a.style.cursor='pointer';
if(pad!=null)
{
a.style.paddingLeft=pad+'px';
}
mxEvent.addListener(a,'click',funct);
mxUtils.write(a,text);
if(parent!=null)
{
parent.appendChild(a);
}
return a;
},
open:function(filename)
{
if(mxClient.IS_NS)
{
try
{
var text='';
var filechar;
netscape.security.PrivilegeManager.enablePrivilege('UniversalFileAccess');
var file=new java.io.File(filename);
var FileReader=new java.io.FileReader(file);
filechar=FileReader.read();
while(filechar!=-1)
{
text=text+String.fromCharCode(filechar);
filechar=FileReader.read();
}
FileReader.close();
return text;
}
catch(err)
{
alert('Access denied: '+err);
return
}
}
else
{
var activeXObject=new ActiveXObject("Scripting.FileSystemObject");
var newStream=activeXObject.OpenTextFile(filename,1);
var text=newStream.readAll();
newStream.close();
return text;
}
return null;
},
save:function(filename,content)
{

var iframe=document.createElement('iframe');
iframe.setAttribute('src','');
iframe.setAttribute('visibility','hidden');
iframe.style.display='none';
document.body.appendChild(iframe);
try
{

if(mxClient.IS_NS)
{
var doc=iframe.contentDocument;
doc.open();
doc.write(content);
doc.close();
netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
saveDocument(doc);
}
else
{
var doc=iframe.contentWindow.document;
doc.write(content);
doc.execCommand('SaveAs',false,filename);
}
}
finally
{
document.body.removeChild(iframe);
}
},
copy:function(content)
{
if(window.clipboardData)
{
window.clipboardData.setData("Text",content);
}
else if(window.netscape)
{
netscape.security.PrivilegeManager.enablePrivilege('UniversalXPConnect');
var clip=Components.classes['@mozilla.org/widget/clipboard;1'].createInstance(Components.interfaces.nsIClipboard);
if(!clip)
{
return;
}
var trans=Components.classes['@mozilla.org/widget/transferable;1'].createInstance(Components.interfaces.nsITransferable);
if(!trans)
{
return;
}
trans.addDataFlavor('text/unicode');
var str=new Object();
var len=new Object();
var str=Components.classes["@mozilla.org/supports-string;1"].createInstance(Components.interfaces.nsISupportsString);
var copytext=content;
str.data=copytext;
trans.setTransferData("text/unicode",str,copytext.length*2);
var clipid=Components.interfaces.nsIClipboard;
if(!clip)
{
return false;
}
clip.setData(trans,null,clipid.kGlobalClipboard);
}
},
load:function(url)
{
var req=new mxXmlRequest(url,null,'GET',false);
req.send();
return req;
},
get:function(url,onload,onerror)
{
new mxXmlRequest(url,null,'GET').send(onload,onerror);
},
post:function(url,params,onload,onerror)
{
new mxXmlRequest(url,params).send(onload,onerror);
},
submit:function(url,params)
{
new mxXmlRequest(url,params).simulate();
},
loadInto:function(url,doc,onload)
{
if(mxClient.IS_IE)
{
doc.onreadystatechange=function()
{


if(doc.readyState==4) onload()
};
}
else
{
doc.addEventListener("load",onload,false);

}
doc.load(url);
},
clone:function(obj,transients)
{
var clone=null;
if(obj!=null&&typeof(obj.constructor)=='function')
{
clone=new obj.constructor();
for(var i in obj)
{
if(transients==null||mxUtils.indexOf(transients,i)<0)
{
if(typeof(obj[i])=='object')
{
clone[i]=mxUtils.clone(obj[i]);
}
else
{
clone[i]=obj[i];
}
}
}
}
return clone;
},
equals:function(a,b)
{
return b!=null&&((a.x==null||a.x==b.x)&&(a.y==null||a.y==b.y)&&(a.width==null||a.width==b.width)&&(a.height==null||a.height==b.height));
},
toString:function(obj)
{
var output='';
for(var i in obj)
{
if(obj[i]==null)
{
output+=i+' = [null]\n';
}
else if(typeof(obj[i])=='function')
{
output+=i+' => [Function]\n';
}
else if(typeof(obj[i])=='object')
{
output+=i+' => [Object]\n';
}
else
{
output+=i+' = '+obj[i]+'\n';
}
}
return output;
},
contains:function(bounds,x,y)
{
return(bounds.x<=x&&bounds.x+bounds.width>=x&&bounds.y<=y&&bounds.y+bounds.height>=y);
},
intersects:function(first,second)
{
return mxUtils.contains(first,second.x,second.y)||mxUtils.contains(first,second.x+second.width,second.y+second.height);
},
getOffset:function(container)
{
var offsetLeft=0;
var offsetTop=0;
if(container.offsetParent)
{
while(container.offsetParent)
{
offsetLeft+=container.offsetLeft
offsetTop+=container.offsetTop
container=container.offsetParent;
}
}
return{x:offsetLeft,y:offsetTop};
},
convertPoint:function(container,x,y)
{
var offset=mxUtils.getOffset(container);
offset.x-=document.body.scrollLeft;
offset.y-=document.body.scrollTop;
return{x:x-offset.x,y:y-offset.y};
},
isNumeric:function(str)
{
return str!=null&&(str.length==null||str.length>0)&&!isNaN(str);
},
intersection:function(x0,y0,x1,y1,x2,y2,x3,y3)
{

var m1=(y1-y0)/(x1-x0);
var b1=y0-m1*x0;
var m2=(y3-y2)/(x3-x2);
var b2=y2-m2*x2;
var x=(b1-b2)/(m2-m1);
var y=m1*x+b1;
return new mxPoint(x,y);
},
morph:function(graph,cells,dx,dy,step,delay)
{
step=step||30;
delay=delay||30;
var current=0;
var f=function()
{
var model=graph.getModel();
current=Math.min(100,current+step);
for(var i=0;i<cells.length;i++)
{
if(!model.isEdge(!cells[i]))
{
var state=graph.getCellBounds(cells[i]);
state.x+=step*dx/100;
state.y+=step*dy/100;
graph.cellRenderer.redraw(state);
}
}
if(current<100)
{
window.setTimeout(f, delay);
}
else
{
graph.move(cells,dx,dy);
}
};
window.setTimeout(f, delay);
},
fadeIn:function(node,to,step,delay,isEnabled)
{
to=(to!=null)?to:100;
step=step||40;
delay=delay||30;
var opacity=0;
mxUtils.setOpacity(node,opacity);
node.style.display='inline';
if(isEnabled||isEnabled==null)
{
var f=function()
{
opacity=Math.min(opacity+step,to);
mxUtils.setOpacity(node,opacity);
if(opacity<to)
{
window.setTimeout(f, delay);
}
};
window.setTimeout(f, delay);
}
else
{
mxUtils.setOpacity(node,to);
}
},
fadeOut:function(node,from,remove,step,delay,isEnabled)
{
step=step||40;
delay=delay||30;
var opacity=from||100;
mxUtils.setOpacity(node,opacity);
if(isEnabled||isEnabled==null)
{
var f=function()
{
opacity=Math.max(opacity-step,0);
mxUtils.setOpacity(node,opacity);
if(opacity>0)
{
window.setTimeout(f, delay);
}
else
{
node.style.display='none';
if(remove&&node.parentNode)
{
node.parentNode.removeChild(node);
}
}
};
window.setTimeout(f, delay);
}
else
{
node.style.display='none';
if(remove&&node.parentNode)
{
node.parentNode.removeChild(node);
}
}
},
setOpacity:function(node,value)
{
if(mxClient.IS_IE)
{
node.style.filter="alpha(opacity="+value+")";
}
else
{
node.style.opacity=(value/100);
}
},
createImage:function(src)
{
var imgName=src.toUpperCase()
var imageNode=null;
if(imgName.substring(imgName.length-3,imgName.length).toUpperCase()=="PNG"&&mxClient.IS_IE&&!mxClient.IS_IE7)
{
imageNode=document.createElement('DIV');
imageNode.style.filter='progid:DXImageTransform.Microsoft.AlphaImageLoader (src=\''+src+'\', sizingMethod=\'scale\')';
}
else
{
imageNode=document.createElement('image');
imageNode.setAttribute('src',src);
}
return imageNode;
},
getStylename:function(style)
{
var pairs=style.split(';');
var stylename=pairs[0];
if(stylename.indexOf('=')<0)
{
return stylename;
}
},
setCellStyles:function(model,cells,key,value)
{
if(cells!=null&&cells.length>0)
{
model.beginUpdate();
try
{
for(var i=0;i<cells.length;i++)
{
var style=mxUtils.setStyle(cells[i].getStyle(),key,value);
model.setStyle(cells[i],style);
}
}
finally
{
model.endUpdate();
}
}
},
setStyle:function(style,key,value)
{
if(style==null||style.length==0)
{
return key+'='+value;
}
else
{
var index=style.indexOf(key+'=');
if(index<0)
{
var sep=(style.charAt(style.length-1)==';')?'':';';
return style+sep+key+'='+value;
}
else
{
var cont=style.indexOf(';',index);
if(cont<0)
{
return style.substring(0,index)+key+'='+value;
}
else
{
return style.substring(0,index)+key+'='+value+style.substring(cont);
}
}
}
},
setCellStyleFlags:function(model,cells,key,flag,value)
{
if(cells!=null&&cells.length>0)
{
model.beginUpdate();
try
{
for(var i=0;i<cells.length;i++)
{
var style=mxUtils.setStyleFlag(cells[i].getStyle(),key,flag,value);
model.setStyle(cells[i],style);
}
}
finally
{
model.endUpdate();
}
}
},
setStyleFlag:function(style,key,flag,value)
{
if(style==null||style.length==0)
{
if(value||value==null)
{
return key+'='+flag;
}
else
{
return '';
}
}
else
{
var index=style.indexOf(key+'=');
if(index<0)
{
if(value||value==null)
{
var sep=(style.charAt(style.length-1)==';')?'':';';
return style+sep+key+'='+flag;
}
else
{
return style;
}
}
else
{
var cont=style.indexOf(';',index);
var tmp='';
if(cont<0)
{
tmp=style.substring(index+key.length+1);
}
else
{
tmp=style.substring(index+key.length+1,cont);
}
if(value==null)
{
tmp=parseInt(tmp)^flag;
}
else if(value)
{
tmp=parseInt(tmp)|flag;
}
else
{
tmp=parseInt(tmp)&~flag;
}
if(cont<0)
{
return style.substring(0,index)+key+'='+tmp;
}
else
{
return style.substring(0,index)+key+'='+tmp+style.substring(cont);
}
}
}
},
show:function(graph,doc)
{
if(doc==null)
{
var wnd=window.open();
doc=wnd.document;
}
else
{
doc.open();
}
doc.write('<html xmlns:v="urn:schemas-microsoft-com:vml">');
doc.write('<head>');
if(mxClient.IS_IE)
{
doc.write('<link rel="stylesheet" href="css/common.css" charset="ISO-8859-1" type="text/css"/>');
doc.write('<link rel="stylesheet" href="css/explorer.css" charset="ISO-8859-1" type="text/css"/>');
}
doc.write('</head>');
doc.write('<body>');
if(mxClient.IS_IE)
{
var tmp=mxUtils.getInnerHtml(graph.container);
doc.write(tmp);
}
else
{
var node=graph.view.getDrawPane().parentNode.parentNode;
doc.body.appendChild(node.cloneNode(true));
}
doc.write('</body>');
doc.write('</html>');
doc.close();
},
print:function(graph)
{
var wnd=window.open();
mxUtils.show(graph,wnd.document);
wnd.print();
wnd.close();
},
popup:function(content,isInternalWindow)
{
if(isInternalWindow)
{
var div=document.createElement('div');
div.style.overflow='scroll';
div.style.width='636px';
div.style.height='460px';
div.appendChild(document.createTextNode(content));
var w=document.body.clientWidth;
var h=document.body.clientHeight;
var wnd=new mxWindow('Popup Window',div,w/2-320,h/2-240,640,480,false,true);
wnd.setCloseAction(function()
{
wnd.destroy();
});
wnd.setVisible(true);
}
else
{
if(mxClient.IS_FF2)
{
var wnd=window.open();
wnd.document.write('<html><body><textarea style="height:100%;width:100%">'+content+'</textarea></body></html>');
wnd.document.close();
}
else
{
var wnd=window.open();
var node=wnd.document.createTextNode(content);
wnd.document.body.appendChild(node);
}
}
},
error:function(message,width,close,icon)
{
var div=document.createElement('div');
div.style.padding='20px';
var img=document.createElement('img');
img.setAttribute('src',icon||mxUtils.errorImage);
img.setAttribute('valign','bottom');
img.style.verticalAlign='middle';
div.appendChild(img);
div.appendChild(document.createTextNode('\u00a0'));
div.appendChild(document.createTextNode('\u00a0'));
div.appendChild(document.createTextNode('\u00a0'));
mxUtils.write(div,message);
var w=document.body.clientWidth;
var h=document.body.clientHeight;
var warn=new mxWindow(mxResources.get('error')||'Error',div,(w-width)/2,h/4,width,null,false,true);
if(close)
{
mxUtils.br(div);
var tmp=document.createElement('p');
var button=document.createElement('button');
if(mxClient.IS_IE)
{
button.style.cssText='float:right';
}
else
{
button.setAttribute('style','float:right');
}
mxEvent.addListener(button,'click',function(evt)
{
warn.destroy();
});
mxUtils.write(button,mxResources.get('close'));
tmp.appendChild(button);
div.appendChild(tmp);
mxUtils.br(div);
warn.setCloseAction(function()
{
warn.destroy();
});
}
warn.setVisible(true);
}
}

var mxConstants=
{
RAD_PER_DEG:0.0174532,
DEG_PER_RAD:57.2957795,
ACTIVE_REGION:0.3,
MIN_ACTIVE_REGION:8,
DIALECT_SVG:'svg',
DIALECT_VML:'vml',
DIALECT_MIXEDHTML:'mixedHtml',
DIALECT_PREFERHTML:'preferHtml',
DIALECT_STRICTHTML:'strictHtml',
NS_SVG:'http://www.w3.org/2000/svg',
NS_XLINK:'http://www.w3.org/1999/xlink',
SVG_SHADOWCOLOR:'gray',
SVG_SHADOWTRANSFORM:'translate(2 3)',
STYLE_PERIMETER:'perimeter',
STYLE_OPACITY:'opacity',
STYLE_FILLCOLOR:'fillColor',
STYLE_GRADIENTCOLOR:'gradientColor',
STYLE_STROKECOLOR:'strokeColor',
STYLE_SEPARATORCOLOR:'separatorColor',
STYLE_STROKEWIDTH:'strokeWidth',
STYLE_ALIGN:'align',
STYLE_VERTICAL_ALIGN:'verticalAlign',
STYLE_IMAGE_ALIGN:'imageAlign',
STYLE_IMAGE_VERTICAL_ALIGN:'imageVerticalAlign',
STYLE_IMAGE:'image',
STYLE_IMAGE_WIDTH:'imageWidth',
STYLE_IMAGE_HEIGHT:'imageHeight',
STYLE_INDICATOR_SHAPE:'indicatorShape',
STYLE_INDICATOR_IMAGE:'indicatorImage',
STYLE_INDICATOR_COLOR:'indicatorColor',
STYLE_INDICATOR_STROKECOLOR:'indicatorStrokeColor',
STYLE_INDICATOR_GRADIENTCOLOR:'indicatorGradientColor',
STYLE_INDICATOR_SPACING:'indicatorSpacing',
STYLE_INDICATOR_WIDTH:'indicatorWidth',
STYLE_INDICATOR_HEIGHT:'indicatorHeight',
STYLE_SHADOW:'shadow',
STYLE_ENDARROW:'endArrow',
STYLE_STARTARROW:'startArrow',
STYLE_ENDSIZE:'endSize',
STYLE_STARTSIZE:'startSize',
STYLE_DASHED:'dashed',
STYLE_ROUNDED:'rounded',
STYLE_PERIMETER_SPACING:'perimeterSpacing',
STYLE_SPACING:'spacing',
STYLE_SPACING_TOP:'spacingTop',
STYLE_SPACING_LEFT:'spacingLeft',
STYLE_SPACING_BOTTOM:'spacingBottom',
STYLE_SPACING_RIGHT:'spacingRight',
STYLE_HORIZONTAL:'horizontal',
STYLE_FONTCOLOR:'fontColor',
STYLE_FONTFAMILY:'fontFamily',
STYLE_FONTSIZE:'fontSize',
STYLE_FONTSTYLE:'fontStyle',
STYLE_SHAPE:'shape',
STYLE_EDGE:'edgeStyle',
FONT_BOLD:1,
FONT_ITALIC:2,
FONT_UNDERLINE:4,
FONT_SHADOW:8,
SHAPE_RECTANGLE:'rectangle',
SHAPE_ELLIPSE:'ellipse',
SHAPE_RHOMBUS:'rhombus',
SHAPE_LINE:'line',
SHAPE_IMAGE:'image',
SHAPE_ARROW:'arrow',
SHAPE_LABEL:'label',
SHAPE_CYLINDER:'cylinder',
SHAPE_SWIMLANE:'swimlane',
SHAPE_CONNECTOR:'connector',
SHAPE_ACTOR:'actor',
ARROW_CLASSIC:'classic',

ALIGN_LEFT:'left',
ALIGN_CENTER:'center',
ALIGN_RIGHT:'right',
ALIGN_TOP:'top',
ALIGN_MIDDLE:'middle',
ALIGN_BOTTOM:'bottom'
}

var mxDatatransfer={
setSourceFunction:function(funct){
mxDatatransfer.sourceFunction=funct;
},
consumeSourceFunction:function(graph,evt,cell){
if(mxDatatransfer.sourceFunction!=null&&graph.isEnabled()){
mxDatatransfer.sourceFunction(graph,evt,cell);
mxDatatransfer.sourceFunction=null;
}
}
}

var mxEvent={
addListener:function(element,eventName,funct)
{
if(element.attachEvent)
{
element.attachEvent("on"+eventName,funct);
}
else if(element.addEventListener)
{
element.addEventListener(eventName,funct,false);
}
},
getSource:function(evt)
{
return(evt.srcElement!=null)?evt.srcElement:evt.target;
},
isConsumed:function(evt)
{
return evt.isConsumed!=null&&evt.isConsumed;
},
isLeftMouseButton:function(evt)
{
return evt.button==(mxClient.IS_IE)?1:0;
},
isPopupTrigger:function(evt)
{
return evt.button==2;
},
isToggleSelection:function(evt)
{
return evt.ctrlKey;
},
consume:function(evt)
{
if(evt.preventDefault)
{
evt.stopPropagation();
evt.preventDefault();
}
else
{
evt.cancelBubble=true;
}
evt.returnValue=false;
evt.isConsumed=true;
}
}

{
function mxXmlRequest(url,params,method,async,username,password){
this.url=url;
this.params=params;
this.method=method||'POST';
this.async=(async!=null)?async:true;
this.username=username;
this.password=password;
}
mxXmlRequest.prototype.isReady=function(){
return this.request.readyState==4;
}
mxXmlRequest.prototype.getXML=function(){
var xml=this.request.responseXML;
if(xml==null||xml.documentElement==null){
xml=mxUtils.parseXml(this.request.responseText);
}
return xml;
}
mxXmlRequest.prototype.getText=function(){
return this.request.responseText;
}
mxXmlRequest.prototype.create=function(){
var req=null;
if(window.XMLHttpRequest){
req=new XMLHttpRequest();
}else if(typeof(ActiveXObject)!="undefined"){
req=new ActiveXObject("Microsoft.XMLHTTP");
}
return req;
}
mxXmlRequest.prototype.send=function(onload,onerror){
this.request=this.create();
if(this.request!=null){
var self=this;
this.request.onreadystatechange=function(){
if(self.isReady()){
if(onload!=null){
onload(self);
}
}
}
this.request.open(this.method,this.url,this.async,this.username,this.password);
if(this.params!=null){
this.request.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
}
this.request.send(this.params);
}
}
mxXmlRequest.prototype.simulate=function(doc,target){
doc=doc||document;
var form=doc.createElement('form');
form.setAttribute('method',this.method);
form.setAttribute('action',this.url);
if(target!=null){
form.setAttribute('target',target);
}
form.style.display='none';
var pars=(this.params.indexOf('&')>0)?this.params.split('&'):
this.params.split();
for(var i=0;i<pars.length;i++){
var pos=pars[i].indexOf('=');
if(pos>0){
var name=pars[i].substring(0,pos);
var value=pars[i].substring(pos+1);
var textarea=doc.createElement('textarea');
textarea.setAttribute('name',name);
value=value.replace(/\n/g,'&#xa;');
var content=doc.createTextNode(value);
textarea.appendChild(content);
form.appendChild(textarea);
}
}
doc.body.appendChild(form);
form.submit();
doc.body.removeChild(form);
}
}

var mxClipboard={
cells:null,
parents:null,
cut:function(graph){
mxClipboard.copy(graph);
graph.remove(graph.getSelectionCells());
},
copy:function(graph){
var cells=graph.getSelectionCells();
var model=graph.getModel();
mxClipboard.parents=new Array();
for(var i=0;i<cells.length;i++){
mxClipboard.parents.push(model.getParent(cells[i]));
}
mxClipboard.cells=graph.cloneCells(cells);
},
paste:function(graph){
var cells=mxClipboard.cells;
if(cells!=null){

var parents=mxClipboard.parents;
var parent=graph.getDefaultParent();
var model=graph.getModel();
model.beginUpdate();
try
{
for(var i=0;i<parents.length;i++){
var pstate=graph.view.getState(parents[i]);
if(pstate!=null){
var geo=model.getGeometry(cells[i]);
if(geo!=null){
geo.x-=pstate.origin.x;
geo.y-=pstate.origin.y;
}
var index=model.getChildCount(parents[i]);
model.add(parents[i],cells[i],index);
}else{
var geo=model.getGeometry(cells[i]);
if(geo!=null){
var tmp=graph.getSwimlaneAt(geo.x+10,geo.y+10);
if(tmp!=null){
parent=tmp;
}
}
var index=model.getChildCount(parent);
model.add(parent,cells[i],index);
}
}
graph.move(cells,10,10);
}
finally
{
model.endUpdate();
}
graph.setSelectionCells(cells);
mxClipboard.cells=graph.cloneCells(cells);
}
}
}

{
function mxWindow(title,content,x,y,width,height,isMinimize,isMovable,replaceNode,style){
style=(style!=null)?style:'mxWindow';
isMinimize=(isMinimize!=null)?isMinimize:true;
isMovable=(isMovable!=null)?isMovable:true;
this.visible=false;
this.div=document.createElement('div');
this.div.className=style;
this.div.style.left=x+'px';
this.div.style.top=y+'px';
this.div.style.width=width+'px';
if(height!=null){
this.div.style.height=height+'px';
}
this.title=document.createElement('div');
this.title.className=style+'Title';
var self=this;
if(isMinimize){
var img=document.createElement('img');
img.setAttribute('src',this.minimizeImage);
img.align='right';
img.style.cursor='default';
img.setAttribute('title','Minimize');
this.title.appendChild(img);
var funct=function(evt){
if(content.style.display=='none'){
img.setAttribute('src',self.minimizeImage);
content.style.display='inline';
if(height!=null){
self.div.style.height=height+'px';
}
}else{
img.setAttribute('src',self.normalizeImage);
content.style.display='none';
self.oldHeight=content.offsetHeight;
if(height!=null){
self.div.style.height=self.title.offsetHeight+'px';
}
}
mxEvent.consume(evt);
}
mxEvent.addListener(img,'mousedown',funct);
}
mxUtils.write(this.title,title);
this.div.appendChild(this.title);
if(isMovable){
this.title.style.cursor='move';
mxEvent.addListener(this.title,'mousedown',function(evt){
self.startX=evt.clientX;
self.startY=evt.clientY;
if(mxClient.IS_IE||mxClient.IS_OP){
var interceptor=document.createElement('div');
interceptor.style.background='url(\''+mxClient.imageBasePath+'images/transparent.gif\')';
interceptor.style.zIndex=3;
interceptor.style.position='absolute';
interceptor.style.left=document.body.offsetTop+'px';
interceptor.style.top=document.body.offsetLeft+'px';
interceptor.style.width=(document.body.offsetWidth-20)+'px';
interceptor.style.height=(document.body.offsetHeight-10)+'px';
mxEvent.addListener(interceptor,'mousemove',function(evt){
var dx=evt.clientX-self.startX;
var dy=evt.clientY-self.startY;
self.div.style.left=(x+dx)+'px';
self.div.style.top=(y+dy)+'px';
});
mxEvent.addListener(interceptor,'mouseup',function(evt){
document.body.removeChild(interceptor);
x=parseInt(self.div.style.left);
y=parseInt(self.div.style.top);
});
document.body.appendChild(interceptor);
}
});
if(!mxClient.IS_IE&&!mxClient.IS_OP){
mxEvent.addListener(this.div,'mousemove',function(evt){
if(self.startX>0){
var dx=evt.clientX-self.startX;
var dy=evt.clientY-self.startY;
self.div.style.left=(x+dx)+'px';
self.div.style.top=(y+dy)+'px';
}
});
var tmp=function(evt){
if(self.startX>0){
var dx=evt.clientX-self.startX;
var dy=evt.clientY-self.startY;
self.div.style.left=(x+dx)+'px';
self.div.style.top=(y+dy)+'px';
x=parseInt(self.div.style.left);
y=parseInt(self.div.style.top);
}
self.startX=0;
}
mxEvent.addListener(this.div,'mouseup',tmp);
mxEvent.addListener(this.div,'mouseout',tmp);
}
}
this.contentWrapper=document.createElement('div');
this.contentWrapper.className=style+'Pane';
this.contentWrapper.appendChild(content);
this.div.appendChild(this.contentWrapper);
this.div.style.display='none';
if(replaceNode!=null&&replaceNode.parentNode!=null){
replaceNode.parentNode.replaceChild(this.div,replaceNode);
}else{
document.body.appendChild(this.div);
}
}
mxWindow.prototype.closeImage=mxClient.imageBasePath+'close.gif';
mxWindow.prototype.minimizeImage=mxClient.imageBasePath+'minimize.gif';
mxWindow.prototype.normalizeImage=mxClient.imageBasePath+'normalize.gif';
mxWindow.prototype.setImage=function(image){
this.image=document.createElement('img');
this.image.setAttribute('align','left');
this.image.style.marginRight='4px';
this.image.style.marginLeft='0px';
this.image.setAttribute('src',image);
this.title.insertBefore(this.image,this.title.firstChild);
}
mxWindow.prototype.setCloseAction=function(funct){
var img=document.createElement('img');
img.setAttribute('src',this.closeImage);
img.align='right';
img.setAttribute('title','Close');
img.style.cursor='default';
this.title.insertBefore(img,this.title.firstChild);
mxEvent.addListener(img,'mousedown',function(evt){
funct(evt);
mxEvent.consume(evt);
});
}
mxWindow.prototype.isVisible=function(){
if(this.div!=null&&this.div.style.display!=null){
return this.div.style.display!='none';
}
return false;
}
mxWindow.prototype.getX=function(){
return parseInt(this.div.style.left);
}
mxWindow.prototype.getY=function(){
return parseInt(this.div.style.top);
}
mxWindow.prototype.setVisible=function(visible){
if(this.div!=null&&visible!=this.visible){
if(visible||visible==null){
mxUtils.fadeIn(this.div,100,null,null,mxClient.IS_FADE_WINDOW);
}else{
mxUtils.fadeOut(this.div,100,false,null,null,mxClient.IS_FADE_WINDOW);
}
}
this.visible=visible;
}
mxWindow.prototype.destroy=function(){
if(this.div!=null){
mxUtils.fadeOut(this.div,100,true,null,null,mxClient.IS_FADE_WINDOW);
this.div=null;
}
}
}

{
function mxPalette(container){
this.container=container;
}
mxPalette.prototype.addItem=function(label,icon,w,h,funct){
var a=document.createElement('a');
a.setAttribute('href','#');
mxEvent.addListener(a,'mousedown',function(evt){
mxDatatransfer.setSourceFunction(funct);
});
var img=document.createElement('img');
img.setAttribute('title',label);
img.setAttribute('src',icon);
img.setAttribute('width',w);
img.setAttribute('height',h);
img.setAttribute('align','absmiddle');
img.setAttribute('border','0');
img.setAttribute('hspace','4');
img.setAttribute('vspace','4');
a.appendChild(img);
this.container.appendChild(a);
}
}

{
function mxToolbar(container){
this.container=container;
this.noReset=false;
}
mxToolbar.prototype.addItem=function(title,icon,funct,pressedIcon,style){
var img=document.createElement('img');
img.className=style||'mxToolbarItem';
img.setAttribute('src',icon);
if(title!=null){
img.setAttribute('title',title);
}
this.container.appendChild(img);
if(funct!=null){
mxEvent.addListener(img,'click',funct);
}

mxEvent.addListener(img,'mousedown',function(evt){
if(pressedIcon!=null){
img.setAttribute('src',pressedIcon);
}else{
img.style.backgroundColor='gray';
}
});
var funct=function(evt){
if(pressedIcon!=null){
img.setAttribute('src',icon);
}else{
img.style.backgroundColor='';
}
}
mxEvent.addListener(img,'mouseup',funct);
mxEvent.addListener(img,'mouseout',funct);
return img;
}
mxToolbar.prototype.addCombo=function(style){
var div=document.createElement('div');
div.style.display='inline';
div.className='mxToolbarComboContainer';
var select=document.createElement('select');
select.className=style||'mxToolbarCombo';
div.appendChild(select);
this.container.appendChild(div);
return select;
}
mxToolbar.prototype.addActionCombo=function(title,style){
var select=document.createElement('select');
select.className=style||'mxToolbarCombo';
this.addOption(select,title,null);
mxEvent.addListener(select,'change',function(evt){
var value=select.options[select.selectedIndex];
select.selectedIndex=0;
if(value.funct!=null){
value.funct(evt);
}
});
this.container.appendChild(select);
return select;
}
mxToolbar.prototype.addOption=function(combo,label,value){
var option=document.createElement('option');
mxUtils.writeln(option,label);
if(typeof(value)=='function'){
option.funct=value;
}else{
option.setAttribute('value',value);
}
combo.appendChild(option);
return option;
}
mxToolbar.prototype.addSwitchMode=function(title,icon,funct,pressedIcon,style){
var img=document.createElement('img');
img.className=style||'mxToolbarMode';
img.setAttribute('src',icon);
img.altIcon=pressedIcon;
if(title!=null){
img.setAttribute('title',title);
}
var self=this;
mxEvent.addListener(img,'click',function(evt){
var tmp=self.selectedMode.altIcon;
if(tmp!=null){
self.selectedMode.altIcon=self.selectedMode.getAttribute('src');
self.selectedMode.setAttribute('src',tmp);
}else{
self.selectedMode.style.borderStyle='none';
}
self.defaultMode=img;
self.selectedMode=img;
self.defaultFunction=null;
self.selectedFunction=null;
var tmp=img.altIcon;
if(tmp!=null){
img.altIcon=img.getAttribute('src');
img.setAttribute('src',tmp);
}else{
img.style.borderStyle='inset';
}
funct();
});
this.container.appendChild(img);
if(this.defaultMode==null){
this.defaultMode=img;
this.selectedMode=img;
this.selectedFunction=null;
var tmp=img.altIcon;
if(tmp!=null){
img.altIcon=img.getAttribute('src');
img.setAttribute('src',tmp);
}else{
img.style.borderStyle='inset';
}
funct();
}
return img;
}
mxToolbar.prototype.addMode=function(title,icon,funct,pressedIcon,style){
var img=document.createElement('img');
img.className=style||'mxToolbarMode';
img.setAttribute('src',icon);
img.altIcon=pressedIcon;
if(title!=null){
img.setAttribute('title',title);
}
var self=this;
mxEvent.addListener(img,'click',function(evt){
self.selectMode(img,funct);
self.noReset=false;
});
mxEvent.addListener(img,'dblclick',function(evt){
self.selectMode(img,funct);
self.noReset=true;
});
this.container.appendChild(img);
if(this.defaultMode==null){
this.defaultMode=img;
this.selectedMode=img;
this.selectedFunction=funct;
this.defaultFunction=funct;
var tmp=img.altIcon;
if(tmp!=null){
img.altIcon=img.getAttribute('src');
img.setAttribute('src',tmp);
}else{
img.style.borderStyle='inset';
}
}
return img;
}
mxToolbar.prototype.selectMode=function(domNode,funct){
if(this.selectedMode!=domNode){
var tmp=this.selectedMode.altIcon;
if(tmp!=null){
this.selectedMode.altIcon=this.selectedMode.getAttribute('src');
this.selectedMode.setAttribute('src',tmp);
}else{
this.selectedMode.style.borderStyle='none';
}
this.selectedMode=domNode;
var tmp=this.selectedMode.altIcon;
if(tmp!=null){
this.selectedMode.altIcon=this.selectedMode.getAttribute('src');
this.selectedMode.setAttribute('src',tmp);
}else{
this.selectedMode.style.borderStyle='inset';
}
this.selectedFunction=funct;
this.onSelectMode();
}
}
mxToolbar.prototype.onSelectMode=function(){
}
mxToolbar.prototype.resetMode=function(forced){
if((forced||!this.noReset)&&this.selectedMode!=this.defaultMode)
{
var tmp=this.selectedMode.altIcon;
if(tmp!=null){
this.selectedMode.altIcon=this.selectedMode.getAttribute('src');
this.selectedMode.setAttribute('src',tmp);
}else{
this.selectedMode.style.borderStyle='none';
}
this.selectedMode=this.defaultMode;
tmp=this.selectedMode.altIcon;
if(tmp!=null){
this.selectedMode.altIcon=this.selectedMode.getAttribute('src');
this.selectedMode.setAttribute('src',tmp);
}else{
this.selectedMode.style.borderStyle='inset';
}
this.selectedFunction=this.defaultFunction;
}
}
mxToolbar.prototype.addSeparator=function(icon){
var img=this.addItem(null,icon,null);
return img;
}
mxToolbar.prototype.addBreak=function(){
mxUtils.br(this.container);
}
mxToolbar.prototype.addLine=function(){
var hr=document.createElement('hr');
hr.style.marginRight='6px';
hr.setAttribute('size','1');
this.container.appendChild(hr);
}
}

{
function mxForm(className){
this.table=document.createElement('table');
this.table.className=className;
this.body=document.createElement('tbody');
this.table.appendChild(this.body);
}
mxForm.prototype.addButtons=function(okFunct,cancelFunct){
var tr=document.createElement('tr');
var td=document.createElement('td');
tr.appendChild(td);
td=document.createElement('td');
var button=document.createElement('button');
mxUtils.write(button,'OK');
td.appendChild(button);
var self=this;
mxEvent.addListener(button,'click',function(){
okFunct();
});
button=document.createElement('button');
mxUtils.write(button,'Cancel');
td.appendChild(button);
mxEvent.addListener(button,'click',function(){
cancelFunct();
});
tr.appendChild(td);
this.body.appendChild(tr);
}
mxForm.prototype.addText=function(name,value){
var input=document.createElement('input');
input.setAttribute('type','text');
input.value=value;
return this.addField(name,input);
}
mxForm.prototype.addTextarea=function(name,value,rows){
var input=document.createElement('textarea');
if(mxClient.IS_NS){
rows--;
}
input.setAttribute('rows',rows||2);
input.value=value;
return this.addField(name,input);
}
mxForm.prototype.addCombo=function(name,isMultiSelect,size){
var select=document.createElement('select');
if(size!=null){
select.setAttribute('size',size);
}
if(isMultiSelect){
select.setAttribute('multiple','true');
}
return this.addField(name,select);
}
mxForm.prototype.addOption=function(combo,label,value,isSelected){
var option=document.createElement('option');
mxUtils.writeln(option,label);
option.setAttribute('value',value);
if(isSelected){
option.setAttribute('selected',isSelected);
}
combo.appendChild(option);
}
mxForm.prototype.addField=function(name,input){
var tr=document.createElement('tr');
var td=document.createElement('td');
mxUtils.write(td,name);
tr.appendChild(td);
td=document.createElement('td');
td.appendChild(input);
tr.appendChild(td);
this.body.appendChild(tr);
return input;
}
}

{
function mxDivResizer(div){
if(div.nodeName.toLowerCase()=='div'){
this.div=div;
var self=this;
mxEvent.addListener(window,'resize',function(evt){
self.resize();
});
this.resize();
}
}
mxDivResizer.prototype.resize=function(){
var w=document.body.clientWidth;
var h=document.body.clientHeight;
var l=parseInt(this.div.style.left);
var r=parseInt(this.div.style.right);
var t=parseInt(this.div.style.top);
var b=parseInt(this.div.style.bottom);
if(l>=0&&r>=0){
this.div.style.width=w-r-l;
}
if(t>=0&&b>=0){
this.div.style.height=h-t-b;
}
}
}

{
function mxEventSource()
{
this.eventListeners=null;
}
mxEventSource.prototype.addListener=function(name,funct)
{
if(this.eventListeners==null)
{
this.eventListeners=new Array();
}
this.eventListeners.push(name);
this.eventListeners.push(funct);
}
mxEventSource.prototype.dispatchEvent=function(name)
{
if(this.eventListeners!=null)
{
var args=null;
for(var i=0;i<this.eventListeners.length;i+=2)
{
var listen=this.eventListeners[i];
if(listen==null||listen==name)
{
if(args==null)
{
args=new Array();
var argCount=arguments.length;
for(var j=1;j<argCount;j++)
{
args.push(arguments[j]);
}
}
this.eventListeners[i+1].apply(this,args);
}
}
}
}
}

{
function mxSession(model,urlInit,urlPoll,urlPost)
{
this.model=model;
this.urlInit=urlInit;
this.urlPoll=urlPoll;
this.urlPost=urlPost;
if(model!=null)
{

this.codec=new mxCodec();
this.codec.lookup=function(id)
{
return model.getCell(id);
}
}
var self=this;
model.addListener('notify',function(sender,changes)
{
if(changes!=null&&self.isLocal||(self.isConnected&&!self.isSuspended))
{
self.post(self.encodeChanges(changes));
}
});
}
mxSession.prototype=new mxEventSource();
mxSession.prototype.constructor=mxSession;
mxSession.prototype.linefeed='\n';
mxSession.prototype.sent=0;
mxSession.prototype.received=0;
mxSession.prototype.isLocal=false;
mxSession.prototype.isConnected=false;
mxSession.prototype.isSuspended=false;
mxSession.prototype.isPolling=false;
mxSession.prototype.start=function()
{
if(this.isLocal)
{
this.isConnected=true;
this.dispatchEvent('connect',this);
}
else if(!this.isConnected)
{
var self=this;
this.get(this.urlInit,function(req)
{
self.isConnected=true;
self.dispatchEvent('connect',self);
self.poll();
});
}
}
mxSession.prototype.suspend=function()
{
if(this.isConnected&&!this.isSuspended)
{
this.isSuspended=true;
this.dispatchEvent('suspend',this);
}
}
mxSession.prototype.resume=function(type,attr,value)
{
if(this.isConnected&&this.isSuspended)
{
this.isSuspended=false;
this.dispatchEvent('resume',this);
if(!this.isPolling)
{
this.poll();
}
}
}
mxSession.prototype.stop=function(reason)
{
if(this.isConnected)
{
this.isConnected=false;
}
this.dispatchEvent('disconnect',this,reason);
}
mxSession.prototype.poll=function()
{
if(this.isConnected&&!this.isSuspended&&this.urlPoll!=null)
{
this.isPolling=true;
var self=this;
this.get(this.urlPoll,function()
{
self.poll()
});
}
else
{
this.isPolling=false;
}
}
mxSession.prototype.post=function(xml,onLoad,onError)
{
if(xml!=null&&xml.length>0)
{
if(!this.isLocal&&this.urlPost!=null)
{
mxUtils.post(this.urlPost,'xml='+xml,onLoad,onError);
}
this.sent+=xml.length;
this.dispatchEvent('post',this,this.urlPost,xml);
}
}
mxSession.prototype.get=function(url,onLoad,onError)
{


if(typeof(mxUtils)!='undefined')
{
var self=this;
var onErrorWrapper=function(ex)
{
if(onError!=null)
{
onError(ex);
}
else
{
self.stop(ex);
}
};

var req=mxUtils.get(url,function(req)
{
try
{
if(req.isReady())
{
self.received+=req.getText().length;
self.dispatchEvent('get',self,url,req);

if(req.getText().indexOf('<?php')<0)
{
if(req.getText().length>0)
{
var node=req.getXML().documentElement;
if(node==null)
{
onErrorWrapper('Invalid response: '+req.getText());
}
else
{
self.receive(node);
}
}
if(onLoad!=null)
{
onLoad(req);
}
}
}
else
{
onErrorWrapper('Response not ready');
}
}
catch(ex)
{
onErrorWrapper(ex);
throw ex;
}
},

function(req)
{
onErrorWrapper('Transmission error');
});
}
}
mxSession.prototype.encodeChanges=function(changes)
{
var xml='';
for(var i=0;i<changes.length;i++)
{



var node=this.codec.encode(changes[i]);
xml+=mxUtils.getXml(node,this.linefeed);
}
return xml;
}
mxSession.prototype.receive=function(node)
{
if(node!=null&&node.nodeType==1)
{
var name=node.nodeName.toLowerCase();
if(name=='state')
{
var tmp=node.firstChild;
while(tmp!=null)
{
this.receive(tmp);
tmp=tmp.nextSibling;
}

var sid=node.getAttribute('namespace');
this.model.prefix=sid+'-';
}
else if(name=='delta')
{
var changes=this.decodeChanges(node);
if(changes.length>0)
{
this.model.dispatchEvent('change',this.model,changes);
}
}
this.dispatchEvent('receive',this,node);
}
}
mxSession.prototype.decodeChanges=function(node)
{
var changes=new Array();
node=node.firstChild;
while(node!=null)
{
if(node.nodeType==1)
{



var change=null;
if(node.nodeName=='mxRootChange')
{
var codec=new mxCodec(node.ownerDocument);
change=codec.decode(node);
}
else
{
this.codec.document=node.ownerDocument;
change=this.codec.decode(node);
}
if(change!=null)
{
change.model=this.model;
change.execute();
changes.push(change);
}
}
node=node.nextSibling;
}
return changes;
}
}

{
function mxUndoableEdit(source,isSignificant){
this.source=source;
this.changes=new Array();
this.isSignificant=(isSignificant!=null)?isSignificant:true;
this.isUndone=false;
this.isRedone=false;
}
mxUndoableEdit.prototype.isEmpty=function(){
return this.changes.length==0;
}
mxUndoableEdit.prototype.add=function(change){
this.changes.push(change);
}
mxUndoableEdit.prototype.die=function(){
}
mxUndoableEdit.prototype.undo=function(){
if(!this.isUndone){
for(var i=this.changes.length-1;i>=0;i--){
if(this.changes[i].execute!=null){
this.changes[i].execute();
}
}
this.isUndone=true;
this.isRedone=false;
}
if(this.notify!=null){
this.notify();
}
}
mxUndoableEdit.prototype.redo=function(){
if(!this.isRedone){
for(var i=0;i<this.changes.length;i++){
if(this.changes[i].execute!=null){
this.changes[i].execute();
}else{
this.changes[i].redo();
}
}
this.isUndone=false;
this.isRedone=true;
}
if(this.notify!=null){
this.notify();
}
}
}

{
function mxUndoManager(){
this.size=100;
this.reset();
}
mxUndoManager.prototype.reset=function(){
this.history=new Array();
this.indexOfNextAdd=0;
}
mxUndoManager.prototype.undo=function(){
var i=this.indexOfNextAdd;
while(this.indexOfNextAdd>0){
var edit=this.history[--this.indexOfNextAdd];
edit.undo();
if(edit.isSignificant){
break;
}
}
}
mxUndoManager.prototype.redo=function(){
var n=this.history.length;
while(this.indexOfNextAdd<n){
var edit=this.history[this.indexOfNextAdd++];
edit.redo();
if(edit.isSignificant){
break;
}
}
}
mxUndoManager.prototype.trim=function(){
if(this.history.length>this.indexOfNextAdd){
var edits=this.history.splice(this.indexOfNextAdd,this.history.length-this.indexOfNextAdd);
for(var i=0;i<edits.length;i++){
edits[i].die();
}
}
}
mxUndoManager.prototype.undoableEditHappened=function(undoableEdit){
this.trim();
if(this.size==this.history.length){
this.history.shift();
}
this.history.push(undoableEdit);
this.indexOfNextAdd=this.history.length;
}
}

{
function mxPath(format){
this.format=format;
this.path=new Array();
this.translate=new mxPoint(0,0);
}
mxPath.prototype.format=null;
mxPath.prototype.translate=null;
mxPath.prototype.path=null;
mxPath.prototype.isVml=function(){
return this.format=='vml';
}
mxPath.prototype.getPath=function(){
return this.path.join('');
}
mxPath.prototype.setTranslate=function(x,y){
this.translate=new mxPoint(x,y);
}
mxPath.prototype.moveTo=function(x,y){
if(this.isVml())
{
this.path.push('m ',(this.translate.x+x),' ',(this.translate.y+y),' ');
}
else
{
this.path.push('M ',Math.floor(this.translate.x+x),' ',Math.floor(this.translate.y+y),' ');
}
}
mxPath.prototype.lineTo=function(x,y){
if(this.isVml())
{
this.path.push('l ',(this.translate.x+x),' ',(this.translate.y+y),' ');
}
else
{
this.path.push('L ',Math.floor(this.translate.x+x),' ',Math.floor(this.translate.y+y),' ');
}
}
mxPath.prototype.curveTo=function(x0,y0,x1,y1,x2,y2){
if(this.isVml())
{
this.path.push('c ',Math.floor(this.translate.x+x0),' ',Math.floor(this.translate.y+y0),' ',Math.floor(this.translate.x+x1),' ',Math.floor(this.translate.y+y1),' ',Math.floor(this.translate.x+x2),' ',Math.floor(this.translate.y+y2),' ');
}
else
{
this.path.push('C ',(this.translate.x+x0),' ',(this.translate.y+y0),' ',(this.translate.x+x1),' ',(this.translate.y+y1),' ',(this.translate.x+x2),' ',(this.translate.y+y2),' ');
}
}
mxPath.prototype.write=function(string)
{
this.path.push(string,' ');
}
mxPath.prototype.end=function(){
if(this.format=='vml')
{
this.path.push('e');
}
else
{
this.path.push('');
}
}
mxPath.prototype.close=function(){
if(this.format=='vml')
{
this.path.push('x e');
}
else
{
this.path.push('Z');
}
}
}

{
function mxShape(){}
mxShape.prototype.SVG_STROKE_TOLERANCE=8;
mxShape.prototype.scale=1;
mxShape.prototype.dialect=null;
mxShape.prototype.init=function(container)
{
if(this.node==null)
{
this.node=this.create(container);
if(container!=null)
{
container.appendChild(this.node);
}
}
this.redraw();
}
mxShape.prototype.create=function(container)
{
var node=null;
if(this.dialect==mxConstants.DIALECT_SVG)
{
node=this.createSvg();
}
else if(this.dialect==mxConstants.DIALECT_STRICTHTML||this.dialect==mxConstants.DIALECT_PREFERHTML||(!this.isRounded&&this.gradient==null&&this.dialect==mxConstants.DIALECT_MIXEDHTML))
{
node=this.createHtml();
}
else
{
node=this.createVml();
}
return node;
}
mxShape.prototype.createHtml=function()
{
return this.createVml();
}
mxShape.prototype.destroy=function()
{
if(this.node!=null)
{
this.node.parentNode.removeChild(this.node);
this.node=null;
}
}
mxShape.prototype.apply=function(state)
{
var style=state.style;
this.style=style;
if(style!=null)
{
var tmp=style[mxConstants.STYLE_FILLCOLOR];
if(tmp!=null)
{
this.fill=tmp;
}
var tmp=style[mxConstants.STYLE_GRADIENTCOLOR];
if(tmp!=null)
{
this.gradient=tmp;
}
var tmp=style[mxConstants.STYLE_OPACITY];
if(tmp!=null)
{
this.opacity=tmp;
}
tmp=style[mxConstants.STYLE_STROKECOLOR];
if(tmp!=null)
{
this.stroke=tmp;
}
tmp=style[mxConstants.STYLE_STROKEWIDTH];
if(tmp!=null)
{
this.strokewidth=tmp;
}
tmp=style[mxConstants.STYLE_SHADOW];
if(tmp!=null)
{
this.isShadow=(tmp=='true');
}
tmp=style[mxConstants.STYLE_DASHED];
if(tmp!=null)
{
this.isDashed=(tmp=='true');
}
tmp=style[mxConstants.STYLE_SPACING];
if(tmp!=null)
{
this.spacing=tmp;
}
tmp=style[mxConstants.STYLE_ENDSIZE];
if(tmp!=null)
{
this.endSize=tmp;
}
tmp=style[mxConstants.STYLE_ROUNDED];
if(tmp!=null)
{
this.isRounded=(tmp=='true');
}
tmp=style[mxConstants.STYLE_STARTARROW];
if(tmp!=null)
{
this.startArrow=tmp;
}
tmp=style[mxConstants.STYLE_ENDARROW];
if(tmp!=null)
{
this.endArrow=tmp;
}
}
}
mxShape.prototype.createSvgGroup=function(shape)
{
var g=document.createElementNS(mxConstants.NS_SVG,'g');
this.innerNode=document.createElementNS(mxConstants.NS_SVG,shape);
this.configureSvgShape(this.innerNode);
this.shadowNode=this.createSvgShadow(this.innerNode);
if(this.shadowNode!=null)
{
g.appendChild(this.shadowNode);
}
g.appendChild(this.innerNode);
return g;
}
mxShape.prototype.createSvgShadow=function(node)
{
if(this.isShadow&&this.fill!=null)
{
var shadow=node.cloneNode(true);
shadow.setAttribute('stroke','none');
shadow.setAttribute('fill',mxConstants.SVG_SHADOWCOLOR);
shadow.setAttribute('transform',mxConstants.SVG_SHADOWTRANSFORM);
return shadow;
}
return null;
}
mxShape.prototype.configureHtmlShape=function(node)
{
if(node.tagUrn=='urn:schemas-microsoft-com:vml')
{
this.configureVmlShape(node);
}
else
{
node.style.position='absolute';
node.style.overflow='hidden';
var color=this.stroke;
if(color=='indicated')
{
color=this.indicatorColor;
}
if(color!=null)
{
node.style.borderColor=color;
if(this.isDashed)
{
node.style.borderStyle='dashed';
}
else if(this.strokewidth>0)
{
node.style.borderStyle='solid';
}
node.style.borderWidth=this.strokewidth;
}
color=this.fill;
if(color=='indicated')
{
color=this.indicatorColor;
}
if(color!=null)
{
node.style.backgroundColor=color;
if(this.opacity!=null)
{
}
}
else
{
node.style.background='url(\''+mxClient.imageBasePath+'images/transparent.gif\')';
}
if(this.opacity!=null)
{
mxUtils.setOpacity(node,this.opacity);
}
}
}
mxShape.prototype.configureVmlShape=function(node)
{
node.style.position='absolute';
var color=this.stroke;
if(color=='indicated')
{
color=this.indicatorColor;
}
if(color!=null)
{
node.setAttribute('strokecolor',color);
}
else
{
node.setAttribute('stroked','false');
}
color=this.fill;
if(color=='indicated')
{
color=this.indicatorColor;
}
if(color!=null)
{
node.setAttribute('fillcolor',color);
if(node.fillNode==null)
{
node.fillNode=document.createElement('v:fill');
node.appendChild(node.fillNode);
}
node.fillNode.setAttribute('color',color);
if(this.gradient!=null)
{
node.fillNode.setAttribute('type','gradient');
node.fillNode.setAttribute('angle','180');
node.fillNode.setAttribute('color2',this.gradient);
}
if(this.opacity!=null)
{
node.fillNode.setAttribute('opacity',this.opacity+'%');
}
}
else
{
node.setAttribute('filled','false');
if(node.fillNode!=null){
node.removeChild(node.fillNode);
node.fillNode=null;
}
}
if(this.isDashed)
{
this.strokeNode=document.createElement('v:stroke');
this.strokeNode.setAttribute('dashstyle','2 2');
node.appendChild(this.strokeNode);
}
else if(this.strokeNode!=null)
{
node.removeChild(this.strokeNode);
this.strokeNode=null;
}
if(this.isShadow&&this.fill!=null)
{
if(this.shadowNode==null)
{
this.shadowNode=document.createElement('v:shadow');
this.shadowNode.setAttribute('on','true');
node.appendChild(this.shadowNode);
}
}
if(this.isRounded)
{
node.setAttribute('arcsize','15%');
}
}
mxShape.prototype.configureSvgShape=function(node)
{
var color=this.stroke;
if(color=='indicated')
{
color=this.indicatorColor;
}
if(color!=null)
{
node.setAttribute('stroke',color);
}
else
{
node.setAttribute('stroke','none');
}
color=this.fill;
if(color=='indicated')
{
color=this.indicatorColor;
}
if(color!=null)
{
if(this.gradient!=null)
{
if(this.gradientNode==null)
{
this.gradientNode=this.createSvgGradient(color,this.gradient,this.opacity);
var id=this.gradientNode.getAttribute('id');
node.setAttribute('fill','url(#'+id+')');
}
}
else
{

this.gradientNode=null;
node.setAttribute('fill',color);
}
}
else
{
node.setAttribute('fill','none');
}
if(this.isDashed)
{
node.setAttribute('stroke-dasharray','3, 3');
}
if(this.opacity!=null)
{

node.setAttribute('fill-opacity',this.opacity/100);
node.setAttribute('stroke-opacity',this.opacity/100);
}
}
mxShape.prototype.createSvgGradient=function(start,end,opacity)
{
var op=(opacity!=null)?opacity:100;
var id='mxgradient-'+start+'-'+end+'-'+op;
var gradient=document.getElementById(id);
if(gradient==null)
{
var gradient=document.createElementNS(mxConstants.NS_SVG,'linearGradient');
gradient.setAttribute('id',id);
gradient.setAttribute('x1','0%');
gradient.setAttribute('y1','0%');
gradient.setAttribute('x2','0%');
gradient.setAttribute('y2','100%');
var stop=document.createElementNS(mxConstants.NS_SVG,'stop');
stop.setAttribute('offset','0%');
stop.setAttribute('style','stop-color:'+start+';stop-opacity:'+(op/100));
gradient.appendChild(stop);
stop=document.createElementNS(mxConstants.NS_SVG,'stop');
stop.setAttribute('offset','100%');
stop.setAttribute('style','stop-color:'+end+';stop-opacity:'+(op/100));
gradient.appendChild(stop);


var svg=document.getElementsByTagName('svg')[0];
svg.appendChild(gradient);
}
return gradient;
}
mxShape.prototype.createPoints=function(moveCmd,lineCmd,curveCmd,isRelative)
{
var offsetX=(isRelative)?this.bounds.x:0;
var offsetY=(isRelative)?this.bounds.y:0;
var points=moveCmd+' '+Math.floor(this.points[0].x-offsetX)+' '+Math.floor(this.points[0].y-offsetY)+' ';
var size=20*this.scale;
for(var i=1;i<this.points.length;i++)
{
var pt=this.points[i];
var p0=this.points[i-1];
var dx=p0.x-pt.x;
var dy=p0.y-pt.y;
if((this.isRounded&&i<this.points.length-1)&&(dx!=0||dy!=0))
{



var dist=Math.sqrt(dx*dx+dy*dy);
var nx1=dx*Math.min(size,dist/2)/dist;
var ny1=dy*Math.min(size,dist/2)/dist;
points+=lineCmd+' '+Math.floor(pt.x+nx1-offsetX)+' '+Math.floor(pt.y+ny1-offsetY)+' ';



var pe=this.points[i+1];
dx=pe.x-pt.x;
dy=pe.y-pt.y;
dist=Math.max(1,Math.sqrt(dx*dx+dy*dy));
var nx2=dx*Math.min(size,dist/2)/dist;
var ny2=dy*Math.min(size,dist/2)/dist;
points+=curveCmd+' '+Math.floor(pt.x-offsetX)+' '+Math.floor(pt.y-offsetY)+' '+Math.floor(pt.x-offsetX)+','+Math.floor(pt.y-offsetY)+' '+Math.floor(pt.x+nx2-offsetX)+' '+Math.floor(pt.y+ny2-offsetY)+' ';
}
else
{
points+=lineCmd+' '+Math.floor(pt.x-offsetX)+' '+Math.floor(pt.y-offsetY)+' ';
}
}
return points;
}
mxShape.prototype.updateHtmlShape=function(node)
{
if(node.tagUrn=='urn:schemas-microsoft-com:vml')
{
this.updateVmlShape(node);
}
else
{
node.style.borderWidth=Math.max(1,Math.floor(this.strokewidth*this.scale));
if(this.bounds!=null){
node.style.left=Math.floor(this.bounds.x)+'px';
node.style.top=Math.floor(this.bounds.y)+'px';
node.style.width=Math.floor(this.bounds.width)+'px';
node.style.height=Math.floor(this.bounds.height)+'px';
}
}
if(this.points!=null&&this.bounds!=null)
{
if(node.tagUrn!='urn:schemas-microsoft-com:vml')
{
while(node.firstChild!=null)
{
node.removeChild(node.firstChild);
}
if(this.points.length==2)
{
node.style.borderStyle='solid';
}
else if(this.points.length==3)
{
var mid=this.points[1];
var n='0';
var s='1';
var w='0';
var e='1';
if(mid.x==this.bounds.x)
{
e='0';
w='1';
}
if(mid.y==this.bounds.y)
{
n='1';
s='0';
}
node.style.borderStyle='solid';
node.style.borderWidth=n+' '+e+' '+s+' '+w+'px';
}
else
{
node.style.width=Math.floor(this.bounds.width+1)+'px';
node.style.height=Math.floor(this.bounds.height+1)+'px';
node.style.borderStyle='none';
var last=this.points[0];
for(var i=1;i<this.points.length;i++)
{
var next=this.points[i];
var tmp=document.createElement('DIV');
var x=Math.min(next.x,last.x)-this.bounds.x;
var y=Math.min(next.y,last.y)-this.bounds.y;
var w=Math.max(1,Math.abs(next.x-last.x));
var h=Math.max(1,Math.abs(next.y-last.y));
tmp.style.left=x+'px';
tmp.style.top=y+'px';
tmp.style.width=w+'px';
tmp.style.height=h+'px';
tmp.style.position='absolute';
tmp.style.overflow='hidden';
tmp.style.borderColor=this.stroke;
tmp.style.borderStyle='solid';
tmp.style.borderWidth='1 0 0 1px';
node.appendChild(tmp);
last=next;
}
}
}
}
}
mxShape.prototype.updateVmlShape=function(node)
{
node.setAttribute('strokeweight',this.strokewidth*this.scale);
if(this.bounds!=null)
{
node.style.left=Math.floor(this.bounds.x)+'px';
node.style.top=Math.floor(this.bounds.y)+'px';
node.style.width=Math.floor(this.bounds.width)+'px';
node.style.height=Math.floor(this.bounds.height)+'px';
}
if(this.points!=null)
{
if(node.nodeName=='polyline'&&node.points!=null)
{
var points='';
for(var i=0;i<this.points.length;i++)
{
points+=this.points[i].x+','+this.points[i].y+' ';
}
node.points.value=points;
node.style.left=null;
node.style.top=null;
node.style.width=null;
node.style.height=null;
}
else if(this.bounds!=null)
{
this.node.setAttribute('coordsize',Math.floor(this.bounds.width)+','+Math.floor(this.bounds.height));
var points=this.createPoints('m','l','c',true);
node.setAttribute('path',points+' e');
}
}
}
mxShape.prototype.updateSvgShape=function(node)
{
node.setAttribute('stroke-width',this.strokewidth*this.scale);
if(this.points!=null)
{
var points=this.createPoints('M','L','C',false);
node.setAttribute('d',points);
node.removeAttribute('x');
node.removeAttribute('y');
node.removeAttribute('width');
node.removeAttribute('height');
}
else if(this.bounds!=null)
{
node.setAttribute('x',this.bounds.x);
node.setAttribute('y',this.bounds.y);
var w=this.bounds.width;
var h=this.bounds.height;
node.setAttribute('width',w);
node.setAttribute('height',h);
if(this.isRounded)
{
var r=Math.min(w/5,h/5);
node.setAttribute('rx',r);
node.setAttribute('ry',r);
}
}
}
mxShape.prototype.reconfigure=function()
{
if(this.dialect==mxConstants.DIALECT_SVG)
{
if(this.innerNode!=null)
{
this.configureSvgShape(this.innerNode);
}
else
{
this.configureSvgShape(this.node);
}
}
else if(this.node.tagUrn=='urn:schemas-microsoft-com:vml')
{
this.configureVmlShape(this.node);
}
else
{
this.configureHtmlShape(this.node);
}
}
mxShape.prototype.redraw=function(){
if(this.dialect==mxConstants.DIALECT_SVG)
{
this.redrawSvg();
}
else if(this.node.tagUrn=='urn:schemas-microsoft-com:vml')
{
this.redrawVml();
}
else
{
this.redrawHtml();
}
}
mxShape.prototype.redrawSvg=function(){
if(this.innerNode!=null)
{
this.updateSvgShape(this.innerNode);
if(this.shadowNode!=null)
{
this.updateSvgShape(this.shadowNode);
}
}
else
{
this.updateSvgShape(this.node);
}
}
mxShape.prototype.redrawVml=function(){
this.updateVmlShape(this.node);
}
mxShape.prototype.redrawHtml=function(){
this.updateHtmlShape(this.node);
}
mxShape.prototype.createPath=function(arg)
{
var x=this.bounds.x;
var y=this.bounds.y;
var w=this.bounds.width;
var h=this.bounds.height;
var path=null;
if(this.dialect==mxConstants.DIALECT_SVG)
{
path=new mxPath('svg');
path.setTranslate(x,y);
}
else
{
path=new mxPath('vml');
}
this.redrawPath(path,x,y,w,h,arg);
return path.getPath();
}
mxShape.prototype.redrawPath=function(path,x,y,w,h)
{
}
}

{
function mxActor(bounds,fill,stroke,strokewidth)
{
this.bounds=bounds;
this.fill=fill;
this.stroke=stroke;
this.strokewidth=strokewidth||1;
}
mxActor.prototype=new mxShape();
mxActor.prototype.constructor=mxActor;
mxActor.prototype.createVml=function()
{
var node=document.createElement('v:shape');
this.configureVmlShape(node);
return node;
}
mxActor.prototype.redrawVml=function()
{
this.updateVmlShape(this.node);
var w=Math.floor(this.bounds.width);
var h=Math.floor(this.bounds.height);
var s=this.strokewidth*this.scale;
this.node.setAttribute('coordsize',w+','+h);
this.node.setAttribute('strokeweight',s);
var d=this.createPath();
this.node.setAttribute('path',d);
}
mxActor.prototype.createSvg=function()
{
return this.createSvgGroup('path');
}
mxActor.prototype.redrawSvg=function()
{
var s=this.strokewidth*this.scale;
var d=this.createPath();
this.innerNode.setAttribute('stroke-width',s);
this.innerNode.setAttribute('d',d);
if(this.shadowNode!=null)
{
this.shadowNode.setAttribute('stroke-width',s);
this.shadowNode.setAttribute('d',d);
}
}
mxActor.prototype.redrawPath=function(path,x,y,w,h)
{
var width=w*2/6;
path.moveTo(0,h);
path.curveTo(0,3*h/5,0,2*h/5,w/2,2*h/5);
path.curveTo(w/2-width,2*h/5,w/2-width,0,w/2,0);
path.curveTo(w/2+width,0,w/2+width,2*h/5,w/2,2*h/5);
path.curveTo(w,2*h/5,w,3*h/5,w,h);
path.close();
}
}

{
function mxRectangleShape(bounds,fill,stroke,strokewidth)
{
this.bounds=bounds;
this.fill=fill;
this.stroke=stroke;
this.strokewidth=strokewidth||1;
}
mxRectangleShape.prototype=new mxShape();
mxRectangleShape.prototype.constructor=mxRectangleShape;
mxRectangleShape.prototype.createHtml=function()
{
var node=document.createElement('DIV');
this.configureHtmlShape(node);
return node;
}
mxRectangleShape.prototype.createVml=function()
{
var name=(this.isRounded)?'v:roundrect':'v:rect';
var node=document.createElement(name);
this.configureVmlShape(node);
return node;
}
mxRectangleShape.prototype.createSvg=function()
{
var node=this.createSvgGroup('rect');
if(this.strokewidth*this.scale>=1&&!this.isRounded)
{
this.innerNode.setAttribute('shape-rendering','optimizeSpeed');
}
return node;
}
}

{
function mxEllipse(bounds,fill,stroke,strokewidth)
{
this.bounds=bounds;
this.fill=fill;
this.stroke=stroke;
this.strokewidth=strokewidth||1;
}
mxEllipse.prototype=new mxShape();
mxEllipse.prototype.constructor=mxEllipse;
mxEllipse.prototype.createVml=function()
{
var node=document.createElement('v:oval');
this.configureVmlShape(node);
return node;
}
mxEllipse.prototype.createSvg=function()
{
return this.createSvgGroup('ellipse');
}
mxEllipse.prototype.redrawSvg=function()
{
this.updateSvgNode(this.innerNode);
if(this.shadowNode!=null)
{
this.updateSvgNode(this.shadowNode);
}
}
mxEllipse.prototype.updateSvgNode=function(node)
{
var s=this.strokewidth*this.scale;
node.setAttribute('stroke-width',s);
if(node!=null)
{
node.setAttribute('cx',this.bounds.x+this.bounds.width/2);
node.setAttribute('cy',this.bounds.y+this.bounds.height/2);
node.setAttribute('rx',this.bounds.width/2);
node.setAttribute('ry',this.bounds.height/2);
}
}
}

{
function mxRhombus(bounds,fill,stroke,strokewidth){
this.bounds=bounds;
this.fill=fill;
this.stroke=stroke;
this.strokewidth=strokewidth||1;
}
mxRhombus.prototype=new mxShape();
mxRhombus.prototype.constructor=mxRhombus;
mxRhombus.prototype.createHtml=function()
{
var node=null;
if(mxClient.IS_CANVAS)
{
node=document.createElement('CANVAS');
this.configureHtmlShape(node);
node.style.borderStyle='none';
}
else
{
node=document.createElement('DIV');
this.configureHtmlShape(node);
}
return node;
}
mxRhombus.prototype.createVml=function()
{
var node=document.createElement('v:shape');
this.configureVmlShape(node);
return node;
}
mxRhombus.prototype.createSvg=function()
{
return this.createSvgGroup('path');
}



mxRhombus.prototype.redrawVml=function(){
this.node.setAttribute('strokeweight',this.strokewidth*this.scale);
this.updateVmlShape(this.node);
var x=0;
var y=0;
var w=Math.floor(this.bounds.width);
var h=Math.floor(this.bounds.height);
this.node.setAttribute('coordsize',w+','+h);
var points='m '+Math.floor(x+w/2)+' '+y+' l '+(x+w)+' '+Math.floor(y+h/2)+' l '+Math.floor(x+w/2)+' '+(y+h)+' l '+x+' '+Math.floor(y+h/2);
this.node.setAttribute('path',points+' x e');
}
mxRhombus.prototype.redrawHtml=function(){
if(this.node.nodeName=='CANVAS')
{
this.redrawCanvas();
}
else
{
this.updateHtmlShape(this.node);
}
}
mxRhombus.prototype.redrawCanvas=function(){
this.updateHtmlShape(this.node);
var x=0;
var y=0;
var w=this.bounds.width;
var h=this.bounds.height;
this.node.setAttribute('width',w);
this.node.setAttribute('height',h);
if(!this.isRepaintNeeded){
var ctx=this.node.getContext('2d');
ctx.clearRect(0,0,w,h);
ctx.beginPath();
ctx.moveTo(x+w/2,y);
ctx.lineTo(x+w,y+h/2);
ctx.lineTo(x+w/2,y+h);
ctx.lineTo(x,y+h/2);
ctx.lineTo(x+w/2,y);
if(this.node.style.backgroundColor!='transparent'){
ctx.fillStyle=this.node.style.backgroundColor;
ctx.fill();
}
if(this.node.style.borderColor!=null){
ctx.strokeStyle=this.node.style.borderColor;
ctx.stroke();
}
this.isRepaintNeeded=false;
}
}
mxRhombus.prototype.redrawSvg=function(){
if(this.dialect==mxConstants.DIALECT_SVG)
{
this.updateSvgNode(this.innerNode);
if(this.shadowNode!=null)
{
this.updateSvgNode(this.shadowNode);
}
}
}
mxRhombus.prototype.updateSvgNode=function(node){
node.setAttribute('stroke-width',this.strokewidth*this.scale);
var x=this.bounds.x;
var y=this.bounds.y;
var w=this.bounds.width;
var h=this.bounds.height;
var d='M '+(x+w/2)+' '+y+' L '+(x+w)+' '+(y+h/2)+' L '+(x+w/2)+' '+(y+h)+' L '+x+' '+(y+h/2)+' Z ';
node.setAttribute('d',d);
}
}

{
function mxPolyline(points,stroke,strokewidth)
{
this.points=points;
this.stroke=stroke||'black';
this.strokewidth=strokewidth||1;
}
mxPolyline.prototype=new mxShape();
mxPolyline.prototype.constructor=mxPolyline;
mxPolyline.prototype.clone=function()
{
var clone=new mxPolyline(this.points,this.stroke,this.strokewidth);
clone.isDashed=this.isDashed;
return clone;
}
mxPolyline.prototype.create=function()
{
var node=null;
if(this.dialect==mxConstants.DIALECT_SVG)
{
node=this.createSvg();
}
else if(this.dialect==mxConstants.DIALECT_STRICTHTML||this.dialect==mxConstants.DIALECT_PREFERHTML)
{
node=document.createElement('DIV');
}
else
{
node=document.createElement('v:polyline');
this.configureVmlShape(node);
var strokeNode=document.createElement('v:stroke');
node.appendChild(strokeNode);
}
return node;
}
mxPolyline.prototype.createSvg=function(){
var g=this.createSvgGroup('path');



var color=this.innerNode.getAttribute('stroke');
this.pipe=document.createElementNS(mxConstants.NS_SVG,'path');
this.pipe.setAttribute('stroke',color);
this.pipe.setAttribute('visibility','hidden');
this.pipe.setAttribute('pointer-events','stroke');
g.appendChild(this.pipe);
return g;
}
mxPolyline.prototype.redrawSvg=function(){
this.updateSvgShape(this.innerNode);
this.pipe.setAttribute('d',this.innerNode.getAttribute('d'));
this.pipe.setAttribute('stroke-width',this.strokewidth*this.scale+mxShape.prototype.SVG_STROKE_TOLERANCE);
}
}

{
function mxArrow(points,fill,stroke,strokewidth,arrowWidth,spacing,endSize)
{
this.points=points;
this.fill=fill;
this.stroke=stroke;
this.strokewidth=strokewidth||1;
this.arrowWidth=arrowWidth||30;
this.spacing=spacing||10;
this.endSize=endSize||30;
}
mxArrow.prototype=new mxShape();
mxArrow.prototype.constructor=mxArrow;
mxArrow.prototype.createVml=function()
{
var node=document.createElement('v:polyline');
this.configureVmlShape(node);
return node;
}
mxArrow.prototype.redrawVml=function()
{
this.node.setAttribute('strokeweight',this.strokewidth*this.scale);
if(this.points!=null)
{
var spacing=this.spacing*this.scale;
var width=30*this.scale;
var arrow=this.endSize*this.scale;
var p0=this.points[0];
var pe=this.points[this.points.length-1];
var dx=pe.x-p0.x;
var dy=pe.y-p0.y;
var dist=Math.sqrt(dx*dx+dy*dy);
var length=dist-2*spacing-arrow;
var nx=dx/dist;
var ny=dy/dist;
var basex=length*nx;
var basey=length*ny;
var floorx=width*ny/3;
var floory=-width*nx/3;
var p0x=p0.x-floorx/2+spacing*nx;
var p0y=p0.y-floory/2+spacing*ny;
var p1x=p0x+floorx;
var p1y=p0y+floory;
var p2x=p1x+basex;
var p2y=p1y+basey;
var p3x=p2x+floorx;
var p3y=p2y+floory;
var p5x=p3x-3*floorx;
var p5y=p3y-3*floory;
this.node.points.value=p0x+','+p0y+','+p1x+','+p1y+','+p2x+','+p2y+','+p3x+','+p3y+','+(pe.x-spacing*nx)+','+(pe.y-spacing*ny)+','+p5x+','+p5y+','+(p5x+floorx)+','+(p5y+floory)+','+p0x+','+p0y;
}
}
mxArrow.prototype.createSvg=function()
{
var node=document.createElementNS(mxConstants.NS_SVG,'polygon');
this.configureSvgShape(node);
return node;
}
mxArrow.prototype.redrawSvg=function()
{
if(this.points!=null)
{
this.node.setAttribute('stroke-width',this.strokewidth*this.scale);
var p0=this.points[0];
var pe=this.points[this.points.length-1];
var tdx=pe.x-p0.x;
var tdy=pe.y-p0.y;
var dist=Math.sqrt(tdx*tdx+tdy*tdy);
var offset=this.spacing*this.scale;
var h=Math.min(25,Math.max(20,dist/5))*this.scale;
var w=dist-2*offset;
var x=p0.x+offset;
var y=p0.y-h/2;
var dx=h;
var dy=h*0.3;
var right=x+w;
var bottom=y+h;
var points=x+','+(y+dy)+' '+(right-dx)+','+(y+dy)+' '+(right-dx)+','+y+' '+right+','+(y+h/2)+' '+(right-dx)+','+bottom+' '+(right-dx)+','+(bottom-dy)+' '+x+','+(bottom-dy);
this.node.setAttribute('points',points);
var dx=pe.x-p0.x;
var dy=pe.y-p0.y;
var theta=Math.atan(dy/dx)*mxConstants.DEG_PER_RAD;
if(dx<0)
{
theta-=180;
}
this.node.setAttribute('transform','rotate('+theta+','+p0.x+','+p0.y+')');
}
}
}

{
function mxText(value,bounds,align,valign,color,family,size,fontStyle,spacing,spacingTop,spacingRight,spacingBottom,spacingLeft,isRotate)
{
this.value=value||'';
this.bounds=bounds;
this.color=color||'black';
this.align=align||0;
this.valign=valign||0;
this.family=family||'Arial,Helvetica';
this.size=size||12;
this.fontStyle=fontStyle||0;
this.spacing=parseInt(spacing||2);
this.spacingTop=this.spacing+parseInt(spacingTop||0);
this.spacingRight=this.spacing+parseInt(spacingRight||0);
this.spacingBottom=this.spacing+parseInt(spacingBottom||0);
this.spacingLeft=this.spacing+parseInt(spacingLeft||0);
this.isRotate=isRotate||false;
}
mxText.prototype=new mxShape();
mxText.prototype.constructor=mxText;
mxText.prototype.isStyleSet=function(style)
{
return(this.fontStyle&style)==style;
}
mxText.prototype.create=function(container)
{
var node=null;
if(this.dialect==mxConstants.DIALECT_SVG)
{
node=this.createSvg();
}
else if(this.dialect==mxConstants.DIALECT_STRICTHTML||this.dialect==mxConstants.DIALECT_PREFERHTML||container.tagUrn!='urn:schemas-microsoft-com:vml')
{
container.style.overflow='visible';
node=this.createHtml();
}
else
{
node=this.createVml();
}
return node;
}
mxText.prototype.createHtml=function()
{
var node=document.createElement('DIV');
node.style.overflow='visible';
return node;
}
mxText.prototype.createVml=function()
{
var node=document.createElement('v:textbox');
node.style.overflow='visible';
node.inset='0,0,0,0';
return node;
}
mxText.prototype.createSvg=function()
{
var node=document.createElementNS(mxConstants.NS_SVG,'text');
var lines=this.value.split('\n');
for(var i=0;i<lines.length;i++)
{
var tspan=this.createSvgSpan(lines[i]);
node.appendChild(tspan);
}
return node;
}
mxText.prototype.redrawHtml=function()
{
this.redrawVml();
}
mxText.prototype.redrawVml=function()
{

var weight=this.isStyleSet(mxConstants.FONT_BOLD)?'bold':'normal';
var s=this.isStyleSet(mxConstants.FONT_ITALIC)?'font-style:italic;':'';
var wm='';
if(this.isRotate)
{
wm='writing-mode: tb-rl;filter: flipv fliph;'+'padding:'+(this.spacingRight*this.scale)+' '+(this.spacingBottom*this.scale)+' '+(this.spacingRight*this.scale)+' '+(this.spacingTop*this.scale)+'px;';
}
else
{
wm='padding:'+(this.spacingTop*this.scale)+' '+(this.spacingRight*this.scale)+' '+(this.spacingBottom*this.scale)+' '+(this.spacingLeft*this.scale)+'px;';
}
var dim='';
if(this.bounds.width==0&&this.bounds.height==0)
{
this.node.style.left=this.bounds.x+'px';
this.node.style.top=this.bounds.y+'px';
this.node.style.position='absolute';
}
else
{
if(this.node.tagUrn!='urn:schemas-microsoft-com:vml')
{
this.node.style.width=this.bounds.width+'px';
this.node.style.height=this.bounds.height+'px';
}
dim='height=\'100%\' width=\'100%\'';
}
var style='style="font-size:'+(this.size*this.scale)+'px;'+'font-family:'+this.family+';'+'font-weight:'+weight+';'+s+'color:'+this.color+';'+wm+'\"';
var align=(this.align==mxConstants.ALIGN_RIGHT)?'right':(this.align==mxConstants.ALIGN_CENTER)?'center':
'left';
var valign=(this.valign==mxConstants.ALIGN_BOTTOM)?'bottom':(this.valign==mxConstants.ALIGN_MIDDLE)?'middle':
'top';
var prefix='<table border=\'0\' cellspacing=\'0\' '+dim+'>'+'<tr><td align=\''+align+'\' valign=\''+valign+'\' '+style+' nowrap=\'nowrap\'>';
var postfix='</td></tr></table>';
var value=this.value.replace(/\n/g,'<br/>');
if(this.isStyleSet(mxConstants.FONT_SHADOW))
{
var p='<p style=\'height:1em;filter:Shadow(Color=#666666,Direction=135,Strength=%);\'>';
value=p+value+'</p>';
}
this.node.innerHTML=prefix+value+postfix;
}
mxText.prototype.redrawSvg=function()
{
var dy=this.size*1.3*this.scale;
var childCount=this.node.childNodes.length;
var x=this.bounds.x;
var y=this.bounds.y;
x+=(this.align==mxConstants.ALIGN_RIGHT)?this.bounds.width-this.spacingRight*this.scale:(this.align==mxConstants.ALIGN_CENTER)?this.spacingLeft+(this.bounds.width-this.spacingLeft-this.spacingRight)/2:
this.spacingLeft*this.scale;
y+=Math.max(this.spacingTop*this.scale+dy-2,(this.valign==mxConstants.ALIGN_BOTTOM)?this.bounds.height-(childCount-1)*dy-this.spacingBottom*this.scale-3:(this.valign==mxConstants.ALIGN_MIDDLE)?this.spacingTop+(this.bounds.height-this.spacingTop-this.spacingBottom)/2-(childCount-1.5)*dy/2+1:0);
this.node.setAttribute('x',x);
this.node.setAttribute('y',y);
if(this.isRotate)
{
this.node.setAttribute('transform','rotate(-90 '+x+' '+y+')'+' translate('+(-this.bounds.height/2+dy)+' '+(-this.bounds.width/2+dy)+')');
}
for(var i=0;i<childCount;i++)
{
var node=this.node.childNodes[i];
node.setAttribute('font-size',Math.floor(this.size*this.scale)+'px');
node.setAttribute('x',x);
node.setAttribute('y',y);
node.setAttribute('style','pointer-events: all');
y+=dy;
}
}
mxText.prototype.createSvgSpan=function(text)
{
var node=document.createElementNS(mxConstants.NS_SVG,'tspan');
var uline=this.isStyleSet(mxConstants.FONT_UNDERLINE)?'underline':'none';
var weight=this.isStyleSet(mxConstants.FONT_BOLD)?'bold':'normal';
var s=this.isStyleSet(mxConstants.FONT_ITALIC)?'italic;':null;
var align=(this.align==mxConstants.ALIGN_RIGHT)?'end':(this.align==mxConstants.ALIGN_CENTER)?'middle':
'start';
node.setAttribute('text-decoration',uline);
node.setAttribute('text-anchor',align);
node.setAttribute('font-family',this.family);
node.setAttribute('font-weight',weight);
node.setAttribute('font-size',Math.floor(this.size*this.scale)+'px');
if(s!=null)
{
node.setAttribute('font-style',s);
}
node.setAttribute('fill',this.color);
mxUtils.write(node,text);
return node;
}
}

{
function mxLine(bounds,stroke,strokewidth)
{
this.bounds=bounds;
this.stroke=stroke||'black';
this.strokewidth=strokewidth||'1';
}
mxLine.prototype=new mxShape();
mxLine.prototype.constructor=mxLine;
mxLine.prototype.clone=function()
{
var clone=new mxLine(this.bounds,this.stroke,this.strokewidth);
clone.isDashed=this.isDashed;
return clone;
}
mxLine.prototype.createVml=function()
{
var node=document.createElement('v:polyline');
this.configureVmlShape(node);
return node;
}
mxLine.prototype.redrawVml=function()
{
this.node.setAttribute('strokeweight',this.strokewidth*this.scale);
var x=this.bounds.x;
var y=this.bounds.y;
var w=this.bounds.width;
var h=this.bounds.height;
this.node.points.value=x+','+(y+h/2)+' '+(x+w)+','+(y+h/2);;
}
mxLine.prototype.createSvg=function()
{
var g=this.createSvgGroup('path');



var color=this.innerNode.getAttribute('stroke');
this.pipe=document.createElementNS(mxConstants.NS_SVG,'path');
this.pipe.setAttribute('stroke',color);
this.pipe.setAttribute('visibility','hidden');
this.pipe.setAttribute('pointer-events','stroke');
g.appendChild(this.pipe);
return g;
}
mxLine.prototype.redrawSvg=function()
{
this.innerNode.setAttribute('stroke-width',this.strokewidth*this.scale);
if(this.bounds!=null)
{
var x=this.bounds.x;
var y=this.bounds.y;
var w=this.bounds.width;
var h=this.bounds.height;
var d='M '+x+' '+(y+h/2)+' L '+(x+w)+' '+(y+h/2);
this.innerNode.setAttribute('d',d);
this.pipe.setAttribute('d',d);
this.pipe.setAttribute('stroke-width',this.strokewidth*this.scale+mxShape.prototype.SVG_STROKE_TOLERANCE);
}
}
}

{
function mxImage(bounds,image,fill,stroke,strokewidth){
this.bounds=bounds;
this.image=image;
this.fill=fill;
this.stroke=stroke;
this.strokewidth=strokewidth||0;
this.isShadow=false;
}
mxImage.prototype=new mxShape();
mxImage.prototype.constructor=mxImage;
mxImage.prototype.create=function(){
var node=null;
if(this.dialect==mxConstants.DIALECT_SVG)
{




node=this.createSvgGroup('rect');
this.innerNode.setAttribute('fill',this.fill);
this.innerNode.setAttribute('visibility','hidden');
this.innerNode.setAttribute('pointer-events','fill');
this.imageNode=document.createElementNS(mxConstants.NS_SVG,'image');
this.imageNode.setAttributeNS(mxConstants.NS_XLINK,'href',this.image);
this.imageNode.setAttribute('style','pointer-events:none');
this.configureSvgShape(this.imageNode);
node.insertBefore(this.imageNode,this.innerNode);
}
else
{
if(this.dialect==mxConstants.DIALECT_STRICTHTML||this.dialect==mxConstants.DIALECT_PREFERHTML)
{
node=document.createElement('DIV');
this.configureHtmlShape(node);
var imgName=this.image.toUpperCase()
if(imgName.substring(imgName.length-3,imgName.length)=="PNG"&&mxClient.IS_IE&&!mxClient.IS_IE7)
{
node.style.filter='progid:DXImageTransform.Microsoft.AlphaImageLoader (src=\''+this.image+'\', sizingMethod=\'scale\')';
}
else
{
var img=document.createElement('img');
img.setAttribute('src',this.image);
img.style.width='100%';
img.style.height='100%';
img.setAttribute('border','0');
node.appendChild(img);
}
}
else
{
node=document.createElement('v:image');
node.setAttribute('src',this.image);
this.configureVmlShape(node);
}
}
return node;
}
mxImage.prototype.redrawSvg=function()
{
this.updateSvgShape(this.innerNode);
this.updateSvgShape(this.imageNode);
}
}

{
function mxLabel(bounds,fill,stroke,strokewidth)
{
this.bounds=bounds;
this.fill=fill;
this.stroke=stroke;
this.strokewidth=strokewidth||1;
}
mxLabel.prototype=new mxShape();
mxLabel.prototype.constructor=mxLabel;
mxLabel.prototype.imageSize=24;
mxLabel.prototype.spacing=2;
mxLabel.prototype.indicatorSize=10;
mxLabel.prototype.indicatorSpacing=2;
mxLabel.prototype.createHtml=function()
{
var name=(this.isRounded&&mxClient.IS_VML)?'v:roundrect':'DIV';
var node=document.createElement(name);
this.configureHtmlShape(node);
if(this.indicatorColor!=null&&this.indicatorShape!=null)
{
this.indicator=new this.indicatorShape(this.bounds);
this.indicator.dialect=this.dialect;
this.indicator.fill=this.indicatorColor;
this.indicator.gradient=this.indicatorGradientColor;
this.indicator.init(node);
}
else if(this.indicatorImage!=null)
{
this.indicatorImageNode=mxUtils.createImage(this.indicatorImage);
this.indicatorImageNode.style.position='absolute';
node.appendChild(this.indicatorImageNode);
}
if(this.image!=null)
{
this.imageNode=mxUtils.createImage(this.image);
this.stroke=null;
this.configureHtmlShape(this.imageNode);
node.appendChild(this.imageNode);
}
return node;
}
mxLabel.prototype.createVml=function()
{
var node=document.createElement('v:group');
var name=(this.isRounded)?'v:roundrect':'v:rect';
this.rectNode=document.createElement(name);
this.configureVmlShape(this.rectNode);
this.isShadow=false;
this.configureVmlShape(node);
node.setAttribute('coordorigin','0,0');
node.appendChild(this.rectNode);
if(this.indicatorColor!=null&&this.indicatorShape!=null)
{
this.indicator=new this.indicatorShape(this.bounds);
this.indicator.dialect=this.dialect;
this.indicator.fill=this.indicatorColor;
this.indicator.gradient=this.indicatorGradientColor;
this.indicator.init(node);
}
else if(this.indicatorImage!=null)
{
this.indicatorImageNode=document.createElement('v:image');
this.indicatorImageNode.setAttribute('src',this.indicatorImage);
node.appendChild(this.indicatorImageNode);
}
if(this.image!=null)
{
this.imageNode=document.createElement('v:image');
this.imageNode.setAttribute('src',this.image);
this.configureVmlShape(this.imageNode);
node.appendChild(this.imageNode);
}
this.label=document.createElement('v:rect');
this.label.style.top='0';
this.label.style.left='0';
this.label.setAttribute('filled','false');
this.label.setAttribute('stroked','false');
node.appendChild(this.label);
return node;
}
mxLabel.prototype.createSvg=function()
{
var g=this.createSvgGroup('rect');
if(this.strokewidth*this.scale>=1&&!this.isRounded)
{
this.innerNode.setAttribute('shape-rendering','optimizeSpeed');
}
if(this.indicatorColor!=null&&this.indicatorShape!=null)
{
this.indicator=new this.indicatorShape(this.bounds);
this.indicator.dialect=this.dialect;
this.indicator.fill=this.indicatorColor;
this.indicator.gradient=this.indicatorGradientColor;
this.indicator.init(g);
}
else if(this.indicatorImage!=null)
{
this.indicatorImageNode=document.createElementNS(mxConstants.NS_SVG,'image');
this.indicatorImageNode.setAttributeNS(mxConstants.NS_XLINK,'href',this.indicatorImage);
g.appendChild(this.indicatorImageNode);
}
if(this.image!=null)
{
this.imageNode=document.createElementNS(mxConstants.NS_SVG,'image');
this.imageNode.setAttributeNS(mxConstants.NS_XLINK,'href',this.image);
this.imageNode.setAttribute('style','pointer-events:none');
this.configureSvgShape(this.imageNode);
g.appendChild(this.imageNode);
}
return g;
}
mxLabel.prototype.redraw=function()
{
var isSvg=(this.dialect==mxConstants.DIALECT_SVG);
var isVml=(this.node.tagUrn=='urn:schemas-microsoft-com:vml');
if(isSvg)
{
this.updateSvgShape(this.innerNode);
if(this.shadowNode!=null)
{
this.updateSvgShape(this.shadowNode);
}
}
else if(isVml)
{
this.updateVmlShape(this.node);
this.node.setAttribute('coordsize',this.bounds.width+','+this.bounds.height);
this.updateVmlShape(this.rectNode);
this.rectNode.style.top='0';
this.rectNode.style.left='0';
this.label.style.width=this.bounds.width;
this.label.style.height=this.bounds.height;
}
else
{
this.updateHtmlShape(this.node);
}
var imageWidth=0;
var imageHeight=0;
if(this.imageNode!=null)
{
imageWidth=(this.style[mxConstants.STYLE_IMAGE_WIDTH]||this.imageSize)*this.scale;
imageHeight=(this.style[mxConstants.STYLE_IMAGE_HEIGHT]||this.imageSize)*this.scale;
}
var indicatorSpacing=0;
var indicatorWidth=0;
var indicatorHeight=0;
if(this.indicator!=null||this.indicatorImageNode!=null)
{
indicatorSpacing=(this.style[mxConstants.STYLE_INDICATOR_SPACING]||this.indicatorSpacing)*this.scale;
indicatorWidth=(this.style[mxConstants.STYLE_INDICATOR_WIDTH]||this.indicatorSize)*this.scale;
indicatorHeight=(this.style[mxConstants.STYLE_INDICATOR_HEIGHT]||this.indicatorSize)*this.scale;
}
var align=this.style[mxConstants.STYLE_IMAGE_ALIGN];
var valign=this.style[mxConstants.STYLE_IMAGE_VERTICAL_ALIGN];
var inset=this.spacing*this.scale;
var width=Math.max(imageWidth,indicatorWidth);
var height=imageHeight+indicatorSpacing+indicatorHeight;
var x=(isSvg)?this.bounds.x:0;
if(align==mxConstants.ALIGN_RIGHT)
{
x+=this.bounds.width-width-inset;
}
else if(align==mxConstants.ALIGN_CENTER)
{
x+=(this.bounds.width-width)/2;
}
else 
{
x+=inset;
}
var y=(isSvg)?this.bounds.y:0;
if(valign==mxConstants.ALIGN_BOTTOM)
{
y+=this.bounds.height-height-inset;
}
else if(valign==mxConstants.ALIGN_TOP)
{
y+=inset;
}
else 
{
y+=(this.bounds.height-height)/2;
}
if(this.imageNode!=null)
{
if(isSvg)
{
this.imageNode.setAttribute('x',(x+(width-imageWidth)/2)+'px');
this.imageNode.setAttribute('y',y+'px');
this.imageNode.setAttribute('width',imageWidth+'px');
this.imageNode.setAttribute('height',imageHeight+'px');
}
else
{
this.imageNode.style.left=(x+width-imageWidth)+'px';
this.imageNode.style.top=y+'px';
this.imageNode.style.width=imageWidth+'px';
this.imageNode.style.height=imageHeight+'px';
}
}
if(this.indicator!=null)
{
this.indicator.bounds=new mxRectangle(x+(width-indicatorWidth)/2,y+imageHeight+indicatorSpacing,indicatorWidth,indicatorHeight);
this.indicator.redraw();
}
else if(this.indicatorImageNode!=null)
{
if(isSvg)
{
this.indicatorImageNode.setAttribute('x',(x+(width-indicatorWidth)/2)+'px');
this.indicatorImageNode.setAttribute('y',(y+imageHeight+indicatorSpacing)+'px');
this.indicatorImageNode.setAttribute('width',indicatorWidth+'px');
this.indicatorImageNode.setAttribute('height',indicatorHeight+'px');
}
else
{
this.indicatorImageNode.style.left=(x+(width-indicatorWidth)/2)+'px';
this.indicatorImageNode.style.top=(y+imageHeight+indicatorSpacing)+'px';
this.indicatorImageNode.style.width=indicatorWidth+'px';
this.indicatorImageNode.style.height=indicatorHeight+'px';
}
}
}
}

{
function mxCylinder(bounds,fill,stroke,strokewidth)
{
this.bounds=bounds;
this.fill=fill;
this.stroke=stroke;
this.strokewidth=strokewidth||1;
}
mxCylinder.prototype=new mxShape();
mxCylinder.prototype.constructor=mxCylinder;
mxCylinder.prototype.maxHeight=40;
mxCylinder.prototype.create=function(container)
{

if(this.stroke==null)
{
this.stroke=this.fill;
}
return mxShape.prototype.create.apply(this,arguments);
}
mxCylinder.prototype.createVml=function()
{
var node=document.createElement('v:group');
node.setAttribute('coordorigin','0,0');
this.background=document.createElement('v:shape');
this.label=this.background;
this.configureVmlShape(this.background);
node.appendChild(this.background);
this.fill=null;
this.isShadow=false;
this.configureVmlShape(node);
this.foreground=document.createElement('v:shape');
this.configureVmlShape(this.foreground);
node.appendChild(this.foreground);
return node;
}
mxCylinder.prototype.redrawVml=function()
{
var x=Math.floor(this.bounds.x);
var y=Math.floor(this.bounds.y);
var w=Math.floor(this.bounds.width);
var h=Math.floor(this.bounds.height);
var s=this.strokewidth*this.scale;
this.node.setAttribute('coordsize',w+','+h);
this.background.setAttribute('coordsize',w+','+h);
this.foreground.setAttribute('coordsize',w+','+h);
this.updateVmlShape(this.node);
this.updateVmlShape(this.background);
this.background.style.top='0';
this.background.style.left='0';
this.updateVmlShape(this.foreground);
this.foreground.style.top='0';
this.foreground.style.left='0';
this.background.setAttribute('strokeweight',s);
this.foreground.setAttribute('strokeweight',s);
var d=this.createPath(false);
this.background.setAttribute('path',d);
var d=this.createPath(true);
this.foreground.setAttribute('path',d);
}
mxCylinder.prototype.createSvg=function()
{
var g=this.createSvgGroup('path');
this.foreground=document.createElementNS(mxConstants.NS_SVG,'path');
if(this.stroke!=null)
{
this.foreground.setAttribute('stroke',this.stroke);
}
else
{
this.foreground.setAttribute('stroke','none');
}
this.foreground.setAttribute('fill','none');
g.appendChild(this.foreground);
return g;
}
mxCylinder.prototype.redrawSvg=function()
{
var s=this.strokewidth*this.scale;
var d=this.createPath(false);
this.innerNode.setAttribute('stroke-width',s);
this.innerNode.setAttribute('d',d);
if(this.shadowNode!=null)
{
this.shadowNode.setAttribute('stroke-width',s);
this.shadowNode.setAttribute('d',d);
}
d=this.createPath(true);
this.foreground.setAttribute('stroke-width',s);
this.foreground.setAttribute('d',d);
}
mxCylinder.prototype.redrawPath=function(path,x,y,w,h,isForeground)
{
var dy=Math.min(this.maxHeight,Math.floor(h/5));
if(isForeground)
{
path.moveTo(0,dy);
path.curveTo(0,2*dy,w,2*dy,w,dy);
}
else
{
path.moveTo(0,dy);
path.curveTo(0,-dy/3,w,-dy/3,w,dy);
path.lineTo(w,h-dy);
path.curveTo(w,h+dy/3,0,h+dy/3,0,(h-dy));
path.close();
}
}
}

{
function mxConnector(points,stroke,strokewidth)
{
this.points=points;
this.stroke=stroke||'black';
this.strokewidth=strokewidth||1;
}
mxConnector.prototype=new mxShape();
mxConnector.prototype.constructor=mxConnector;
mxConnector.prototype.clone=function()
{
var clone=new mxPolyline(this.points,this.stroke,this.strokewidth);
clone.isDashed=this.isDashed;
return clone;
}
mxConnector.prototype.create=function()
{
var node=null;
if(this.dialect==mxConstants.DIALECT_SVG)
{
node=this.createSvg();
}
else if(this.dialect==mxConstants.DIALECT_STRICTHTML||this.dialect==mxConstants.DIALECT_PREFERHTML)
{
node=this.createHtml();
}
else
{
node=this.createVml();
}
return node;
}
mxConnector.prototype.createHtml=function()
{
var node=document.createElement('DIV');
this.configureHtmlShape(node);
node.style.borderStyle='none';
node.style.background='';
return node;
}
mxConnector.prototype.createVml=function()
{
var node=document.createElement('v:shape');
this.configureVmlShape(node);
var strokeNode=document.createElement('v:stroke');
strokeNode.setAttribute('endarrow',this.endArrow);
strokeNode.setAttribute('startarrow',this.startArrow);
node.appendChild(strokeNode);
return node;
}
mxConnector.prototype.createSvg=function()
{
var g=this.createSvgGroup('path');
var color=this.innerNode.getAttribute('stroke');
if(this.startArrow!=null)
{
this.start=document.createElementNS(mxConstants.NS_SVG,'path');
this.start.setAttribute('stroke',color);
this.start.setAttribute('fill',color);
g.appendChild(this.start);
}
if(this.endArrow!=null)
{
this.end=document.createElementNS(mxConstants.NS_SVG,'path');
this.end.setAttribute('stroke',color);
this.end.setAttribute('fill',color);
g.appendChild(this.end);
}



this.pipe=document.createElementNS(mxConstants.NS_SVG,'path');
this.pipe.setAttribute('stroke',color);
this.pipe.setAttribute('visibility','hidden');
this.pipe.setAttribute('pointer-events','stroke');
g.appendChild(this.pipe);
return g;
}
mxConnector.prototype.redrawSvg=function()
{
this.updateSvgShape(this.innerNode);
this.pipe.setAttribute('d',this.innerNode.getAttribute('d'));
this.pipe.setAttribute('stroke-width',this.strokewidth*this.scale+mxShape.prototype.SVG_STROKE_TOLERANCE);
if(this.points!=null&&(this.end!=null||this.start!=null))
{
if(this.start!=null)
{
this.start.setAttribute('stroke-width',this.strokewidth*this.scale);
var p0=this.points[1];
var pe=this.points[0];
var dx=pe.x-p0.x;
var dy=pe.y-p0.y;
var dist=Math.max(1,Math.sqrt(dx*dx+dy*dy));
var size=6*this.scale;
var nx=dx*size/dist;
var ny=dy*size/dist;
pe.x-=nx*this.strokewidth*this.scale/6;
pe.y-=ny*this.strokewidth*this.scale/6;
var d='M '+pe.x+' '+pe.y+' L '+(pe.x-nx-ny/2)+' '+(pe.y-ny+nx/2)+' L '+(pe.x-nx/2)+' '+(pe.y-ny/2)+' L '+(pe.x+ny/2-nx)+' '+(pe.y-ny-nx/2)+' z';
this.start.setAttribute('d',d);
}
if(this.end!=null)
{
this.end.setAttribute('stroke-width',this.strokewidth*this.scale);
var p0=this.points[this.points.length-2];
var pe=this.points[this.points.length-1];
var dx=pe.x-p0.x;
var dy=pe.y-p0.y;
var dist=Math.max(1,Math.sqrt(dx*dx+dy*dy));
var size=6*this.scale;
var nx=dx*size/dist;
var ny=dy*size/dist;
pe.x-=nx*this.strokewidth*this.scale/6;
pe.y-=ny*this.strokewidth*this.scale/6;
var d='M '+pe.x+' '+pe.y+' L '+(pe.x-nx-ny/2)+' '+(pe.y-ny+nx/2)+' L '+(pe.x-nx/2)+' '+(pe.y-ny/2)+' L '+(pe.x+ny/2-nx)+' '+(pe.y-ny-nx/2)+' z';
this.end.setAttribute('d',d);
}
}
}
}

{
function mxSwimlane(bounds,fill,stroke,strokewidth)
{
this.bounds=bounds;
this.fill=fill;
this.stroke=stroke;
this.strokewidth=strokewidth||1;
}
mxSwimlane.prototype=new mxShape();
mxSwimlane.prototype.constructor=mxSwimlane;
mxSwimlane.prototype.imageSize=16;
mxSwimlane.prototype.defaultStartSize=40;
mxSwimlane.prototype.createHtml=function()
{
var node=document.createElement('DIV');
this.configureHtmlShape(node);
node.style.background='';
node.style.backgroundColor='';
node.style.borderStyle='none';
this.label=document.createElement('DIV');
this.configureHtmlShape(this.label);
node.appendChild(this.label);
this.content=document.createElement('DIV');
var tmp=this.fill;
this.configureHtmlShape(this.content);
this.content.style.background='';
this.content.style.backgroundColor='';
if(this.style[mxConstants.STYLE_HORIZONTAL]!="true")
{
this.content.style.borderTopStyle='none';
}
else
{
this.content.style.borderLeftStyle='none';
}
this.content.style.cursor='default';
node.appendChild(this.content);
var color=this.style[mxConstants.STYLE_SEPARATORCOLOR];
if(color!=null)
{
this.separator=document.createElement('DIV');
this.separator.style.borderColor=color;
this.separator.style.borderLeftStyle='dashed';
node.appendChild(this.separator);
}
if(this.image!=null)
{
this.imageNode=mxUtils.createImage(this.image);
this.configureHtmlShape(this.imageNode);
this.imageNode.style.borderStyle='none';
node.appendChild(this.imageNode);
}
return node;
}
mxSwimlane.prototype.redrawHtml=function(){
this.updateHtmlShape(this.node);
this.startSize=this.style[mxConstants.STYLE_STARTSIZE]||40;
this.updateHtmlShape(this.label);
this.label.style.top='0';
this.label.style.left='0';
if(this.style[mxConstants.STYLE_HORIZONTAL]!="true")
{
this.label.style.height=this.startSize*this.scale;
this.updateHtmlShape(this.content);
var h=this.startSize*this.scale;
this.content.style.top=h;
this.content.style.left='0';
this.content.style.height=Math.max(1,this.bounds.height-h);
if(this.separator!=null)
{
this.separator.style.left=Math.floor.floor(this.bounds.width)+'px';
this.separator.style.top=parseInt(this.startSize*this.scale)+'px';
this.separator.style.width='1px';
this.separator.style.height=Math.floor(this.bounds.height)+'px';
this.separator.style.borderWidth=Math.floor(this.scale);
}
if(this.imageNode!=null)
{
this.imageNode.style.left=(this.bounds.width-this.imageSize-4)+'px';
this.imageNode.style.top='0px';
this.imageNode.style.width=Math.floor(this.imageSize*this.scale)+'px';
this.imageNode.style.height=Math.floor(this.imageSize*this.scale)+'px';
}
}
else
{
this.label.style.width=this.startSize*this.scale;
this.updateHtmlShape(this.content);
var w=this.startSize*this.scale;
this.content.style.top='0';
this.content.style.left=w;
this.content.style.width=Math.max(0,this.bounds.width-w);
if(this.separator!=null)
{
this.separator.style.left=Math.floor(this.startSize*this.scale)+'px';
this.separator.style.top=Math.floor(this.bounds.height)+'px';
this.separator.style.width=Math.floor(this.bounds.width)+'px';
this.separator.style.height='1px';
}
if(this.imageNode!=null)
{
this.imageNode.style.left=(this.bounds.width-this.imageSize-4)+'px';
this.imageNode.style.top='0px';
this.imageNode.style.width=this.imageSize*this.scale+'px';
this.imageNode.style.height=this.imageSize*this.scale+'px';
}
}
}
mxSwimlane.prototype.createVml=function()
{
var node=document.createElement('v:group');
var name=(this.isRounded)?'v:roundrect':'v:rect';
this.label=document.createElement(name);
this.configureVmlShape(this.label);
if(this.isRounded)
{
this.label.setAttribute('arcsize','20%');
}
this.isShadow=false;
this.configureVmlShape(node);
node.setAttribute('coordorigin','0,0');
node.appendChild(this.label);
this.content=document.createElement(name);
var tmp=this.fill;
this.fill=null;
this.configureVmlShape(this.content);
if(this.isRounded)
{
this.content.setAttribute('arcsize','4%');
}
this.fill=tmp;
this.content.style.borderBottom='0px';
node.appendChild(this.content);
var color=this.style[mxConstants.STYLE_SEPARATORCOLOR];
if(color!=null)
{
this.separator=document.createElement('v:polyline');
this.separator.setAttribute('strokecolor',color);
var strokeNode=document.createElement('v:stroke');
strokeNode.setAttribute('dashstyle','2 2');
this.separator.appendChild(strokeNode);
node.appendChild(this.separator);
}
if(this.image!=null)
{
this.imageNode=document.createElement('v:image');
this.imageNode.setAttribute('src',this.image);
this.configureVmlShape(this.imageNode);
node.appendChild(this.imageNode);
}
return node;
}
mxSwimlane.prototype.redrawVml=function(){
this.updateVmlShape(this.node);
this.startSize=this.style[mxConstants.STYLE_STARTSIZE]||40;
this.node.setAttribute('coordsize',this.bounds.width+','+this.bounds.height);
this.updateVmlShape(this.label);
this.label.style.top='0';
this.label.style.left='0';
if(this.style[mxConstants.STYLE_HORIZONTAL]!="true")
{
this.label.style.height=this.startSize*this.scale;
this.updateVmlShape(this.content);
var h=this.startSize*this.scale;
this.content.style.top=h;
this.content.style.left='0';
this.content.style.height=Math.max(0,this.bounds.height-h);
if(this.separator!=null)
{
this.separator.points.value=Math.floor(this.bounds.width)+','+Math.floor(this.startSize*this.scale)+' '+Math.floor(this.bounds.width)+','+Math.floor(this.bounds.height);
}
if(this.imageNode!=null)
{
this.imageNode.style.left=(this.bounds.width-this.imageSize-4)+'px';
this.imageNode.style.top='0px';
this.imageNode.style.width=Math.floor(this.imageSize*this.scale)+'px';
this.imageNode.style.height=Math.floor(this.imageSize*this.scale)+'px';
}
}
else
{
this.label.style.width=this.startSize*this.scale;
this.updateVmlShape(this.content);
var w=this.startSize*this.scale;
this.content.style.top='0';
this.content.style.left=w;
this.content.style.width=Math.max(0,this.bounds.width-w);
if(this.separator!=null)
{
this.separator.points.value='0,'+Math.floor(this.bounds.height)+' '+Math.floor(this.bounds.width+this.startSize*this.scale)+','+Math.floor(this.bounds.height);
}
if(this.imageNode!=null)
{
this.imageNode.style.left=(this.bounds.width-this.imageSize-4)+'px';
this.imageNode.style.top='0px';
this.imageNode.style.width=Math.floor(this.imageSize*this.scale)+'px';
this.imageNode.style.height=Math.floor(this.imageSize*this.scale)+'px';
}
}
}
mxSwimlane.prototype.createSvg=function(){
var node=this.createSvgGroup('rect');
if(this.strokewidth*this.scale>=1&&!this.isRounded)
{
this.innerNode.setAttribute('shape-rendering','optimizeSpeed');
}
if(this.isRounded)
{
this.innerNode.setAttribute('rx',10);
this.innerNode.setAttribute('ry',10);
}
this.content=document.createElementNS(mxConstants.NS_SVG,'path');
this.configureSvgShape(this.content);
this.content.setAttribute('fill','none');
if(this.strokewidth*this.scale>=1&&!this.isRounded)
{
this.content.setAttribute('shape-rendering','optimizeSpeed');
}
if(this.isRounded)
{
this.content.setAttribute('rx',10);
this.content.setAttribute('ry',10);
}
node.appendChild(this.content);
var color=this.style[mxConstants.STYLE_SEPARATORCOLOR];
if(color!=null)
{
this.separator=document.createElementNS(mxConstants.NS_SVG,'line');
this.separator.setAttribute('stroke',color);
this.separator.setAttribute('fill','none');
this.separator.setAttribute('stroke-dasharray','2, 2');
this.separator.setAttribute('shape-rendering','optimizeSpeed');
node.appendChild(this.separator);
}
if(this.image!=null)
{
this.imageNode=document.createElementNS(mxConstants.NS_SVG,'image');
this.imageNode.setAttributeNS(mxConstants.NS_XLINK,'href',this.image);
this.configureSvgShape(this.imageNode);
node.appendChild(this.imageNode);
}
return node;
}
mxSwimlane.prototype.redrawSvg=function(){
var tmp=this.isRounded;
this.isRounded=false;
this.updateSvgShape(this.innerNode);
this.updateSvgShape(this.content);
if(this.shadowNode!=null)
{
this.updateSvgShape(this.shadowNode);
if(this.style[mxConstants.STYLE_HORIZONTAL]!="true")
{
this.shadowNode.setAttribute('height',this.startSize*this.scale);
}
else
{
this.shadowNode.setAttribute('width',this.startSize*this.scale);
}
}
this.isRounded=tmp;
this.startSize=this.style[mxConstants.STYLE_STARTSIZE]||40;
if(this.style[mxConstants.STYLE_HORIZONTAL]!="true")
{
this.innerNode.setAttribute('height',this.startSize*this.scale);
var h=this.startSize*this.scale;
var points='M '+this.bounds.x+' '+(this.bounds.y+h)+' l 0 '+(this.bounds.height-h)+' l '+this.bounds.width+' 0'+' l 0 '+(-this.bounds.height+h);
this.content.setAttribute('d',points);
this.content.removeAttribute('x');
this.content.removeAttribute('y');
this.content.removeAttribute('width');
this.content.removeAttribute('height');
if(this.separator!=null)
{
this.separator.setAttribute('x1',this.bounds.x+this.bounds.width);
this.separator.setAttribute('y1',this.bounds.y+this.startSize*this.scale);
this.separator.setAttribute('x2',this.bounds.x+this.bounds.width);
this.separator.setAttribute('y2',this.bounds.y+this.bounds.height);
}
if(this.imageNode!=null)
{
this.imageNode.setAttribute('x',this.bounds.x+this.bounds.width-this.imageSize-4);
this.imageNode.setAttribute('y',this.bounds.y);
this.imageNode.setAttribute('width',this.imageSize*this.scale+'px');
this.imageNode.setAttribute('height',this.imageSize*this.scale+'px');
}
}
else
{
this.innerNode.setAttribute('width',this.startSize*this.scale);
var w=this.startSize*this.scale;
var points='M '+(this.bounds.x+w)+' '+this.bounds.y+' l '+(this.bounds.width-w)+' 0'+' l 0 '+this.bounds.height+' l '+(-this.bounds.width+w)+' 0';
this.content.setAttribute('d',points);
this.content.removeAttribute('x');
this.content.removeAttribute('y');
this.content.removeAttribute('width');
this.content.removeAttribute('height');
if(this.separator!=null)
{
this.separator.setAttribute('x1',this.bounds.x+this.startSize*this.scale);
this.separator.setAttribute('y1',this.bounds.y+this.bounds.height);
this.separator.setAttribute('x2',this.bounds.x+this.bounds.width);
this.separator.setAttribute('y2',this.bounds.y+this.bounds.height);
}
if(this.imageNode!=null)
{
this.imageNode.setAttribute('x',this.bounds.x+this.bounds.width-this.imageSize-4);
this.imageNode.setAttribute('y',this.bounds.y);
this.imageNode.setAttribute('width',this.imageSize*this.scale+'px');
this.imageNode.setAttribute('height',this.imageSize*this.scale+'px');
}
}
}
}

{
function mxFlowLayout(graph,spacing,isVertical,x0,y0){
this.graph=graph;
this.isVertical=isVertical;
this.spacing=(spacing!=null)?spacing:graph.gridSize;
this.x0=(x0!=null)?x0:this.spacing;
this.y0=(y0!=null)?x0:this.spacing;
}
mxFlowLayout.prototype.move=function(cell,x,y){
var model=this.graph.getModel();
var parent=model.getParent(cell);
if(cell!=null&&parent!=null){
var index=0;
var last=0;
var childCount=model.getChildCount(parent);

for(index=0;index<childCount;index++){
var child=model.getChildAt(parent,index);
var bounds=this.graph.getCellBounds(child);
if(bounds!=null){
var tmp=bounds.x+bounds.width/2;
if(last<x&&tmp>x){
break;
}
last=tmp;
}
}
var idx=parent.getIndex(cell);
var idx=Math.max(0,index-((index>idx)?1:0));
model.add(parent,cell,idx);
}
}
mxFlowLayout.prototype.execute=function(parent){
if(parent!=null){
var model=this.graph.getModel();
model.beginUpdate();
try
{
var last=null;
var childCount=model.getChildCount(parent);
for(var i=0;i<childCount;i++){
var child=model.getChildAt(parent,i);
if(!this.isIgnored(child)){
var geo=model.getGeometry(child);
if(geo!=null){
geo=geo.clone();
if(last!=null){
if(this.isVertical){
geo.y=last.y+last.height+this.spacing;
}else{
geo.x=last.x+last.width+this.spacing;
}
}else{
if(this.isVertical){
geo.y=this.y0;
}else{
geo.x=this.x0;
}
}
if(this.isVertical){
geo.x=this.x0;
}else{
geo.y=this.y0;
}
model.setGeometry(child,geo);
last=geo;
}
}
}
}
finally
{
model.endUpdate();
}
}
}
mxFlowLayout.prototype.isIgnored=function(cell){
return!this.graph.isSwimlane(cell);
}
}

{
function mxCompactTreeLayout(graph,isHorizontal){
this.graph=graph;
this.isHorizontal=(isHorizontal!=null)?isHorizontal:true;
this.levelDistance=10;
this.nodeDistance=20;
}
mxCompactTreeLayout.prototype.move=function(cell,x,y){

}
mxCompactTreeLayout.prototype.execute=function(parent){
var model=this.graph.getModel();
var root=null;
if(model.getEdgeCount(parent)>0){
root=parent;
}else{
var roots=this.graph.findTreeRoots(parent);
if(roots.length>0){
root=roots[0];
}
}
if(root!=null){
var swimlane=this.graph.getSwimlane(root);
var node=this.dfs(root,swimlane);
if(node!=null){
model.beginUpdate();
try
{
this.layout(node);
var x0=this.graph.gridSize;
var y0=this.graph.gridSize;
if(swimlane==null){
var g=model.getGeometry(root);
if(g!=null){
x0=g.x;
y0=g.y;
}
}
var bounds=null;
if(this.isHorizontal){
bounds=this.horizontalLayout(node,x0,y0);
}else{
bounds=this.verticalLayout(node,null,x0,y0);
}
if(bounds!=null){
if(swimlane!=null){
var width=bounds.width-bounds.x;
var height=bounds.height-bounds.y;
var style=this.graph.getCellStyle(swimlane);
var isHorizontal=style[mxConstants.STYLE_HORIZONTAL]=="true";
var offsetX=(isHorizontal)?40:10;
var offsetY=(isHorizontal)?10:40;
this.moveNode(node,offsetX-bounds.x,offsetY-bounds.y);
var g=model.getGeometry(swimlane);
if(swimlane!=null&&g!=null){
var g=g.clone();
g.height=(isHorizontal)?Math.max(g.height,height+20):height+60;
g.width=(isHorizontal)?width+60:Math.max(g.width,width+20);
this.graph.resize(swimlane,g);
}
}
}
}
finally
{
model.endUpdate();
}
}
}
}
mxCompactTreeLayout.prototype.moveNode=function(node,dx,dy){
node.x+=dx;
node.y+=dy;
this.apply(node);
var child=node.child;
if(child!=null){
this.moveNode(child,dx,dy);
var s=child.next;
while(s!=null){
this.moveNode(s,dx,dy);
s=s.next;
}
}
}
mxCompactTreeLayout.prototype.dfs=function(cell,swimlane){
var node=null;
if(cell!=null&&cell._visited==null){
var model=this.graph.getModel();
cell._visited=true;
node=this.createNode(cell);
var edgeCount=model.getEdgeCount(cell);
var prev=null;
for(var i=0;i<edgeCount;i++){
var edge=model.getEdgeAt(cell,i);
if(model.getTerminal(edge,true)==cell){
var target=model.getTerminal(edge,false);
if(swimlane==null||swimlane==
this.graph.getSwimlane(target))
{
var tmp=this.dfs(target,swimlane);
if(tmp!=null&&model.getGeometry(target)!=null){
if(prev==null){
node.child=tmp;
}else{
prev.next=tmp;
}
prev=tmp;
}
}
}
}
}
return node;
}
mxCompactTreeLayout.prototype.layout=function(node){
if(node!=null){
node.cell._visited=null;
var child=node.child;
while(child!=null){
this.layout(child);
child=child.next;
}
if(node.child!=null){
this.attachParent(node,this.join(node));
}else{
this.layoutLeaf(node);
}
}
}
mxCompactTreeLayout.prototype.apply=function(node,bounds){
var g=this.graph.getModel().getGeometry(node.cell);
if(node.cell!=null&&g!=null){
if(g.x!=node.x||g.y!=node.y){
g=g.clone();
g.x=node.x;
g.y=node.y;
this.graph.getModel().setGeometry(node.cell,g);
}
if(bounds==null){
bounds=new mxRectangle(g.x,g.y,g.width,g.height);
}else{
bounds=new mxRectangle(Math.min(bounds.x,g.x),Math.min(bounds.y,g.y),Math.max(bounds.x+bounds.width,g.x+g.width),Math.max(bounds.y+bounds.height,g.y+g.height));
}
}
return bounds;
}
mxCompactTreeLayout.prototype.horizontalLayout=function(node,x0,y0,bounds){
node.x+=x0+node.offsetX;
node.y+=y0+node.offsetY;
bounds=this.apply(node,bounds);
var child=node.child;
if(child!=null){
bounds=this.horizontalLayout(child,node.x,node.y,bounds);
var siblingOffset=node.y+child.offsetY;
var s=child.next;
while(s!=null){
bounds=this.horizontalLayout(s,node.x+child.offsetX,siblingOffset,bounds);
siblingOffset+=s.offsetY;
s=s.next;
}
}
return bounds;
}
mxCompactTreeLayout.prototype.verticalLayout=function(node,parent,x0,y0,bounds){
node.x+=x0+node.offsetY;
node.y+=y0+node.offsetX;
bounds=this.apply(node,bounds);
var child=node.child;
if(child!=null){
bounds=this.verticalLayout(child,node,node.x,node.y,bounds);
var siblingOffset=node.x+child.offsetY;
var s=child.next;
while(s!=null){
bounds=this.verticalLayout(s,node,siblingOffset,node.y+child.offsetX,bounds);
siblingOffset+=s.offsetY;
s=s.next;
}
}
return bounds;
}
mxCompactTreeLayout.prototype.attachParent=function(node,height){
var x=this.nodeDistance+this.levelDistance;
var y2=(height-node.width)/2-this.nodeDistance;
var y1=y2+node.width+2*this.nodeDistance-height;
node.child.offsetX=x+node.height;
node.child.offsetY=y1;
node.contour.upperHead=this.createLine(node.height,0,this.createLine(x,y1,node.contour.upperHead));
node.contour.lowerHead=this.createLine(node.height,0,this.createLine(x,y2,node.contour.lowerHead));
}
mxCompactTreeLayout.prototype.layoutLeaf=function(node){
var dist=2*this.nodeDistance;
node.contour.upperTail=this.createLine(node.height+dist,0,null);
node.contour.upperHead=node.contour.upperTail;
node.contour.lowerTail=this.createLine(0,-node.width-dist,null);
node.contour.lowerHead=this.createLine(node.height+dist,0,node.contour.lowerTail);
}
mxCompactTreeLayout.prototype.join=function(node){
var dist=2*this.nodeDistance;
var child=node.child;
node.contour=child.contour;
var h=child.width+dist;
var sum=h;
child=child.next;
while(child!=null){
var d=this.merge(node.contour,child.contour);
child.offsetY=d+h;
child.offsetX=0;
h=child.width+dist;
sum+=d+h;
child=child.next;
}
return sum;
}
mxCompactTreeLayout.prototype.merge=function(p1,p2){
var x=0;
var y=0;
var total=0;
var upper=p1.lowerHead;
var lower=p2.upperHead;
while(lower!=null&&upper!=null){
var d=this.offset(x,y,lower.dx,lower.dy,upper.dx,upper.dy);
y+=d;
total+=d;
if(x+lower.dx<=upper.dx){
x+=lower.dx;
y+=lower.dy;
lower=lower.next;
}else{
x-=upper.dx;
y-=upper.dy;
upper=upper.next;
}
}
if(lower!=null){
var b=this.bridge(p1.upperTail,0,0,lower,x,y);
p1.upperTail=(b.next!=null)?p2.upperTail:b;
p1.lowerTail=p2.lowerTail;
}else{
var b=this.bridge(p2.lowerTail,x,y,upper,0,0);
if(b.next==null){
p1.lowerTail=b;
}
}
p1.lowerHead=p2.lowerHead;
return total;
}
mxCompactTreeLayout.prototype.offset=function(p1,p2,a1,a2,b1,b2){
var d=0;
if(b1<=p1||p1+a1<=0){
return 0;
}
var t=b1*a2-a1*b2;
if(t>0){
if(p1<0){
var s=p1*a2;
d=s/a1-p2;
}else if(p1>0){
var s=p1*b2;
d=s/b1-p2;
}else{
d=-p2;
}
}else if(b1<p1+a1){
var s=(b1-p1)*a2;
d=b2-(p2+s/a1);
}else if(b1>p1+a1){
var s=(a1+p1)*b2;
d=s/b1-(p2+a2);
}else{
d=b2-(p2+a2);
}
if(d>0){
return d;
}else{
return 0;
}
}
mxCompactTreeLayout.prototype.bridge=function(line1,x1,y1,line2,x2,y2){
var dx=x2+line2.dx-x1;
var dy=0;
var s=0;
if(line2.dx==0){
dy=line2.dy;
}else{
var s=dx*line2.dy;
dy=s/line2.dx;
}
var r=this.createLine(dx,dy,line2.next);
line1.next=this.createLine(0,y2+line2.dy-dy-y1,r);
return r;
}
mxCompactTreeLayout.prototype.createNode=function(cell){
var node=new Object();
node.cell=cell;
node.x=0;
node.y=0;
node.width=0;
node.height=0;
var geo=this.graph.getModel().getGeometry(cell);
if(geo!=null){
if(this.isHorizontal){
node.width=geo.height;
node.height=geo.width;
}else{
node.width=geo.width;
node.height=geo.height;
}
}
node.offsetX=0;
node.offsetY=0;
node.contour=new Object();
return node;
}
mxCompactTreeLayout.prototype.createLine=function(dx,dy,next){
var line=new Object();
line.dx=dx;
line.dy=dy;
line.next=next;
return line;
}
}

{
function mxFastOrganicLayout(graph){
this.graph=graph;
}
mxFastOrganicLayout.prototype.forceConstant=50;
mxFastOrganicLayout.prototype.forceConstantSquared=0;
mxFastOrganicLayout.prototype.minDistanceLimit=2;
mxFastOrganicLayout.prototype.minDistanceLimitSquared=4;
mxFastOrganicLayout.prototype.initialTemp=200;
mxFastOrganicLayout.prototype.temperature=0;
mxFastOrganicLayout.prototype.maxIterations=0;
mxFastOrganicLayout.prototype.iteration=0;
mxFastOrganicLayout.prototype.vertexArray;
mxFastOrganicLayout.prototype.dispX;
mxFastOrganicLayout.prototype.dispY;
mxFastOrganicLayout.prototype.cellLocation;
mxFastOrganicLayout.prototype.radius;
mxFastOrganicLayout.prototype.radiusSquared;
mxFastOrganicLayout.prototype.isMoveable;
mxFastOrganicLayout.prototype.neighbours;
mxFastOrganicLayout.prototype.move=function(cell,x,y){

}
mxFastOrganicLayout.prototype.execute=function(parent){
var model=this.graph.getModel();
this.vertexArray=this.graph.getChildren(true,false,parent);
var n=this.vertexArray.length;
this.dispX=new Array(n);
this.dispY=new Array(n);
this.cellLocation=new Array(n);
this.isMoveable=new Array(n);
this.neighbours=new Array(n);
this.radius=new Array(n);
this.radiusSquared=new Array(n);
if(this.forceConstant<0.001){
this.forceConstant=0.001;
}
this.forceConstantSquared=this.forceConstant*this.forceConstant;



for(var i=0;i<this.vertexArray.length;i++){
var vertex=this.vertexArray[i];
this.cellLocation[i]=new Array(2);
vertex._index=i;
var bounds=model.getGeometry(vertex);

var width=bounds.width;
var height=bounds.height;
var x=bounds.x;
if(x==0){
x=Math.random();
}
var y=bounds.y;
if(y==0){
y=Math.random();
}
this.cellLocation[i][0]=x+width/2.0;
this.cellLocation[i][1]=y+height/2.0;
this.radius[i]=Math.min(width,height);
this.radiusSquared[i]=this.radius[i]*this.radius[i];
}
for(var i=0;i<n;i++){
this.dispX[i]=0;
this.dispY[i]=0;
this.isMoveable[i]=this.graph.isMovable(this.vertexArray[i]);


var edges=model.getEdges(this.vertexArray[i]);
var cells=model.getOpposites(edges,this.vertexArray[i],true,true);
this.neighbours[i]=new Array(cells.length);
for(var j=0;j<cells.length;j++){
var index=cells[j]._index;

if(index!=null){
this.neighbours[i][j]=index;
}



else{
this.neighbours[i][j]=i;
}
}
}
this.temperature=this.initialTemp;
if(this.maxIterations==0){
this.maxIterations=20*Math.sqrt(n);
}

for(this.iteration=0;this.iteration<this.maxIterations;this.iteration++){
this.calcRepulsion();
this.calcAttraction();
this.calcPositions();
this.reduceTemperature();
}

model.beginUpdate();
try
{
var minx=null;
var miny=null;
for(var i=0;i<this.vertexArray.length;i++){
var vertex=this.vertexArray[i];
vertex._index=null;
var geo=model.getGeometry(vertex);
if(geo!=null){
this.cellLocation[i][0]-=bounds.width/2.0;
this.cellLocation[i][1]-=bounds.height/2.0;
geo=geo.clone();
geo.x=this.graph.snap(this.cellLocation[i][0]);
geo.y=this.graph.snap(this.cellLocation[i][1]);
model.setGeometry(vertex,geo);
if(minx==null){
minx=geo.x;
}else{
minx=Math.min(minx,geo.x);
}
if(miny==null){
miny=geo.y;
}else{
miny=Math.min(miny,geo.y);
}
}
}


if(minx!=null||minx!=null){
for(var i=0;i<this.vertexArray.length;i++){
var vertex=this.vertexArray[i];
var geo=model.getGeometry(vertex);
if(geo!=null){
if(minx!=null){
geo.x-=minx-1;
}
if(miny!=null){
geo.y-=miny-1;
}
}
}
}
}
finally
{
model.endUpdate();
}
};
mxFastOrganicLayout.prototype.calcPositions=function(){
for(var index=0;index<this.vertexArray.length;index++){
if(this.isMoveable[index]){

var deltaLength=Math.sqrt(this.dispX[index]*this.dispX[index]+this.dispY[index]*this.dispY[index]);
if(deltaLength<0.001){
deltaLength=0.001;
}

var newXDisp=this.dispX[index]/deltaLength*Math.min(deltaLength,this.temperature);
var newYDisp=this.dispY[index]/deltaLength*Math.min(deltaLength,this.temperature);
this.dispX[index]=0;
this.dispY[index]=0;
this.cellLocation[index][0]+=newXDisp;
this.cellLocation[index][1]+=newYDisp;
}
}
};
mxFastOrganicLayout.prototype.calcAttraction=function(){

for(var i=0;i<this.vertexArray.length;i++){
for(var k=0;k<this.neighbours[i].length;k++){

var j=this.neighbours[i][k];
if(i!=j){
var xDelta=this.cellLocation[i][0]-this.cellLocation[j][0];
var yDelta=this.cellLocation[i][1]-this.cellLocation[j][1];
var deltaLengthSquared=xDelta*xDelta+yDelta*yDelta-this.radiusSquared[i]-this.radiusSquared[j];
if(deltaLengthSquared<this.minDistanceLimitSquared){
deltaLengthSquared=this.minDistanceLimitSquared;
}
var deltaLength=Math.sqrt(deltaLengthSquared);
var force=(deltaLengthSquared)/this.forceConstant;
var displacementX=(xDelta/deltaLength)*force;
var displacementY=(yDelta/deltaLength)*force;
if(this.isMoveable[i]){
this.dispX[i]-=displacementX;
this.dispY[i]-=displacementY;
}
if(this.isMoveable[j]){
this.dispX[j]+=displacementX;
this.dispY[j]+=displacementY;
}
}
}
}
};
mxFastOrganicLayout.prototype.calcRepulsion=function(){
for(var i=0;i<this.vertexArray.length;i++){
for(var j=i;j<this.vertexArray.length;j++){
if(j!=i){
var xDelta=this.cellLocation[i][0]-this.cellLocation[j][0];
var yDelta=this.cellLocation[i][1]-this.cellLocation[j][1];
if(xDelta==0){
xDelta=1;
}
if(yDelta==0){
yDelta=1;
}
var deltaLength=Math.sqrt((xDelta*xDelta)+(yDelta*yDelta));
var deltaLengthWithRadius=deltaLength-this.radius[i]-this.radius[j];
if(deltaLengthWithRadius<this.minDistanceLimit){
deltaLengthWithRadius=this.minDistanceLimit;
}
var force=this.forceConstantSquared/deltaLengthWithRadius;
var displacementX=(xDelta/deltaLength)*force;
var displacementY=(yDelta/deltaLength)*force;
if(this.isMoveable[i]){
this.dispX[i]+=displacementX;
this.dispY[i]+=displacementY;
}
if(this.isMoveable[j]){
this.dispX[j]-=displacementX;
this.dispY[j]-=displacementY;
}
}
}
}
};
mxFastOrganicLayout.prototype.reduceTemperature=function(){
this.temperature=this.initialTemp*(1.0-this.iteration/this.maxIterations);
};
}

{
function mxCircleLayout(graph,radius)
{
this.graph=graph;
this.radius=(radius!=null)?radius:100;
}
mxCircleLayout.prototype.move=function(cell,x,y)
{

}
mxCircleLayout.prototype.execute=function(parent)
{
var model=this.graph.getModel();
var max=0;
var vertices=new Array();
var childCount=model.getChildCount(parent);
for(var i=0;i<childCount;i++)
{
var cell=model.getChildAt(parent,i);
if(model.isVertex(cell))
{
vertices.push(cell);
var g=model.getGeometry(cell);
max=Math.max(max,Math.max(g.width,g.height));
}
}

model.beginUpdate();
try
{
var vertexCount=vertices.length;
var phi=2*Math.PI/vertexCount;
var r=Math.max(vertexCount*max/Math.PI,this.radius);
for(var i=0;i<vertexCount;i++)
{
var g=model.getGeometry(vertices[i]);
g=g.clone();
g.x=r+r*Math.sin(i*phi);
g.y=r+r*Math.cos(i*phi);
model.setGeometry(vertices[i],g);
}
}
finally
{
model.endUpdate();
}
}
}

{
function mxGraphModel(root)
{
this.updateLevel=0;
this.currentEdit=this.createUndoableEdit();
if(root==null)
{
root=new mxCell();
root.insert(new mxCell());
}
this.setRoot(root);
}
mxGraphModel.prototype=new mxEventSource();
mxGraphModel.prototype.constructor=mxGraphModel;
mxGraphModel.prototype.defaultEdge=null;
mxGraphModel.prototype.defaultGroup=null;
mxGraphModel.prototype.isCreateIds=true;
mxGraphModel.prototype.prefix='';
mxGraphModel.prototype.postfix='';
mxGraphModel.prototype.nextId=0;
mxGraphModel.prototype.cells=null;
mxGraphModel.prototype.isMaintainEdgeParent=true;
mxGraphModel.prototype.isEdgesConnectable=false;
mxGraphModel.prototype.getCell=function(id)
{
return(this.cells!=null)?this.cells[id]:null;
}
mxGraphModel.prototype.isRoot=function(cell)
{
return cell!=null&&this.root==cell;
}
mxGraphModel.prototype.isLayer=function(cell)
{
return this.isRoot(this.getParent(cell));
}
mxGraphModel.prototype.isVertex=function(cell)
{
return cell.isVertex();
}
mxGraphModel.prototype.isConnectable=function(cell)
{
return cell.isConnectable()&&(!this.isEdge(cell)||this.isEdgesConnectable);
}
mxGraphModel.prototype.isEdge=function(cell)
{
return cell.isEdge();
}
mxGraphModel.prototype.isCollapsed=function(cell)
{
return cell.isCollapsed();
}
mxGraphModel.prototype.isVisible=function(cell)
{
return cell.isVisible();
}
mxGraphModel.prototype.getCells=function(filter,parent,result)
{
result=result||new Array();
if(typeof(filter)=='function')
{
parent=parent||this.getRoot();
if(filter(parent))
{
result.push(parent);
}
var childCount=this.getChildCount(parent);
for(var i=0;i<childCount;i++)
{
var child=this.getChildAt(parent,i);
this.getCells(filter,child,result);
}
}
return result;
}
mxGraphModel.prototype.getEdge=function(source,target)
{
var tmp1=this.getEdgeCount(source);
var tmp2=this.getEdgeCount(target);
var edgeCount=tmp1;
var terminal=source;
var opposite=target;
if(tmp2<tmp1)
{
terminal=target;
opposite=source;
edgeCount=tmp2;
}
for(var i=0;i<edgeCount;i++)
{
var tmp=this.getEdgeAt(terminal,i);
if(this.getTerminal(tmp,terminal==target)==opposite)
{
return tmp;
}
}
return null;
}
mxGraphModel.prototype.rootChanged=function(root)
{
var oldRoot=this.root;
this.root=root;
this.nextId=0;
this.cells=null;
this.cellAdded(root);
return oldRoot;
}
mxGraphModel.prototype.cellAdded=function(cell)
{
if(cell!=null)
{
if(cell.getId()==null&&this.isCreateIds)
{
cell.setId(this.createId(cell));
}
if(cell.getId()!=null)
{
var collision=this.getCell(cell.getId());
if(collision!=cell)
{
while(collision!=null)
{
cell.setId(this.createId(cell));
collision=this.getCell(cell.getId());
}
if(this.cells==null)
{
this.cells=new Array();
}
this.cells[cell.getId()]=cell;
}
}
var childCount=this.getChildCount(cell);
for(var i=0;i<childCount;i++)
{
this.cellAdded(this.getChildAt(cell,i));
}
}
}
mxGraphModel.prototype.createId=function(cell)
{
var id=this.nextId;
this.nextId++;
return this.prefix+id+this.postfix;
}
mxGraphModel.prototype.cellRemoved=function(cell)
{
if(cell!=null&&this.cells!=null)
{
var childCount=this.getChildCount(cell);
for(var i=childCount-1;i>=0;i--)
{
this.cellRemoved(this.getChildAt(cell,i));
}
if(this.cells!=null&&cell.getId()!=null)
{
this.cells[cell.getId()]=null;
}
}
}
mxGraphModel.prototype.createUndoableEdit=function()
{
var edit=new mxUndoableEdit(this,true);
edit.notify=function(){
edit.source.dispatchEvent('change',edit.source,edit.changes);
edit.source.dispatchEvent('notify',edit.source,edit.changes);
}
return edit;
}
mxGraphModel.prototype.cloneCell=function(cell)
{
var cells=new Array();
cells.push(cell);
return this.cloneCells(cells,true)[0];
}
mxGraphModel.prototype.cloneCells=function(cells,includeChildren)
{
var clones=new Array();
for(var i=0;i<cells.length;i++)
{
clones.push(this.cloneCellAnnotated(cells[i],includeChildren));
}
for(var i=0;i<clones.length;i++)
{
this.restoreClone(clones[i],cells[i]);
}
for(var i=0;i<cells.length;i++)
{
this.cleanupCell(cells[i]);
}
return clones;
}
mxGraphModel.prototype.cloneCellAnnotated=function(cell,includeChildren)
{
var clone=cell.clone();
cell._clone=clone;
if(includeChildren)
{
var childCount=this.getChildCount(cell);
for(var i=0;i<childCount;i++)
{
var cloneChild=this.cloneCellAnnotated(this.getChildAt(cell,i),true);
clone.insert(cloneChild);
}
}
return clone;
}
mxGraphModel.prototype.restoreClone=function(clone,cell)
{
var source=this.getTerminal(cell,true);
if(source!=null)
{
var tmp=source._clone;
if(tmp!=null)
{
tmp.insertEdge(clone,true);
}
}
var target=this.getTerminal(cell,false);
if(target!=null)
{
var tmp=target._clone;
if(tmp!=null)
{
tmp.insertEdge(clone,false);
}
}
var childCount=this.getChildCount(clone);
for(var i=0;i<childCount;i++)
{
this.restoreClone(this.getChildAt(clone,i),this.getChildAt(cell,i));
}
}
mxGraphModel.prototype.cleanupCell=function(cell)
{
cell._clone=null;
var childCount=this.getChildCount(cell);
for(var i=0;i<childCount;i++)
{
this.cleanupCell(this.getChildAt(cell,i));
}
}
mxGraphModel.prototype.contains=function(cell)
{
return this.isAncestor(this.root,cell);
}
mxGraphModel.prototype.isAncestor=function(parent,child)
{
while(child!=null&&child!=parent)
{
child=this.getParent(child);
}
return child==parent;
}
mxGraphModel.prototype.setRoot=function(root)
{
var oldRoot=this.root;
this.execute(new mxRootChange(this,root));
return root;
}
mxGraphModel.prototype.getRoot=function(cell)
{
var root=cell||this.root;
if(cell!=null)
{
while(cell!=null)
{
root=cell;
cell=this.getParent(cell);
}
}
return root;
}
mxGraphModel.prototype.getChildCount=function(cell)
{
return(cell!=null)?cell.getChildCount():0;
}
mxGraphModel.prototype.getChildAt=function(cell,index)
{
return(cell!=null)?cell.getChildAt(index):null;
}
mxGraphModel.prototype.getChildren=function(cell)
{
return(cell!=null)?cell.children:null;
}
mxGraphModel.prototype.addVertex=function(parent,id,value,x,y,width,height,style)
{
var geometry=new mxGeometry(x,y,width,height);
var vertex=new mxCell(value,geometry,style);
vertex.setId(id);
vertex.vertex=true;
vertex.connectable=true;
var index=this.getChildCount(parent);
return this.add(parent,vertex,index);
}
mxGraphModel.prototype.addEdge=function(parent,id,value,source,target,style)
{
var edge=new mxCell(value,new mxGeometry(),style);
edge.setId(id);
edge.edge=true;
var index=this.getChildCount(parent);
this.beginUpdate();
try
{
this.add(parent,edge,index);
this.setTerminal(edge,source,true);
this.setTerminal(edge,target,false);
}
finally
{
this.endUpdate();
}
return edge;
}
mxGraphModel.prototype.add=function(parent,child,index)
{
if(parent!=null&&child!=null){
if(index==null)
{
index=this.getChildCount(parent);
}
this.execute(new mxChildChange(this,parent,child,index));
if(this.isMaintainEdgeParent)
{
var root=this.getRoot(child);
var edgeCount=this.getEdgeCount(child);
for(var i=0;i<edgeCount;i++)
{


var edge=this.getEdgeAt(child,i);
if(this.isAncestor(root,edge))
{
this.updateEdgeParent(edge);
}
}
}
}
return child;
}
mxGraphModel.prototype.remove=function(cell)
{
if(cell==this.root)
{
this.setRoot(null)
}
else if(this.getParent(cell)!=null)
{
this.execute(new mxChildChange(this,null,cell));
}
}
mxGraphModel.prototype.getParent=function(cell)
{
return(cell!=null)?cell.getParent():null;
}
mxGraphModel.prototype.parentForCellChanged=function(cell,parent,index)
{
var previous=this.getParent(cell);
if(parent!=null)
{
if(parent!=previous||previous.getIndex(cell)!=index)
{
parent.insert(cell,index);
}
}
else if(previous!=null)
{
var oldIndex=previous.getIndex(cell);
previous.remove(oldIndex);
}


if(!this.contains(previous)&&parent!=null)
{
this.cellAdded(cell);
}
else if(parent==null)
{
this.cellRemoved(cell);
}
return previous;
}
mxGraphModel.prototype.getGeometry=function(cell,geometry)
{
return(cell!=null)?cell.getGeometry():null;
}
mxGraphModel.prototype.setGeometry=function(cell,geometry)
{
if(geometry!=this.getGeometry(cell))
{
this.execute(new mxGeometryChange(this,cell,geometry));
}
}
mxGraphModel.prototype.geometryForCellChanged=function(cell,geometry)
{
var previous=this.getGeometry(cell);
cell.setGeometry(geometry);
return previous;
}
mxGraphModel.prototype.getEdgeCount=function(cell)
{
return(cell!=null)?cell.getEdgeCount():0;
}
mxGraphModel.prototype.getEdgeAt=function(cell,index)
{
return(cell!=null)?cell.getEdgeAt(index):null;
}
mxGraphModel.prototype.getEdges=function(cell)
{
return(cell!=null)?cell.edges:null;
}
mxGraphModel.prototype.getOpposites=function(edges,terminal,sources,targets)
{
var terminals=new Array();
if(edges!=null)
{
for(var i=0;i<edges.length;i++)
{
var source=this.getTerminal(edges[i],true);
var target=this.getTerminal(edges[i],false);
if(source==terminal&&target!=null&&target!=terminal&&targets)
{
terminals.push(target);
}
else if(target==terminal&&source!=null&&source!=terminal&&sources)
{
terminals.push(source);
}
}
}
return terminals;
}
mxGraphModel.prototype.setTerminals=function(cell,source,target)
{
this.beginUpdate();
try
{
this.setTerminal(cell,source,true);
this.setTerminal(cell,target,false);
}
finally
{
this.endUpdate();
}
}
mxGraphModel.prototype.getTerminal=function(cell,isSource)
{
return cell.getTerminal(isSource);
}
mxGraphModel.prototype.setTerminal=function(cell,terminal,isSource)
{
if(terminal!=this.getTerminal(cell,isSource))
{
this.execute(new mxTerminalChange(this,cell,terminal,isSource));
if(this.isMaintainEdgeParent)
{
this.updateEdgeParent(cell);
}
}
}
mxGraphModel.prototype.terminalForCellChanged=function(cell,terminal,isSource)
{
var previous=this.getTerminal(cell,isSource);
if(terminal!=null)
{
terminal.insertEdge(cell,isSource);
}
else if(previous!=null)
{
previous.removeEdge(cell,isSource);
}
return previous;
}
mxGraphModel.prototype.updateEdgeParent=function(edge)
{
var source=this.getTerminal(edge,true);
var target=this.getTerminal(edge,false);
var cell=this.getNearestCommonAncestor(source,target);
if(cell!=null&&this.getParent(edge)!=cell)
{
this.add(cell,edge,cell.getChildCount());
}
}
mxGraphModel.prototype.getNearestCommonAncestor=function(cell1,cell2)
{
if(cell1!=null&&cell2!=null)
{

var ancestors=new Array();
var cell=cell2;
while(cell!=null){
ancestors.push(cell);
cell=this.getParent(cell);
}
if(ancestors.length>0)
{

cell=cell1;
while(cell!=null){
var parent=this.getParent(cell);
if(mxUtils.indexOf(ancestors,cell)>=0&&parent!=null)
{
return cell;
}
cell=parent;
}
}
}
return null;
}
mxGraphModel.prototype.getValue=function(cell)
{
return(cell!=null)?cell.getValue():null;
}
mxGraphModel.prototype.setValue=function(cell,value)
{
this.execute(new mxValueChange(this,cell,value));
}
mxGraphModel.prototype.valueForCellChanged=function(cell,value)
{
return cell.valueChanged(value);
}
mxGraphModel.prototype.getStyle=function(cell)
{
return cell.getStyle();
}
mxGraphModel.prototype.setStyle=function(cell,style)
{
if(style!=this.getStyle(cell))
{
this.execute(new mxStyleChange(this,cell,style));
}
}
mxGraphModel.prototype.styleForCellChanged=function(cell,style)
{
var previous=this.getStyle(cell);
cell.setStyle(style);
return previous;
}
mxGraphModel.prototype.setCollapsed=function(cell,collapsed)
{
if(collapsed!=this.isCollapsed(cell))
{
this.execute(new mxCollapseChange(this,cell,collapsed));
}
}
mxGraphModel.prototype.collapsedStateForCellChanged=function(cell,collapsed)
{
var previous=this.isCollapsed(cell);
cell.setCollapsed(collapsed);
return previous;
}
mxGraphModel.prototype.setVisible=function(cell,visible)
{
if(visible!=this.isVisible(cell))
{
this.execute(new mxVisibleChange(this,cell,visible));
}
}
mxGraphModel.prototype.visibleStateForCellChanged=function(cell,visible)
{
var previous=this.isVisible(cell);
cell.setVisible(visible);
return previous;
}
mxGraphModel.prototype.execute=function(edit)
{
edit.execute();
this.beginUpdate();
this.currentEdit.add(edit);
this.endUpdate();
}
mxGraphModel.prototype.beginUpdate=function()
{
this.updateLevel++;
}
mxGraphModel.prototype.endUpdate=function()
{
this.updateLevel--;
if(this.updateLevel==0)
{
if(!this.currentEdit.isEmpty())
{
this.dispatchEvent('undo',this,this.currentEdit);
this.currentEdit.notify();
this.currentEdit=this.createUndoableEdit();
}
}
}
function mxRootChange(model,root)
{
this.model=model;
this.root=root;
this.previous=root;
}
mxRootChange.prototype.execute=function()
{
this.root=this.previous;
this.previous=this.model.rootChanged(this.previous);
}
function mxChildChange(model,parent,child,index)
{
this.model=model;
this.parent=parent;
this.previous=parent;
this.child=child;
this.index=index;
this.previousIndex=index;
this.isAdded=(parent==null);
}
mxChildChange.prototype.execute=function()
{
var tmp=this.model.getParent(this.child);
var tmp2=(tmp!=null)?tmp.getIndex(this.child):0;
tmp=this.model.parentForCellChanged(this.child,this.previous,this.previousIndex);
this.connect(this.child,this.previous!=null);
this.parent=this.previous;
this.previous=tmp;
this.index=this.previousIndex;
this.previousIndex=tmp2;
this.isAdded=!this.isAdded;
}
mxChildChange.prototype.connect=function(cell,isConnect)
{
isConnect=(isConnect!=null)?isConnect:true;
var source=cell.getTerminal(true);
var target=cell.getTerminal(false);
if(source!=null)
{
if(isConnect)
{
source.insertEdge(cell,true);
}
else
{
source.removeEdge(cell,true);
}
}
if(target!=null)
{
if(isConnect)
{
target.insertEdge(cell,false);
}
else
{
target.removeEdge(cell,false);
}
}
cell.setTerminal(source,true);
cell.setTerminal(target,false);
var childCount=this.model.getChildCount(cell);
for(var i=0;i<childCount;i++)
{
this.connect(this.model.getChildAt(cell,i),isConnect);
}
}
function mxTerminalChange(model,cell,terminal,isSource)
{
this.model=model;
this.cell=cell;
this.terminal=terminal;
this.previous=terminal;
this.isSource=isSource;
}
mxTerminalChange.prototype.execute=function()
{
this.terminal=this.previous;
this.previous=this.model.terminalForCellChanged(this.cell,this.previous,this.isSource);
}
function mxValueChange(model,cell,value)
{
this.model=model;
this.cell=cell;
this.value=value;
this.previous=value;
}
mxValueChange.prototype.execute=function()
{
this.value=this.previous;
this.previous=this.model.valueForCellChanged(this.cell,this.previous);
}
function mxStyleChange(model,cell,style)
{
this.model=model;
this.cell=cell;
this.style=style;
this.previous=style;
}
mxStyleChange.prototype.execute=function()
{
this.style=this.previous;
this.previous=this.model.styleForCellChanged(this.cell,this.previous);
}
function mxGeometryChange(model,cell,geometry)
{
this.model=model;
this.cell=cell;
this.geometry=geometry;
this.previous=geometry;
}
mxGeometryChange.prototype.execute=function()
{
this.geometry=this.previous;
this.previous=this.model.geometryForCellChanged(this.cell,this.previous);
}
function mxCollapseChange(model,cell,collapsed)
{
this.model=model;
this.cell=cell;
this.collapsed=collapsed;
this.previous=collapsed;
}
mxCollapseChange.prototype.execute=function()
{
this.collapsed=this.previous;
this.previous=this.model.collapsedStateForCellChanged(this.cell,this.previous);
}
function mxVisibleChange(model,cell,visible)
{
this.model=model;
this.cell=cell;
this.visible=visible;
this.previous=visible;
}
mxVisibleChange.prototype.execute=function()
{
this.visible=this.previous;
this.previous=this.model.visibleStateForCellChanged(this.cell,this.previous);
}
}

{
function mxCell(value,geometry,style){
this.value=value;
this.setGeometry(geometry);
this.setStyle(style);
if(this.onInit!=null){
this.onInit();
}
}
mxCell.prototype.id=null;
mxCell.prototype.value=null;
mxCell.prototype.geometry=null;
mxCell.prototype.style=null;
mxCell.prototype.vertex=false;
mxCell.prototype.edge=false;
mxCell.prototype.collapsed=false;
mxCell.prototype.visible=true;
mxCell.prototype.connectable=true;
mxCell.prototype.parent=null;
mxCell.prototype.children=null;
mxCell.prototype.source=null;
mxCell.prototype.target=null;
mxCell.prototype.edges=null;
mxCell.prototype.constraints=null;
mxCell.prototype.transient=['_clone','id','value','states','parent','source','target','children','edges'];
mxCell.prototype.is=function(type,attr,value){
var value=this.getValue();
var nodeName=(value!=null)?value.nodeName:null;
if(nodeName!=null&&type.toUpperCase()==nodeName.toUpperCase()){
return attr==null||this.getAttribute(attr).toUpperCase()==value.toUpperCase();
}
return false;
}
mxCell.prototype.getValue=function(){
return this.value;
}
mxCell.prototype.setValue=function(value){
this.value=value;
}
mxCell.prototype.valueChanged=function(newValue){
var previous=this.getValue();
this.setValue(newValue);
return previous;
}
mxCell.prototype.getId=function(){
return this.id;
}
mxCell.prototype.setId=function(id){
this.id=id;
}
mxCell.prototype.isVisible=function(){
return this.visible;
}
mxCell.prototype.setVisible=function(visible){
this.visible=visible;
}
mxCell.prototype.isVertex=function(){
return this.vertex;
}
mxCell.prototype.isConnectable=function(){
return this.connectable;
}
mxCell.prototype.isEdge=function(){
return this.edge;
}
mxCell.prototype.getAttribute=function(key){
var userObject=this.getValue();
return(userObject!=null&&userObject.nodeType==1)?userObject.getAttribute(key):null;
}
mxCell.prototype.setAttribute=function(key,value){
var userObject=this.getValue();
if(userObject!=null&&userObject.nodeType==1){
userObject.setAttribute(key,value);
}
}
mxCell.prototype.insert=function(child,childIndex){
if(child!=null){
childIndex=(childIndex!=null)?childIndex:this.getChildCount();
var parent=child.parent;
if(parent!=null){
var index=parent.getIndex(child);
parent.remove(index);
}
child.setParent(this);
if(this.children==null){
this.children=new Array(childIndex+1);
this.children[childIndex]=child;
}else{
this.children.splice(childIndex,0,child);
}
}
return child;
}
mxCell.prototype.remove=function(childIndex){
if(this.children!=null&&childIndex>=0){
var child=this.getChildAt(childIndex);
if(child!=null){
this.children.splice(childIndex,1);
child.setParent(null);
}
}
}
mxCell.prototype.getChildCount=function(){
if(this.children==null){
return 0;
}
return this.children.length;
}
mxCell.prototype.getChildAt=function(childIndex){
if(this.children==null){
return null;
}
return this.children[childIndex];
}
mxCell.prototype.getIndex=function(child){
if(this.children!=null){
for(var i=0;i<this.children.length;i++){
if(this.children[i]==child){
return i;
}
}
}
return-1;
}
mxCell.prototype.getTerminal=function(isSource){
return(isSource)?this.source:this.target;
}
mxCell.prototype.setTerminal=function(terminal,isSource){
if(isSource){
this.source=terminal;
}else{
this.target=terminal;
}
}
mxCell.prototype.insertEdge=function(edge,isOutgoing){
if(edge!=null){
var old=edge.getTerminal(isOutgoing);
if(old!=null){
old.removeEdge(edge,isOutgoing);
}
edge.setTerminal(this,isOutgoing);
if(edge.getTerminal(!isOutgoing)!=this){
if(this.edges==null){
this.edges=new Array();
}
this.edges.push(edge);
}
}
return edge;
}
mxCell.prototype.removeEdge=function(edge,isOutgoing){
if(edge!=null){
if(edge.getTerminal(!isOutgoing)!=this&&this.edges!=null){
var index=this.getEdgeIndex(edge);
if(index>=0){
this.edges.splice(index,1);
}
}
edge.setTerminal(null,isOutgoing);
}
}
mxCell.prototype.getEdgeCount=function(){
if(this.edges==null){
return 0;
}
return this.edges.length;
}
mxCell.prototype.getDirectedEdgeCount=function(outgoing,ignoredEdge){
var count=0;
if(this.edges!=null){
for(var i=0;i<this.edges.length;i++){
if(this.edges[i]!=ignoredEdge&&this.edges[i].getTerminal(outgoing)==this)
{
count++;
}
}
}
return count;
}
mxCell.prototype.getEdgeAt=function(edgeIndex){
if(this.edges==null){
return null;
}
return this.edges[edgeIndex];
}
mxCell.prototype.getEdgeIndex=function(edge){
if(this.edges!=null){
for(var i=0;i<this.edges.length;i++){
if(this.edges[i]==edge){
return i;
}
}
}
return-1;
}
mxCell.prototype.getParent=function(parent){
return this.parent;
}
mxCell.prototype.setParent=function(parent){
this.parent=parent;
}
mxCell.prototype.getStyle=function(){
return this.style;
}
mxCell.prototype.setStyle=function(style){
this.style=style;
}
mxCell.prototype.getGeometry=function(){
return this.geometry;
}
mxCell.prototype.setGeometry=function(geometry){
this.geometry=geometry;
}
mxCell.prototype.isCollapsed=function(){
return this.collapsed;
}
mxCell.prototype.setCollapsed=function(collapsed){
this.collapsed=collapsed;
}
mxCell.prototype.clone=function(){
var clone=mxUtils.clone(this,this.transient);
clone.setValue(this.cloneValue());
return clone;
}
mxCell.prototype.cloneValue=function(){
var value=this.getValue();
if(typeof(value.clone)=='function'){
value=value.clone();
}else if(value.nodeType!=null){
value=value.cloneNode(true);
}
return value;
}
}

{
function mxGeometry(x,y,width,height)
{
this.x=(x!=null)?x:0;
this.y=(y!=null)?y:0;
this.width=(width!=null)?width:0;
this.height=(height!=null)?height:0;
this.offset=null;
this.sourcePoint=null;
this.targetPoint=null;
this.alternateBounds=null;
this.isRelative=false;
this.points=null;
}
mxGeometry.prototype.swap=function()
{
if(this.alternateBounds!=null)
{
var old=new mxRectangle(this.x,this.y,this.width,this.height);
this.x=this.alternateBounds.x;
this.y=this.alternateBounds.y;
this.width=this.alternateBounds.width;
this.height=this.alternateBounds.height;
this.alternateBounds=old;
}
}
mxGeometry.prototype.getTerminalPoint=function(isSource)
{
return(isSource)?this.sourcePoint:this.targetPoint;
}
mxGeometry.prototype.setTerminalPoint=function(point,isSource)
{
if(isSource)
{
this.sourcePoint=point;
}
else
{
this.targetPoint=point;
}
}
mxGeometry.prototype.translate=function(dx,dy)
{
var clone=this.clone();
clone.x+=dx;
clone.y+=dy;
if(clone.sourcePoint!=null)
{
clone.sourcePoint.x+=dx;
clone.sourcePoint.y+=dy;
}
if(clone.targetPoint!=null)
{
clone.targetPoint.x+=dx;
clone.targetPoint.y+=dy;
}
if(clone.points!=null)
{
for(var i=0;i<clone.points.length;i++)
{
var pt=clone.points[i];
pt.x+=dx;
pt.y+=dy;
}
}
return clone;
}
mxGeometry.prototype.clone=function()
{
var clone=new mxGeometry(this.x,this.y,this.width,this.height);
clone.alternateBounds=mxUtils.clone(this.alternateBounds);
clone.isRelative=this.isRelative;
if(this.offset!=null)
{
clone.offset=new mxPoint(this.offset.x,this.offset.y);
}
if(this.sourcePoint!=null)
{
clone.sourcePoint=new mxPoint(this.sourcePoint.x,this.sourcePoint.y);
}
if(this.targetPoint!=null)
{
clone.targetPoint=new mxPoint(this.targetPoint.x,this.targetPoint.y);
}
if(this.points!=null)
{
clone.points=new Array();
for(var i=0;i<this.points.length;i++)
{
var pt=this.points[i];
clone.points.push(new mxPoint(pt.x,pt.y));
}
}
return clone;
}
}

var mxCellPath={
PATH_SEPARATOR:'.',
create:function(cell){
var result='';
var parent=cell.parent;
while(parent!=null){
var index=parent.getIndex(cell);
result=index+mxCellPath.PATH_SEPARATOR+result;
cell=parent;
parent=cell.parent;
}
return(result.length>1)?result.substring(0,result.length-1)
:"";
},
resolve:function(root,path){
var parent=root;
var tokens=path.split(mxCellPath.PATH_SEPARATOR);
for(var i=0;i<tokens.length;i++){
parent=parent.getChildAt(parseInt(tokens[i]));
}
return parent;
}
}

var mxPerimeter={
RectanglePerimeter:function(bounds,edgeState,terminalState,isSource,next)
{
var cx=bounds.x+bounds.width/2;
var cy=bounds.y+bounds.height/2;
var dx=next.x-cx;
var dy=next.y-cy;
var alpha=Math.atan2(dy,dx);
var p=new mxPoint(0,0);
var pi=Math.PI;
var pi2=Math.PI/2;
var beta=pi2-alpha;
var t=Math.atan2(bounds.height,bounds.width);
if(alpha<-pi+t||alpha>pi-t)
{
p.x=bounds.x;
p.y=cy-bounds.width*Math.tan(alpha)/2;
}
else if(alpha<-t)
{
p.y=bounds.y;
p.x=cx-bounds.height*Math.tan(beta)/2;
}
else if(alpha<t)
{
p.x=bounds.x+bounds.width;
p.y=cy+bounds.width*Math.tan(alpha)/2;
}
else
{
p.y=bounds.y+bounds.height;
p.x=cx+bounds.height*Math.tan(beta)/2;
}
return p;
},
RightAngleRectanglePerimeter:function(bounds,edgeState,terminalState,isSource,next)
{
var p=mxPerimeter.RectanglePerimeter(bounds,edgeState,terminalState,isSource,next);
if(next.x>=bounds.x&&next.x<=bounds.x+bounds.width)
{
p.x=next.x;
}
else if(next.y>=bounds.y&&next.y<=bounds.y+bounds.height)
{
p.y=next.y;
}
if(next.x<bounds.x)
{
p.x=bounds.x;
}
else if(next.x>bounds.x+bounds.width)
{
p.x=bounds.x+bounds.width;
}
if(next.y<bounds.y)
{
p.y=bounds.y;
}
else if(next.y>bounds.y+bounds.height)
{
p.y=bounds.y+bounds.height;
}
return p;
},
EllipsePerimeter:function(bounds,edgeState,terminalState,isSource,next)
{
var x=bounds.x;
var y=bounds.y;
var a=(bounds.width+1)/2;
var b=(bounds.height+1)/2;
var cx=x+a;
var cy=y+b;
var px=next.x;
var py=next.y;

var dx=px-cx;
var dy=py-cy;
if(dx==0)
{
return new mxPoint(cx,cy+b*dy/Math.abs(dy));
}
var d=dy/dx;
var h=cy-d*cx;
var e=a*a*d*d+b*b;
var f=-2*cx*e;
var g=a*a*d*d*cx*cx+b*b*cx*cx-a*a*b*b;
var det=Math.sqrt(f*f-4*e*g);
var xout1=(-f+det)/(2*e);
var xout2=(-f-det)/(2*e);
var yout1=d*xout1+h;
var yout2=d*xout2+h;
var dist1=Math.sqrt(Math.pow((xout1-px),2)+Math.pow((yout1-py),2));
var dist2=Math.sqrt(Math.pow((xout2-px),2)+Math.pow((yout2-py),2));
var xout=0;
var yout=0;
if(dist1<dist2)
{
xout=xout1;
yout=yout1;
}
else
{
xout=xout2;
yout=yout2;
}
return new mxPoint(xout,yout);
},
RhombusPerimeter:function(bounds,edgeState,terminalState,isSource,next)
{
var x=bounds.x;
var y=bounds.y;
var w=bounds.width;
var h=bounds.height;
var cx=x+w/2;
var cy=x+h/2;
var px=next.x;
var py=next.y;
if(cx==px)
{
if(cy>py)
{
return new mxPoint(cx,y);
}
else
{
return new mxPoint(cx,y+h);
}
}
else if(cy==py)
{
if(cx>px)
{
return new mxPoint(x,cy);
}
else
{
return new mxPoint(x+w,cy);
}
}

if(px<cx)
{
if(py<cy)
{
return mxUtils.intersection(px,py,cx,cy,cx,y,x,cy);
}
else
{
return mxUtils.intersection(px,py,cx,cy,cx,y+h,x,cy);
}
}
else if(py<cy)
{
return mxUtils.intersection(px,py,cx,cy,cx,y,x+w,cy);
}
else
{
return mxUtils.intersection(px,py,cx,cy,cx,y+h,x+w,cy);
}
}
}

{
function mxStylesheet()
{
this.styles=new Array();
var style=new Array();
style[mxConstants.STYLE_SHAPE]=mxConstants.SHAPE_RECTANGLE;
style[mxConstants.STYLE_PERIMETER]=mxPerimeter.RightAngleRectanglePerimeter;
style[mxConstants.STYLE_VERTICAL_ALIGN]=mxConstants.ALIGN_MIDDLE;
style[mxConstants.STYLE_ALIGN]=mxConstants.ALIGN_CENTER;
style[mxConstants.STYLE_FILLCOLOR]='#C3D9FF';
style[mxConstants.STYLE_STROKECOLOR]='#6482B9';
style[mxConstants.STYLE_FONTCOLOR]='#774400';
this.putDefaultVertexStyle(style);
style=new Array();
style[mxConstants.STYLE_SHAPE]=mxConstants.SHAPE_CONNECTOR;
style[mxConstants.STYLE_ENDARROW]=mxConstants.ARROW_CLASSIC;
style[mxConstants.STYLE_VERTICAL_ALIGN]=mxConstants.ALIGN_MIDDLE;
style[mxConstants.STYLE_ALIGN]=mxConstants.ALIGN_CENTER;
style[mxConstants.STYLE_STROKECOLOR]='#6482B9';
style[mxConstants.STYLE_FONTCOLOR]='#446299';
style[mxConstants.STYLE_FONTSIZE]='11';
this.putDefaultEdgeStyle(style);
}
mxStylesheet.prototype.putDefaultVertexStyle=function(style)
{
this.putCellStyle('defaultVertex',style);
}
mxStylesheet.prototype.putDefaultEdgeStyle=function(style)
{
this.putCellStyle('defaultEdge',style);
}
mxStylesheet.prototype.getDefaultVertexStyle=function()
{
return this.styles['defaultVertex'];
}
mxStylesheet.prototype.getDefaultEdgeStyle=function()
{
return this.styles['defaultEdge'];
}
mxStylesheet.prototype.putCellStyle=function(name,style)
{
this.styles[name]=style;
}
mxStylesheet.prototype.getCellStyle=function(name,defaultStyle)
{
var style=defaultStyle;
if(name!=null&&name.length>0)
{
var imin=0;
var pairs=name.split(';');
if(pairs!=null)
{
name=pairs[0];
}
if(name.indexOf('=')<0)
{
var tmp=this.styles[name];
if(tmp!=null)
{
style=tmp;
}
imin=1;
}
if(pairs!=null)
{
if(style!=null)
{
style=mxUtils.clone(style);
}
else
{
style=new Object();
}
for(var i=imin;i<pairs.length;i++)
{
var tmp=pairs[i];
var c=tmp.indexOf('=');
if(c>=0)
{
var key=tmp.substring(0,c);
var value=tmp.substring(c+1);
style[key]=value;
}
}
}
}
return style;
}
}

{
function mxCellState(view,cell,style){
this.view=view;
this.cell=cell;
this.style=style;
this.origin=new mxPoint(0,0);
this.absoluteOffset=new mxPoint(0,0);
}
mxCellState.prototype.view=null;
mxCellState.prototype.cell=null;
mxCellState.prototype.style=null;
mxCellState.prototype.origin=null;
mxCellState.prototype.absoluteOffset=null;
mxCellState.prototype.invalid=true;
mxCellState.prototype.x=0;
mxCellState.prototype.y=0;
mxCellState.prototype.width=0;
mxCellState.prototype.height=0;
mxCellState.prototype.absolutePoints=null;
mxCellState.prototype.terminalDistance=0;
mxCellState.prototype.length=0;
mxCellState.prototype.setAbsoluteTerminalPoint=function(point,isSource){
if(isSource){
if(this.absolutePoints==null||this.absolutePoints.length==0){
this.absolutePoints=new Array();
this.absolutePoints.push(point);
}else{
this.absolutePoints[0]=point;
}
}else{
if(this.absolutePoints==null){
this.absolutePoints=new Array();
this.absolutePoints.push(null);
this.absolutePoints.push(point);
}else if(this.absolutePoints.length==1){
this.absolutePoints.push(point);
}else{
this.absolutePoints[this.absolutePoints.length-1]=point;
}
}
}
mxCellState.prototype.getCenterX=function(){
return this.x+this.width/2;
}
mxCellState.prototype.getCenterY=function(){
return this.y+this.height/2;
}
mxCellState.prototype.destroy=function(){
this.view.graph.cellRenderer.destroy(this);
this.view.graph.destroyHandler(this);
}
}

{
function mxGraphSelection(graph)
{
this.graph=graph;
this.cells=new Array();
}
mxGraphSelection.prototype.clear=function()
{
if(this.cells.length>0)
{
this.doClear();
this.graph.dispatchEvent('select',this);
}
}
mxGraphSelection.prototype.doClear=function()
{
for(var i=0;i<this.cells.length;i++)
{
var state=this.graph.view.getState(this.cells[i]);
if(state!=null)
{
this.graph.destroyHandler(state);
}
}
this.cells=new Array();
}
mxGraphSelection.prototype.setCells=function(cells)
{
this.doClear();
this.addCells(cells);
}
mxGraphSelection.prototype.addCells=function(cells)
{
var t0=mxLog.enter('mxGraphSelection.addCells');
window.status=mxResources.get('updatingSelection');
for(var i=0;i<cells.length;i++)
{
this.doAddCell(cells[i]);
}
this.graph.dispatchEvent('select',this,cells);
window.status=mxResources.get('done');
mxLog.leave('mxGraphSelection.addCells',t0);
}
mxGraphSelection.prototype.setCell=function(cell){
if(this.cells.length==0&&cell==null)
{
return;
}
this.doClear();
this.addCell(cell);
this.graph.dispatchEvent('select',this,[cell]);
}
mxGraphSelection.prototype.addCell=function(cell)
{
this.doAddCell(cell);
this.graph.dispatchEvent('select',this,[cell]);
}
mxGraphSelection.prototype.doAddCell=function(cell)
{
var state=this.graph.view.getState(cell);
if(state!=null&&!this.graph.hasHandler(state))
{
this.graph.createHandler(state);
this.cells.push(cell);
}
}
mxGraphSelection.prototype.removeCell=function(cell)
{
var index=mxUtils.indexOf(this.cells,cell);
if(index>=0)
{
var cells=new Array();
for(var i=0;i<this.cells.length;i++)
{
if(i!=index)
{
cells.push(this.cells[i]);
}
}
this.doClear();
this.addCells(cells);
}
}
mxGraphSelection.prototype.isSelected=function(cell)
{
if(cell==null)
{
return false;
}
var state=this.graph.view.getState(cell);
return this.graph.hasHandler(state);
}
}

{
function mxCellEditor(graph)
{
this.graph=graph;
this.textarea=document.createElement('textarea');
this.textarea.className='mxCellEditor';
this.textarea.setAttribute('cols','20');
this.textarea.setAttribute('rows','4');
this.textarea.style.position='absolute';
this.textarea.style.overflow='visible';
var self=this;
mxEvent.addListener(this.textarea,'blur',function(evt)
{
self.stopEditing();
});
}
mxCellEditor.prototype.startEditing=function(cell)
{
this.stopEditing();
this.cell=cell;
var state=this.graph.view.getState(cell);
var scale=this.graph.view.scale;
var minHeight=30;
if(state.text!=null)
{
this.textNode=state.text.node;
this.textNode.style.display='none';
this.textarea.style.fontFamily=state.text.family;
this.textarea.style.fontSize=state.text.size*this.graph.view.scale;
this.textarea.style.color=state.text.color;
if(this.textarea.style.color=='white')
{
this.textarea.style.color='black';
}
this.textarea.style.textAlign=state.text.align||'left';
this.textarea.style.fontWeight=
state.text.isStyleSet(mxConstants.FONT_BOLD)?'bold':'normal';
minHeight=state.text.size*scale+20;
}
else
{
this.textNode=null;
}
var offset=mxUtils.getOffset(this.graph.container);
var minWidth=(this.textarea.style.textAlign=='left')?80:30;
var spacing=parseInt(state.style[mxConstants.STYLE_SPACING]||2)*scale;
var spacingTop=(parseInt(state.style[mxConstants.STYLE_SPACING_TOP]||0))*scale+spacing;
var spacingRight=(parseInt(state.style[mxConstants.STYLE_SPACING_RIGHT]||0))*scale+spacing;
var spacingBottom=(parseInt(state.style[mxConstants.STYLE_SPACING_BOTTOM]||0))*scale+spacing;
var spacingLeft=(parseInt(state.style[mxConstants.STYLE_SPACING_LEFT]||0))*scale+spacing;
var x=state.x+offset.x+spacingLeft;
var y=state.y+offset.y+spacingTop;
var width=Math.max(minWidth,state.width-spacingLeft-spacingRight);
var height=Math.max(minHeight,state.height-spacingTop-spacingBottom);
this.textarea.style.left=x+'px';
this.textarea.style.top=y+'px';
this.textarea.style.width=width+'px';
this.textarea.style.height=height+'px';
this.textarea.style.zorder=1;
this.textarea.value=this.graph.convertValueToString(cell);
document.body.appendChild(this.textarea);
this.textarea.focus();
this.textarea.select();
}
mxCellEditor.prototype.isEditing=function(cell)
{
return this.cell!=null;
}
mxCellEditor.prototype.stopEditing=function(isCancel)
{
isCancel=isCancel||false;
if(this.cell!=null)
{
if(this.textNode!=null){
this.textNode.style.display='inline';
this.textNode=null;
}
if(!isCancel)
{
this.graph.labelChanged(this.cell,this.textarea.value.replace(/\r/g,''));
}
this.cell=null;
this.textarea.blur();
this.textarea.parentNode.removeChild(this.textarea);
}
}
}

{
function mxCellRenderer(){
this.shapes=new Array();
this.createDefaultShapes();
}
mxCellRenderer.prototype.createDefaultShapes=function(){
this.registerShape(mxConstants.SHAPE_ARROW,mxArrow);
this.registerShape(mxConstants.SHAPE_RECTANGLE,mxRectangleShape);
this.registerShape(mxConstants.SHAPE_ELLIPSE,mxEllipse);
this.registerShape(mxConstants.SHAPE_RHOMBUS,mxRhombus);
this.registerShape(mxConstants.SHAPE_IMAGE,mxImage);
this.registerShape(mxConstants.SHAPE_LINE,mxLine);
this.registerShape(mxConstants.SHAPE_LABEL,mxLabel);
this.registerShape(mxConstants.SHAPE_CYLINDER,mxCylinder);
this.registerShape(mxConstants.SHAPE_SWIMLANE,mxSwimlane);
this.registerShape(mxConstants.SHAPE_CONNECTOR,mxConnector);
this.registerShape(mxConstants.SHAPE_ACTOR,mxActor);
}
mxCellRenderer.prototype.registerShape=function(key,shape){
this.shapes[key]=shape;
}
mxCellRenderer.prototype.initialize=function(state){
var model=state.view.graph.getModel();
if(state.shape==null&&state.cell!=state.view.currentRoot&&(model.isVertex(state.cell)||model.isEdge(state.cell))){
this.createShape(state);
if(state.shape!=null){
state.shape.init(state.view.getDrawPane());
state.shape.scale=state.view.scale;
this.createLabel(state);
this.createOverlay(state);
this.createControl(state);
this.installListeners(state);
}

var cells=state.view.graph.getSelectionCells();
for(var i=0;i<cells.length;i++){
if(cells[i]==state.cell){
state.doCreateHandler=true;
break;
}
}
}
}
mxCellRenderer.prototype.createShape=function(state){
if(state.style!=null){
var graph=state.view.graph;
var isEdge=graph.getModel().isEdge(state.cell);
var key=state.style[mxConstants.STYLE_SHAPE];
var ctor=(key!=null)?this.shapes[key]:null;
if(ctor==null)
{
if(isEdge)
{
ctor=mxPolyline;
}
else
{
ctor=mxRectangleShape;
}
}
state.shape=new ctor();
if(isEdge)
{
state.shape.points=state.absolutePoints;
}
else
{
state.shape.bounds=new mxRectangle(state.x,state.y,state.width,state.height);
}
state.shape.dialect=state.view.graph.dialect;
this.configureShape(state);
}
}
mxCellRenderer.prototype.configureShape=function(state){
state.shape.apply(state);
var image=state.view.graph.getImage(state);
if(image!=null){
state.shape.image=image;
}
var indicator=state.view.graph.getIndicatorColor(state);
var key=state.view.graph.getIndicatorShape(state);
var ctor=(key!=null)?this.shapes[key]:null;
if(indicator!=null){
state.shape.indicatorShape=ctor;
state.shape.indicatorColor=indicator;
state.shape.indicatorGradientColor=
state.view.graph.getIndicatorGradientColor(state);
}else{
var indicator=state.view.graph.getIndicatorImage(state);
if(indicator!=null){
state.shape.indicatorImage=indicator;
}
}
}
mxCellRenderer.prototype.createLabel=function(state){
var isEdge=state.view.graph.getModel().isEdge(state.cell);
if(state.style[mxConstants.STYLE_FONTSIZE]>0||state.style[mxConstants.STYLE_FONTSIZE]==null)
{
var value=state.view.graph.getLabel(state.cell);
if(isEdge&&(value==null||value.length==0))
{
return;
}


var width=state.width;
var height=state.height;
if(state.view.graph.isSwimlane(state.cell))
{
var startSize=state.style[mxConstants.STYLE_STARTSIZE]||0;
height=startSize;
}
var bounds=(isEdge)?new mxRectangle(state.absoluteOffset.x,state.absoluteOffset.y,0,0):
new mxRectangle(state.x,state.y,state.width,state.height);
var valign=(isEdge)?mxConstants.ALIGN_TOP:
state.view.graph.getVerticalAlign(state);
state.text=new mxText(value,bounds,(isEdge)?mxConstants.ALIGN_LEFT:
state.style[mxConstants.STYLE_ALIGN],valign,state.style[mxConstants.STYLE_FONTCOLOR],state.style[mxConstants.STYLE_FONTFAMILY],state.style[mxConstants.STYLE_FONTSIZE],state.style[mxConstants.STYLE_FONTSTYLE],state.style[mxConstants.STYLE_SPACING],state.style[mxConstants.STYLE_SPACING_TOP],state.style[mxConstants.STYLE_SPACING_RIGHT],state.style[mxConstants.STYLE_SPACING_BOTTOM],state.style[mxConstants.STYLE_SPACING_LEFT],state.style[mxConstants.STYLE_HORIZONTAL]=="true");
state.text.dialect=state.view.graph.dialect;
if(state.text.dialect!=mxConstants.DIALECT_SVG&&!isEdge){
if(state.shape.label!=null){
state.text.init(state.shape.label);
}else{
state.text.init(state.shape.node);
}
}else{













if(state.text.dialect==mxConstants.DIALECT_VML&&isEdge){
state.text.init(state.view.graph.container);
}else{
state.text.init(state.view.getDrawPane());
}
}
}
}
mxCellRenderer.prototype.createOverlay=function(state){
var graph=state.view.graph;
var overlay=graph.getOverlay(state.cell);
if(overlay!=null){
var b=new mxRectangle(0,0,16,16);
state.overlay=new mxImage(b,overlay.image);
state.overlay.dialect=state.view.graph.dialect;
state.overlay.init(state.view.getOverlayPane());
state.overlay.node.style.cursor='help';
mxEvent.addListener(state.overlay.node,'click',function(evt){
overlay.dispatchEvent('click',overlay,evt,state.cell);
});
mxEvent.addListener(state.overlay.node,'mousedown',function(evt){
mxEvent.consume(evt);
});
mxEvent.addListener(state.overlay.node,'mousemove',function(evt){
graph.dispatchGraphEvent('mousemove',evt,state.cell,overlay);
});
}
}
mxCellRenderer.prototype.createControl=function(state){
var graph=state.view.graph;
var image=graph.getExpanderImage(state);
if(image!=null){
var b=new mxRectangle(0,0,9,9);
state.control=new mxImage(b,image);
state.control.dialect=state.view.graph.dialect;
state.control.init(state.view.getOverlayPane());
var node=state.control.innerNode||state.control.node;
if(graph.isEnabled()){
node.style.cursor='pointer';
}
mxEvent.addListener(node,'click',function(evt){
if(graph.isEnabled()){
var cells=new Array();
cells[0]=state.cell;
if(graph.getModel().isCollapsed(state.cell)){
graph.expand(cells);
}else{
graph.collapse(cells);
}
mxEvent.consume(evt);
}
});
mxEvent.addListener(node,'mousedown',function(evt){
graph.dispatchGraphEvent('mousedown',evt,state.cell);
mxEvent.consume(evt);
});
mxEvent.addListener(node,'mousemove',function(evt){
graph.dispatchGraphEvent('mousemove',evt,state.cell,mxResources.get('collapse-expand'));
});
}
}
mxCellRenderer.prototype.installListeners=function(state){
var graph=state.view.graph;
if(graph.dialect==mxConstants.DIALECT_SVG){
var events='all';
if(state.view.graph.getModel().isEdge(state.cell)&&state.shape.stroke!=null&&state.shape.fill==null)
{
events='visibleStroke';
}
if(state.shape.innerNode!=null){
state.shape.innerNode.setAttribute('style','pointer-events:'+events);
}else{
state.shape.node.setAttribute('style','pointer-events:'+events);
}
}
if(graph.isEnabled()){
if(state.view.graph.getModel().isEdge(state.cell)){
state.shape.node.style.cursor='pointer';
}else if(state.view.graph.isMovable(state.cell)){
state.shape.node.style.cursor='move';
}
}
mxEvent.addListener(state.shape.node,'mousedown',function(evt){
graph.editor.stopEditing();



if(state.shape!=null&&mxEvent.getSource(evt)==state.shape.content)
{
graph.dispatchGraphEvent('mousedown',evt);
}else{
graph.dispatchGraphEvent('mousedown',evt,state.cell);
}
});
mxEvent.addListener(state.shape.node,'mousemove',function(evt){
if(state.shape!=null&&mxEvent.getSource(evt)==state.shape.content)
{
graph.dispatchGraphEvent('mousemove',evt);
}else{
graph.dispatchGraphEvent('mousemove',evt,state.cell);
}
});
mxEvent.addListener(state.shape.node,'mouseup',function(evt){
if(state.shape!=null&&mxEvent.getSource(evt)==state.shape.content)
{
graph.dispatchGraphEvent('mouseup',evt);
}else{
graph.dispatchGraphEvent('mouseup',evt,state.cell);
}
});
mxEvent.addListener(state.shape.node,'dblclick',function(evt){
if(state.shape!=null&&mxEvent.getSource(evt)==state.shape.content)
{
graph.dblClick(evt);
}else{
graph.dblClick(evt,state.cell);
}
mxEvent.consume(evt);
});
if(state.text!=null)
{
if(graph.isEnabled()){
state.text.node.style.cursor='move';
}
mxEvent.addListener(state.text.node,'mousedown',function(evt){
graph.editor.stopEditing();

if(graph.getModel().isEdge(state.cell)&&graph.isCellSelected(state.cell))
{
graph.dispatchGraphEvent('mousedown',evt,state.cell,mxEdgeHandler.prototype.LABEL_INDEX);
}
else
{
graph.dispatchGraphEvent('mousedown',evt,state.cell);
}
});
mxEvent.addListener(state.text.node,'mousemove',function(evt){
graph.dispatchGraphEvent('mousemove',evt,state.cell);
});
mxEvent.addListener(state.text.node,'mouseup',function(evt){
graph.dispatchGraphEvent('mouseup',evt,state.cell);
});
mxEvent.addListener(state.text.node,'dblclick',function(evt){
graph.dblClick(evt,state.cell);
mxEvent.consume(evt);
});
}
}
mxCellRenderer.prototype.redraw=function(state){
var isEdge=state.view.graph.getModel().isEdge(state.cell);
if(state.shape!=null){
var s=state.view.scale;
if(!mxUtils.equals(state,state.shape.bounds)||state.shape.scale!=s)
{
state.shape.bounds=new mxRectangle(state.x,state.y,state.width,state.height);
state.shape.scale=s;
state.shape.redraw();
}
if(isEdge){
state.shape.points=state.absolutePoints;
state.shape.scale=s;
state.shape.redraw();
}
if(state.text!=null){
var bounds=(isEdge)?new mxRectangle(state.absoluteOffset.x,state.absoluteOffset.y,0,0):
state.shape.bounds;
var boundsChanged=!mxUtils.equals(bounds,state.text.bounds);
if(boundsChanged){
state.text.bounds=bounds;
}
if(state.text.scale!=s||boundsChanged){
state.text.scale=s;
state.text.redraw();
}
}
var overlay=state.view.graph.getOverlay(state.cell);
if(overlay!=null&&state.overlay==null){
this.createOverlay(state);
}else if(overlay==null&&state.overlay!=null){
state.overlay.destroy();
state.overlay=null;
}if(state.overlay!=null){
var b=overlay.getBounds(state);
if(b!=null){
state.overlay.bounds=b;
state.overlay.scale=s;
state.overlay.redraw();
}
}
if(state.control!=null){
var b=(isEdge)?new mxRectangle(state.x+state.width/2-4*s,state.y+state.height/2-4*s,9*s,9*s)
:new mxRectangle(state.x+4*s,state.y+4*s,9*s,9*s);
state.control.bounds=b;
state.control.scale=s;
state.control.redraw();
}
}
if(state.doCreateHandler){
state.doCreateHandler=null;
state.view.graph.createHandler(state);
}
if(state.view.graph.hasHandler(state)){
state.view.graph.redrawHandler(state);
}
}
mxCellRenderer.prototype.destroy=function(state){
if(state.shape!=null){
if(state.text!=null){
state.text.destroy();
state.text=null;
}
if(state.overlay!=null){
state.overlay.destroy();
state.overlay=null;
}
if(state.control!=null){
state.control.destroy();
state.control=null;
}
state.shape.destroy();
state.shape=null;
}
}
}

var mxEdgeStyle={
SideToSide:function(state,source,target,points,result)
{
if(state.style[mxConstants.STYLE_HORIZONTAL]=="true")
{
mxEdgeStyle.TopToBottom(state,source,target,points,result);
}
else
{
var pt=(points!=null)?points[0]:null;
if(pt!=null)
{
var view=state.view;
pt=new mxPoint(view.scale*(view.translate.x+pt.x+state.origin.x),view.scale*(view.translate.y+pt.y+state.origin.y));
}
var l=Math.max(source.x,target.x);
var r=Math.min(source.x+source.width,target.x+target.width);
var t=Math.max(source.y,target.y);
var b=Math.min(source.y+source.height,target.y+target.height);
if((r<l&&b<t)||pt!=null)
{
var x=(pt!=null)?pt.x:r+(l-r)/2;
var y1=source.y+source.height/2;
var y2=target.y+target.height/2;
if(!mxUtils.contains(target,x,y1)&&!mxUtils.contains(source,x,y1))
{
result.push({x:x,y:y1,isRouted:true});
}
if(!mxUtils.contains(target,x,y2)&&!mxUtils.contains(source,x,y2))
{
result.push({x:x,y:y2,isRouted:true});
}
if(pt!=null&&result.length==1)
{
result.push({x:x,y:pt.y,isRouted:true});
}
}
}
},
TopToBottom:function(state,source,target,points,result)
{
var pt=(points!=null)?points[0]:null;
if(pt!=null)
{
var view=state.view;
pt=new mxPoint(view.scale*(view.translate.x+pt.x+state.origin.x),view.scale*(view.translate.y+pt.y+state.origin.y));
}
var t=Math.max(source.y,target.y);
var b=Math.min(source.y+source.height,target.y+target.height);
if(b<t||pt!=null)
{
var x=source.x+source.width/2;
var y=(pt!=null)?pt.y:b+(t-b)/2;
if(!mxUtils.contains(target,x,y)&&!mxUtils.contains(source,x,y))
{
result.push({x:x,y:y,isRouted:true});
}
x=target.x+target.width/2;
if(!mxUtils.contains(target,x,y)&&!mxUtils.contains(source,x,y))
{
result.push({x:x,y:y,isRouted:true});
}
if(pt!=null&&result.length==1)
{
result.push({x:pt.x,y:y,isRouted:true});
}
}
}
}

{
function mxGraphView(graph)
{
this.graph=graph;
this.translate=new mxPoint(0,0);
this.bounds=new mxRectangle(0,0,0,0);

if(graph!=null)
{
this.model=graph.getModel();
if(this.model.viewCount==null)
{
this.model.viewCount=0;
}
this.id=this.model.viewCount;
this.model.viewCount++;
}
}
mxGraphView.prototype=new mxEventSource();
mxGraphView.prototype.constructor=mxGraphView;
mxGraphView.prototype.currentRoot=null;
mxGraphView.prototype.bounds=null;
mxGraphView.prototype.scale=1;
mxGraphView.prototype.translate=null;
mxGraphView.prototype.getBounds=function(cells)
{
if(cells!=null&&cells.length>0)
{
var state=this.getState(cells[0]);
if(state!=null)
{
var minX=state.x;
var minY=state.y;
var maxX=state.x+state.width;
var maxY=state.y+state.height;
for(var i=1;i<cells.length;i++)
{
state=this.getState(cells[i]);
minX=Math.min(minX,state.x);
minY=Math.min(minY,state.y);
maxX=Math.max(maxX,state.x+state.width);
maxY=Math.max(maxY,state.y+state.height);
}
return new mxRectangle(minX,minY,maxX-minX,maxY-minY);
}
}
return null;
}
mxGraphView.prototype.setCurrentRoot=function(root)
{
if(this.currentRoot!=root)
{
var change=new mxCurrentRootChange(this,root);
change.execute();
var edit=new mxUndoableEdit(this,false);
edit.add(change);
this.dispatchEvent('undo',this,edit);
this.graph.sizeDidChange();
}
}
mxGraphView.prototype.setScale=function(scale)
{
var oldScale=this.scale;
this.scale=scale;
this.dispatchEvent('scale',this,oldScale,scale);
this.revalidate();
}
mxGraphView.prototype.setTranslate=function(dx,dy)
{
var oldDx=this.translate.x;
var oldDy=this.translate.y;
this.translate.x=dx;
this.translate.y=dy;
this.dispatchEvent('translate',this,oldDx,oldDy,dx,dy);
this.revalidate();
}
mxGraphView.prototype.revalidate=function()
{
this.invalidate();
this.validate();
}
mxGraphView.prototype.refresh=function()
{
if(this.currentRoot!=null)
{
this.clear();
this.invalidate();
}
else
{
this.invalidate();
}
this.validate();
}
mxGraphView.prototype.clear=function(cell,isForce)
{
isForce=(isForce!=null)?isForce:false;
cell=cell||this.model.getRoot();
this.removeState(cell);
if(isForce||cell!=this.currentRoot)
{
var childCount=this.model.getChildCount(cell);
for(var i=0;i<childCount;i++)
{
this.clear(this.model.getChildAt(cell,i),isForce);
}
}
else
{
this.invalidate(cell);
}
}
mxGraphView.prototype.invalidate=function(cell)
{
cell=cell||this.model.getRoot();
var state=this.getState(cell);
if(state==null||!state.invalid)
{
if(state!=null)
{
state.invalid=true;
}
var childCount=this.model.getChildCount(cell);
for(var i=0;i<childCount;i++)
{
this.invalidate(this.model.getChildAt(cell,i));
}
var edgeCount=this.model.getEdgeCount(cell);
for(var i=0;i<edgeCount;i++)
{
this.invalidate(this.model.getEdgeAt(cell,i));
}
}
}
mxGraphView.prototype.validate=function(cell)
{
var t0=mxLog.enter('mxGraphView.validate');
window.status=mxResources.get('updatingDocument');
cell=cell||(this.currentRoot!=null)?this.currentRoot:this.model.getRoot();
this.validateBounds(null,null,cell);
this.bounds=this.validatePoints(cell);
this.validateBackground();
window.status=mxResources.get('done');
mxLog.leave('mxGraphView.validate',t0);
}
mxGraphView.prototype.validateBackground=function()
{
var bg=this.graph.getBackgroundImage();
if(bg!=null)
{
if(this.backgroundImage==null||this.backgroundImage.image!=bg)
{
if(this.backgroundImage!=null)
{
this.backgroundImage.destroy();
}
var bounds=new mxRectangle(0,0,1,1);
this.backgroundImage=new mxImage(bounds,bg);
this.backgroundImage.dialect=this.graph.dialect;
this.backgroundImage.init(this.backgroundPane);
}
this.backgroundImage.scale=this.scale;
this.backgroundImage.bounds.x=this.scale*this.translate.x;
this.backgroundImage.bounds.y=this.scale*this.translate.y;
this.backgroundImage.bounds.width=
this.scale*this.graph.getBackgroundImageWidth();
this.backgroundImage.bounds.height=
this.scale*this.graph.getBackgroundImageHeight();
this.backgroundImage.redraw();
}
else if(this.backgroundImage!=null)
{
this.backgroundImage.destroy();
this.backgroundImage=null;
}
}
mxGraphView.prototype.validateBounds=function(parent,parentState,cell)
{
var state=this.getState(cell,true);
var geo=this.model.getGeometry(cell);
if(state!=null&&state.invalid&&geo!=null)
{
if(cell!=this.currentRoot&&parent!=null&&parentState!=null)
{
if(geo.isRelative)
{
}
else
{
state.origin.x=parentState.origin.x;
state.origin.y=parentState.origin.y;
if(!this.model.isEdge(cell))
{
state.origin.x+=geo.x;
state.origin.y+=geo.y;
}
}
}else
{
state.origin.x=0;
state.origin.y=0;
}
if(geo.isRelative)
{
}
else
{
state.x=this.scale*(this.translate.x+state.origin.x);
state.y=this.scale*(this.translate.y+state.origin.y);
state.width=this.scale*geo.width;
state.height=this.scale*geo.height;
}
}
if(!this.model.isCollapsed(cell)||cell==this.currentRoot)
{
var childCount=this.model.getChildCount(cell);
for(var i=0;i<childCount;i++)
{
this.validateBounds(cell,state,this.model.getChildAt(cell,i));
}
}
}
mxGraphView.prototype.validatePoints=function(cell)
{
var state=this.getState(cell);
var minX=null;
var minY=null;
var maxX=0;
var maxY=0;
if(state!=null)
{
if(state.invalid)
{
if(this.model.isEdge(cell))
{
var src=this.getVisibleTerminal(cell,true);
if(src!=null&&!this.model.isAncestor(src,cell))
{
this.validatePoints(src);
}
var trg=this.getVisibleTerminal(cell,false);
if(trg!=null&&!this.model.isAncestor(trg,cell))
{
this.validatePoints(trg);
}
var geo=this.model.getGeometry(cell);
this.updatePoints(state,geo.points);
this.updateTerminalPoints(state);
this.updateEdgeBounds(state);
this.updateEdgeLabelOffset(state);
}
state.invalid=false;
if(cell!=this.currentRoot){
this.graph.cellRenderer.redraw(state);
}
}
if(this.model.isEdge(cell)||this.model.isVertex(cell))
{
minX=state.x;
minY=state.y;
maxX=state.x+state.width;
maxY=state.y+state.height;
}
}
if(!this.model.isCollapsed(cell)||cell==this.currentRoot)
{
var childCount=this.model.getChildCount(cell);
for(var i=0;i<childCount;i++)
{
var bounds=this.validatePoints(this.model.getChildAt(cell,i));
minX=(minX!=null)?Math.min(minX,bounds.x):bounds.x;
minY=(minY!=null)?Math.min(minY,bounds.y):bounds.y;
maxX=Math.max(maxX,bounds.x+bounds.width);
maxY=Math.max(maxY,bounds.y+bounds.height);
}
}
return new mxRectangle(minX,minY,maxX-minX,maxY-minY);
}
mxGraphView.prototype.updatePoints=function(state,points)
{
if(state!=null)
{
var edge=state.cell;
var geo=this.model.getGeometry(edge);
var src=this.getVisibleTerminal(edge,true);
var trg=this.getVisibleTerminal(edge,false);
var pts=new Array();
pts.push(null);
var edgeStyle=state.style[mxConstants.STYLE_EDGE];
if(edgeStyle!=null&&src!=null&&trg!=null)
{
var source=this.getState(src);
var target=this.getState(trg);
if(source!=null&&target!=null){
edgeStyle(state,source,target,points,pts);
}
}
else if(points!=null)
{
for(var i=0;i<points.length;i++)
{
var pt=mxUtils.clone(points[i]);
pt.x+=this.translate.x+state.origin.x;
pt.y+=this.translate.y+state.origin.y;
pt.x*=this.scale;
pt.y*=this.scale;
pts.push(pt);
}
}
pts.push(null);
state.absolutePoints=pts;
}
}
mxGraphView.prototype.updateTerminalPoints=function(state)
{
var edge=state.cell;
var geo=this.model.getGeometry(edge);
var pt=geo.getTerminalPoint(true);
if(pt!=null)
{
pt=new mxPoint(this.scale*(this.translate.x+pt.x+state.origin.x),this.scale*(this.translate.y+pt.y+state.origin.y));
state.setAbsoluteTerminalPoint(pt,true);
}
else
{
state.setAbsoluteTerminalPoint(null,true);
}
pt=geo.getTerminalPoint(false);
if(pt!=null)
{
pt=new mxPoint(this.scale*(this.translate.x+pt.x+state.origin.x),this.scale*(this.translate.y+pt.y+state.origin.y));
state.setAbsoluteTerminalPoint(pt,false);
}
else
{
state.setAbsoluteTerminalPoint(null,false);
}
var src=this.getVisibleTerminal(edge,true);
var trg=this.getVisibleTerminal(edge,false);
if(trg!=null)
{
this.updateTerminalPoint(state,trg,src,false);
}
if(src!=null)
{
this.updateTerminalPoint(state,src,trg,true);
}
}
mxGraphView.prototype.updateTerminalPoint=function(state,start,end,isSource)
{
var endState=this.getState(end);
state.setAbsoluteTerminalPoint(this.getPerimeterPoint(state,start,end,isSource),isSource);
}
mxGraphView.prototype.getPerimeterPoint=function(state,start,end,isSource)
{
var point=null;
var terminalState=this.getState(start);
if(terminalState!=null)
{
var perimeter=terminalState.style[mxConstants.STYLE_PERIMETER];
var next=this.getNextPoint(state,end,isSource);
if(perimeter!=null&&next!=null)
{
var bounds=new mxRectangle(terminalState.x,terminalState.y,terminalState.width,terminalState.height);
var border=parseInt(state.style[mxConstants.STYLE_PERIMETER_SPACING]||0);
border+=parseInt(terminalState.style[mxConstants.STYLE_PERIMETER_SPACING]||0);
if(border>0){
bounds.x-=border;
bounds.y-=border;
bounds.width+=2*border;
bounds.height+=2*border;
}
point=perimeter(bounds,state,terminalState,isSource,next);
}
else
{
point=new mxPoint(terminalState.getCenterX(),terminalState.getCenterY());
}
}
return point;
}
mxGraphView.prototype.getNextPoint=function(state,opposite,isSource)
{
var point=null;
var pts=state.absolutePoints;
if(pts!=null&&(isSource||pts.length>2||opposite==null))
{
var count=pts.length;
point=pts[(isSource)?Math.min(1,count-1):Math.max(0,count-2)];
}
if(point==null&&opposite!=null)
{
var oppositeState=this.getState(opposite);
if(oppositeState!=null)
{
point=new mxPoint(oppositeState.getCenterX(),oppositeState.getCenterY());
}
}
return point;
}
mxGraphView.prototype.getVisibleTerminal=function(edge,isSource)
{
var result=this.model.getTerminal(edge,isSource);
var best=result;
while(result!=null&&result!=this.currentRoot)
{
if(!this.model.isVisible(best)||this.model.isCollapsed(result))
{
best=result;
}
result=this.model.getParent(result);
}
return best;
}
mxGraphView.prototype.updateEdgeBounds=function(state)
{
var points=state.absolutePoints;
state.length=0;
if(points!=null&&points.length>0)
{
var p0=points[0];
var pe=points[points.length-1];
if(p0==null||pe==null)
{




mxLog.warn('mxGraphView.updateEdgeBounds: '+'cannot resolve terminal on cell.id='+state.cell.getId()+' p0='+p0+' pe='+pe);
}
else
{
if(p0.x!=pe.x||p0.y!=pe.y)
{
var dx=pe.x-p0.x;
var dy=pe.y-p0.y;
state.terminalDistance=Math.sqrt(dx*dx+dy*dy);
}
else
{
state.terminalDistance=0;
}
var length=0;
var pt=points[0];
if(pt!=null)
{
var minX=pt.x;
var minY=pt.y;
var maxX=minX;
var maxY=minY;
for(var i=1;i<points.length;i++)
{
var tmp=points[i];
if(tmp!=null)
{
var dx=pt.x-tmp.x;
var dy=pt.y-tmp.y;
length+=Math.sqrt(dx*dx+dy*dy);
pt=tmp;
minX=Math.min(pt.x,minX);
minY=Math.min(pt.y,minY);
maxX=Math.max(pt.x,maxX);
maxY=Math.max(pt.y,maxY);
}
}
state.length=length;
var markerSize=1;
state.x=minX;
state.y=minY;
state.width=Math.max(markerSize,maxX-minX);
state.height=Math.max(markerSize,maxY-minY);
}
}
}
}
mxGraphView.prototype.updateEdgeLabelOffset=function(state)
{
var geometry=this.model.getGeometry(state.cell);
var points=state.absolutePoints;
state.absoluteOffset.x=state.getCenterX();
state.absoluteOffset.y=state.getCenterY();
if(points!=null&&points.length>0)
{
var p0=points[0];
var pe=points[points.length-1];
if(p0!=null&&pe!=null)
{
var dx=pe.x-p0.x;
var dy=pe.y-p0.y;
var x0=0;
var y0=0;
var off=geometry.offset;
if(off!=null)
{
x0=off.x;
y0=off.y;
}
var x=p0.x+dx/2+x0*this.scale;
var y=p0.y+dy/2+y0*this.scale;
state.absoluteOffset.x=x;
state.absoluteOffset.y=y;
}
}
}
mxGraphView.prototype.getState=function(cell,create)
{
create=create||false;
var state=null;
if(cell!=null)
{
if(cell.states==null)
{
cell.states=new Array();
}
state=cell.states[this.id];
if(state==null&&create&&this.model.isVisible(cell))
{
state=this.createState(cell);
cell.states[this.id]=state;
}
}
return state;
}
mxGraphView.prototype.removeState=function(cell)
{
var state=null;
if(cell!=null&&cell.states!=null)
{
state=cell.states[this.id];
if(state!=null)
{
this.graph.cellRenderer.destroy(state);
state.destroy();
cell.states[this.id]=null;
}
}
return state;
}
mxGraphView.prototype.createState=function(cell)
{
var style=this.graph.getCellStyle(cell);
var state=new mxCellState(this,cell,style);
this.graph.cellRenderer.initialize(state);
return state;
}
mxGraphView.prototype.getCanvas=function()
{
return this.canvas;
}
mxGraphView.prototype.getBackgroundPane=function()
{
return this.backgroundPane;
}
mxGraphView.prototype.getDrawPane=function()
{
return this.drawPane;
}
mxGraphView.prototype.getOverlayPane=function()
{
return this.overlayPane;
}
mxGraphView.prototype.init=function(container)
{
var graph=this.graph;
if(container!=null)
{
mxEvent.addListener(container,'mousedown',function(evt){
graph.dispatchGraphEvent('mousedown',evt);
});
mxEvent.addListener(container,'mousemove',function(evt){
graph.dispatchGraphEvent('mousemove',evt);
});
mxEvent.addListener(container,'mouseup',function(evt){
graph.dispatchGraphEvent('mouseup',evt);
});
mxEvent.addListener(container,'dblclick',function(evt){
graph.dblClick(evt);
mxEvent.consume(evt);
});
}
if(graph.dialect==mxConstants.DIALECT_SVG)
{
this.createSvg();
}
else if(graph.dialect==mxConstants.DIALECT_VML)
{
this.createVml();
}
else
{
this.createHtml();
}
}
mxGraphView.prototype.createHtml=function()
{
var container=this.graph.container;
if(container!=null)
{
var width=container.offsetWidth;
var height=container.offsetHeight;
this.canvas=this.createHtmlPane(width,height);



this.backgroundPane=this.createHtmlPane(1,1);
this.drawPane=this.createHtmlPane(1,1);
this.overlayPane=this.createHtmlPane(1,1);
this.canvas.appendChild(this.backgroundPane);
this.canvas.appendChild(this.drawPane);
this.canvas.appendChild(this.overlayPane);
if(container!=null)
{
container.appendChild(this.canvas);
}
}
}
mxGraphView.prototype.createHtmlPane=function(width,height)
{
var pane=document.createElement('DIV');
pane.style.width=width+'px';
pane.style.height=height+'px';
pane.style.position='absolute';
pane.style.left='0px';
pane.style.top='0px';
return pane;
}
mxGraphView.prototype.createVml=function()
{
var container=this.graph.container;
if(container!=null){
var width=container.offsetWidth;
var height=container.offsetHeight;
this.canvas=this.createVmlPane(width,height);
this.backgroundPane=this.createVmlPane(width,height);
this.drawPane=this.createVmlPane(width,height);
this.overlayPane=this.createVmlPane(width,height);
this.canvas.appendChild(this.backgroundPane);
this.canvas.appendChild(this.drawPane);
this.canvas.appendChild(this.overlayPane);
if(container!=null)
{
container.appendChild(this.canvas);
}
}
}
mxGraphView.prototype.createVmlPane=function(width,height)
{
var pane=document.createElement('v:group');

pane.setAttribute('coordsize',width+','+height);
pane.setAttribute('coordorigin','0,0');
pane.style.width=width+'px';
pane.style.height=height+'px';
pane.style.left='0px';
pane.style.top='0px';
pane.style.position='absolute';
return pane;
}
mxGraphView.prototype.createSvg=function(){
var container=this.graph.container;
this.canvas=document.createElementNS(mxConstants.NS_SVG,'g');
this.drawPane=document.createElementNS(mxConstants.NS_SVG,'g');
this.overlayPane=document.createElementNS(mxConstants.NS_SVG,'g');
this.backgroundPane=document.createElementNS(mxConstants.NS_SVG,'g');
this.canvas.appendChild(this.backgroundPane);
this.canvas.appendChild(this.drawPane);
this.canvas.appendChild(this.overlayPane);
var root=document.createElementNS(mxConstants.NS_SVG,'svg');

var self=this;
var f=function(evt)
{
if(self.graph.container!=null)
{
var width=self.graph.container.offsetWidth;
var height=self.graph.container.offsetHeight;
root.setAttribute('width',Math.max(width,self.bounds.width));
root.setAttribute('height',Math.max(height,self.bounds.height));
}
};
mxEvent.addListener(window,'resize',f);
if(mxClient.IS_OP)
{
f();
}
root.appendChild(this.canvas);
if(container!=null)
{
if(container.style.overflow=='auto')
{
var wrapper=document.createElement('div');
wrapper.style.position='absolute';
wrapper.style.left='0px';
wrapper.style.right='0px';
wrapper.style.width='1px';
wrapper.style.height='1px';
wrapper.style.borderColor='white';
wrapper.style.borderStyle='solid';
wrapper.appendChild(root);
container.appendChild(wrapper);
}
else
{
container.appendChild(root);
}
}
}
function mxCurrentRootChange(view,root)
{
this.view=view;
this.root=root;
this.previous=root;
this.isUp=root==null;
if(!this.isUp)
{
var tmp=this.view.currentRoot;
var model=this.view.graph.getModel();
while(tmp!=null)
{
if(tmp==root)
{
this.isUp=true;
break;
}
tmp=model.getParent(tmp);
}
}
}
mxCurrentRootChange.prototype.execute=function()
{
var tmp=this.view.currentRoot;
this.view.currentRoot=this.previous;
this.previous=tmp;
var name=(this.isUp)?'up':'down';
this.view.dispatchEvent(name,this.view,this.previous,this.view.currentRoot);
if(this.isUp)
{
this.view.clear(this.view.currentRoot,true);
this.view.validate();
}
else
{
this.view.refresh();
}
this.isUp=!this.isUp;
}
}

{
function mxGraph(container,model,renderHint)
{
this.container=container;
this.model=(model!=null)?model:new mxGraphModel();
if(mxClient.IS_SVG)
{
this.dialect=mxConstants.DIALECT_SVG;
}
else if(renderHint=='exact'&&mxClient.IS_VML)
{
this.dialect=mxConstants.DIALECT_VML;
}
else if(renderHint=='fastest')
{
this.dialect=mxConstants.DIALECT_STRICTHTML;
}
else if(renderHint=='faster')
{
this.dialect=mxConstants.DIALECT_PREFERHTML;
}
else
{
this.dialect=mxConstants.DIALECT_MIXEDHTML;
}
this.view=new mxGraphView(this);
this.selection=new mxGraphSelection(this);
this.editor=new mxCellEditor(this);
var self=this;
this.model.addListener('change',function(sender,evt)
{
self.graphModelChanged(evt);
});

if(container!=null)
{
this.view.init(container);
this.sizeDidChange();

this.tooltipHandler=new mxTooltipHandler(this);
this.tooltipHandler.isEnabled=false;
this.panningHandler=new mxPanningHandler(this);
this.panningHandler.isPanEnabled=false;
this.connectionHandler=new mxConnectionHandler(this);
this.connectionHandler.isEnabled=false;
this.graphHandler=new mxGraphHandler(this);
}
}
mxResources.add(mxClient.basePath+'js/resources/graph');
mxGraph.prototype=new mxEventSource();
mxGraph.prototype.constructor=mxGraph;
mxGraph.prototype.EMPTY_ARRAY=new Array();

mxGraph.prototype.graphListeners=null;
mxGraph.prototype.model=null;
mxGraph.prototype.selection=null;
mxGraph.prototype.view=null;
mxGraph.prototype.stylesheet=new mxStylesheet();
mxGraph.prototype.cellRenderer=new mxCellRenderer();
mxGraph.prototype.editor=null;
mxGraph.prototype.tolerance=4;
mxGraph.prototype.zoomFactor=1.2;
mxGraph.prototype.multiplicities=new Array();
mxGraph.prototype.gridSize=10;
mxGraph.prototype.defaultParent=null;
mxGraph.prototype.alternateEdgeStyle=null;
mxGraph.prototype.backgroundImage=null;
mxGraph.prototype.backgroundImageWidth=0;
mxGraph.prototype.backgroundImageHeight=0;
mxGraph.prototype.enabled=true;
mxGraph.prototype.editable=true;
mxGraph.prototype.movable=true;
mxGraph.prototype.sizable=true;
mxGraph.prototype.selectable=true;
mxGraph.prototype.autoSize=false;
mxGraph.prototype.autoLayout=true;
mxGraph.prototype.isGridEnabled=true;
mxGraph.prototype.isExtendParentOnResize=true;
mxGraph.prototype.isShiftDownwards=false;
mxGraph.prototype.isShiftRightwards=false;
mxGraph.prototype.isCollapseToPreferredSize=true;
mxGraph.prototype.isKeepSelectionVisibleOnZoom=false;
mxGraph.prototype.isCenterZoom=false;
mxGraph.prototype.isResetViewOnRootChange=true;
mxGraph.prototype.isResetEdgesOnResize=false;
mxGraph.prototype.isResetEdgesOnMove=true;
mxGraph.prototype.isAllowLoops=false;
mxGraph.prototype.isMultigraph=true;
mxGraph.prototype.isAllowDanglingEdges=true;
mxGraph.prototype.isLabelsVisible=true;
mxGraph.prototype.isSwimlaneNesting=true;
mxGraph.prototype.collapsedImage=mxClient.imageBasePath+'collapsed.gif';
mxGraph.prototype.expandedImage=mxClient.imageBasePath+'expanded.gif';
mxGraph.prototype.warningImageBasename=mxClient.imageBasePath+'warning';
mxGraph.prototype.alreadyConnected='Nodes area already connected';
mxGraph.prototype.containsValidationErrors='Contains validation errors';
mxGraph.prototype.getModel=function()
{
return this.model;
}

mxGraph.prototype.setOverlay=function(cell,overlay)
{
cell.overlay=overlay;
var state=this.view.getState(cell);
if(state!=null)
{
this.cellRenderer.redraw(state);
}
this.dispatchEvent('addoverlay',this,cell,overlay);
}
mxGraph.prototype.getOverlay=function(cell)
{
return cell.overlay;
}
mxGraph.prototype.removeOverlay=function(cell)
{
var overlay=cell.overlay;
cell.overlay=null;
var state=this.view.getState(cell);
if(state!=null)
{
this.cellRenderer.redraw(state);
}
this.dispatchEvent('removeoverlay',this,cell,overlay);
return overlay;
}
mxGraph.prototype.clearOverlays=function(cell)
{
cell=(cell!=null)?cell:this.model.getRoot();
this.removeOverlay(cell);
var childCount=this.model.getChildCount(cell);
for(var i=0;i<childCount;i++)
{
this.clearOverlays(this.model.getChildAt(cell,i));
}
}
mxGraph.prototype.setWarning=function(cell,warning,img,isSelect)
{
if(warning!=null&&warning.length>0)
{
if(img==null)
{
img=this.warningImageBasename;
img+=(mxClient.IS_MAC)?'.png':'.gif';
}
var overlay=new mxOverlay(img,'<font color=red>'+warning+'</font>');
var self=this;
if(isSelect)
{
overlay.addListener('click',function(sender,evt)
{
self.setSelectionCell(cell);
});
}
this.setOverlay(cell,overlay);
return overlay;
}
else
{
return this.removeOverlay(cell);
}
}

mxGraph.prototype.click=function(evt,cell)
{
this.dispatchEvent('click',this,evt,cell);
if(this.isEnabled()&&!mxEvent.isConsumed(evt))
{
if(cell!=null)
{
this.selectCellForEvent(cell,evt);
}
else
{
if(!mxEvent.isToggleSelection(evt))
{
this.selection.clear();
}
}
}
}
mxGraph.prototype.dblClick=function(evt,cell)
{
this.dispatchEvent('dblclick',this,evt,cell);
if(this.isEnabled()&&!mxEvent.isConsumed(evt)&&cell!=null)
{
this.edit(cell);
}
}
mxGraph.prototype.edit=function(cell)
{
if(cell==null)
{
cell=this.getSelectionCell();
}
if(cell!=null&&this.isEditable(cell))
{
this.startEditingAtCell(cell);
}
}
mxGraph.prototype.startEditingAtCell=function(cell)
{
this.dispatchEvent('startEditing',this,cell);
this.editor.startEditing(cell);
}
mxGraph.prototype.graphModelChanged=function(changes)
{
for(var i=0;i<changes.length;i++)
{
var change=changes[i];
if(change.constructor==mxRootChange)
{
if(this.isResetViewOnRootChange)
{
this.view.scale=1;
this.view.translate.x=0;
this.view.translate.y=0;
}
this.cellRemoved(change.previous);
this.selection.clear();
this.dispatchEvent('root',this);
}
else if(change.constructor==mxChildChange)
{
if(change.isAdded)
{
this.view.clear(change.child);
}
else
{
this.cellRemoved(change.child,true);
}
var newParent=this.model.getParent(change.child);
if(newParent!=null&&this.model.getChildCount(newParent)<=1)
{
this.view.clear(newParent);
}
if(change.previous!=null&&this.model.getChildCount(change.previous)<=1)
{
this.view.clear(change.previous);
}
}
else if(change.constructor==mxValueChange||change.constructor==mxStyleChange||change.constructor==mxVisibleChange||change.constructor==mxCollapseChange)
{
this.view.invalidate(change.cell);
this.cellRemoved(change.cell,change.constructor==mxVisibleChange);
}
else if(change.constructor==mxTerminalChange||change.constructor==mxGeometryChange)
{
this.view.invalidate(change.cell);
}
}
this.view.validate();
this.sizeDidChange();
}
mxGraph.prototype.sizeDidChange=function()
{
if(this.container!=null&&this.container.style.overflow=='auto'&&mxClient.IS_SVG)
{
var width=this.container.clientWidth-30;
var height=this.container.clientHeight-30;
var w=Math.max(width,this.view.bounds.width+20)+'px';
var h=Math.max(height,this.view.bounds.height+20)+'px';
var root=this.view.getDrawPane().parentNode.parentNode;
root.setAttribute('width',w);
root.setAttribute('height',h);

root=root.parentNode;
root.style.width=w;
root.style.height=h;
}
this.dispatchEvent('size',this,this.view.bounds);
}

mxGraph.prototype.labelChanged=function(cell,newValue)
{
this.model.beginUpdate();
try
{
var oldValue=this.model.getValue(cell);
this.model.setValue(cell,newValue);
if(this.isUpdateSize(cell))
{
this.updateSize(cell);
}
this.dispatchEvent('labelChanged',this,cell,oldValue,newValue);
}
finally
{
this.model.endUpdate();
}
}
mxGraph.prototype.flip=function(edge)
{
this.model.beginUpdate();
try
{
if(this.alternateEdgeStyle!=null)
{
var style=edge.getStyle();
if(style==null||style.length==0)
{
this.model.setStyle(edge,this.alternateEdgeStyle);
}
else
{
this.model.setStyle(edge,null);
}
var geo=this.model.getGeometry(edge);
if(geo!=null)
{
geo=geo.clone();
geo.points=new Array();
this.model.setGeometry(edge,geo);
}
}
this.dispatchEvent('flip',this,edge);
}
finally
{
this.model.endUpdate();
}
}
mxGraph.prototype.getPreferredSizeForCell=function(cell)
{
var result=null;
if(cell!=null)
{
var state=this.view.getState(cell);
var style=(state!=null)?state.style:this.getCellStyle(cell);
if(style!=null&&!this.model.isEdge(cell))
{
var fontSize=style[mxConstants.STYLE_FONTSIZE]||10;
var dx=(style[mxConstants.STYLE_SHAPE]==mxConstants.SHAPE_LABEL&&style[mxConstants.STYLE_VERTICAL_ALIGN]==mxConstants.ALIGN_MIDDLE)?style[mxConstants.STYLE_IMAGE_WIDTH]||24:0;
var dy=(style[mxConstants.STYLE_SHAPE]==mxConstants.SHAPE_LABEL&&style[mxConstants.STYLE_VERTICAL_ALIGN]!=mxConstants.ALIGN_MIDDLE)?style[mxConstants.STYLE_IMAGE_HEIGHT]||14:0;
var value=this.getLabel(cell);
if(value!=null)
{
var lines=value.split('\n');
var maxLength=0;
for(var i=0;i<lines.length;i++)
{
maxLength=Math.max(maxLength,lines[i].length);
}
var width=maxLength*fontSize/2+28+dx;
var height=parseInt(lines.length*fontSize*1.25+20+dy);
if(style[mxConstants.STYLE_HORIZONTAL]=="true")
{
var tmp=height;
height=width;
width=tmp;
}
result=new mxRectangle(0,0,width,height);
}
}
}
return result;
}
mxGraph.prototype.updateSize=function(cell)
{
var size=this.getPreferredSizeForCell(cell);
var geo=this.model.getGeometry(cell);
if(size!=null&&geo!=null)
{
var width=size.width;
var height=size.height;
if(geo.width!=width||geo.height!=height)
{
geo=geo.clone();
geo.width=Math.max(geo.width,width);
this.model.beginUpdate();
try
{
if(this.isSwimlane(cell))
{
var tmp=this.getCellStyle(cell);
var param=height;
if(tmp[mxConstants.STYLE_HORIZONTAL]=="true")
{
param=width;
}
var style=cell.getStyle()||'';
style=mxUtils.setStyle(style,mxConstants.STYLE_STARTSIZE,param-12);
this.model.setStyle(cell,style);
}
else
{
geo.height=Math.max(geo.height,height);
}
this.resize(cell,geo);
}
finally
{
this.model.endUpdate();
}
}
}
}
mxGraph.prototype.setCellStyle=function(style,cells)
{
if(cells==null)
{
cells=this.getSelectionCells();
}
this.model.beginUpdate();
try
{
for(var i=0;i<cells.length;i++)
{
this.model.setStyle(cells[i],style);
}
}
finally
{
this.model.endUpdate();
}
}
mxGraph.prototype.toggleCellStyle=function(key,cell)
{
cell=cell||this.getSelectionCell();
if(cell!=null)
{
var state=this.view.getState(cell);
var style=(state!=null)?state.style:this.getCellStyle(cell);
if(style!=null)
{
var val=(style[key]=="true")?"false":"true";
this.setCellStyles(key,val);
}
}
}
mxGraph.prototype.setCellStyles=function(key,value,cells)
{
cells=(cells!=null)?cells:this.getSelectionCells();
mxUtils.setCellStyles(this.model,cells,key,value);
}
mxGraph.prototype.toggleCellStyleFlags=function(key,flag,cells)
{
this.setCellStyleFlags(key,flag,null,cells);
}
mxGraph.prototype.setCellStyleFlags=function(key,flag,value,cells)
{
cells=(cells!=null)?cells:this.getSelectionCells();
mxUtils.setCellStyleFlags(this.model,cells,key,flag,value);
}
mxGraph.prototype.alignCells=function(align,cells,param)
{
cells=cells||this.getSelectionCells();
if(cells!=null&&cells.length>1)
{
if(param==null)
{
for(var i=0;i<cells.length;i++)
{
var g=this.model.getGeometry(cells[i]);
if(g!=null&&!this.model.isEdge(cells[i]))
{
if(param==null)
{
if(align==mxConstants.ALIGN_CENTER)
{
param=g.x+g.width/2;
break;
}
else if(align==mxConstants.ALIGN_RIGHT)
{
param=g.x+g.width;
}
else if(align==mxConstants.ALIGN_TOP)
{
param=g.y;
}
else if(align==mxConstants.ALIGN_MIDDLE)
{
param=g.y+g.height/2;
break;
}
else if(align==mxConstants.ALIGN_BOTTOM)
{
param=g.y+g.height;
}
else
{
param=g.x;
}
}
else
{
if(align==mxConstants.ALIGN_RIGHT)
{
param=Math.max(param,g.x+g.width);
}
else if(align==mxConstants.ALIGN_TOP)
{
param=Math.min(param,g.y);
}
else if(align==mxConstants.ALIGN_BOTTOM)
{
param=Math.max(param,g.y+g.height);
}
else
{
param=Math.min(param,g.x);
}
}
}
}
}
this.model.beginUpdate();
try
{
for(var i=0;i<cells.length;i++)
{
var g=this.model.getGeometry(cells[i]);
if(g!=null&&!this.model.isEdge(cells[i]))
{
g=g.clone();
if(align==mxConstants.ALIGN_CENTER)
{
g.x=param-g.width/2;
}
else if(align==mxConstants.ALIGN_RIGHT)
{
g.x=param-g.width;
}
else if(align==mxConstants.ALIGN_TOP)
{
g.y=param;
}
else if(align==mxConstants.ALIGN_MIDDLE)
{
g.y=param-g.height/2;
}
else if(align==mxConstants.ALIGN_BOTTOM)
{
g.y=param-g.height;
}
else
{
g.x=param;
}
this.model.setGeometry(cells[i],g);
}
}
}
finally
{
this.model.endUpdate();
}
}
}

mxGraph.prototype.cloneCells=function(cells)
{
var clones=this.model.cloneCells(cells,true);
for(var i=0;i<cells.length;i++)
{
var g=this.model.getGeometry(clones[i]);
if(g!=null)
{
var state=this.view.getState(cells[i]);
var pstate=this.view.getState(this.model.getParent(cells[i]));
if(state!=null&&pstate!=null)
{
if(this.model.isEdge(clones[i]))
{
var pts=state.absolutePoints;
var src=this.model.getTerminal(cells[i],true);
while(src!=null&&mxUtils.indexOf(cells,src)<0)
{
src=this.model.getParent(src);
}
if(src==null)
{
g.setTerminalPoint(new mxPoint(pts[0].x,pts[0].y),true);
}
var trg=this.model.getTerminal(cells[i],false);
while(trg!=null&&mxUtils.indexOf(cells,trg)<0)
{
trg=this.model.getParent(trg);
}
if(trg==null)
{
var n=pts.length-1;
g.setTerminalPoint(new mxPoint(pts[n].x,pts[n].y),false);
}
}
else
{
var dx=pstate.origin.x;
var dy=pstate.origin.y;
g.x+=dx;
g.y+=dy;
}
}
}
}
return clones;
}
mxGraph.prototype.addCells=function(cells,parent,index)
{
parent=parent||this.getDefaultParent();
index=(index!=null)?index:this.model.getChildCount(parent);
this.model.beginUpdate();
try
{
for(var i=0;i<cells.length;i++)
{
this.model.add(parent,cells[i],index+i);
}
this.layout(parent);
this.dispatchEvent('add',this,cells);
}
finally
{
this.model.endUpdate();
}
return cells;
}
mxGraph.prototype.addCell=function(cell,parent,index)
{
parent=parent||this.getDefaultParent();
index=(index!=null)?index:this.model.getChildCount(parent);
this.model.beginUpdate();
try
{
this.model.add(parent,cell,index);
this.layout(parent);
this.dispatchEvent('add',this,[cell]);
}
finally
{
this.model.endUpdate();
}
return cell;
}
mxGraph.prototype.splitEdge=function(edge,cell,newEdge)
{
newEdge=newEdge||edge.clone();
var parent=this.model.getParent(edge);
var index=this.model.getChildCount(parent);
this.model.beginUpdate();
this.model.add(parent,newEdge,index);
this.model.setTerminals(newEdge,this.model.getTerminal(edge,true),cell);
this.model.setTerminal(edge,cell,true);
this.layout(parent);
this.dispatchEvent('add',this,[newEdge]);
this.model.endUpdate();
return newEdge;
}

mxGraph.prototype.hide=function(cells,hideEdges)
{
this.remove(cells,hideEdges,true,false);
}
mxGraph.prototype.show=function(cells,showEdges)
{
this.remove(cells,hideEdges,true,true);
}
mxGraph.prototype.remove=function(cells,removeEdges,isHide,isShow)
{
cells=cells||this.getSelectionCells();
removeEdges=(removeEdges!=null)?removeEdge:true;
isHide=(isHide!=null)?isHide:false;
isShow=(isShow!=null)?isShow:false;
var parent=null;
if(cells.length>0)
{
parent=this.model.getParent(cells[0]);
this.model.beginUpdate();
try
{
for(var i=0;i<cells.length;i++)
{
if(isHide)
{
this.model.setVisible(cells[i],isShow);
}
else
{
this.model.remove(cells[i]);
}
if(removeEdges)
{
this.removeEdges(cells[i],true,isHide);
}
else
{
}
}
if(parent!=null)
{
this.layout(parent);
}
if(isShow)
{
this.dispatchEvent('show',this,cells);
}
else if(isHide)
{
this.dispatchEvent('hide',this,cells);
}
else
{
this.dispatchEvent('remove',this,cells);
}
}
finally
{
this.model.endUpdate();
}
}
}
mxGraph.prototype.removeEdges=function(cell,recurse,isHide,isShow)
{
recurse=(recurse!=null)?recurse:true;
isHide=(isHide!=null)?isHide:false;
isShow=(isShow!=null)?isShow:false;
if(cell!=null)
{
this.model.beginUpdate();
try
{
var edges=this.model.getEdges(cell);
if(edges!=null)
{
if(isHide)
{
for(var i=0;i<edges.length;i++)
{
this.model.setVisible(edges[i],isShow);
}
}
else
{
while(edges.length>0)
{
this.model.remove(edges[0]);
}
}
if(isShow)
{
this.dispatchEvent('show',this,edges);
}
else if(isHide)
{
this.dispatchEvent('hide',this,edges);
}
else
{
this.dispatchEvent('remove',this,edges);
}
}
if(recurse)
{
var childCount=this.model.getChildCount(cell);
for(var i=0;i<childCount;i++)
{
this.removeEdges(this.model.getChildAt(cell,i),true);
}
}
}
finally
{
this.model.endUpdate();
}
}
}
mxGraph.prototype.cellRemoved=function(cell,isClearSelection)
{
if(cell!=null)
{
if(isClearSelection&&this.isCellSelected(cell)){
var index=mxUtils.indexOf(this.cells,cell);
this.getSelectionCells().splice(index,1);
}
this.view.removeState(cell);
var childCount=this.model.getChildCount(cell);
for(var i=0;i<childCount;i++)
{
this.cellRemoved(this.model.getChildAt(cell,i),isClearSelection);
}
}
}

mxGraph.prototype.resize=function(cell,bounds)
{
var g=this.model.getGeometry(cell);
if(g.x!=bounds.x||g.y!=bounds.y||g.width||bounds.width||g.height!=bounds.height)
{
g=g.clone();
g.x=bounds.x;
g.y=bounds.y;
g.width=bounds.width;
g.height=bounds.height;
this.model.beginUpdate();
this.model.setGeometry(cell,g);
if(this.isResetEdgesOnResize)
{
this.resetEdges([cell]);
}
this.extendParent(cell);
if(!this.layout(this.model.getParent(cell)))
{
this.cascadeResize(cell);
}
this.dispatchEvent('resize',this,cell,bounds);
this.model.endUpdate();
}
}
mxGraph.prototype.cascadeResize=function(cell)
{
var state=this.view.getState(cell);
var pstate=this.view.getState(this.model.getParent(cell));
if(state!=null&&pstate!=null)
{
var cells=this.getCellsToShift(state);
if(cells!=null)
{
var scale=this.view.scale;
var x0=state.x-pstate.origin.x-this.view.translate.x*scale;
var y0=state.y-pstate.origin.y-this.view.translate.y*scale;
var right=state.x+state.width;
var bottom=state.y+state.height;
var geo=this.model.getGeometry(cell);
var dx=state.width-geo.width*scale+x0-geo.x*scale;
var dy=state.height-geo.height*scale+y0-geo.y*scale;
var fx=1-geo.width*scale/state.width;
var fy=1-geo.height*scale/state.height;
this.model.beginUpdate();
try
{
for(var i=0;i<cells.length;i++)
{
var g=this.model.getGeometry(cells[i]);
state=this.view.getState(cells[i]);
if(state!=null&&cells[i]!=cell&&this.isShiftable(cells[i]))
{
if(this.isShiftRightwards)
{
if(state.x>=right)
{
g=g.translate(-dx,0);
}
else
{
var tmpDx=Math.max(0,state.x-x0);
g=g.translate(-fx*tmpDx,0);
}
}
if(this.isShiftDownwards)
{
if(state.y>=bottom)
{
g=g.translate(0,-dy);
}
else
{
var tmpDy=Math.max(0,state.y-y0);
g=g.translate(0,-fy*tmpDy);
}
if(g!=this.model.getGeometry(cells[i]))
{
this.model.setGeometry(cells[i],g);
this.extendParent(cells[i]);
}
}
}
}
}
finally
{
this.model.endUpdate();
}
}
}
}
mxGraph.prototype.extendParent=function(cell)
{
var parent=this.model.getParent(cell);
var p=this.model.getGeometry(parent);
if(this.isExtendParentOnResize&&parent!=null&&p!=null&&!this.model.isCollapsed(parent))
{
var g=this.model.getGeometry(cell);
if(g!=null&&(p.width<g.x+g.width||p.height<g.y+g.height))
{
p=p.clone();
p.width=Math.max(p.width,g.x+g.width);
p.height=Math.max(p.height,g.y+g.height);
this.resize(parent,p);
}
}
}
mxGraph.prototype.getCellsToShift=function(state)
{
return this.getCellsBeyond(state.x+((this.isShiftDownwards)?0:state.width),state.y+((this.isShiftDownwards&&this.isShiftRightwards)?0:state.height),this.model.getParent(state.cell),this.isShiftRightwards,this.isShiftDownwards);
}

mxGraph.prototype.move=function(cells,dx,dy,clone,target,evt)
{
var clones=cells;
if(dx!=0||dy!=0||clone||target!=null)
{
this.model.beginUpdate();
try
{
if(clone)
{



clones=this.cloneCells(cells);
for(var i=0;i<clones.length;i++)
{
var parent=this.model.getParent(cells[i]);
this.model.add(parent,clones[i]);
var pstate=this.view.getState(parent);
var geo=this.model.getGeometry(clones[i]);
if(pstate!=null&&geo!=null)
{
clones[i].setGeometry(geo.translate(-pstate.origin.x,-pstate.origin.y));
}
}
}
else
{
if(evt!=null&&target==null)
{
var point=mxUtils.convertPoint(this.container,evt.clientX,evt.clientY);
for(var i=0;i<cells.length;i++)
{
var layout=this.getLayout(this.model.getParent(cells[i]));
if(layout!=null&&layout.move!=null)
{
layout.move(cells[i],point.x,point.y);
}
}
}
this.disconnect(cells);
}
for(var i=0;i<cells.length;i++)
{
var g=this.model.getGeometry(clones[i]);
if(g!=null&&this.isMovable(clones[i]))
{
g=g.translate(dx,dy);
this.model.setGeometry(clones[i],g);
}
}
this.moveInto(clones,target);


this.keepInside(clones);
}
finally
{
this.model.endUpdate();
}
}
return clones;
}
mxGraph.prototype.resetEdges=function(cells)
{
this.model.beginUpdate();
try
{
for(var i=0;i<cells.length;i++)
{
var edges=this.model.getEdges(cells[i]);
if(edges!=null)
{
for(var j=0;j<edges.length;j++)
{
var geo=this.model.getGeometry(edges[j]);
if(geo!=null&&geo.points!=null&&geo.points.length>0)
{
geo=geo.clone();
geo.points=new Array();
this.model.setGeometry(edges[j],geo);
}
}
}
}
}
finally
{
this.model.endUpdate();
}
}
mxGraph.prototype.keepInside=function(cells)
{
this.model.beginUpdate();
try
{
for(var i=0;i<cells.length;i++)
{
var cell=cells[i];
if(this.isKeepInsideParentOnMove(cell))
{
var c=this.getContentArea(cell);
if(c!=null)
{
var g=this.model.getGeometry(cell);
if(c!=null&&(g.x<c.x||g.y<c.y||c.width<g.x+g.width||c.height<g.y+g.height))
{
if(this.isAllowOverlapParent(cell))
{
g=g.translate(Math.min(0,c.width-g.x-g.width/2),Math.min(0,c.height-g.y-g.height/2));
}
else
{
g=g.translate(Math.min(0,c.width-g.x-g.width),Math.min(0,c.height-g.y-g.height));
}
g.x=Math.max(g.x,c.x);
g.y=Math.max(g.y,c.y);
this.model.setGeometry(cell,g);
}
}
}
}
}
finally
{
this.model.endUpdate();
}
}
mxGraph.prototype.getContentArea=function(cell)
{
var parent=this.model.getParent(cell);
if(cell!=null&&!this.model.isEdge(cell)&&parent!=null&&parent!=this.getDefaultParent())
{
var g=this.model.getGeometry(parent);
if(g!=null)
{
var x=0;
var y=0;
if(this.isSwimlane(parent))
{
var pstate=this.view.getState(parent);
var offset=pstate.style[mxConstants.STYLE_STARTSIZE]||40;
if(pstate.style[mxConstants.STYLE_HORIZONTAL]=="true")
{
x=offset;
}
else
{
y=offset;
}
}
return new mxRectangle(x,y,g.width,g.height);
}
}
return null;
}
mxGraph.prototype.moveInto=function(cells,target)
{
if(cells!=null&&cells.length>0)
{
if(target!=null)
{
var cell=cells[0];
if(this.model.isEdge(target)&&this.model.isConnectable(cell))
{
if(this.getEdgeValidationError(target,this.model.getTerminal(target,true),cell)==null)
{
this.splitEdge(target,cell);
}
}
else
{
this.model.beginUpdate();
try
{
for(var i=0;i<cells.length;i++)
{
var parent=this.model.getParent(cells[i]);
if(target!=parent)
{
var state=this.view.getState(target);
var pstate=this.view.getState(parent);
var g=this.model.getGeometry(cells[i]);
if(g!=null&&state!=null&&pstate!=null)
{
g=g.translate(pstate.origin.x-state.origin.x,pstate.origin.y-state.origin.y);
this.model.setGeometry(cells[i],g);
}
var index=this.model.getChildCount(target);
this.model.add(target,cells[i],index);
}
}
}
finally
{
this.model.endUpdate();
}
}
}
if(this.isResetEdgesOnMove)
{
this.resetEdges(cells);
}
var parent=this.model.getParent(cells[0]);
if(target!=null)
{
this.layout(target);
}
else if(parent!=null)
{
this.layout(parent);
}
this.dispatchEvent('move',this,cells);
}
}

mxGraph.prototype.connect=function(edge,terminal,isSource)
{
this.model.beginUpdate();
try
{
this.model.setTerminal(edge,terminal,isSource);
var geo=this.model.getGeometry(edge);
if(geo!=null)
{
geo=geo.clone();
if(geo.points!=null)
{
geo.points=new Array();
this.model.setGeometry(edge,geo);
}
}
var parent=this.model.getParent(terminal);
if(terminal!=null&&parent!=null)
{
this.layout(parent);
}
this.dispatchEvent('connect',this,edge,terminal,isSource);
}
finally
{
this.model.endUpdate();
}
}
mxGraph.prototype.disconnect=function(cells)
{
this.model.beginUpdate();
try
{
for(var i=0;i<cells.length;i++)
{
var g=this.model.getGeometry(cells[i]);
if(g!=null)
{
if(this.model.isEdge(cells[i]))
{
g=g.clone();
var state=this.view.getState(cells[i]);
var pstate=this.view.getState(this.model.getParent(cells[i]));
if(state!=null&&pstate!=null)
{
var dx=-pstate.origin.x;
var dy=-pstate.origin.y;
var pts=state.absolutePoints;
var src=this.model.getTerminal(cells[i],true);
while(src!=null&&mxUtils.indexOf(cells,src)<0)
{
src=this.model.getParent(src);
}
if(src==null)
{
g.setTerminalPoint(new mxPoint(pts[0].x+dx,pts[0].y+dy),true);
this.model.setTerminal(cells[i],null,true);
}
var trg=this.model.getTerminal(cells[i],false);
while(trg!=null&&mxUtils.indexOf(cells,trg)<0)
{
trg=this.model.getParent(trg);
}
if(trg==null)
{
var n=pts.length-1;
g.setTerminalPoint(new mxPoint(pts[n].x+dx,pts[n].y+dy),false);
this.model.setTerminal(cells[i],null,false);
}
}
this.model.setGeometry(cells[i],g);
}
}
}
}
finally
{
this.model.endUpdate();
}
}

mxGraph.prototype.getCellBounds=function(cell)
{
return this.view.getState(cell);
}
mxGraph.prototype.refresh=function(cell)
{
this.view.clear(cell,cell==null);
this.view.validate();
}
mxGraph.prototype.snap=function(value)
{
if(this.isGridEnabled)
{
return Math.round(value/this.gridSize)*this.gridSize;
}
return v;
}
mxGraph.prototype.shift=function(dx,dy)
{
var canvas=this.view.getCanvas();
if(this.dialect==mxConstants.DIALECT_SVG)
{
canvas.setAttribute('transform','translate('+dx+','+dy+')');
}
else if(this.dialect==mxConstants.DIALECT_VML)
{
canvas.setAttribute('coordorigin',(-dx)+','+(-dy));
}
else
{
if(dx==0&&dy==0)
{
if(this.shiftPreview!=null)
{
this.shiftPreview.parentNode.removeChild(this.shiftPreview);
this.shiftPreview=null;
canvas.style.display='inline';
}
}
else
{
if(this.shiftPreview==null)
{
this.shiftPreview=this.view.getDrawPane().cloneNode(true);
this.shiftPreview.style.position='absolute';
var pt=mxUtils.getOffset(this.container);
this.shiftPreview.style.left=pt.x+'px';
this.shiftPreview.style.top=pt.y+'px';
canvas.style.display='none';
this.container.appendChild(this.shiftPreview);
}
this.shiftPreview.style.left=dx+'px';
this.shiftPreview.style.top=dy+'px';
}
}
}
mxGraph.prototype.zoomIn=function()
{
var scale=this.view.scale*this.zoomFactor;
var w=this.container.offsetWidth+30;
var h=this.container.offsetHeight+30;
this.view.scale=scale;
var state=this.view.getState(this.getSelectionCell());
if(this.isKeepSelectionVisibleOnZoom&&state!=null)
{
var rect=new mxRectangle(state.x*this.zoomFactor,state.y*this.zoomFactor,state.width*this.zoomFactor,state.height*this.zoomFactor);

if(!this.scrollRectToVisible(rect))
{
this.view.setScale(scale);
}
}
else if(this.container.style.overflow!='auto'&&this.isCenterZoom)
{
this.view.translate.x-=w*(this.zoomFactor-1)/2;
this.view.translate.y-=h*(this.zoomFactor-1)/2;
this.view.setScale(scale);
}
else
{
this.view.setScale(scale);
}
}
mxGraph.prototype.zoomOut=function()
{
var w=this.container.offsetWidth+30;
var h=this.container.offsetHeight+30;
var f=1/this.zoomFactor;
var scale=this.view.scale/this.zoomFactor;
this.view.scale=scale;
var state=this.view.getState(this.getSelectionCell());
if(this.isKeepSelectionVisibleOnZoom&&state!=null)
{
var state=this.view.getState(this.getSelectionCell());
var rect=new mxRectangle(state.x/this.zoomFactor,state.y/this.zoomFactor,state.width/this.zoomFactor,state.height/this.zoomFactor);

if(!this.scrollRectToVisible(rect))
{
this.view.setScale(scale);
}
}
else if(this.container.style.overflow!='auto'&&this.isCenterZoom)
{
this.view.translate.x-=w*(f-1)/2;
this.view.translate.y-=h*(f-1)/2;
this.view.setScale(scale);
}
else
{
this.view.setScale(scale);
}
}
mxGraph.prototype.zoomActual=function()
{
this.view.translate.x=0;
this.view.translate.y=0;
this.view.setScale(1);
}
mxGraph.prototype.fit=function()
{
var border=10;
var w1=this.container.offsetWidth-30-2*border;
var h1=this.container.offsetHeight-30-2*border;
var bounds=this.view.bounds;
var w2=bounds.width/this.view.scale;
var h2=bounds.height/this.view.scale;
var s=Math.min(w1/w2,h1/h2);
if(s>0.1&&s<8)
{
this.view.translate.x=(bounds.x!=null)?this.view.translate.x-bounds.x/this.view.scale+border:border;
this.view.translate.y=(bounds.y!=null)?this.view.translate.y-bounds.y/this.view.scale+border:border;
this.view.setScale(s);
}
}
mxGraph.prototype.scrollCellToVisible=function(cell)
{
var x=-this.view.translate.x;
var y=-this.view.translate.y;
var state=this.view.getState(cell);
var bounds=new mxRectangle(x+state.x,y+state.y,state.width,state.height);
if(this.scrollRectToVisible(bounds))
{
this.view.setTranslate(this.view.translate.x,this.view.translate.y);
}
}
mxGraph.prototype.scrollRectToVisible=function(rect)
{
if(rect!=null)
{
var isChanged=false;
if(this.container.style.overflow=='auto')
{
}
else
{
var x=-this.view.translate.x;
var y=-this.view.translate.y;
var w=this.container.offsetWidth;
var h=this.container.offsetHeight;
var scale=this.view.scale;
if(rect.x+rect.width>x+w)
{
this.view.translate.x-=(rect.x+rect.width-w-x)/scale;
isChanged=true;
}
if(rect.y+rect.height>y+h)
{
this.view.translate.y-=(rect.y+rect.height-h-y)/scale;
isChanged=true;
}
if(rect.x<x)
{
this.view.translate.x+=(x-rect.x)/scale;
isChanged=true;
}
if(rect.y<y)
{
this.view.translate.y+=(y-rect.y)/scale;
isChanged=true;
}
if(isChanged)
{
this.view.refresh();
}
}
}
return isChanged;
}

mxGraph.prototype.collapse=function(cells)
{
if(cells==null)
{
cells=this.getSelectionCells();
}
this.editor.stopEditing(false);
this.model.beginUpdate();
try
{
for(var i=0;i<cells.length;i++)
{
if(this.isCollapsable(cells[i])&&!this.model.isCollapsed(cells[i]))
{
this.model.setCollapsed(cells[i],true);
this.swapBounds(cells[i],true);
this.extendParent(cells[i]);
this.cascadeResize(cells[i]);
}
}
var parent=this.model.getParent(cells[0]);
if(parent!=null)
{
this.layout(parent);
}
this.dispatchEvent('collapse',this,cells);
}
finally
{
this.model.endUpdate();
}
}
mxGraph.prototype.expand=function(cells)
{
if(cells==null)
{
cells=this.getSelectionCells();
}
this.editor.stopEditing(false);
this.model.beginUpdate();
try
{
for(var i=0;i<cells.length;i++)
{
if(this.isExpandable(cells[i])&&this.model.isCollapsed(cells[i]))
{
this.model.setCollapsed(cells[i],false);
this.swapBounds(cells[i],false);
this.extendParent(cells[i]);
this.cascadeResize(cells[i]);
}
}
var parent=this.model.getParent(cells[0]);
if(parent!=null)
{
this.layout(parent);
}
this.dispatchEvent('expand',this,cells);
}
finally
{
this.model.endUpdate();
}
}
mxGraph.prototype.swapBounds=function(cell,willCollapse)
{
var g=this.model.getGeometry(cell);
if(g!=null)
{
g=g.clone();
this.updateAlternateBounds(cell,g,willCollapse);
g.swap();
this.model.setGeometry(cell,g);
}
}
mxGraph.prototype.updateAlternateBounds=function(cell,g,willCollapse)
{
if(g.alternateBounds==null)
{
var bounds=(this.isCollapseToPreferredSize)?this.getPreferredSizeForCell(cell):g;
g.alternateBounds=new mxRectangle(g.x,g.y,bounds.width,bounds.height);
}
else
{
g.alternateBounds.x=g.x;
g.alternateBounds.y=g.y;
}
}

mxGraph.prototype.getCurrentRoot=function(cell)
{
return this.view.currentRoot;
}
mxGraph.prototype.goInto=function(cell)
{
if(cell==null)
{
cell=this.getSelectionCell();
}
if(cell!=null&&this.isValidRoot(cell))
{
this.view.setCurrentRoot(cell);
this.selection.clear();
}
}
mxGraph.prototype.goUp=function()
{
var root=this.model.getRoot();
var current=this.getCurrentRoot();
if(current!=null)
{
var next=this.model.getParent(current);
while(next!=root&&!this.isValidRoot(next)&&this.model.getParent(next)!=root)
{
next=this.model.getParent(next);
}
if(next==root||this.model.getParent(next)==root)
{
this.view.setCurrentRoot(null);
}
else
{
this.view.setCurrentRoot(next);
}
var state=this.view.getState(current);
if(state!=null)
{
this.setSelectionCell(current);
}
}
}
mxGraph.prototype.home=function()
{
var current=this.getCurrentRoot();
if(current!=null)
{
this.view.setCurrentRoot(null);
var state=this.view.getState(current);
if(state!=null)
{
this.setSelectionCell(current);
}
}
}

mxGraph.prototype.group=function(group,border)
{
var tmp=this.getSelectionCells();
if(tmp.length>1)
{
var parent=this.model.getParent(tmp[0]);
var cells=new Array();
cells.push(tmp[0]);
for(var i=1;i<tmp.length;i++)
{
if(this.model.getParent(tmp[i])==parent)
{
cells.push(tmp[i]);
}
}
if(cells.length>1)
{
if(group==null)
{
group=new mxCell("");
group.vertex=true;
}
group=this.addGroup(group,cells,border||0);
if(group!=null){
this.setSelectionCell(group);
}
}
}
}
mxGraph.prototype.ungroup=function(cells)
{
cells=cells||this.getSelectionCells();
this.selection.clear();
this.model.beginUpdate();
try
{
for(var i=0;i<cells.length;i++)
{
var cell=cells[i];
var childCount=this.model.getChildCount(cell);
if(childCount>0)
{
var children=new Array();
for(var j=0;j<childCount;j++)
{
children.push(this.model.getChildAt(cell,j));
}
this.moveInto(children,this.model.getParent(cell));
this.selection.addCells(children);
this.remove(cells.slice(i,i+1));
}
}
}
finally
{
this.model.endUpdate();
}
}
mxGraph.prototype.addGroup=function(group,cells,border)
{
var parent=this.model.getParent(cells[0]);
var pstate=this.view.getState(parent);
var bounds=this.view.getBounds(cells);
if(bounds!=null){
var scale=this.view.scale;
var translate=this.view.translate;
var x=bounds.x-pstate.origin.x*scale;
var y=bounds.y-pstate.origin.y*scale;
var width=bounds.width;
var height=bounds.height;
var geo=new mxGeometry(x/scale-border-translate.x,y/scale-border-translate.y,width/scale+2*border,height/scale+2*border);
group.setGeometry(geo);
this.groupCells(parent,group,cells,-geo.x,-geo.y);
return group;
}
return null;
}
mxGraph.prototype.groupCells=function(parent,group,cells,dx,dy)
{
var index=this.model.getChildCount(parent);
this.model.beginUpdate();
try
{
parent=this.model.add(parent,group,index);
for(var i=0;i<cells.length;i++)
{
index=this.model.getChildCount(parent);
this.model.add(group,cells[i],index);
var geometry=this.model.getGeometry(cells[i]);
if(geometry!=null)
{
geometry=geometry.translate(dx,dy);
this.model.setGeometry(cells[i],geometry);
}
}
}
finally
{
this.model.endUpdate();
}
}

mxGraph.prototype.getLayout=function(cell)
{
return null;
}
mxGraph.prototype.isAutoLayout=function(cell)
{
return this.autoLayout;
}
mxGraph.prototype.layout=function(cell)
{
if(cell!=null&&this.isAutoLayout(cell))
{
var layout=this.getLayout(cell);
if(layout!=null)
{
layout.execute(cell);
this.dispatchEvent('layout',this,cell);
return true;
}
}
return false;
}

mxGraph.prototype.getEdgeValidationError=function(edge,source,target)
{
if(edge!=null&&this.model.getTerminal(edge,true)==null&&this.model.getTerminal(edge,false)==null)
{
return null;
}
if(!this.isAllowLoops&&source==target)
{
return '';
}
if(source!=null&&target!=null&&this.model.getValue(source)!=null&&this.model.getValue(target)!=null)
{
var error='';
if(!this.isMultigraph)
{
var tmp=this.model.getEdge(source,target);
if(tmp!=null&&edge!=tmp)
{
error+=(mxResources.get('alreadyConnected')||this.alreadyConnected)+'\n';
}
}


var sourceOut=source.getDirectedEdgeCount(true,edge);
var targetIn=target.getDirectedEdgeCount(false,edge);
for(var i=0;i<this.multiplicities.length;i++)
{
var rule=this.multiplicities[i];
if((rule.isSource&&source.is(rule.type,rule.attr,rule.value))||(!rule.isSource&&target.is(rule.type,rule.attr,rule.value)))
{
if(rule.isSource&&(rule.max==0||(rule.max==1&&sourceOut>0)))
{
error+=rule.countError+'\n';
}
else if(!rule.isSource&&(rule.max==0||(rule.max==1&&targetIn>0)))
{
error+=rule.countError+'\n';
}
var valid=rule.validNeighbors;
var isValid=false;
if(valid!=null&&valid.length>0)
{
for(var j=0;j<valid.length;j++)
{
if(rule.isSource&&target.is(valid[j]))
{
isValid=true;
break;
}
else if(!rule.isSource&&source.is(valid[j]))
{
isValid=true;
break;
}
}
if(!isValid)
{
error+=rule.typeError+'\n';
}
}
var err=this.validateEdge(edge,source,target);
if(err!=null)
{
error+=err;
}
}
}
return(error.length>0)?error:null;
}
return(this.isAllowDanglingEdges)?null:'';
}
mxGraph.prototype.validateEdge=function(edge,source,target)
{
return null;
}
mxGraph.prototype.validate=function(cell,context)
{
cell=(cell!=null)?cell:this.model.getRoot();
context=(context!=null)?context:new Object();
var isValid=true;
var childCount=this.model.getChildCount(cell);
for(var i=0;i<childCount;i++)
{
var tmp=this.model.getChildAt(cell,i);
var ctx=context;
if(this.isValidRoot(tmp))
{
ctx=new Object();
}
var warn=this.validate(tmp,ctx);
if(warn!=null)
{
var html=warn.replace(/\n/g,'<br>');
var len=html.length;
this.setWarning(tmp,html.substring(0,Math.max(0,len-4)));
}
else
{
this.setWarning(tmp,null);
}
isValid=isValid&&warn==null;
}
var warning='';

if(this.model.isCollapsed(cell)&&!isValid)
{
warning+=(mxResources.get('containsValidationErrors')||this.containsValidationErrors)+'\n';
}

if(this.model.isEdge(cell))
{
warning+=this.getEdgeValidationError(cell,this.model.getTerminal(cell,true),cell.getTerminal(false))||'';
}
else
{
warning+=this.getCellValidationError(cell)||'';
}
var err=this.validateCell(cell,context);
if(err!=null)
{
warning+=err;
}



if(this.model.getParent(cell)==null)
{
this.view.validate();
}
return(warning.length>0||!isValid)?warning:null;
}
mxGraph.prototype.getCellValidationError=function(cell)
{
var error='';
var outCount=cell.getDirectedEdgeCount(true);
var inCount=cell.getDirectedEdgeCount(false);
for(var i=0;i<this.multiplicities.length;i++)
{
var rule=this.multiplicities[i];
if(rule.isSource&&cell.is(rule.type,rule.attr,rule.value)&&((rule.max==0&&outCount>0)||(rule.min==1&&outCount==0)||(rule.max==1&&outCount>1)))
{
error+=rule.countError+'\n';
}
else if(!rule.isSource&&cell.is(rule.type,rule.attr,rule.value)&&((rule.max==0&&inCount>0)||(rule.min==1&&inCount==0)||(rule.max==1&&inCount>1)))
{
error+=rule.countError+'\n';
}
}
return(error.length>0)?error:null;
}
mxGraph.prototype.validateCell=function(cell,context)
{
return null;
}

mxGraph.prototype.getBackgroundImage=function()
{
return this.backgroundImage;
}
mxGraph.prototype.setBackgroundImage=function(image)
{
this.backgroundImage=image;
}
mxGraph.prototype.getBackgroundImageWidth=function()
{
return this.backgroundImageWidth;
}
mxGraph.prototype.getBackgroundImageHeight=function()
{
return this.backgroundImageHeight;
}
mxGraph.prototype.getExpanderImage=function(state)
{
var tmp=this.model.isCollapsed(state.cell);
if((tmp&&this.isExpandable(state.cell))||(!tmp&&this.isCollapsable(state.cell)))
{
return(tmp)?this.collapsedImage:this.expandedImage;
}
return null;
}
mxGraph.prototype.convertValueToString=function(cell)
{
var value=this.model.getValue(cell);
return(value!=null)?value.toString():'';
}
mxGraph.prototype.getLabel=function(cell)
{
return this.isLabelsVisible?this.convertValueToString(cell):'';
}
mxGraph.prototype.getTooltip=function(cell,index)
{
var tip=null;
if(index!=null)
{
tip=index.toString();
}
else
{
tip=this.getTooltipForCell(cell);
}
return tip;
}
mxGraph.prototype.getTooltipForCell=function(cell)
{
var tip=null;
if(cell.getTooltip!=null)
{
tip=cell.getTooltip();
}
else
{
tip=this.convertValueToString(cell);
}
return tip;
}
mxGraph.prototype.getCellStyle=function(cell)
{
var stylename=this.model.getStyle(cell);
var style=null;
if(this.model.isEdge(cell))
{
style=this.stylesheet.getDefaultEdgeStyle();
}
else
{
style=this.stylesheet.getDefaultVertexStyle();
}
if(stylename!=null)
{
style=this.stylesheet.getCellStyle(stylename,style);
}
if(style==null)
{
style=mxGraph.prototype.EMPTY_ARRAY;
}
return style;
}
mxGraph.prototype.getImage=function(state)
{
return(state!=null&&state.style!=null)?state.style[mxConstants.STYLE_IMAGE]:null;
}
mxGraph.prototype.getVerticalAlign=function(state)
{
return(state!=null&&state.style!=null)?state.style[mxConstants.STYLE_VERTICAL_ALIGN]:null;
}
mxGraph.prototype.getIndicatorColor=function(state)
{
var color=(state!=null&&state.style!=null)?state.style[mxConstants.STYLE_INDICATOR_COLOR]:null;

if(color=='swimlane')
{
var swimlane=null;
if(this.model.getTerminal(state.cell,false)!=null)
{
swimlane=this.model.getTerminal(state.cell,false);
}
else
{
swimlane=state.cell;
}
swimlane=this.getSwimlane(swimlane);
if(swimlane!=null)
{
var swimlaneState=this.view.getState(swimlane);
var style=(swimlaneState!=null)?swimlaneState.style:this.getCellStyle(swimlane);
if(style!=null)
{
color=style[mxEditor.prototype.cycleAttributeName];
}
}
else
{
color=null;
}
}
return color;
}
mxGraph.prototype.getIndicatorGradientColor=function(state)
{
return(state!=null&&state.style!=null)?state.style[mxConstants.STYLE_INDICATOR_GRADIENTCOLOR]:null;
}
mxGraph.prototype.getIndicatorShape=function(state)
{
return(state!=null&&state.style!=null)?state.style[mxConstants.STYLE_INDICATOR_SHAPE]:null;
}
mxGraph.prototype.getIndicatorImage=function(state)
{
return(state!=null&&state.style!=null)?state.style[mxConstants.STYLE_INDICATOR_IMAGE]:null;
}

mxGraph.prototype.isEnabled=function()
{
return this.enabled;
}
mxGraph.prototype.setEnabled=function(enabled)
{
this.enabled=enabled;
}
mxGraph.prototype.isCloneable=function()
{
return true;
}
mxGraph.prototype.isSwimlane=function(cell)
{
if(cell!=null)
{
var state=this.view.getState(cell);
var style=(state!=null)?state.style:this.getCellStyle(cell);
if(style!=null&&!this.model.isEdge(cell))
{
return(style[mxConstants.STYLE_SHAPE]==mxConstants.SHAPE_SWIMLANE);
}
}
return false;
}
mxGraph.prototype.isValidRoot=function(cell)
{
return cell!=null;
}
mxGraph.prototype.isMovable=function(cell)
{
return this.movable;
}
mxGraph.prototype.setMovable=function(movable)
{
this.movable=movable;
}
mxGraph.prototype.isSizable=function(cell)
{
return this.sizable;
}
mxGraph.prototype.setSizable=function(sizable)
{
this.sizable=sizable;
}
mxGraph.prototype.isEditable=function(cell)
{
return this.editable;
}
mxGraph.prototype.setEditable=function(editable)
{
this.editable=editable;
}
mxGraph.prototype.setConnectable=function(connectable)
{
this.connectionHandler.isEnabled=connectable;
}
mxGraph.prototype.setTooltips=function(enabled)
{
this.tooltipHandler.isEnabled=enabled;
}
mxGraph.prototype.setPanning=function(enabled)
{
this.panningHandler.isPanEnabled=enabled;
}
mxGraph.prototype.setUpdateSize=function(updateSize)
{
this.autoSize=updateSize;
}
mxGraph.prototype.isEditing=function(cell)
{
return this.editor!=null&&this.editor.isEditing(cell);
}
mxGraph.prototype.isUpdateSize=function(cell)
{
return this.autoSize;
}
mxGraph.prototype.isKeepInsideParentOnMove=function(cell)
{
return true;
}
mxGraph.prototype.isAllowOverlapParent=function(cell)
{
return false;
}
mxGraph.prototype.isShiftable=function(cell)
{
return!this.model.isEdge(cell);
}
mxGraph.prototype.isExpandable=function(cell)
{
return this.model.getChildCount(cell)>0;
}
mxGraph.prototype.isCollapsable=function(cell)
{
return this.isExpandable(cell);
}
mxGraph.prototype.isValidDropTarget=function(cell,cells,evt)
{
if(this.model.isEdge(cell))
{
return cells!=null&&cells.length==1&&this.isSplitDropTarget(cell,cells[0],evt);
}
else
{
return this.isParentDropTarget(cell,cells,evt);
}
}
mxGraph.prototype.isSplitDropTarget=function(edge,cell,evt)
{
if(edge!=null)
{
var src=this.model.getTerminal(edge,true);
var trg=this.model.getTerminal(edge,false);
return(!this.model.isAncestor(cell,src)&&!this.model.isAncestor(cell,trg));
}
return false;
}
mxGraph.prototype.isParentDropTarget=function(parent,cells,evt)
{
if(parent!=null)
{
return this.isSwimlane(parent)||(this.model.getChildCount(parent)&&!this.model.isCollapsed(parent));
}
return false;
}

mxGraph.prototype.getDefaultParent=function()
{
var parent=this.defaultParent;
if(parent==null)
{
parent=this.getCurrentRoot();
if(parent==null)
{
var root=this.model.getRoot();
parent=this.model.getChildAt(root,0);
}
}
return parent;
}
mxGraph.prototype.getDropTarget=function(cells,evt,cell)
{
if(!this.isSwimlaneNesting)
{
for(var i=0;i<cells.length;i++)
{
if(this.isSwimlane(cells[i]))
{
return null;
}
}
}
if(cell==null)
{
var pt=mxUtils.convertPoint(this.container,evt.clientX,evt.clientY);
cell=this.getSwimlaneAt(pt.x,pt.y);
}
while(cell!=null&&!this.isValidDropTarget(cell,cells,evt)&&!this.model.isLayer(cell))
{
cell=this.model.getParent(cell);
}
return(!this.model.isLayer(cell))?cell:null;
}
mxGraph.prototype.getSwimlane=function(cell)
{
while(cell!=null&&!this.isSwimlane(cell))
{
cell=this.model.getParent(cell);
}
return cell;
}
mxGraph.prototype.getSwimlaneAt=function(x,y,parent)
{
parent=parent||this.getDefaultParent();
if(parent!=null)
{
var childCount=this.model.getChildCount(parent);
for(var i=0;i<childCount;i++)
{
var child=this.model.getChildAt(parent,i);
var result=this.getSwimlaneAt(x,y,child);
if(result!=null)
{
return result;
}
else if(this.isSwimlane(child))
{
var state=this.view.getState(child);
if(state!=null)
{
if(mxUtils.contains(state,x,y))
{
return child;
}
}
}
}
}
return null;
}
mxGraph.prototype.getCellAt=function(x,y,parent)
{
parent=parent||this.getDefaultParent();
if(parent!=null)
{
var childCount=this.model.getChildCount(parent);
for(var i=0;i<childCount;i++)
{
var cell=this.model.getChildAt(parent,i);
var result=this.getCellAt(x,y,cell);
if(result==null)
{
var state=this.view.getState(cell);
if(this.model.isVisible(cell)&&state!=null&&state.x<=x&&state.y<=y&&state.x+state.width>=x&&state.y+state.height>=y)
{
result=cell;
}
}
if(result!=null)
{
return result;
}
}
}
return null;
}
mxGraph.prototype.getCells=function(x,y,width,height,parent,result)
{
var result=result||new Array();
if(width>0||height>0)
{
var right=x+width;
var bottom=y+height;
parent=parent||this.getDefaultParent();
if(parent!=null)
{
var childCount=this.model.getChildCount(parent);
for(var i=0;i<childCount;i++)
{
var cell=this.model.getChildAt(parent,i);
var state=this.view.getState(cell);
if(this.model.isVisible(cell)&&state!=null)
{
if(state.x>=x&&state.y>=y&&state.x+state.width<=right&&state.y+state.height<=bottom)
{
result.push(cell);
}
else
{
this.getCells(x,y,width,height,cell,result);
}
}
}
}
}
return result;
}
mxGraph.prototype.getCellsBeyond=function(x0,y0,parent,rightHalfpane,bottomHalfpane)
{
var result=new Array();
if(rightHalfpane||bottomHalfpane)
{
if(parent==null)
{
parent=this.getDefaultParent();
}
if(parent!=null)
{
var childCount=this.model.getChildCount(parent);
for(var i=0;i<childCount;i++)
{
var child=this.model.getChildAt(parent,i);
var state=this.view.getState(child);
if(this.model.isVisible(child)&&state!=null)
{
if((!rightHalfpane||state.x>=x0)&&(!bottomHalfpane||state.y>=y0))
{
result.push(child);
}
}
}
}
}
return result;
}
mxGraph.prototype.findTreeRoots=function(parent)
{
var roots=new Array();
if(parent!=null)
{
var childCount=this.model.getChildCount(parent);
var maxDiff=0;
var root=0;
for(var i=0;i<childCount;i++)
{
var cell=this.model.getChildAt(parent,i);
var fanIn=cell.getDirectedEdgeCount(false);
var fanOut=cell.getDirectedEdgeCount(true);
if(fanIn==0)
{
if(fanOut>0)
{
roots.push(cell);
}
}
var diff=fanOut-fanIn;
if(diff>=maxDiff)
{
root=cell;
maxDiff=diff;
}
}
if(root.length==0&&root!=null)
{
roots.push(root);
}
}
return roots;
}

mxGraph.prototype.isCellSelected=function(cell)
{
return this.selection.isSelected(cell);
}
mxGraph.prototype.isSelectionEmpty=function()
{
return this.selection.cells.length==0;
}
mxGraph.prototype.getSelectionCount=function()
{
return this.selection.cells.length;
}
mxGraph.prototype.getSelectionCell=function()
{
return this.selection.cells[0];
}
mxGraph.prototype.getSelectionCells=function()
{
return this.selection.cells;
}
mxGraph.prototype.setSelectionCell=function(cell)
{
this.selection.setCell(cell);
}
mxGraph.prototype.setSelectionCells=function(cells)
{
this.selection.setCells(cells);
}
mxGraph.prototype.selectRegion=function(rect,evt)
{
var cells=this.getCells(rect.x,rect.y,rect.width,rect.height);
this.selectCellsForEvent(cells,evt);
}
mxGraph.prototype.select=function(isNext,isParent,isChild)
{
var sel=this.selection;
var cell=(sel.cells.length>0)?sel.cells[0]:null;
if(sel.cells.length>1)
{
sel.clear();
}
var parent=(cell!=null)?this.model.getParent(cell):
this.getDefaultParent();
var childCount=this.model.getChildCount(parent);
if(cell==null&&childCount>0)
{
this.setSelectionCell(this.model.getChildAt(parent,0));
}
else if((cell==null||isParent)&&this.view.getState(parent)!=null&&parent.getGeometry()!=null)
{
if(this.view.currentRoot!=parent){
this.setSelectionCell(parent);
}
}
else if(cell!=null&&isChild)
{
var tmp=this.model.getChildCount(cell);
if(tmp>0)
{
this.setSelectionCell(this.model.getChildAt(cell,0));
}
}
else if(childCount>0)
{
var i=parent.getIndex(cell);
if(isNext)
{
i++;
this.setSelectionCell(this.model.getChildAt(parent,i%childCount));
}
else
{
i--;
this.setSelectionCell(this.model.getChildAt(parent,(i<0)?childCount-1:i));
}
}
}
mxGraph.prototype.selectAll=function(parent)
{
parent=parent||this.getDefaultParent();
var children=this.model.getChildren(parent);
if(children!=null)
{
this.setSelectionCells(children);
}
}
mxGraph.prototype.selectCells=function(vertices,edges,parent)
{
parent=parent||this.getDefaultParent();
var self=this;
var filter=function(cell)
{
return self.view.getState(cell)!=null&&self.model.getChildCount(cell)==0&&((self.model.isVertex(cell)&&vertices)||(self.model.isEdge(cell)&&edges));
}
var cells=this.model.getCells(filter,parent);
this.setSelectionCells(cells);
}
mxGraph.prototype.getChildren=function(vertices,edges,parent)
{
parent=parent||this.getDefaultParent();
var children=new Array();
var childCount=this.model.getChildCount(parent);
for(var i=0;i<childCount;i++)
{
var child=this.model.getChildAt(parent,i);
if(((this.model.isVertex(child)&&vertices)||(this.model.isEdge(child)&&edges)))
{
children.push(child);
}
}
return children;
}
mxGraph.prototype.selectCellForEvent=function(cell,evt)
{
var isSelected=this.isCellSelected(cell);
if(mxEvent.isToggleSelection(evt))
{
if(isSelected)
{
this.selection.removeCell(cell);
}
else
{
this.selection.addCell(cell);
}
}
else if(!isSelected||this.getSelectionCount()!=1)
{
this.setSelectionCell(cell);
}
}
mxGraph.prototype.selectCellsForEvent=function(cells,evt)
{
if(mxEvent.isToggleSelection(evt))
{
this.selection.addCells(cells);
}
else
{
this.setSelectionCells(cells);
}
}

mxGraph.prototype.createHandler=function(state)
{
if(this.model.isEdge(state.cell))
{
state.handler=new mxEdgeHandler(state);
}
else
{
state.handler=new mxVertexHandler(state);
}
}
mxGraph.prototype.redrawHandler=function(state)
{
if(state!=null&&state.handler!=null)
{
state.handler.redraw();
}
}
mxGraph.prototype.hasHandler=function(state)
{
return state!=null&&state.handler!=null;
}
mxGraph.prototype.destroyHandler=function(state)
{
if(state!=null&&state.handler!=null)
{
state.handler.destroy();
state.handler=null;
}
}

mxGraph.prototype.addGraphListener=function(listener)
{
if(this.graphListeners==null)
{
this.graphListeners=new Array();
}
this.graphListeners.push(listener);
}
mxGraph.prototype.removeGraphListener=function(listener)
{
if(this.graphListeners!=null)
{
for(var i=0;i<this.graphListeners.length;i++)
{
if(this.graphListeners[i]==listener)
{
this.graphListeners.splice(i,1);
break;
}
}
}
}
mxGraph.prototype.dispatchGraphEvent=function(evtName,evt,cell,index)
{
if(typeof(mxDatatransfer)!='undefined')
{
mxDatatransfer.consumeSourceFunction(this,evt,cell);
}




if(evtName=='mousedown')
{
this.isMouseDown=true;
}
if((evtName!='mouseup'||this.isMouseDown)&&evt.detail!=2)
{
if(evtName=='mouseup')
{
this.isMouseDown=false;
}


if(!this.isEditing()&&(mxClient.IS_OP||evt.target!=this.container))
{
if(this.gestureHandler!=null)
{
if(evtName=='mousedown')
{


this.gestureHandler.mouseDown(evt,cell,index);
}
else if(evtName=='mousemove')
{
this.gestureHandler.mouseMove(evt,cell,index);
}
else if(evtName=='mouseup')
{
this.gestureHandler.mouseUp(evt,cell,index);
this.gestureHandler=null;
}
}
else if(this.graphListeners!=null)
{
evt.returnValue=true;
for(var i=0;i<this.graphListeners.length&&!mxEvent.isConsumed(evt);i++)
{
if(evtName=='mousedown')
{
this.graphListeners[i].mouseDown(evt,cell,index);
if(mxEvent.isConsumed(evt))
{
this.gestureHandler=this.graphListeners[i];
break;
}
}
else if(evtName=='mousemove')
{
this.graphListeners[i].mouseMove(evt,cell,index);
}
else if(evtName=='mouseup')
{
this.graphListeners[i].mouseUp(evt,cell,index);
}
}
}
if(evtName=='mouseup')
{
this.click(evt,cell);
}
}
}
}
}

{
function mxOverlay(image,tooltip)
{
this.image=image;
this.tooltip=tooltip;
}
mxOverlay.prototype=new mxEventSource();
mxOverlay.prototype.constructor=mxOverlay;
mxOverlay.prototype.image=null;
mxOverlay.prototype.imageWidth=16;
mxOverlay.prototype.imageHeight=16;
mxOverlay.prototype.tooltip=null;
mxOverlay.prototype.getBounds=function(state)
{
var isEdge=state.view.graph.getModel().isEdge(state.cell);
var s=state.view.scale;
var pt=null;
if(isEdge)
{
var pts=state.absolutePoints;
if(pts.length%2==1)
{
pt=pts[pts.length/2+1];
}
else
{
var idx=pts.length/2;
var p0=pts[idx-1];
var p1=pts[idx];
pt=new mxPoint(p0.x+(p1.x-p0.x)/2,p0.y+(p1.y-p0.y)/2);
}
}
return(pt!=null)?new mxRectangle(pt.x-this.imageWidth/2*s,pt.y-this.imageHeight/2*s,this.imageWidth*s,this.imageHeight*s)
:new mxRectangle(state.x+state.width-this.imageWidth*3/4*s,state.y+state.height-this.imageHeight*3/4*s,this.imageWidth*s,this.imageHeight*s)
}
mxOverlay.prototype.toString=function()
{
return this.tooltip;
}
}

{
function mxOutline(graph,container){
this.source=graph;
this.graph=new mxGraph(container,graph.getModel(),'fastest');
this.graph.setEnabled(false);
this.graph.isLabelsVisible=false;
this.graph.stylesheet=graph.stylesheet;
var self=this;
graph.getModel().addListener('change',function(sender,changes){
self.sourceChanged(sender,changes);
});
this.sourceChanged(null);
}
mxOutline.prototype.sourceChanged=function(sender,changes){
var bounds=this.source.view.bounds;
var mw=parseInt(this.source.container.offsetWidth);
var mh=parseInt(this.source.container.offsetHeight);
var c=this.graph.container.style;
var scale=0.3;
var cw=parseInt(c.width);
var ch=parseInt(c.height);
if(cw>0||ch>0){
var w=Math.max(mw,bounds.width+Math.abs(bounds.x))+cw*0.1;
var h=Math.max(mh,bounds.height+Math.abs(bounds.y))+ch*0.1;
var scale=Math.min(cw/w,ch/h);
}
this.graph.view.scale=scale;
}
}

{
function mxMultiplicity(isSource,type,attr,value,min,max,validNeighbors,countError,typeError){
this.isSource=isSource;
this.type=type;
this.attr=attr;
this.value=value;
if(min!=null){
this.min=min;
}
if(max!=null){
this.max=max;
}
this.validNeighbors=validNeighbors;
this.countError=mxResources.get(countError)||countError;
this.typeError=mxResources.get(typeError)||typeError;
}
mxMultiplicity.type=null;
mxMultiplicity.attr=null;
mxMultiplicity.value=null;
mxMultiplicity.isSource=true;
mxMultiplicity.min=1;
mxMultiplicity.max='n';
mxMultiplicity.validNeighbors=null;
mxMultiplicity.countError=null;
mxMultiplicity.typeError=null;
}

{
function mxGraphCanvas(doc){
this.document=doc;
}
mxGraphCanvas.prototype.createCanvas=function(graph){
this.graph=graph;
var bounds=graph.view.bounds;
var w=bounds.x+bounds.width+10;
var h=bounds.y+bounds.height+10;
if(mxClient.IS_IE){
var str='<object classid="CLSID:369303C2-D7AC-11D0-89D5-00A0C90833E6"'+' style="width:'+w+'px;height:'+h+'px;">\n';
str+='<param name="Line0001" value="SetLineColor(125, 125, 125)">\n';
str+='<param name="Line0002" value="RoundRect(-100, -100, 100, 100, 10, 10)">\n';
str+='</object>\n';
this.document.write(str);
}else{
var canvas=this.document.createElement('canvas');
canvas.setAttribute('width',w);
canvas.setAttribute('height',h);
var ctx=canvas.getContext('2d');
ctx.clearRect(0,0,w,h);
var cell=graph.getModel().getRoot();
this.paintCell(ctx,cell);
this.document.body.appendChild(canvas);
}
}
mxGraphCanvas.prototype.paintCell=function(ctx,cell){
if(cell!=null){
var model=this.graph.getModel();
var state=this.graph.view.getState(cell);
if(state!=null&&model.getGeometry(cell)!=null)
{
var x=state.x;
var y=state.y;
var w=state.width;
var h=state.height;
if(model.isVertex(cell)){
var d=state.style[mxConstants.STYLE_STARTSIZE];
var fill=state.style[mxConstants.STYLE_FILLCOLOR];
if(fill!=null){
ctx.fillStyle=fill;
ctx.fillRect(x,y,d,h);
}
var stroke=state.style[mxConstants.STYLE_STROKECOLOR];
if(stroke!=null){
ctx.strokeStyle=stroke;
ctx.strokeRect(x,y,w,h);
}
var image=state.style[mxConstants.STYLE_IMAGE];
if(image!=null){
var img=new Image();
img.src=image;
img.onload=function(){
ctx.drawImage(img,x,y,w,h);
}
}
}else if(model.isEdge(cell)){
var pts=state.absolutePoints;
if(pts!=null&&pts.length>1){
var stroke=state.style[mxConstants.STYLE_STROKECOLOR];
if(stroke!=null){
ctx.strokeStyle=stroke;
}
ctx.beginPath();
ctx.moveTo(pts[0].x,pts[0].y);
for(var i=1;i<pts.length;i++){
ctx.lineTo(pts[i].x,pts[i].y);
}
ctx.stroke();
}
}
}
var childCount=model.getChildCount(cell);
for(var i=0;i<childCount;i++){
this.paintCell(ctx,model.getChildAt(cell,i));
}
}
}
function mxGraphIECanvasContext(parent){
this.parent=parent;
}
}

{
function mxGraphHandler(graph)
{
this.graph=graph;
this.graph.addGraphListener(this);
}
mxGraphHandler.prototype.maxCells=(mxClient.IS_IE)?10:50;
mxGraphHandler.prototype.isEnabled=true;
mxGraphHandler.prototype.mouseDown=function(evt,cell,index)
{
if(this.isEnabled&&this.graph.isEnabled()&&index==null)
{
if(cell!=null)
{
this.delayedSelection=this.graph.isCellSelected(cell);
if(!this.delayedSelection)
{
this.graph.selectCellForEvent(cell,evt);
}
this.cell=cell;
if(this.graph.isMovable(cell))
{
this.startX=evt.clientX;
this.startY=evt.clientY;
var tmp=this.graph.getSelectionCells();
this.cells=new Array();
for(var i=0;i<tmp.length;i++)
{
if(this.graph.isMovable(tmp[i]))
{
this.cells.push(tmp[i]);
}
}
this.bounds=this.graph.view.getBounds(this.cells);
this.shape=new mxRectangleShape(this.bounds,null,'black');


this.shape.dialect=(this.graph.dialect!=mxConstants.DIALECT_SVG)?mxConstants.DIALECT_VML:mxConstants.DIALECT_SVG;
this.shape.isDashed=true;
this.shape.init(this.graph.view.getOverlayPane());
if(this.graph.dialect!=mxConstants.DIALECT_SVG)
{
var self=this;
mxEvent.addListener(this.shape.node,'mousemove',function(evt)
{
self.graph.dispatchGraphEvent('mousemove',evt,self.target||self.cell);
});
}
else
{
this.shape.node.setAttribute('style','pointer-events:none;');
}
this.shape.node.style.display='none';
this.highlight=new mxRectangleShape(new mxRectangle(0,0,0,0),null,'red','3');
this.highlight.dialect=this.graph.dialect;
this.highlight.init(this.graph.view.getOverlayPane());
if(this.graph.dialect!=mxConstants.DIALECT_SVG)
{
var self=this;
mxEvent.addListener(this.highlight.node,'mousemove',function(evt)
{
self.graph.dispatchGraphEvent('mousemove',evt,self.target);
});
}
else
{
this.highlight.node.setAttribute('style','pointer-events:none;');
}
this.highlight.node.style.display='none';
}
mxEvent.consume(evt);
}
}
}
mxGraphHandler.prototype.mouseMove=function(evt,cell)
{
if(this.cell!=null&&this.shape!=null&&this.shape.node!=null)
{
var dx=this.graph.snap(evt.clientX-this.startX);
var dy=this.graph.snap(evt.clientY-this.startY);
var bounds=new mxRectangle(this.bounds.x+dx,this.bounds.y+dy,this.bounds.width,this.bounds.height);
this.shape.bounds=bounds;
this.shape.node.style.display='inline';
this.shape.redraw();
cell=this.graph.getDropTarget(this.cells,evt,cell);
var model=this.graph.getModel();
var parent=cell;
while(parent!=null&&parent!=this.cell)
{
parent=model.getParent(parent);
}
var state=this.graph.view.getState(cell);
if(!this.graph.isCellSelected(cell)&&state!=null&&parent==null&&model.getParent(this.cell)!=cell)
{
if(this.target!=cell)
{
this.target=cell;
this.highlight.bounds=state;
this.highlight.node.style.display='inline';
this.highlight.redraw();
}
}
else
{
this.target=null;
this.highlight.node.style.display='none';
}
mxEvent.consume(evt);
}
}
mxGraphHandler.prototype.mouseUp=function(evt){
if(this.cell!=null&&this.graph.isMovable(this.cell)&&(evt.clientX!=this.startX||evt.clientY!=this.startY))
{
var scale=this.graph.view.scale;
var dx=this.graph.snap((evt.clientX-this.startX)/scale);
var dy=this.graph.snap((evt.clientY-this.startY)/scale);
var clone=this.graph.isCloneable()&&evt.ctrlKey;

this.cells=this.graph.move(this.graph.getSelectionCells(),dx,dy,clone,this.target,evt);
if(clone)
{
this.graph.setSelectionCells(this.cells);
}
}
else if(this.delayedSelection)
{
this.graph.selectCellForEvent(this.cell,evt);
}
if(this.cell!=null)
{
mxEvent.consume(evt);
}
if(this.shape!=null)
{
this.shape.destroy();
this.shape=null;
}
if(this.highlight!=null)
{
this.highlight.destroy();
this.highlight=null;
}
this.delayedSelection=false;
this.cell=null;
this.target=null;
}
}

{
function mxPanningHandler(graph,factoryMethod){
this.graph=graph;
this.factoryMethod=factoryMethod;
this.graph.addGraphListener(this);
this.isUseShiftKey=true;
this.isUsePopupTrigger=true;
this.isUseLeftButton=false;
this.isUseLeftButtonForPopup=false;
this.isSelectOnPopup=true;
this.isPanEnabled=true;
this.isActive=false;
this.div=document.createElement('div');
this.table=document.createElement('table');
this.table.className='mxPopupMenu';
this.tbody=document.createElement('tbody');
this.table.appendChild(this.tbody);
this.div.appendChild(this.table);
this.div.className='mxPopupMenu';
this.div.style.position='absolute';
this.div.style.display='none';
if(document.body!=null){
document.body.appendChild(this.div);
}
}
mxPanningHandler.prototype.MENU_TRANSPARENCY=90;
mxPanningHandler.prototype.addItem=function(title,image,funct){
var tr=document.createElement('tr');
tr.className='mxPopupMenuItem';
var col1=document.createElement('td');
col1.className='mxPopupMenuIcon';
if(image!=null){
var img=document.createElement('img');
img.src=image;
col1.appendChild(img);
}
tr.appendChild(col1);
var col2=document.createElement('td');
col2.className='mxPopupMenuItem';
mxUtils.write(col2,title);
tr.appendChild(col2);
this.tbody.appendChild(tr);
var div=this.div;
mxEvent.addListener(tr,'mousedown',function(evt){
mxEvent.consume(evt);
});
mxEvent.addListener(tr,'mouseup',function(evt){
div.style.display='none';
funct(evt);
mxEvent.consume(evt);
});
if(mxClient.IS_IE){
mxEvent.addListener(tr,'mousemove',function(evt){
tr.style.backgroundColor='#000066';
tr.style.color='white';
});
mxEvent.addListener(tr,'mouseout',function(evt){
tr.style.backgroundColor='';
tr.style.color='';
});
}
}
mxPanningHandler.prototype.addSeparator=function(){
var tr=document.createElement('tr');
var col1=document.createElement('td');
col1.className='mxPopupMenuIcon';
tr.appendChild(col1);
var col2=document.createElement('td');
var hr=document.createElement('hr');
hr.setAttribute('size','1');
col2.appendChild(hr);
tr.appendChild(col2);
this.tbody.appendChild(tr);
}
mxPanningHandler.prototype.isPanningTrigger=function(evt){
return(this.isUseLeftButton&&mxEvent.isLeftMouseButton(evt))||(this.isUseShiftKey&&evt.shiftKey)||(this.isUsePopupTrigger&&mxEvent.isPopupTrigger(evt));
}
mxPanningHandler.prototype.isPopupTrigger=function(evt){
return mxEvent.isPopupTrigger(evt)||(this.isUseLeftButtonForPopup&&mxEvent.isLeftMouseButton(evt))||(this.isUseShiftKey&&evt.shiftKey);
}
mxPanningHandler.prototype.mouseDown=function(evt,cell){
if(this.div!=null){
this.div.style.display='none';
}
this.popupTrigger=this.isPopupTrigger(evt);
this.startX=evt.clientX;
this.startY=evt.clientY;
if(this.isPanEnabled&&this.isPanningTrigger(evt)){
this.isActive=true;
mxEvent.consume(evt);
}else if(this.popupTrigger){
mxEvent.consume(evt);
}
}
mxPanningHandler.prototype.mouseMove=function(evt,cell){
if(this.isActive){
var dx=evt.clientX-this.startX;
var dy=evt.clientY-this.startY;
this.graph.shift(dx,dy);
mxEvent.consume(evt);
}
}
mxPanningHandler.prototype.mouseUp=function(evt,cell){
var dx=Math.abs(evt.clientX-this.startX);
var dy=Math.abs(evt.clientY-this.startY);
if(this.popupTrigger&&dx<this.graph.tolerance&&dy<this.graph.tolerance&&this.factoryMethod!=null)
{
if(this.graph.isEnabled()&&this.isSelectOnPopup&&cell!=null&&!this.graph.isCellSelected(cell))
{
this.graph.setSelectionCell(cell);
}
if(this.div!=null){
var point={x:evt.clientX+document.body.scrollLeft,y:evt.clientY+document.body.scrollTop};
this.div.style.left=point.x+'px';
this.div.style.top=point.y+'px';
while(this.tbody.firstChild!=null){
this.tbody.removeChild(this.tbody.firstChild);
}
this.factoryMethod(this,cell,evt);
mxUtils.fadeIn(this.div,this.MENU_TRANSPARENCY,10,null,mxClient.IS_FADE_MENU);
}
mxEvent.consume(evt);
}else if(this.isActive){
this.graph.shift(0,0);
var dx=evt.clientX-this.startX;
var dy=evt.clientY-this.startY;
var scale=this.graph.view.scale;
var t=this.graph.view.translate;
this.graph.view.setTranslate(t.x+dx/scale,t.y+dy/scale);
mxEvent.consume(evt);
}
this.isActive=false;
}
}

{
function mxTerminalMarker(graph,highlightColor){
this.graph=graph;
this.shape=new mxRectangleShape(new mxRectangle(0,0,0,0),null,highlightColor||'red','3');
this.shape.dialect=(this.graph.dialect!=mxConstants.DIALECT_SVG)?mxConstants.DIALECT_VML:mxConstants.DIALECT_SVG;
this.shape.init(graph.view.getOverlayPane());
this.shape.node.style.background='';
this.shape.node.style.display='none';
this.sourcePoint=new mxPoint(0,0);
this.targetPoint=new mxPoint(0,0);
}
mxTerminalMarker.prototype.init=function(evt,cell){
if(cell!=null){
this.shape.node.style.display='none';
this.source=cell;
this.sourceState=this.graph.view.getState(this.source);
this.sourcePerimeter=this.sourceState.style[mxConstants.STYLE_PERIMETER];
this.sourcePoint.x=this.sourceState.getCenterX();
this.sourcePoint.y=this.sourceState.getCenterY();
this.targetPoint.x=this.sourcePoint.x;
this.targetPoint.y=this.sourcePoint.y;
}
}
mxTerminalMarker.prototype.updateTerminal=function(evt,cell,isSource){
var model=this.graph.getModel();
if(cell!=null&&model.isConnectable(cell)){
var state=this.graph.view.getState(cell);
var perimeter=state.style[mxConstants.STYLE_PERIMETER];
var w=Math.max(mxConstants.MIN_ACTIVE_REGION/2,state.width*mxConstants.ACTIVE_REGION/2);
var h=Math.max(mxConstants.MIN_ACTIVE_REGION/2,state.height*mxConstants.ACTIVE_REGION/2);
var region=new mxRectangle(state.getCenterX()-w,state.getCenterY()-h,2*w,2*h);
var point=mxUtils.convertPoint(this.graph.container,evt.clientX,evt.clientY);
var childCount=model.getChildCount(cell);
if((isSource&&childCount==0)||mxUtils.contains(region,point.x,point.y))
{
this.shape.node.style.display='inline';
this.shape.bounds=new mxRectangle(state.x-2,state.y-2,state.width+4,state.height+4);
this.shape.redraw();
this.cell=cell;
if(isSource){
if(perimeter!=null&&this.sourceState){
var next=new mxPoint(this.sourceState.getCenterX(),this.sourceState.getCenterY());
var point=perimeter(state,null,this.sourceState,false,next);
this.targetPoint.x=point.x;
this.targetPoint.y=point.y;
}else{
this.targetPoint.x=state.getCenterX();
this.targetPoint.y=state.getCenterY();
}
if(this.sourcePerimeter!=null){
var point=this.sourcePerimeter(this.sourceState,null,this.sourceState,false,this.targetPoint);
this.sourcePoint.x=point.x;
this.sourcePoint.y=point.y;
}
}
return;
}
}
if(isSource){
var point=mxUtils.convertPoint(this.graph.container,evt.clientX,evt.clientY);
this.targetPoint.x=this.graph.snap(point.x);
this.targetPoint.y=this.graph.snap(point.y);
if(this.sourcePerimeter!=null){
var point=this.sourcePerimeter(this.sourceState,null,this.sourceState,false,this.targetPoint);
this.sourcePoint.x=point.x;
this.sourcePoint.y=point.y;
}
}
this.shape.node.style.display='none';
this.cell=null;
}
mxTerminalMarker.prototype.destroy=function(){
this.shape.destroy();
}
mxTerminalMarker.prototype.reset=function(){
if(this.shape.node!=null){
this.shape.node.style.display='none';
this.source=null;
this.cell=null;
}
}
}

{
function mxConnectionHandler(graph,edgeFactoryMethod)
{
this.graph=graph;
this.factoryMethod=edgeFactoryMethod;
this.graph.addGraphListener(this);
this.marker=new mxTerminalMarker(graph);
this.points=new Array();
this.points.push(this.marker.sourcePoint);
this.points.push(this.marker.targetPoint);
this.shape=new mxPolyline(this.points,'#6482B9');
this.shape.dialect=this.graph.dialect;
this.shape.isDashed=true;
this.shape.init(graph.view.getOverlayPane());
this.shape.node.style.display='none';
this.isEnabled=true;
this.isActive=false;
}
mxConnectionHandler.prototype.mouseDown=function(evt,cell)
{
if(this.graph.isEnabled()&&this.isEnabled){
this.marker.init(evt,cell);
if(this.marker.cell!=null)
{
this.shape.redraw();
this.shape.node.style.display='inline';
this.isActive=true;
this.error=this.graph.getEdgeValidationError(null,cell,cell);
mxEvent.consume(evt);
}
}
}
mxConnectionHandler.prototype.mouseMove=function(evt,cell)
{
if(this.graph.isEnabled()&&this.isEnabled)
{
this.marker.updateTerminal(evt,cell,this.isActive);
if(this.isActive)
{
this.error=this.graph.getEdgeValidationError(null,this.marker.source,this.marker.cell);
this.shape.node.setAttribute((this.graph.dialect!=mxConstants.DIALECT_SVG)?'strokecolor':'stroke',(this.error==null)?'#00FF00':'red');
this.shape.strokewidth=(this.error==null)?2:1;
this.shape.redraw();
}
else if(this.previous==null||this.previous.cell!=this.marker.cell)
{
var state=this.graph.view.getState(this.marker.cell);
if(this.previous!=null&&this.previous.shape!=null)
{
this.previous.shape.node.style.cursor=
this.previousCursor;
}
if(state!=null)
{
this.previousCursor=state.shape.node.style.cursor;
state.shape.node.style.cursor=(mxClient.IS_IE)?'all-scroll':'default';
}
this.previous=state;
}
if(this.marker.cell!=null||this.marker.source!=null)
{
mxEvent.consume(evt);
}
}
}
mxConnectionHandler.prototype.mouseUp=function(evt,cell)
{
if(this.isActive){
this.shape.node.style.display='none';
this.isActive=false;
if(this.error==null)
{
var edge=null;
if(this.factoryMethod!=null)
{
edge=this.factoryMethod(this.marker.source,this.marker.cell);
if(edge.getGeometry()==null)
{
edge.setGeometry(new mxGeometry());
}
}
else
{
edge=new mxCell('',new mxGeometry());
edge.edge=true;
}
var source=this.marker.source;
var target=this.marker.cell;
if(source!=null&&target!=null)
{

var model=this.graph.getModel();
model.beginUpdate();
try
{
var parent=this.graph.getDefaultParent();
if(model.getParent(source)==
model.getParent(target))
{
parent=model.getParent(source);
}
this.graph.addCell(edge,parent);
model.setTerminals(edge,source,target);
}
finally
{
model.endUpdate();
}
this.graph.setSelectionCell(edge);
}
}
else
{
if(this.marker.source==this.marker.cell)
{
this.graph.setSelectionCell(this.marker.source);
}
if(this.error.length>0)
{
alert(this.error);
}
}
this.error=null;
this.marker.reset();
mxEvent.consume(evt);
}
}
}

{
function mxRubberband(graph){
this.graph=graph;
this.graph.addGraphListener(this);
this.div=document.createElement('div');
this.div.className='mxRubberband';

mxEvent.addListener(this.div,'mousedown',function(evt){
graph.dispatchGraphEvent('mousedown',evt);
});
mxEvent.addListener(this.div,'mousemove',function(evt){
graph.dispatchGraphEvent('mousemove',evt);
});
mxEvent.addListener(this.div,'mouseup',function(evt){
graph.dispatchGraphEvent('mouseup',evt);
});
this.isActive=false;
this.isEnabled=true;
}
mxRubberband.prototype.mouseDown=function(evt,cell,index){
if(this.graph.isEnabled()&&this.isEnabled&&cell==null&&index==null)
{
this.startX=evt.clientX+document.body.scrollLeft;
this.startY=evt.clientY+document.body.scrollTop;
this.redraw(evt);
mxUtils.setOpacity(this.div,30);
this.div.style.display='inline';
document.body.appendChild(this.div);
this.isActive=true;
mxEvent.consume(evt);
}else{
this.isActive=false;
}
}
mxRubberband.prototype.mouseMove=function(evt){
if(this.isActive){
this.redraw(evt);
mxEvent.consume(evt);
}
}
mxRubberband.prototype.mouseUp=function(evt){
if(this.isActive){
mxUtils.fadeOut(this.div,30,true,10,null,mxClient.IS_FADE_RUBBERBAND);
var offset=mxUtils.getOffset(this.graph.container);
this.x-=offset.x;
this.y-=offset.y;
var rect=new mxRectangle(this.x,this.y,this.width,this.height);
if(rect.width>this.graph.tolerance||rect.height>this.graph.tolerance)
{
this.graph.selectRegion(rect,evt);
mxEvent.consume(evt);
}
}
this.isActive=false;
}
mxRubberband.prototype.redraw=function(evt){
var x=evt.clientX+document.body.scrollLeft;
var y=evt.clientY+document.body.scrollTop;
this.x=Math.min(this.startX,x);
this.y=Math.min(this.startY,y);
this.width=Math.max(this.startX,x)-this.x;
this.height=Math.max(this.startY,y)-this.y;
this.div.style.left=this.x+'px';
this.div.style.top=this.y+'px';
this.div.style.width=Math.max(1,this.width)+'px';
this.div.style.height=Math.max(1,this.height)+'px';
}
mxRubberband.prototype.destroy=function(){
if(this.div.parentNode!=null){
this.div.parentNode.removeChild(this.div);
}
this.graph.removeGraphListener(this);
}
}

{
function mxVertexHandler(state){
this.state=state;
this.graph=state.view.graph;
this.graph.addGraphListener(this);
this.bounds=new mxRectangle(state.x,state.y,state.width,state.height);
this.selectionBorder=new mxRectangleShape(this.bounds,null,'#00FF00');


this.selectionBorder.dialect=(this.graph.dialect!=mxConstants.DIALECT_SVG)?mxConstants.DIALECT_VML:mxConstants.DIALECT_SVG;
this.selectionBorder.isDashed=true;
this.selectionBorder.init(this.graph.view.getOverlayPane());
if(this.graph.dialect!=mxConstants.DIALECT_SVG){
var cell=this.state.cell;
var graph=this.graph;
this.selectionBorder.node.style.cursor='move';
mxEvent.addListener(this.selectionBorder.node,'mousedown',function(evt){
graph.dispatchGraphEvent('mousedown',evt,cell);
});
mxEvent.addListener(this.selectionBorder.node,'mousemove',function(evt){
graph.dispatchGraphEvent('mousemove',evt,cell);
});
mxEvent.addListener(this.selectionBorder.node,'mouseup',function(evt){
graph.dispatchGraphEvent('mouseup',evt,cell);
});
}else{
this.selectionBorder.node.setAttribute('style','pointer-events:none;');
}
if(this.graph.isSizable(state.cell)&&this.graph.getSelectionCount()<mxGraphHandler.prototype.maxCells)
{
this.sizers=new Array();
var i=0;
if(!this.isSingleSizer){
this.sizers.push(this.createSizer('nw-resize',i++));
this.sizers.push(this.createSizer('n-resize',i++));
this.sizers.push(this.createSizer('ne-resize',i++));
this.sizers.push(this.createSizer('w-resize',i++));
this.sizers.push(this.createSizer('e-resize',i++));
this.sizers.push(this.createSizer('sw-resize',i++));
this.sizers.push(this.createSizer('s-resize',i++));
}
this.sizers.push(this.createSizer('se-resize',i++));
}
this.redraw();
}
mxVertexHandler.prototype.isSingleSizer=false;
mxVertexHandler.prototype.createSizer=function(cursor,index){
var bounds=new mxRectangle(0,0,1,1);
var sizer=new mxRectangleShape(bounds,'#00FF00','black');
sizer.dialect=this.graph.dialect;
sizer.init(this.graph.view.getOverlayPane());
sizer.node.style.cursor=cursor;
var graph=this.graph;
var cell=this.state.cell;
mxEvent.addListener(sizer.node,'mousedown',function(evt){
graph.dispatchGraphEvent('mousedown',evt,cell,index);
});
mxEvent.addListener(sizer.node,'mousemove',function(evt){
graph.dispatchGraphEvent('mousemove',evt,cell);
});
mxEvent.addListener(sizer.node,'mouseup',function(evt){
graph.dispatchGraphEvent('mouseup',evt,cell);
});
return sizer;
}
mxVertexHandler.prototype.createBounds=function(x,y){
var s=3;
return new mxRectangle(x-s,y-s,2*s,2*s);
}
mxVertexHandler.prototype.mouseDown=function(evt,cell,index){
if(this.graph.isEnabled()&&this.state.cell==cell&&index!=null)
{
this.index=index;
this.startX=evt.clientX;
this.startY=evt.clientY;
mxEvent.consume(evt);
}
}
mxVertexHandler.prototype.mouseMove=function(evt){
if(this.index!=null){
var dx=this.graph.snap(evt.clientX-this.startX);
var dy=this.graph.snap(evt.clientY-this.startY);
this.bounds=this.union(this.state,dx,dy,this.index);
this.drawPreview();
mxEvent.consume(evt);
}
}
mxVertexHandler.prototype.mouseUp=function(evt){
if(this.index!=null&&this.state!=null){
var scale=this.graph.view.scale;
var cell=this.state.cell;
var dx=this.graph.snap(evt.clientX-this.startX)/scale;
var dy=this.graph.snap(evt.clientY-this.startY)/scale;
var geo=this.graph.getModel().getGeometry(cell);
var bounds=this.union(geo,dx,dy,this.index);
this.graph.resize(cell,bounds);
this.index=null;
mxEvent.consume(evt);
}
}
mxVertexHandler.prototype.union=function(bounds,dx,dy,index){
if(this.isSingleSizer){
return new mxRectangle(bounds.x,bounds.y,Math.max(0,bounds.width+dx),Math.max(0,bounds.height+dy));
}else{
var left=bounds.x;
var right=bounds.x+bounds.width;
var top=bounds.y;
var bottom=bounds.y+bounds.height;
if(index>4){
bottom=this.graph.snap(bottom+dy);
}else if(index<3){
top=this.graph.snap(top+dy);
}
if(index==0||index==3||index==5)
{
left+=dx;
}else if(index==2||index==4||index==7)
{
right+=dx;
}
var width=right-left;
var height=bottom-top;
if(width<0){
left+=width;
width=Math.abs(width);
}
if(height<0){
top+=height;
height=Math.abs(height);
}
return new mxRectangle(left,top,width,height);
}
}
mxVertexHandler.prototype.redraw=function(){
this.bounds=new mxRectangle(this.state.x,this.state.y,this.state.width,this.state.height);
if(this.sizers!=null){
var s=this.state;
var r=s.x+s.width;
var b=s.y+s.height;
if(this.isSingleSizer){
this.sizers[0].bounds=this.createBounds(r,b);
this.sizers[0].redraw();
}else{
var cx=s.x+s.width/2;
var cy=s.y+s.height/2;
this.sizers[0].bounds=this.createBounds(s.x,s.y);
this.sizers[0].redraw();
this.sizers[1].bounds=this.createBounds(cx,s.y);
this.sizers[1].redraw();
this.sizers[2].bounds=this.createBounds(r,s.y);
this.sizers[2].redraw();
this.sizers[3].bounds=this.createBounds(s.x,cy);
this.sizers[3].redraw();
this.sizers[4].bounds=this.createBounds(r,cy);
this.sizers[4].redraw();
this.sizers[5].bounds=this.createBounds(s.x,b);
this.sizers[5].redraw();
this.sizers[6].bounds=this.createBounds(cx,b);
this.sizers[6].redraw();
this.sizers[7].bounds=this.createBounds(r,b);
this.sizers[7].redraw();
}
}
this.drawPreview();
}
mxVertexHandler.prototype.drawPreview=function(){
this.selectionBorder.bounds=this.bounds;
this.selectionBorder.redraw();
}
mxVertexHandler.prototype.destroy=function(evt,cell){
this.graph.removeGraphListener(this);
this.selectionBorder.destroy();
if(this.sizers!=null){
for(var i=0;i<this.sizers.length;i++){
this.sizers[i].destroy();
}
}
}
}

{
function mxEdgeHandler(state)
{
this.state=state;
this.graph=this.state.view.graph;
this.graph.addGraphListener(this);
this.marker=new mxTerminalMarker(this.graph);

this.points=new Array();
this.points.push(new mxPoint(0,0));

this.abspoints=this.state.absolutePoints;
this.shape=new mxPolyline(this.abspoints,'#00FF00');
this.shape.dialect=(this.graph.dialect!=mxConstants.DIALECT_SVG)?mxConstants.DIALECT_VML:mxConstants.DIALECT_SVG;
this.shape.isDashed=true;
this.shape.init(this.graph.view.getOverlayPane());








var cell=this.state.cell;
var graph=this.graph;
mxEvent.addListener(this.shape.node,'dblclick',function(evt)
{
graph.dblClick(evt,cell);
mxEvent.consume(evt);
});
mxEvent.addListener(this.shape.node,'mousedown',function(evt)
{
graph.dispatchGraphEvent('mousedown',evt,cell);
});
mxEvent.addListener(this.shape.node,'mouseup',function(evt)
{
graph.dispatchGraphEvent('mouseup',evt,cell);
});
mxEvent.addListener(this.shape.node,'mousemove',function(evt)
{
graph.dispatchGraphEvent('mousemove',evt,cell);
});

var dummyBounds=new mxRectangle(0,0,1,1);
if(this.graph.getSelectionCount()<mxGraphHandler.prototype.maxCells)
{
this.bends=new Array();
for(var i=0;i<this.abspoints.length;i++)
{
if(!this.abspoints[i].isRouted)
{
var terminal=(i==0||i==this.abspoints.length-1);

if(i>0&&terminal&&this.bends.length==1)
{
var bend=this.createVirtualBend();
if(bend!=null)
{
this.virtual=true;
this.bends.push(bend);
}
}
var connected=terminal&&this.graph.getModel().getTerminal(state.cell,i==0)!=null;
var color=(connected)?'red':'#00FF00';
var bend=new mxRectangleShape(dummyBounds,color,'black');
bend.dialect=this.graph.dialect;
bend.init(this.graph.view.getOverlayPane());
bend.node.style.cursor='all-scroll';
this.installListeners(bend.node,this.bends.length);
this.bends.push(bend);
}
}
}
this.label=new mxPoint(state.absoluteOffset.x,state.absoluteOffset.y);
this.labelShape=new mxRectangleShape(dummyBounds,'yellow','black');
this.labelShape.dialect=this.graph.dialect;
this.labelShape.init(this.graph.view.getOverlayPane());
this.labelShape.node.style.cursor='move';
var graph=this.graph;
var cell=this.state.cell;
mxEvent.addListener(this.labelShape.node,'mousedown',function(evt)
{
graph.dispatchGraphEvent('mousedown',evt,cell,mxEdgeHandler.prototype.LABEL_INDEX);
});
this.redraw();
}
mxEdgeHandler.prototype.LABEL_INDEX=-1;
mxEdgeHandler.prototype.createVirtualBend=function()
{
var style=this.state.style[mxConstants.STYLE_EDGE];
if(style!=null)
{
var bend=new mxRectangleShape(new mxRectangle(0,0,1,1),'#00FF00','black');
bend.dialect=this.graph.dialect;
bend.init(this.graph.view.getOverlayPane());
var crs=(style==mxEdgeStyle.SideToSide&&this.state.style[mxConstants.STYLE_HORIZONTAL]!="true")?'col-resize':'row-resize';
bend.node.style.cursor=crs;
this.installListeners(bend.node,1);
var graph=this.graph;
var cell=this.state.cell;
mxEvent.addListener(bend.node,'dblclick',function(evt)
{
graph.flip(cell,evt);
mxEvent.consume(evt);
});
mxEvent.addListener(bend.node,'mousemove',function(evt)
{
graph.dispatchGraphEvent('mousemove',evt,cell,mxResources.get('doubleClickOrientation'));
});
return bend;
}
return null;
}
mxEdgeHandler.prototype.mouseDown=function(evt,cell,index)
{
if(this.graph.isEnabled()&&this.state.cell==cell&&index!=null)
{
this.index=index;
this.isSource=index==0;
this.isTarget=index==this.bends.length-1;
this.isLabel=index==this.LABEL_INDEX;
this.startX=evt.clientX;
this.startY=evt.clientY;
if(this.isSource||this.isTarget)
{
this.marker.init(evt,cell);
this.shape.stroke='black';

var p0=this.abspoints[0];
var pe=this.abspoints[this.abspoints.length-1];
this.abspoints=new Array();
this.abspoints.push(p0);
this.abspoints.push(pe);
}
mxEvent.consume(evt);
}
}
mxEdgeHandler.prototype.mouseMove=function(evt,cell)
{
if(this.index!=null)
{
var point=mxUtils.convertPoint(this.graph.container,evt.clientX,evt.clientY);
if(this.isLabel)
{
this.label.x=point.x;
this.label.y=point.y;
}
else if(this.isSource||this.isTarget)
{
this.marker.updateTerminal(evt,cell,true);
var idx=(this.isSource)?0:this.abspoints.length-1;
var idx2=(!this.isSource)?0:this.abspoints.length-1;
this.abspoints[idx].x=point.x;
this.abspoints[idx].y=point.y;
if(this.marker.cell!=null)
{
this.abspoints[idx].x=this.marker.targetPoint.x;
this.abspoints[idx].y=this.marker.targetPoint.y;

}
var src=(this.isSource)?this.marker.cell:
this.graph.getModel().getTerminal(this.state.cell,true);
var trg=(this.isTarget)?this.marker.cell:
this.graph.getModel().getTerminal(this.state.cell,false);
this.error=this.graph.getEdgeValidationError(this.state.cell,src,trg);
this.shape.node.setAttribute((this.graph.dialect!=mxConstants.DIALECT_SVG)?'strokecolor':'stroke',(this.error==null)?'#00FF00':'red');
}
else
{
this.isActive=true;
var index=this.index;
if(this.virtual)
{
this.convertVirtualPoint(point);
index=0;
}
else
{
point.x=this.graph.snap(point.x);
point.y=this.graph.snap(point.y);
index--;
}
this.points[index]=point;
this.state.view.updatePoints(this.state,this.points);
this.state.view.updateTerminalPoints(this.state);
this.abspoints=this.state.absolutePoints;
}
this.drawPreview();
mxEvent.consume(evt);
}
}
mxEdgeHandler.prototype.convertVirtualPoint=function(point)
{
var scale=this.graph.view.scale;
point.x=this.graph.snap(point.x)/scale-this.graph.view.translate.x-this.state.origin.x;
point.y=this.graph.snap(point.y)/scale-this.graph.view.translate.y-this.state.origin.y;
}
mxEdgeHandler.prototype.mouseUp=function(evt)
{
if(this.index!=null)
{
if(evt.clientX!=this.startX||evt.clientY!=this.startY)
{
var model=this.graph.getModel();
if(this.isLabel)
{
var p0=this.abspoints[0];
var pe=this.abspoints[this.abspoints.length-1];
var dx=pe.x-p0.x;
var dy=pe.y-p0.y;
var cx=p0.x+dx/2;
var cy=p0.y+dy/2;
var x=this.label.x-cx;
var y=this.label.y-cy;
var geometry=model.getGeometry(this.state.cell).clone();
var scale=this.graph.view.scale;
geometry.offset=new mxPoint(x/scale,y/scale);
model.setGeometry(this.state.cell,geometry);
}
else if(this.isSource||this.isTarget)
{
if(this.error==null)
{
var edge=this.state.cell;
model.beginUpdate();
try
{
var parent=model.getParent(edge);
if(this.graph.isCloneable()&&evt.ctrlKey)
{
var clone=edge.clone();
model.add(parent,clone,model.getChildCount(parent));
model.setTerminal(clone,model.getTerminal(edge,!this.isSource),!this.isSource);
edge=clone;
}
if(this.marker.cell==null)
{
var abs=this.state.absolutePoints;
var start=abs[(this.isSource)?0:abs.length-1];
var current=this.abspoints[(this.isSource)?0:this.abspoints.length-1];
var geo=model.getGeometry(edge).clone();
var scale=this.graph.view.scale;
var pstate=this.graph.view.getState(parent);
var dx=(pstate!=null)?pstate.origin.x:0;
var dy=(pstate!=null)?pstate.origin.y:0;
geo.setTerminalPoint(new mxPoint(start.x+(current.x-start.x)/scale-dx,start.y+(current.y-start.y)/scale-dy),this.isSource);
model.setGeometry(edge,geo);
model.setTerminal(edge,null,this.isSource);
}
else
{
this.graph.connect(edge,this.marker.cell,this.isSource);
}
}
finally
{
model.endUpdate();
}
}
else
{
this.graph.view.invalidate(this.state.cell);
this.graph.view.revalidate(this.state.cell);
}
this.marker.reset();
}
else if(this.isActive)
{
var geo=model.getGeometry(this.state.cell);
if(geo!=null)
{
geo=geo.clone();
geo.points=this.points;
model.setGeometry(this.state.cell,geo);
}
}
this.abspoints=this.state.absolutePoints;
var p0=this.abspoints[0];
var pe=this.abspoints[this.abspoints.length-1];
this.marker.sourcePoint.x=p0.x;
this.marker.sourcePoint.y=p0.y;
this.marker.targetPoint.x=pe.x;
this.marker.targetPoint.y=pe.y;
if(this.shape.node!=null)
{
this.shape.node.setAttribute((this.graph.dialect!=mxConstants.DIALECT_SVG)?'strokecolor':'stroke','#00FF00');
this.redraw();
}
this.isActive=false;
this.index=null;
if(this.error!=null&&this.error.length>0)
{
alert(this.error);
}
this.error=null;
}
mxEvent.consume(evt);
}
}
mxEdgeHandler.prototype.redraw=function()
{
this.abspoints=this.state.absolutePoints;
if(this.bends!=null)
{
var model=this.graph.getModel();
var s=4;
var n=this.abspoints.length-1;
var x0=this.abspoints[0].x;
var y0=this.abspoints[0].y;
var bounds=new mxRectangle(x0-s,y0-s,2*s,2*s);
this.bends[0].bounds=bounds;
var color=(model.getTerminal(this.state.cell,true)!=null)?'red':'#00FF00';
this.bends[0].fill=color;
this.bends[0].reconfigure();
this.bends[0].redraw();
var xn=this.abspoints[n].x;
var yn=this.abspoints[n].y;
bounds=new mxRectangle(xn-s,yn-s,2*s,2*s);
var bn=this.bends.length-1;
this.bends[bn].bounds=bounds;
var color=(model.getTerminal(this.state.cell,false)!=null)?'red':'#00FF00';
this.bends[bn].fill=color;
this.bends[bn].reconfigure();
this.bends[bn].redraw();
if(this.virtual)
{
var g=model.getGeometry(this.state.cell);
var pt=(g.points!=null)?g.points[0]:null;
if(pt==null)
{
pt=new mxPoint(x0+(xn-x0)/2,y0+(yn-y0)/2);
}
else
{
pt=new mxPoint(this.graph.view.scale*(pt.x+this.graph.view.translate.x+this.state.origin.x),this.graph.view.scale*(pt.y+this.graph.view.translate.y+this.state.origin.y));
}
bounds=new mxRectangle(pt.x-s,pt.y-s,2*s,2*s);
this.bends[1].bounds=bounds;
this.bends[1].reconfigure();
this.bends[1].redraw();
}
}
var s=3;
this.label=new mxPoint(this.state.absoluteOffset.x,this.state.absoluteOffset.y);
var bounds=new mxRectangle(this.label.x-s,this.label.y-s,2*s,2*s);
this.labelShape.bounds=bounds;
this.labelShape.redraw();
var lab=this.graph.getLabel(this.state.cell);
if(lab!=null&&lab.length>0)
{
this.labelShape.node.style.display='inline';
}
else
{
this.labelShape.node.style.display='none';
}
this.drawPreview();
}
mxEdgeHandler.prototype.drawPreview=function()
{
if(this.isLabel)
{
var s=3;
var bounds=new mxRectangle(this.label.x-s,this.label.y-s,2*s,2*s);
this.labelShape.bounds=bounds;
this.labelShape.redraw();
}
else
{
this.shape.points=this.abspoints;
this.shape.redraw();
}
}
mxEdgeHandler.prototype.installListeners=function(node,index)
{
var graph=this.graph;
var cell=this.state.cell;
mxEvent.addListener(node,'mousedown',function(evt)
{
graph.dispatchGraphEvent('mousedown',evt,cell,index);
});
}
mxEdgeHandler.prototype.destroy=function(evt,cell)
{
this.graph.removeGraphListener(this);
this.marker.destroy();
this.shape.destroy();
this.labelShape.destroy();
if(this.bends!=null)
{
for(var i=0;i<this.bends.length;i++)
{
if(this.bends[i]!=null)
{
this.bends[i].destroy();
}
}
}
}
}

{
function mxKeyHandler(graph,target)
{
target=target||document;
this.normalKeys=new Array();
this.controlKeys=new Array();
this.graph=graph;
var self=this;
mxEvent.addListener(target,"keydown",function(evt)
{
self.keyDown(evt);
});
}
mxKeyHandler.prototype.enabled=true;
mxKeyHandler.prototype.isEnabled=function()
{
return this.enabled;
}
mxKeyHandler.prototype.bindKey=function(code,funct)
{
this.normalKeys[code]=funct;
}
mxKeyHandler.prototype.bindControlKey=function(code,funct)
{
this.controlKeys[code]=funct;
}
mxKeyHandler.prototype.keyDown=function(evt)
{
if(this.isEnabled()&&this.graph.isEnabled())
{
if(this.graph.isEditing()&&((evt.keyCode==13&&!evt.ctrlKey&&!evt.shiftKey)||(evt.keyCode==113)))
{
if(this.onEditNewline!=null)
{
this.onEditNewline(evt);
}
else
{
this.graph.editor.stopEditing(false);
}
}
else if(evt.keyCode==27)
{
if(this.onAbort!=null)
{
this.onAbort(evt);
}
else
{
this.graph.editor.stopEditing(true);
}
}
else if(!this.graph.isEditing())
{
var funct=(evt.ctrlKey)?this.controlKeys[evt.keyCode]:
this.normalKeys[evt.keyCode];
if(funct!=null)
{
funct(evt);
mxEvent.consume(evt);
}
}
}
}
}

{
function mxOutlineHandler(outline){
this.graph=outline.source;
this.navigation=outline.graph;
this.navigation.addGraphListener(this);
var self=this;
var funct=function(sender){
self.update();
};
this.graph.view.addListener('scale',funct);
this.graph.view.addListener('translate',funct);
this.graph.view.addListener('down',funct);
this.graph.view.addListener('up',funct);
this.graph.getModel().addListener('change',funct);
this.bounds=new mxRectangle(0,0,0,0);
this.selectionBorder=new mxRectangleShape(this.bounds,null,'#0099FF',3);
this.selectionBorder.dialect=this.navigation.dialect;
this.selectionBorder.init(this.navigation.view.getOverlayPane());
var s=3;
this.sizer=new mxRectangleShape(this.bounds,'#00FFFF','#0033FF');
this.sizer.dialect=this.navigation.dialect;
this.sizer.init(this.navigation.view.getOverlayPane());
this.sizer.node.style.cursor='pointer';
mxEvent.addListener(this.sizer.node,'mousedown',function(evt){
outline.graph.dispatchGraphEvent('mousedown',evt,null,0);
});
this.isActive=false;
this.update();
}
mxOutlineHandler.prototype.update=function(){
var t=this.graph.view.translate;
this.navigation.view.setTranslate(Math.max(0,t.x),Math.max(0,t.y));
var t2=this.navigation.view.translate;
var scale=this.graph.view.scale;
var scale2=scale/this.navigation.view.scale;
var scale3=1.0/this.navigation.view.scale;
var container=this.graph.container;
this.bounds=new mxRectangle((t2.x-t.x)/scale3,(t2.y-t.y)/scale3,(container.offsetWidth/scale2),(container.offsetHeight/scale2));
this.selectionBorder.bounds=this.bounds;
this.selectionBorder.redraw();
var s=3;
this.sizer.bounds=new mxRectangle(this.bounds.x+this.bounds.width-s,this.bounds.y+this.bounds.height-s,2*s,2*s);
this.sizer.redraw();
}
mxOutlineHandler.prototype.mouseDown=function(evt,cell,index){
this.index=index;
this.startX=evt.clientX;
this.startY=evt.clientY;
this.isActive=true;
mxEvent.consume(evt);
}
mxOutlineHandler.prototype.mouseMove=function(evt,cell){
if(this.isActive){
var dx=evt.clientX-this.startX;
var dy=evt.clientY-this.startY;
var bounds=null;
if(this.index==null){
var scale=this.navigation.view.scale;
bounds=new mxRectangle(this.bounds.x+dx,this.bounds.y+dy,this.bounds.width,this.bounds.height);
this.selectionBorder.bounds=bounds;
this.selectionBorder.redraw();
dx/=scale;
dx*=this.graph.view.scale;
dy/=scale;
dy*=this.graph.view.scale;
this.graph.shift(-dx,-dy);
}else{
var container=this.graph.container;
var viewRatio=container.offsetWidth/container.offsetHeight;
dy=dx/viewRatio;
bounds=new mxRectangle(this.bounds.x,this.bounds.y,this.bounds.width+dx,this.bounds.height+dy);
this.selectionBorder.bounds=bounds;
this.selectionBorder.redraw();
}
var s=3;
this.sizer.bounds=new mxRectangle(bounds.x+bounds.width-s,bounds.y+bounds.height-s,2*s,2*s);
this.sizer.redraw();
mxEvent.consume(evt);
}
}
mxOutlineHandler.prototype.mouseUp=function(evt,cell){
if(this.isActive){
var dx=evt.clientX-this.startX;
var dy=evt.clientY-this.startY;
if(this.index==null){
this.graph.shift(0,0);
dx/=this.navigation.view.scale;
dy/=this.navigation.view.scale;

var t=this.graph.view.translate;
this.graph.view.setTranslate(t.x-dx,t.y-dy)
}else{
var w=this.selectionBorder.bounds.width;
var h=this.selectionBorder.bounds.height;
var scale=this.graph.view.scale;
this.graph.view.setScale(scale-(dx*scale)/w);
}
this.index=null;
this.isActive=false;
this.update();
mxEvent.consume(evt);
}
}
}

{
function mxTooltipHandler(graph,delay)
{
this.graph=graph;
this.delay=delay||500;
this.graph.addGraphListener(this);
this.div=document.createElement('div');
this.div.className='mxTooltip';
this.div.style.position='absolute';
this.div.style.display='none';
if(document.body!=null)
{
document.body.appendChild(this.div);
}
this.isEnabled=true;
}
mxTooltipHandler.prototype.mouseDown=function(evt,cell,index)
{
this.reset(evt,cell,index,false);
this.div.style.display='none'
}
mxTooltipHandler.prototype.hide=function()
{
this.div.style.display='none';
}
mxTooltipHandler.prototype.mouseMove=function(evt,cell,index)
{
if(evt!=this.lastEvent)
{
this.reset(evt,cell,index,true);

if(cell!=this.cell||index!=this.index)
{
this.hide();
}
}
this.lastEvent=evt;
}
mxTooltipHandler.prototype.mouseUp=function(evt,cell,index)
{
this.reset(evt,cell,index,true);
this.hide();
}
mxTooltipHandler.prototype.reset=function(evt,cell,index,restart)
{
if(this.thread!=null){
window.clearTimeout(this.thread);
this.thread=null;
}
if(restart&&this.isEnabled&&cell!=null&&this.div.style.display!='inline')
{
var self=this;
var x=evt.clientX;
var y=evt.clientY;
this.thread=window.setTimeout(function()
{
if(!self.graph.isEditing())
{
self.showTooltip(cell,index,x,y);
self.cell=cell;
self.index=index;
}
},this.delay);
}
}
mxTooltipHandler.prototype.showTooltip=function(cell,index,x,y)
{
var tip=this.graph.getTooltip(cell,index);
if(tip!=null&&tip.length>0)
{
while(this.div.firstChild!=null)
{
this.div.removeChild(this.div.firstChild);
}
this.div.style.left=x+'px';
this.div.style.top=(y+10)+'px';
this.div.innerHTML=tip;
this.div.style.display='inline';
}
}
mxTooltipHandler.prototype.destroy=function()
{
if(this.div.parentNode!=null)
{
this.div.parentNode.removeChild(this.div);
}
this.graph.removeGraphListener(this);
}
}

{
function mxHighlight(graph,color)
{
this.graph=graph;
this.graph.addGraphListener(this);
this.marker=new mxTerminalMarker(graph,color||'blue');
}
mxHighlight.prototype.enabled=true;
mxHighlight.prototype.mouseDown=function(evt,cell)
{
}
mxHighlight.prototype.mouseMove=function(evt,cell)
{
if(this.enabled)
{
this.marker.updateTerminal(evt,cell,true);
mxEvent.consume(evt);
}
}
mxHighlight.prototype.mouseUp=function(evt,cell)
{
this.marker.reset();
}
}

{
function mxDefaultKeyHandler(editor)
{
if(editor!=null){
this.editor=editor;
this.handler=new mxKeyHandler(editor.graph,document);

this.handler.onAbort=function(evt)
{
editor.graph.editor.stopEditing(true);
editor.hideProperties();
editor.toolbar.toolbar.resetMode(true);
}

this.handler.onEditNewline=function(evt)
{
editor.graph.editor.stopEditing(false);
}
}
}
mxDefaultKeyHandler.prototype.handler=null;
mxDefaultKeyHandler.prototype.bindAction=function(code,actionname,isControl)
{
var editor=this.editor;
var f=function()
{
editor.execute(actionname);
};
if(isControl)
{
this.handler.bindControlKey(code,f);
}
else
{
this.handler.bindKey(code,f);
}
}
}

{
function mxDefaultPopupMenu(config)
{
this.config=config;
}
mxDefaultPopupMenu.prototype.config=null;
mxDefaultPopupMenu.prototype.createMenu=function(editor,menu,cell,evt)
{
var model=editor.graph.getModel();
var childCount=model.getChildCount(cell);

var conditions=new Array();
conditions['nocell']=cell==null;
conditions['ncells']=editor.graph.getSelectionCount()>1;
conditions['notRoot']=model.getRoot()!=
model.getParent(editor.graph.getDefaultParent());
conditions['cell']=cell!=null;
var isCell=cell!=null&&editor.graph.getSelectionCount()==1;
conditions['nonEmpty']=isCell&&childCount>0;
conditions['expandable']=isCell&&editor.graph.isExpandable(cell);
conditions['collapsable']=isCell&&editor.graph.isCollapsable(cell);
conditions['validRoot']=isCell&&editor.graph.isValidRoot(cell);
conditions['emptyValidRoot']=conditions['validRoot']&&childCount==0;
conditions['swimlane']=isCell&&editor.graph.isSwimlane(cell);
var addSeparator=false;
if(this.config!=null)
{
var item=this.config.firstChild;
while(item!=null)
{
if(item.nodeName=='add')
{
var condition=item.getAttribute('if');
if(condition==null||conditions[condition])
{
var as=item.getAttribute('as');
as=mxResources.get(as)||as;
var action=item.getAttribute('action');
var icon=item.getAttribute('icon');
if(addSeparator)
{
menu.addSeparator();
addSeparator=false;
}
this.addAction(menu,editor,as,icon,action,cell);
}
}
else if(item.nodeName=='separator')
{
addSeparator=true;
}
item=item.nextSibling;
}
}
}
mxDefaultPopupMenu.prototype.addAction=function(menu,editor,label,icon,action,cell)
{
menu.addItem(label,icon,function()
{
editor.execute(action,cell);
});
}
}

{
function mxDefaultToolbar(container,editor)
{
if(container!=null&&editor!=null)
{
this.toolbar=new mxToolbar(container);
this.editor=editor;
editor.graph.addGraphListener(this);
}
}
mxDefaultToolbar.prototype.editor=null;
mxDefaultToolbar.prototype.toolbar=null;
mxDefaultToolbar.prototype.spacing=4;
mxDefaultToolbar.prototype.mouseDown=function(evt,cell,index)
{
if(this.editor.isForcedInserting||(cell==null&&index==null))
{
if(this.toolbar.selectedFunction!=null&&!mxEvent.isPopupTrigger(evt))
{
this.editor.graph.selection.clear();
this.toolbar.selectedFunction(evt,cell);
this.toolbar.resetMode();
mxEvent.consume(evt);
}
}
}
mxDefaultToolbar.prototype.mouseMove=function(evt,cell)
{
mxEvent.consume(evt);
}
mxDefaultToolbar.prototype.mouseUp=function(evt,cell)
{
mxEvent.consume(evt);
}
mxDefaultToolbar.prototype.addItem=function(label,icon,actionname,pressedIcon)
{
var editor=this.editor;
this.toolbar.addItem(label,icon,function()
{
editor.execute(actionname);
},pressedIcon);
}
mxDefaultToolbar.prototype.addSeparator=function(icon)
{
icon=(icon!=null)?icon:mxClient.imageBasePath+'separator.gif';
this.toolbar.addSeparator(icon);
}
mxDefaultToolbar.prototype.addCombo=function()
{
return this.toolbar.addCombo();
}
mxDefaultToolbar.prototype.addActionCombo=function(title)
{
return this.toolbar.addActionCombo(title);
}
mxDefaultToolbar.prototype.addActionOption=function(combo,label,actionname)
{
var editor=this.editor;
this.addOption(combo,label,function()
{
editor.execute(actionname);
});
}
mxDefaultToolbar.prototype.addOption=function(combo,label,value)
{
return this.toolbar.addOption(combo,label,value);
}
mxDefaultToolbar.prototype.addMode=function(label,icon,modename,pressedIcon,funct)
{
var self=this;
this.toolbar.addSwitchMode(label,icon,function(evt)
{
if(modename=='select')
{
self.editor.graph.panningHandler.isUseLeftButton=false;
self.editor.graph.connectionHandler.isEnabled=false;
}
else if(modename=='connect')
{
self.editor.graph.panningHandler.isUseLeftButton=false;
self.editor.graph.connectionHandler.isEnabled=true;
}
else if(modename=='pan')
{
self.editor.graph.panningHandler.isUseLeftButton=true;
self.editor.graph.connectionHandler.isEnabled=false;
}
if(funct!=null){
funct(self.editor);
}
},pressedIcon);
}
mxDefaultToolbar.prototype.addPrototype=function(label,icon,ptype,pressedIcon)
{
var img=null;
if(ptype==null)
{
img=this.toolbar.addMode(label,icon,null,pressedIcon);
}
else
{
var createFunction=function()
{
if(typeof(ptype)=='function')
{
return ptype();
}
else
{
return ptype.clone();
}
};
var self=this;
var mode=function(evt,cell)
{
var pt=mxUtils.convertPoint(self.editor.graph.container,evt.clientX,evt.clientY);
return self.editor.addVertex(cell,createFunction(),pt.x,pt.y);
};
img=this.toolbar.addMode(label,icon,mode,pressedIcon);


var funct=function(graph,evt,cell)
{
var model=graph.getModel();
var vertex=createFunction();
if(cell!=null&&(!graph.isValidDropTarget(cell)||model.isConnectable(cell))&&!model.isEdge(cell)&&model.isConnectable(vertex)&&graph.getEdgeValidationError(null,cell,vertex)==null)
{
model.beginUpdate();
try
{
var geo=model.getGeometry(cell);
var g=model.getGeometry(vertex).clone();
g.x=geo.x+(geo.width-g.width)/2;
g.y=geo.y+(geo.height-g.height)/2;
var step=self.spacing*graph.gridSize;
var dist=cell.getDirectedEdgeCount(true)*20;
if(self.editor.isHorizontalFlow)
{
g.x+=(g.width+geo.width)/2+step+dist;
}
else
{
g.y+=(g.height+geo.height)/2+step+dist;
}
vertex.setGeometry(g);
var edge=self.editor.createEdge();
var parent=model.getParent(cell);
model.add(parent,edge);
model.setTerminals(edge,cell,vertex);
graph.addCell(vertex,parent);
}
finally
{
model.endUpdate();
}
graph.setSelectionCell(vertex);
graph.scrollCellToVisible(vertex);
}
else
{
mode(evt);
}
self.toolbar.resetMode();
mxEvent.consume(evt);
};
mxEvent.addListener(img,'mousedown',function(evt)
{
mxDatatransfer.setSourceFunction(funct);
self.startX=evt.clientX;
self.startY=evt.clientY;
mxEvent.consume(evt);
});


mxEvent.addListener(img,'mousemove',function(evt)
{
if(typeof(mxDatatransfer)!='undefined'&&mxDatatransfer.sourceFunction==funct)
{
if(self.sprite==null)
{
var sprite=document.createElement('img');
self.sprite=sprite;
sprite.setAttribute('src',img.getAttribute('src'));
sprite.style.position='absolute';
sprite.style.left=evt.clientX+document.body.scrollLeft;
sprite.style.top=evt.clientY+document.body.scrollTop;
sprite.style.width=(2*img.offsetWidth)+'px';
sprite.style.height=(2*img.offsetHeight)+'px';
sprite.style.zIndex=2;
mxUtils.setOpacity(sprite,70);
document.body.appendChild(sprite);
}
if(!mxClient.IS_IE&&self.interceptor==null)
{
var interceptor=document.createElement('div');
self.interceptor=interceptor;
interceptor.style.background='url(\''+mxClient.imageBasePath+'images/transparent.gif\')';
interceptor.style.zIndex=3;
interceptor.style.position='absolute';
interceptor.style.left=document.body.offsetTop+'px';
interceptor.style.top=document.body.offsetLeft+'px';
interceptor.style.width=((mxClient.IS_IE?document.body.offsetWidth-10:window.innerWidth)-10)+'px';
interceptor.style.height=((mxClient.IS_IE?document.body.offsetHeight:window.innerHeight)-10)+'px';
document.body.appendChild(interceptor);
mxEvent.addListener(interceptor,'mousemove',function(evt)
{
sprite.style.left=evt.clientX+document.body.scrollLeft;
sprite.style.top=evt.clientY+document.body.scrollTop;
mxEvent.consume(evt);
});
mxEvent.addListener(interceptor,'mouseup',function(evt)
{
var graph=self.editor.graph;
document.body.removeChild(interceptor);
document.body.removeChild(sprite);
if(Math.abs(evt.clientX-self.startX)>graph.tolerance||Math.abs(evt.clientY-self.startY)>graph.tolerance)
{
var pt=mxUtils.convertPoint(graph.container,evt.clientX,evt.clientY);
var target=graph.getCellAt(pt.x,pt.y);
graph.dispatchGraphEvent('mouseup',evt,target);
}
else
{
mxDatatransfer.setSourceFunction(null);
}
mxEvent.consume(evt);
self.interceptor=null;
self.sprite=null;
});
}
else
{
if(self.sprite!=null)
{
self.sprite.style.left=evt.clientX+document.body.scrollLeft;
self.sprite.style.top=evt.clientY+document.body.scrollTop;
}
}
}
if(typeof(mxEvent)!='undefined')
{
mxEvent.consume(evt);
}
});
mxEvent.addListener(img,'mouseup',function(evt)
{
var graph=self.editor.graph;
if(self.interceptor==null)
{
if(self.sprite!=null)
{
if(Math.abs(evt.clientX-self.startX)>2*graph.tolerance||Math.abs(evt.clientY-self.startY)>2*graph.tolerance)
{
var pt=mxUtils.convertPoint(graph.container,evt.clientX,evt.clientY);
var target=graph.getCellAt(pt.x,pt.y);
graph.dispatchGraphEvent('mouseup',evt,target);
}
document.body.removeChild(self.sprite);
self.sprite=null;
}
mxDatatransfer.setSourceFunction(null);
mxEvent.consume(evt);
}
});
}
return img;
}
}

{
function mxEditor(config){
this.addAction('save',function(editor){editor.save();});
this.addAction('print',function(editor){mxUtils.print(editor.graph);});
this.addAction('preview',function(editor){mxUtils.show(editor.graph);});
this.addAction('refresh',function(editor){editor.graph.refresh();});
this.addAction('cut',function(editor){mxClipboard.cut(editor.graph);});
this.addAction('copy',function(editor){mxClipboard.copy(editor.graph);});
this.addAction('paste',function(editor){mxClipboard.paste(editor.graph);});
this.addAction('delete',function(editor){
if(editor.graph.isEnabled())
{
editor.graph.remove();
}
});
this.addAction('group',function(editor){
if(editor.graph.isEnabled())
{
editor.group();
}
});
this.addAction('ungroup',function(editor){
if(editor.graph.isEnabled())
{
editor.graph.ungroup();
}
});
this.addAction('undo',function(editor){
if(editor.graph.isEnabled())
{
editor.undo();
}
});
this.addAction('redo',function(editor){
if(editor.graph.isEnabled())
{
editor.redo();
}
});
this.addAction('zoomIn',function(editor){
if(editor.graph.isEnabled())
{
editor.graph.zoomIn();
}
});
this.addAction('zoomOut',function(editor){
if(editor.graph.isEnabled())
{
editor.graph.zoomOut();
}
});
this.addAction('actualSize',function(editor){
if(editor.graph.isEnabled())
{
editor.graph.zoomActual();
}
});
this.addAction('fit',function(editor){
if(editor.graph.isEnabled())
{
editor.graph.fit();
}
});
this.addAction('showProperties',function(editor,cell){editor.showProperties(cell);});
this.addAction('selectAll',function(editor){
if(editor.graph.isEnabled())
{
editor.graph.selectAll();
}
});
this.addAction('selectNone',function(editor){
if(editor.graph.isEnabled())
{
editor.graph.selection.clear();
}
});
this.addAction('selectVertices',function(editor){
if(editor.graph.isEnabled())
{
editor.graph.selectCells(true,false);
}
});
this.addAction('selectEdges',function(editor){
if(editor.graph.isEnabled())
{
editor.graph.selectCells(false,true);
}
});
this.addAction('edit',function(editor,cell){
if(editor.graph.isEnabled())
{
editor.graph.edit(cell);
}
});
this.addAction('goInto',function(editor,cell){
if(editor.graph.isEnabled())
{
editor.graph.goInto(cell);
}
});
this.addAction('goUp',function(editor){
if(editor.graph.isEnabled())
{
editor.graph.goUp();
}
});
this.addAction('home',function(editor){
if(editor.graph.isEnabled())
{
editor.graph.home();
}
});
this.addAction('selectPrevious',function(editor){
if(editor.graph.isEnabled())
{
editor.graph.select(false);
}
});
this.addAction('selectNext',function(editor){
if(editor.graph.isEnabled())
{
editor.graph.select(true);
}
});
this.addAction('selectParent',function(editor){
if(editor.graph.isEnabled())
{
editor.graph.select(false,true);
}
});
this.addAction('selectChild',function(editor){
if(editor.graph.isEnabled())
{
editor.graph.select(false,false,true);
}
});
this.addAction('collapse',function(editor){
if(editor.graph.isEnabled())
{
editor.graph.collapse();
}
});
this.addAction('expand',function(editor){
if(editor.graph.isEnabled())
{
editor.graph.expand();
}
});
this.addAction('bold',function(editor){
editor.graph.toggleCellStyleFlags(mxConstants.STYLE_FONTSTYLE,mxConstants.FONT_BOLD);
});
this.addAction('italic',function(editor){
editor.graph.toggleCellStyleFlags(mxConstants.STYLE_FONTSTYLE,mxConstants.FONT_ITALIC);
});
this.addAction('underline',function(editor){
editor.graph.toggleCellStyleFlags(mxConstants.STYLE_FONTSTYLE,mxConstants.FONT_UNDERLINE);
});
this.addAction('shadow',function(editor){
editor.graph.toggleCellStyleFlags(mxConstants.STYLE_FONTSTYLE,mxConstants.FONT_SHADOW);
});
this.addAction('alignCellsLeft',function(editor){editor.graph.alignCells(mxConstants.ALIGN_LEFT);});
this.addAction('alignCellsCenter',function(editor){editor.graph.alignCells(mxConstants.ALIGN_CENTER);});
this.addAction('alignCellsRight',function(editor){editor.graph.alignCells(mxConstants.ALIGN_RIGHT);});
this.addAction('alignCellsTop',function(editor){editor.graph.alignCells(mxConstants.ALIGN_TOP);});
this.addAction('alignCellsMiddle',function(editor){editor.graph.alignCells(mxConstants.ALIGN_MIDDLE);});
this.addAction('alignCellsBottom',function(editor){editor.graph.alignCells(mxConstants.ALIGN_BOTTOM);});
this.addAction('alignFontLeft',function(editor){editor.graph.setCellStyles(mxConstants.STYLE_ALIGN,mxConstants.ALIGN_LEFT);});
this.addAction('alignFontCenter',function(editor){editor.graph.setCellStyles(mxConstants.STYLE_ALIGN,mxConstants.ALIGN_CENTER);});
this.addAction('alignFontRight',function(editor){editor.graph.setCellStyles(mxConstants.STYLE_ALIGN,mxConstants.ALIGN_RIGHT);});
this.addAction('alignFontTop',function(editor){editor.graph.setCellStyles(mxConstants.STYLE_VERTICAL_ALIGN,mxConstants.ALIGN_TOP);});
this.addAction('alignFontMiddle',function(editor){editor.graph.setCellStyles(mxConstants.STYLE_VERTICAL_ALIGN,mxConstants.ALIGN_MIDDLE);});
this.addAction('alignFontBottom',function(editor){editor.graph.setCellStyles(mxConstants.STYLE_VERTICAL_ALIGN,mxConstants.ALIGN_BOTTOM);});
this.addAction('zoom',function(editor){
var current=editor.graph.view.scale*100;
var scale=parseFloat(prompt(mxResources.get('askZoom'),current))/100;
if(!isNaN(scale)){
editor.graph.view.setScale(scale);
}
});
this.addAction('toggleTasks',function(editor){
if(editor.tasks!=null){
editor.tasks.setVisible(!editor.tasks.isVisible());
}else{
editor.showTasks();
}
});
this.addAction('toggleHelp',function(editor){
if(editor.help!=null){
editor.help.setVisible(!editor.help.isVisible());
}else{
editor.showHelp();
}
});
this.addAction('toggleOutline',function(editor){
if(editor.outline!=null){
editor.outline.setVisible(!editor.outline.isVisible());
}else{
editor.showOutline();
}
});
this.addAction('toggleConsole',function(editor){
mxLog.setVisible(!mxLog.isVisible());
});
this.addAction('dump',function(editor){editor.dump();});
document.oncontextmenu=function(){
return false;
};

if(document.body!=null){
this.configure(config);
if(this.onInit!=null){
var tmp=document.cookie;
var isFirstTime=tmp.indexOf('mxgraph=seen')<0;
if(isFirstTime){
document.cookie=
'mxgraph=seen; expires=Fri, 27 Jul 2199 02:47:11 UTC; path=/';
}
this.onInit(isFirstTime);
}
}
}
mxResources.add(mxClient.basePath+'js/resources/editor');
mxEditor.prototype=new mxEventSource();
mxEditor.prototype.constructor=mxEditor;
mxEditor.prototype.linefeed='&#xa;';
mxEditor.prototype.urlHelp=null;
mxEditor.prototype.helpWindowImage=null;
mxEditor.prototype.tasksWindowImage=null;
mxEditor.prototype.isValidating=false;
mxEditor.prototype.isHorizontalFlow=false;
mxEditor.prototype.isLayoutDiagram=false;
mxEditor.prototype.swimlaneSpacing=0;
mxEditor.prototype.isLayoutSwimlane=false;
mxEditor.prototype.dblClickAction='edit';
mxEditor.prototype.isMaintainSwimlanes=false;
mxEditor.prototype.isForcedInserting=false;
mxEditor.prototype.isMovePropertiesDialog=false;
mxEditor.prototype.actions=new Array();
mxEditor.prototype.popupHandler=new mxDefaultPopupMenu();
mxEditor.prototype.undoManager=new mxUndoManager();
mxEditor.prototype.modified=false;
mxEditor.prototype.tasksTop=20;
mxEditor.prototype.helpWidth=300;
mxEditor.prototype.helpHeight=260;
mxEditor.prototype.isModalProperties=false;
mxEditor.prototype.propertiesWidth=240;
mxEditor.prototype.propertiesHeight=null;
mxEditor.prototype.defaultEdgeStyle=null;
mxEditor.prototype.isSwimlaneRequired=false;
mxEditor.prototype.isSelectSwimlane=true;
mxEditor.prototype.cycleAttributeValues=new Array();
mxEditor.prototype.cycleAttributeIndex=0;
mxEditor.prototype.cycleAttributeName='fillColor';
mxEditor.prototype.isAutoSave=false;
mxEditor.prototype.autoSaveDelay=10;
mxEditor.prototype.autoSaveThrottle=2;
mxEditor.prototype.autoSaveThreshold=5;
mxEditor.prototype.ignoredChanges=0;
mxEditor.prototype.lastSnapshot=0;
mxEditor.prototype.configure=function(filename){
if(filename!=null){
var xml=mxUtils.load(filename).getXML().documentElement;
if(xml!=null){
var baseFilename=xml.getAttribute('extend');
this.configure(baseFilename);
var dec=new mxCodec(xml.ownerDocument);
this.isConfiguring=true;
dec.decode(xml,this);
this.isConfiguring=null;
this.ignoredChanges=0;
this.lastSnapshot=new Date().getTime();
this.modified=false;
this.undoManager.reset();
}
}
}
mxEditor.prototype.resetFirstTime=function(){
document.cookie=
'mxgraph=seen; expires=Fri, 27 Jul 2001 02:47:11 UTC; path=/';
}
mxEditor.prototype.addAction=function(actionname,funct){
this.actions[actionname]=funct;
}
mxEditor.prototype.execute=function(actionname,cell){
var action=this.actions[actionname];
if(action!=null){
try{
action(this,cell);
}catch(err){
mxUtils.error('Cannot execute '+actionname+': '+err.message,280,true);
throw err;
}
}else{
mxUtils.error('Cannot find action '+actionname,280,true);
}
}
mxEditor.prototype.setGraphContainer=function(container){
this.graph=new mxGraph(container);
this.keyHandler=new mxDefaultKeyHandler(this);
var self=this;
this.graph.addListener('click',function(sender,evt,cell){
if(cell==null&&self.isSelectSwimlane&&!mxEvent.isConsumed(evt))
{
var pt=mxUtils.convertPoint(self.graph.container,evt.clientX,evt.clientY);
var swimlane=self.graph.getSwimlaneAt(pt.x,pt.y);
if(swimlane!=null){
self.graph.selectCellForEvent(swimlane,evt);
mxEvent.consume(evt);
}
}
});


this.graph.dblClick=function(evt,cell)
{
self.toolbar.toolbar.resetMode(true);
if(cell!=null&&self.graph.isEnabled())
{
self.execute(self.dblClickAction,cell);
}
self.dispatchEvent('dblclick',self,evt,cell);
}
var listener=function(sender,edit){
self.undoManager.undoableEditHappened(edit);
};
this.graph.getModel().addListener('undo',listener);
this.graph.view.addListener('undo',listener);

listener=function(sender){
self.dispatchEvent('root',self);
};
this.graph.view.addListener('down',listener);
this.graph.view.addListener('up',listener);

listener=function(sender,changes){
if(self.isValidating==true){
self.graph.validate();
}
self.modified=true;
if(!this.isConfiguring){
if(self.isAutoSave){
self.autosave(changes);
}
}
self.dispatchEvent('root',self);
};
this.graph.getModel().addListener('change',listener);
listener=function(sender,cell){
if(self.isMaintainSwimlanes&&self.graph.isSwimlane(cell))
{
var model=self.graph.getModel();
var parent=model.getParent(cell);
if(parent!=null){
var geo=model.getGeometry(cell);
var param=(self.isHorizontalFlow)?geo.width:geo.height;
var childCount=model.getChildCount(parent);
model.beginUpdate();
try
{
for(var i=0;i<childCount;i++){
var child=model.getChildAt(parent,i);
if(cell!=child&&self.graph.isSwimlane(child))
{
geo=model.getGeometry(child);
if(geo!=null){
geo=geo.clone();
if(self.isHorizontalFlow){
geo.width=param;
}else{
geo.height=param;
}
model.setGeometry(child,geo);
}
}
}
}
finally
{
model.endUpdate();
}
}
}
};
this.graph.addListener('resize',listener);




var offset=50;
listener=function(sender,cells){
for(var i=0;i<cells.length;i++)
{
var cell=cells[i];
if(self.isMaintainSwimlanes&&self.graph.isSwimlane(cell))
{
var model=self.graph.getModel();
var geo=model.getGeometry(cell);
var param=(self.isHorizontalFlow)?geo.width:geo.height;
if(param==null||param==0){
param=(self.isHorizontalFlow)?self.graph.container.offsetWidth-10:
self.graph.container.offsetHeight;
param-=offset;
}
var parent=self.graph.getDefaultParent();
var childCount=model.getChildCount(parent);
for(var i=0;i<childCount;i++){
var child=model.getChildAt(parent,i);
geo=model.getGeometry(child);
if(cell!=child&&self.graph.isSwimlane(child))
{
param=(self.isHorizontalFlow)?geo.width:geo.height;
}
}
geo=model.getGeometry(cell);
if(geo!=null){
if(self.isHorizontalFlow){
geo.width=param;
}else{
geo.height=param;
}
}
}
}
};
this.graph.addListener('add',listener);
this.graph.getLayout=function(cell){
var layout=null;
if(self.isLayoutSwimlane&&this.isSwimlane(cell)){
if(self.swimlaneLayout==null){
self.swimlaneLayout=self.createSwimlaneLayout();
}
layout=self.swimlaneLayout;
}else if(self.isLayoutDiagram&&(self.graph.isValidRoot(cell)||self.graph.getModel().getParent(self.graph.getModel().getParent(cell))==null))
{
if(self.diagramLayout==null){
self.diagramLayout=self.createDiagramLayout();
}
layout=self.diagramLayout;
}
return layout;
}
this.graph.setTooltips(true);
this.graph.setPanning(true);


if(this.isModalProperties){
this.graph.isEditing=function(cell){
return(this.editor!=null&&this.editor.isEditing(cell))||self.isPropertiesVisible();
}

}else{
this.keyHandler.handler.isEnabled=function(){
return self.keyHandler.handler.enabled&&!self.isPropertiesVisible();
}
}
if(mxClient.IS_IE){
new mxDivResizer(container);
document.onmousemove=function(){
if(self.graph.isEnabled()&&!self.graph.isEditing()&&!self.isPropertiesVisible())
{
document.selection.empty();
}
}
}
}
mxEditor.prototype.createDiagramLayout=function(){
var gs=this.graph.gridSize;
return new mxFlowLayout(this.graph,this.swimlaneSpacing,this.isHorizontalFlow,2*gs,2*gs);
}
mxEditor.prototype.createSwimlaneLayout=function(){
return new mxCompactTreeLayout(this.graph,this.isHorizontalFlow);
}
mxEditor.prototype.setToolbarContainer=function(container){
this.toolbar=new mxDefaultToolbar(container,this);
new mxRubberband(this.graph);
var self=this;
this.graph.panningHandler.factoryMethod=function(menu,cell,evt){
return self.createPopupMenu(menu,cell,evt);
};
this.graph.connectionHandler.factoryMethod=function(source,target){
return self.createEdge(source,target)
};
if(mxClient.IS_IE){
new mxDivResizer(container);
}
}
mxEditor.prototype.setStatusContainer=function(container){
if(mxClient.IS_IE){
new mxDivResizer(container);
}
this.addListener('save',function(sender){
var tstamp=new Date().toLocaleString();
container.innerHTML=mxResources.get('lastSaved')+': '+tstamp;
});

var self=this;
this.addListener('open',function(sender){
container.innerHTML=mxResources.get('currentFile')+': '+self.filename;
});
this.status=container;
}
mxEditor.prototype.setStatus=function(message){
if(this.status!=null){
this.status.innerHTML=message;
}
}
mxEditor.prototype.setMapContainer=function(tmp){
if(tmp!=null){
var fx=1/6000;
var fy=1/7000;
var map=new GMap2(tmp);
map.setCenter(new GLatLng(37.4419,-122.1419),13);
var listener=function(evt){
var dx=graph.view.translate.x*fx;
var dy=graph.view.translate.y*fy;
var s=Math.floor(5+8*graph.view.scale);
mxLog.debug('property changes: s='+s);
if(map.getCenter()!=s){
map.setCenter(new GLatLng(37.4569+dy,-122.1569-dx),s);
}else{
map.panTo(new GLatLng(37.4569+dy,-122.1569-dx),s);
}
}
graph.shift=function(dx,dy){
var canvas=this.view.getCanvas();
if(this.dialect!=mxConstants.DIALECT_SVG){
canvas.setAttribute('coordorigin',(-dx)+','+(-dy));
}else{
canvas.setAttribute('transform','translate('+dx+','+dy+')');
}
dx*=fx;
dy*=fy;
map.panTo(new GLatLng(37.4569+dy,-122.1569-dx));
}
editor.graph.view.addListener(null,listener);
}
}
mxEditor.prototype.setTitleContainer=function(container){
var self=this;
this.addListener('root',function(sender){
container.innerHTML=self.getTitle();
});
if(mxClient.IS_IE){
new mxDivResizer(container);
}
}
mxEditor.prototype.treeLayout=function(cell,isHorizontal){
if(cell!=null){
var layout=new mxCompactTreeLayout(this.graph,isHorizontal);
layout.execute(cell);
}
}
mxEditor.prototype.getTitle=function(){
var title='';
var graph=this.graph;
var cell=graph.getCurrentRoot();
while(cell!=null&&graph.getModel().getParent(graph.getModel().getParent(cell))!=null)
{
if(graph.isValidRoot(cell))
{
title=' > '+graph.convertValueToString(cell)+title;
}
cell=graph.getModel().getParent(cell);
}
var prefix=this.getRootTitle();
return prefix+title;
}
mxEditor.prototype.getRootTitle=function(){
var root=this.graph.getModel().getRoot();
return this.graph.convertValueToString(root);
}
mxEditor.prototype.undo=function(){
this.undoManager.undo();
}
mxEditor.prototype.redo=function(){
this.undoManager.redo();
}
mxEditor.prototype.group=function(){
this.graph.group(this.createGroup(),this.graph.gridSize);
}
mxEditor.prototype.open=function(filename){
if(filename!=null){
try{
var xml=mxUtils.load(filename).getXML();
this.readGraphModel(xml.documentElement);
this.filename=filename;
this.dispatchEvent('open',this);
}catch(e){
mxUtils.error('Cannot open '+filename+': '+e.message,280,true);
throw e;
}
}
}
mxEditor.prototype.readGraphModel=function(node){
var dec=new mxCodec(node.ownerDocument);
dec.decode(node,this.graph.getModel());
this.lastSnapshot=new Date().getTime();
this.ignoredChanges=0;
this.modified=false;
this.undoManager.reset();
}
mxEditor.prototype.save=function(isAutomatic,linefeed){
if(isAutomatic==null||isAutomatic==this.isAutoSave){
try{
var xml=mxUtils.getXml(this.writeGraphModel(),this.linefeed);
if(this.urlPost!=null&&this.urlPost.length>0){
var url=this.urlPost;
if(isAutomatic){
url+='?draft=true';
}
mxUtils.post(url,'xml='+xml);
}else if(!isAutomatic){


mxUtils.popup(xml);

}
this.dispatchEvent('save',this);
}catch(e){
}
}
}
mxEditor.prototype.writeGraphModel=function(){
var enc=new mxCodec(mxUtils.createXmlDocument());
var node=enc.encode(this.graph.getModel());
this.lastSnapshot=new Date().getTime();
this.ignoredChanges=0;
this.modified=false;
return node;
}
mxEditor.prototype.autosave=function(changes){
var now=new Date().getTime();
var dt=(now-this.lastSnapshot)/1000;
if(dt>this.autoSaveDelay||(this.ignoredChanges>=this.autoSaveThreshold&&dt>this.autoSaveThrottle))
{
this.lastSnapshot=now;
this.ignoredChanges=1;
this.save(true);
}else{
this.ignoredChanges++;
}
}
mxEditor.prototype.connect=function(urlInit,urlPoll,urlPost,onChange){
var session=null;
if(!mxClient.IS_LOCAL){
var session=new mxSession(this.graph.getModel(),urlInit,urlPoll,urlPost);


var self=this;
session.addListener('receive',function(sender,node){
if(node.nodeName=='mxGraphModel'){
self.readGraphModel(node);
}
});
session.addListener('get',sender,onChange);
session.addListener('post',sender,onChange);
session.addListener('connect',sender,onChange);
session.addListener('disconnect',sender,onChange);
session.start();
}
return session;
}
mxEditor.prototype.swapStyles=function(first,second){
var style=this.graph.stylesheet.styles[second];
this.graph.view.stylesheet.putCellStyle(second,this.graph.stylesheet.styles[first]);
this.graph.stylesheet.putCellStyle(first,style);
this.graph.refresh();
}
mxEditor.prototype.showProperties=function(cell){
if(cell==null&&!this.graph.isSelectionEmpty()){
cell=this.graph.getSelectionCell();
}else if(cell==null){
cell=this.graph.getCurrentRoot();
if(cell==null){
cell=this.graph.getModel().getRoot();
}
}
if(cell!=null){
this.graph.editor.stopEditing();
var offset=mxUtils.getOffset(this.graph.container);
var x=offset.x+10;
var y=offset.y;
if(this.properties!=null&&!this.isMovePropertiesDialog)
{
x=this.properties.getX();
y=this.properties.getY();
}
else
{
var bounds=this.graph.getCellBounds(cell);
if(bounds!=null){
x+=bounds.x+Math.min(200,bounds.width);
y+=bounds.y;
}
}
this.hideProperties();
var node=this.createProperties(cell);
if(node!=null){
this.properties=new mxWindow(mxResources.get('properties'),node,x,y,this.propertiesWidth,this.propertiesHeight,false);
this.properties.setVisible(true);
}
}
}
mxEditor.prototype.hideProperties=function(){
if(this.properties!=null){
this.properties.destroy();
this.properties=null;
}
}
mxEditor.prototype.showTasks=function(tasks){
if(this.tasks==null){
var div=document.createElement('div');
div.style.padding='4px';
div.style.paddingLeft='20px';
var w=document.body.clientWidth;
var wnd=new mxWindow(mxResources.get('tasks'),div,w-220,this.tasksTop,200);
wnd.setCloseAction(function(){
wnd.setVisible(false);
});
var self=this;
var funct=function(sender){
div.innerHTML='';
self.createTasks(div);
};
this.graph.addListener('select',funct);
this.graph.getModel().addListener('change',funct);
this.graph.addListener('root',funct);
if(this.tasksWindowImage!=null){
wnd.setImage(this.tasksWindowImage);
}
this.tasks=wnd;
this.createTasks(div);
}
this.tasks.setVisible(true);
}
mxEditor.prototype.createTasks=function(div){
}
mxEditor.prototype.showHelp=function(tasks){
if(this.help==null){
var frame=document.createElement('iframe');
frame.setAttribute('width',(this.helpWidth-8)+'px');
frame.setAttribute('height',(this.helpHeight-28)+'px');
frame.setAttribute('src',mxResources.get('urlHelp')||this.urlHelp);
frame.style.backgroundColor='white';
var w=document.body.clientWidth;
var h=document.body.clientHeight;
var wnd=new mxWindow(mxResources.get('help'),frame,(w-this.helpWidth)/2,(h-this.helpHeight)/3,this.helpWidth,this.helpHeight);
wnd.setCloseAction(function(){
wnd.setVisible(false);
});
if(this.helpWindowImage!=null){
wnd.setImage(this.helpWindowImage);
}
this.help=wnd;
}
this.help.setVisible(true);
}
mxEditor.prototype.showOutline=function(){
if(this.outline==null){
var div=document.createElement('div');
div.style.width='196px';
div.style.height='172px';
div.style.background='white';
div.style.overflow='hidden';
var wnd=new mxWindow(mxResources.get('outline'),div,600,480,200,200);
wnd.setCloseAction(function(){
wnd.setVisible(false);
});
wnd.setVisible(true);
var outline=new mxOutline(this.graph,div);
new mxOutlineHandler(outline);
this.outline=wnd;
}
this.outline.setVisible(true);
}
mxEditor.prototype.isPropertiesVisible=function(){
return this.properties!=null;
}
mxEditor.prototype.createProperties=function(cell){
var value=this.graph.getModel().getValue(cell);
if(value.nodeType!=null){
var form=new mxForm('properties');
var id=form.addText('ID',cell.getId());
id.setAttribute('readonly','true');
var tmp=this.graph.getModel().getStyle(cell);
var style=form.addText('Style',tmp||'');
var attrs=value.attributes;
var texts=new Array(attrs.length);
for(var i=0;i<attrs.length;i++){
var val=attrs[i].nodeValue;


texts[i]=form.addTextarea(attrs[i].nodeName,val,(attrs[i].nodeName=='label')?4:2);
}
var self=this;
form.addButtons(
function(){
self.hideProperties();
var model=self.graph.getModel();

model.beginUpdate();
try
{
if(style.value.length>0){
model.setStyle(cell,style.value);
}else{
model.setStyle(cell,null);
}
for(var i=0;i<attrs.length;i++){
var edit=new mxNodeAttributeChange(value,attrs[i].nodeName,texts[i].value);
model.execute(edit);
}
if(self.graph.isUpdateSize(cell)){
self.graph.updateSize(cell);
}
model.setValue(cell,value);
}
finally
{
model.endUpdate();
}
},
function(){
self.hideProperties();
});
return form.table;
}
return null;
}
mxEditor.prototype.createPopupMenu=function(menu,cell,evt){
this.popupHandler.createMenu(this,menu,cell,evt);
}
mxEditor.prototype.createEdge=function(source,target)
{
var model=this.graph.getModel();
var e=model.cloneCell(model.defaultEdge);
if(e.getGeometry()==null)
{
e.setGeometry(new mxGeometry());
}
var style=this.getEdgeStyle();
if(style!=null)
{
e.setStyle(style);
}
return e;
}
mxEditor.prototype.getEdgeStyle=function(){
return this.defaultEdgeStyle;
}
mxEditor.prototype.createGroup=function(){
var model=this.graph.getModel();
return model.cloneCell(model.defaultGroup);
}
mxEditor.prototype.consumeCycleAttribute=function(cell){
return(this.cycleAttributeValues.length>0&&this.graph.isSwimlane(cell))?this.cycleAttributeValues[this.cycleAttributeIndex++%this.cycleAttributeValues.length]:null;
}
mxEditor.prototype.cycleAttribute=function(cell){
var value=this.consumeCycleAttribute(cell);
if(value!=null){
cell.setStyle(cell.getStyle()+';'+this.cycleAttributeName+'='+value);
}
}
mxEditor.prototype.addVertex=function(parent,vertex,x,y){
var model=this.graph.getModel();
while(parent!=null&&!this.graph.isValidDropTarget(parent)){
parent=model.getParent(parent);
}
parent=(parent!=null)?parent:this.graph.getSwimlaneAt(x,y);
var scale=this.graph.view.scale;
var geo=model.getGeometry(vertex);
var pgeo=model.getGeometry(parent);
if(!this.graph.isSwimlane(vertex)){
if(parent==null&&this.isSwimlaneRequired){
return null;
}else if(parent!=null&&pgeo!=null){
var state=this.graph.view.getState(parent);
if(state!=null){
x-=state.origin.x*scale;
y-=state.origin.y*scale;
if(this.graph.isConstrainedMoving){
var width=geo.width;
var height=geo.height;
var tmp=state.x+state.width;
if(x+width>tmp){
x-=x+width-tmp;
}
tmp=state.y+state.height;
if(y+height>tmp){
y-=y+height-tmp;
}
}
}else{
if(pgeo!=null){
x-=pgeo.x*scale;
y-=pgeo.y*scale;
}
}
}
}else if(this.isSelectSwimlane&&parent!=null){
if(!this.graph.isSwimlaneNesting){
parent=null;
}
}
geo=geo.clone();
geo.x=this.graph.snap(x/scale-this.graph.view.translate.x-this.graph.gridSize/2);
geo.y=this.graph.snap(y/scale-this.graph.view.translate.y-this.graph.gridSize/2);
vertex.setGeometry(geo);
if(parent==null){
parent=this.graph.getDefaultParent();
}
var array=new Array();
array.push(vertex);
this.cycleAttribute(vertex);
model.beginUpdate();
try
{
this.graph.addCell(vertex,parent);
this.graph.keepInside(array);
}
finally
{
model.endUpdate();
}
this.graph.setSelectionCell(vertex);
this.graph.scrollCellToVisible(vertex);
return vertex;
}
mxEditor.prototype.dump=function(cell){
var model=this.graph.getModel();
if(cell==null){
mxLog.debug('Dumping graph model:');
cell=model.getRoot();
}
mxLog.debug('cell='+cell.getId());
var childCount=model.getChildCount(cell);
if(childCount>0){
mxLog.debug('{');
for(var i=0;i<childCount;i++){
this.dump(model.getChildAt(cell,i));
}
mxLog.debug('}');
}
}
mxEditor.prototype.dumpCell=function(cell){
cells=cells||this.graph.getSelectionCells();
mxLog.debug('Dumping '+cells.length+' cell(s)');
for(var i=0;i<cells.length;i++){
mxLog.debug(i+'. Label: '+this.graph.convertValueToString(cells[i]));
mxLog.debug(i+'. IsRoot: '+(this.graph.getModel().isRoot(cells[i])));
mxLog.debug(i+'. Parent: '+cells[i].parent);
mxLog.debug(i+'. IsVertex: '+cells[i].isVertex());
mxLog.debug(i+'. IsSwimlane: '+this.graph.isSwimlane(cells[i]));
var geo=this.graph.getModel().getGeometry(cells[i]);
if(geo!=null){
mxLog.debug(i+'.geometry.x: '+geo.x);
mxLog.debug(i+'.geometry.y: '+geo.y);
mxLog.debug(i+'.geometry.width: '+geo.width);
mxLog.debug(i+'.geometry.height: '+geo.height);
}else{
mxLog.debug(i+'.geometry: null');
}
}
}

function mxPropertyChange(obj,property,value){
this.obj=obj;
this.property=property;
this.value=value;
this.previous=value;
}
mxPropertyChange.prototype.execute=function(){
var tmp=this.obj[this.property];
this.obj[this.property]=this.previous;
this.previous=tmp;
}
function mxNodeAttributeChange(node,attribute,value){
this.node=node;
this.attribute=attribute;
this.value=value;
this.previous=value;
}
mxNodeAttributeChange.prototype.execute=function(){
var tmp=this.node.getAttribute(this.attribute);
if(this.previous==null){
this.node.removeAttribute(this.attribute);
}else{
this.node.setAttribute(this.attribute,this.previous);
}
this.previous=tmp;
}
function mxNodeChildChange(node,child,isRemove){
this.node=node;
this.child=child;
this.isRemove=(isRemove!=null)?isRemove:false;
}
mxNodeChildChange.prototype.execute=function(){
if(this.isRemove){
if(this.child.parentNode!=null){
this.child.parentNode.removeChild(this.child);
}
}else if(this.child.parentNode==null){
this.node.appendChild(this.child);
}
this.isRemove=!this.isRemove;
}
function mxNodeReplaceChange(node,oldChild,newChild){
this.node=node;
this.oldChild=oldChild;
this.newChild=newChild;
}
mxNodeReplaceChange.prototype.execute=function(){
if(this.newChild!=null){
if(this.oldChild!=null){
this.node.insertBefore(this.newChild,this.oldChild);
}else{
this.node.appendChild(this.newChild);
}
}
if(this.oldChild!=null&&this.oldChild.parentNode==this.node){
this.node.removeChild(this.oldChild);
}
var tmp=this.newChild;
this.newChild=this.oldChild;
this.oldChild=tmp;
}
function mxNodeOrderChange(node,child,before){
this.node=node;
this.child=child;
this.before=before;
}
mxNodeOrderChange.prototype.execute=function(){
var tmp=this.child.nextSibling;
if(this.before!=null){
this.node.insertBefore(this.child,this.before);
}else{
this.node.appendChild(this.child);
}
this.before=tmp;
}
function mxNodeReplaceChildren(parent,holder){
this.parent=parent;
this.holder=holder;
}
mxNodeReplaceChildren.prototype.execute=function(){
var previous=this.parent.cloneNode(true);
while(this.parent.firstChild!=null){
this.parent.removeChild(this.parent.firstChild);
}
if(this.holder!=null){
var tmp=this.holder.cloneNode(true);
while(tmp.firstChild!=null){
this.parent.appendChild(tmp.firstChild);
}
}
this.holder=previous;
}
function mxRemoveNode(node){
this.parent=null;
this.node=node;
}
mxRemoveNode.prototype.execute=function(){
if(this.parent==null){
this.parent=this.node.parentNode;
this.parent.removeChild(this.node);
}else{
this.parent.appendChild(this.node);
this.parent=null;
}
}
}

var mxCodecRegistry=
{
codecs:new Array(),
register:function(codec)
{
var name=mxUtils.getFunctionName(codec.template.constructor);
mxCodecRegistry.codecs[name]=codec;
},
getCodec:function(ctor)
{
var name=mxUtils.getFunctionName(ctor);
var codec=mxCodecRegistry.codecs[name];

if(codec==null)
{
codec=new mxObjectCodec(new ctor());
mxCodecRegistry.register(codec);
}
return codec;
}
}

{
function mxCodec(document)
{
this.document=document||mxUtils.createXmlDocument();
this.objects=new Array();
}
mxCodec.prototype.document=null;
mxCodec.prototype.objects=null;
mxCodec.prototype.isEncodeDefaults=false;
mxCodec.prototype.getObject=function(id)
{
var obj=null;
if(id!=null)
{
obj=this.objects[id];
if(obj==null)
{
obj=this.lookup(id);
if(obj==null)
{
var node=this.getElementById(id);
if(node!=null)
{
obj=this.decode(node);
}
}
}
}
return obj;
}
mxCodec.prototype.lookup=function(id)
{
return null;
}
mxCodec.prototype.getElementById=function(id,attr)
{
attr=attr||'id';
var expr='//*[@'+attr+'=\''+id+'\']';
return mxUtils.selectSingleNode(this.document,expr);
}
mxCodec.prototype.getId=function(obj)
{
var id=null;
if(obj!=null&&obj.constructor==mxCell)
{
id=this.reference(obj);
if(id==null)
{
id=obj.getId();
if(id==null)
{
id=mxCellPath.create(obj);
if(id.length==0)
{
id='root';
}
}
}
}
return id;
}
mxCodec.prototype.reference=function(obj)
{
return null;
}
mxCodec.prototype.encode=function(obj)
{
var node=null;
if(obj!=null&&obj.constructor!=null)
{
var enc=mxCodecRegistry.getCodec(obj.constructor);
node=enc.encode(this,obj);
}
return node;
}
mxCodec.prototype.decode=function(node,into)
{
var obj=null;
if(node!=null&&node.nodeType==1)
{
var ctor=null;
try
{
var ctor=eval(node.nodeName);
}
catch(err)
{
}
if(ctor!=null)
{
try
{
var dec=mxCodecRegistry.getCodec(ctor);
obj=dec.decode(this,node,into);
}
catch(err)
{
mxLog.debug('Cannot decode '+node.nodeName+': '+err);
throw err;
}
}
}
return obj;
}
mxCodec.prototype.encodeCell=function(cell,node,isIncludeChildren)
{
node.appendChild(this.encode(cell));
if(isIncludeChildren==null||isIncludeChildren)
{
var childCount=cell.getChildCount();
for(var i=0;i<childCount;i++)
{
this.encodeCell(cell.getChildAt(i),node);
}
}
}
mxCodec.prototype.decodeCell=function(node,isRestoreStructures)
{
var cell=null;
if(node!=null&&node.nodeType==1)
{


var decoder=mxCodecRegistry.getCodec(mxCell);
cell=decoder.decode(this,node);
if(isRestoreStructures==null||isRestoreStructures==true)
{
var parent=cell.getParent();
if(parent!=null)
{
parent.insert(cell);
}
var source=cell.getTerminal(true);
if(source!=null)
{
source.insertEdge(cell,true);
}
var target=cell.getTerminal(false);
if(target!=null)
{
target.insertEdge(cell,false);
}
}
}
return cell;
}
mxCodec.prototype.setAttribute=function(node,attribute,value)
{
if(attribute!=null&&value!=null)
{
node.setAttribute(attribute,value);
}
}
}

{
function mxObjectCodec(template,exclude,idrefs,mapping)
{
this.template=template;
this.exclude=exclude||new Array();
this.idrefs=idrefs||new Array();
this.mapping=mapping||new Object();
this.reverse=new Object();
for(var i in this.mapping)
{
this.reverse[this.mapping[i]]=i;
}
}
mxObjectCodec.prototype.exclude=null;
mxObjectCodec.prototype.idrefs=null;
mxObjectCodec.prototype.mapping=null;
mxObjectCodec.prototype.isExcluded=function(obj,attr,value,isWrite)
{
return mxUtils.indexOf(this.exclude,attr)>=0;
}
mxObjectCodec.prototype.isReference=function(obj,attr,value,isWrite)
{
return mxUtils.indexOf(this.idrefs,attr)>=0;
}
mxObjectCodec.prototype.encode=function(enc,obj)
{
var name=mxUtils.getFunctionName(obj.constructor);
var node=enc.document.createElement(name);
obj=this.beforeEncode(enc,obj,node);
enc.setAttribute(node,'id',enc.getId(obj));
var isArray=name=='Array';
for(var i in obj)
{
var value=obj[i];
if(value!=null&&!this.isExcluded(obj,i,value,true))
{
if(this.isReference(obj,i,value,true))
{
value=enc.getId(obj[i]);
if(value==null)
{
mxLog.warn('mxObjectCodec.encode: No ID for '+name+'.'+i+'='+obj[i]);
}
}
if(value!=null)
{
var attr=i;
var mapped=this.mapping[i];
if(mapped!=null)
{
attr=mapped;
}
var isImplicit=isArray&&mxUtils.isNumeric(attr);
var type=typeof(value);
if(type=='object')
{
var child=enc.encode(value);
if(child!=null)
{
if(!isImplicit)
{
child.setAttribute('as',i);
}
node.appendChild(child);
}
else
{
mxLog.warn('mxObjectCodec.encode: No node for '+name+'.'+i+': '+value);
}
}
else if(enc.isEncodeDefaults||this.template[i]!=value)
{
if(value==true||value==false)
{


value=(value==true)?'1':'0';
}
if(isArray)
{
var element=enc.document.createElement('add');
if(type=='function')
{
element.appendChild(enc.document.createTextNode(value));
}
else
{
enc.setAttribute(element,'value',value);
}
if(!isImplicit)
{
element.setAttribute('as',i);
}
node.appendChild(element);
}
else if(type!='function')
{
enc.setAttribute(node,attr,value);
}
}
}
}
}
return this.afterEncode(enc,obj,node);
}
mxObjectCodec.prototype.beforeEncode=function(enc,obj,node)
{
return obj;
}
mxObjectCodec.prototype.afterEncode=function(enc,obj,node)
{
return node;
}
mxObjectCodec.prototype.decode=function(dec,node,into)
{
var id=node.getAttribute('id');
var obj=dec.objects[id]||dec.lookup(id);
if(obj==null)
{
obj=into||new this.template.constructor();
if(id!=null)
{
dec.objects[id]=obj;
}
}
node=this.beforeDecode(dec,node,obj);
if(node!=null)
{
var type=mxUtils.getFunctionName(obj.constructor);
var isArray=type=='Array';
var attrs=node.attributes;
if(attrs!=null)
{
for(var i=0;i<attrs.length;i++)
{
var name=attrs[i].nodeName;
if(name!='as'&&name!='id')
{
var value=attrs[i].nodeValue;
if(this.isReference(obj,name,value,false))
{
var tmp=dec.getObject(value);
if(tmp==null)
{
mxLog.warn('mxObjectCodec.decode: No object for '+type+'.'+name+'='+value);
}
value=tmp;
}

if(value=='true'||value=='false'||value=='0'||value=='1')
{
value=value=='1'||value=='true';
value=(value==true)?1:0;

}
else if(mxUtils.isNumeric(value))
{
value=parseFloat(value);
}
var mapped=this.reverse[name];
if(mapped!=null)
{
name=mapped;
}
if(!this.isExcluded(obj,name,value,false))
{
obj[name]=value;
}
}
}
}
var child=node.firstChild;
while(child!=null)
{
if(child.nodeType==1)
{
if(!this.processInclude(dec,child,obj))
{
var role=child.getAttribute('as');
if(isArray||(role!=null&&!this.isExcluded(obj,role,child,false)))
{
var template=(into!=null&&role!=null)?into[role]:null;
var tmp=null;
if(isArray&&child.nodeName=='add')
{
tmp=child.getAttribute('value');
if(tmp==null)
{
tmp=mxUtils.eval(mxUtils.getTextContent(child));
}
}
else if(child.nodeName=='add')
{
tmp=mxUtils.eval(mxUtils.getTextContent(child));
}
else{
tmp=dec.decode(child,template);

}
if(tmp!=null&&tmp!=template)
{
if(role!=null)
{
obj[role]=tmp;
}
else
{
obj.push(tmp);
}
}
}
}
}
child=child.nextSibling;
}
}
return this.afterDecode(dec,node,obj);
}
mxObjectCodec.prototype.processInclude=function(dec,node,into)
{
if(node.nodeType==1&&node.nodeName=='include')
{
var name=node.getAttribute('name');
if(name!=null)
{
var xml=mxUtils.load(name).getXML().documentElement;
if(xml!=null)
{
dec.decode(xml,into);
}
}
return true;
}
return false;
}
mxObjectCodec.prototype.beforeDecode=function(dec,node,obj)
{
return node;
}
mxObjectCodec.prototype.afterDecode=function(dec,node,obj)
{
return obj;
}
}

mxCodecRegistry.register(function()
{
var codec=new mxObjectCodec(new mxCell(),['children','edges','states','overlay','transient'],['parent','source','target']);
codec.isExcluded=function(obj,attr,value,isWrite)
{
return mxUtils.indexOf(this.exclude,attr)>=0||(isWrite&&attr=='value'&&value.nodeType==1);
};
codec.afterEncode=function(enc,obj,node)
{
if(obj.value!=null)
{
if(obj.value.nodeType==1)
{




var tmp=node;
node=obj.value.cloneNode(true);
node.appendChild(tmp);


var id=tmp.getAttribute('id');
node.setAttribute('id',id);
tmp.removeAttribute('id');
}
}
return node;
};
codec.beforeDecode=function(dec,node,obj)
{
var inner=node;
if(node.nodeName!='mxCell')
{

var tmp=node.getElementsByTagName('mxCell')[0];
if(tmp!=null&&tmp.parentNode==node)
{
inner=tmp;
var tmp2=tmp.previousSibling;
while(tmp2!=null&&tmp2.nodeType==3)
{
var tmp3=tmp2.previousSibling;
tmp2.parentNode.removeChild(tmp2);
tmp2=tmp3;
}
tmp2=tmp.nextSibling;
while(tmp2!=null&&tmp2.nodeType==3)
{
var tmp3=tmp2.previousSibling;
tmp2.parentNode.removeChild(tmp2);
tmp2=tmp3;
}
tmp.parentNode.removeChild(tmp);
}
else
{
inner=null;
}
obj.value=node.cloneNode(true);
var id=obj.value.getAttribute('id');
if(id!=null)
{
obj.setId(id);
obj.value.removeAttribute('id');
}
}


if(inner!=null)
{
for(var i=0;i<this.idrefs.length;i++)
{
var attr=this.idrefs[i];
var ref=inner.getAttribute(attr);
if(ref!=null)
{
inner.removeAttribute(attr);
var object=dec.objects[ref]||dec.lookup(ref);
if(object==null)
{
var element=dec.getElementById(ref);
if(element!=null)
{
var decoder=mxCodecRegistry.codecs[element.nodeName]||this;
object=decoder.decode(dec,element);
}
}
obj[attr]=object;
}
}
}
return inner;
};
return codec;
}());

mxCodecRegistry.register(function()
{
var codec=new mxObjectCodec(new mxGraphModel());
codec.encode=function(enc,obj)
{
var name=mxUtils.getFunctionName(obj.constructor);
var node=enc.document.createElement(name);
var rootNode=enc.document.createElement('root');
enc.encodeCell(obj.getRoot(),rootNode);
node.appendChild(rootNode);
return node;
};
codec.beforeDecode=function(dec,node,into)
{
into=into||new mxGraphModel();

var root=node.getElementsByTagName('root')[0];
var rootCell=null;
if(root!=null)
{
var tmp=root.firstChild;
while(tmp!=null)
{
var cell=dec.decodeCell(tmp);
if(cell!=null&&cell.getParent()==null)
{
rootCell=cell;
}
tmp=tmp.nextSibling;
}
root.parentNode.removeChild(root);
}
var arrays=node.getElementsByTagName('Array');
for(var i=0;i<arrays.length;i++)
{
var arr=arrays[i];
var role=arr.getAttribute('as');
if(role=='templates')
{
this.decodeTemplates(dec,arr,into);
arr.parentNode.removeChild(arr);
}
}
var defaultEdge=node.getAttribute('defaultEdge');
if(defaultEdge!=null)
{
node.removeAttribute('defaultEdge');
into.defaultEdge=into.templates[defaultEdge];
}
var defaultGroup=node.getAttribute('defaultGroup');
if(defaultGroup!=null)
{
node.removeAttribute('defaultGroup');
into.defaultGroup=into.templates[defaultGroup];
}
if(rootCell!=null)
{
into.setRoot(rootCell);
}
return node;
};
codec.decodeTemplates=function(dec,node,model)
{
if(model.templates==null)
{
model.templates=new Array();
}
var children=mxUtils.getChildNodes(node);
for(var j=0;j<children.length;j++)
{
var name=children[j].getAttribute('as');
var child=children[j].firstChild;
while(child!=null&&child.nodeType!=1)
{
child=child.nextSibling;
}
if(child!=null)
{




model.templates[name]=dec.decodeCell(child);
}
}
};
return codec;
}());

mxCodecRegistry.register(function()
{
var codec=new mxObjectCodec(new mxRootChange(),['model','previous','root']);
codec.afterEncode=function(enc,obj,node)
{
enc.encodeCell(obj.root,node);
return node;
};
codec.beforeDecode=function(dec,node,obj)
{
if(node.firstChild!=null&&node.firstChild.nodeType==1)
{
var tmp=node.firstChild;
obj.root=dec.decodeCell(tmp,false);
var tmp2=tmp.nextSibling;
tmp.parentNode.removeChild(tmp);
tmp=tmp2;
while(tmp!=null)
{
var tmp2=tmp.nextSibling;
dec.decodeCell(tmp);
tmp.parentNode.removeChild(tmp);
tmp=tmp2;
}
}
return node;
};
codec.afterDecode=function(dec,node,obj)
{
obj.previous=obj.root;
return obj;
};
return codec;
}());

mxCodecRegistry.register(function()
{
var codec=new mxObjectCodec(new mxChildChange(),['model','previous','previousIndex','child'],['parent']);
codec.isReference=function(obj,attr,value,isWrite)
{
if(attr=='child'&&(obj.previous!=null||!isWrite))
{
return true;
}
return mxUtils.indexOf(this.idrefs,attr)>=0;
};
codec.afterEncode=function(enc,obj,node)
{
if(this.isReference(obj,'child',obj.child,true))
{
node.setAttribute('child',enc.getId(obj.child));
}
else
{






enc.encodeCell(obj.child,node);
}
return node;
};
codec.beforeDecode=function(dec,node,obj)
{
if(node.firstChild!=null&&node.firstChild.nodeType==1)
{
var tmp=node.firstChild;
obj.child=dec.decodeCell(tmp,false);




obj.child.setParent(null);
var tmp2=tmp.nextSibling;
tmp.parentNode.removeChild(tmp);
tmp=tmp2;
while(tmp!=null)
{
var tmp2=tmp.nextSibling;
if(tmp.nodeType==1)
{






var id=tmp.getAttribute('id');
if(dec.lookup(id)==null)
{
dec.decodeCell(tmp);
}
}
tmp.parentNode.removeChild(tmp);
tmp=tmp2;
}
}
else
{
var childRef=node.getAttribute('child');
obj.child=dec.getObject(childRef);
}
return node;
};
codec.afterDecode=function(dec,node,obj)
{
obj.previous=obj.parent;
obj.previousIndex=obj.index;
return obj;
};
return codec;
}());

mxCodecRegistry.register(function()
{
var codec=new mxObjectCodec(new mxTerminalChange(),['model','previous'],['cell','terminal']);
codec.afterDecode=function(dec,node,obj)
{
obj.previous=obj.terminal;
return obj;
};
return codec;
}());

{
var mxGenericChangeCodec=function(obj,variable)
{
var codec=new mxObjectCodec(obj,['model','previous'],['cell']);
codec.afterDecode=function(dec,node,obj)
{
if(obj.previous==null)
{
obj.previous=obj[variable];
}
return obj;
}
return codec;
};
mxCodecRegistry.register(mxGenericChangeCodec(new mxValueChange(),'value'));
mxCodecRegistry.register(mxGenericChangeCodec(new mxStyleChange(),'style'));
mxCodecRegistry.register(mxGenericChangeCodec(new mxGeometryChange(),'geometry'));
mxCodecRegistry.register(mxGenericChangeCodec(new mxCollapseChange(),'collapsed'));
mxCodecRegistry.register(mxGenericChangeCodec(new mxVisibleChange(),'visible'));
}

mxCodecRegistry.register(function()
{
return new mxObjectCodec(new mxGraph(),['graphListeners','eventListeners','view','container','cellRenderer','editor','selection','gestureHandler','selection']);
}());

mxCodecRegistry.register(function()
{
var codec=new mxObjectCodec(new mxGraphView());
codec.encode=function(enc,view)
{
return this.encodeCell(enc,view,view.graph.getModel().getRoot());
};
codec.encodeCell=function(enc,view,cell)
{
var model=view.graph.getModel();
var state=view.getState(cell);
var childCount=model.getChildCount(cell);
var parent=model.getParent(cell);
var geo=model.getGeometry(cell);
var name='layer';
if(parent==null)
{
name='graph';
}
else if(model.isEdge(cell))
{
name='edge';
}
else if(childCount>0&&geo!=null)
{
name='group';
}
else if(model.isVertex(cell))
{
name='vertex';
}
var node=enc.document.createElement(name);
var label=view.graph.getLabel(cell);
if(label!=null){
node.setAttribute('label',view.graph.getLabel(cell));
}
if(parent==null){
var bounds=view.bounds;
if(bounds!=null)
{
node.setAttribute('x',bounds.x);
node.setAttribute('y',bounds.y);
node.setAttribute('width',bounds.width);
node.setAttribute('height',bounds.height);
}
}
else if(state!=null&&geo!=null)
{
var abs=state.absolutePoints;
if(abs!=null&&abs.length>0)
{
var pts=abs[0].x+','+abs[0].y;
for(var i=1;i<abs.length;i++)
{
pts+=' '+abs[i].x+','+abs[i].y;
}
node.setAttribute('points',pts);
}
else
{
node.setAttribute('x',state.x);
node.setAttribute('y',state.y);
node.setAttribute('width',state.width);
node.setAttribute('height',state.height);
}
for(var i in state.style)
{
var value=state.style[i];
if(value!=null&&typeof(value)!='function'&&typeof(value)!='object')
{
node.setAttribute(i,value);
}
}
}
for(var i=0;i<childCount;i++)
{
node.appendChild(this.encodeCell(enc,view,model.getChildAt(cell,i)));
}
return node;
};
return codec;
}());

mxCodecRegistry.register(function()
{
var codec=new mxObjectCodec(new mxStylesheet());
codec.encode=function(enc,obj)
{
var node=enc.document.createElement(obj.constructor.name);
for(var i in obj.styles)
{
var style=obj.styles[i];
var styleNode=enc.document.createElement('style');
if(i!=null)
{
styleNode.setAttribute('name',i);
for(var j in style)
{
var entry=enc.document.createElement('entry');
entry.setAttribute('key',j);
var type=typeof(style[j]);
if(type!='object'&&type!='function')
{
entry.setAttribute('value',style[j]);
}
else if(type=='function')
{



var name=null;
for(var k in mxPerimeter)
{
if(mxPerimeter[k]==style[j])
{
name='mxPerimeter.'+k;
}
}
if(name==null)
{
for(var k in mxEdgeStyle)
{
if(mxEdgeStyle[k]==style[j])
{
name='mxEdgeStyle.'+k;
}
}
}
if(name!=null)
{
var tmp=enc.document.createTextNode(name);
entry.appendChild(tmp);
}
}
styleNode.appendChild(entry);
}
if(styleNode.childNodes.length>0)
{
node.appendChild(styleNode);
}
}
}
return node;
};
codec.decode=function(dec,node,into)
{
var obj=into||new this.template.constructor();
var id=node.getAttribute('id');
if(id!=null)
{
dec.objects[id]=obj;
}
node=node.firstChild;
while(node!=null)
{
if(!this.processInclude(dec,node,obj)&&node.nodeName=='add')
{
var as=node.getAttribute('as');
if(as!=null)
{
var extend=node.getAttribute('extend');
var style=(extend!=null)?mxUtils.clone(obj.styles[extend]):null;
if(style==null)
{
if(extend!=null)
{
mxLog.warn('mxStylesheetCodec.decode: stylesheet '+extend+' not found to extend');
}
style=new Array();
}
}
var entry=node.firstChild;
while(entry!=null)
{
if(entry!=null&&entry.nodeName=='add')
{
var key=entry.getAttribute('as');
var text=mxUtils.getTextContent(entry);
if(text!=null&&text.length>0)
{
style[key]=eval(text);
}
else
{
var value=entry.getAttribute('value');
if(mxUtils.isNumeric(value))
{
style[key]=parseFloat(value);
}
else
{
style[key]=value;
}
}
}
entry=entry.nextSibling;
}
}
obj.putCellStyle(as,style);
node=node.nextSibling;
}
return obj;
};
return codec;
}());

mxCodecRegistry.register(function()
{
var codec=new mxObjectCodec(new mxDefaultKeyHandler());
codec.encode=function(enc,obj)
{
return null;
};
codec.decode=function(dec,node,into)
{
if(into!=null)
{
var editor=into.editor;
node=node.firstChild;
while(node!=null)
{
if(!this.processInclude(dec,node,into)&&node.nodeName=='add')
{
var as=node.getAttribute('as');
var action=node.getAttribute('action');
var control=node.getAttribute('control');
into.bindAction(as,action,control);
}
node=node.nextSibling;
}
}
return into;
};
return codec;
}());

mxCodecRegistry.register(function()
{
var codec=new mxObjectCodec(new mxDefaultToolbar());
codec.encode=function(enc,obj){
return null;
}
codec.decode=function(dec,node,into)
{
if(into!=null)
{
var editor=into.editor;
var model=editor.graph.getModel();
node=node.firstChild;
while(node!=null)
{
if(node.nodeType==1)
{
if(!this.processInclude(dec,node,into))
{
if(node.nodeName=='separator')
{
into.addSeparator();
}
else if(node.nodeName=='br')
{
into.toolbar.addBreak();
}
else if(node.nodeName=='hr')
{
into.toolbar.addLine();
}
else if(node.nodeName=='add')
{
var as=node.getAttribute('as');
as=mxResources.get(as)||as;
var icon=node.getAttribute('icon');
var pressedIcon=node.getAttribute('pressedIcon');
var action=node.getAttribute('action');
var mode=node.getAttribute('mode');
var template=node.getAttribute('template');
if(action!=null)
{
into.addItem(as,icon,action,pressedIcon);
}
else if(mode!=null)
{
var funct=mxUtils.eval(mxUtils.getTextContent(node));
into.addMode(as,icon,mode,pressedIcon,funct);
}
else if(template!=null)
{
var cell=model.templates[template];
var style=node.getAttribute('style');
if(style!=null){
cell=cell.clone();
cell.setStyle(style);
}
into.addPrototype(as,icon,cell,pressedIcon);
}
else
{
var children=mxUtils.getChildNodes(node);
if(children.length>0)
{
if(icon==null)
{
var combo=into.addActionCombo(as);
for(var i=0;i<children.length;i++)
{
var child=children[i];
if(child.nodeName=='separator')
{
into.addOption(combo,'---');
}
else if(child.nodeName=='add')
{
var lab=child.getAttribute('as');
var act=child.getAttribute('action');
into.addActionOption(combo,lab,act);
}
}
}
else
{
var select=null;
var create=function()
{
var template=model.templates[select.value];
if(template!=null)
{
var clone=template.clone();
var style=select.options[select.selectedIndex].cellStyle;
if(style!=null)
{
clone.setStyle(style);
}
return clone;
}
else
{
mxLog.warn('Template '+template+' not found');
}
return null;
}
var img=into.addPrototype(as,icon,create);
select=into.addCombo();

mxEvent.addListener(select,'change',function()
{
into.toolbar.selectMode(img,function(evt)
{
var pt=mxUtils.convertPoint(editor.graph.container,evt.clientX,evt.clientY);
return editor.addVertex(null,funct(),pt.x,pt.y);
});
into.toolbar.noReset=false;
});
for(var i=0;i<children.length;i++)
{
var child=children[i];
if(child.nodeName=='separator')
{
into.addOption(select,'---');
}
else if(child.nodeName=='add')
{
var lab=child.getAttribute('as');
var tmp=child.getAttribute('template');
var option=into.addOption(select,lab,tmp||template);
option.cellStyle=child.getAttribute('style');
}
}
}
}
}
}
}
}
node=node.nextSibling;
}
}
return into;
};
return codec;
}());

mxCodecRegistry.register(function()
{
var codec=new mxObjectCodec(new mxDefaultPopupMenu());
codec.encode=function(enc,obj){
return null;
};
codec.decode=function(dec,node,into)
{
var inc=node.getElementsByTagName('include')[0];
if(inc!=null)
{
this.processInclude(dec,inc,into);
}
else if(into!=null)
{
into.config=node;
}
return into;
};
return codec;
}());

mxCodecRegistry.register(function()
{
var codec=new mxObjectCodec(new mxEditor(),['modified','lastSnapshot','ignoredChanges','undoManager','graphContainer','toolbarContainer']);
codec.beforeDecode=function(enc,node,obj)
{
var ui=node.getElementsByTagName('ui')[0];
if(ui!=null)
{
node.removeChild(ui);
var tmp=ui.firstChild;
while(tmp!=null)
{
if(tmp.nodeName=='add')
{
var as=tmp.getAttribute('as');
var elt=tmp.getAttribute('element');
var style=tmp.getAttribute('style');
var element=null;
if(elt!=null)
{
element=document.getElementById(elt);
if(element!=null&&style!=null)
{
element.style.cssText=style;
}
}
else
{
var x=parseInt(tmp.getAttribute('x'));
var y=parseInt(tmp.getAttribute('y'));
var width=tmp.getAttribute('width');
var height=tmp.getAttribute('height');
element=document.createElement('div');
if(!mxClient.IS_IE){
element.style.padding='2px';
}
var wnd=new mxWindow(mxResources.get(as),element,x,y,width,height,false,true,null,style);
wnd.setVisible(true);
}
if(as=='graph')
{
obj.setGraphContainer(element);
}
else if(as=='toolbar')
{
obj.setToolbarContainer(element);
}
else if(as=='title')
{
obj.setTitleContainer(element);
}
else if(as=='status')
{
obj.setStatusContainer(element);
}
else if(as=='map')
{
obj.setMapContainer(element);
}
}
else if(tmp.nodeName=='resource')
{
mxResources.add(tmp.getAttribute('basename'));
}
else if(tmp.nodeName=='stylesheet')
{
mxClient.link('stylesheet',tmp.getAttribute('name'));
}
tmp=tmp.nextSibling;
}
}
return node;
};
return codec;
}());
