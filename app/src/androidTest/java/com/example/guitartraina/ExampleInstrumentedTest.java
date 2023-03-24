package com.example.guitartraina;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.android.volley.VolleyError;
import com.example.guitartraina.activities.account.Email;
import com.example.guitartraina.activities.account.EncryptedPassword;
import com.example.guitartraina.activities.account.User;
import com.example.guitartraina.api.IResult;
import com.example.guitartraina.api.VolleyService;

import java.nio.charset.StandardCharsets;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    IResult resultCallback = null;
    VolleyService volleyService;

    @Test
    public void requests() {
        Looper.prepare();
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Toast.makeText(appContext, "test", Toast.LENGTH_SHORT).show();
        initVolleyCallback();
        volleyService = new VolleyService(resultCallback, appContext);
        logIn();
        postCodigo();
        changePassword();
        try {
            while (true) {

            }
        } catch (Exception ignored) {
        }
    }

    private void changePassword() {
        volleyService.putStringDataVolley("/ChangePassword?email=" + "a19100060@ceti.mx" + "&token=" + "UlPl73" + "&password=" + "hola");
    }

    private void postCodigo() {
        volleyService.postStringDataVolley("/Tokens?email=a19100060%40ceti.mx");
    }

    private void getProduct() {
        volleyService.getJsonDataVolley("/Guitars/19594");
    }

    private void getProducts() {
        volleyService.getJsonArrayDataVolley("/Guitars/0/10");
    }

    public void registerUser() {
        User user = new User();
        user.setEmail(new Email("hola4@gmail.com"));
        user.setEncryptedPassword(new EncryptedPassword());
        user.setUserName("test");
        user.setRol("Gratis");
        volleyService.postStringDataVolley("/Users?email=a19100069@ceti.mx&username=wEEEESSS&password=$2a$10$MkhvrQf42ZeVZQWnOz/.Ueg/G4ZCSaHe1y0CjAR9BkOgplgAZZxsy");
    }

    public void logIn() {
        volleyService.getStringDataVolley("/LogIn?email=a19100060%40ceti.mx&password=Hh1a1.");
    }

    private void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, Object response) {
                Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
                Toast.makeText(appContext, "Success: "+ response, Toast.LENGTH_LONG).show();
                Log.d("notifySuccess", "Volley requester " + requestType);
                Log.d("notifySuccess", "Volley JSON post" + response);
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
                error.printStackTrace();
                String body = "";
                if (error.networkResponse.data != null) {
                    body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                }
                Toast.makeText(appContext, "failed: " + body + " " + error.networkResponse.statusCode, Toast.LENGTH_LONG).show();
                Log.d("notifyError", "Volley requester " + requestType);
                Log.d("notifyError", "Volley JSON post" + "That didn't work!" + error + " " + error.networkResponse.statusCode);
                Log.d("notifyError", "Error: " + error
                        + "\nStatus Code " + error.networkResponse.statusCode
                        + "\nResponse Data " + body
                        + "\nCause " + error.getCause()
                        + "\nmessage " + error.getMessage());
            }
        };
    }
}