package cn.edu.zju.acm.onlinejudge.judgeservice;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.CopyUtils;
import org.apache.log4j.Logger;

import cn.edu.zju.acm.onlinejudge.bean.Limit;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.Reference;
import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.ReferenceType;
import cn.edu.zju.acm.onlinejudge.dao.DAOFactory;
import cn.edu.zju.acm.onlinejudge.dao.ProblemDAO;
import cn.edu.zju.acm.onlinejudge.dao.SubmissionDAO;
import cn.edu.zju.acm.onlinejudge.judgeservice.submissiontest.LanguageTest;
import cn.edu.zju.acm.onlinejudge.judgeservice.submissiontest.NegationTest;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.util.Utility;

// TODO: add limit checks here
public class JudgeClientInstance extends Thread {
    public static enum Status {
        CONNECTING, WAITING, RUNNING, ERROR, STOPEED
    }

    private DataInputStream in;

    private DataOutputStream out;

    private ProblemDAO problemDAO = DAOFactory.getProblemDAO();

    private SubmissionDAO submissionDAO = DAOFactory.getSubmissionDAO();



    private Socket socket;

    private InetSocketAddress address;

    private Logger logger;

    private Exception error = null;

    private Submission submission = null;

    private JudgeQueue judgeQueue = null;

    private Status status = Status.CONNECTING;

    private JudgeService service;

    private JudgeClient client;

    private SubmissionFilter submissionFilter = null;

    private SubmissionFilter clientFilter = null;

    private SubmissionFilter serviceFilter = null;

    public JudgeClientInstance(JudgeService service, JudgeClient client) {
        this.service = service;
        this.client = client;
        this.address = new InetSocketAddress(client.getHost(), client.getPort());
        this.logger = Logger.getLogger(JudgeService.class.getName());
        this.submissionFilter = new SubmissionFilter();
        this.submissionFilter.add(new NegationTest(new LanguageTest(this.client.getSupportedLanguages())),
                Priority.DENY);
    }

    public Status getStatus() {
        return this.status;
    }

    public Exception getError() {
        return this.error;
    }

    public Submission getSubmission() {
        return this.submission;
    }

    public void setSubmissionFilter(SubmissionFilter submissionFilter) {
        submissionFilter.addFirst(new NegationTest(new LanguageTest(this.client.getSupportedLanguages())),
                Priority.DENY);
        this.submissionFilter = submissionFilter;
        this.judgeQueue = null;
    }

