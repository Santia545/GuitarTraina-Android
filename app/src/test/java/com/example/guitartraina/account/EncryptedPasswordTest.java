package com.example.guitartraina.account;

import static org.junit.Assert.*;

import com.example.guitartraina.activities.account.EncryptedPassword;
import com.example.guitartraina.activities.account.Password;

import org.junit.Test;

public class EncryptedPasswordTest {

    @Test
    public void test() {
        Password E= new Password("Hh1azz.");
        EncryptedPassword bdPass= new EncryptedPassword(E);
        assertTrue(bdPass.comparePassword("Hh1azz."));
    }
}