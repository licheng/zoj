/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.persistence.sql;

import cn.edu.zju.acm.onlinejudge.bean.Reference;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.ReferenceType;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.persistence.ReferencePersistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;



import java.sql.Blob;

/**
 * <p>ReferencePersistenceImpl implements ReferencePersistence interface</p>
 * <p>ReferencePersistence interface defines the API used to manager the reference related affairs
 * in persistence layer.</p>
 *
 * @version 2.0
 * @author ZOJDEV
 */
public class ReferencePersistenceImpl implements ReferencePersistence {


    /**
     * <p>Creates the specified problem reference in persistence layer.</p>
     *
     * @param problemId the id of the refered problem
     * @param reference the reference which the problem refer to
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void createProblemReference(long problemId, Reference reference, long user) throws PersistenceException {
    	
    	
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	conn = Database.createConnection();   
        	conn.setAutoCommit(false);
        	String query = "INSERT INTO reference (reference_type_id, name, content_type, " +
        			"content, size, compressed, create_user, create_date, last_update_user, last_update_date) " +
        			"VALUES(?,?,?,?,?,?,?,?,?,?)" ;
        	ps = conn.prepareStatement(query);
        	ps.setLong(1, reference.getReferenceType().getId());
        	ps.setString(2, reference.getName());
        	ps.setString(3, reference.getContentType());
        	//ps.setBytes(4, new byte[0]);
        	
        	ps.setBytes(4, reference.getContent());
        	
        	if (reference.getContent().length < 10000) for(int i = 0; i < reference.getContent().length; i+=50) {
        		System.out.print(reference.getContent()[i] + " ");
        	}
        	ps.setLong(5, reference.getSize());
        	ps.setBoolean(6, reference.isCompressed());
            ps.setLong(7, user);
            ps.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
            ps.setLong(9, user);
            ps.setTimestamp(10, new Timestamp(System.currentTimeMillis()));
            ps.executeUpdate();            	
        	reference.setId(Database.getLastId(conn, ps, rs));
        	/*
        	query = "SELECT content FROM reference WHERE reference_id=?";
        	ps = conn.prepareStatement(query);
        	ps.setLong(1, reference.getId());
        	rs = ps.executeQuery();
        	rs.next();
        	
        	Blob blob = rs.getBlob(1);
        	blob.setBytes(1, reference.getContent());
        	query = "UPDATE reference SET content=? WHERE reference_id=?";
        	ps = conn.prepareStatement(query);
        	ps.setBlob(1, blob);
        	ps.setLong(2, reference.getId());
        	ps.executeUpdate();
        	*/
        	query = "INSERT INTO problem_reference (problem_id, reference_id) VALUES (?,?)";
        	ps = conn.prepareStatement(query);
        	ps.setLong(1, problemId);
        	ps.setLong(2, reference.getId());
        	ps.executeUpdate();
        	
