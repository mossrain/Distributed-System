#!/bin/bash
uptime
uptime>2052717-hw1-q2.log

for ((i=1;i<15;i++));
do
    sleep 10
    uptime
    uptime>>2052717-hw1-q2.log
done