    @Override
    public void interrupt() {
        this.logger.info("interrrupted");
        super.interrupt();
        Utility.closeSocket(this.socket);
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            try {
                this.process();
            } catch (Exception e) {
                this.status = Status.ERROR;
                this.error = e;
                if (this.submission != null) {
                    this.service.judge(this.submission, Priority.HIGH);
                }
                try {
                    if (this.client.getErrorHandlingStrategy().onJudgeError(this, e) == JudgeClientErrorHandlingStrategy.Action.STOP) {
                        break;
                    }
                } catch (InterruptedException e1) {
                }
            }
        }
        this.status = Status.STOPEED;
    }

    private void process() throws IOException, InterruptedException, JudgeClientErrorException,
            JudgeServerErrorException {
        this.status = Status.CONNECTING;
        this.socket = new Socket();
        this.socket.setKeepAlive(true);
        this.socket.setSoTimeout(JudgeClient.READ_TIMEOUT);
        logger.info("Connecting to " + this.address.getAddress().getCanonicalHostName() + ":" + this.address.getPort());
        this.socket.connect(address, JudgeClient.CONNECTION_TIMEOUT);
        logger.info("Connected");
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        this.status = Status.RUNNING;
        try {

            while (!this.isInterrupted()) {

                try {
                    this.status = Status.WAITING;
                    if (this.serviceFilter != this.service.getSubmissionFilter() ||
                            this.clientFilter != this.client.getSubmissionFilter() || this.judgeQueue == null) {
                        this.serviceFilter = this.service.getSubmissionFilter();
                        this.clientFilter = this.client.getSubmissionFilter();
                        this.judgeQueue = this.service.createJudgeQueue(this.serviceFilter, this.clientFilter,
                                this.submissionFilter);
                    }
                    this.submission = this.judgeQueue.poll();
                    this.service.judgeStart(submission);
                    this.status = Status.RUNNING;
                    try {
                        this.judge(this.submission);
                    } catch (ProblemDataErrorException e) {
                        logger.error(e);
                        this.submission.setJudgeReply(JudgeReply.JUDGE_INTERNAL_ERROR);
                    }
                    this.commitSubmission();
                    this.submission.setContent(null);
                    // IMPORTANT: to avoid rejudging this one when queue.poll throws a PersistenceException
                    this.submission = null;
                    this.client.getErrorHandlingStrategy().onJudgeSuccess();
                } catch (PersistenceException e) {
                    throw new JudgeServerErrorException(e);
                } finally {
                    this.service.judgeDone(submission);
                }








            }


        } finally {
            Utility.closeSocket(this.socket);






        }
    }

    private void commitSubmission() throws PersistenceException {
        submissionDAO.beginTransaction();
        submissionDAO.update(this.submission, problemDAO.getProblem(this.submission.getProblemId()).getContestId());
        submissionDAO.commitTransaction();
    }

    private void judge(Submission submission) throws JudgeServerErrorException, IOException, PersistenceException,
            JudgeClientErrorException, ProblemDataErrorException {
        Problem problem = problemDAO.getProblem(submission.getProblemId());
        int reply = this.sendJudgeCommand(problem.getId(), problem.getRevision(), submission.getId());
        if (reply == JudgeReply.NO_SUCH_PROBLEM.getId()) {
            reply = this.sendDataCommand(problem);
        }
        if (reply == JudgeReply.COMPILATION_ERROR.getId()) {
            throw new ProblemDataErrorException("Special judge compilation failure for problem " + problem.getId());
        }
        if (reply != JudgeReply.READY.getId()) {
            throw new JudgeClientErrorException();
        }
        reply = this.sendCompileCommand(submission.getId(), submission.getLanguage(), submission.getContent());

        if (reply != JudgeReply.COMPILING.getId()) {
            throw new JudgeClientErrorException();
        }
        submission.setJudgeReply(JudgeReply.COMPILING);
        reply = this.readJudgeReply();

        if (reply == JudgeReply.COMPILATION_ERROR.getId()) {
            submission.setJudgeReply(JudgeReply.COMPILATION_ERROR);
            int length = this.in.readInt();
            byte[] bytes = new byte[length];
            this.in.read(bytes);
            submission.setJudgeComment(new String(bytes));
            return;
        } else if (reply != JudgeReply.READY.getId()) {
            throw new JudgeClientErrorException();
        }
        Limit limit = problem.getLimit();
        reply = this.sendTestcaseCommand(1, limit.getTimeLimit(), limit.getMemoryLimit(), limit.getOutputLimit());
        if (reply != JudgeReply.RUNNING.getId()) {
            throw new JudgeClientErrorException();
        }
        submission.setJudgeReply(JudgeReply.RUNNING);
        while (reply == JudgeReply.RUNNING.getId()) {
            int timeConsumption = this.in.readInt();
            int memoryConsumption = this.in.readInt();
            submission.setTimeConsumption(timeConsumption);
            submission.setMemoryConsumption(memoryConsumption);
            logger.info("Running " + timeConsumption + " " + memoryConsumption);
            reply = this.readJudgeReply();
        }
        if (reply == JudgeReply.JUDGING.getId()) {
            submission.setJudgeReply(JudgeReply.JUDGING);
            reply = this.readJudgeReply();

        }
        if (reply == JudgeReply.JUDGE_INTERNAL_ERROR.getId()) {
            throw new JudgeClientErrorException();
        }
        submission.setJudgeReply(JudgeReply.findById(reply));
        if (submission.getJudgeReply() == null || submission.getJudgeReply() != JudgeReply.TIME_LIMIT_EXCEEDED &&
                submission.getJudgeReply() != JudgeReply.MEMORY_LIMIT_EXCEEDED &&
                submission.getJudgeReply() != JudgeReply.OUTPUT_LIMIT_EXCEEDED &&
                submission.getJudgeReply() != JudgeReply.FLOATING_POINT_ERROR &&
                submission.getJudgeReply() != JudgeReply.SEGMENTATION_FAULT &&
                submission.getJudgeReply() != JudgeReply.RUNTIME_ERROR &&
                submission.getJudgeReply() != JudgeReply.ACCEPTED &&
                submission.getJudgeReply() != JudgeReply.WRONG_ANSWER &&
                submission.getJudgeReply() != JudgeReply.PRESENTATION_ERROR) {
            throw new JudgeClientErrorException();
        }
    }

    private int readJudgeReply() throws IOException {
        int reply = this.in.readInt();
        JudgeReply r = JudgeReply.findById(reply);
        if (r == null) {
            logger.error("Invalid judge reply " + reply);
        } else if (r != JudgeReply.RUNNING) {
            logger.info(r.getDescription());
        }
        return reply;
    }

    private int sendJudgeCommand(long problemId, int problemRevision, long submissionId) throws IOException {
        logger.info("Judge prob:" + problemId + " rev:" + problemRevision + " sub:" + submissionId);
        this.sendCommand(JudgeClientCommandsFactory.createJudgeCommand(problemId, problemRevision, submissionId));
        return this.readJudgeReply();













    }

    private void zipProblemData(File outputFile, Problem problem) throws PersistenceException,
            JudgeServerErrorException, ProblemDataErrorException {

        try {
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(outputFile));
            try {
                List<Reference> inputFiles = DAOFactory.getReferenceDAO().getProblemReferences(problem.getId(),
                        ReferenceType.INPUT);
                List<Reference> outputFiles = DAOFactory.getReferenceDAO().getProblemReferences(problem.getId(),
                        ReferenceType.OUTPUT);
                if (inputFiles.size() != outputFiles.size() && inputFiles.size() > 0 && outputFiles.size() > 0) {
                    throw new ProblemDataErrorException("Unequal number of inputs and outputs for problem " +
                            problem.getId());
                }
                for (Reference input : inputFiles) {
                    if (input.getContent() == null) {
                        throw new ProblemDataErrorException("Can not find content for input with reference id " +
                                input.getId());
                    }
                }
                for (Reference output : outputFiles) {
                    if (output.getContent() == null) {
                        throw new ProblemDataErrorException("Can not find content for output with reference id " +
                                output.getId());
                    }
                }
                Reference specialJudge = null;
                if (problem.isChecker()) {
                    List<Reference> specialJudges = DAOFactory.getReferenceDAO().getProblemReferences(problem.getId(),
                            ReferenceType.CHECKER_SOURCE);
                    if (specialJudges.size() == 0) {
                        throw new ProblemDataErrorException("Can not find special judge for problem " + problem.getId());
                    }
                    if (specialJudges.size() > 1) {
                        throw new ProblemDataErrorException("Find more than one special judge for problem " +
                                problem.getId());
                    }
                    specialJudge = specialJudges.get(0);
                    String contentType = specialJudge.getContentType();
                    if (contentType == null) {
                        throw new ProblemDataErrorException(
                                "Can not find source content type for special judge with reference id " +
                                        specialJudge.getId());
                    }
                    byte[] content = specialJudge.getContent();
                    if (content == null) {
                        throw new ProblemDataErrorException(
                                "Can not find source content for special judge with reference id " +
                                        specialJudge.getId());
                    }
                    if (content.length == 0) {
                        throw new ProblemDataErrorException("Empty source for special judge with reference id " +
                                specialJudge.getId());
                    }
                }
                for (int i = 0; i < inputFiles.size(); i++) {
                    zipOut.putNextEntry(new ZipEntry(String.format("%d.in", i + 1)));
                    CopyUtils.copy(inputFiles.get(i).getContent(), zipOut);
                }
                for (int i = 0; i < outputFiles.size(); i++) {
                    zipOut.putNextEntry(new ZipEntry(String.format("%d.out", i + 1)));
                    CopyUtils.copy(outputFiles.get(i).getContent(), zipOut);
                }

                if (specialJudge != null) {
                    zipOut.putNextEntry(new ZipEntry(String.format("judge.%s", specialJudge.getContentType())));
                    CopyUtils.copy(specialJudge.getContent(), zipOut);
                }
            } finally {
                zipOut.close();
            }
        } catch (IOException e) {
            throw new JudgeServerErrorException("Fail to zip problem data", e);
        }
    }

    private int sendDataCommand(Problem problem) throws PersistenceException, JudgeServerErrorException, IOException,
            ProblemDataErrorException {
        File tempFile;
        try {
            tempFile = File.createTempFile("prob", null);
        } catch (IOException e) {
            throw new JudgeServerErrorException("Can not create temporary file", e);
        }
        try {
            this.zipProblemData(tempFile, problem);
            FileInputStream fin;
            try {
                fin = new FileInputStream(tempFile);
            } catch (FileNotFoundException e) {
                throw new JudgeServerErrorException("Can not find temporary file " + tempFile.getAbsolutePath(), e);
            }
            logger.info("Data size:" + tempFile.length());
            this.sendCommand(JudgeClientCommandsFactory.createDataCommand((int) tempFile.length()));
            int reply = this.readJudgeReply();
            if (reply == JudgeReply.READY.getId()) {
                try {
                    CopyUtils.copy(fin, out);
                } finally {
                    fin.close();
                }
                reply = this.readJudgeReply();
                if (reply == JudgeReply.COMPILING.getId()) {
                    reply = this.readJudgeReply();
                }
            }
            return reply;
        } finally {
            tempFile.delete();
        }
    }

    private void sendCommand(byte[] bytes) throws IOException {
        this.out.write(bytes);
        this.out.flush();
    }

    private int sendCompileCommand(long submissionId, Language language, String sourceFile)
            throws JudgeServerErrorException, IOException, ProblemDataErrorException {
        this.logger.info("Compile " + submissionId + "." + language.getOptions());
        byte[] bytes = sourceFile.getBytes();
        this.logger.info("Compiler:" + language.getId() + " source size:" + bytes.length);
        this.sendCommand(JudgeClientCommandsFactory.createCompileCommand((int) language.getId(), bytes.length));
        int reply = this.readJudgeReply();
        if (reply == JudgeReply.READY.getId()) {
            this.out.write(bytes);
            this.out.flush();
            reply = this.readJudgeReply();
        }
        return reply;
    }

    private int sendTestcaseCommand(int testcase, int timeLimit, int memoryLimit, int outputLimit) throws IOException {
        this.logger.info("Testcase:" + testcase + " TL:" + timeLimit + " ML:" + memoryLimit + " OL:" + outputLimit);
        this.sendCommand(JudgeClientCommandsFactory
                .createTestCaseCommand(testcase, timeLimit, memoryLimit, outputLimit));
        return this.readJudgeReply();
    }
}
