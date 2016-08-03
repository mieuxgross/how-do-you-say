package com.cs683proj.howdoyousay;

import java.util.UUID;

import android.support.v4.app.Fragment;

public class TransDisplayActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		
		UUID transId = (UUID)getIntent().getSerializableExtra(TransDisplayFragment.EXTRA_TRANS_ID);
	
		return TransDisplayFragment.newInstance(transId);
	}
}
