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