package com.jostleme.jostle.common.domain;

import java.text.DateFormat;
import java.util.Date;

/**
 * Enum for languages we support.
 * @author LenT
 *
 */
public enum Language {
    AUTO_DETECT("autoDetect", "MMMM d"),
    ENGLISH("en", "MMMM d"),
    SPANISH("es", "d' de 'MMMM"),
    PORTUGUESE("pt_BR", "d' de 'MMMM"),
    FRENCH("fr", "d MMMM"),
    DUTCH("nl", "d MMMM"),
    GERMAN("de", "d. MMMM"),
    JAPANESE("ja", "MM/dd");

    private final String locale;
    private final String monthAndDayFormat;

    public String getLocale() {
        return locale;
    }

    public String getMonthAndDayFormat() {
        return monthAndDayFormat;
    }

    // Constructor
    Language( String locale, String monthDayFormat ) {
        this.locale = locale;
        this.monthAndDayFormat = monthDayFormat;
    }

    public static Language stringLocaleToLanguage( String locale ) {
        for(Language language:Language.values()) {
            if(language.getLocale().equals(locale))
                return language;
        }
        return null;
    }

    public static boolean isAutoDetect(String stringLocale) {
        if(stringLocale != null && stringLocale.equals(AUTO_DETECT.getLocale()))
            return true;
        return false;
    }

}
