package com.test.cinemometar;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ValidatorTest {
    @Test
    public void whenInputIsValid() throws Exception{
        String search = "EDDB";

        Boolean result = MainActivity.validateInput(search);

        assertThat(result).isEqualTo(true);
    }
    @Test
    public void whenInputIsInvalid() throws Exception{
        String search = "";

        Boolean result = MainActivity.validateInput(search);

        assertThat(result).isEqualTo(false);
    }
}