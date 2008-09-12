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
import java.util.ArrayList;
import java.util.List;

import cn.edu.zju.acm.onlinejudge.bean.Reference;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.ReferenceType;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.persistence.ReferencePersistence;

/**
 * <p>
 * ReferencePersistenceImpl implements ReferencePersistence interface
 * </p>
 * <p>
 * ReferencePersistence interface defines the API used to manager the reference related affairs in persistence layer.
 * </p>
 * 
 * @version 2.0
 * @author Zhang, Zheng
 * @author Xu, Chuan
 */
public class ReferencePersistenceImpl implements ReferencePersistence {

    /**
     * <p>
     * Creates the specified problem reference in persistence layer.
     * </p>
     * 
     * @param problemId
     *            the id of the referred problem
     * @param reference
     *            the reference which the problem refer to
     * @param user
     *            the id of the user who made this modification
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public void createProblemReference(long problemId, Reference reference, long user) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            conn.setAutoCommit(false);
            PreparedStatement ps = null;
            try {
                ps =
                        conn
                            .prepareStatement("INSERT INTO reference (reference_type_id, name, content_type, "
                                + "content, size, compressed, create_user, create_date, last_update_user, last_update_date) "
                                + "VALUES(?,?,?,?,?,?,?,?,?,?)");
                ps.setLong(1, reference.getReferenceType().getId());
                ps.setString(2, reference.getName());
                ps.setString(3, reference.getContentType());
                ps.setBytes(4, reference.getContent());
                ps.setLong(5, reference.getSize());
                ps.setBoolean(6, reference.isCompressed());
                ps.setLong(7, user);
                ps.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
                ps.setLong(9, user);
                ps.setTimestamp(10, new Timestamp(System.currentTimeMillis()));
                ps.executeUpdate();
            } finally {
                Database.dispose(ps);
            }
            reference.setId(Database.getLastId(conn));
            try {
                ps = conn.prepareStatement("INSERT INTO problem_reference (problem_id, reference_id) VALUES (?,?)");
                ps.setLong(1, problemId);
                ps.setLong(2, reference.getId());
                ps.executeUpdate();
            } finally {
                Database.dispose(ps);
            }
            conn.commit();
        } catch (Exception e) {
            Database.rollback(conn);
            throw new PersistenceException("Failed to create problem reference.", e);
        } finally {
            Database.dispose(conn);
        }

    }

    /**
     * <p>
     * Creates the specified contest reference in persistence layer.
     * </p>
     * 
     * @param contestId
     *            the id of the referred contest
     * @param reference
     *            the reference which the contest refer to
     * @param user
     *            the id of the user who made this modification
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public void createContestReference(long contestId, Reference reference, long user) throws PersistenceException {
    // empty
    }

    /**
     * <p>
     * Creates the specified post reference in persistence layer.
     * </p>
     * 
     * @param postId
     *            the id of the referred post
     * @param reference
     *            the reference which the contest refer to
     * @param user
     *            the id of the user who made this modification
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public void createPostReference(long postId, Reference reference, long user) throws PersistenceException {
    // empty
    }

    /**
     * <p>
     * Updates the specified reference in persistence layer.
     * </p>
     * 
     * @param reference
     *            the Reference instance to update
     * @param user
     *            the id of the user who made this modification
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public void updateReference(Reference reference, long user) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            try {
                ps =
                        conn
                            .prepareStatement("UPDATE reference SET reference_type_id=?, name=?, content_type=?, "
                                + "content=?, size=?, compressed=?, last_update_user=?, last_update_date=NOW() WHERE reference_id=?");
                ps.setLong(1, reference.getReferenceType().getId());
                ps.setString(2, reference.getName());
                ps.setString(3, reference.getContentType());
                ps.setBytes(4, reference.getContent());
                ps.setLong(5, reference.getSize());
                ps.setBoolean(6, reference.isCompressed());
                ps.setLong(7, user);
                ps.setLong(8, reference.getId());
                ps.executeUpdate();
            } finally {
                Database.dispose(ps);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Failed to create problem.", e);
        } finally {
            Database.dispose(conn);
        }
    }

    /**
     * <p>
     * Deletes the specified reference in persistence layer.
     * </p>
     * 
     * @param id
     *            the id of the reference to delete
     * @param user
     *            the id of the user who made this modification
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public void deleteReference(long id, long user) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            conn.setAutoCommit(false);
            PreparedStatement ps = null;
            try {
                String query = "DELETE FROM problem_reference WHERE reference_id = ?";
                ps = conn.prepareStatement(query);
                ps.setLong(1, id);
                ps.executeUpdate();
            } finally {
                Database.dispose(ps);
            }
            try {
                ps = conn.prepareStatement("DELETE FROM reference WHERE reference_id = ?");
                ps.setLong(1, id);
                ps.executeUpdate();
            } finally {
                Database.dispose(ps);
            }
            conn.commit();
        } catch (Exception e) {
            Database.rollback(conn);
            throw new PersistenceException("Failed to create problem.", e);
        } finally {
            Database.dispose(conn);
        }

    }

    /**
     * <p>
     * Gets the reference with given id in persistence layer.
     * </p>
     * 
     * @param id
     *            the id of the reference
     * @return the reference with given id in persistence layer
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public Reference getReference(long id) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            try {
                ps =
                        conn.prepareStatement("SELECT reference_id, reference_type_id, name, content_type, "
                            + "content, size, compressed FROM reference WHERE reference_id=?");
                ps.setLong(1, id);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    return null;
                }
                return this.populateReference(rs);
            } finally {
                Database.dispose(ps);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Failed to get the reference with id " + id, e);
        } finally {
            Database.dispose(conn);
        }
    }

    /**
     * Populates a Reference with given ResultSet.
     * 
     * @param rs
     * @return a Reference instance
     * @throws SQLException
     */
    private Reference populateReference(ResultSet rs) throws SQLException {
        Reference reference = new Reference();
        reference.setId(rs.getLong(DatabaseConstants.REFERENCE_REFERENCE_ID));
        long refTypeId = rs.getLong(DatabaseConstants.REFERENCE_REFERENCE_TYPE_ID);
        reference.setReferenceType(ReferenceType.findById(refTypeId));
        reference.setName(rs.getString(DatabaseConstants.REFERENCE_NAME));
        reference.setContentType(rs.getString(DatabaseConstants.REFERENCE_CONTENT_TYPE));
        reference.setContent(rs.getBytes(DatabaseConstants.REFERENCE_CONTENT));
        reference.setSize(rs.getLong(DatabaseConstants.REFERENCE_SIZE));
        reference.setCompressed(rs.getBoolean(DatabaseConstants.REFERENCE_COMPRESSED));
        return reference;
    }

