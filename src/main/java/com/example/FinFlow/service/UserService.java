package com.example.FinFlow.service;

import com.example.FinFlow.additional.Response;
import com.example.FinFlow.model.Allows;
import com.example.FinFlow.model.User;
import com.example.FinFlow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.io.File;
import java.time.LocalDate;
import java.util.*;

@Service
public class UserService {
    @Value("${server.link}")
    private String serverLink;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Value("${upload.path}")
    private String uploadPath;
    public Response addNewUser(User user){
        User fromDB = userRepository.findByEmail(user.getEmail()).orElse(null);

        if(fromDB != null) return new Response("User with such email is already registered",400);
        if (user.getEmail() == null || user.getPassword() == null) return new Response("Email or password is not set, please set them",400);
        user.setVerified(false);
        user.setVerificationCode(UUID.randomUUID().toString());
        userRepository.save(user);

        String newServer = serverLink + "auth/activate/%s".formatted(user.getVerificationCode());
        String message = String.format(
                "Hello, %s! \n" +
                        "Welcome to FinanceFlow. Please, visit next link: %s",
                user.getName(),
                newServer
        );

        Runnable runnable = () -> emailService.sendEmail(user.getEmail(),"Activating account",message);
        Thread thread = new Thread(runnable,"emailSend");
        thread.start();
        return new Response("The user is registered, please verify your accountant by link we sent",200);
    }

    public boolean addTestUsers(int amount) {
        Random rd = new Random();
        String[] names = {"valera","petya","igor","taras","dima","vasya"};
        for (int i = 0; i < amount; i++){
            String name = names[rd.nextInt(names.length)] + rd.nextInt(45);
            User user = new User(name, LocalDate.now().minusDays(rd.nextInt(60)),
                    name + "@gmail.com",
                    rd.nextInt(2) == 1 ? null : "+380532"+rd.nextInt(250),
                    passwordEncoder.encode(String.valueOf(12345)) );
            user.setVerified(rd.nextBoolean());
            int length = Allows.values().length;
            for(int b = 0; b < rd.nextInt(length); b++){
                Allows r = Allows.values()[rd.nextInt(length)];
                if (user.getAllows().contains(r)) continue;
                user.getAllows().add(r);
            }

            userRepository.save(user);
        }
        return true;
    }

    public Iterable<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean verify(String code) {
        User user = userRepository.findByVerificationCode(code).orElse(null);
        if(user == null) return false;

        user.setVerified(true);
        userRepository.save(user);
        return true;
    }
    public void saveUser(User user){
        userRepository.save(user);
    }
    public Response changeUsersSettings(String emailByToken, Map<String, String> request) {
        User user = userRepository.findByEmail(emailByToken).orElse(null);

        if (user == null) return new Response("No user found",400);
        System.out.println(request.get("username"));
        if (request.containsKey("username")){ user.setName(request.get("username")); }
//        if (request.containsKey("email")){ user.setEmail(request.get("email")); }
        if (request.containsKey("phone")){ user.setPhoneNumber(request.get("phone")); }

        userRepository.save(user);
        return new Response("User settings have been saved",200);
    }
    public Response setForgotCode(String email) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) return new Response("No user found",400);
        if (!user.isVerified()) return new Response("This user is not verified, please verify first on your email",400);
        String code = UUID.randomUUID().toString().substring(0,5);
        user.setVerificationCode(code);
        userRepository.save(user);
        return new Response(code,200);

    }

    public Response changePasswordByCode(Map<String, String> password) {
        String code = password.get("code");
        System.out.println(code+" codee");
        User user = userRepository.findByVerificationCode(code).orElse(null);
        if (user == null) return new Response("User is not found",400);
        String pass = password.get("password");
        user.setVerificationCode(null);
        user.setPassword(passwordEncoder.encode(pass));
        userRepository.save(user);

        Map<String,String> data = new HashMap<>();
        data.put("email",user.getEmail());
        data.put("password",pass);
        return new Response(data, 200);
    }

    public Response changePassword(String emailByToken, Map<String, String> request) {

        User user = userRepository.findByEmail(emailByToken).orElse(null);
        if (user == null) return new Response("User is not found",400);
        String currentPass = request.get("current_password");
        String newPass = request.get("new_password");
        if (!passwordEncoder.matches(currentPass,user.getPassword())) return new Response("'Current password' doesn`t equal real current password",400);
        user.setPassword(passwordEncoder.encode(newPass));

        userRepository.save(user);

        Map<String,String> data = new HashMap<>();
        data.put("email",user.getEmail());
        data.put("password",newPass);

        return new Response(data, 200);
    }

    public Response getUsersProfile(String mainEmail,String userEmail){
        User main = userRepository.findByEmail(mainEmail).orElse(null);
        User user = userRepository.findByEmail(userEmail).orElse(null);
        if(user == null || main == null) return new Response(null, HttpStatus.NOT_FOUND);
        if (main.getCompany() == null || user.getCompany() == null) return new Response(null,HttpStatus.BAD_REQUEST);
        if (main.getCompany().intValue() != user.getCompany().intValue()) return new Response(null,HttpStatus.BAD_REQUEST);
        if (user.getPhotos() == null) return new Response(null,HttpStatus.NO_CONTENT);


        File f = new File(uploadPath+"/"+user.getPhotos());

        return new Response(f,200);
    }

}
