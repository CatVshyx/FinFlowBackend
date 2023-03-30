package com.example.FinFlow.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "account")
public class User  {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;
    private String name;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate dateOfCreation;
    private String email;

    @Enumerated(EnumType.ORDINAL)
    @Column(length = 512)
    private Collection<Allows> allows = new ArrayList<>();
    @Enumerated(EnumType.ORDINAL)
    private Role role = Role.USER;
    private String phoneNumber;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private boolean isVerified;
    @Column(name = "company_id")
    @Nullable
    private Integer company;
    @JsonIgnore
    @Column( length = 64)
    private String verificationCode;


//    @JsonIgnore
    @Column(length = 64)
    private String photos;


    public User(){}

    public User(String name, LocalDate dateOfCreation, String email, String phoneNumber, String password) {
        this.name = name;
        this.dateOfCreation = dateOfCreation;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(LocalDate dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public String getEmail() {
        return email;
    }

    public String getPhotos() {
//        return "https://test-back-ncm8.onrender.com/auth/media/"+photos;
        if (photos == null) return null;
        return photos;
//        return "http://localhost:8080/auth/media/"+photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public Integer getCompany() {
        return company;
    }

    public void setCompany(Integer company) {
        this.company = company;
    }

    public Collection<Allows> getAllows() {
        return allows;
    }

    public void setAllows(Collection<Allows> allows) {
        this.allows = allows;
    }

    public static Collection<? extends GrantedAuthority> getAuthorities(Collection<Allows> allows) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        allows.forEach(allow -> authorities.add(new SimpleGrantedAuthority(allow.name())));
        return authorities;
    }



    public static UserDetails toUserDetails(User local){
        return new org.springframework.security.core.userdetails.User(local.email, local.password, getAuthorities(local.allows));
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dateOfCreation=" + dateOfCreation +
                ", email='" + email + '\'' +
                ", allows=" + allows +
                ", role=" + role +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", photos='" + photos + '\'' +
                '}';
    }
}
