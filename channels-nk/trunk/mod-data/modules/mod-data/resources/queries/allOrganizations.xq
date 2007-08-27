(: All organizations in the model :)
<list>
	{
		 for $e in collection('__MODEL__')/organization
		 order by $e/name
		 return
		 	<organization>
		 		<id>{$e/id/text()}</id>
		 		<name>{$e/name/text()}</name>
		 	</organization>
	}
</list>