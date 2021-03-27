##  Использование технологии java-agent для для анализа и профилирования java программы.

Информацию, которую необходимо получить с помощью java
агента:
- Общее количество загруженных в JVM классов на момент окончания выполнения программы
- Максимальное, минимальное, среднее время выполнения метода
`TransactionProcessor.processTransaction()`
- Агент должен модифицировать `TransactionProcessor.processTransaction()` так чтобы при
вызове метода `processTransaction()` к номеру транзакции прибавлялось число 99. Мы
увидим это в терминале в виде сообщений `Processing tx: <N>`

Модификация байткода осуществлялась при помощи библиотеки `Javassist`

Для упаковки агента в `jar` и запуска `TransactionProcessor` с ним написан скрипт `build_and_run`

Пример запуска:
```
$ build_and_run.bat
немного кода gradle
...
Hello from Java Agent!
Processing tx: 99
tx: 110 completed
Processing tx: 100
tx: 819 completed
Processing tx: 101
tx: 390 completed
Processing tx: 102
tx: 393 completed
Processing tx: 103
tx: 894 completed
Processing tx: 104
tx: 72 completed
Processing tx: 105
tx: 868 completed
Processing tx: 106
tx: 518 completed
Processing tx: 107
tx: 647 completed
Processing tx: 108
tx: 368 completed
Min: 0.073, Avg: 0.5134, Max: 0.897
Number of loaded classes: 2965
```

Отмечу, что при выводе `tx: _ completed` выводится число, которое использовалось в качестве аргумента `Thread.sleep()` для проверки вывода минимального, среднего и максимального времени работы метода.

По выводу видно, что минимальное время действительно минимально и тд.