package com.example.firebasepoc;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.firebasepoc.data.Person;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.Icicle;

/**
 * A fragment representing a single Person detail screen.
 * This fragment is either contained in a {@link PersonListActivity}
 * in two-pane mode (on tablets) or a {@link PersonDetailActivity}
 * on handsets.
 */
public class PersonDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_PERSON = "mPerson";

    private static final SimpleDateFormat DOB_FORMAT = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());

    /**
     * The Person this fragment is presenting.
     */
    @Icicle private Person mPerson;

    @Bind(R.id.val_first_name) TextView mVal_first_name;
    @Bind(R.id.val_last_name) TextView mVal_last_name;
    @Bind(R.id.val_dob) TextView mVal_dob;
    @Bind(R.id.val_zip) TextView mVal_zip;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PersonDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);

        if(mPerson == null) {
            mPerson = (Person)getArguments().getSerializable(ARG_PERSON);
        }

        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null && mPerson != null) {
            appBarLayout.setTitle(mPerson.firstname + " " + mPerson.lastname);
        }
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    public Person getPerson() {
        return mPerson;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.person_detail, container, false);

        ButterKnife.bind(this, rootView);

        if (mPerson != null) {
            mVal_first_name.setText(mPerson.firstname);
            mVal_last_name.setText(mPerson.lastname);
            mVal_dob.setText(DOB_FORMAT.format(mPerson.getBirthDate()));
            mVal_zip.setText(mPerson.zip);
        }

        return rootView;
    }
}
