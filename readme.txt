Реализация двухуровневого кэша, 1-й уровеннь - память ОЗУ, 2-й уровень - файлы жёсткого диска.

RAM cache - кэш первого уровня, HDD cache - кэш второго уровня.
В программе реализовано 2 алгоритма кэширования - lru, mru.
Запуск программы возможен без каких-либо аргументов командной строки, в этом случае все переменные будут установлены по-умолчанию. Отчёт по процессу кэширования пишется в файл Cache.log, автоматически создаваемый в папке с программой.
Файлы для кэша 2-го уровня создаются в папке /Cached Files/cache_file_XXXXXXX.cache

Аргументы командной строки (регистр аргументов не важен):

	l1s целое_число     - Размер кэша 1-го уровня (RAM кэша).

	l2s целое_число     - Размер кэша 2-го уровня (HDD кэша).

	ck строка
		lru	- Least recently used	(Вытеснение давно использовавшегося)
		mru	- Most recently used	(Вытеснение недавно использовавшегося)
			При указании строки, отличной от вышеуказанных, выставляется алгоритм mru.

	dr	    - detailed report. Детализация информации о кэшировании. При наличии аргумента, в лог файл пишется история о выполнении процесса работы с кэшем. При его отсутствии - только общая сводка (Summary). По умолчанию, детальная информация отсутствует.
	test	- Запуск тестирования алгоритмов, реализованных в программе. При наличии этого аргумента, пишет в лог результаты работы обоих алгоритмов. По умолчанию не активно.
	n целое_число	- Число проходов цикла кэширования (запрос -> возврат кэшированной записи).
	m целое_число 	- Количество записей в выборке, из которых случайным образом запрашивается запись из кэша.

TODO:
        - ! Files are not put into Data Cache folder, fix it
        - Move constants to gradle
        - ? Make caches not to be fully filled in the beginning
        - ? Maybe validate some more arguments
        - Put all hardcodes to consts
        - Split methods into smaller ones
        - Several classes should be split, since S in SOLID breaks
        - Clean architecture
            - CacheFeeder should be replaced with ApiMapper
            - ? What should go to presentation layer
        - Multithreading (JavaRx2/3)
        - DI (Dagger2)
        - Check cache algorithms
        - Unit tests