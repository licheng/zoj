package cn.edu.zju.acm.onlinejudge.judgeservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import cn.edu.zju.acm.onlinejudge.bean.Reference;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.ReferenceType;
import cn.edu.zju.acm.onlinejudge.dao.ReferenceDAO;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceCreationException;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;

public class MockReferenceDAO extends MockDAO implements ReferenceDAO {

    private AtomicLong id = new AtomicLong();

    private Map<Long, List<Reference>> referenceMap = new HashMap<Long, List<Reference>>();

    public synchronized void save(Reference reference, long problemId) {
        reference.setId(id.getAndIncrement());
        update(reference, problemId);
    }

    public synchronized void update(Reference reference, long problemId) {
        List<Reference> references = referenceMap.get(problemId);
        if (references == null) {
            references = new ArrayList<Reference>();
            referenceMap.put(problemId, references);
        }
        references.add(cloneReference(reference));

    }

    public synchronized List<Reference> getProblemReferences(long problemId, ReferenceType referenceType)
            throws PersistenceCreationException, PersistenceException {
        List<Reference> ret = new ArrayList<Reference>();
        List<Reference> references = referenceMap.get(problemId);
        if (references != null) {
            for (Reference reference : references) {
                if (reference.getReferenceType().equals(referenceType)) {
                    ret.add(reference);
                }
            }
        }
        return ret;
    }

    private Reference cloneReference(Reference reference) {
        Reference ret = new Reference();
        ret.setId(reference.getId());
        byte[] content = new byte[reference.getContent().length];
        System.arraycopy(reference.getContent(), 0, content, 0, content.length);
        ret.setContent(content);
        ret.setContentType(reference.getContentType());
        ret.setName(reference.getName());
        ret.setReferenceType(reference.getReferenceType());
        ret.setCompressed(reference.isCompressed());
        return ret;
    }
}
