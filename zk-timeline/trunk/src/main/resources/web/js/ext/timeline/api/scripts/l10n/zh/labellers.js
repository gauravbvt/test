/*==================================================
 *  Localization of labellers.js
 *==================================================
 */

Timeline.GregorianDateLabeller.monthNames["zh"] = [
    "1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"
];

Timeline.GregorianDateLabeller.labelIntervalFunctions["zh"] = function(date, intervalUnit) {
    var text;
    var emphasized = false;
    
    var date2 = Timeline.DateTime.removeTimeZoneOffset(date, this._timeZone);
    
    switch(intervalUnit) {
    case Timeline.DateTime.DAY:
    case Timeline.DateTime.WEEK:
        text = Timeline.GregorianDateLabeller.getMonthName(date2.getUTCMonth(), this._locale) + 
            date2.getUTCDate() + "日";
        break;
    case Timeline.DateTime.HOUR:
    	text = date2.getUTCHours() + "时";
    	break;
    case Timeline.DateTime.MINUTE:
    	text = date2.getUTCMinutes() + "分";
    	break;
    case Timeline.DateTime.SECOND:
    	text = date2.getUTCSeconds() + "秒";
    	break;
    case Timeline.DateTime.MILLISECOND:
    	text = date2.getUTCMilliseconds() + "毫秒";
    	break; 
    case Timeline.DateTime.YEAR:
    	text = date2.getUTCFullYear() + "年";
    	break;   
    case Timeline.DateTime.CENTURY:
    	text = date2.getUTCFullYear() + "世纪";
    	break;
    case Timeline.DateTime.MILLENNIUM:
    	text = date2.getUTCFullYear() + "千年";
    	break;	
    default:
        return this.defaultLabelInterval(date, intervalUnit);
    }
    
    return { text: text, emphasized: emphasized };
};