package com.parksense.service;

import com.parksense.model.SearchHistory;
import com.parksense.model.SearchHistoryItem;
import com.parksense.repository.SearchHistoryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchHistoryService {

    private static final int DEFAULT_LIMIT = 10;
    private static final int MAX_LIMIT = 50;

    private final SearchHistoryRepository searchHistoryRepository;

    public SearchHistoryService(SearchHistoryRepository searchHistoryRepository) {
        this.searchHistoryRepository = searchHistoryRepository;
    }

    public List<SearchHistoryItem> getRecentSearches(int limit) {
        int normalizedLimit = Math.min(Math.max(1, limit), MAX_LIMIT);

        return searchHistoryRepository.findAll(
                        PageRequest.of(0, normalizedLimit, Sort.by(Sort.Direction.DESC, "searchedAt"))
                ).stream()
                .map(this::toSearchHistoryItem)
                .toList();
    }

    public List<SearchHistoryItem> getRecentSearches() {
        return getRecentSearches(DEFAULT_LIMIT);
    }

    private SearchHistoryItem toSearchHistoryItem(SearchHistory searchHistory) {
        return new SearchHistoryItem(
                searchHistory.getId(),
                searchHistory.getLatitude(),
                searchHistory.getLongitude(),
                searchHistory.getArrivalTime(),
                searchHistory.getSearchedAt(),
                searchHistory.getBestOptionSummary()
        );
    }
}
