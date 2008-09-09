/*
 * Copyright 2007 Xu, Chuan <xuchuan@gmail.com>
 * 
 * This file is part of ZOJ.
 * 
 * ZOJ is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 * 
 * ZOJ is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with ZOJ. if not, see
 * <http://www.gnu.org/licenses/>.
 */

package cn.edu.zju.acm.onlinejudge.judgeservice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;
import cn.edu.zju.acm.onlinejudge.persistence.ContestPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;

public class LanguageManager {
    private static Map<Long, Language> languageMap = new HashMap<Long, Language>();

    private static Map<String, Language> languageExtensionMap = new HashMap<String, Language>();

    private static ContestPersistence languageDAO = PersistenceManager.getInstance().getContestPersistence();

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

    synchronized public static int getNumberOfLanguages() {
        return languageMap.size();
    }

    synchronized public static void reload() throws PersistenceException {
        languageMap.clear();
        languageExtensionMap.clear();
        for (Language language : (List<Language>) languageDAO.getAllLanguages()) {
            languageMap.put(language.getId(), language);
            languageExtensionMap.put(language.getOptions(), language);
        }
    }
}
