import string
import operator
import nltk
import re
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
from nltk.util import ngrams
from nltk.corpus import stopwords
from collections import OrderedDict

stopword_list = stopwords.words("english");

fileHandle = open('result1', 'r')
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

def tokenize(text):
    tokens = nltk.word_tokenize(text)
    return tokens

question = 'What hockey team did Wayne Gretzky play for'
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
#print cos


finalMap = {}
l = len(cos)
for x in range (0, l):
	finalMap[x]=cos[x]

sorted_x = sorted(finalMap.items(), key=operator.itemgetter(1), reverse=True)
#print sorted_x

for i in range (1, 40):
	j = sorted_x[i][0]
	print('')
	print str_Array[j]
