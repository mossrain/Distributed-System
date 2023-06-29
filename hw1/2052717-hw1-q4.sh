#!/bin/bash
n=$1
let count=1
echo $n
sleep 1
# echo $count sec
for ((i=$n-1;i>0;i--));
do
    let ok=$[count%10]
    if test $ok -eq 0
    then
        echo $i
    fi
    sleep 1
    let count=$count+1
    # echo $count sec
done

let ok=$[count%10]
if test $ok -eq 0
then
    echo $i
fi