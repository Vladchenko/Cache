Command line arguments used in this program.

level1Cache=integerValue
	integerValue > 0;	Stands for a maximum number of entries present in a cache level1.

level2Cache=integerValue
	integerValue > 0;    Stands for a maximum number of entries present in a cache level2.

cachekind=StringValue
	lru	- Least recently used (¬ытеснение давно использовавшегос€)
	mru	- Most recently used (¬ытеснение недавно использовавшегос€)
	lfu	- Least frequently used (¬ытеснение наименее используемого)