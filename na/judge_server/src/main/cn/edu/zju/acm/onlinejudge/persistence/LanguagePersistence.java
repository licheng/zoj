
package cn.edu.zju.acm.onlinejudge.persistence;

import java.util.List;
import java.util.Map;

import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;

public interface LanguagePersistence {

    /**
     * <p>
     * Creates the specified language in persistence layer.
     * </p>
     * 
     * @param language
     *            the Language instance to create
     * @param user
     *            the id of the user who made this modification
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    void createLanguage(Language language, long user) throws PersistenceException;

    /**
     * <p>
     * Updates the specified language in persistence layer.
     * </p>
     * 
     * @param language
     *            the Language instance to update
     * @param user
     *            the id of the user who made this modification
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    void updateLanguage(Language language, long user) throws PersistenceException;

    /**
     * <p>
     * Deletes the specified language in persistence layer.
     * </p>
     * 
     * @param id
     *            the id of the language to delete
     * @param user
     *            the id of the user who made this modification
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    void deleteLanguage(long id, long user) throws PersistenceException;

    /**
     * <p>
     * Gets the language with given id.
     * </p>
     * 
     * @param id
     *            the language id.
     * @return the language with given id or null.
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    Language getLanguage(long id) throws PersistenceException;

    /**
     * <p>
     * Gets all languages in persistence layer.
     * </p>
     * 
     * @return a list of Language instances containing all languages in persistence layer
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    List<Language> getAllLanguages() throws PersistenceException;

    /**
     * Gets a Language Map. Language id is the key and Language itself is the value.
     * 
     * @return a Language Map
     * @throws PersistenceException
     */
    Map<Long, Language> getLanguageMap();
}