
import java.io.PrintStream;
import java.text.DecimalFormat;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;


public class TranslateText {
    /**
     * Translates the source text in any language to English.
     *
     * @param sourceText source text to be translated
     * @param out        print stream
     */
    private static void translateText(String sourceText, PrintStream out) {
        Translate translate = createTranslateService();
        Translation translation = translate.translate(sourceText);
    }

    /**
     * Translate the source text from source to target language.
     *
     * @param sourceText source text to be translated
     * @param sourceLang source language of the text
     * @param targetLang target language of translated text
     * @param out        print stream
     */
    private static void translateTextWithOptions(
            String sourceText,
            String sourceLang,
            String targetLang,
            PrintStream out) {
        Translate translate = createTranslateService();
        TranslateOption srcLang = TranslateOption.sourceLanguage(sourceLang);
        TranslateOption tgtLang = TranslateOption.targetLanguage(targetLang);

        Translation translation = translate.translate(sourceText, srcLang, tgtLang);
        translatedString = translation.getTranslatedText();
        if (longestTranslationLength < translation.getTranslatedText().length()) {
            longestTranslationLength = translation.getTranslatedText().length();
            longestTranslationLang = targetLang;
        } else if (longestTranslationLength == translation.getTranslatedText().length()) {
            longestTranslationLang += ", " + targetLang;
        }
    }

    private static double calculatePercentage(double sourceTextLength, double translationTextLength) {
        return ((translationTextLength * 100) / sourceTextLength) - 100;
    }

    /**
     * Create Google Translate API Service.
     *
     * @return Google Translate Service
     */
    private static Translate createTranslateService() {
        return TranslateOptions.newBuilder().build().getService();
    }

    private static final String BASE_LANG = "en";
    private static final String[] TARGET_LANGS = {"fr","de"};

    private static int longestTranslationLength = 0;
    private static String longestTranslationLang;
    private static String translatedString;

    public static void run() {
        ResourceDiffFinder.findResourceDiff();
        for (String key : ResourceDiffFinder.englishKeys) {
            if (ResourceDiffFinder.toBeTranslated.containsKey(key)) {   // when there's no corresponding translation string
                longestTranslationLength = 0;
                for (String language : TARGET_LANGS) {
                    try {
                        TranslateText.translateTextWithOptions(ResourceDiffFinder.toBeTranslated.get(key), BASE_LANG, language, System.out);
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        System.out.println("FAIL");
                        TranslateText.translateText(ResourceDiffFinder.toBeTranslated.get(key), System.out);
                    }
                }
                String line = key + "=" + translatedString;
                RandomValueGenerator.generateRandomTextWithLength(line, longestTranslationLength);

                DecimalFormat f = new DecimalFormat("##.00");
                System.out.println("EN\t:" + ResourceDiffFinder.toBeTranslated.get(key).length());
                System.out.println("length:\t" + longestTranslationLength +
                        "(" + f.format(calculatePercentage(ResourceDiffFinder.toBeTranslated.get(key).length(), longestTranslationLength)) + "% longer)");
                System.out.println("lang:\t" + longestTranslationLang);
                System.out.println("key:\t" + key);
                System.out.println("=============================================================");
            } else {    //  if translation for a key is found
                String val = ResourceDiffFinder.localizationStrMap.get(key);
                String line = key + "=" + val;

                RandomValueGenerator.generateRandomTextWithLength(line, val.length());
            }
        }
        RandomValueGenerator.write();
    }
}
