package com.example.FinFlow.service;

import com.example.FinFlow.model.Category;
import com.example.FinFlow.additional.Response;
import com.example.FinFlow.model.Company;
import com.example.FinFlow.model.Discount;
import com.example.FinFlow.model.Product;
import com.example.FinFlow.model.User;
import com.example.FinFlow.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private UserService userService;
    public Response addNewProduct(Product product, String userEmail){
        User user = userService.findByEmail(userEmail).get();

        Company company = companyService.findCompanyById(user.getCompany());

        if(company == null) return new Response("Company not found",404);
        Product optional = company.getProducts()
                .stream()
                .filter(opt -> opt.getProductName().equals(product.getProductName()))
                .findAny()
                .orElse(null);
        if (optional != null) return new Response("Such product with such name already exists in your company",400);
        company.getProducts().add(product);
        companyService.updateCompany(company);
        company = companyService.findCompanyById(user.getCompany());
        company.getProducts().forEach(product1 -> {
            if(product.getProductName().equals(product1.getProductName())){
                product.setId(product1.getId());
            }
        } );
        Map<String,String> object = new HashMap<>();
        object.put("id",String.valueOf(product.getId().intValue()));
        System.out.println(product.getId() + " id");
        return new Response(object,200);
    }
    public boolean deleteProduct(int id){
        Optional<Product> p = productRepository.findById(id);
        if(p.isEmpty()) return false;
        productRepository.delete(p.get());
        return true;
    }
    public Response addDiscount(int productId, Discount discount){
        Optional<Product> optional = productRepository.findById(productId);

        if(optional.isEmpty()) return new Response("Product with such id doesn`t exist",404);

        if (discount.getStartDate().isAfter(discount.getEndDate())) return new Response("Start date can`t be after end date",400);
        Product p = optional.get();
        p.setDiscount(discount);

        productRepository.save(p);
        return new Response("The discount was successfully saved",200);
    }

    public Response removeDiscount(int id){
        Optional<Product> optional = productRepository.findById(id);
        if (optional.isEmpty()) return  new Response("Product with such id doesn`t exist",404);
        Product product = optional.get();

        if (product.getDiscount() == null) return new Response("The discount on this product is not set, please add the discount first",400);

        product.setDiscount(null);
        productRepository.save(product);
        return new Response("The discount was successfully deleted",200);

    }
    public Response sellProduct(Map<String,String> request) throws NumberFormatException{
        Optional<Product> optional = productRepository.findById(Integer.parseInt(request.get("id")));
        if(optional.isEmpty()) return new Response("Product not found",404);

        Product p = optional.get();
        int sold = Integer.parseInt(request.get("sold"));

        p.setSold(p.getSold() + sold);
        p.setAmount(p.getAmount() - sold);

        productRepository.save(p);
        return new Response("Ok",200);
    }
    public Response editProduct(Map<String, String> request) {
        Optional<Product> optional = productRepository.findById(Integer.parseInt(request.get("id")));

        if(optional.isEmpty()){

            return new Response("Product is not found",404);
        };
        String changes;
        Product product = optional.get();

        changes = request.get("name");
        if (changes != null) product.setProductName(changes);
        changes = request.get("amount");
        if (changes != null) product.setAmount(Integer.parseInt(changes));
        changes = request.get("category");
        if (changes != null) product.setCategory(Category.valueOf(changes));
        changes = request.get("supplier");
        if(changes != null) product.setSupplier(changes);
        changes = request.get("price");
        if(changes != null) product.setPrice(Float.parseFloat(changes));

        productRepository.save(product);
        return new Response("Product is edited",200);
    }

    public Response editDiscount(Map<String, String> request) throws DateTimeParseException, NumberFormatException {
        Optional<Product> optional = productRepository.findById(Integer.parseInt(request.get("id")));

        if(optional.isEmpty()) return new Response("Product with such id doesn`t exist",404);
        Product product = optional.get();
        Discount discount = product.getDiscount();
        System.out.println(request);
        System.out.println(discount == null);

        if(discount == null) return new Response("The discount on this product is not set ",400);
        String change = request.get("discount");
        if(change != null)discount.setDiscount(Float.parseFloat(change));
        change = request.get("start_date");
        if(change != null)discount.setStartDate(LocalDate.parse(change));
        change = request.get("end_date");
        if(change != null)discount.setEndDate(LocalDate.parse(change));
        if (discount.getStartDate().isAfter(discount.getEndDate())) return new Response("The start date can`t be after end date",400);
        productRepository.save(product);
        return new Response("The discount was successfully changed",200);
    }
}
