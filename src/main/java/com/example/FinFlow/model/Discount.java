package com.example.FinFlow.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.persistence.*;
import java.time.LocalDate;
@Entity
public class Discount {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @JsonIgnore
    private Integer id;

    @JsonProperty("discountProperty")
    private float discount = 0;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate startDate;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate endDate;

    @OneToOne(mappedBy = "discount")
    private Product product;
    public Discount(){}
    public Discount(float discount, LocalDate startDate, LocalDate endDate) {
        this.discount = discount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Discount{" +
                "id=" + id +
                ", discount=" + discount +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", product=" + product +
                '}';
    }
}
