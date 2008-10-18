/*
 * Copyright 2007 Xu, Chuan <xuchuan@gmail.com>
 *
 * This file is part of ZOJ.
 *
 * ZOJ is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * ZOJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZOJ. if not, see <http://www.gnu.org/licenses/>.
 */

#include "unittest.h"

#include <fcntl.h>
#include <sys/socket.h>

#include "check.h"
#include "global.h"
#include "trace.h"
#include "test_util-inl.h"

class TextFileTest : public TestFixture {
  protected:
    virtual void SetUp() {
        filename_ = tmpnam(NULL);
        make_file_ = 1;
    }

    virtual void TearDown() {
        delete file_;
        system(("rm -f " + filename_).c_str());
    }

    void MakeFile(const string& content) {
        if (make_file_) {
            int fd = open(filename_.c_str(), O_RDWR | O_CREAT | O_TRUNC, 0600);
            ASSERT(fd >= 0);
            Writen(fd, content.c_str(), content.size());
            close(fd);
        }
        file_ = new TextFile(filename_);
    }

    int make_file_;
    string filename_;
    TextFile* file_;
};

TEST_F(TextFileTest, Read) {
    MakeFile("abcd");
    ASSERT_EQUAL((int)'a', file_->Read());
    ASSERT_EQUAL((int)'b', file_->Read());
    ASSERT_EQUAL((int)'c', file_->Read());
}

TEST_F(TextFileTest, ReadFailure) {
    make_file_ = 0;
    MakeFile("");
    ASSERT_EQUAL(-1, file_->Read());
}

TEST_F(TextFileTest, ReadEOF) {
    MakeFile("");
    ASSERT_EQUAL(0, file_->Read());
}

TEST_F(TextFileTest, ReadCR) {
    MakeFile("\ra");
    ASSERT_EQUAL((int)'\n', file_->Read());
    ASSERT_EQUAL((int)'a', file_->Read());
    ASSERT_EQUAL(0, file_->Read());
}

TEST_F(TextFileTest, ReadCREOF) {
    MakeFile("\r");
    ASSERT_EQUAL((int)'\n', file_->Read());
    ASSERT_EQUAL(0, file_->Read());
}

TEST_F(TextFileTest, ReadCRLF) {
    MakeFile("\r\n");
    ASSERT_EQUAL((int)'\n', file_->Read());
    ASSERT_EQUAL(0, file_->Read());
}

TEST_F(TextFileTest, ReadCRCRLF) {
    MakeFile("\r\r\n");
    ASSERT_EQUAL((int)'\n', file_->Read());
    ASSERT_EQUAL((int)'\n', file_->Read());
    ASSERT_EQUAL(0, file_->Read());
}

TEST_F(TextFileTest, ReadCRLFLF) {
    MakeFile("\r\n\n");
    ASSERT_EQUAL((int)'\n', file_->Read());
    ASSERT_EQUAL((int)'\n', file_->Read());
    ASSERT_EQUAL(0, file_->Read());
}

TEST_F(TextFileTest, ReadCROnBufferBorder) {
    char buf[1025];
    memset(buf, 'a', sizeof(buf));
    buf[1023] = '\r';
    buf[1024] = 0;
    MakeFile(buf);
    for (int i = 0; i < 1023; ++i) {
        ASSERT_EQUAL((int)'a', file_->Read());
    }
    ASSERT_EQUAL((int)'\n', file_->Read());
    ASSERT_EQUAL(0, file_->Read());
}

TEST_F(TextFileTest, ReadCRLFOnBufferBorder) {
    char buf[1026];
    memset(buf, 'a', sizeof(buf));
    buf[1023] = '\r';
    buf[1024] = '\n';
    buf[1025] = 0;
    MakeFile(buf);
    for (int i = 0; i < 1023; ++i) {
        ASSERT_EQUAL((int)'a', file_->Read());
    }
    ASSERT_EQUAL((int)'\n', file_->Read());
    ASSERT_EQUAL(0, file_->Read());
}

TEST_F(TextFileTest, ReadHuge) {
    char buf[4097];
    for (int i = 0; i < 4096; ++i) {
        buf[i] = ' ' + i % 90;
    }
    buf[4096] = 0;
    MakeFile(buf);
    for (int i = 0; i < 4096; ++i) {
        ASSERT_EQUAL(' ' + i % 90, file_->Read());
    }
    ASSERT_EQUAL(0, file_->Read());
}

