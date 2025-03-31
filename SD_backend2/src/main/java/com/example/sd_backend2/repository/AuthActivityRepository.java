package com.example.sd_backend2.repository;

import com.example.sd_backend2.model.AuthActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuthActivityRepository extends JpaRepository<AuthActivity, Long> {
    List<AuthActivity> findTop100ByOrderByTimestampDesc();
}
