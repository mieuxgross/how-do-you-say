// Author: Michael Gross
// For: CS683 Mobile App Development Winter '14
// Created: 9/15/14

package com.cs683proj.howdoyousay;

import android.support.v4.app.Fragment;

// App starting point
// returns instance of translation query fragment
// 
public class HDYSActivity extends SingleFragmentActivity{

	@Override
    protected Fragment createFragment() {
        return new TransQueryFragment();
    }

}
