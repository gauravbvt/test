declare variable $input as node() external;
for $project in $input//project
return
	<option value="{$project/name}">{$project/name}</option>
