package com.example.FinFlow.driveAPI;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class DriveRunner {
    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final java.io.File CREDENTIALS_FOLDER = new java.io.File(System.getProperty("user.dir"));
    private static final String CLIENT_SECRET_FILE_NAME = "credentials.json";
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {

        java.io.File clientSecretFilePath = new java.io.File(CREDENTIALS_FOLDER, CLIENT_SECRET_FILE_NAME);
        System.out.println(CREDENTIALS_FOLDER.getAbsolutePath());
        System.out.println("PATH TO CREDENTIALS FOR GOOGLE DRIVE " + clientSecretFilePath.getAbsolutePath());
        if (!clientSecretFilePath.exists()) {
            throw new FileNotFoundException("Please copy " + CLIENT_SECRET_FILE_NAME //
                    + " to folder: " + CREDENTIALS_FOLDER.getAbsolutePath());
        }
        InputStream in = new FileInputStream(clientSecretFilePath);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                clientSecrets, SCOPES).setDataStoreFactory(new FileDataStoreFactory(CREDENTIALS_FOLDER))
                .setAccessType("offline").build();

        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }
    public static Drive getDrive() {
        if (!CREDENTIALS_FOLDER.exists()) {
            CREDENTIALS_FOLDER.mkdirs();
            System.out.println("Created Folder: " + CREDENTIALS_FOLDER.getAbsolutePath());
            System.out.println("Copy file " + CLIENT_SECRET_FILE_NAME + " into folder above.. and rerun this class!!");
            return null;
        }
        try{
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Credential credential = getCredentials(HTTP_TRANSPORT);

            return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential) //
                    .setApplicationName(APPLICATION_NAME).build();
        }catch (IOException | GeneralSecurityException e){
            e.printStackTrace();
        }
        return null;
    }
}
