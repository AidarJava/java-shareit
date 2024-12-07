package ru.practicum.shareit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDtoIn;
import ru.practicum.shareit.user.dto.UserDtoOut;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
//@Rollback(false)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "spring.datasource.url=jdbc:h2:mem:shareit;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        //properties = "spring.datasource.url=jdbc:postgresql://localhost:5433/shareit",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class UserServiceImplTest {
    private final EntityManager em;
    private final UserServiceImpl service;

    @Test
    void testSaveUser() {
        UserDtoIn userDto = makeUserDto("some@email.com", "Пётр");

        UserDtoOut userDtoOut=service.saveUser(userDto);
        System.out.println("CHECKING_____"+userDtoOut);
        em.flush();
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        System.out.println("CHECKING2_____"+query);
        User user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();
        System.out.println("CHECKING3_____"+user);

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
        //assertThat(user.getState(), equalTo(userDto.getState()));
        // assertThat(user.getRegistrationDate(), notNullValue());
    }

    private UserDtoIn makeUserDto(String email, String name) {
        UserDtoIn dto = new UserDtoIn();
        dto.setEmail(email);
        dto.setName(name);
        return dto;
    }
}
