package cn.edu.zju.acm.onlinejudge.dao;

import java.util.List;

import cn.edu.zju.acm.onlinejudge.bean.Reference;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.ReferenceType;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceCreationException;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;

public interface ReferenceDAO extends GenericDAO {

    List<Reference> getProblemReferences(long problemId, ReferenceType referenceType) throws PersistenceCreationException, PersistenceException;

    void save(Reference reference, long problemId) throws PersistenceCreationException, PersistenceException;

}