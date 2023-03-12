package com.example.FinFlow.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;

@Entity
public class Date {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;

    private Long sold;
    public Date(){}

    public Date(LocalDate date, Long sold) {
        this.date = date;
        this.sold = sold;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getSold() {
        return sold;
    }

    public void setSold(Long sold) {
        this.sold = sold;
    }

    @Override
    public String toString() {
        return "Date{" +
                "date=" + date +
                ", sold=" + sold +
                '}';
    }
}