            conn.commit();
        } catch (SQLException e) {
        	Database.rollback(conn);
        	throw new PersistenceException("Failed to create problem reference.", e);
		} finally {
			Database.dispose(conn, ps, rs);
        }   
		
    }

    /**
     * <p>Creates the specified contest reference in persistence layer.</p>
     *
     * @param contestId the id of the refered contest
     * @param reference the reference which the contest refer to
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void createContestReference(long contestId, Reference reference, long user) throws PersistenceException {
    	
    }

    /**
     * <p>Creates the specified post reference in persistence layer.</p>
     *
     * @param postId the id of the refered post
     * @param reference the reference which the contest refer to
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void createPostReference(long postId, Reference reference, long user) throws PersistenceException {
    	
    }

    /**
     * <p>Updates the specified reference in persistence layer.</p>
     *
     * @param reference the Reference instance to update
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void updateReference(Reference reference, long user) throws PersistenceException {
    	
    	
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	conn = Database.createConnection();   
        	String query = "UPDATE reference SET reference_type_id=?, name=?, content_type=?, " +
        			"content=?, size=?, compressed=?, last_update_user=?, last_update_date=NOW() WHERE reference_id=?";
        	ps = conn.prepareStatement(query);
        	ps.setLong(1, reference.getReferenceType().getId());
        	ps.setString(2, reference.getName());
        	ps.setString(3, reference.getContentType());
        	ps.setBytes(4, reference.getContent());
        	ps.setLong(5, reference.getSize());
        	ps.setBoolean(6, reference.isCompressed());
            ps.setLong(7, user);
            ps.setLong(8, reference.getId());
            ps.executeUpdate();
        	            
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to create problem.", e);
		} finally {
			Database.dispose(conn, ps, rs);
        }   
		
    }

    /**
     * <p>Deletes the specified reference in persistence layer.</p>
     *
     * @param id the id of the reference to delete
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void deleteReference(long id, long user) throws PersistenceException {
    	
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	conn = Database.createConnection();   
        	conn.setAutoCommit(false);
        	
        	String query = "DELETE FROM problem_reference WHERE reference_id = ?";        	
        	ps = conn.prepareStatement(query);
        	ps.setLong(1, id);
        	ps.executeUpdate();
        	
        	query = "DELETE FROM reference WHERE reference_id = ?";        	
        	ps = conn.prepareStatement(query);
        	ps.setLong(1, id);        	
        	ps.executeUpdate();
        	
            conn.commit();
        } catch (SQLException e) {
        	Database.rollback(conn);
        	throw new PersistenceException("Failed to create problem.", e);
		} finally {
			Database.dispose(conn, ps, rs);
        }   
		
    }

    /**
     * <p>Gets the reference with given id in persistence layer.</p>
     *
     * @param id the id of the reference
     * @return the reference with given id in persistence layer
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public Reference getReference(long id) throws PersistenceException {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
            	
        try {
        	conn = Database.createConnection();  
        	String query = "SELECT reference_id, reference_type_id, name, content_type, " +
        			"content, size, compressed FROM reference WHERE reference_id=?";
        	ps = conn.prepareStatement(query);   
            ps.setLong(1, id);
            rs = ps.executeQuery();
                                     
            if (!rs.next()) {
            	return null;
            }
            Reference reference = populateReference(rs);
           
            return reference;
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to get the reference with id " + id, e);
		} finally {
        	Database.dispose(conn, ps, rs);
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
     * <p>Gets all problem references to the given problem with specified reference type.</p>
     *
     * @return a list containing all problem references to the given problem with specified reference type
     * @param problemId the id of the refered problem
     * @param referenceType the reference type of the returend references
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public List getProblemReferences(long problemId, ReferenceType referenceType) throws PersistenceException {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
            
        try {
        	conn = Database.createConnection();  
        	String query = "SELECT r.reference_id, reference_type_id, name, content_type, " +
        			"content, size, compressed " +
        			"FROM problem_reference pr LEFT JOIN reference r ON pr.reference_id = r.reference_id " +
        			"WHERE pr.problem_id = ? AND r.reference_type_id=?";
        	ps = conn.prepareStatement(query);   
            ps.setLong(1, problemId);
            ps.setLong(2, referenceType.getId());
            rs = ps.executeQuery();
                   
            List references = new ArrayList();
            while (rs.next()) {
            	Reference reference = populateReference(rs);
            	references.add(reference);
            }                       
            return references;
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to get the references", e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }   
    }
    
    /**
     * <p>Gets all problem reference without data to the given problem with specified reference type.</p>
     *
     * @return a list containing all problem references to the given problem with specified reference type
     * @param problemId the id of the refered problem
     * @param referenceType the reference type of the returend references
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public List getProblemReferenceInfo(long problemId, ReferenceType referenceType) throws PersistenceException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
            
        try {
            conn = Database.createConnection();  
            String query = "SELECT r.reference_id, reference_type_id, name, content_type, size " +
                    "FROM problem_reference pr LEFT JOIN reference r ON pr.reference_id = r.reference_id " +
                    "WHERE pr.problem_id = ? AND r.reference_type_id=?";
            ps = conn.prepareStatement(query);   
            ps.setLong(1, problemId);
            ps.setLong(2, referenceType.getId());
            rs = ps.executeQuery();
                   
            List references = new ArrayList();
            while (rs.next()) {                
                Reference reference = populateReferenceInfo(rs);
                references.add(reference);
            }                       
            return references;
            
        } catch (SQLException e) {
            throw new PersistenceException("Failed to get the reference info", e);
        } finally {
            Database.dispose(conn, ps, rs);
        }   
    }

    /**
     * <p>Gets all contest references to the given contest with specified reference type.</p>
     *
     * @return a list containing all contest references to the given contest with specified reference type
     * @param contestId the id of the refered contest
     * @param referenceType the reference type of the returend references
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public List getContestReferences(long contestId, ReferenceType referenceType) throws PersistenceException {
    	return null;
    }

    /**
     * <p>Gets all post references to the given post with specified reference type.</p>
     *
     * @return a list containing all post references to the given post with specified reference type
     * @param postId the id of the refered post
     * @param referenceType the reference type of the returend references
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public List getPostReferences(long postId, ReferenceType referenceType) throws PersistenceException {
    	return null;
    	
    }
    
}
