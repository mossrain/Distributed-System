import re
import json

data={
    "Paul Kocher":{
        2020: 0
    }
}

with open('E:\\underreality\\dblp_1_1_1.xml','r',encoding='utf-8') as f:
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
    f2=open('new_json.json','w')
    f2.write(json_data)
    f2.close()




            


            
            
