package com.cs683proj.howdoyousay;
//fragment for main translation query layout

import java.util.ArrayList;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.memetix.mst.detect.Detect;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

public class TransQueryFragment extends Fragment implements OnInitListener{
	
	// TAG for logging indentification
	private static final String TAG = "TransQuerytFragment";
	
	// Translation object for current translation
	private Translation mTranslation;

	// ArrayList to store all current session translations
	private ArrayList<Translation> mTranslations;
	
	//////////////////////////////////////////////////
	// UI elements
	private Spinner mFromLangPicked;		// language from
	private Spinner mToLangPicked;			// language to
	private EditText mInboundQuery;			// holding my current query
	private TextView mTranslatedResult;		// to display results
	private Button mGoButton;				// translate button
	private Button mSpeakButton;			// tts button
	private Button mPastQueriesButton;		// to get to all session translations
	
	//////////////////////////////////////////////////
	// Core add-ons
	private TextToSpeech mtts;				// mtts is object of text-to-speech engine
	private Language translatedResultLang;	// holding current language of result
	
	//////////////////////////////////////////////////
	// from/to supported language arrays
	private String[] flanglist = {"Auto-Detect","Arabic","Bulgarian","Catalan","Chinese_Simplified","Chinese_Traditional","Czech","Danish","Dutch","English","Estonian","Finnish","French","German","Greek","Haitian_Creole","Hebrew","Hindi","Hmong_Daw","Hungarian","Indonesian","Italian","Japanese","Latvian","Lithuanian","Malay","Norwegian","Persian","Polish","Portuguese","Romanian","Russian","Slovak","Slovenian","Spanish","Swedish","Thai","Turkish","Ukrainian","Urdu","Vietnamese"};
	private String[] tlanglist = {"Arabic","Bulgarian","Catalan","Chinese_Simplified","Chinese_Traditional","Czech","Danish","Dutch","English","Estonian","Finnish","French","German","Greek","Haitian_Creole","Hebrew","Hindi","Hmong_Daw","Hungarian","Indonesian","Italian","Japanese","Latvian","Lithuanian","Malay","Norwegian","Persian","Polish","Portuguese","Romanian","Russian","Slovak","Slovenian","Spanish","Swedish","Thai","Turkish","Ukrainian","Urdu","Vietnamese"};
	
	//////////////////////////////////////////////////
	// method to detect language through "Auto-Detect"
	public String detect(String text) throws Exception{
		// Set the Client ID / Client Secret statically
		Detect.setClientId("HowDoYouSay");
	    Detect.setClientSecret(“**client_secret_here**”);
	    
	    // wrapper API call
	    Language detection = Detect.execute(text);

	    //Ideally, I'd changed this to support running the program in a different source language
	    //This returns the detected language in localized English
		return detection.getName(Language.ENGLISH);
	}
	
	//////////////////////////////////////////////////
	// method to translate
	@SuppressLint("DefaultLocale") 
	public String translate(String text, String from, String to) throws Exception{
	    // Set the Client ID / Client Secret statically
		Translate.setClientId("HowDoYouSay");
	    Translate.setClientSecret("**client_secret_here**");
	    
	    // initialize result
	    String translatedText = "";
	    
	    // wrapper API call
	    translatedText = Translate.execute(text,Language.valueOf(from.toUpperCase()),Language.valueOf(to.toUpperCase()));
	    
	    return translatedText;
	}
	
