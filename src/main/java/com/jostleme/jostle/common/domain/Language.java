package com.jostleme.jostle.common.domain;

/**
 * Enum for languages we support.
 * @author LenT
 *
 */
public enum Language {
	AUTO_DETECT( "autoDetect" ),
	ENGLISH( "en" ),
	SPANISH( "es" ),
	PORTUGUESE( "pt_BR" ),
	FRENCH( "fr" ),
	DUTCH( "nl" ),
	GERMAN( "de" ),
	JAPANESE( "ja" );

	private final String locale;
	
	public String getLocale() {
		return locale;
	}
	
	// Constructor
	Language(String locale ) {
		this.locale = locale;
	}


public static Language stringLocaleToLanguage(String locale ) {
	for(Language language: Language.values()) {
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
