#!/usr/bin/python

import sys
import nltk
from nltk.corpus import stopwords

#This script takes a question

if len(sys.argv) != 2:
	print "Incorrect argument format"
else:
	question = sys.argv[1];
	stopword_list = stopwords.words("english");
	print stopword_list;