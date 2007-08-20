(: All scenarios in a project.
	 Variables:
	 						projectId -- the id of the project
:)
<list>
	{
		 for $e in collection('__MODEL__')/scenario
		 where projectId = $projectId
		 order by $e/name
		 return
		 	<scenario>
		 		<id>{$e/id}</id>
		 		<name>{$e/name}</name>
		 	</scenario>
	}
</list>
