@if "%__ECHO%" NEQ "" (@echo %__ECHO%) else (@echo off)

setlocal

if not defined JAVA_HOME (
  echo Please point the system variable JAVA_HOME to where your JVM is installed.
  goto :the_end
)

if not exist %JAVA_HOME% (
  echo Can not find folder %JAVA_HOME%
  echo Please check your JAVA_HOME variable.
  goto :the_end
)

if exist %JAVA_HOME%\jre\bin\java.exe (
  set JAVA_EXE=%JAVA_HOME%\jre\bin\java.exe
)

if exist %JAVA_HOME%\bin\java.exe (
  set JAVA_EXE=%JAVA_HOME%\bin\java.exe
)

if not defined JAVA_EXE (
  echo Expected to find file %JAVA_HOME%\jre\bin\java.exe or %JAVA_HOME%\bin\java.exe
  echo Please check your JAVA_HOME variable or edit the script to match your JDK.
  goto :the_end
)


%JAVA_EXE% %JAVA_OPTS% -jar SPECjvm2008.jar %*

:the_end
endlocal
:end

