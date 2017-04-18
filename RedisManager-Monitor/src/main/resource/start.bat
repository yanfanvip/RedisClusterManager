@echo off
set website="http://localhost:8080/metric"
set ip="10.16.232.129"
java -jar systemMonitor.jar --website=%website% --ip=%ip%