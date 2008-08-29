package cn.edu.zju.acm.onlinejudge.judgeservice;

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

    private JudgeClient client;

    private Socket socket;

    private InetSocketAddress address;

    private Logger logger;

    public JudgeClientInstance(JudgeClient client, String host, int port) {
        this.client = client;
        this.address = new InetSocketAddress(host, port);
        logger = Logger.getLogger(JudgeService.class.getName());
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
        } catch (IOException e) {
            logger.error(e);
            this.client.removeInstance(this);
            return;
        }
        try {
            logger.info("Start testing");
            JudgeQueue queue = this.client.getJudgeQueue();
            while (!this.isInterrupted()) {
                Submission submission;
                submission = queue.poll();
                try {
                    this.judge(submission);
                } catch (IOException e) {
                    queue.rejudge(submission);
                    this.logger.error("Judge failed", e);
                    return;
                } catch (JudgeServerErrorException e) {
                    this.logger.error("Judge failed", e);
                    submission.setJudgeReply(JudgeReply.JUDGE_INTERNAL_ERROR);
                }

                submissionDAO.beginTransaction();
                submissionDAO.update(submission, problemDAO.getProblem(submission.getProblemId()).getContestId());
                submissionDAO.commitTransaction();
                submission.setContent(null);
                if (submission.getJudgeReply() == JudgeReply.JUDGE_INTERNAL_ERROR) {
                    // queue.rejudge(submission);
                    return;
                }
            }
        } catch (PersistenceException e) {
            this.logger.error(e);
        } catch (InterruptedException e) {
        } finally {
            if (!this.socket.isClosed()) {
                try {
                    this.socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            this.client.removeInstance(this);
        }
    }

    private void judge(Submission submission) throws JudgeServerErrorException, IOException, PersistenceException {
        Problem problem = problemDAO.getProblem(submission.getProblemId());
        int reply = this.sendJudgeCommand(problem.getId(), problem.getRevision(), submission.getId());
        if (reply == JudgeReply.NO_SUCH_PROBLEM.getId()) {
            reply = this.sendDataCommand(problem);
        }
        if (reply != JudgeReply.READY.getId()) {
            throw new JudgeServerErrorException();
        }
        reply = this.sendCompileCommand(submission.getLanguage().getOptions(), submission.getContent());
        if (reply != JudgeReply.COMPILING.getId()) {
            throw new JudgeServerErrorException();
        }
        submission.setJudgeReply(JudgeReply.COMPILING);
        reply = this.in.readInt();
        logJudgeReply(reply);
        if (reply == JudgeReply.COMPILATION_ERROR.getId()) {
            submission.setJudgeReply(JudgeReply.COMPILATION_ERROR);
            int length = this.in.readInt();
            byte[] bytes = new byte[length];
            this.in.read(bytes);
            submission.setJudgeComment(new String(bytes));
            return;
        } else if (reply != JudgeReply.READY.getId()) {
            throw new JudgeServerErrorException();
        }
        Limit limit = problem.getLimit();
        reply = this.sendTestcaseCommand(1, limit.getTimeLimit(), limit.getMemoryLimit(), limit.getOutputLimit());
        if (reply != JudgeReply.RUNNING.getId()) {
            throw new JudgeServerErrorException();
        }
        submission.setJudgeReply(JudgeReply.RUNNING);
        while (reply == JudgeReply.RUNNING.getId()) {
            int timeConsumption = this.in.readInt();
            int memoryConsumption = this.in.readInt();
            submission.setTimeConsumption(timeConsumption);
            submission.setMemoryConsumption(memoryConsumption);
            logger.info("Running " + timeConsumption + " " + memoryConsumption);
            reply = this.in.readInt();
        }
        logJudgeReply(reply);
        if (reply == JudgeReply.JUDGING.getId()) {
            submission.setJudgeReply(JudgeReply.JUDGING);
            reply = this.in.readInt();
            logJudgeReply(reply);
        }
        if (reply == JudgeReply.JUDGE_INTERNAL_ERROR.getId()) {
            throw new JudgeServerErrorException();
        }
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
    
    private int sendPingCommand() throws IOException {
        logger.info("Ping");
        this.sendCommand(JudgeClientCommandsFactory.createPingCommand());
        int reply = this.in.readInt();
        logJudgeReply(reply);
        return reply;
    }

    private int sendJudgeCommand(long problemId, int problemRevision, long submissionId) throws IOException {
        logger.info("Judge prob:" + problemId + " rev:" + problemRevision + " sub:" + submissionId);
        this.sendCommand(JudgeClientCommandsFactory.createJudgeCommand(problemId, problemRevision, submissionId));
        int reply = this.in.readInt();
        logJudgeReply(reply);
        return reply;
    }

    private int sendDataCommand(Problem problem) throws IOException, PersistenceException {
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
            logger.info("Data size:" + tempFile.length());
            this.sendCommand(JudgeClientCommandsFactory.createDataCommand((int) tempFile.length()));
            int reply = in.readInt();
            logJudgeReply(reply);
            if (reply == JudgeReply.READY.getId()) {
                try {
                    CopyUtils.copy(fin, out);
                } finally {
                    fin.close();
                }
                reply = in.readInt();
                logJudgeReply(reply);
                if (reply == JudgeReply.COMPILING.getId()) {
                    reply = in.readInt();
                    logJudgeReply(reply);
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

    private int sendCompileCommand(String sourceFileType, String sourceFile) throws JudgeServerErrorException,
            IOException {
        byte[] bytes = sourceFile.getBytes();
        String sourceFileTypes[] = { "cc", "cpp", "pas", "c", "java" };
        for (int i = 0; i <= sourceFileTypes.length; ++i) {
            if (i == sourceFileTypes.length) {
                throw new JudgeServerErrorException();
            } else if (sourceFileType.equals(sourceFileTypes[i])) {
                logger.info("Compiler:" + i + " source size:" + bytes.length);
                this.sendCommand(JudgeClientCommandsFactory.createCompileCommand(i, sourceFile.length()));
                break;
            }
        }
        int reply = this.in.readInt();
        logJudgeReply(reply);
        if (reply == JudgeReply.READY.getId()) {
            this.out.write(bytes);
            this.out.flush();
            reply = this.in.readInt();
            logJudgeReply(reply);
        }
        return reply;
    }

    private int sendTestcaseCommand(int testcase, int timeLimit, int memoryLimit, int outputLimit) throws IOException {
        logger.info("Testcase:" + testcase + " TL:" + timeLimit + " ML:" + memoryLimit + " OL:" + outputLimit);
        this.sendCommand(JudgeClientCommandsFactory
                .createTestCaseCommand(testcase, timeLimit, memoryLimit, outputLimit));
        int reply = this.in.readInt();
        logJudgeReply(reply);
        return reply;
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            if (this.socket != null && !this.socket.isClosed()) {
                this.socket.close();
            }
        } catch (IOException e) {
        }
    }
}
