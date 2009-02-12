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

package cn.edu.zju.acm.onlinejudge.judgeservice;

import java.util.HashMap;
import java.util.Map;

import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;
import cn.edu.zju.acm.onlinejudge.persistence.LanguagePersistence;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;

// TODO(xuchuan): Remove this class. Use LanguagePersistence directly
public class LanguageManager {
    private static Map<Long, Language> languageMap = new HashMap<Long, Language>();

    private static Map<String, Language> languageExtensionMap = new HashMap<String, Language>();

    private static LanguagePersistence languageDAO = PersistenceManager.getInstance().getLanguagePersistence();

    static {
        try {
            LanguageManager.reload();
        } catch (PersistenceException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    synchronized public static Language getLanguage(long languageId) {
        return LanguageManager.languageMap.get(languageId);
    }

    synchronized public static Language getLanguageByExtension(String extension) {
        return LanguageManager.languageExtensionMap.get(extension);
    }

    synchronized public static int getNumberOfLanguages() {
        return LanguageManager.languageMap.size();
    }

    synchronized public static void reload() throws PersistenceException {
        LanguageManager.languageMap.clear();
        LanguageManager.languageExtensionMap.clear();
        for (Language language : LanguageManager.languageDAO.getAllLanguages()) {
            LanguageManager.languageMap.put(language.getId(), language);
            LanguageManager.languageExtensionMap.put(language.getOptions(), language);
        }
    }
}
