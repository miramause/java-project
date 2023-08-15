//package com.softserve.itacademy;
//
//import com.softserve.itacademy.model.Role;
//import com.softserve.itacademy.model.User;
//import com.softserve.itacademy.repository.RoleRepository;
//import com.softserve.itacademy.repository.UserRepository;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//
//public class UserRepositoryTest {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    @Autowired
//    private TestEntityManager entityManager;
//
//    @Test
//    public void newUserTest(){
//        Role role = roleRepository.getOne(2L);
//
//        User user = new User();
//        user.setEmail("newUser@mail.com");
//        user.setRole(role);
//        user.setFirstName("firstName");
//        user.setLastName("lastName");
//        user.setPassword("qwQW12!@");
//
//        userRepository.save(user);
//
//        User actual = userRepository.getUserByEmail("newUser@mail.com");
//        Assertions.assertEquals("firstName",actual.getFirstName());
//    }
//}
