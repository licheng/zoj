package cn.edu.zju.acm.onlinejudge.dao;

public class DAOFactory {
    private static SubmissionDAO submissionDAO;

    private static ProblemDAO problemDAO;

    private static LanguageDAO languageDAO;
    
    private static ReferenceDAO referenceDAO;

    static {
	problemDAO = new ProblemDAOImpl();
	submissionDAO = new SubmissionDAOImpl();
	languageDAO = new LanguageDAOImpl();
	referenceDAO = new ReferenceDAOImpl();
    }

    public static SubmissionDAO getSubmissionDAO() {
	return submissionDAO;
    }

    public static ProblemDAO getProblemDAO() {
	return problemDAO;
    }

    public static LanguageDAO getLanguageDAO() {
	return languageDAO;
    }

    public static ReferenceDAO getReferenceDAO() {
        return referenceDAO;
    }
}
