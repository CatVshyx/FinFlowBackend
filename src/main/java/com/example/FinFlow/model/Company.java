package com.example.FinFlow.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
@Entity
@JsonFilter("companyFilter")
public class Company {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    private String name;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate dateOfCreation;
    @Column( length = 16)
    private String inviteLink;
    @OneToMany(targetEntity = Date.class,cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @JoinColumn(name="company_id")
    private Set<Date> revenues;

    @OneToMany(targetEntity = User.class, cascade = CascadeType.REFRESH,orphanRemoval = false)
    @JoinColumn(name="company_id")
    private Set<User> users = new HashSet<>();

    @OneToMany(targetEntity = Product.class, cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @JoinColumn(name="company_id")
    private Set<Product> products = new HashSet<>();
    public Company(){}
    public Company(String name){
        this.name = name;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateOfCreation() {
        return dateOfCreation;
    }

    public String getInviteLink() {
        return inviteLink;
    }

    public void setInviteLink(String inviteLink) {
        this.inviteLink = inviteLink;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public void setDateOfCreation(LocalDate dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public Set<Date> getRevenues() {
        return revenues;
    }

    public void setRevenues(Set<Date> revenues) {
        this.revenues = revenues;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dateOfCreation=" + dateOfCreation +
                ", inviteLink='" + inviteLink + '\'' +
                '}';
    }
}
