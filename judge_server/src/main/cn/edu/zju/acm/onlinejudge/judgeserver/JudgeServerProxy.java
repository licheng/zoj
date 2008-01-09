package cn.edu.zju.acm.onlinejudge.judgeserver;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.CopyUtils;

import cn.edu.zju.acm.onlinejudge.bean.Limit;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.Reference;
import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.ReferenceType;
import cn.edu.zju.acm.onlinejudge.dao.DAOFactory;
import cn.edu.zju.acm.onlinejudge.dao.ProblemDAO;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;

public class JudgeServerProxy {
    public static final int CONNECTION_TIMEOUT = 30000;

    public static final int READ_TIMEOUT = 60000;

    private final SocketAddress socketAddress;

    private ThreadLocal<Socket> socket = new ThreadLocal<Socket>();

    private ProblemDAO problemDAO = DAOFactory.getProblemDAO();

    public static final int READY = 900;

    public static final int DUPLICATE_PROBLEM = 901;

    public static final int NO_SUCH_PROBLEM = 902;

    public static final int SERVER_ERROR = 910;

    public JudgeServerProxy(InetAddress address, int port) {
	this.socketAddress = new InetSocketAddress(address, port);
    }

    public void judge(Submission submission) throws JudgeServerErrorException,
	    PersistenceException,
	    NoSuchProblemException {
	try {
	    Problem problem = problemDAO.getProblem(submission.getProblemId());
	    String sourceFileType = submission.getLanguage().getOptions();
	    Limit limit = problem.getLimit();
	    String judgeCommand =
		    makeCommand("judge %d %s %d * %s %d %d %d\n",
			    submission.getId(),
			    sourceFileType,
			    problem.getId(),
			    "" + problem.getRevision(),
			    limit.getTimeLimit(),
			    limit.getMemoryLimit(),
			    limit.getOutputLimit());
	    Socket socket = getConnection();
	    OutputStream out = socket.getOutputStream();
	    Scanner in = new Scanner(socket.getInputStream());
	    out.write(judgeCommand.getBytes("ASCII"));
	    out.flush();
	    int response = in.nextInt();
	    if (response == JudgeReply.JUDGE_INTERNAL_ERROR.getId()) {
		submission.setJudgeReply(JudgeReply.JUDGE_INTERNAL_ERROR);
		return;
	    }
	    if (response == NO_SUCH_PROBLEM) {
		throw new NoSuchProblemException(problem.getId() + " " + problem.getRevision());
	    }
	    if (response != READY) {
		throw new JudgeServerErrorException();
	    }
	    CopyUtils.copy(new ByteArrayInputStream(submission.getContent().getBytes("ASCII")), out);
	    socket.shutdownOutput();
	    submission.setJudgeReply(JudgeReply.COMPILING);
	    response = in.nextInt();
	    if (response == JudgeReply.COMPILATION_ERROR.getId()) {
		submission.setJudgeReply(JudgeReply.COMPILATION_ERROR);
		in.nextLine();
		StringBuilder buf = new StringBuilder();
		while (in.hasNextLine()) {
		    buf.append(in.nextLine());
		    buf.append('\n');
		}
		submission.setJudgeComment(buf.toString());
		return;
	    }
	    if (response != JudgeReply.RUNNING.getId()) {
		throw new JudgeServerErrorException();
	    }
	    submission.setJudgeReply(JudgeReply.RUNNING);
	    for (;;) {
		double timeConsumption = in.nextDouble();
		int memoryConsumption = in.nextInt();
		if (timeConsumption < 0 && memoryConsumption < 0) {
		    break;
		}
		submission.setTimeConsumption((int) (timeConsumption * 1000));
		submission.setMemoryConsumption(memoryConsumption);
	    }
	    response = in.nextInt();
	    if (response == JudgeReply.JUDGING.getId()) {
		submission.setJudgeReply(JudgeReply.JUDGING);
		response = in.nextInt();
	    }
	    submission.setJudgeReply(JudgeReply.findById(response));
	    if (submission.getJudgeReply() == null) {
		throw new JudgeServerErrorException();
	    }
	} catch (IOException e) {
	    throw new JudgeServerErrorException(e);
	} catch (NoSuchElementException e) {
	    throw new JudgeServerErrorException(e);
	} finally {
	    closeSocket();
	}
    }

