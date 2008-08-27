package cn.edu.zju.acm.onlinejudge.judgeservice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;
import cn.edu.zju.acm.onlinejudge.dao.LanguageDAO;

public class MockLanguageDAO extends MockDAO implements LanguageDAO {
    private List<Language> languages = Collections.synchronizedList(new ArrayList<Language>());

    public MockLanguageDAO() {
        Language language = new Language(0, "cc", "cc", "cc", "cc");
        save(language);
    }

    public List<Language> getAllLanguages() {
        return new ArrayList<Language>(languages);
    }

    public void save(Language language) {
        languages.add(language);
    }
}