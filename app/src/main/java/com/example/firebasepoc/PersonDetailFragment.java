package com.example.firebasepoc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.firebasepoc.data.Person;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;

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

    public static final short REQUEST_EDIT_PERSON = 8512;

    private static final SimpleDateFormat DOB_FORMAT = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());

    /**
     * The Person this fragment is presenting.
     */
    @State Person mPerson;

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
            appBarLayout.setTitle(mPerson.getFirstname() + " " + mPerson.getLastname());
        }
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    public static class ConfirmDelete extends DialogFragment implements DialogInterface.OnClickListener {

        private Person person;
        public Runnable callback;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            this.person = (Person)getArguments().getSerializable(PersonDetailFragment.ARG_PERSON);

            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Delete " + person.getFullName() + "?")
                    .setPositiveButton("Delete", this)
                    .setNegativeButton("Cancel", null);
            // Create the AlertDialog object and return it
            return builder.create();
        }

        /** Delete clicked */
        @Override
        public void onClick(DialogInterface dialog, int which) {

            if(this.person.getKey() == null) {
                Log.e(App.TAG, "unknown user");
                return;
            }

            Firebase peopleRef = ((App)getActivity().getApplication()).getFbPeopleRef();

            peopleRef.child(this.person.getKey()).setValue(null, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if(callback != null) {
                        callback.run();
                    }
                }
            });
        }
    }

    public void deletePerson(Runnable callback) {

        ConfirmDelete confirmDelete = new ConfirmDelete();
        Bundle args = new Bundle();
        args.putSerializable(PersonDetailFragment.ARG_PERSON, this.mPerson);

        confirmDelete.setArguments(args);
        confirmDelete.show(getFragmentManager(), "confirmdelete");
        confirmDelete.callback = callback;
    }

    public void editPerson(Context packageContext) {
        Intent editIntent = new Intent(packageContext, EditPersonActivity.class);
        editIntent.putExtra(PersonDetailFragment.ARG_PERSON, this.mPerson);
        startActivityForResult(editIntent, REQUEST_EDIT_PERSON);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.person_detail, container, false);

        ButterKnife.bind(this, rootView);

        if (mPerson != null) {
            //populate details
            mVal_first_name.setText(mPerson.getFirstname());
            mVal_last_name.setText(mPerson.getLastname());
            if(mPerson.getBirthDate() != null) {
                mVal_dob.setText(DOB_FORMAT.format(mPerson.getBirthDate()));
            }
            mVal_zip.setText(mPerson.getZip());
        }

        return rootView;
    }
}
