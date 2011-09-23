select	filho2.nm_pessoa 'filho2',
	pai.nm_pessoa 'pai',	
	filho1.nm_pessoa 'filho1'	
from 	pessoas pai,
	pessoas filho1,
	pessoas filho2
where	pai.cd_pessoa = filho1.cd_pai and	
	pai.cd_pessoa = filho2.cd_pai and
	filho2.cd_pessoa = 1