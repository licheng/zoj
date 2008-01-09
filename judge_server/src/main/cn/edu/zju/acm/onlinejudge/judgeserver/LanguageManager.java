package cn.edu.zju.acm.onlinejudge.judgeserver;

import java.util.HashMap;
import java.util.Map;

import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;
import cn.edu.zju.acm.onlinejudge.dao.DAOFactory;
import cn.edu.zju.acm.onlinejudge.dao.LanguageDAO;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;

public class LanguageManager {
    private static Map<Long, Language> languageMap = new HashMap<Long, Language>();

    private static Map<String, Language> languageExtensionMap = new HashMap<String, Language>();

    private static LanguageDAO languageDAO = DAOFactory.getLanguageDAO();

    static {
	try {
	    reload();
	} catch (PersistenceException e) {
	    throw new ExceptionInInitializerError(e);
	}
    }

    synchronized public static Language getLanguage(long languageId) {
	return languageMap.get(languageId);
    }

    synchronized public static Language getLanguageByExtension(String extension) {
	return languageExtensionMap.get(extension);
    }

    synchronized public static void reload() throws PersistenceException {
	languageMap.clear();
	languageExtensionMap.clear();
	for (Language language : languageDAO.getAllLanguages()) {
	    languageMap.put(language.getId(), language);
	    languageExtensionMap.put(language.getOptions(), language);
	}
    }
}
