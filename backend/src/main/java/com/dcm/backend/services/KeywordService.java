package com.dcm.backend.services;

import com.dcm.backend.entities.Keyword;

import java.util.List;

public interface KeywordService {

    /**
     * Gets a keyword from the database
     *
     * @param keyword Keyword to get
     * @return Keyword
     */
    public Keyword getOrAddKeyword(String keyword);

    /**
     * Gets all previously defined keywords
     *
     * @return List of keywords
     */
    public List<String> getKeywords();

    /**
     * Deletes all keywords that are not associated with any file
     */
    public void deleteUnusedKeywords();
}
