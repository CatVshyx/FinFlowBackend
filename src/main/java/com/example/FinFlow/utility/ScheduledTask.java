package com.example.FinFlow.utility;


import com.example.FinFlow.model.Company;
import com.example.FinFlow.model.Date;
import com.example.FinFlow.model.Product;
import com.example.FinFlow.service.CompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class ScheduledTask {
    private static final Logger log = LoggerFactory.getLogger(ScheduledTask.class);

    @Autowired
    private CompanyService service;

    @Scheduled(fixedRate = 1000 * 60 * 60 * 24)
    public void reportCurrentTime(){
        Company[] array = service.getAllExistingCompanies();

        List<Date> arrayList = new ArrayList<>();
        for(Company currCompany : array){
            Set<Product> productSet = currCompany.getProducts();
            long value = 0;
            for(Product currProduct : productSet){
                float discount = currProduct.getDiscount()  == null ? 1 : currProduct.getDiscount().getDiscount();
                value += (int) (currProduct.getPrice() * discount ) * currProduct.getAmount();
                currProduct.setSold(0);
            }
            Date localDate = new Date(LocalDate.now(),value);

            arrayList.add(localDate);
            currCompany.getRevenues().add(localDate);
            service.updateCompany(currCompany);
        }

        log.info("Current amount of all revenues is %s".formatted(arrayList));
    }
}
