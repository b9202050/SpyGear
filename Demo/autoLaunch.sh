#!/bin/bash

# start web cam via mjpg_streamer in background
push ~/mjpg_streamer
	./mjpg_streamer -i "./input_uvc.so -y -r QVGA -f 15" -o "./output_http.so -w ./www" &
pop

# start SpyGearPi in background
push ~/dist
	sudo java -jar SpyGearPi.jar &
pop

echo "=================="
echo "SpyGear"
echo "=================="
echo ""
echo ""
echo "Start your engine..."
echo ""
