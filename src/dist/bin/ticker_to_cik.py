#!/usr/bin/env python
import json
import re
import sys
import urllib
 
URL = 'http://www.sec.gov/cgi-bin/browse-edgar?CIK={}&Find=Search&owner=exclude&action=getcompany'
CIK_RE = re.compile(r'.*CIK=(\d{10}).*')

def get_cik_from_ticker(ticker):
    results = CIK_RE.findall(urllib.urlopen(URL.format(ticker)).read())
    if len(results):
        return str(results[0])

def get_cik_from_tickers(tickers):
    cik_dict = {}
    for ticker in tickers:
        cik = get_cik_from_ticker(ticker)
        cik_dict[ticker] = cik
    return cik_dict

if __name__ == '__main__':
    json_string = json.dumps(get_cik_from_tickers(sys.argv[1:]))    
    print(json_string)
