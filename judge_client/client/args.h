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

#ifndef __ARGS_H
#define __ARGS_H

#include <string>
#include <vector>

using namespace std;

class ArgumentInfo {
  public:
    ArgumentInfo(string type,
                 string name,
                 string defaultValue,
                 string description,
                 bool optional,
                 void* reference);
    void Print();
    bool Assign(const string& value);

    const string& type() { return type_; }
    const string& name() { return name_; }
    bool optional() { return optional_; }

  private:
    string type_;
    string name_;
    string defaultValue_;
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

#define DEFINE_OPTIONAL_ARG(type, name, defaultValue, description) \
    DEFINE_##type; \
    type ARG_##name = defaultValue; \
    ArgumentInfo _info_##name(#type, #name, #defaultValue, description, true, &ARG_##name)

#define DECLARE_ARG(type, name) \
    extern type ARG_##name

// Extracts parameter values from the passed-in arguments.
int parseArguments(int argc, char* argv[]);

#endif
