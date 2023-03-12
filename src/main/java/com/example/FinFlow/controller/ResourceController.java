package com.example.FinFlow.controller;

import com.example.FinFlow.model.Category;
import com.example.FinFlow.additional.Response;
import com.example.FinFlow.config.JwtService;
import com.example.FinFlow.model.Discount;
import com.example.FinFlow.model.Product;
import com.example.FinFlow.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;

@RestController
@RequestMapping("/res")
public class ResourceController {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private ProductService productService;

    @PreAuthorize("hasAuthority('storage_edit')")
    @PostMapping(value = "/addProduct", consumes = {"*/*"})
    public ResponseEntity<Object> addProduct(@RequestHeader(HttpHeaders.AUTHORIZATION) String token , @RequestBody Map<String, String > request){
        if(!containsKeys(new String[]{"name","category","price","amount"},request))
            return new ResponseEntity<>("Not all required keys[name,category,price,amount] are set", HttpStatus.BAD_REQUEST);

        float price; int amount; Category category;
        try{
            price =Float.parseFloat(request.get("price"));
            amount = Integer.parseInt(request.get("amount"));
            category = Category.valueOf(request.get("category"));
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>(
                    "Please check all the arguments - \ncategory[DAIRY,FRUIT,VEGETABLES," +
                            "CEREAL,MEAT,FISH,GRAINS,SWEET,WATER,SNACKS]\namount -" +
                            " only numerics \nprice - also [4.5,12,9.01] ",
                    HttpStatus.BAD_REQUEST
            );
        }
        Product product = new Product(request.get("name"), price,amount,category);
        if (request.containsKey("supplier")) product.setSupplier(request.get("supplier"));
        Response ans = productService.addNewProduct(product,getEmailByToken(token));
        System.out.println(ans.getDescription());
        return new ResponseEntity<>(ans.getDescription(), ans.getHttpCode());
    }

    @PreAuthorize("hasAuthority('storage_edit')")
    @DeleteMapping("/deleteProduct")
    public ResponseEntity<String> deleteProduct(@RequestBody Map<String, String > request){
        if(!request.containsKey("id")) return new ResponseEntity<>("Write id of the product you want to delete", HttpStatus.BAD_REQUEST);
        boolean ans = productService.deleteProduct(Integer.parseInt(request.get("id")));
        if(!ans) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>("The product is successfully deleted",HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('storage_edit')")
    @PostMapping("/sellProduct")
    public ResponseEntity<Object> editProduct(@RequestBody Map<String,String> request){
        if(!request.containsKey("id")) return new ResponseEntity<>("Write id to edit the product ", HttpStatus.BAD_REQUEST);

        try{
            Response r = productService.sellProduct(request);
            return new ResponseEntity<>(r.getDescription(),r.getHttpCode());
        }catch (NumberFormatException e){
            return new ResponseEntity<>("Not correct numbers",HttpStatus.BAD_REQUEST);
        }

    }

    @PreAuthorize("hasAuthority('storage_edit')")
    @PostMapping("/editProduct")
    public ResponseEntity<Object> sellProduct(@RequestBody Map<String,String> request){
        if(!request.containsKey("id")) return new ResponseEntity<>("Write id to sell the product ", HttpStatus.BAD_REQUEST);

        Response answer = productService.editProduct(request);
        return new ResponseEntity<>(answer.getDescription(), answer.getHttpCode());
    }


    @PreAuthorize("hasAuthority('promo_edit')")
    @PostMapping("/addDiscount")
    public ResponseEntity<Object> addDiscount(@RequestBody Map<String,String> request){
        if(!containsKeys(new String[]{"id","start_date","end_date","discount"},request))
            return new ResponseEntity<>("Not all required keys[id,start_date,end_date,discount] are set", HttpStatus.BAD_REQUEST);
        Discount discount;
        try{
            discount = new Discount(
                    Float.parseFloat(request.get("discount")),
                    LocalDate.parse(request.get("start_date")),
                    LocalDate.parse(request.get("end_date")));
        }catch (DateTimeParseException e){
            return new ResponseEntity<>("start and end dates aren`t set correct, please write them in format 'yyyy-mm-dd'", HttpStatus.BAD_REQUEST);
        }catch (NumberFormatException e){
            return new ResponseEntity<>("exception with number format. Discount is set like 0.53, 1.24 etc.", HttpStatus.BAD_REQUEST);
        }

        Response isSucceed = productService.addDiscount(Integer.parseInt(request.get("id")),discount);
        return new ResponseEntity<>(isSucceed.getDescription(),isSucceed.getHttpCode());
    }

    @PreAuthorize("hasAuthority('promo_edit')")
    @PostMapping("/editDiscount")
    public ResponseEntity<Object> editDiscount(@RequestBody Map<String,String> request){
        if(!request.containsKey("id")) return new ResponseEntity<>("Id is not set", HttpStatus.BAD_REQUEST);
        Response answer;
        try{
            answer = productService.editDiscount(request);
        }catch (DateTimeParseException e){
            return new ResponseEntity<>("start and end dates aren`t set correct, please write them in format 'yyyy-mm-dd'", HttpStatus.BAD_REQUEST);
        }catch (NumberFormatException e){
            return new ResponseEntity<>("exception with number format. Discount is set like 0.53, 1.24 etc.", HttpStatus.BAD_REQUEST);
        }
        System.out.println(answer.toString());
        return new ResponseEntity<>(answer.getDescription(),answer.getHttpCode());
    }
    @PreAuthorize("hasAuthority('promo_edit')")
    @DeleteMapping("/deleteDiscount")
    public ResponseEntity<Object> deleteDiscount(@RequestBody Map<String,String> request){
        if(!request.containsKey("id")) return new ResponseEntity<>("Id is not set", HttpStatus.BAD_REQUEST);

        Response ans = productService.removeDiscount(Integer.parseInt(request.get("id")));
        return new ResponseEntity<>(ans.getDescription(), ans.getHttpCode());
    }
    static boolean containsKeys(String[] requiredKeys, Map<String,String> obj){
        for(String key : requiredKeys){
            if(!obj.containsKey(key)) return false;
        }
        return true;
    }
    private  String getEmailByToken(String token){
        final String jwtToken = token.substring(7);
        return jwtService.extractEmail(jwtToken);
    }
}
