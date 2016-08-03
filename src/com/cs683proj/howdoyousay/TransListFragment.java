package com.cs683proj.howdoyousay;
// fragment for translation list layout
import java.util.ArrayList;
import java.util.Locale;

import com.memetix.mst.language.Language;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TransListFragment extends ListFragment {
	
	// TAG for logging indentification
	private static final String TAG = "TransListFragment";
	
	// storage structure for translations
	private ArrayList<Translation> mTranslations;
	
	// when launched, create store, wire to adapter
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.translations_title);
        mTranslations = TranslationStore.get(getActivity()).getTranslations();
        
        TransAdapter adapter = new TransAdapter(mTranslations);
        
        setListAdapter(adapter);
    }
	
	// method for list item change
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Pick a historical translation
		Translation t = ((TransAdapter)getListAdapter()).getItem(position);
		Log.d(TAG, t.getmSource() + " was clicked");
		
		// Start TransDisplayActivity to view historical translation
		Intent i = new Intent(getActivity(), TransDisplayActivity.class);
		// pass along t's unique ID
		i.putExtra(TransDisplayFragment.EXTRA_TRANS_ID, t.getmId());
		startActivity(i);
	}

	// update TransAdapter on resume lifecycle change
	@Override
    public void onResume() {
        super.onResume();
        ((TransAdapter)getListAdapter()).notifyDataSetChanged();
    }

	// anonymous inner class for TransAdapter
	private class TransAdapter extends ArrayAdapter<Translation> implements OnInitListener {
		
		// my tts object
		private TextToSpeech mtts;
		
		// pull down reference through activity
		public TransAdapter(ArrayList<Translation> translations) {
			super(getActivity(), 0, translations);
		}
		
		// inflate view
		@Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        // If we weren't given a view, inflate one
	        if (convertView == null) {
	            convertView = getActivity().getLayoutInflater()
	                .inflate(R.layout.list_item_trans, parent, false);
	        }
	        // Configure the view for this Translation
	        Translation t = getItem(position);
	        
	        // to display source text of translation
	        TextView sourceTextView =
	            (TextView)convertView.findViewById(R.id.trans_list_item_mSourceTextView);
	        sourceTextView.setText(t.getmSource());
	        
	        // to display translated text of translation
	        TextView translatedTextView =
	            (TextView)convertView.findViewById(R.id.trans_list_item_mTranslatedTextView);
	        translatedTextView.setText(t.getmTranslated());
	        
	        // wire up speak button
	        mtts = new TextToSpeech(getActivity(),this);
	        ImageButton speakButton = 
	        		(ImageButton)convertView.findViewById(R.id.trans_list_item_speakButton);
	        speakButton.setTag(position);
	        speakButton.setOnClickListener(new Button.OnClickListener() {
	        	@SuppressLint("DefaultLocale") @Override
	        	public void onClick(View v) {
	        		// index pulled through clicked v-tag
	        		Integer index = (Integer) v.getTag();
	        		// now select the appropriate translation in store
	        		Translation t = ((TransAdapter)getListAdapter()).getItem(index);
	        		// and speak it aloud
	        		speakOut(t.getmTranslated(),Language.valueOf(t.getmTo().toUpperCase()));
	        	}
	        });
	        
	        // wire up delete button
	        ImageButton deleteButton = 
	        		(ImageButton)convertView.findViewById(R.id.trans_list_item_closeButton);
	        deleteButton.setTag(position);
	        deleteButton.setOnClickListener(new Button.OnClickListener() {
	        	@Override
	        	public void onClick(View v) {
	        		// index pulled through clicked v-tag
	        		Integer index = (Integer) v.getTag();
	        		// delete item from store
	        		((TransAdapter)getListAdapter()).remove(((TransAdapter)getListAdapter()).getItem(index));
	        		// update the adapter
	        		((TransAdapter)getListAdapter()).notifyDataSetChanged();
	        	}
	        });
	        
	        return convertView;
	    } // end method getView
		
		//////////////////////////////////////////////////
		// method onInit initializes the text-to-speech engine
		// will log tts initialization status
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
		}
		
		// speakOut method for tts on TransList
		@SuppressWarnings("deprecation")
		private void speakOut(String text, Language l) {
			
			if(l == Language.ENGLISH ||
					l == Language.FRENCH ||
					l == Language.GERMAN ||
					l == Language.ITALIAN ||
					l == Language.SPANISH) {
					
				switch(l) {
					
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
					mtts.setLanguage(new Locale("es", "ES"));
					break;
				
				default:
					break;
				
				}
				mtts.speak(text, TextToSpeech.QUEUE_FLUSH, null);	
			} else {
				// default tts engine only supports 5 languages, this will address the rest
				Toast.makeText(getActivity(),
	                    "Language not supported",
	                    Toast.LENGTH_SHORT).show();
				mtts.setLanguage(Locale.US);
				// at least speaking out error message
				mtts.speak("Language not supported", TextToSpeech.QUEUE_FLUSH, null);
			}
			
		} // end method speakOut
	} // end anonymous inner class TransAdapter
} // end class TransListFragment