package com.example.FinFlow.service;


import com.example.FinFlow.additional.Response;
import com.example.FinFlow.config.JwtService;
import com.example.FinFlow.model.*;
import com.example.FinFlow.repository.CompanyRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class CompanyService {

    @Value("${server.link}")
    private String serverLink;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private EmailService emailService;
    public Response createCompany(String companyName, String userEmail){
        Optional<Company> optional =  companyRepository.findByName(companyName);
        if (optional.isPresent()) return new Response("The company with such name already exists",400);
        User user = userService.findByEmail(userEmail).orElse(null);

        if (user == null) return new Response("User not found",404);
        if(user.getCompany() != null && user.getRole().equals(Role.OWNER)) return new Response("Remove your company before creating a new one",400);
        Company newCompany = new Company(companyName);
        user.setAllows(List.of(Allows.values()));
        user.setRole(Role.OWNER);

        newCompany.setDateOfCreation(LocalDate.now());
        newCompany.getUsers().add(user);
        newCompany.setInviteLink(UUID.randomUUID().toString().substring(0,8));
        companyRepository.save(newCompany);
        return new Response("The company %s was successfully created at %s".formatted(companyName,newCompany.getDateOfCreation()),200);
    }

    public Response removeCompany( String userEmail) {
        User user = userService.findByEmail(userEmail).orElse(null);
        if ( user == null) return new Response("User not found",404);
        if (user.getCompany() == null) return new Response("Company not found",404);
        if (!user.getRole().equals(Role.OWNER)) return new Response("You are not owner to remove company",400);
        Company company = companyRepository.findById(user.getCompany()).orElse(null);

        deleteCompany(company);
        return new Response("The company %s was successfully removed".formatted(company.getName()),200 );
    }
    private void deleteCompany(Company company){
        company.getUsers().forEach(user -> {
            user.setRole(Role.USER);
            user.getAllows().clear();
        });
        companyRepository.delete(company);
    }
    public Response addUserByCode(String userEmail, String code){
        User main = userService.findByEmail(userEmail).orElse(null);
        Company company = companyRepository.findByInviteLink(code).orElse(null);
        if(main == null || company == null) return new Response("Company with such invite code is not found" ,404);
        if (main.getCompany() != null && main.getCompany().intValue() == company.getId()) return new Response("You are already in this company" ,400);
        if (main.getRole().equals(Role.OWNER)) {return new Response("Before joining new company, remove yours" ,400);}
        main.setRole(Role.USER);
        main.getAllows().clear();
        main.getAllows().add(Allows.storage_view);
        company.getUsers().add(main);

        companyRepository.save(company);
        return new Response("You have been successufully added to %s".formatted(company.getName()),200 );
    }
    public Response getAllCompanyData(String userEmail) throws JsonProcessingException {
        User user = userService.findByEmail(userEmail).orElse(null);
        if(user == null) return new Response("User not found",404);

        if (user.getCompany() == null ){
            return new Response("Not a member of a company",400);
        }
        Company company = companyRepository.findById(user.getCompany()).orElse(null);
        if(company == null) return new Response("Not a member of a company",400);


        Collection<Allows> allows = user.getAllows();
        Set<String> standartFilter = new HashSet<>(Arrays.asList("id","name","dateOfCreation","inviteLink"));
        boolean setDiscountFilter = true;
        if (allows.contains(Allows.storage_view) || allows.contains(Allows.storage_edit)
                || allows.contains(Allows.promo_edit) || allows.contains(Allows.promo_view)){
            standartFilter.add("products");
        }
        if (allows.contains(Allows.admin_view) || allows.contains(Allows.admin_edit)){
            standartFilter.add("users");
        }
        if (allows.contains(Allows.finance_view) || allows.contains(Allows.finance_edit)){
            standartFilter.add("revenues");
        }
        if(allows.contains(Allows.promo_edit) || allows.contains(Allows.promo_view)){
            setDiscountFilter = false;
        }

        return new Response(filterCompany(company,standartFilter,setDiscountFilter),200);
    }
    private String filterCompany(Company company,Set<String> companySet, boolean isDiscountFilter) throws JsonProcessingException {
        SimpleBeanPropertyFilter companyFilter = SimpleBeanPropertyFilter.filterOutAllExcept(companySet);
        SimpleBeanPropertyFilter filterDiscount = SimpleBeanPropertyFilter.serializeAllExcept(isDiscountFilter ?"discount" : null);
        FilterProvider filters = new SimpleFilterProvider()
                .addFilter("companyFilter", companyFilter)
                .addFilter("productFilter",filterDiscount);

        ObjectMapper mapper = new ObjectMapper();

        return mapper.writer(filters).writeValueAsString(company);
    }
    public Company findCompanyById(int id) {
        return companyRepository.findById(id).orElse(null);
    }
    public void updateCompany(Company company){
        companyRepository.save(company);
    }

    public Response deleteUser(String adminsEmail, String userEmail) {
        User admin = userService.findByEmail(adminsEmail).orElse(null);
        User user = userService.findByEmail(userEmail).orElse(null);

        Response response = checkUserByCompany(user);
        if(response != null) return response;
        if (adminsEmail.equals(userEmail)){ return new Response("You can`t delete yourself",400);}
        if (admin.getCompany().intValue() != user.getCompany().intValue())return new Response("User is not a member of your company",400);;
        Company company = companyRepository.findById(admin.getCompany()).orElse(null);
        if (!admin.getRole().equals(Role.ADMINISTRATOR) && !admin.getRole().equals(Role.OWNER)) return new Response("You dont have permissions to delete",400);
        if(user.getRole().equals(Role.OWNER) || user.getRole().equals(Role.ADMINISTRATOR) && !admin.getRole().equals(Role.OWNER))return new Response("You don`t have permissions to delete admins and owners",400);
        user.setRole(Role.USER);
        user.getAllows().clear();
        company.getUsers().remove(user);
        companyRepository.save(company);
        return new Response("User %s was successfully deleted".formatted(user.getName()),200);
    }

    public Response changeUserAllows(String emailByToken, Map<String, String> request) {
        User admin = userService.findByEmail(emailByToken).orElse(null);
        User user = userService.findByEmail(request.get("email")).orElse(null);

        Response r = checkUserByCompany(user);
        if(r != null) return r;
        if (emailByToken.equals(user.getEmail())) return new Response("You are not allowed",403);
        if (admin.getCompany().intValue() != user.getCompany().intValue()) return new Response("User is not a member of your company ",400);
        if (user.getRole().equals(Role.OWNER)) return new Response("You are not allowed to do it",400);
//        if(user.getRole().equals(Role.ADMINISTRATOR) && !admin.getRole().equals(Role.OWNER)){ return new Response("You cant change roles of other admins ",400); }

        Collection<Allows> allows = user.getAllows();
        request.forEach((key, value) -> {
            if(contains(key)){
                Allows currAllow = Allows.valueOf(key);
                boolean isRequired = Boolean.parseBoolean(value);
                if (isRequired){if (!allows.contains(currAllow)) allows.add(currAllow);}
                else {allows.remove(currAllow);}
            }
        });

        userService.saveUser(user);
        return new Response("User allows have been changed",200);
    }
    private Response checkUserByCompany(User user){
        if ( user == null) return new Response("User not found",404); ;
        if( user.getCompany() == null) return new Response("User is not a member of your company ",400);
        return null;
    }
    public Response addUserBySpecification(String emailByToken, Map<String, Object> request) {
        User admin = userService.findByEmail(emailByToken).orElse(null);
        User user = userService.findByEmail((String) request.get("email")).orElse(null);


        if (admin == null || user == null) return new Response("User(s) not found",404);
        if (admin.getCompany() == null) return new Response("You are not a member of company",400);
        if (user.getCompany() != null && admin.getCompany().intValue() == user.getCompany().intValue() ) return new Response("User is already a member",400);
        request.remove("email");
        request.put("company_id",admin.getCompany());
        HashMap<String,Object> map = new HashMap<>();
        map.put("extra",request);
        System.out.println(map);
        String token = jwtService.generateToken(map,User.toUserDetails(user),72);
        String newServer = serverLink + "auth/addUserBySpecification/%s".formatted(token);
        String message = String.format(
                "Hello, %s! \n" +
                        "You have been invited to new company. Please, visit next link: %s",user.getName(),newServer
        );
        Runnable runnable = () -> emailService.sendEmail(user.getEmail(),"Invitation",message);
        Thread thread = new Thread(runnable,"invite");
        thread.start();
        return new Response("Invitation has been successfully sent to %s".formatted(user.getName()),200);
    }
    public Response confirmAddingBySpecification(String token){
        String email = jwtService.extractEmail(token);
        User user = userService.findByEmail(email).orElse(null);
        System.out.println(token);
        if(user == null) return new Response("User not found",404);
        HashMap<String,Object> map = (HashMap<String, Object>) jwtService.extractExtraClaims(token,"extra");

        Optional<Company> optional = companyRepository.findById(Integer.valueOf(map.get("company_id").toString()));
        if (optional.isEmpty()) return new Response("Company is not found",400);
        Company company = optional.get();
        map.forEach((key, value) -> {
            if(contains(key)){ user.getAllows().add(Allows.valueOf(key)); }
        });
        System.out.println(map);
        if (map.containsKey("role")){
            String role = map.get("role").toString();
            user.setRole(Role.OWNER.name().equals(role) ? Role.USER : Role.valueOf(role));
        }else {
            user.setRole(Role.USER);
        }


        company.getUsers().add(user);
        companyRepository.save(company);
        return new Response("You are added",200);
    }

    public Response leaveCompany(String emailByToken) {
        User u = userService.findByEmail(emailByToken).orElse(null);
        if(u.getCompany() == null) return new Response("You are not a member of a company",400);

        Company comp = companyRepository.findById(u.getCompany()).orElse(null);
        if (u.getRole().equals(Role.OWNER)) return new Response(null,302);
        u.setRole(Role.USER);
        u.getAllows().clear();
        comp.getUsers().remove(u);
        companyRepository.save(comp);
        return new Response("You have been deleted form company '%s'".formatted(comp.getName()),200);
    }

    public Company[] getAllExistingCompanies(){
        Iterable<Company> iterator = companyRepository.findAll();
        Stream<Company> stream = StreamSupport.stream(iterator.spliterator(),false);

        return stream.toArray(Company[]::new);
    }
    public static boolean contains(String test) {

        for (Allows c : Allows.values()) {
            if (c.name().equals(test)) {
                return true;
            }
        }

        return false;
    }
}
