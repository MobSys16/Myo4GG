package de.uni_luebeck.imis.gestures.adapter;

import de.uni_luebeck.imis.gestures.R;

/**
 * @author Finn
 * @since 14.09.2016
 *
 * Class description...
 */
public enum CustomPagerEnum {
    PATIENT_OVERVIEW(R.layout.fragment_myo_test_1),
    PATIENT_HEART(R.layout.fragment_myo_test_2),
    PATIENT_LUNG(R.layout.fragment_myo_test_3);

    private int mLayoutResId;

    CustomPagerEnum (int layoutResId) {
        mLayoutResId = layoutResId;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }
}
