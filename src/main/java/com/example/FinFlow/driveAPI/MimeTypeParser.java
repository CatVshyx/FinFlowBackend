package com.example.FinFlow.driveAPI;

import java.util.HashMap;
import java.util.Map;

public class MimeTypeParser {
    private static final HashMap<String,String > mimetypes = new HashMap<>(Map.of("application/x-zip-compressed",".zip","image/jpeg",".jpg","text/plain",".txt","image/png",".png" ) );
    public static String getMimeByExtension(String ext){
        return mimetypes.entrySet().stream()
                .filter(entry -> entry.getValue().equals(ext))
                .findAny().orElse(null)
                .getKey();
    }
    public static String getExtensionByMimeType(String mime){
        return mimetypes.get(mime);
    }
}
