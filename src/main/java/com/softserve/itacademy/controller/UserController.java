package com.softserve.itacademy.controller;

import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.RoleService;
import com.softserve.itacademy.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

@Controller
@RequestMapping("/users")
public class UserController {
    Logger logger = Logger.getLogger(UserController.class.getName());
    FileHandler consText = new FileHandler("C:\\Users\\MARIANA\\Documents\\Java Marathon\\sprint13\\jom_springmvc__22-m14_s13_team_1_03\\UserControllerLogs.log");
    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) throws IOException {
        this.userService = userService;
        this.roleService = roleService;
        logger.setUseParentHandlers(false);
        logger.addHandler(consText);
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("user", new User());
        logger.info("UserController @GetMapping /users/create ");
        return "create-user";
    }

    @PostMapping("/create")
    public String create(@Validated @ModelAttribute("user") User user, BindingResult result) {
        if (result.hasErrors()) {
            logger.warning("ERROR, UserController @PostMapping /users/create " + user.getId());

            return "create-user";
        }
        user.setPassword(user.getPassword());
        user.setRole(roleService.readById(2));
        User newUser = userService.create(user);

        logger.info("UserController @PostMapping /users/create ");
        return "redirect:/todos/all/users/" + newUser.getId();
    }

    @GetMapping("/{id}/read")
    public String read(@PathVariable long id, Model model) {
        User user = userService.readById(id);
        model.addAttribute("user", user);
        logger.info("UserController @GetMapping /users/"+user.getId()+"/read ");
        return "user-info";
    }

    @GetMapping("/{id}/update")
    public String update(@PathVariable long id, Model model) {
        User user = userService.readById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.getAll());
        logger.info("UserController @GetMapping /users/"+user.getId()+"/update ");

        return "update-user";
    }


    @PostMapping("/{id}/update")
    public String update(@PathVariable long id, Model model, @Validated @ModelAttribute("user") User user, @RequestParam("roleId") long roleId, BindingResult result) {
        User oldUser = userService.readById(id);
        if (result.hasErrors()) {
            user.setRole(oldUser.getRole());
            model.addAttribute("roles", roleService.getAll());

            logger.warning("ERROR, UserController @PostMapping /users/"+user.getId()+"/update ");

            return "update-user";
        }
        if (oldUser.getRole().getName().equals("USER")) {
            user.setRole(oldUser.getRole());
        } else {
            user.setRole(roleService.readById(roleId));
        }
        userService.update(user);
        logger.info("UserController @PostMapping /users/"+user.getId()+"/update ");
        return "redirect:/users/" + id + "/read";
    }


    @GetMapping("/{id}/delete")
    public String delete(@PathVariable("id") long id) {
        userService.delete(id);

        logger.info("UserController @GetMapping /users/"+id+"/delete ");

        return "redirect:/users/all";
    }

    @GetMapping("/all")
    public String getAll(Model model) {
        logger.info("UserController @GetMapping /users/all ");
        model.addAttribute("users", userService.getAll());
        return "users-list";
    }
}
