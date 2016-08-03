package com.cs683proj.howdoyousay;
// fragment for displaying single translation layout
import java.util.UUID;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

public class TransDisplayFragment extends Fragment {
	
	// Extra trans id used to pull UUID passed from TransListFragment's intent
	public static final String EXTRA_TRANS_ID =
			"com.cs683proj.howdoyousay.trans_id";
	
	// Translation object for current translation
	private Translation mTranslation;
	
	//////////////////////////////////////////////////
	// UI elements
	private TextView mDisplaySource;
	private TextView mDisplayTranslated;
	private TextView mDisplayFrom;
	private TextView mDisplayTo;
	private CheckBox mDisplayIsValid;

	// method newInstance returns display fragment
	public static TransDisplayFragment newInstance(UUID transId) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_TRANS_ID, transId);
		
		TransDisplayFragment fragment = new TransDisplayFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setTitle(R.string.translation_title);
        
		// pulling passed UUID
		UUID transId = (UUID)getArguments().getSerializable(EXTRA_TRANS_ID);
		// getting the translation corresponding to pass UUID
		mTranslation = TranslationStore.get(getActivity()).getTranslation(transId);
	}
	
	// inflating view
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.trans_display_frag, parent, false);
		
		// wire mDisplaySource to translation's mSource
		mDisplaySource = (TextView)v.findViewById(R.id.displaySource);
		mDisplaySource.setText(mTranslation.getmSource());
		
		// wire mDisplayTranslated to translation's mTranslated
		mDisplayTranslated = (TextView)v.findViewById(R.id.displayTrans);
		mDisplayTranslated.setText(mTranslation.getmTranslated());
		
		// wire mDisplayFrom to translation's mFrom
		mDisplayFrom = (TextView)v.findViewById(R.id.fromLang);
		mDisplayFrom.setText(mTranslation.getmFrom());
		
		// wire mDisplayTo to translation's mTo
		mDisplayTo = (TextView)v.findViewById(R.id.toLang);
		mDisplayTo.setText(mTranslation.getmTo());
		
		// wire mDisplayIsValid to translation's isValid
		mDisplayIsValid = (CheckBox)v.findViewById(R.id.validCheck);
		mDisplayIsValid.setChecked(mTranslation.getIsValid());
		
		return v;
	}
}

