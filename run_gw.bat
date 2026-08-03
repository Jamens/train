@echo off
cd /d d:\Project\train\gateway
start "" /B ..\mvnw spring-boot:run > d:\Project\train\gw_run.log 2>&1
exit /b
