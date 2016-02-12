package com.example.firebasepoc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.firebasepoc.data.Person;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseRecyclerAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * An activity representing a list of Persons. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link PersonDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class PersonListActivity extends AppCompatActivity {

    public static final short REQUEST_ADD_PERSON = 1693;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.fab) FloatingActionButton fab;
    @Bind(R.id.person_list) RecyclerView recyclerView;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private Firebase mFirebase;

    private Firebase mPeopleRef;

    public void savePerson(Person person) {
        Firebase newPersonRef = this.mPeopleRef.push();
        newPersonRef.setValue(person);
        person.setKey(newPersonRef.getKey());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_list);

        ButterKnife.bind(this);

        this.mFirebase = new Firebase(getResources().getString(R.string.firebase_url));
        this.mPeopleRef = mFirebase.child("people");

        toolbar.setTitle("My People");

        setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(PersonListActivity.this, EditPersonActivity.class), REQUEST_ADD_PERSON);
            }
        });

        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.person_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        @Bind(R.id.id) TextView mIdView;
        @Bind(R.id.content) TextView mContentView;
        public Person mPerson;

        public PersonViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new PeopleRecyclerViewAdapter());
    }

    public class PeopleRecyclerViewAdapter extends FirebaseRecyclerAdapter<Person, PersonViewHolder> {

        public PeopleRecyclerViewAdapter() {
            super(Person.class, R.layout.person_list_content, PersonViewHolder.class, PersonListActivity.this.mPeopleRef);
        }

        @Override
        protected Person parseSnapshot(DataSnapshot snapshot) {
            Person result = super.parseSnapshot(snapshot);

            //populate primary key so we can edit/delete this Person later
            result.setKey(snapshot.getKey());

            return result;
        }

        @Override
        protected void populateViewHolder(PersonViewHolder holder, final Person person, int position) {
            holder.mPerson = person;
            holder.mIdView.setText(holder.mPerson.getFirstname());
            holder.mContentView.setText(holder.mPerson.getLastname());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putSerializable(PersonDetailFragment.ARG_PERSON, person);
                        PersonDetailFragment fragment = new PersonDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.person_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, PersonDetailActivity.class);
                        intent.putExtra(PersonDetailFragment.ARG_PERSON, person);

                        context.startActivity(intent);
                    }
                }
            });
        }
    };
}
