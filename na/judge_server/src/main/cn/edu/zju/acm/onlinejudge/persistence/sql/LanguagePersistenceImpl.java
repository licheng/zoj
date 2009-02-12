/*
 * Copyright 2007 Zhang, Zheng <oldbig@gmail.com> Xu, Chuan <xuchuan@gmail.com>
 * 
 * This file is part of ZOJ.
 * 
 * ZOJ is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either revision 3 of the License, or (at your option) any later revision.
 * 
 * ZOJ is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with ZOJ. if not, see
 * <http://www.gnu.org/licenses/>.
 */

package cn.edu.zju.acm.onlinejudge.persistence.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;
import cn.edu.zju.acm.onlinejudge.persistence.LanguagePersistence;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceCreationException;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;

public class LanguagePersistenceImpl implements LanguagePersistence {

    /**
     * The statement to create a language.
     */
    private static final String INSERT_LANGUAGE =
            MessageFormat
                         .format(
                                 "INSERT INTO {0} ({1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)",
                                 new Object[] {DatabaseConstants.LANGUAGE_TABLE,
                                               DatabaseConstants.LANGUAGE_LANGUAGE_ID, DatabaseConstants.LANGUAGE_NAME,
                                               DatabaseConstants.LANGUAGE_DESCRIPTION,
                                               DatabaseConstants.LANGUAGE_OPTIONS, DatabaseConstants.LANGUAGE_COMPILER,
                                               DatabaseConstants.CREATE_USER, DatabaseConstants.CREATE_DATE,
                                               DatabaseConstants.LAST_UPDATE_USER, DatabaseConstants.LAST_UPDATE_DATE});

    /**
     * The statement to update a language.
     */
    private static final String UPDATE_LANGUAGE =
            MessageFormat.format("UPDATE {0} SET {1}=?, {2}=?, {3}=?, {4}=?, {5}=?, {6}=? WHERE {7}=?",
                                 new Object[] {DatabaseConstants.LANGUAGE_TABLE, DatabaseConstants.LANGUAGE_NAME,
                                               DatabaseConstants.LANGUAGE_DESCRIPTION,
                                               DatabaseConstants.LANGUAGE_OPTIONS, DatabaseConstants.LANGUAGE_COMPILER,
                                               DatabaseConstants.LAST_UPDATE_USER, DatabaseConstants.LAST_UPDATE_DATE,
                                               DatabaseConstants.LANGUAGE_LANGUAGE_ID});
    /**
     * The statement to delete a language.
     */
    private static final String DELETE_LANGUAGE =
            MessageFormat.format("DELETE FROM {0} WHERE {1}=?", new Object[] {DatabaseConstants.LANGUAGE_TABLE,
                                                                              DatabaseConstants.LANGUAGE_LANGUAGE_ID});
    /**
     * The statement to delete a language.
     */
    private static final String DELETE_SUBMISSION =
            MessageFormat
                         .format("DELETE FROM {0} WHERE {1}=?", new Object[] {DatabaseConstants.SUBMISSION_TABLE,
                                                                              DatabaseConstants.SUBMISSION_LANGUAGE_ID});
    /**
     * The query to get all languages.
     */
    private static final String GET_ALL_LANGUAGES =
            MessageFormat.format("SELECT {0}, {1}, {2}, {3}, {4} FROM {5}",
                                 new Object[] {DatabaseConstants.LANGUAGE_LANGUAGE_ID, DatabaseConstants.LANGUAGE_NAME,
                                               DatabaseConstants.LANGUAGE_DESCRIPTION,
                                               DatabaseConstants.LANGUAGE_OPTIONS, DatabaseConstants.LANGUAGE_COMPILER,
                                               DatabaseConstants.LANGUAGE_TABLE});
    /**
     * The statement to delete a language.
     */
    private static final String DELETE_LANGUAGE_CONTEST =
            MessageFormat.format("DELETE FROM {0} WHERE {1}=?",
                                 new Object[] {DatabaseConstants.CONTEST_LANGUAGE_TABLE,
                                               DatabaseConstants.CONTEST_LANGUAGE_LANGUAGE_ID});

    /**
     * The languages cache.
     */
    private Map<Long, Language> allLanguages = new HashMap<Long, Language>();

