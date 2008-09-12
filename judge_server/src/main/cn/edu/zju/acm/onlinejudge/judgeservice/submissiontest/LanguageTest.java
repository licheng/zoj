/*
 * Copyright 2007 Xu, Chuan <xuchuan@gmail.com>
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
        return this.supportedLanguageIds.contains(submission.getLanguage().getId());
    }

    @Override
    public int hashCode() {
        return this.supportedLanguageIds.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LanguageTest) {
            return this.supportedLanguageIds.equals(((LanguageTest) obj).supportedLanguageIds);
        }
        return false;
    }
}
