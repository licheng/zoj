/*
 * Copyright 2007 Zhang, Zheng <oldbig@gmail.com>
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

package cn.edu.zju.acm.onlinejudge.persistence;

import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.request.ProblemCriteria;
import java.util.List;

/**
 * <p>ProblemPersistence interface defines the API used to manager the problem related affairs
 * in persistence layer.</p>
 *
 * @version 2.0
 * @author ZOJDEV
 */
public interface ProblemPersistence {


    /**
     * <p>Creates the specified problem in persistence layer.</p>
     *
     * @param problem the Problem instance to create
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    void createProblem(Problem problem, long user) throws PersistenceException;

    /**
     * <p>Updates the specified problem in persistence layer.</p>
     *
     * @param problem the Problem instance to update
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    void updateProblem(Problem problem, long user) throws PersistenceException;

    /**
     * <p>Deletes the specified problem in persistence layer.</p>
     *
     * @param id the id of the problem to delete
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    void deleteProblem(long id, long user) throws PersistenceException;

    /**
     * <p>Gets the problem with given id in persistence layer.</p>
     *
     * @param id the id of the problem
     * @return the problem with given id in persistence layer
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    Problem getProblem(long id) throws PersistenceException;


    /**
     * <p>Searchs all problems according with the given criteria in persistence layer.</p>
     *
     * @return a list of problems according with the given criteria
     * @param criteria the problem search criteria
     * @param offset the offset of the start position to search
     * @param count the maximum number of problems in returned list
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    List<Problem> searchProblems(ProblemCriteria criteria, int offset, int count) throws PersistenceException;

	List<Problem> searchProblems(ProblemCriteria criteria) throws PersistenceException;
    
    int getProblemsCount(long contestId) throws PersistenceException;

}