    public LanguagePersistenceImpl() throws PersistenceCreationException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement(LanguagePersistenceImpl.GET_ALL_LANGUAGES);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Language language =
                            new Language(rs.getLong(DatabaseConstants.LANGUAGE_LANGUAGE_ID),
                                         rs.getString(DatabaseConstants.LANGUAGE_NAME),
                                         rs.getString(DatabaseConstants.LANGUAGE_DESCRIPTION),
                                         rs.getString(DatabaseConstants.LANGUAGE_COMPILER),
                                         rs.getString(DatabaseConstants.LANGUAGE_OPTIONS));
                    this.allLanguages.put(language.getId(), language);
                }
            } finally {
                Database.dispose(ps);
            }
        } catch (Exception e) {
            throw new PersistenceCreationException("Fail to get all languages", e);
        } finally {
            Database.dispose(conn);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * cn.edu.zju.acm.onlinejudge.persistence.sql.LanguagePersistence#createLanguage(cn.edu.zju.acm.onlinejudge.bean
     * .enumeration.Language, long)
     */
    public void createLanguage(Language language, long user) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            synchronized (this.allLanguages) {
                try {
                    ps = conn.prepareStatement(LanguagePersistenceImpl.INSERT_LANGUAGE);
                    ps.setLong(1, language.getId());
                    ps.setString(2, language.getName());
                    ps.setString(3, language.getDescription());
                    ps.setString(4, language.getOptions());
                    ps.setString(5, language.getCompiler());
                    ps.setLong(6, user);
                    ps.setTimestamp(7, new Timestamp(new Date().getTime()));
                    ps.setLong(8, user);
                    ps.setTimestamp(9, new Timestamp(new Date().getTime()));
                    ps.executeUpdate();
                } finally {
                    Database.dispose(ps);
                }
                this.allLanguages.put(language.getId(), language);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Failed to create language.", e);
        } finally {
            Database.dispose(conn);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * cn.edu.zju.acm.onlinejudge.persistence.sql.LanguagePersistence#updateLanguage(cn.edu.zju.acm.onlinejudge.bean
     * .enumeration.Language, long)
     */
    public void updateLanguage(Language language, long user) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            synchronized (this.allLanguages) {
                try {
                    ps = conn.prepareStatement(LanguagePersistenceImpl.UPDATE_LANGUAGE);
                    ps.setString(1, language.getName());
                    ps.setString(2, language.getDescription());
                    ps.setString(3, language.getOptions());
                    ps.setString(4, language.getCompiler());
                    ps.setLong(5, user);
                    ps.setTimestamp(6, new Timestamp(new Date().getTime()));
                    ps.setLong(7, language.getId());
                    if (ps.executeUpdate() == 0) {
                        throw new PersistenceException("no such language");
                    }
                } finally {
                    Database.dispose(ps);
                }
                this.allLanguages.put(language.getId(), language);
            }
        } catch (PersistenceException pe) {
            throw pe;
        } catch (Exception e) {
            throw new PersistenceException("Failed to update language.", e);
        } finally {
            Database.dispose(conn);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see cn.edu.zju.acm.onlinejudge.persistence.sql.LanguagePersistence#deleteLanguage(long, long)
     */
    // TODO(xuchuan): this is a very dangerous operation. Check if necessary.
    public void deleteLanguage(long id, long user) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            conn.setAutoCommit(false);
            synchronized (this.allLanguages) {
                PreparedStatement ps = null;
                ps = conn.prepareStatement(LanguagePersistenceImpl.DELETE_SUBMISSION);
                try {
                    ps.setLong(1, id);
                    ps.executeUpdate();
                } finally {
                    Database.dispose(ps);
                }
                try {
                    ps = conn.prepareStatement(LanguagePersistenceImpl.DELETE_LANGUAGE_CONTEST);
                    ps.setLong(1, id);
                    ps.executeUpdate();
                } finally {
                    Database.dispose(ps);
                }
                try {
                    ps = conn.prepareStatement(LanguagePersistenceImpl.DELETE_LANGUAGE);
                    ps.setLong(1, id);
                    ps.executeUpdate();
                } finally {
                    Database.dispose(ps);
                }
                conn.commit();
                this.allLanguages.remove(id);
            }
        } catch (Exception e) {
            Database.rollback(conn);
            throw new PersistenceException("Failed to delete language.", e);
        } finally {
            Database.dispose(conn);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see cn.edu.zju.acm.onlinejudge.persistence.sql.LanguagePersistence#getLanguage(long)
     */
    public Language getLanguage(long id) throws PersistenceException {
        synchronized (this.allLanguages) {
            return this.allLanguages.get(id);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see cn.edu.zju.acm.onlinejudge.persistence.sql.LanguagePersistence#getAllLanguages()
     */
    public List<Language> getAllLanguages() throws PersistenceException {
        synchronized (this.allLanguages) {
            return new ArrayList<Language>(this.allLanguages.values());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see cn.edu.zju.acm.onlinejudge.persistence.sql.LanguagePersistence#getLanguageMap()
     */
    public Map<Long, Language> getLanguageMap() {
        synchronized (this.allLanguages) {
            return new HashMap<Long, Language>(this.allLanguages);
        }
    }
}
