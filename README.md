# 参考
https://blog.csdn.net/u013613428/article/details/51499552

```xml
	<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
		<withJansi>true</withJansi>
		<encoder>
			<pattern>[%thread] %highlight(%-5level) %cyan(%logger{15}) - %highlight(%msg) %n</pattern>
		</encoder>
	</appender>

	<root level="DEBUG">
		<appender-ref ref="STDOUT" />
	</root>
	
```


# todo
网络请求的数据都比较复杂, 所以不适合做json数据的传递, 而是直接传递一整个数据包

解决思路:
在数据源前后拼接一个字节, 然后传递, 然后读取关键字节即可做到错误即可