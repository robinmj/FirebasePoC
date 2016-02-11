package com.example.firebasepoc;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.example.firebasepoc.data.Person;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.text.SimpleDateFormat;
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
public class EditPersonActivity extends AppCompatActivity {

    @Icicle private Person person;

    @Bind(R.id.detail_toolbar) Toolbar toolbar;
    @Bind(R.id.fld_first_name) EditText fld_first_name;
    @Bind(R.id.fld_last_name) EditText fld_last_name;
    @Bind(R.id.fld_dob) EditText fld_dob;
    @Bind(R.id.fld_zip) EditText fld_zip;

    private static final SimpleDateFormat DOB_FORMAT = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());


    private TextView.OnEditorActionListener nameValidator = new TextView.OnEditorActionListener() {
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

        if(this.person == null) {
            //try to get person from extras if it wasn't in savedInstanceState
            this.person = (Person)getIntent().getSerializableExtra(PersonDetailFragment.ARG_PERSON);
        }

        if(this.person == null) {
            //otherwise, assume we're creating a new person
            this.person = new Person();
        } else {
            //pre-populate fields
            fld_first_name.setText(this.person.firstname);
            fld_last_name.setText(this.person.lastname);
            if(this.person.getBirthDate() != null) {
                fld_dob.setText(DOB_FORMAT.format(this.person.getBirthDate()));
            }
            fld_zip.setText(this.person.zip);
        }

        fld_first_name.setOnEditorActionListener(nameValidator);
        fld_last_name.setOnEditorActionListener(nameValidator);
        fld_dob.setOnEditorActionListener(nameValidator);
        fld_zip.setOnEditorActionListener(nameValidator);

        if(this.person.getKey() == null) {
            //otherwise, the activity label is used for the title
            this.toolbar.setTitle(getResources().getString(R.string.title_add_person));
        }

        //this has to happen after toolbar.setTitle()
        setSupportActionBar(this.toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void savePerson() {
        Person person = this.person;

        person.setFirstname(fld_first_name.getText().toString());
        person.setLastname(fld_last_name.getText().toString());
        person.setZip(fld_zip.getText().toString());

        String key = person.getKey();

        Firebase peopleRef = new Firebase(getResources().getString(R.string.firebase_url)).child("people");

        if(key == null) {
            //insert
            Firebase newPersonRef = peopleRef.push();
            newPersonRef.setValue(person.toMap(), new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    Snackbar.make(toolbar, "Created New Person", Snackbar.LENGTH_SHORT).show();
                }
            });

            person.setKey(newPersonRef.getKey());
        } else {
            //update
            peopleRef.child(key).setValue(person.toMap(), new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    Snackbar.make(toolbar, "Person Saved", Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
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
