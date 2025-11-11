from random import randint

def get_code():
    a = randint(1000, 9999)
    return str(a)

def error_traceback_in_file(tb : str) :
    import datetime
    now = datetime.datetime.now()
    filename = "log/0" + str(now.month) + str(now.day) + "_" + str(now.hour) + str(now.minute) + str(now.second)
    with open(filename, 'w') as file :
        file.write(tb)
        
def get_deepl_apikey():
    with open("deepl", 'r') as file :
        res = file.readline()
        return res