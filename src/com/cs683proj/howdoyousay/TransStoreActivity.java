package com.cs683proj.howdoyousay;

import android.support.v4.app.Fragment;

public class TransStoreActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new TransListFragment();
	}

}
