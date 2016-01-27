import re;
import sys;
import time;
#Regex for checking paragraph headers
paraRegex = re.compile(r"<P ID=\d+");
#Regex for sanitizing input
puncRegex = re.compile(r"\'|\"|\,|\.|\?|\!|\(|\)|\&|\-|\d");
currentParagraph = 0;
currentWords = 0;
currentUnique = 0;
lexicon = [];
lineCount = 0;

def computeLine(line):
    global currentWords;
    #Scrub the line
    cleanArr = sanitize(line);
    for word in cleanArr:
        #Compute each word in the line
        lexify(word);
        currentWords += 1;
    return;

def sanitize(line):
    global currentParagraph;
    #Check if we're starting a new paragraph rather than computing a line
    if(paraRegex.search(line) or line == "</P>"):
        currentParagraph += 1;
        return [];
    #Set lowercase
    line = line.lower();
    #Split into words
    arr = line.split();
    #Sanitize these words
    arr = [a for a in arr if  puncRegex.search(a) == None];
    return arr;

def lexify(word):
    global lexicon;
    if((len(word) == 0) or (len(word) == 1 and not(word == "i" or word == "a"))):
        return;
    thisWord = next((item for item in lexicon if item["text"] == word),{"wordId":-1,"text":word,"totalCount":1,"docCount":1,"docList":None});
    #If we're adding a new word to the lexicon
    if(thisWord["wordId"] == -1):
        #Assign it an ID of its list slot
        thisWord["wordId"] = len(lexicon);
        #Initialize its paragraph list
        thisWord["docList"] = [currentParagraph];
        lexicon.append(thisWord);
    else:
        #We found an additional count of an existing word
        lexicon[thisWord["wordId"]]["totalCount"] += 1;
        #If this is a new document we found the word in
        if(not(currentParagraph in thisWord["docList"])):
            #Update the document listings
            lexicon[thisWord["wordId"]]["docList"].append(currentParagraph);
            lexicon[thisWord["wordId"]]["docCount"] += 1;
    return;

def organize():
    global currentUnique;
    global lexicon;
    #Sort our list by number of hits
    lexicon = sorted(lexicon, key = lambda word: word["totalCount"], reverse=True);
    #Grab a list of words only appearing in a single document
    rare = [item for item in lexicon if item["docCount"] == 1];
    currentUnique = len(rare);
    return;

def report():
    print("Total Paragraphs: " + str(currentParagraph));
    print("Total Words: " + str(currentWords));
    print("Total Unique Words: " + str(len(lexicon)));
    print("Total Words In Only One Document: " + str(currentUnique));
    print("Percentage Of Words In Only One Document: " + str(currentUnique / currentWords));
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
    #start timing
    begin = time.clock();
    #For each line in the file
    for line in f:
        lineCount += 1;
        #We compute it
        computeLine(line);
        print(lineCount/456549);
#Gather data
organize();
end = time.clock();
#Print Data
report();
