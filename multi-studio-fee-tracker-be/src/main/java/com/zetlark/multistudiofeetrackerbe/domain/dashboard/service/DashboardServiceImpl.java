package com.zetlark.multistudiofeetrackerbe.domain.dashboard.service;

import com.zetlark.multistudiofeetrackerbe.domain.activity.entity.UserClientActivity;
import com.zetlark.multistudiofeetrackerbe.domain.activity.repository.UserClientActivityRepository;
import com.zetlark.multistudiofeetrackerbe.domain.dashboard.dto.DashdboardDataDto;
import com.zetlark.multistudiofeetrackerbe.application.config.security.CurrentUserProvider;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final UserClientActivityRepository userClientActivityRepository;
    private final CurrentUserProvider currentUserProvider;

    public DashboardServiceImpl(UserClientActivityRepository userClientActivityRepository, CurrentUserProvider currentUserProvider) {
        this.userClientActivityRepository = userClientActivityRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public DashdboardDataDto getData(Long month) {
        List<UserClientActivity> userClientActivities = currentUserProvider.isAdmin()
                ? userClientActivityRepository.findAllByMonth(month)
                : userClientActivityRepository.findAllByAppUserAndMonth(currentUserProvider.getUsername(), month);

        Long uniqueClients = userClientActivities == null ? 0L :
                userClientActivities.stream()
                        .map(a -> a.getClient() == null ? null : a.getClient().getId())
                        .filter(java.util.Objects::nonNull)
                        .distinct()
                        .count();

        BigDecimal totalRevenue = userClientActivities == null ? BigDecimal.ZERO :
                userClientActivities.stream()
                        .filter(java.util.Objects::nonNull)
                        .map(UserClientActivity::getFee)
                        .filter(java.util.Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);


        Long workDays = userClientActivities == null ? 0L :
                userClientActivities.stream()
                        .filter(java.util.Objects::nonNull)
                        .map(UserClientActivity::getDate)
                        .distinct()
                        .count();

        DashdboardDataDto dto = new DashdboardDataDto();
        dto.setMonth(month);
        dto.setTotalClients(uniqueClients);
        dto.setTotalRevenue(totalRevenue);
        dto.setWorkDays(workDays);
        return dto;
    }
}
