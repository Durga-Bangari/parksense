package com.parksense.controller;

import com.parksense.config.ParkingProviderProperties;
import com.parksense.provider.ParkingDataProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

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
}
