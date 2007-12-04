/*
 * Copyright 2007 Xu, Chuan <xuchuan@gmail.com>
 *
 * This file is part of ZOJ Judge Server.
 *
 * ZOJ Judge Server is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * ZOJ Judge Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZOJ Judge Server; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

#include "params.h"

#include <iostream>
#include <getopt.h>

#include "util.h"

static vector<ArgumentInfo*> infoList;

ArgumentInfo::ArgumentInfo(string type,
                           string name,
                           string defaultValue,
                           string description,
                           bool optional,
                           void* reference)
    : type_(type),
      name_(name),
      defaultValue_(defaultValue),
      description_(description),
      optional_(optional),
      reference_(reference) {
    infoList.push_back(this);
}

void ArgumentInfo::Print() {
    cout<<"        -"<<name_<<": "<<description_<<endl
        <<"            type: "<<type_
        <<"            optional: "<<(optional_ ? "true" : "false");
    if (optional_) {
        cout<<endl<<"            default value: "<<defaultValue_;
    }
    cout<<endl;
}

bool ArgumentInfo::Assign(const string& value) {
    char* p;
    if (type_ == "int") {
        if (value.empty()) {
            return false;
        }
        *(int*)reference_ = strtol(value.c_str(), &p, 10);
        if (p != value.c_str() + value.size()) {
            return false;
        }
    } else if (type_ == "string") {
        *(string*)reference_ = value;
    } else if (type_ == "bool") {
        string s;
        for (int i = 0; i < value.size(); ++i) {
            s += tolower(value[i]);
        }
        if (s == "true") {
            *(bool*)reference_ = true;
        } else if (s == "false") {
            *(bool*)reference_ = false;
        } else {
            return false;
        }
    }
    return true;
}

void printUsage() {
    cout<<"Usage: judge [arguments] "<<endl;
    for (int i = 0; i < infoList.size(); ++i) {
        infoList[i]->Print();
    }
}

int parseArguments(int argc, char* argv[]) {
    vector<bool> assigned(infoList.size(), false);
    for (int i = 1; i < argc; ++i) {
        if (argv[i][0] == '-' && argv[i][1] == '-') {
            char* p = argv[i] + 2;
            while (*p && *p != '=') {
                ++p;
            }
            string name(argv[i] + 2, p - argv[i] - 2);
            bool found = false;
            for (int j = 0; j < infoList.size(); ++j) {
                if (infoList[j]->name() == name) {
                    if (*p) {
                        if (!infoList[j]->Assign(p + 1)) {
                            cerr<<"Invalid value for argument "<<name<<endl;
                            return -1;
                        }
                    } else {
                        if (infoList[j]->type() != "bool") {
                            cerr<<"Missing value for argument "<<name<<endl;
                            return -1;
                        }
                        infoList[j]->Assign("true");
                    }
                    found = true;
                    assigned[j] = true;
                    break;
                }
            }
            if (!found) {
                cerr<<"Invalid argument "<<name<<endl;
                return -1;
            }
        }
    }
    for (int i = 0; i < infoList.size(); ++i) {
        if (!assigned[i] && !infoList[i]->optional()) {
            cerr<<"Missing argument "<<infoList[i]->name()<<endl;
            return -1;
        }
    }
    return 0;
}