	//////////////////////////////////////////////////
	// method detects underlying internet connection
	// -> can't translate without it!
	public boolean checkInternetConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) {
			// yay connected
			return true;
		} else {
			// no dice
			return false;
    	}
	}

	
	//////////////////////////////////////////////////
	// method onCreate survives activity lifecycle restoration
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get my TranslationStore singleton
		mTranslations = TranslationStore.get(getActivity()).getTranslations();
		getActivity().setTitle(R.string.app_title);
		setHasOptionsMenu(true);
		
	}
	
	//////////////////////////////////////////////////
	// inflate main TranQueryFragment view
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		
		// inflate main view
		View v = inflater.inflate(R.layout.trans_query_frag, parent, false);
		
		// instantiate translation object
		mTranslation = new Translation();
		
		// Wiring the editText
		mInboundQuery = (EditText)v.findViewById(R.id.sourcequery);
        mInboundQuery.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(
                    CharSequence c, int start, int before, int count) {
            		// stringify my char sequence and push to source
                	mTranslation.setmSource(c.toString());
            }
            public void beforeTextChanged(
                    CharSequence c, int start, int count, int after) {
                // This space intentionally left blank
            }
            public void afterTextChanged(Editable c) {
                // This one too
            }
		
        });
        
        // Wiring the TextView for displaying result
        mTranslatedResult = (TextView)v.findViewById(R.id.translatedresult);

        // Wiring the from spinner
        mFromLangPicked = (Spinner)v.findViewById(R.id.fromlangspinner);
        
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,flanglist);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mFromLangPicked.setAdapter(dataAdapter1);

        mFromLangPicked.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        	// once selected set mFrom
        	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        		mTranslation.setmFrom(parent.getItemAtPosition(position).toString());
        	}

        	@Override
        	public void onNothingSelected(AdapterView<?> parent) {
        		// Can't select nothing, defaults onto the first language in the language array (auto-detect)
        	}
        });         
        
        // Wiring the to spinner
        mToLangPicked = (Spinner)v.findViewById(R.id.tolangspinner);
        
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,tlanglist);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mToLangPicked.setAdapter(dataAdapter2);
        
        mToLangPicked.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        	// once selected set mTo
        	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        		mTranslation.setmTo(parent.getItemAtPosition(position).toString());
        	}

        	@Override
        	public void onNothingSelected(AdapterView<?> parent) {
        		//Can't select nothing, defaults onto the first language in the language array (arabic)
        	}
        });  
        
        // Wiring the Go button
        mGoButton = (Button)v.findViewById(R.id.gobutton);
        mGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	// anonymous inner class to run translations asynchronously
            	// prevents UI blocking while waiting for translation result
            	class bgStuff extends AsyncTask<Void, Void, Void>{
                	
            		// Parameters for translation
                    String translatedText = "";
                    String reversedText = "";
                    String frmLang = "";
                    //String src = mTranslation.getmSource(); // for troubleshooting
                    
                    Translation sub = new Translation();
                    
                    @Override
                    protected Void doInBackground(Void... params) {
                    	
                    	// if auto-detecting the language
                    	if(mTranslation.getmFrom() == "Auto-Detect") {
                    		try {
                    			frmLang = detect(mTranslation.getmSource());
                    		} catch (Exception e) {
                    			e.printStackTrace();
                    		}
                    	}
                    	else {
                    		frmLang = mTranslation.getmFrom();
                    	}
                		// now try the translation
                		try { 
                    		translatedText = translate(mTranslation.getmSource(),frmLang,mTranslation.getmTo()); 
                    	} catch (Exception e) {
                    		e.printStackTrace();
                    		//translatedText = e.toString(); // for troubleshooting
                    	}
                    	
                    	// Now try the reverse translation
                        try{
                        	reversedText = translate(translatedText,mTranslation.getmTo(),frmLang);
                        } catch (Exception e) {
                        	e.printStackTrace();
                        	//reversedText = e.toString(); // for troubleshooting
                        }
                        return null;
                    }
                    
                    @SuppressLint("DefaultLocale") @Override
                    protected void onPostExecute(Void result) {
                        // After receiving results, show the translation, then add the results to TranslationStore
                    	/*
                    	 * Toast as stub, save for troubleshooting
                    	Toast.makeText(getActivity(),
                                translatedText,
                                Toast.LENGTH_SHORT).show();
                    	*/
                    	
                    	// Setting sub's members
                    	
                    	// Showing from language as detected language, and not "Auto-Detect"
                    	if (mTranslation.getmFrom() == "Auto-Detect") {
                    		sub.setmFrom(frmLang);
                    	} else {
                    		sub.setmFrom(mTranslation.getmFrom());
                    	}
                    	sub.setmSource(mTranslation.getmSource());
                    	sub.setmTo(mTranslation.getmTo());
                        sub.setmTranslated(translatedText);
                        
                        // Here isValid is True is meaning is preserved
                        // ie: if mSource == reversedText
                        sub.setIsValid(mTranslation.getmSource().equalsIgnoreCase(reversedText));
                        
                        // Now add it to the Translations singleton
                        mTranslations.add(sub);
                        translatedResultLang = Language.valueOf(mTranslation.getmTo().toUpperCase());
                        mTranslatedResult.setText(translatedText);
                        super.onPostExecute(result);
                    }

                     
                } // end inner anonymous class bgStuff
            	
            	// main execution of asynchronous task
            	
            	// Check for connectivity, if available, run task
                if(checkInternetConnection(getActivity())) {
                	new bgStuff().execute();
                } else { // if check is false, network is down
                	Toast.makeText(getActivity(),
                            "network is down",
                            Toast.LENGTH_SHORT).show();
                }
            } // end method onClick
        }); // end mGoButton setOnClickListener
        
        mtts = new TextToSpeech(getActivity(),this);
        mSpeakButton = (Button)v.findViewById(R.id.speakbutton);
        mSpeakButton.setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		
        		speakOut(mTranslatedResult.getText().toString());
        	}
        }); // end mSpeakButton setOnClickListener
        
        mPastQueriesButton = (Button)v.findViewById(R.id.pastqueriesbutton);
        mPastQueriesButton.setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		// Create intent to start TransStoreActivity
        		Intent i = new Intent(getActivity(), TransStoreActivity.class);
        		// launch with intent
        		startActivity(i);
        	}
        }); // end mPastQueriesButton setOnClickListener

		return v;
	} // end method onCreateView
	
	
	//////////////////////////////////////////////////
	// method inflates my options menu
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.hdys_menu, menu);
	}

	//////////////////////////////////////////////////
	// method onInit initializes the text-to-speech engine
	@Override
	public void onInit(int status) {
	   if (status == TextToSpeech.SUCCESS) {
	        
		   	
	        int result = mtts.setLanguage(Locale.US);
	 
	        if (result == TextToSpeech.LANG_MISSING_DATA
	                || result == TextToSpeech.LANG_NOT_SUPPORTED) {
	            Log.e(TAG, "This Language is not supported");
	        } else {
	        	Log.i(TAG, "Initilization Success!"); 
	        }
	 
	    } else {
	        Log.e(TAG, "Initilization Failed!");
	    }
	} // end method onInit
	
	
	//////////////////////////////////////////////////
	// method invokes text-to-speech engine
	@SuppressWarnings("deprecation")
	private void speakOut(String text) {
		
		// the default Android tts engine currently only handles a handful of languages
		// (English, French, German, Italian, Spanish)
		// must endeavor to find broader engine
		
		// If one of the supported tts engine languages
		if(translatedResultLang == Language.ENGLISH ||
				translatedResultLang == Language.FRENCH ||
				translatedResultLang == Language.GERMAN ||
				translatedResultLang == Language.ITALIAN ||
				translatedResultLang == Language.SPANISH) {
				
			switch(translatedResultLang) {
				
			case ENGLISH:
				mtts.setLanguage(Locale.ENGLISH);
				break;
			
			case FRENCH:
				mtts.setLanguage(Locale.FRENCH);
				break;
				
			case GERMAN:
				mtts.setLanguage(Locale.GERMAN);
				break;
				
			case ITALIAN:
				mtts.setLanguage(Locale.ITALIAN);
				break;
				
			case SPANISH:
				// no direct supported locale
				mtts.setLanguage(new Locale("es", "ES"));
				break;
			
			default: // should never get used as per above if statement
				break;
			
			} // end switch(translatedResultLang)
			// invoke engine with QUEUE_FLUSH to permit interruption
			mtts.speak(text, TextToSpeech.QUEUE_FLUSH, null);	
		} else { // else the langauge isn't tts supported
			// short toast
			Toast.makeText(getActivity(),
                    "Language not supported",
                    Toast.LENGTH_SHORT).show();
			mtts.setLanguage(Locale.US);
			// at least demonstrating a capability of speaking with error message in English
			// invoke engine with QUEUE_FLUSH to permit interruption
			mtts.speak("Language not supported", TextToSpeech.QUEUE_FLUSH, null);
		}	
	} // end method speakOut
} // end class TransQueryFragment
