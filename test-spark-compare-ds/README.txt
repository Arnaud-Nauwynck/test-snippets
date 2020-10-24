
Left DS:
+------------+-----+
|          id| col1|
+------------+-----+
|        same|  123|
|      differ|23400|
|only_in_left|  234|
+------------+-----+

Right DS:
+-------------+----+
|           id|col1|
+-------------+----+
|         same| 123|
|       differ| 234|
|only_in_right| 234|
+-------------+----+

diffDS = leftDs.joinWithColumn(rightDs, leftDs.col('id') === rightDs.col('id'), 'fullouter')
+-------------------+--------------------+
|                 _1|                  _2|
+-------------------+--------------------+
|[only_in_left, 234]|                null|
|    [differ, 23400]|       [differ, 234]|
|        [same, 123]|         [same, 123]|
|               null|[only_in_right, 234]|
+-------------------+--------------------+

Diff
joinDs.filter( not( joinDs.col('_1') ==== joinDs.col('_2') ) )
+---------------+-------------+
|             _1|           _2|
+---------------+-------------+
|[differ, 23400]|[differ, 234]|
+---------------+-------------+


Left Only
joinDs.filter(isnull(joinDs.col('_2')) .select(joinDs.col('_1'))
+-------------------+
|                 _1|
+-------------------+
|[only_in_left, 234]|
+-------------------+


Right Only
joinDs.filter(isnull(joinDs.col('_1')) .select(joinDs.col('_2'))
+--------------------+
|                  _2|
+--------------------+
|[only_in_right, 234]|
+--------------------+

