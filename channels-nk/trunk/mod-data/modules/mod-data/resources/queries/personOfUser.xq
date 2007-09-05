(: Return person, if any, given user id :)
(: Variable: userId -- the user id :)

<list>
	{
		for $p in collection('__MODEL__')/person
		where $p/userId = $userId
		return
			<person>
				<id>{$p/id/text()}</id>
				<lastName>{$p/lastName/text()}</lastName>
				<firstName>{$p/firstName/text()}</firstName>
			</person>
	}
</list>