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

#ifndef __ARGS_H
#define __ARGS_H

#include <string>
#include <vector>

using namespace std;

class ArgumentInfo {
  public:
    ArgumentInfo(string type, string name, string default_value, string description, bool optional, void* reference);
    void Print();
    bool Assign(const string& value);

    const string& type() { return type_; }
    const string& name() { return name_; }
    bool IsOptional() { return optional_; }

  private:
    string type_;
    string name_;
    string default_value_;
    string description_;
    bool optional_;
    void* reference_;
};

#define DEFINE_int ;
#define DEFINE_bool ;
#define DEFINE_string ;

#define DEFINE_ARG(type, name, description) \
    DEFINE_##type; \
    type ARG_##name; \
    ArgumentInfo _info_##name(#type, #name, "", description, false, &ARG_##name)

#define DEFINE_OPTIONAL_ARG(type, name, default_value, description) \
    DEFINE_##type; \
    type ARG_##name = default_value; \
    ArgumentInfo _info_##name(#type, #name, #default_value, description, true, &ARG_##name)

#define DECLARE_ARG(type, name) extern type ARG_##name

// Extracts parameter values from the passed-in arguments.
int ParseArguments(int argc, char* argv[]);

#endif
