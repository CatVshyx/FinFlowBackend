package com.example.FinFlow.driveAPI;

import com.google.api.client.auth.oauth2.Credential;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.oauth2.ServiceAccountCredentials;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class DriveRunner {
    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final String credentials = " { \"type\": \"service_account\",\n" +
            "  \"project_id\": \"datasource-381016\",\n" +
            "  \"private_key_id\": \"c2b0903ab83112792d52fef27583ac5af2ae0196\",\n" +
            "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDDYbNZfpf/mvZa\\nVkYax/Tv8YBWPD1j7CatgFd1gcPGNK7GeqLv6lsnqeL/qE3MlQF5YQO5vEFWb6zD\\nRgvYcEERxg7S2DgqPpfKjBc9hsIKoGgM0Jv266Qexqxcmv0P9wdoVxFq5SfzpOS7\\nrs19l45aAJLnpUXcGfp0g6i4lukhwEmFJ44f4PyeXBqsu+kbXik3uPNzGwcep4vG\\n7LXL67e4wRTnqHijC+4i2GcbGEbxQw3T/6L6x6O3Fv7LL0R78rhmfnEz5r9RxTnp\\nqqMBsDhVPKH0u5scXapiQtrM2/dxogP1+xwCbfyTwkIOzgVpQF+y39CTbMxNSbsP\\nzS7oqMBFAgMBAAECggEASCt+pEAuxSvoc9YKbzoNL9R1I2V9B7vjTZNquu0D8+Lh\\n7E7AgP6ATVZjkywIENm5gaxkQ+dV+yelGTx2g6xzXK1cLGXR2UjlqNu2efOdGW/G\\n/3TPJ5ZDOVsJr+tQLuWAxvniZSngaA8wrmMTZKDm5DfyC77w3yx5ZzB60jRsSRqn\\n3ZMvnrp6flzpwlNx2J1mRBQDo5BLSG/xDPnl7XKQMU2xpIDF8IGtG/GG9QlGNe/W\\nU45seNqEP1t0N8HgPMi+uqfTF6f0tj1A5O8YJZqBgNW3vIYHhbbfTNK/WQVVAdTh\\nEfYjawv64p/0Nb1hP4sQYlNb5AkYrCbsy0Zu0bxSwQKBgQD5JtJLMz4uzYWLo0YV\\n/vT99N85/GFLhow198NCXwnDqNwYNpNl+9jWUTpEkK2WBuZO4MxwKUGxFheE3vku\\nukrxHN6r7uVgyi3wk85B/fcX71vjl9781mB3CSqccvEGcNAp54avVcAFEysTOn2i\\niLiMELe+KzUDUVpC279Iqul+rwKBgQDIwIWDXpH0o2oD9l7E8JiOkOmMNQjC8ldN\\nCxy661rJjUtSc/m4oAM8DYducTQW85q2dD/jYvYof9Z98xWDlOQ6/A+iwhjB3ooD\\nbrvBEhO+JFIvtAYHj5uwLcH97kHDEv0i6NDrj+wAJXI5mRO9F+XnHnqpFhphCicK\\ntR62cLpNSwKBgGBhbCg7AmsIhgimeI45unwjJddAkkKtIcAewCIEsbpGsB2NvydM\\nbW06rtenP/8xXBZXT75ztz3R1fdIagxxdFwhVuIb4A+5JlxSUQ+mNtfDeEBql5An\\nPdnOafVrYWGkcmJDdg6NlWJhL+CYV9FwIjPMo8ek3zURW2FRsIVj2D9xAoGBAJI0\\ntIgyrfYPtbxhCDBCUbkzDQTR8ymXucEgoLH5istTnlHUqUCvOOvZppLm21sD29+Z\\n1pXSkxh30k00ZqzIGVhkDzcoSUOYwccyNIeulLZcWYbog6OqEWdETA6WIUrjX3y3\\nNsD4tpZ8ry4HsHp9FzE133uZ32hhLoH72vxgKS71AoGADgYSf/TVaNJXcc4k5sXz\\nx9feCKrOM8MsCeB4xItqA4uUjGnCX1ctcCLdR0sQE0qHAoWvih3/i7wFxl9WW+vb\\n8cDzU239e4/nQCmJXYZRmOSw+0CpGvMWmNLm0L8DKjELbEpgzD+Ho3OV5zjvfDtg\\nNZoAaDCBZyItQsHxDq3jFAw=\\n-----END PRIVATE KEY-----\\n\",\n" +
            "  \"client_email\": \"finance-flow@datasource-381016.iam.gserviceaccount.com\",\n" +
            "  \"client_id\": \"113367597596936969249\",\n" +
            "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
            "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
            "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
            "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/finance-flow%40datasource-381016.iam.gserviceaccount.com\"\n" +
            "}";

    private static Drive getService() throws IOException, GeneralSecurityException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        GoogleCredential credential = GoogleCredential.fromStream(new ByteArrayInputStream(credentials.getBytes())).createScoped(SCOPES);

        Drive service = new Drive.Builder(httpTransport, JSON_FACTORY,credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

        return service;
    }


    public static Drive getDrive() {
        try {
            return getService();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }

    }
}
