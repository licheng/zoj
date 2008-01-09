package cn.edu.zju.acm.onlinejudge.dao;

import java.util.List;

import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;

class LanguageDAOImpl extends AbstractDAO implements LanguageDAO {

    public List<Language> getAllLanguages() throws PersistenceException {
	return PersistenceManager.getInstance().getContestPersistence().getAllLanguages();
    }
}
