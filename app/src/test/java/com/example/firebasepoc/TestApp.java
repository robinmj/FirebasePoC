package com.example.firebasepoc;

import com.firebase.client.Firebase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by robin on 2/13/16.
 */
public class TestApp extends App {

    public Firebase mockFirebase = mock(Firebase.class);

    public Firebase mockPeople = mock(Firebase.class);

    public Firebase createFirebase() {
        when(mockFirebase.child("people")).thenReturn(mockPeople);

        return mockFirebase;
    }
}
