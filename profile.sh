#!/bin/bash

main_jar="build/release/SPECjvm2008/SPECjvm2008.jar"
profiler_jar="/local/he-profiler-jni/he-profiler/target/he-profiler-0.0.1-SNAPSHOT.jar"
profiler_jni_jar="/local/he-profiler-jni/native/native-jni/target/he-profiler-native-jni-0.0.1-SNAPSHOT.jar"
profiler_native_lib_dir="/local/he-profiler-jni/native/native-unix/target"

# Using -Djava.library.path=${profiler_native_lib_dir} with --parseJvmArgs doesn't work
# Set LD_LIBRARY_PATH instead
LD_LIBRARY_PATH=${profiler_native_lib_dir} java -cp ${main_jar}:${profiler_jar}:${profiler_jni_jar} spec.harness.Launch $*
