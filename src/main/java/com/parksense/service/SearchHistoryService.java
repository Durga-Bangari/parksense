package com.parksense.service;

import com.parksense.model.SearchHistory;
import com.parksense.model.SearchHistoryItem;
import com.parksense.repository.SearchHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;

    public SearchHistoryService(SearchHistoryRepository searchHistoryRepository) {
        this.searchHistoryRepository = searchHistoryRepository;
    }

    public List<SearchHistoryItem> getRecentSearches() {
        return searchHistoryRepository.findTop10ByOrderBySearchedAtDesc().stream()
                .map(this::toSearchHistoryItem)
                .toList();
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
