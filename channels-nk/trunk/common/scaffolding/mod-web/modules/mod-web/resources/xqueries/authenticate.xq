declare variable $input as node() external;
declare variable $userid as node() external;
declare variable $password as node() external;
declare variable $project as node() external;

let $proj := $input//project[name = $project][admin = $userid]
return
	if (not($proj))
	then 
		<b>f</b>
	else if ($input//user[id = $userid][password = $password])
			 then <b>t</b>
			 else <b>f</b>
