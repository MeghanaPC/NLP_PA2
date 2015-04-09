#!/usr/bin/python

import sys
import nltk

#This script takes a question

if len(sys.argv) != 2:
	print "Incorrect argument format"
else:
	question = sys.argv[1];
	stopword_list = stopwords.