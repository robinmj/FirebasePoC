package com.example.firebasepoc;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.firebasepoc.data.Person;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.Icicle;

/**
 * An activity representing a single Person detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link PersonListActivity}.
 */
public class EditPersonActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener {

    @Icicle private Person mPerson;

    @Bind(R.id.detail_toolbar) Toolbar mToolbar;
    @Bind(R.id.fld_first_name) EditText mFld_first_name;
    @Bind(R.id.fld_last_name) EditText mFld_last_name;
    @Bind(R.id.fld_dob) EditText mFld_dob;
    @Bind(R.id.fld_zip) EditText mFld_zip;

    private static final SimpleDateFormat DOB_FORMAT = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());


    private TextView.OnEditorActionListener mFieldChangedListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch(actionId) {
                case EditorInfo.IME_ACTION_UNSPECIFIED:
                case EditorInfo.IME_ACTION_DONE:
                case EditorInfo.IME_ACTION_NEXT:
                    savePerson();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_edit);

        Icepick.restoreInstanceState(this, savedInstanceState);

        ButterKnife.bind(this);

        if(this.mPerson == null) {
            //try to get mPerson from extras if it wasn't in savedInstanceState
            this.mPerson = (Person)getIntent().getSerializableExtra(PersonDetailFragment.ARG_PERSON);
        }

        if(this.mPerson == null) {
            //otherwise, assume we're creating a new mPerson
            this.mPerson = new Person();
        } else {
            //pre-populate fields
            mFld_first_name.setText(this.mPerson.firstname);
            mFld_last_name.setText(this.mPerson.lastname);
            if(this.mPerson.getBirthDate() != null) {
                mFld_dob.setText(DOB_FORMAT.format(this.mPerson.getBirthDate()));
            }
            mFld_zip.setText(this.mPerson.zip);
        }

        mFld_first_name.setOnEditorActionListener(mFieldChangedListener);
        mFld_last_name.setOnEditorActionListener(mFieldChangedListener);
        mFld_dob.setOnFocusChangeListener(mEditDobListener);
        mFld_zip.setOnEditorActionListener(mFieldChangedListener);

        if(this.mPerson.getKey() == null) {
            //otherwise, the activity label is used for the title
            this.mToolbar.setTitle(getResources().getString(R.string.title_add_person));
        }

        //this has to happen after mToolbar.setTitle()
        setSupportActionBar(this.mToolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void savePerson() {
        Person person = this.mPerson;

        person.setFirstname(mFld_first_name.getText().toString());
        person.setLastname(mFld_last_name.getText().toString());
        person.setZip(mFld_zip.getText().toString());

        String key = person.getKey();

        Firebase peopleRef = new Firebase(getResources().getString(R.string.firebase_url)).child("people");

        if(key == null) {
            //insert
            Firebase newPersonRef = peopleRef.push();
            newPersonRef.setValue(person.toMap(), new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    Snackbar.make(mToolbar, "Created New Person", Snackbar.LENGTH_SHORT).show();
                }
            });

            person.setKey(newPersonRef.getKey());
        } else {
            //update
            peopleRef.child(key).setValue(person.toMap(), new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    Snackbar.make(mToolbar, "Person Saved", Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    private TextView.OnFocusChangeListener mEditDobListener = new TextView.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(!hasFocus) {
                //DOB field lost focus- don't open picker
                return;
            }

            Bundle args = new Bundle();

            Long initialTime = EditPersonActivity.this.mPerson.getDob();

            if(initialTime == null) {
                initialTime = System.currentTimeMillis();
            }

            args.putLong(DobPickerFragment.ARG_INITIAL_TIME, initialTime);

            DobPickerFragment dobPicker = new DobPickerFragment();
            dobPicker.setArguments(args);
            dobPicker.show(getSupportFragmentManager(), "dobpicker");
        }
    };

    public static class DobPickerFragment extends DialogFragment {
        public static final String ARG_INITIAL_TIME = "initial_time";

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(getArguments().getLong(ARG_INITIAL_TIME));

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(),
                    (DatePickerDialog.OnDateSetListener)getActivity(),
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(year,month,day);
        this.mPerson.setDob(calendar.getTimeInMillis());
        this.mFld_dob.setText(DOB_FORMAT.format(this.mPerson.getBirthDate()));
        savePerson();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, PersonListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
