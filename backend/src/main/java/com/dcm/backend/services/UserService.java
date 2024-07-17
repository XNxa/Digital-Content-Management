package com.dcm.backend.services;

import com.dcm.backend.dto.UserDTO;
import com.dcm.backend.exceptions.UserNotFoundException;

import java.util.Collection;

public interface UserService {

    /**
     * Count the number of users
     *
     * @return the number of users
     */
    int count();

    /**
     * List users
     *
     * @param firstResult the first result
     * @param maxResults  the max number of results
     * @param filter      the filter
     * @return the list of users
     */
    Collection<UserDTO> list(int firstResult, int maxResults, UserDTO filter);

    /**
     * Create a user
     *
     * @param user the user to create
     */
    void create(UserDTO user);

    /**
     * Delete a user by id
     *
     * @param id the keycloak id of the user
     */
    void delete(String id);

    /**
     * Update a user
     *
     * @param user the user to update
     */
    void update(UserDTO user) throws UserNotFoundException;

    /**
     * Get a user by id
     *
     * @param id the keycloak id of the user
     * @return the user
     */
    UserDTO getUser(String id);

    /**
     * Get the functions of all users
     *
     * @return the functions
     */
    Collection<String> getFunctions();
}
