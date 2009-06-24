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

#include "args.h"

#include <iostream>
#include <stdlib.h>

#include <getopt.h>

#include "util.h"

vector<ArgumentInfo*>* info_list;

ArgumentInfo::ArgumentInfo(
        string type, string name, string default_value, string description, bool optional, void* reference)
        : type_(type), name_(name), default_value_(default_value),
          description_(description), optional_(optional), reference_(reference) {
    if (info_list == NULL) {
        info_list = new vector<ArgumentInfo*>;
    }
    info_list->push_back(this);
}

void ArgumentInfo::Print() {
    cout<<string(8, ' ')<<"--"<<name_
        <<string(max(int(14 - name_.length()), 1), ' ')
        <<description_<<endl<<string(24, ' ')<<"type: "<<type_;
    if (optional_) {
        cout<<endl<<string(24, ' ')<<"default value: "<<default_value_;
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

void PrintUsage() {
    cout<<"Usage: judge [options] "<<endl;
    for (int i = 0; i < info_list->size(); ++i) {
        (*info_list)[i]->Print();
    }
}

int ParseArguments(int argc, const char* argv[]) {
    if (info_list == NULL) {
        info_list = new vector<ArgumentInfo*>;
    }
    vector<bool> assigned(info_list->size(), false);
    for (int i = 1; i < argc; ++i) {
        if (argv[i][0] == '-' && argv[i][1] == '-') {
            const char* p = argv[i] + 2;
            while (*p && *p != '=') {
                ++p;
            }
            string name(argv[i] + 2, p - argv[i] - 2);
            bool found = false;
            for (int j = 0; j < info_list->size(); ++j) {
                if ((*info_list)[j]->name() == name) {
                    if (*p) {
                        if (!(*info_list)[j]->Assign(p + 1)) {
                            cerr<<"Invalid value for argument "<<name<<endl;
                            PrintUsage();
                            return -1;
                        }
                    } else {
                        if ((*info_list)[j]->type() != "bool") {
                            cerr<<"Missing value for argument "<<name<<endl;
                            PrintUsage();
                            return -1;
                        }
                        (*info_list)[j]->Assign("true");
                    }
                    found = true;
                    assigned[j] = true;
                    break;
                }
            }
            if (!found) {
                cerr<<"Invalid argument "<<name<<endl;
                PrintUsage();
                return -1;
            }
        }
    }
    for (int i = 0; i < info_list->size(); ++i) {
        if (!assigned[i] && !(*info_list)[i]->IsOptional()) {
            cerr<<"Missing argument "<<(*info_list)[i]->name()<<endl;
            PrintUsage();
            return -1;
        }
    }
    return 0;
}
