package ru.ulush.springboot_crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ru.ulush.springboot_crud.model.Role;
import ru.ulush.springboot_crud.model.User;
import ru.ulush.springboot_crud.service.RoleService;
import ru.ulush.springboot_crud.service.UserService;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }


//    @GetMapping("/")
//    public String homePage() {
//        return "admin";
//    }

    @GetMapping("/admin")
    public String getUsers(ModelMap model) {
        model.addAttribute("users", userService.listUsers());
        return "admin";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "/login";
    }

    @GetMapping("/admin/{id}")
    public String getUser(@PathVariable("id") long id, Model model) {
        model.addAttribute(userService.getUser(id));
        return "admin/show";
    }

    @GetMapping("/admin/new")
    public String newUser(Model model) {
        User user = new User();
        List<Role> showRoles = roleService.listRoles();
        model.addAttribute("user", user);
        model.addAttribute("showRoles", showRoles);
        return "admin/new";
    }

    @GetMapping("/admin/{id}/edit")
    public String editUser(Model model, @PathVariable("id") long id) {
        model.addAttribute("user", userService.getUser(id));
        model.addAttribute("showRoles", roleService.listRoles());
        return "admin/edit";
    }

    @GetMapping("/user/userspace/{id}")
    public String userspace(Model model, @PathVariable("id") long id, Principal principal) {
        User user = userService.getUserByName(principal.getName());
        if(!user.getId().equals(id)) {
            return "redirect:/access-denied";
        }
        model.addAttribute("user", userService.getUser(id));
        return "user/userspace";
    }

//    @GetMapping("/admin/adminspace/{id}")
//    public String adminspace(Model model, @PathVariable("id") long id, Principal principal) {
//        User user = userService.getUserByName(principal.getName());
//        if(!user.getId().equals(id)) {
//            return "redirect:/access-denied";
//        }
//        model.addAttribute("user", userService.getUser(id));
//        return "/admin/adminspace";
//    }

    @PostMapping("/admin/new")
    public String createUser(Model model, @ModelAttribute("user") User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.add(user);
        model.addAttribute("users", userService.listUsers());
        return "admin";
    }

//    @GetMapping("/registration")
//    public String reg(){
//        return "registration";
//    }
//
//    @PostMapping("/registration")
//    public String regNewUser(Model model, @ModelAttribute("user") User user) {
//        User us = new User();
//        us.setUsername(user.getUsername());
//        us.setPassword(passwordEncoder.encode(user.getPassword()));
//        us.setRole(new Role("ROLE_USER"));
//        userService.add(us);
//        model.addAttribute("users", userService.listUsers());
//        return "user/userspace";
//    }


    @PostMapping("/admin/{id}")
    public String updateUser(Model model, @ModelAttribute("user") User user) {
        if(!user.getPassword().equals(userService.getUser(user.getId()).getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userService.updateUser(user);
        model.addAttribute("users", userService.listUsers());
        return "admin";
    }

    @PostMapping("/admin/{id}/delete")
    public String deleteUser(Model model, @PathVariable("id") long id) {
        userService.deleteUser(userService.getUser(id));
        model.addAttribute("users", userService.listUsers());
        return "admin";
    }
    @GetMapping("/access-denied")
    public String showAccessDenied() {
        return "admin/access-denied";
    }
}
