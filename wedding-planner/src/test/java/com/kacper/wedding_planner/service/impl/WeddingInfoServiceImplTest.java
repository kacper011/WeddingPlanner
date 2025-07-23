package com.kacper.wedding_planner.service.impl;

import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.model.WeddingInfo;
import com.kacper.wedding_planner.repository.WeddingInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeddingInfoServiceImplTest {

    @Mock
    private WeddingInfoRepository weddingInfoRepository;
    @InjectMocks
    private WeddingInfoServiceImpl weddingInfoService;
    private User testUser;
    private WeddingInfo weddingInfo;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        weddingInfo = new WeddingInfo();
        weddingInfo.setBrideName("Anna");
        weddingInfo.setGroomName("Jan");
        weddingInfo.setWeddingDate(LocalDate.of(2025, 8, 15));
    }

    @Test
    void shouldUpdateExistingWeddingInfor() {

        //given
        WeddingInfo existingInfo = new WeddingInfo();
        existingInfo.setUser(testUser);
        existingInfo.setBrideName("Old Bride");
        existingInfo.setGroomName("Old Groom");
        existingInfo.setWeddingDate(LocalDate.of(2020, 1, 1));

        when(weddingInfoRepository.findByUser(testUser)).thenReturn(Optional.of(existingInfo));

        //when
        weddingInfoService.saveOrUpdateWeddingInfo(weddingInfo, testUser);

        //then
        verify(weddingInfoRepository).save(existingInfo);
        verify(weddingInfoRepository, never()).save(weddingInfo);

        assert existingInfo.getBrideName().equals("Anna");
        assert existingInfo.getGroomName().equals("Jan");
        assert existingInfo.getWeddingDate().equals(LocalDate.of(2025, 8, 15));
    }
}