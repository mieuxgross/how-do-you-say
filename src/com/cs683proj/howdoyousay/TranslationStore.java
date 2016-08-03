package com.cs683proj.howdoyousay;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;

// setting up ArrayList to store Translation objects
public class TranslationStore {
	private ArrayList<Translation> mTranslations;
	
	private static TranslationStore sTranslationStore;
	@SuppressWarnings("unused")
	private Context mAppContext;
	
	// constructor preserves context through lifecycle
	private TranslationStore(Context appContext) {
		mAppContext = appContext;
		mTranslations = new ArrayList<Translation>();
		//////////////////////////////////////
		//Test implementation 100 Translations
		/*
		for (int i = 0; i < 100; i++) {
			Translation t = new Translation();
			t.setmSource("known lang snip #" + i);
			t.setIsValid(i % 2 == 0);
			mTranslations.add(t);
		}
		*/
		//End of test implementation
		//////////////////////////////////////
		
	}
	
	// get ensures non-null context
	public static TranslationStore get(Context c) {
		
		if (sTranslationStore == null) {
			sTranslationStore = new TranslationStore(c.getApplicationContext());
		}
		return sTranslationStore;
	}
	
	// method stores new translation
	public void addTranslation(Translation t){
		mTranslations.add(t);
	}
	
	// returns store
	public ArrayList<Translation> getTranslations() {
		return mTranslations;
	}
	
	// returns specific translation
	public Translation getTranslation(UUID id) {
		for (Translation t : mTranslations) {
			if (t.getmId().equals(id))
				return t;
		}
		return null;
	}
}
