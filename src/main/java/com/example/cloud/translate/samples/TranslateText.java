/*
 * Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.cloud.translate.samples;

import java.io.PrintStream;
import java.text.DecimalFormat;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import com.example.cloud.translate.samples.localization.ResourceDiffFinder;

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

    public static void main(String[] args) {
        ResourceDiffFinder.findResourceDiff();
        for (String key : ResourceDiffFinder.englishKeys) {
            if (ResourceDiffFinder.toBeTranslated.containsKey(key)) {
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
            }
        }
        RandomValueGenerator.write();
    }
}
