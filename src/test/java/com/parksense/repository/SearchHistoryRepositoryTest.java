package com.parksense.repository;

import com.parksense.model.SearchHistory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class SearchHistoryRepositoryTest {

    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    @Test
    void savesSearchHistoryRecord() {
        SearchHistory searchHistory = new SearchHistory(
                47.6,
                -122.3,
                LocalDateTime.of(2026, 4, 12, 18, 0),
                LocalDateTime.of(2026, 4, 12, 17, 45),
                "Central Garage is the top recommendation based on availability, price, and distance"
        );

        SearchHistory savedSearchHistory = searchHistoryRepository.save(searchHistory);

        assertEquals(47.6, savedSearchHistory.getLatitude());
        assertEquals(-122.3, savedSearchHistory.getLongitude());
        assertEquals(
                "Central Garage is the top recommendation based on availability, price, and distance",
                savedSearchHistory.getBestOptionSummary()
        );
    }

    @Test
    void returnsMostRecentSearchesFirst() {
        searchHistoryRepository.save(new SearchHistory(
                47.6,
                -122.3,
                LocalDateTime.of(2026, 4, 12, 18, 0),
                LocalDateTime.of(2026, 4, 12, 17, 30),
                "Older recommendation summary"
        ));
        searchHistoryRepository.save(new SearchHistory(
                47.61,
                -122.31,
                LocalDateTime.of(2026, 4, 12, 19, 0),
                LocalDateTime.of(2026, 4, 12, 17, 45),
                "Newer recommendation summary"
        ));

        List<SearchHistory> recentSearches = searchHistoryRepository.findAll(
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "searchedAt"))
        ).getContent();

        assertEquals(2, recentSearches.size());
        assertEquals("Newer recommendation summary", recentSearches.get(0).getBestOptionSummary());
        assertEquals("Older recommendation summary", recentSearches.get(1).getBestOptionSummary());
    }
}
