package cn.edu.zju.acm.onlinejudge.judgeserver;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
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
import cn.edu.zju.acm.onlinejudge.bean.enumeration.ReferenceType;
import cn.edu.zju.acm.onlinejudge.dao.DAOFactory;
import cn.edu.zju.acm.onlinejudge.dao.ProblemDAO;
import cn.edu.zju.acm.onlinejudge.dao.SubmissionDAO;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;

public class JudgeClientInstance extends Thread {
    private DataInputStream in;

    private DataOutputStream out;

    private ProblemDAO problemDAO = DAOFactory.getProblemDAO();

    private SubmissionDAO submissionDAO = DAOFactory.getSubmissionDAO();

    private JudgeQueue queue;

    private Socket socket;

    private InetSocketAddress address;

    private boolean tired = false;

    private Logger logger;

    public JudgeClientInstance(JudgeQueue queue, InetSocketAddress address) throws IOException {
        this.queue = queue;
        this.address = address;
        logger = Logger.getLogger(JudgeService.class.getName());
    }

    public synchronized void rest() {
        this.tired = true;
    }

    public synchronized void wakeup() {
        this.tired = false;
        this.notify();
    }

    public void run() {
        try {
            this.socket = new Socket();
            this.socket.setKeepAlive(true);
            this.socket.setSoTimeout(JudgeClient.READ_TIMEOUT);
            logger.info("Connecting to " + this.address.getAddress().getCanonicalHostName() + ":"
                    + this.address.getPort());
            this.socket.connect(address, JudgeClient.CONNECTION_TIMEOUT);
            logger.info("Connected");
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            logger.info("Start testing");
            while (!this.isInterrupted()) {
                Submission submission;
                try {
                    submission = queue.poll();
                } catch (InterruptedException e) {
                    throw e;
                } catch (Exception e) {
                    continue;
                }
                try {
                    this.judge(submission);
                } catch (IOException e) {
                    queue.rejudge(submission);
                    throw e;
                } catch (JudgeServerErrorException e) {
                    e.printStackTrace();
                    submission.setJudgeReply(JudgeReply.JUDGE_INTERNAL_ERROR);
                }
                submissionDAO.beginTransaction();
                submissionDAO.update(submission, problemDAO.getProblem(submission.getProblemId()).getContestId());
                submissionDAO.commitTransaction();
                submission.setContent(null);
                if (submission.getJudgeReply() == JudgeReply.JUDGE_INTERNAL_ERROR) {
                    // queue.rejudge(submission);
                    break;
                }
                synchronized (this) {
                    while (this.tired) {
                        this.wait();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (!this.socket.isClosed()) {
                    this.socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void judge(Submission submission) throws JudgeServerErrorException, IOException, PersistenceException {
        logger.info("Problem " + submission.getId());
        Problem problem = problemDAO.getProblem(submission.getProblemId());
        int reply = this.sendRequest(submission.getLanguage().getOptions(), problem.getId(), problem.getRevision(),
                submission.getContent());
        logJudgeReply(reply);
        if (reply == JudgeReply.NO_SUCH_PROBLEM.getId()) {
            reply = sendProblem(problem);
            logJudgeReply(reply);
        }
        if (reply != JudgeReply.READY.getId()) {
            throw new JudgeServerErrorException();
        }
        reply = this.in.readByte();
        logJudgeReply(reply);
        if (reply != JudgeReply.COMPILING.getId()) {
            throw new JudgeServerErrorException();
        }
        submission.setJudgeReply(JudgeReply.COMPILING);
        reply = this.in.readByte();
        logJudgeReply(reply);
        if (reply == JudgeReply.COMPILATION_ERROR.getId()) {
            submission.setJudgeReply(JudgeReply.COMPILATION_ERROR);
            submission.setJudgeComment(in.readUTF());
            return;
        } else if (reply != JudgeReply.READY.getId()) {
            throw new JudgeServerErrorException();
        }
        Limit limit = problem.getLimit();
        this.sendTestcase(1, limit.getTimeLimit(), limit.getMemoryLimit(), limit.getOutputLimit());
        submission.setJudgeReply(JudgeReply.RUNNING);
        for (;;) {
            reply = this.in.readByte();
            if (reply != JudgeReply.RUNNING.getId()) {
                break;
            }
            int timeConsumption = this.in.readInt();
            int memoryConsumption = this.in.readInt();
            submission.setTimeConsumption(timeConsumption);
            submission.setMemoryConsumption(memoryConsumption);
            logger.info("Running " + timeConsumption + " " + memoryConsumption);
        }
        logJudgeReply(reply);
        if (reply == JudgeReply.JUDGING.getId()) {
            submission.setJudgeReply(JudgeReply.JUDGING);
            reply = this.in.readByte();
            logJudgeReply(reply);
        }
        if (reply == JudgeReply.JUDGE_INTERNAL_ERROR.getId()) {
            throw new JudgeServerErrorException();
        }
        this.sendTestcase(0, 0, 0, 0);
        submission.setJudgeReply(JudgeReply.findById(reply));
        if (submission.getJudgeReply() == null) {
            throw new JudgeServerErrorException();
        }
    }

    private void logJudgeReply(int reply) {
        JudgeReply r = JudgeReply.findById(reply);
        if (r == null) {
            logger.error("Invalid judge reply " + reply);
        } else {
            logger.info(r.getDescription());
        }
    }

    private int sendRequest(String sourceFileType, long problemId, int problemRevision, String sourceFile)
            throws JudgeServerErrorException, IOException {
        String sourceFileTypes[] = { "cc", "cpp", "pas", "c", "java", "cs" };
        for (int i = 0; i <= sourceFileTypes.length; ++i) {
            if (i == sourceFileTypes.length) {
                throw new JudgeServerErrorException();
            } else if (sourceFileType.equals(sourceFileTypes[i])) {
                this.out.writeByte(i + 1);
                break;
            }
        }
        this.out.writeInt((int) problemId);
        this.out.writeInt(problemRevision);
        this.out.writeUTF(sourceFile);
        this.out.flush();
        return this.in.readByte();

    }

    private int sendProblem(Problem problem) throws JudgeServerErrorException, PersistenceException, IOException {
        logger.info("Sending problem " + problem.getId());
        File tempFile = File.createTempFile("prob", null);
        try {
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(tempFile));
            List<Reference> inputFiles = DAOFactory.getReferenceDAO().getProblemReferences(problem.getId(),
                    ReferenceType.INPUT);
            for (int i = 0; i < inputFiles.size(); i++) {
                zipOut.putNextEntry(new ZipEntry(String.format("%d.in", i + 1)));
                CopyUtils.copy(inputFiles.get(i).getContent(), zipOut);
            }
            List<Reference> outputFiles = DAOFactory.getReferenceDAO().getProblemReferences(problem.getId(),
                    ReferenceType.OUTPUT);
            for (int i = 0; i < outputFiles.size(); i++) {
                zipOut.putNextEntry(new ZipEntry(String.format("%d.out", i + 1)));
                CopyUtils.copy(outputFiles.get(i).getContent(), zipOut);
            }
            if (problem.isChecker()) {
                List<Reference> specialJudges = DAOFactory.getReferenceDAO().getProblemReferences(problem.getId(),
                        ReferenceType.CHECKER_SOURCE);
                if (specialJudges.size() > 0) {
                    String contentType = "cc";
                    if (specialJudges.get(0).getContentType() != null) {
                        contentType = specialJudges.get(0).getContentType();
                    }
                    zipOut.putNextEntry(new ZipEntry(String.format("judge.%s", contentType)));
                    CopyUtils.copy(specialJudges.get(0).getContent(), zipOut);
                }
            }
            zipOut.close();

            FileInputStream fin = new FileInputStream(tempFile);
            this.out.writeInt((int) tempFile.length());
            try {
                CopyUtils.copy(fin, out);
            } finally {
                fin.close();
            }
            int reply = in.readByte();
            if (reply == JudgeReply.COMPILING.getId()) {
                logJudgeReply(reply);
                reply = in.readByte();
            }
            return reply;
        } finally {
            tempFile.delete();
        }
    }

    private void sendTestcase(int testcase, int timeLimit, int memoryLimit, int outputLimit) throws IOException {
        logger.info(testcase + " " + timeLimit + " " + memoryLimit + " " + outputLimit);
        this.out.writeByte(testcase);
        this.out.writeShort(timeLimit);
        this.out.writeInt(memoryLimit);
        this.out.writeShort(outputLimit);
    }

    public void terminate() {
        try {
            if (!this.socket.isClosed()) {
                this.socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.interrupt();
    }
}
