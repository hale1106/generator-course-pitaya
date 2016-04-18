@echo off

REM set path
set backup_path=E:\backup\<%= props.name %>\<%= props.subName %>
set project_path=E:\workspace\<%= props.name %>\<%= props.subName %>
set publish_path=D:\project\WebPublish\<%= props.name %>\<%= props.subName %>
set static_publish_path=D:\project\staticPublish\<%= props.name %>\<%= props.subName %>

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

REM backup
set h=%time:~0,2%
set h=%h: =0%
set curent_time=%date:~0,4%%date:~5,2%%date:~8,2%%h%%time:~3,2%%time:~6,2%
set backup_dest=%backup_path%\%curent_time%
if not exist %backup_dest% md %backup_dest%
xcopy %publish_path% %backup_dest% /e /y


REM publish
xcopy %project_path%\build %publish_path% /e /y

REM publish static
xcopy %project_path%\build %static_publish_path% /e /y

echo deploy success

pause