#!/usr/bin/python

import sys
import nltk
from nltk.corpus import stopwords
import string
import re

#This script takes a question removes apostophe, punctuation and stop words

if len(sys.argv) != 2:
	print "Incorrect argument format"
else:
	question = sys.argv[1];
	print question;
	#remove apostrophe
	question = re.sub(r"'s","",question)
	#remove punctuation
	exclude = set(string.punctuation)
	question = ''.join(ch for ch in question if ch not in exclude);
	#remove stop words
	stopword_list = stopwords.words("english");
	question_words = question.split();
	result = str();
	for words in question_words:
		if words not in stopword_list:
			result += words+" "
	print result;