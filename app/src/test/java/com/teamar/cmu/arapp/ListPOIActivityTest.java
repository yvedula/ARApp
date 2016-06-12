package com.teamar.cmu.arapp;


import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;

/**
 * Created by Yash on 11-Jun-16.
 */
public class ListPOIActivityTest extends AndroidTestCase {




    @Before
    public void setUp() throws Exception {

        super.setUp();
    }

    @SmallTest
    public void testWebRequest()
    {
        ListPOIActivity lpa = new ListPOIActivity();
        int n = lpa.makeJsonArrayRequest("https://example.wikitude.com/GetSamplePois/");
        assertEquals(n, 30);
    }

    @SmallTest
    public void testTest()
    {
        ListPOIActivity lpa = new ListPOIActivity();
        int n = lpa.sayHi();
        assertEquals(n, 1);
    }


}