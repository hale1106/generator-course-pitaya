@echo off

REM set path
set project_name=<%= props.name %>
set backup_path=E:\backup\<%= props.name %>
set project_path=E:\workspace\<%= props.name %>
set publish_path=D:\project\webPublish\<%= props.name %>
set static_publish_path=D:\project\staticPublish\<%= props.name %>\static
set tomcat_path=D:\server\apache-tomcat-7.0.47

if not exist %backup_path% md %backup_path%
if not exist %publish_path% md %publish_path%
if not exist %static_publish_path% md %static_publish_path%

REM set work path
echo %cd%
set current_dir=%project_path%
pushd %current_dir% 
echo %cd%

REM pull code
git pull

REM maven build
call mvn install

REM backup
set h=%time:~0,2%
set h=%h: =0%
set curent_time=%date:~0,4%%date:~5,2%%date:~8,2%%h%%time:~3,2%%time:~6,2%
set backup_dest=%backup_path%\%curent_time%
if not exist %backup_dest% md %backup_dest%
xcopy %publish_path% %backup_dest% /e /y

set build_target_path=%project_path%\target\%project_name%

REM replace config file
copy %build_target_path%\WEB-INF\classes\config_online.properties %build_target_path%\WEB-INF\classes\config.properties
copy %build_target_path%\WEB-INF\classes\log4j_online.properties %build_target_path%\WEB-INF\classes\log4j.properties

REM publish
xcopy %build_target_path% %publish_path% /e /y

REM publish static
xcopy %build_target_path%\static %static_publish_path% /e /y

REM stop tomcat server
REM taskkill /f /im tomcat6.exe

REM start tomcat server
echo %cd%
set current_dir=%tomcat_path%\bin
pushd %current_dir% 
echo %cd%

REM call startup.bat

echo deploy success

pause