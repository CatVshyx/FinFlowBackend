package com.example.FinFlow.service;

import com.example.FinFlow.additional.Response;
import com.example.FinFlow.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileService {
    @Value("${upload.path}")
    private String uploadPath;
    @Autowired
    UserService userService;
    public Response uploadFile(String email, MultipartFile file) throws IOException {
        User user = userService.findByEmail(email).orElse(null);
        if (user == null) new Response("User not found",404);

        File uploadDir = new File(uploadPath);
        if(!uploadDir.exists()){ uploadDir.mkdir(); }
        String userPhotos = user.getPhotos();

        if(userPhotos != null){
            File userFile = new File(uploadPath+"/"+user.getPhotos());
            userFile.delete();
        }

        String uuiidFile = UUID.randomUUID().toString().substring(0,6);
        String resultFilename = uuiidFile+"."+file.getOriginalFilename();
        System.out.println(resultFilename);

        file.transferTo(new File(uploadPath+"/"+resultFilename));

        user.setPhotos(resultFilename);
        userService.saveUser(user);


        return new Response("The file was saved",200);
    }
}
