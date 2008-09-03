package cn.edu.zju.acm.onlinejudge.judgeservice.submissiontest;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;

public class LanguageTest implements Test {
    private Set<Long> supportedLanguageIds = new HashSet<Long>();

    public LanguageTest(Collection<Language> supportedLanguages) {
        for (Language language : supportedLanguages) {
            this.supportedLanguageIds.add(language.getId());
        }
    }

    @Override
    public boolean test(Submission submission, int priority) {
        return supportedLanguageIds.contains(submission.getLanguage().getId());
    }
}
