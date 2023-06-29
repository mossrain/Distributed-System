#!/bin/bash
wc -l $1 | awk '{print $1}'
wc -l $1 | awk '{print $1}'>2052717-hw1-q3.log # 总行数
rownum=`wc -l $1 | awk '{print $1}'`
wc -m $1 | awk '{print $1}'
wc -m $1 | awk '{print $1}'>>2052717-hw1-q3.log  # 总字符数

line1=`cat -n $1 | head -n 1` 
line2=`cat -n $1 | tail -n 1`

line1=${line1% up*}
line1=${line1##* }
line2=${line2% up*}
line2=${line2##* }

# 时间差
time=$(($(date +%s -d $line2) - $(date +%s -d $line1)));
echo $time seconds>>2052717-hw1-q3.log
echo $time seconds

# 求平均
sum1=0.00
sum2=0.00
sum3=0.00

for ((i=1;i<=$rownum;i++));
do
    line=`cat -n $1 | head -n $i` 
    num1=${line:0-16:4}
    num2=${line:0-10:4}
    num3=${line:0-4:4}
    
    sum1=`awk 'BEGIN{print "'$sum1'" + "'$num1'"}'`
    sum2=`awk 'BEGIN{print "'$sum2'" + "'$num2'"}'`
    sum3=`awk 'BEGIN{print "'$sum3'" + "'$num3'"}'`

done

avg1=`awk 'BEGIN{print "'$sum1'" / "'$rownum'"}'`
avg2=`awk 'BEGIN{print "'$sum2'" / "'$rownum'"}'`
avg3=`awk 'BEGIN{print "'$sum3'" / "'$rownum'"}'`

# 格式化输出
# avg1=`awk 'GEGIN{printf("%.2f",'$sum1' / '$rownum')}'`
# avg2=`awk 'GEGIN{printf("%.2f",'$sum2' / '$rownum')}'`
# avg3=`awk 'GEGIN{printf("%.2f",'$sum3' / '$rownum')}'`
    
echo $avg1 $avg2 $avg3
echo $avg1 $avg2 $avg3>>2052717-hw1-q3.log
