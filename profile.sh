#!/bin/bash

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

main_jar="build/release/SPECjvm2008/SPECjvm2008.jar"
profiler_jar="/local/he-profiler-jni/he-profiler/target/he-profiler-0.0.1-SNAPSHOT.jar"
profiler_jni_jar="/local/he-profiler-jni/native/native-jni/target/he-profiler-native-jni-0.0.1-SNAPSHOT.jar"
profiler_native_lib_dir="/local/he-profiler-jni/native/native-unix/target"

# Using -Djava.library.path=${profiler_native_lib_dir} with --parseJvmArgs doesn't work
# Set LD_LIBRARY_PATH instead
LD_LIBRARY_PATH=${profiler_native_lib_dir} $JAVA_EXE -cp ${main_jar}:${profiler_jar}:${profiler_jni_jar} spec.harness.Launch $*
