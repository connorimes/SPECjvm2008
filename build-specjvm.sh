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


if [ -f "$JAVA_HOME/../lib/tools.jar" ] ; then
  JAVA_TOOLS_JAR="$JAVA_HOME/../lib/tools.jar"
elif [ -f "$JAVA_HOME/lib/tools.jar" ] ; then
  JAVA_TOOLS_JAR="$JAVA_HOME/lib/tools.jar"
elif [ `uname -s` = 'Darwin' ]; then
  if [ -f "$JAVA_HOME/../Classes/classes.jar" ] ; then
    JAVA_TOOLS_JAR="$JAVA_HOME/../Classes/classes.jar"
  elif  [ -f "$JAVA_HOME/Classes/classes.jar" ] ; then
    JAVA_TOOLS_JAR="$JAVA_HOME/Classes/classes.jar"
  fi
  EXTRA_BOOTCLASSPATH_ARG="-Xbootclasspath/p:lib/javac.jar"
fi

if [ -z "$JAVA_TOOLS_JAR" ] ; then
  echo "Need the Javac tool and expected to find file:"
  if [ `uname -s` = 'Darwin' ]; then
    echo JAVA_TOOLS_JAR="$JAVA_HOME/../Classes/classes.jar"
  else
    echo "$JAVA_HOME/../lib/tools.jar"
  fi
  echo "Please update your JAVA_HOME variable or point to the tools jar in"
  echo "system variable JAVA_TOOLS_JAR directly."
  exit
fi

$JAVA_EXE $EXTRA_BOOTCLASSPATH_ARG -cp lib/ant.jar:lib/ant-launcher.jar:$JAVA_TOOLS_JAR org.apache.tools.ant.Main -f build.xml $*
