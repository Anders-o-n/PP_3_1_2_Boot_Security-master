package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entities.Role;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.services.UserDetailService;
import ru.kata.spring.boot_security.demo.services.UserServiceDAO;


import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller

public class MainController {

    private final UserServiceDAO userService;

    private UserDetailService userDetailService;

    private RoleRepository roleRepository;



    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Autowired
    public void setUserDetailService(UserDetailService userDetailService) {
        this.userDetailService = userDetailService;
    }

    public MainController(UserServiceDAO userService) {
        this.userService = userService;
    }

    @GetMapping("/admin/")
    public String getAllPeople(Model model) {
        List<User> list = userService.getAllPeople();
        model.addAttribute("allPeople", list);
        return "moreUsers";
    }

    @GetMapping("/admin/{id}")
    public String showUser(@PathVariable("id") int id, Model model) {
        model.addAttribute("person", userService.getById(id));
        return "showUser";
    }

    @GetMapping("/user")
    public String showUser(Principal principal, Model model) {
        User user = userDetailService.findByUsername(principal.getName());
        model.addAttribute("person", user);
        return "showUserRoleUser";
    }

    @GetMapping("/admin/addNewUser")
    public String addNewUser(Model model) {
        User user = new User();
        model.addAttribute("person", user);
        List<Role> roles = roleRepository.findAll();
        model.addAttribute("allRoles", roles);
        return "addUser";
    }

    @PostMapping("/admin/save")
    public String saveUser(@ModelAttribute("person") @Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            List<Role> roles = roleRepository.findAll();
            model.addAttribute("allRoles", roles);
            return "addUser";
        }
        userService.save(user);
        return "redirect:/admin/";
    }

    @GetMapping("/admin/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        model.addAttribute("person", userService.getById(id));
        List<Role> roles = roleRepository.findAll();
        model.addAttribute("allRoles", roles);
        return "editUser";
    }

    @PatchMapping("/admin/{id}/update")
    public String update(@ModelAttribute("person") @Valid User user, BindingResult bindingResult, @PathVariable("id") int id, Model model) {

        if (bindingResult.hasErrors()) {
            List<Role> roles = roleRepository.findAll();
            model.addAttribute("allRoles", roles);
            return "editUser";
        }
        userService.updateUser(id, user);
        return "redirect:/admin/";
    }

    @DeleteMapping("/admin/{id}")
    public String delete(@PathVariable("id") int id) {
        userService.deleteUser(id);
        return "redirect:/admin/";
    }

}
