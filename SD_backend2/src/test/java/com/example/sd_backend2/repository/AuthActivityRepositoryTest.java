package com.example.sd_backend2.repository;

import com.example.sd_backend2.model.AuthActivity;
import com.example.sd_backend2.model.Author;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class AuthActivityRepositoryTest {

    @Autowired
    private AuthActivityRepository authActivityRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testFindTop100ByOrderByTimestampDesc() {
        Author author = new Author("testUser", "pass", false);
        entityManager.persist(author);

        // Create two AuthActivity records with different timestamps
        AuthActivity activity1 = new AuthActivity(author, "login", LocalDateTime.now().minusHours(1));
        AuthActivity activity2 = new AuthActivity(author, "logout", LocalDateTime.now());
        entityManager.persist(activity1);
        entityManager.persist(activity2);
        entityManager.flush();

        List<AuthActivity> activities = authActivityRepository.findTop100ByOrderByTimestampDesc();
        assertThat(activities).isNotEmpty();
        assertThat(activities.get(0).getActivity()).isEqualTo("logout");
    }
}
