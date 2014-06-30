/*
 * Copyright 2010 Li, Cheng <hanshuiys@gmail.com>
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

#ifndef __SCRIPT_INITIALIZER_H__
#define __SCRIPT_INITIALIZER_H__

class ScriptRunner;

namespace {
    class ScriptInitializerBuilder;
};

class ScriptInitializer {
private:
    int language_id_;    // should be consistent with compiler.cc

protected:
    ScriptInitializer(int language_id) : language_id_(language_id) { }
    int GetLanguageId() { return language_id_; }
    virtual ScriptInitializer* clone() = 0;
    friend class ::ScriptInitializerBuilder;

public:
    // this function will be called each time target script runs
    virtual void SetUp(ScriptRunner* runner) {}
    virtual ~ScriptInitializer() {}

    static ScriptInitializer* create(int language_id);
};


#endif // __SCRIPT_INITIALIZER_H__
