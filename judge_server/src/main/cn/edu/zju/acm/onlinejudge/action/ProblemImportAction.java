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

package cn.edu.zju.acm.onlinejudge.action;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.CopyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Limit;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.Reference;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.ReferenceType;
import cn.edu.zju.acm.onlinejudge.form.ProblemImportForm;
import cn.edu.zju.acm.onlinejudge.persistence.ProblemPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.ReferencePersistence;
import cn.edu.zju.acm.onlinejudge.util.ConfigManager;
import cn.edu.zju.acm.onlinejudge.util.ContestManager;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;
import cn.edu.zju.acm.onlinejudge.util.ProblemEntry;
import cn.edu.zju.acm.onlinejudge.util.ProblemManager;
import cn.edu.zju.acm.onlinejudge.util.ProblemPackage;

/**
 * <p>
 * ProblemImportAction
 * </p>
 * 
 * 
 * @author Zhang, Zheng
 * @version 2.0
 */
public class ProblemImportAction extends BaseAction {

    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public ProblemImportAction() {
    // empty
    }

    /**
     * ProblemImportAction.
     * 
     * @param mapping
     *            action mapping
     * @param form
     *            action form
     * @param request
     *            http servlet request
     * @param response
     *            http servlet response
     * 
     * @return action forward instance
     * 
     * @throws Exception
     *             any errors happened
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, ContextAdapter context) throws Exception {

        // check contest
        boolean isProblemset = context.getRequest().getRequestURI().endsWith("importProblems.do");

        ActionForward forward = this.checkContestAdminPermission(mapping, context, isProblemset, false);
        if (forward != null) {
            return forward;
        }

        ProblemPackage pack = (ProblemPackage) context.getSessionAttribute("ProblemPackage");
        context.setSessionAttribute("ProblemPackage", null);

        AbstractContest contest = context.getContest();

        InputStream in = null;
        String filePath = context.getRequest().getParameter("problemFilePath");
        FormFile file = ((ProblemImportForm) form).getProblemFile();

        if (filePath != null && filePath.trim().length() > 0) {
            in = new FileInputStream(filePath);
        } else if (file != null) {
            in = file.getInputStream();
        }
        if (in != null) {
            ActionMessages messages = new ActionMessages();
            pack = ProblemManager.importProblem(in, messages);
            // seePackage(pack, messages);

            if (messages.size() > 0) {
                return this.handleFailure(mapping, context, messages);
            }
            context.setSessionAttribute("ProblemPackage", pack);

            return this.handleSuccess(mapping, context, "preview");
        }

        if (pack == null) {
            return this.handleSuccess(mapping, context, "selectcontest");
        }

        try {

            ProblemImportAction.createProblems(pack, contest.getId());
            /*
             * ProblemCriteria criteria = new ProblemCriteria(); criteria.setContestId(new Long(contest.getId()));
             * 
             * // remove List oldProblems = problemPersistence.searchProblems(criteria); for (Iterator it =
             * oldProblems.iterator(); it.hasNext();) { problemPersistence.deleteProblem(((Problem) it.next()).getId(),
             * 0); }
             */
            ContestManager.getInstance().refreshContest(contest.getId());
        } catch (Exception pe) {
            this.error(pe);
            ActionMessages messages = new ActionMessages();
            messages.add("message", new ActionMessage("onlinejudge.importProblems.failure"));
            this.saveErrors(context.getRequest(), messages);
            context.setAttribute("back", "editContest.do?contestId=" + contest.getId());
            return this.handleSuccess(mapping, context, "success");
        }

        ActionMessages messages = new ActionMessages();
        messages.add("message", new ActionMessage("onlinejudge.importProblems.success"));
        this.saveErrors(context.getRequest(), messages);
        context.setAttribute("back", "showContestProblems.do?contestId=" + contest.getId());

        return this.handleSuccess(mapping, context, "success");

    }

    private static void createProblems(ProblemPackage pack, long cid) throws Exception {
        ProblemPersistence problemPersistence = PersistenceManager.getInstance().getProblemPersistence();

        ProblemEntry[] problems = pack.getProblemEntries();
        for (int i = 0; i < problems.length; ++i) {
            // info
            problems[i].getProblem().setContestId(cid);
            problemPersistence.createProblem(problems[i].getProblem(), 0);

            long problemId = problems[i].getProblem().getId();
            ProblemImportAction.createReference(ReferenceType.DESCRIPTION, problems[i].getText(), problemId, 0,
                                                ProblemManager.PROBLEM_TEXT_FILE, null);
            ProblemImportAction.createReference(ReferenceType.INPUT, problems[i].getInput(), problemId, 0,
                                                ProblemManager.INPUT_FILE, null);
            ProblemImportAction.createReference(ReferenceType.OUTPUT, problems[i].getOutput(), problemId, 0,
                                                ProblemManager.OUTPUT_FILE, null);
            ProblemImportAction.createReference(ReferenceType.CHECKER, problems[i].getChecker(), problemId, 0,
                                                ProblemManager.CHECKER_FILE, null);
            ProblemImportAction.createReference(ReferenceType.CHECKER_SOURCE, problems[i].getCheckerSource(),
                                                problemId, 0, ProblemManager.CHECKER_SOURCE_FILE,
                                                problems[i].getCheckerSourceType());

            ProblemImportAction.createReference(ReferenceType.JUDGE_SOLUTION, problems[i].getSolution(), problemId, 0,
                                                ProblemManager.JUDGE_SOLUTION_FILE, problems[i].getSolutionType());

            // images
        }

        Map<String, byte[]> images = pack.getImages();
        String imagesPath = ConfigManager.getImagePath();
        for (String string : images.keySet()) {
            String name = string;
            String path = imagesPath + "/" + name;
            byte[] image = images.get(name);
            FileOutputStream out = new FileOutputStream(path);
            try {
                CopyUtils.copy(new ByteArrayInputStream(image), out);
            } catch (Exception e) {
                out.close();
                throw e;
            }

        }

    }

    private static void createReference(ReferenceType type, byte[] data, long problemId, long user, String fileName,
                                        String fileType) throws Exception {
        if (data == null) {
            return;
        }

        ReferencePersistence referencePersistence = PersistenceManager.getInstance().getReferencePersistence();
        if (fileType != null && fileType.trim().length() > 0) {
            fileName = fileName + "." + fileType;
        }

        Reference ref = new Reference();
        ref.setName(fileName);
        ref.setContentType(fileType);
        ref.setContent(data);
        ref.setReferenceType(type);
        ref.setSize(data.length);

        referencePersistence.createProblemReference(problemId, ref, user);
    }

    private static void seePackage(ProblemPackage p, ActionMessages m) {
        if (m.size() > 0) {
            for (Iterator it = m.properties(); it.hasNext();) {
                String key = (String) it.next();
                if (key.startsWith("Line ")) {
                    for (Iterator it2 = m.get(key); it2.hasNext();) {
                        ActionMessage o = (ActionMessage) it2.next();
                        String line = key + " - " + o.getKey();
                        System.out.println(line);
                    }
                } else {
                    System.out.println(key);
                }
            }
            return;
        }
        ProblemEntry[] e = p.getProblemEntries();
        for (int i = 0; i < e.length; ++i) {
            Problem pp = e[i].getProblem();
            System.out.println(pp.getCode() + ", " + pp.getTitle() + ", " + pp.isChecker());
            Limit l = pp.getLimit();
            if (l != null) {
                System.out.println(l.getTimeLimit() + ", " + l.getMemoryLimit() + ", " + l.getOutputLimit() + ", " +
                    l.getSubmissionLimit());
            }
            System.out.println(pp.getAuthor() + ", " + pp.getSource() + ", " + pp.getContest());
            /*
             * System.out.println("checker - " + (e[i].getChecker() == null ? "null" : new String(e[i].getChecker())));
             * System.out.println("input - " + (e[i].getInput() == null ? "null" : new String(e[i].getInput())));
             * System.out.println("output - " + (e[i].getOutput() == null ? "null" : new String(e[i].getOutput())));
             */
        }
        System.out.println("-=Images=-");
        for (Iterator<String> it = p.getImages().keySet().iterator(); it.hasNext();) {
            Object key = it.next();
            System.out.println(key + ": " + new String(p.getImages().get(key)));
        }
        System.out.println("-----------------");
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("ProblemImportAction cid file_path");
            return;
        }
        long cid = Long.parseLong(args[0]);
        String path = args[1];
        ActionMessages messages = new ActionMessages();
        ProblemPackage pack = ProblemManager.importProblem(new FileInputStream(path), messages);
        if (messages.size() == 0) {
            ProblemImportAction.createProblems(pack, cid);
        } else {
            ProblemImportAction.seePackage(pack, messages);
        }

        System.out.println("success");
    }

}
