#!/bin/bash
sum=0
is_prime=1
A="2052717"
for ((i=0;i<=100;i++));
do
   if [[ $i -eq 0 || $i -eq 1 ]];then
       continue
   fi

   if [[ $i -eq 2 ]]; then
       let sum=$sum+$i
       continue
    fi

   for ((j=3;j<i;j+=2));
   do
       b=$(( $i%2 ))
       c=$(( $i%$j ))

       if [[ $b -eq 0 ]]; then
       let is_prime=0
       break
       fi

       if [[ $c -eq 0 ]];  then 
       let is_prime=0
       break
       fi

    done

    if [[ $is_prime -eq 1 ]]; then
        let sum=$sum+$i
    else
        let is_prime=1
    fi

done

echo $sum
echo $sum>2052717-hw1-q1.log