    public void sendProblem(Problem problem) throws JudgeServerErrorException, PersistenceException, IOException {
	File tempFile = File.createTempFile("prob", null);

	ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(tempFile));
	List<Reference> inputFiles =
		DAOFactory.getReferenceDAO().getProblemReferences(problem.getId(), ReferenceType.INPUT);
	for (int i = 0; i < inputFiles.size(); i++) {
	    zipOut.putNextEntry(new ZipEntry(String.format("input.%d", i)));
	    CopyUtils.copy(inputFiles.get(i).getContent(), zipOut);
	}
	List<Reference> outputFiles =
		DAOFactory.getReferenceDAO().getProblemReferences(problem.getId(), ReferenceType.OUTPUT);
	for (int i = 0; i < outputFiles.size(); i++) {
	    zipOut.putNextEntry(new ZipEntry(String.format("output.%d", i)));
	    CopyUtils.copy(outputFiles.get(i).getContent(), zipOut);
	}
	if (problem.isChecker()) {
	    List<Reference> specialJudges =
		    DAOFactory.getReferenceDAO().getProblemReferences(problem.getId(), ReferenceType.CHECKER_SOURCE);
	    if (specialJudges.size() > 0) {
		zipOut.putNextEntry(new ZipEntry(String.format("judge.%s", specialJudges.get(0).getContentType())));
		CopyUtils.copy(specialJudges.get(0).getContent(), zipOut);
	    }
	}
	zipOut.close();
	try {
	    Socket socket = getConnection();
	    OutputStream out = socket.getOutputStream();
	    Scanner in = new Scanner(socket.getInputStream());
	    out.write(makeCommand("saveprob %d %d", problem.getId(), problem.getRevision()).getBytes("ASCII"));
	    out.flush();
	    int response = in.nextInt();
	    if (response == JudgeReply.JUDGE_INTERNAL_ERROR.getId()) {
		throw new IllegalArgumentException();
	    }
	    if (response == DUPLICATE_PROBLEM) {
		return;
	    }
	    if (response != READY) {
		throw new JudgeServerErrorException();
	    }
	    FileInputStream fin = new FileInputStream(tempFile);
	    try {
		CopyUtils.copy(fin, out);
	    } finally {
		fin.close();
	    }
	    socket.shutdownOutput();
	    response = in.nextInt();
	    if (response == JudgeReply.JUDGE_INTERNAL_ERROR.getId()) {
		throw new IllegalArgumentException();
	    }
	    if (response != READY) {
		throw new JudgeServerErrorException();
	    }
	} catch (IOException e) {
	    throw new JudgeServerErrorException(e);
	} catch (NoSuchElementException e) {
	    throw new JudgeServerErrorException(e);
	} finally {
	    tempFile.delete();
	    closeSocket();
	}
    }

    private String makeCommand(String format, Object... parameters) {
	StringBuilder builder = new StringBuilder(String.format(format, parameters));
	while (builder.length() < 64) {
	    builder.append(' ');
	}
	return builder.toString();
    }

    private void closeSocket() {
	Socket socket = this.socket.get();
	if (socket != null) {
	    try {
		if (!socket.isClosed()) {
		    socket.close();
		}
		this.socket.set(null);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}

    }

    private Socket getConnection() throws IOException {
	Socket socket = this.socket.get();
	if (socket != null) {
	    return socket;
	}
	socket = new Socket();
	socket.connect(this.socketAddress, CONNECTION_TIMEOUT);
	socket.setSoTimeout(READ_TIMEOUT);
	this.socket.set(socket);
	return socket;

    }
}
