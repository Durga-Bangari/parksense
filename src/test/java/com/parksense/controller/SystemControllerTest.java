package com.parksense.controller;

import com.parksense.config.ParkingProviderProperties;
import com.parksense.model.SearchHistoryItem;
import com.parksense.provider.ParkingDataProvider;
import com.parksense.service.SearchHistoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SystemController.class)
class SystemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParkingDataProvider parkingDataProvider;

    @MockBean
    private ParkingProviderProperties parkingProviderProperties;

    @MockBean
    private SearchHistoryService searchHistoryService;

    @Test
    void healthEndpointReturnsApplicationStatus() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.application").value("ParkSense"));
    }

    @Test
    void providerEndpointReturnsProviderDiagnostics() throws Exception {
        given(parkingProviderProperties.getType()).willReturn("google-maps");
        given(parkingProviderProperties.getGoogleMapsApiKey()).willReturn("demo-key");
        given(parkingProviderProperties.isFallbackToMockOnFailure()).willReturn(true);
        given(parkingProviderProperties.getSearchRadiusMeters()).willReturn(1500);
        given(parkingDataProvider.getProviderType()).willReturn("google-maps");

        mockMvc.perform(get("/api/v1/provider"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.configuredProviderType").value("google-maps"))
                .andExpect(jsonPath("$.activeProviderType").value("google-maps"))
                .andExpect(jsonPath("$.providerReady").value(true))
                .andExpect(jsonPath("$.statusMessage").value("Provider configuration is ready"))
                .andExpect(jsonPath("$.fallbackToMockOnFailure").value(true))
                .andExpect(jsonPath("$.searchRadiusMeters").value(1500));
    }

    @Test
    void providerEndpointReportsIncompleteGoogleMapsConfiguration() throws Exception {
        given(parkingProviderProperties.getType()).willReturn("google-maps");
        given(parkingProviderProperties.getGoogleMapsApiKey()).willReturn("");
        given(parkingProviderProperties.isFallbackToMockOnFailure()).willReturn(true);
        given(parkingProviderProperties.getSearchRadiusMeters()).willReturn(1500);
        given(parkingDataProvider.getProviderType()).willReturn("google-maps");

        mockMvc.perform(get("/api/v1/provider"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.providerReady").value(false))
                .andExpect(jsonPath("$.statusMessage").value("Provider configuration is incomplete"));
    }

    @Test
    void searchHistoryEndpointReturnsRecentSearches() throws Exception {
        given(searchHistoryService.getRecentSearches()).willReturn(List.of(
                new SearchHistoryItem(
                        1L,
                        47.6,
                        -122.3,
                        LocalDateTime.of(2026, 4, 12, 18, 0),
                        LocalDateTime.of(2026, 4, 12, 17, 45),
                        "Central Garage is the top recommendation based on availability, price, and distance"
                )
        ));

        mockMvc.perform(get("/api/v1/search-history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].latitude").value(47.6))
                .andExpect(jsonPath("$[0].longitude").value(-122.3))
                .andExpect(jsonPath("$[0].bestOptionSummary")
                        .value("Central Garage is the top recommendation based on availability, price, and distance"));
    }
}
