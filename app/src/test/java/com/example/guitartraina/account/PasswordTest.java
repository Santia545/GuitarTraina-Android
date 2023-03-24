package com.example.guitartraina.account;

import static org.junit.Assert.*;

import com.example.guitartraina.activities.account.Password;

import org.junit.Test;

public class PasswordTest {

    @Test
    public void isValid() {
        Password E= new Password("Hh1a1.");
        assertFalse(E.isValid());

    }
}