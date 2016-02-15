package com.example.firebasepoc;

import android.content.Intent;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ImageView;

import com.firebase.client.Firebase;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowImageView;
import org.robolectric.util.ActivityController;

import static org.hamcrest.Matchers.hasEntry;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by robin on 2/14/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 19)
public class TestEditPerson {

    private EditText mFld_first_name;
    private EditText mFld_last_name;
    private ShadowImageView mImg_sync_status;
    private Firebase mockPeopleRef;
    private Firebase mockPersonRef;

    @Before
    public void setup() {
        mockPeopleRef = ((TestApp) RuntimeEnvironment.application).mockPeople;
        mockPersonRef = mock(Firebase.class);

        when(mockPeopleRef.push()).thenReturn(mockPersonRef);
    }

    private EditPersonActivity createAddPersonActivity() throws Exception {
        return createEditPersonActivity(new Intent(RuntimeEnvironment.application, EditPersonActivity.class));
    }

    private EditPersonActivity createEditPersonActivity(Intent i) throws Exception {
        ActivityController<EditPersonActivity> activityController = Robolectric.buildActivity(EditPersonActivity.class);

        ActivityController<EditPersonActivity> c = activityController.withIntent(i);

        c.create().start().resume().visible();

        EditPersonActivity activity = activityController.get();

        mFld_first_name = (EditText) activity.findViewById(R.id.fld_first_name);
        mFld_last_name = (EditText) activity.findViewById(R.id.fld_last_name);
        mImg_sync_status = shadowOf((ImageView) activity.findViewById(R.id.img_sync_status));

        return activity;
    }

    Firebase.CompletionListener completionListener = null;

    private void setCompletionListener(Firebase.CompletionListener completionListener) {
        this.completionListener = completionListener;
    }

    @Test
    public void testAddPerson() throws Exception {
        EditPersonActivity activity = createAddPersonActivity();

        //tell our mock to invoke the success callback upon creating a person
        doAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                setCompletionListener((Firebase.CompletionListener) invocation.getArguments()[1]);
                return null;
            }
        }).when(mockPersonRef).setValue(argThat(hasEntry("firstname", "John")), any(Firebase.CompletionListener.class));

        when(mockPersonRef.getKey()).thenReturn("mock_pk");

        mFld_first_name.setText("John");

        //trigger save
        mFld_first_name.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER, 1));

        Assert.assertNotNull(completionListener);

        //verify 'uploading' icon is displayed
        Assert.assertEquals(R.drawable.ic_cloud_upload_black_24dp, mImg_sync_status.getImageResourceId());

        completionListener.onComplete(null, mockPersonRef);

        //verify 'done' icon is displayed
        Assert.assertEquals(R.drawable.ic_cloud_done_black_24dp, mImg_sync_status.getImageResourceId());

        //reset to test update
        setCompletionListener(null);

        //should access existing record
        when(mockPeopleRef.child("mock_pk")).thenReturn(mockPersonRef);

        mFld_last_name.setText("Smith");

        //trigger update
        mFld_last_name.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER, 1));

        //mockPersonRef.setValue should be called
        Assert.assertNotNull(completionListener);

    }
}
