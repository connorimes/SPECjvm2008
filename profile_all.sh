#!/bin/bash
#
# Execute the benchmarks specified in $BENCHMARKS.
# Each benchmark is run $TRIAL times.
#

BENCHMARKS=( \
	startup.compiler.compiler \
	startup.compiler.sunflow \ # appears to hang
	startup.compress \
	startup.crypto.aes \
	startup.crypto.rsa \
	startup.crypto.signverify \
	startup.mpegaudio \
	startup.scimark.fft \
	startup.scimark.lu \
	startup.scimark.monte_carlo \
	startup.scimark.sor \
	startup.scimark.sparse \
	startup.serial \
	startup.sunflow \
	startup.xml.transform \
	startup.xml.validation \
	# compiler.compiler \
	# compiler.sunflow \
	# compress \
	# crypto.aes \
	# crypto.rsa \
	# crypto.signverify \
	# derby \
	# mpegaudio \
	# scimark.fft.large \
	# scimark.lu.large \
	# scimark.sor.large \
	# scimark.sparse.large \
	# scimark.fft.small \
	# scimark.lu.small \
	# scimark.sor.small \
	# scimark.sparse.small \
	# scimark.monte_carlo \
	# serial \
	# sunflow \
	# xml.transform \
	# xml.validation \
	)

TRIALS=1
SLEEP_TIME=20
ARGS=-ikv

for b in ${BENCHMARKS[@]}; do
	if [ -d $b ]; then
		echo "Directory $b already exists - skipping this execution"
		continue
	fi
	for (( i=1; i<=$TRIALS; i++ )); do
		echo "Running $b trial $i"
		./profile.sh $ARGS $b
		log_dir=$b/heartbeat_logs/trial$i
		mkdir -p $log_dir
		mv heartbeat-*.log $log_dir
		echo "Sleeping $SLEEP_TIME"
		sleep $SLEEP_TIME
	done
done
