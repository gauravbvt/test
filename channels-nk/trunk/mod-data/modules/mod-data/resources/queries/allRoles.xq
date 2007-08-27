(: All roles in the model :)
<list>
	{
		 for $e in collection('__MODEL__')/role
		 order by $e/name
		 return
		 	<role>
		 		<id>{$e/id/text()}</id>
		 		<name>{$e/name/text()}</name>
		 	</role>
	}
</list>
