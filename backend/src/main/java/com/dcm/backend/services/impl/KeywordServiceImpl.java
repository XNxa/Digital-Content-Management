package com.dcm.backend.services.impl;

import com.dcm.backend.entities.Keyword;
import com.dcm.backend.repositories.KeywordRepository;
import com.dcm.backend.repositories.specifications.UnusedKeywordsSpecification;
import com.dcm.backend.services.KeywordService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KeywordServiceImpl implements KeywordService {

    @Autowired
    KeywordRepository keywordRepository;

    @Override
    @Transactional
    public void deleteUnusedKeywords() {
        UnusedKeywordsSpecification spec = new UnusedKeywordsSpecification();
        keywordRepository.delete(spec);
    }

    @Override
    public Keyword getOrAddKeyword(String keyword) {
        Optional<Keyword> k = keywordRepository.findById(keyword);
        if (k.isEmpty()) {
            k = Optional.of(keywordRepository.save(new Keyword(keyword)));
        }
        return k.get();
    }

    public List<String> getAll() {
        return keywordRepository.findAll()
                .stream()
                .map(Keyword::getName)
                .toList();
    }

}