class CompareTextFilesTest : public TestFixture {
  protected:
    virtual void SetUp() {
        filename_[0] = filename_[1] = "";
        const char* content[] = {"a ab abc\r",
                                 "  aa  \n",
                                 "  \r\n",
                                 "\r\n",
                                 " aaa   bbb c  \n",
                                 "a"};
        for (int i = 0; i < sizeof(content) / sizeof(content[0]); ++i) {
            lines_[0].push_back(content[i]);
        }
        lines_[1] = lines_[0];
    }

    virtual void TearDown() {
        system(("rm -f " + filename_[0] + " " + filename_[1]).c_str());
    }

    int Run() {
        if (filename_[0].empty()) {
            filename_[0] = MakeFile(lines_[0]);
        }
        if (filename_[1].empty()) {
            filename_[1] = MakeFile(lines_[1]);
        }
        return CompareTextFiles(filename_[0], filename_[1]);
    }

    string MakeFile(const vector<string>& lines) {
        string filename = tmpnam(NULL);
        int fd = open(filename.c_str(), O_RDWR | O_CREAT | O_TRUNC, 0600);
        ASSERT(fd >= 0);
        string content;
        for (int i = 0; i < lines.size(); ++i) {
            content += lines[i];
        }
        Writen(fd, content.c_str(), content.size());
        close(fd);
        return filename;
    }

    string filename_[2];
    vector<string> lines_[2];
};

TEST_F(CompareTextFilesTest, InvalidFilename1) {
    filename_[0] = filename_[1] = "/invalid";
    ASSERT_EQUAL(INTERNAL_ERROR, Run());
}

TEST_F(CompareTextFilesTest, InvalidFilename2) {
    filename_[0] = "/invalid";
    ASSERT_EQUAL(INTERNAL_ERROR, Run());
}

TEST_F(CompareTextFilesTest, InvalidFilename3) {
    filename_[1] = "invalid";
    ASSERT_EQUAL(INTERNAL_ERROR, Run());
}

TEST_F(CompareTextFilesTest, Accepted) {
    ASSERT_EQUAL(ACCEPTED, Run());
}

TEST_F(CompareTextFilesTest, AcceptedIgnoreEndingNewLine) {
    lines_[1].back() += '\n';
    ASSERT_EQUAL(ACCEPTED, Run());
}

TEST_F(CompareTextFilesTest, WrongAnswerMissingLine) {
    lines_[1].erase(lines_[1].begin() + 1);
    ASSERT_EQUAL(WRONG_ANSWER, Run());
}

TEST_F(CompareTextFilesTest, WrongAnswerExtraLine) {
    lines_[1].insert(lines_[1].begin() + 3, "a\n");
    ASSERT_EQUAL(WRONG_ANSWER, Run());
}

TEST_F(CompareTextFilesTest, WrongAnswerExtraLineAtTheEnd) {
    lines_[1].push_back("\na");
    ASSERT_EQUAL(WRONG_ANSWER, Run());
}

TEST_F(CompareTextFilesTest, WrongAnswerExtraField) {
    lines_[1][1] = "  aa b  \n";
    ASSERT_EQUAL(WRONG_ANSWER, Run());
}

TEST_F(CompareTextFilesTest, WrongAnswerLongerField) {
    lines_[1][1] = "  aab  \n";
    ASSERT_EQUAL(WRONG_ANSWER, Run());
}

TEST_F(CompareTextFilesTest, WrongAnswerShorterField) {
    lines_[1][1] = "  a  \n";
    ASSERT_EQUAL(WRONG_ANSWER, Run());
}

TEST_F(CompareTextFilesTest, WrongAnswerLineSplit) {
    lines_[1][0] = "a ab \n";
    lines_[1].insert(lines_[1].begin() + 1, "abc\r");
    ASSERT_EQUAL(WRONG_ANSWER, Run());
}

TEST_F(CompareTextFilesTest, PresentationErrorExtraBlankLine) {
    lines_[1].insert(lines_[1].begin() + 3, " \t  \n");
    ASSERT_EQUAL(PRESENTATION_ERROR, Run());
}

