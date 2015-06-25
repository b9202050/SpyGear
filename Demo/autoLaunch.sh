#!/bin/bash

# start web cam via mjpg_streamer in background
pushd /home/pi/mjpg-streamer
	./mjpg_streamer -i "./input_uvc.so -y -r QVGA -f 15" -o "./output_http.so -w ./www" &
popd

# start SpyGearPi in background
pushd /home/pi/dist
	sudo java -jar SpyGearPi.jar &
popd

echo "=================="
echo "SpyGear"
echo "=================="
echo ""
echo ""
echo "Start your engine..."
echo ""
