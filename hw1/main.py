import re;
paraRegex = re.compile(r"<P ID=\d+");
puncRegex = re.compile(r"\'|\"|\,|\.|\?|\!|\(|\)");
currentParagraph = 0;
currentWords = 0;
currentUnique = 0;
lexicon = [];
lineCount = 0;

def computeLine(line):
    cleanArr = sanitize(line);
    for word in cleanArr:
        lexify(word);
        currentWords += 1;
    return;

def sanitize(line):
    if(paraRegex.match(line) or line == "</P>"):
        global currentParagraph;
        currentParagraph += 1;
        return [];
    line = puncRegex.sub('',line);
    line = line.lower();
    arr = line.split();
    return arr;

def lexify(word):
    thisWord = next((item for item in lexicon if item["text"] == word),{"wordId":0,"text":word,"totalCount":1,"docCount":1,"docList":None});
    if(thisWord["wordId"] == 0):
        thisWord["wordId"] = len(lexicon);
        thisWord["docList"] = [currentParagraph];
        lexicon.append(thisWord);
    else:
        thisWord["totalCount"] += 1;
        if(not(currentParagraph in thisWord["docList"])):
            thisWord["docList"].append(currentParagraph);
            thisWord["docCount"] += 1;
        lexicon[thisWord["wordId"]] = thisWord;
    return;

def organize():
    sorted(lexicon, key = lambda word: word["totalCount"]);
    rare = [item for item in lexicon if item["docCount"] == 1];
    currentUnique = len(rare);
    return;

def report():
    print("Total Paragraphs: " + str(currentParagraph));
    print("Total Words: " + str(currentWords));
    print("Total Words In Only One Document: " + str(currentUnique));
    print("Percentage Of Such Words: " + str(currentUnique / currentWords));
    print("Top Words: ");
    for num in range(0,19):
        cur = lexicon[num];
        print(str(num+1) + ": " + cur["text"] + " count: " + cur["totalCount"] + " document count: "  + cur["docCount"]);
    cur = lexicon[99];
    print(str(num+1) + ": " + cur["text"] + " count: " + cur["totalCount"] + " document count: "  + cur["docCount"]);
    cur = lexicon[499];
    print(str(num+1) + ": " + cur["text"] + " count: " + cur["totalCount"] + " document count: "  + cur["docCount"]);
    cur = lexicon[999];
    print(str(num+1) + ": " + cur["text"] + " count: " + cur["totalCount"] + " document count: "  + cur["docCount"]);
    return;

with open("caesar-polo-esau.txt", encoding="latin1") as f:
    for line in f:
        lineCount += 1;
        computeLine(line);
        msg = "{:.2f}".format(100/123)+"\n";
        sys.stdout.write(msg); 
        sys.stdout.flush();
        sys.stdout.write('\r' + ' '*len(msg));
        sys.stdout.flush();
organize();
report();
