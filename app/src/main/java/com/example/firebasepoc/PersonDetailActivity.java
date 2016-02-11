package com.example.firebasepoc;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.firebasepoc.data.Person;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * An activity representing a single Person detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link PersonListActivity}.
 */
public class PersonDetailActivity extends AppCompatActivity {

    @Bind(R.id.detail_toolbar) Toolbar toolbar;

    private PersonDetailFragment personDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);

        ButterKnife.bind(this);

        setSupportActionBar(this.toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putSerializable(PersonDetailFragment.ARG_PERSON,
                    getIntent().getSerializableExtra(PersonDetailFragment.ARG_PERSON));
            PersonDetailFragment fragment = new PersonDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.person_detail_container, fragment)
                    .commit();
            this.personDetailFragment = fragment;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_person_detail, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public static class ConfirmDelete extends DialogFragment implements DialogInterface.OnClickListener {

        private Person person;

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

            Snackbar.make(((PersonDetailActivity)getActivity()).toolbar, "Deleted 1 Person", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpTo(this, new Intent(this, PersonListActivity.class));
                return true;
            case R.id.action_delete:

                Person person = this.personDetailFragment.getPerson();

                ConfirmDelete confirmDelete = new ConfirmDelete();
                Bundle args = new Bundle();
                args.putSerializable(PersonDetailFragment.ARG_PERSON, person);

                confirmDelete.setArguments(args);
                confirmDelete.show(getSupportFragmentManager(), "confirmdelete");
                break;
            case R.id.action_edit:

                Intent editIntent = new Intent(this, EditPersonActivity.class);
                editIntent.putExtra(PersonDetailFragment.ARG_PERSON,  this.personDetailFragment.getPerson());
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
