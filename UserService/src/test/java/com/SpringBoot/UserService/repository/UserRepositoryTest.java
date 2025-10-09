package com.SpringBoot.UserService.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.SpringBoot.UserService.model.User;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void saveAndFindByEmail() {
        User u = User.builder().name("X").email("x@y.com").passwordHash("h").role("ATTENDEE").build();
        var saved = userRepository.save(u);
        assertThat(saved.getId()).isNotNull();

        var found = userRepository.findByEmail("x@y.com");
        assertThat(found).isPresent();
    }
}
