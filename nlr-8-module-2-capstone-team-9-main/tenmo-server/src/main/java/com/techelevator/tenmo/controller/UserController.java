package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

public class UserController {
    private UserDao userDao;
    public UserController(UserDao userDao) {
        this.userDao = userDao;
    }

    @RequestMapping(path = "/users", method = RequestMethod.GET)
    public List<User> listUsers() { return userDao.findAll();}

    @RequestMapping(path = "/users/userId", method = RequestMethod.GET)
    public String getUsernameByUserId(@PathVariable int userId) {
        return userDao.findUsernameByUserId(userId);
    }
}
