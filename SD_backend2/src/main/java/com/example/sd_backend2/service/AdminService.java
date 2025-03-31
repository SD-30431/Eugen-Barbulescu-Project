package com.example.sd_backend2.service;

import com.example.sd_backend2.dto.AuthActivityDTO;
import com.example.sd_backend2.dto.CategoryStatsDTO;
import com.example.sd_backend2.model.AuthActivity;
import com.example.sd_backend2.repository.AuthActivityRepository;
import com.example.sd_backend2.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private AuthActivityRepository authActivityRepository;

    @Autowired
    private BookRepository bookRepository;

    public List<AuthActivityDTO> getRecentActivity() {
        return authActivityRepository.findTop100ByOrderByTimestampDesc()
                .stream()
                .map(activity -> new AuthActivityDTO(
                        activity.getId(),
                        activity.getAuthor().getAuthorId(),
                        activity.getActivity(),
                        activity.getTimestamp()
                ))
                .collect(Collectors.toList());
    }

    public List<CategoryStatsDTO> getCategoryStats() {
        List<Object[]> stats = bookRepository.countBooksByCategory();
        return stats.stream()
                .map(obj -> new CategoryStatsDTO(
                        ((Number) obj[0]).longValue(),
                        ((Number) obj[1]).longValue()
                ))
                .collect(Collectors.toList());
    }
}
