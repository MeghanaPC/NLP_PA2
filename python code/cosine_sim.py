import string
import operator
import nltk
import re
import os
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
from nltk.util import ngrams
from nltk.corpus import stopwords
from collections import OrderedDict
from nltk.stem import PorterStemmer

def tokenize(text):
    tokens = nltk.word_tokenize(text)
    return tokens

def findOverlapScore(ngram, question):
	stemmer = PorterStemmer();
	stemmedGram = ' '.join( stemmer.stem(kw) for kw in ngram.split(" "))
	stemmedQues = ' '.join( stemmer.stem(kw) for kw in question.split(" "))
	seen = list()
	count = 0

	for quesWord in stemmedQues.split():
		for gram in stemmedGram.split():
		        if gram not in seen:
		                if quesWord == gram:
		                        seen.append(gram)
		                        count += 1

	return count
						

def createRankedNgramFile(question, documentName, resultFile):
	stopword_list = stopwords.words("english");

	fileHandle = open(documentName, 'r')
	inputString = fileHandle.read()
	input_words = inputString.split();
	"""
	stoppedInput = str();
	for words in input_words:
		if words not in stopword_list:
			stoppedInput += words+" "
	no_punctuation = stoppedInput.translate(None, string.punctuation)
	"""
	no_punctuation = inputString.translate(None, string.punctuation)

	str_Array = {}



	#question = 'What hockey team did Wayne Gretzky play for'
	question = re.sub(r"'s","",question)
	#remove punctuation
	exclude = set(string.punctuation)
	question = ''.join(ch for ch in question if ch not in exclude);
	#remove stop words

	question_words = question.split();
	result = str();
	for words in question_words:
		if words not in stopword_list:
			result += words+" "
	print result;
	str_Array[0] = result


	n = 10
	ngramsArray = ngrams(no_punctuation.split(), n)

	ngramsArray = OrderedDict.fromkeys(ngramsArray)
	arr = []
	k = 1
	stopword_list = stopwords.words("english");

	for grams in ngramsArray:
		str_Array[k] = ' '.join(grams)
		k += 1      

	tfidf = TfidfVectorizer(tokenizer=tokenize, stop_words ='english')
	tfs = tfidf.fit_transform(str_Array.values())

	cos = cosine_similarity(tfs[0:1], tfs).flatten ();

	finalMap = {}
	l = len(cos)
	for x in range (0, l):
		finalMap[x]=cos[x]

	sorted_x = sorted(finalMap.items(), key=operator.itemgetter(1), reverse=True)

	count = 0
	aggResultMap = {}
	resultHandle = open(resultFile, 'w')
	for i in range (1, len(sorted_x)):
		j = sorted_x[i][0]
		#resultHandle.write(str(str_Array[j]) + '\n');
		simScore = finalMap[j]
		keywordOverlapScore = findOverlapScore(str(str_Array[j]), question)
		aggResultMap[str(str_Array[j])] = keywordOverlapScore + simScore
		count += 1
		if(count >= 2000):
			break;
		#print('')
		#print str_Array[j]

	sorted_result = sorted(aggResultMap.items(), key=operator.itemgetter(1), reverse=True)
	for i in range (1, len(sorted_result)):
		resultHandle.write(str(sorted_result[i][0]) + '\n');

pathToDevInput = '/home/meghana/Documents/NLP_PA2/pa2_data/pa2-release/topdocs/dev_parsed/'
pathToTestInput = '/home/meghana/Documents/NLP_PA2/pa2_data/pa2-release/topdocs/test_parsed/'
pathToDevQuestions = '/home/meghana/Documents/NLP_PA2/pa2_data/pa2-release/qadata/dev/questions.txt'
pathToTestQuestions = '/home/meghana/Documents/NLP_PA2/pa2_data/pa2-release/qadata/test/questions.txt'
pathToDevOutput = '/home/meghana/Documents/NLP_PA2/pa2_data/pa2-release/dev_ngrams_keywordOverlap'
pathToTestOutput = '/home/meghana/Documents/NLP_PA2/pa2_data/pa2-release/test_ngramskeywordOverlap'

if not os.path.exists(pathToDevOutput):
	os.makedirs(pathToDevOutput)
if not os.path.exists(pathToTestOutput):
	os.makedirs(pathToTestOutput)

#createRankedNgramFile('What team did Wayne Gretzy play for?', pathToDevInput + "top_docs.0", pathToDevOutput + "/Ngrams_" + '0');

"""
with open(pathToDevQuestions,'r') as devQues:
	topDocName = pathToDevInput
	firstLineDone = False
	qNo = 0
	for line in devQues:
		if(firstLineDone is False):
			if('Number' in line):
				qNo = line.split()[1];
			 	topDocName += "top_docs." + qNo
				firstLineDone = True
		else:
			firstLineDone = False
			createRankedNgramFile(line, topDocName, pathToDevOutput + "/Ngrams_" + qNo);
			print topDocName + ' ' + qNo + ' ' + line
			print ''
			topDocName = pathToDevInput

"""
with open(pathToTestQuestions,'r') as testQues:
	topDocName = pathToTestInput
	firstLineDone = False
	qNo = 0
	for line in testQues:
		if(firstLineDone is False):
			if('Number' in line):
				qNo = line.split()[1];
			 	topDocName += "top_docs." + qNo
				firstLineDone = True
		else:
			firstLineDone = False
			createRankedNgramFile(line, topDocName, pathToTestOutput + "/Ngrams_" + qNo);
			print topDocName + ' ' + qNo + ' ' + line
			print ''
			topDocName = pathToTestInput


