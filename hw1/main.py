import re;
import sys;
import time;
paraRegex = re.compile(r"<P ID=\d+");
puncRegex = re.compile(r"\'|\"|\,|\.|\?|\!|\(|\)|\&|\-|\d");
currentParagraph = 0;
currentWords = 0;
currentUnique = 0;
lexicon = [];
lineCount = 0;

def hasNumbers(inputString):
   return any(char.isdigit() for char in inputString) 

def computeLine(line):
    global currentWords;
    cleanArr = sanitize(line);
    for word in cleanArr:
        lexify(word);
        currentWords += 1;
    return;

def sanitize(line):
    global currentParagraph;
    if(paraRegex.search(line) or line == "</P>"):
        currentParagraph += 1;
        return [];
    #line = puncRegex.sub('',line);
    line = line.lower();
    arr = line.split();
    arr = [a for a in arr if  puncRegex.search(a) == None];
    return arr;

def lexify(word):
    global lexicon;
    if((len(word) == 0) or (len(word) == 1 and not(word == "i" or word == "a"))):
        return;
    thisWord = next((item for item in lexicon if item["text"] == word),{"wordId":-1,"text":word,"totalCount":1,"docCount":1,"docList":None});
    if(thisWord["wordId"] == -1):
        thisWord["wordId"] = len(lexicon);
        thisWord["docList"] = [currentParagraph];
        lexicon.append(thisWord);
        #lexicon = sorted(lexicon, key = lambda word: word["text"]);
    else:
        lexicon[thisWord["wordId"]]["totalCount"] += 1;
        if(not(currentParagraph in thisWord["docList"])):
            lexicon[thisWord["wordId"]]["docList"].append(currentParagraph);
            lexicon[thisWord["wordId"]]["docCount"] += 1;
    return;

def organize():
    global currentUnique;
    global lexicon;
    lexicon = sorted(lexicon, key = lambda word: word["totalCount"], reverse=True);
    rare = [item for item in lexicon if item["docCount"] == 1];
    currentUnique = len(rare);
    return;

def report():
    print("Total Paragraphs: " + str(currentParagraph));
    print("Total Words: " + str(currentWords));
    print("Total Words In Only One Document: " + str(currentUnique));
    print("Percentage Of Such Words: " + str(currentUnique / currentWords));
    print("Top Words: ");
    for num in range(0,20):
        cur = lexicon[num];
        print(str(num+1) + ": '" + cur["text"] + "' COUNT: " + str(cur["totalCount"]) + " DOCUMENT COUNT: "  + str(cur["docCount"]));
    cur = lexicon[99];
    print(str(100) + ": '" + cur["text"] + "' COUNT: " + str(cur["totalCount"]) + " DOCUMENT COUNT: "  + str(cur["docCount"]));
    cur = lexicon[499];
    print(str(500) + ": '" + cur["text"] + "' COUNT: " + str(cur["totalCount"]) + " DOCUMENT COUNT: "  + str(cur["docCount"]));
    cur = lexicon[999];
    print(str(1000) + ": '" + cur["text"] + "' COUNT: " + str(cur["totalCount"]) + " DOCUMENT COUNT: "  + str(cur["docCount"]));
    print(end - begin);
    return;

with open("caesar-polo-esau.txt", encoding="latin1") as f:
    begin = time.clock();
    for line in f:
        lineCount += 1;
        computeLine(line);
        print(lineCount/456549);
organize();
end = time.clock();
report();
