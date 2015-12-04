#!/bin/sh

if [ -z "$JAVA_HOME" ] ; then
  echo "Please point the system variable JAVA_HOME to where your jdk is installed."
  exit
fi

if [ ! -d "$JAVA_HOME" ] ; then
  echo "Can not find folder $JAVA_HOME"
  echo "Please check your JAVA_HOME variable."
  exit
fi

if [ -f "$JAVA_HOME/jre/bin/java" ] ; then
  JAVA_EXE="$JAVA_HOME/jre/bin/java"
fi

if [ -f "$JAVA_HOME/bin/java" ] ; then
  JAVA_EXE="$JAVA_HOME/bin/java"
fi

if [ -z "$JAVA_EXE" ] ; then
  echo "Expected to find file $JAVA_HOME/jre/bin/java or $JAVA_HOME/bin/java"
  echo "Please check your JAVA_HOME variable or edit the script to match your JDK."
  exit
fi

$JAVA_EXE $JAVA_OPTS -jar SPECjvm2008.jar -r $*
