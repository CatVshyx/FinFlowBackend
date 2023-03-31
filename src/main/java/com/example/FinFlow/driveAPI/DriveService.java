package com.example.FinFlow.driveAPI;

import com.example.FinFlow.additional.Response;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.*;
import com.google.api.services.drive.Drive;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import java.io.*;
import java.util.*;

@Service
public class DriveService{
    private static final Drive drive = DriveRunner.getDrive();
    public static final String PHOTO_FOLDER = "1-MJtymNF3KoGw85gdDbk1C1Owj0icij-";
    public static final String INFO_ID = "1DfbPIAKPZjLq4kjgVYXKl4Xxw_BYLrel";
    public static final String HELP_ID = "1KUBaJwGAaIPT66e7yX32txtew-0cnoH1";
    public DriveService(){
        System.out.println("Drive service initialized");

        getAllFiles(null);
    }

    public boolean isDownloadable(com.google.api.services.drive.model.File f){
        System.out.println(f.getMimeType() + " MIME TYPE");
        if (f.getMimeType().contains("folder")
                || f.getMimeType().contains("shortcut")
                || f.getMimeType().contains("vnd.google-apps")){
            System.out.println("THIS IS a FOLDER or shortcut on supports google apps only");
            return false;
        }
        return true;
    }
    public Map.Entry<InputStream,String> downloadFileAsInputStream(String fileId){
        if (fileId == null){return null;}
        com.google.api.services.drive.model.File file;
        try {
            file = drive.files().get(fileId).execute();
            if (!isDownloadable(file)){ return null; }
            return new AbstractMap.SimpleEntry<>(drive.files().get(fileId).executeMediaAsInputStream(), file.getMimeType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public List<com.google.api.services.drive.model.File> getAllFiles(String folder){
        List<com.google.api.services.drive.model.File> files;
        try {
            Drive.Files.List list = drive.files().list();
            if (folder != null){ list.setQ("'%s' in parents".formatted(PHOTO_FOLDER)); }

            files = list.setFields("nextPageToken, files(id, name, mimeType)").execute().getFiles();
            files.forEach(file -> {
                System.out.printf("%s (%s) - %s \n", file.getName(), file.getId(),file.getMimeType());
            });
            return files;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public  Response updateFile(String fileId, InputStream metadata) {
        try{
            com.google.api.services.drive.model.File fileFromDrive = drive.files().get(fileId).execute();
            com.google.api.services.drive.model.File file = new com.google.api.services.drive.model.File();

            file.setMimeType(fileFromDrive.getMimeType());
            InputStreamContent fileContent = new InputStreamContent(file.getMimeType(), metadata);

            drive.files().update(fileFromDrive.getId(),null,fileContent).execute();
            return new Response( "The file was updated",200);
        }catch (IOException e){
            return new Response( e.getMessage(),500);
        }


    }
    public  Response uploadFile(String filename,InputStream uploadedFile, String folderId){
        com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
        String ext = filename.substring(filename.indexOf('.'));
        fileMetadata.setName(filename);
        fileMetadata.setParents(Collections.singletonList(folderId));
        System.out.println("uploading file " + filename );
        InputStreamContent fileContent = new InputStreamContent(MimeTypeParser.getMimeByExtension(ext), uploadedFile);
        try {
            com.google.api.services.drive.model.File created = drive.files().create(fileMetadata, fileContent).setFields("id").execute();
            System.out.println(created.getId());
            return new Response(created.getId(),200);
        } catch (IOException e) {
            e.printStackTrace();
            return new Response(e.getMessage(),500);
        }
    }
    public  Response deleteFile(String fileId){
        try {
            drive.files().delete(fileId).execute();
            System.out.println( " DELETED successfully" + fileId);
            return new Response("Deleted",200);
        } catch (IOException e) {
            System.out.println("FILE NOT FOUND OR STH ELSE");
            e.printStackTrace();
            return new Response("Sth went wrong",200);
        }
    }
    public Response createFolder(String name){
        com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
        fileMetadata.setName(name);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        try {
            com.google.api.services.drive.model.File file = drive.files().create(fileMetadata)
                    .setFields("id")
                    .execute();
            System.out.println("Folder ID: " + file.getId());
            return new Response("Folder id:".formatted(file.getId()),200);
        } catch (GoogleJsonResponseException e) {
            System.err.println("Unable to create folder: " + e.getDetails());
            return new Response("Unable to create folder: " + e.getDetails(),500);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static StreamingResponseBody getStreamResponseBody(InputStream stream){
        StreamingResponseBody responseStream = outputStream -> {
            try(stream){
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = stream.read(buffer)) != -1){
                    outputStream.write(buffer,0,bytesRead);
                }
            }
        };
        return responseStream;
    }
}
