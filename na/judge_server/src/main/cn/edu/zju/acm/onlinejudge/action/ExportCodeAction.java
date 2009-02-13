package cn.edu.zju.acm.onlinejudge.action;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.Reference;
import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.ReferenceType;
import cn.edu.zju.acm.onlinejudge.bean.request.ProblemCriteria;
import cn.edu.zju.acm.onlinejudge.bean.request.SubmissionCriteria;
import cn.edu.zju.acm.onlinejudge.persistence.ContestPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.ProblemPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.ReferencePersistence;
import cn.edu.zju.acm.onlinejudge.persistence.UserPersistence;
import cn.edu.zju.acm.onlinejudge.security.UserSecurity;
import cn.edu.zju.acm.onlinejudge.util.ContestManager;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;
import cn.edu.zju.acm.onlinejudge.util.RankListEntry;
import cn.edu.zju.acm.onlinejudge.util.StatisticsManager;
import cn.edu.zju.acm.onlinejudge.util.Utility;
import cn.edu.zju.acm.onlinejudge.util.ContestStatistics;

public class ExportCodeAction  extends BaseAction {
	/**
     * <p>
     * Default constructor.
     * </p>
     */
    public ExportCodeAction() {
        // empty
    }

    /**
     * ExportProblemsAction.
     *
     * @param mapping action mapping
     * @param form action form
     * @param request http sevelet request
     * @param response http sevelet response
     *
     * @return action forward instance
     *
     * @throws Exception any errors happened
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, ContextAdapter context) throws Exception {

        SubmissionCriteria criteria = new SubmissionCriteria();
        ArrayList judgeReplies = new ArrayList();
    	judgeReplies.add(JudgeReply.ACCEPTED);
        criteria.setJudgeReplies(judgeReplies);
        List runs = StatisticsManager.getInstance().getSubmissions(criteria, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
        
        HttpServletResponse response = context.getResponse();
        response.setContentType("application/zip");        
        response.setHeader("Content-disposition", 
                    "attachment; filename=StudentCode.zip");

        ZipOutputStream out = null;
        StringBuilder sb = new StringBuilder();
        try {
            out = new ZipOutputStream(response.getOutputStream());
            for (Object obj : runs) {
            	Submission s = (Submission) obj;
            	out.putNextEntry(new ZipEntry(s.getProblemCode() + "/" + s.getUserName() + "/" + s.getId()));
            	byte[] data = s.getContent().getBytes();
            	out.write(data);
            	out.closeEntry();                               
            }
            
        } catch (IOException e) {            
            throw e;
        } finally {
            if (out != null) {
                out.close();
            }
        }
               
        return null;
                          	    	   
    }
}