    /**
     * Populates a Reference with given ResultSet.
     * 
     * @param rs
     * @return a Reference instance
     * @throws SQLException
     */
    private Reference populateReferenceInfo(ResultSet rs) throws SQLException {
        Reference reference = new Reference();
        reference.setId(rs.getLong(DatabaseConstants.REFERENCE_REFERENCE_ID));
        long refTypeId = rs.getLong(DatabaseConstants.REFERENCE_REFERENCE_TYPE_ID);
        reference.setReferenceType(ReferenceType.findById(refTypeId));
        reference.setName(rs.getString(DatabaseConstants.REFERENCE_NAME));
        reference.setContentType(rs.getString(DatabaseConstants.REFERENCE_CONTENT_TYPE));
        reference.setSize(rs.getLong(DatabaseConstants.REFERENCE_SIZE));
        return reference;
    }

    /**
     * <p>
     * Gets all problem references to the given problem with specified reference type.
     * </p>
     * 
     * @return a list containing all problem references to the given problem with specified reference type
     * @param problemId
     *            the id of the referred problem
     * @param referenceType
     *            the reference type of the returned references
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public List<Reference> getProblemReferences(long problemId, ReferenceType referenceType) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            try {
                ps =
                        conn.prepareStatement("SELECT r.reference_id, reference_type_id, name, content_type, "
                            + "content, size, compressed "
                            + "FROM problem_reference pr LEFT JOIN reference r ON pr.reference_id = r.reference_id "
                            + "WHERE pr.problem_id = ? AND r.reference_type_id=?");
                ps.setLong(1, problemId);
                ps.setLong(2, referenceType.getId());
                ResultSet rs = ps.executeQuery();

                List<Reference> references = new ArrayList<Reference>();
                while (rs.next()) {
                    Reference reference = this.populateReference(rs);
                    references.add(reference);
                }
                return references;
            } finally {
                Database.dispose(ps);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Failed to get the references", e);
        } finally {
            Database.dispose(conn);
        }
    }

    /**
     * <p>
     * Gets all problem reference without data to the given problem with specified reference type.
     * </p>
     * 
     * @return a list containing all problem references to the given problem with specified reference type
     * @param problemId
     *            the id of the referred problem
     * @param referenceType
     *            the reference type of the returned references
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public List<Reference> getProblemReferenceInfo(long problemId, ReferenceType referenceType) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            try {
                ps =
                        conn.prepareStatement("SELECT r.reference_id, reference_type_id, name, content_type, size "
                            + "FROM problem_reference pr LEFT JOIN reference r ON pr.reference_id = r.reference_id "
                            + "WHERE pr.problem_id = ? AND r.reference_type_id=?");
                ps.setLong(1, problemId);
                ps.setLong(2, referenceType.getId());
                ResultSet rs = ps.executeQuery();
                List<Reference> references = new ArrayList<Reference>();
                while (rs.next()) {
                    Reference reference = this.populateReferenceInfo(rs);
                    references.add(reference);
                }
                return references;
            } finally {
                Database.dispose(ps);
            }
        } catch (Exception e) {
            throw new PersistenceException("Failed to get the reference info", e);
        } finally {
            Database.dispose(conn);
        }
    }

    /**
     * <p>
     * Gets all contest references to the given contest with specified reference type.
     * </p>
     * 
     * @return a list containing all contest references to the given contest with specified reference type
     * @param contestId
     *            the id of the referred contest
     * @param referenceType
     *            the reference type of the returned references
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public List<Reference> getContestReferences(long contestId, ReferenceType referenceType) throws PersistenceException {
        return null;
    }

    /**
     * <p>
     * Gets all post references to the given post with specified reference type.
     * </p>
     * 
     * @return a list containing all post references to the given post with specified reference type
     * @param postId
     *            the id of the referred post
     * @param referenceType
     *            the reference type of the returned references
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public List<Reference> getPostReferences(long postId, ReferenceType referenceType) throws PersistenceException {
        return null;

    }

}
