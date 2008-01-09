package cn.edu.zju.acm.onlinejudge.dao;

import java.util.List;

import cn.edu.zju.acm.onlinejudge.bean.Reference;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.ReferenceType;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceCreationException;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;

public class ReferenceDAOImpl extends AbstractDAO implements ReferenceDAO {

    public List<Reference> getProblemReferences(long problemId, ReferenceType referenceType) throws PersistenceCreationException, PersistenceException {
	return PersistenceManager.getInstance().getReferencePersistence().getProblemReferences(problemId, referenceType);
    }

    public void save(Reference reference, long problemId) throws PersistenceCreationException, PersistenceException {
	PersistenceManager.getInstance().getReferencePersistence().createProblemReference(problemId, reference, 1);
    }

}