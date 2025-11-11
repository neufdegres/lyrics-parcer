import sys
import util
from detect_lang import detect_ja
from cutlet import Cutlet
from unicodedata import normalize

def link(items : list):
    res = ""
    for i in range(len(items)) :
        if i != 0  :
            curr_lang = items[i]["lang"]
            if items[i-0]["lang"] == "ja" :
                if curr_lang == "ja" :
                    res += " "
            else :
                if curr_lang == "ja" :
                    res += " "
                    
        res += items[i]["text"]    
        res += " "
    return res
            
try :
    cut = Cutlet(use_foreign_spelling=False)
       
    filename_in = sys.argv[1]
    filename_out = sys.argv[2]

    with open(filename_in, 'r') as file :
        with open(filename_out, 'w') as new_file :    
            lines = file.read().splitlines()
            for l in lines :
                raw = normalize('NFKC', l)
                tab = raw.split(" ")
                sub_res = []
                for t in tab :
                    is_ja = detect_ja(t)
                    if not is_ja :
                        sub_res.append({"lang" : "else", "text": t})
                    else :
                        text = cut.romaji(t).lower()
                        sub_res.append({"lang" : "ja", "text": text})            
                res = link(sub_res).strip()

                new_file.write(f"{res}\n")
                
    print("0")    
    
except Exception as e :
    util.error_traceback_in_file(repr(e))
    print("1")