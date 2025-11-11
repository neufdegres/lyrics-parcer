import os
import sys
import deepl
import util
from unicodedata import normalize
    
try :
    auth_key = util.get_deepl_apikey()
    if auth_key.startswith("[") : raise Exception
    
    translator = deepl.Translator(auth_key)
    
    
    filename_in = sys.argv[1]
    filename_out = sys.argv[2]

    with open(filename_in, 'r') as file :
        with open(filename_out, 'w') as new_file :
            lines = file.read().splitlines()
            for l in lines :
                ori = normalize('NFKC', l)
                if len(ori.strip()) == 0 : res = ""
                else : res = translator.translate_text(ori, source_lang="JA", target_lang="EN-US")
                new_file.write(f"{res}\n")
            
    print("0")

except Exception as e :
    util.error_traceback_in_file(repr(e))
    print("1")