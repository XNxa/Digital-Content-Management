package com.dcm.backend.services;

import com.dcm.backend.entities.FileHeader;
import com.dcm.backend.exceptions.BrokenLinkException;

public interface LinkService {

    /**
     * Generate a link for a file
     *
     * @param file the file to generate the link for
     * @return the generated link
     */
    String generateLink(FileHeader file);

    /**
     * Access a file by its link
     *
     * @param uuid the link to access the file
     * @return the file header
     */
    FileHeader access(String uuid) throws BrokenLinkException;

}
