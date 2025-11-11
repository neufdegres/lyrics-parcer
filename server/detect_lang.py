from lingua import Language, LanguageDetectorBuilder

def detect_ja(text : str):
    languages = [Language.ENGLISH, Language.JAPANESE]
    detector = LanguageDetectorBuilder.from_languages(*languages).build()
    language = detector.detect_language_of(text)
    if language == Language.JAPANESE :
        return True
    return False

def detect_ko(text : str):
    languages = [Language.ENGLISH, Language.KOREAN]
    detector = LanguageDetectorBuilder.from_languages(*languages).build()
    language = detector.detect_language_of(text)
    if language == Language.KOREAN :
        return True
    return False