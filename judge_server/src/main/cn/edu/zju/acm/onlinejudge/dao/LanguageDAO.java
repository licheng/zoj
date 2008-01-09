package cn.edu.zju.acm.onlinejudge.dao;

import java.util.List;

import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;

public interface LanguageDAO extends GenericDAO {

    List<Language> getAllLanguages() throws PersistenceException;

}