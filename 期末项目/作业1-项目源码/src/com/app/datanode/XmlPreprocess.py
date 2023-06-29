import re
import json
import os

data={}
dirpath='E:\\underreality\\大三上\\分布式系统\\dblp\\'
files=os.listdir(dirpath)

# print(files)

for file in files:

    with open(dirpath+file,'r',encoding='utf-8') as f:
        print(file+" start preprocessing")
        # key=False
        author=[]
        for line in f.readlines():
            if "key=" in line:
                authors=[]
            if "<author" in line:
                searchObj=re.search(r'<author([\s\S]*?)>([\s\S]*?)</author>',line,re.M|re.I)
                if searchObj:
                    authors.append(searchObj.group(2))
            if "<year>" in line:
                searchObj=re.search(r'<year>([\d]*?)</year>',line,re.M|re.I)
                if searchObj:
                    year=int(searchObj.group(1))
                    for author in authors:
                        #data["author"]
                        if data.__contains__(author):
                            if data[author].__contains__(year):
                                data[author][year]+=1
                            else:
                                data[author][year]=1
                        else:
                            data[author]={}
                            data[author][year]=1

        # print(data)
        json_data = json.dumps(data,indent=2,sort_keys=True)
        # print(json_data)
        f2=open(dirpath+file.split(".")[0]+'-0.json','w')
        f2.write(json_data)
        f2.close()

        f3=open(dirpath+file.split(".")[0]+'-1.json','w')
        f3.write(json_data)
        f3.close()

        print(file+" complete preprocess!")
        




                


                
                
