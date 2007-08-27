(: All scenarios in a project. 							:)

(: Variables:															  :)
(: 			projectId -- the id of the project	:)

<list>
	{
		 for $e in collection('__MODEL__')/scenario
		 where $e/projectId = $projectId
		 order by $e/name
		 return
		 	<scenario>
		 		<id>{$e/id/text()}</id>
		 		<name>{$e/name/text()}</name>
		 	</scenario>
	}
</list>
