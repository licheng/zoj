/*
 * Copyright 2007 Zhang, Zheng <oldbig@gmail.com>
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

package cn.edu.zju.acm.onlinejudge.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.CopyUtils;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import cn.edu.zju.acm.onlinejudge.bean.Limit;
import cn.edu.zju.acm.onlinejudge.bean.Problem;

public class ProblemManager {

    public static final String PROBLEM_CSV_FILE = "problems.csv";

    public static final String INPUT_FILE = "input";

    public static final String OUTPUT_FILE = "output";

    public static final String PROBLEM_TEXT_FILE = "text";

    public static final String CHECKER_FILE = "checker";

    public static final String CHECKER_SOURCE_FILE = "checker";

    public static final String JUDGE_SOLUTION_FILE = "solution";

    public static final String IMAGES_DIR = "images";

    public static ProblemPackage importProblem(InputStream in, ActionMessages messages) {

        Map<String, byte[]> files = new HashMap<String, byte[]>();
        ZipInputStream zis = null;
        try {

            zis = new ZipInputStream(new BufferedInputStream(in));
            // zis = new ZipInputStream(new BufferedInputStream(new FileInputStream("d:/100.zip")));
            ZipEntry entry = zis.getNextEntry();
            while (entry != null) {
                if (!entry.isDirectory()) {

                    // TODO the file may be too big. > 100M
                    /*
                     * byte data[] = new byte[(int) entry.getSize()]; int l = 0; while (l < data.length) { int ll =
                     * zis.read(data, l, data.length - l); if (ll < 0) { break; } l += ll; }
                     */
                    // System.out.println("zip ******* " + l);
                    ByteArrayOutputStream buf = new ByteArrayOutputStream();
                    CopyUtils.copy(zis, buf);
                    files.put(entry.getName(), buf.toByteArray());

                }
                entry = zis.getNextEntry();
            }

        } catch (IOException ioe) {
            messages.add("error", new ActionMessage("onlinejudge.importProblem.invalidzip"));
            return null;
        } finally {
            try {
                if (zis != null) {
                    zis.close();
                }
            } catch (IOException e) {
                // ignore
            }
        }
        /*
         * files.clear(); files.put("problems.csv", "3,a\n2,b,true,1,2,3,4,a,b,c\n1,c".getBytes());
         * files.put("1\\checker", "checker".getBytes()); files.put("1\\input", "input".getBytes());
         * files.put("1\\output", "output".getBytes()); files.put("3\\checker", "checker3".getBytes());
         * files.put("3\\input", "input3".getBytes()); files.put("3\\output", "output3".getBytes());
         * files.put("images\\1.jpg", "1".getBytes()); files.put("images\\2\\2.jpg", "2".getBytes());
         */
        if (!files.containsKey(ProblemManager.PROBLEM_CSV_FILE)) {
            messages.add("error", new ActionMessage("onlinejudge.importProblem.missingproblemscsv"));
            return null;
        }
        ProblemPackage problemPackage = ProblemManager.parse(files, messages);
        if (messages.size() > 0) {
            return null;
        }
        return problemPackage;
    }

    private static ProblemPackage parse(Map<String, byte[]> files, ActionMessages messages) {
        Map<String, ProblemEntry> entryMap = new TreeMap<String, ProblemEntry>();
        byte[] csv = files.get(ProblemManager.PROBLEM_CSV_FILE);

        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(csv)));
        int index = 0;
        try {
            for (;;) {
                index++;
                String messageKey = "Line " + index;
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                if (line.trim().length() == 0) {
                    continue;
                }
                // CSV format code,title,checker,tl,ml,ol,sl,author,source,contest
                String[] values = ProblemManager.split(line);
                Problem problem = new Problem();
                if (values.length < 2) {
                    messages.add(messageKey, new ActionMessage("onlinejudge.importProblem.invalidline"));
                    continue;
                }
                if (values[0].length() > 8 || values[0].length() == 0) {
                    messages.add(messageKey, new ActionMessage("onlinejudge.importProblem.invalidcode"));
                }

                problem.setCode(values[0]);

                if (values[1].length() > 128 || values[1].length() == 0) {
                    messages.add(messageKey, new ActionMessage("onlinejudge.importProblem.invalidtitle"));
                }
                problem.setTitle(values[1]);

                if (values.length > 2 && Boolean.valueOf(values[2]).booleanValue()) {
                    problem.setChecker(true);
                }

                Limit limit = new Limit();

                Integer tl = ProblemManager.retrieveInt(values, 3);
                if (tl != null) {
                    if (tl.intValue() < 0) {
                        messages.add(messageKey, new ActionMessage("onlinejudge.importProblem.invalidtl"));
                    } else {
                        limit.setTimeLimit(tl.intValue());
                    }
                }
                Integer ml = ProblemManager.retrieveInt(values, 4);
                if (ml != null) {
                    if (ml.intValue() < 0) {
                        messages.add(messageKey, new ActionMessage("onlinejudge.importProblem.invalidml"));
                    } else {
                        limit.setMemoryLimit(ml.intValue());
                    }
                }
                Integer ol = ProblemManager.retrieveInt(values, 5);
                if (ol != null) {
                    if (ol.intValue() < 0) {
                        messages.add(messageKey, new ActionMessage("onlinejudge.importProblem.invalidol"));
                    } else {
                        limit.setOutputLimit(ol.intValue());
                    }
                }
                Integer sl = ProblemManager.retrieveInt(values, 6);
                if (sl != null) {
                    if (sl.intValue() < 0) {
                        messages.add(messageKey, new ActionMessage("onlinejudge.importProblem.invalidsl"));
                    } else {
                        limit.setSubmissionLimit(sl.intValue());
                    }
                }
                if (tl != null || ml != null || ol != null || sl != null) {
                    if (tl != null && ml != null && ol != null && sl != null) {
                        problem.setLimit(limit);
                    } else {
                        messages.add(messageKey, new ActionMessage("onlinejudge.importProblem.missinglimit"));
                    }
                }

                if (values.length > 7 && values[7].length() > 0) {
                    if (values[7].length() > 32) {
                        messages.add(messageKey, new ActionMessage("onlinejudge.importProblem.invalidauthor"));
                    } else {
                        problem.setAuthor(values[7]);
                    }
                }

                if (values.length > 8 && values[8].length() > 0) {
                    if (values[8].length() > 128) {
                        messages.add(messageKey, new ActionMessage("onlinejudge.importProblem.invalidsource"));
                    } else {
                        problem.setSource(values[8]);
                    }
                }

                if (values.length > 9 && values[9].length() > 0) {
                    if (values[9].length() > 128) {
                        messages.add(messageKey, new ActionMessage("onlinejudge.importProblem.invalidcontest"));
                    } else {
                        problem.setContest(values[9]);
                    }
                }
                ProblemEntry entry = new ProblemEntry();
                entry.setProblem(problem);
                if (entryMap.containsKey(problem.getCode())) {
                    messages.add(messageKey, new ActionMessage("onlinejudge.importProblem.reduplicatecode"));
                }
                entryMap.put(problem.getCode(), entry);
            }
        } catch (IOException e) {
            messages.add("error", new ActionMessage("onlinejudge.importProblem.invalidproblemscsv"));
        }
        if (messages.size() > 0) {
            return null;
        }
        if (entryMap.size() == 0) {
            messages.add("error", new ActionMessage("onlinejudge.importProblem.emptyproblemscsv"));
        }

        ProblemPackage problemPackage = new ProblemPackage();
        problemPackage.setProblemEntries(new ProblemEntry[entryMap.size()]);

        // retrieve checker, input, output
        index = 0;
        for (String string : entryMap.keySet()) {
            ProblemEntry entry = entryMap.get(string);
            String code = entry.getProblem().getCode();
            byte[] checker = files.get(code + "/" + ProblemManager.CHECKER_FILE);
            byte[] input = files.get(code + "/" + ProblemManager.INPUT_FILE);
            byte[] output = files.get(code + "/" + ProblemManager.OUTPUT_FILE);
            byte[] text = files.get(code + "/" + ProblemManager.PROBLEM_TEXT_FILE);
            byte[] solution = null;
            byte[] checkerSource = null;
            String checkerSourceType = ProblemManager.getFileType(code, ProblemManager.CHECKER_SOURCE_FILE, files);
            if (checkerSourceType != null) {
                checkerSource = files.get(code + "/" + ProblemManager.CHECKER_SOURCE_FILE + "." + checkerSourceType);
            }

            String solutionType = ProblemManager.getFileType(code, ProblemManager.JUDGE_SOLUTION_FILE, files);
            if (solutionType != null) {
                solution = files.get(code + "/" + ProblemManager.JUDGE_SOLUTION_FILE + "." + solutionType);
            }
            if ("cpp".equals(checkerSourceType)) {
                checkerSourceType = "cc";
            }
            if ("cpp".equals(solutionType)) {
                solutionType = "cc";
            }

            entry.setChecker(checker);
            entry.setInput(input);
            entry.setOutput(output);
            entry.setText(text);
            entry.setSolution(solution);
            entry.setSolutionType(solutionType);
            entry.setCheckerSource(checkerSource);
            entry.setCheckerSourceType(checkerSourceType);

            problemPackage.getProblemEntries()[index] = entry;
            index++;
        }
        // retrieve images
        Map<String, byte[]> imageMap = new HashMap<String, byte[]>();
        Map<String, String> usedImages = new HashMap<String, String>();
        Map<String, String> duplicateImages = new HashMap<String, String>();
        for (String string : files.keySet()) {
            String path = string;
            if (ProblemManager.isImageFile(path)) {
                String imageName = new File(path).getName();

                if (imageMap.containsKey(imageName)) {
                    String s = duplicateImages.get(imageName);
                    s = (s == null ? "" : s) + " " + path;
                    duplicateImages.put(imageName, s);
                }
                if (ProblemManager.isUsed(imageName)) {
                    String s = usedImages.get(imageName);
                    s = (s == null ? "" : s) + " " + path;
                    usedImages.put(imageName, s);
                }
                imageMap.put(imageName, files.get(path));

            }
        }
        problemPackage.setImages(imageMap);
        problemPackage.setUsedImages(usedImages);
        problemPackage.setDuplicateImages(duplicateImages);
        return problemPackage;
    }

    private static boolean isImageFile(String path) {
        String name = path.toLowerCase();
        return name.endsWith(".bmp") || path.endsWith(".gif") || path.endsWith(".tif") || path.endsWith(".png") ||
            path.endsWith(".jpg") || path.endsWith(".jpeg");
    }

    private static boolean isUsed(String image) {
        File f = new File(ConfigManager.getImagePath(), image);
        return f.exists();
    }

    private static String getFileType(String code, String name, Map<String, byte[]> files) {
        if (files.containsKey(code + "/" + name + ".cc")) {
            return "cc";
        } else if (files.containsKey(code + "/" + name + ".cpp")) {
            return "cpp";
        } else if (files.containsKey(code + "/" + name + ".c")) {
            return "c";
        } else if (files.containsKey(code + "/" + name + ".pas")) {
            return "pas";
        } else if (files.containsKey(code + "/" + name + ".fpc")) {
            return "fpc";
        } else if (files.containsKey(code + "/" + name + ".java")) {
            return "java";
        } else {
            return null;
        }

    }

    private static Integer retrieveInt(String[] values, int index) {
        if (values.length > index && values[index].length() > 0) {
            try {
                return Integer.valueOf(values[index]);
            } catch (Exception e) {
                return new Integer(-1);
            }
        }
        return null;
    }

    private static String[] split(String line) {
        int quote = 0;
        StringBuffer sb = new StringBuffer();
        List<String> values = new ArrayList<String>();
        for (int i = 0; i < line.length(); ++i) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (quote == 0 && sb.length() == 0) {
                    quote = 1;
                    continue;
                }
                if (quote == 0) {
                    sb.append(ch);
                    continue;
                }

                if (quote == 2) {
                    sb.append(ch);
                    quote--;
                } else {
                    quote++;
                }
            } else if (quote == 1) {
                sb.append(ch);
            } else if (ch == ',') {
                values.add(sb.toString().trim());
                sb = new StringBuffer();
                quote = 0;
            } else {
                sb.append(ch);
            }

        }
        values.add(sb.toString().trim());
        return values.toArray(new String[0]);
    }
}
