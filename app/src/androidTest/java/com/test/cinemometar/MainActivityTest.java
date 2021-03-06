package com.test.cinemometar;

import static org.junit.Assert.*;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mainActivity = null;

    @Before
    public void setUp() throws Exception {
        mainActivity = mainActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch(){
        View view = mainActivity.findViewById(R.id.searchEditTxt);
        assertNotNull(view);
    }

    @After
    public void tearDown() throws Exception {
        mainActivity = null;
    }
}