TEST_F(CompareTextFilesTest, PresentationErrorExtraBlankLineAtTheEnd) {
    lines_[1].push_back("\n\n\n");
    ASSERT_EQUAL(PRESENTATION_ERROR, Run());
}

TEST_F(CompareTextFilesTest, PresentationErrorMissingBlankLine) {
    lines_[1].erase(lines_[1].begin() + 3);
    ASSERT_EQUAL(PRESENTATION_ERROR, Run());
}

TEST_F(CompareTextFilesTest, PresentationErrorExtraSpace) {
    lines_[1].back() += " ";
    ASSERT_EQUAL(PRESENTATION_ERROR, Run());
}

TEST_F(CompareTextFilesTest, PresentationErrorMissingSpace) {
    lines_[1][4] = " aaa  bbb c  \n";
    ASSERT_EQUAL(PRESENTATION_ERROR, Run());
}

TEST_F(CompareTextFilesTest, PresentationErrorNormalized) {
    lines_[1].clear();
    lines_[1].push_back("a ab abc\n");
    lines_[1].push_back("aa\n");
    lines_[1].push_back("aaa bbb c\n");
    lines_[1].push_back("a\n");
    ASSERT_EQUAL(PRESENTATION_ERROR, Run());
}

class DoCheckTest: public TestFixture {
  protected:
    virtual void SetUp() {
        root_ = tmpnam(NULL);
        ASSERT_EQUAL(0, mkdir(root_.c_str(), 0700));
        ASSERT_EQUAL(0, chdir(root_.c_str()));
        fd_[0] = fd_[1] = -1;
        ASSERT_EQUAL(0, socketpair(AF_UNIX, SOCK_STREAM, 0, fd_));
        InstallHandlers();
        ASSERT_EQUAL(0, symlink((TESTDIR + "/1.in").c_str(), "input"));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/1.out").c_str(), "output"));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/judge").c_str(), "judge"));
        ASSERT_EQUAL(0, shutdown(fd_[0], SHUT_WR));
        output_ = judge_ = "";
    }

    virtual void TearDown() {
        UninstallHandlers();
        if (fd_[0] >= 0) {
            close(fd_[0]);
        }
        if (fd_[1] >= 0) {
            close(fd_[1]);
        }
        system(("rm -rf " + root_).c_str());
    }

    int Run() {
        ASSERT_EQUAL(0, symlink((TESTDIR + "/" + output_).c_str(), "p.out"));
        int ret = DoCheck(fd_[1], 0, judge_);
        shutdown(fd_[1], SHUT_WR);
        return ret;
    }

    int fd_[2];
    char buf_[32];
    string root_;
    string output_;
    string judge_;
};

TEST_F(DoCheckTest, Accepted) {
    output_ = "ac.out";

    ASSERT_EQUAL(0, Run());

    ASSERT_EQUAL(JUDGING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(ACCEPTED, ReadLastUint32(fd_[0]));
}

TEST_F(DoCheckTest, WrongAnswer) {
    output_ = "wa.out";

    ASSERT_EQUAL(0, Run());

    ASSERT_EQUAL(JUDGING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(WRONG_ANSWER, ReadLastUint32(fd_[0]));
}

TEST_F(DoCheckTest, PresentationError) {
    output_ = "pe.out";

    ASSERT_EQUAL(0, Run());

    ASSERT_EQUAL(JUDGING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(PRESENTATION_ERROR, ReadLastUint32(fd_[0]));
}

TEST_F(DoCheckTest, SpecialJudgeAccepted) {
    output_ = "ac.out";
    judge_ = "judge";

    ASSERT_EQUAL(0, Run());

    ASSERT_EQUAL(JUDGING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(ACCEPTED, ReadLastUint32(fd_[0]));
}

TEST_F(DoCheckTest, SpecialJudgeWrongAnswer) {
    output_ = "wa.out";
    judge_ = "judge";

    ASSERT_EQUAL(0, Run());

    ASSERT_EQUAL(JUDGING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(WRONG_ANSWER, ReadLastUint32(fd_[0]));
}

TEST_F(DoCheckTest, SpecialJudgePresentationError) {
    output_ = "pe.out";
    judge_ = "judge";

    ASSERT_EQUAL(0, Run());

    ASSERT_EQUAL(JUDGING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(PRESENTATION_ERROR, ReadLastUint32(fd_[0]));
}
