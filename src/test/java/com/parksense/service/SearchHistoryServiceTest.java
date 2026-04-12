package com.parksense.service;

import com.parksense.model.SearchHistory;
import com.parksense.model.SearchHistoryItem;
import com.parksense.repository.SearchHistoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class SearchHistoryServiceTest {

    private final SearchHistoryRepository searchHistoryRepository = mock(SearchHistoryRepository.class);
    private final SearchHistoryService searchHistoryService = new SearchHistoryService(searchHistoryRepository);

    @Test
    void returnsRecentSearchesUsingRequestedLimit() {
        SearchHistory latestSearch = new SearchHistory(
                47.6,
                -122.3,
                LocalDateTime.of(2026, 4, 12, 18, 0),
                LocalDateTime.of(2026, 4, 12, 17, 45),
                "Central Garage is the top recommendation based on availability, price, and distance"
        );
        PageRequest pageRequest = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "searchedAt"));
        given(searchHistoryRepository.findAll(eq(pageRequest)))
                .willReturn(new PageImpl<>(List.of(latestSearch)));

        List<SearchHistoryItem> recentSearches = searchHistoryService.getRecentSearches(5);

        assertEquals(1, recentSearches.size());
        assertEquals(47.6, recentSearches.get(0).latitude());
        assertEquals(
                "Central Garage is the top recommendation based on availability, price, and distance",
                recentSearches.get(0).bestOptionSummary()
        );
    }

    @Test
    void capsRecentSearchLimitAtMaximum() {
        PageRequest maxPageRequest = PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "searchedAt"));
        given(searchHistoryRepository.findAll(eq(maxPageRequest)))
                .willReturn(new PageImpl<>(List.of()));

        List<SearchHistoryItem> recentSearches = searchHistoryService.getRecentSearches(200);

        assertEquals(0, recentSearches.size());
    }
}
