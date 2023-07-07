package com.example.guitartraina;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.android.volley.VolleyError;
import com.example.guitartraina.activities.account.Email;
import com.example.guitartraina.activities.account.EncryptedPassword;
import com.example.guitartraina.activities.account.User;
import com.example.guitartraina.api.IResult;
import com.example.guitartraina.api.VolleyService;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ReadWavFileTest {
    @Test
    public void testReadWavFile() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        // Read the WAV file into a byte array
        byte[] fileData = null;
        try {
            File file = new File(appContext.getExternalFilesDir(null), "audioTest.wav");
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            fis.close();
            bos.close();
            fileData = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            fail("Failed to read the WAV file.");
        }

        // Perform assertions to verify the read operation

        // Assert that the file data is not null
        assertNotNull(fileData);
        String header = new String(fileData, 0, 44);
        Log.d("HEAD", "testReadWavFile: "+header);
        // Assert that the file data is not empty
        assertTrue(fileData.length > 0);
        // Assert that the first four bytes represent the "RIFF" identifier
        assertEquals('R', fileData[0]);
        assertEquals('I', fileData[1]);
        assertEquals('F', fileData[2]);
        assertEquals('F', fileData[3]);

        // Assert any other specific properties or values based on your WAV file's header

        // ... add more assertions as needed

        // If all assertions pass, the test is successful
    }

}