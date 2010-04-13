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
#include "text_checker.h"

#include <fcntl.h>

#include "protocol.h"

class TextCheckerTest : public TestFixture, public TextChecker {
  protected:
    void SetUp() {
        root_ = tmpnam(NULL);
        ASSERT_EQUAL(0, mkdir(root_.c_str(), 0700));
        ASSERT_EQUAL(0, chdir(root_.c_str()));
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

    void TearDown() {
        if (system(("rm -rf " + root_).c_str())) {
        }
    }

    void MakeFile(const string& filename, const vector<string>& lines) {
        int fd = open(filename.c_str(), O_RDWR | O_CREAT | O_TRUNC, 0600);
        ASSERT(fd >= 0);
        string content;
        for (int i = 0; i < lines.size(); ++i) {
            content += lines[i];
        }
        ASSERT_EQUAL((int)content.size(), (int) write(fd, content.c_str(), content.size()));
        close(fd);
    }

    int Run() {
        MakeFile("output", lines_[0]);
        MakeFile("p.out", lines_[1]);
        return InternalCheck(0);
    }

    string root_;
    string filename_[2];
    vector<string> lines_[2];
};

TEST_F(TextCheckerTest, Accepted) {
    ASSERT_EQUAL(ACCEPTED, Run());
}

TEST_F(TextCheckerTest, AcceptedIgnoreEndingNewLine) {
    lines_[1].back() += '\n';
    ASSERT_EQUAL(ACCEPTED, Run());
}

TEST_F(TextCheckerTest, WrongAnswerMissingLine) {
    lines_[1].erase(lines_[1].begin() + 1);
    ASSERT_EQUAL(WRONG_ANSWER, Run());
}

TEST_F(TextCheckerTest, WrongAnswerExtraLine) {
    lines_[1].insert(lines_[1].begin() + 3, "a\n");
    ASSERT_EQUAL(WRONG_ANSWER, Run());
}

TEST_F(TextCheckerTest, WrongAnswerExtraLineAtTheEnd) {
    lines_[1].push_back("\na");
    ASSERT_EQUAL(WRONG_ANSWER, Run());
}

TEST_F(TextCheckerTest, WrongAnswerExtraField) {
    lines_[1][1] = "  aa b  \n";
    ASSERT_EQUAL(WRONG_ANSWER, Run());
}

TEST_F(TextCheckerTest, WrongAnswerLongerField) {
    lines_[1][1] = "  aab  \n";
    ASSERT_EQUAL(WRONG_ANSWER, Run());
}

TEST_F(TextCheckerTest, WrongAnswerShorterField) {
    lines_[1][1] = "  a  \n";
    ASSERT_EQUAL(WRONG_ANSWER, Run());
}

TEST_F(TextCheckerTest, WrongAnswerLineSplit) {
    lines_[1][0] = "a ab \n";
    lines_[1].insert(lines_[1].begin() + 1, "abc\r");
    ASSERT_EQUAL(WRONG_ANSWER, Run());
}

TEST_F(TextCheckerTest, PresentationErrorExtraBlankLine) {
    lines_[1].insert(lines_[1].begin() + 3, " \t  \n");
    ASSERT_EQUAL(PRESENTATION_ERROR, Run());
}

TEST_F(TextCheckerTest, PresentationErrorExtraBlankLineAtTheEnd) {
    lines_[1].push_back("\n\n\n");
    ASSERT_EQUAL(PRESENTATION_ERROR, Run());
}

TEST_F(TextCheckerTest, PresentationErrorMissingBlankLine) {
    lines_[1].erase(lines_[1].begin() + 3);
    ASSERT_EQUAL(PRESENTATION_ERROR, Run());
}

TEST_F(TextCheckerTest, PresentationErrorExtraSpace) {
    lines_[1].back() += " ";
    ASSERT_EQUAL(PRESENTATION_ERROR, Run());
}

TEST_F(TextCheckerTest, PresentationErrorMissingSpace) {
    lines_[1][4] = " aaa  bbb c  \n";
    ASSERT_EQUAL(PRESENTATION_ERROR, Run());
}

TEST_F(TextCheckerTest, PresentationErrorNormalized) {
    lines_[1].clear();
    lines_[1].push_back("a ab abc\n");
    lines_[1].push_back("aa\n");
    lines_[1].push_back("aaa bbb c\n");
    lines_[1].push_back("a\n");
    ASSERT_EQUAL(PRESENTATION_ERROR, Run());
}

