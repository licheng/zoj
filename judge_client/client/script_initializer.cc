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

#include <sys/syscall.h>
#include <cstdlib>

#include "script_initializer.h"
#include "text_checker.h"
#include "special_checker.h"
#include "common_io.h"
#include "compiler.h"
#include "environment.h"
#include "global.h"
#include "logging.h"
#include "protocol.h"
#include "script_runner.h"
#include "strutil.h"
#include "util.h"

#include <cstdio>

DECLARE_ARG(string, root);

namespace {
    class PythonScriptInitializer : public ScriptInitializer {
    private:
        string loader_string;
        string memory_limit_str;
        const char* commands[7];

    public:
        PythonScriptInitializer() : ScriptInitializer(5) {};

        virtual ScriptInitializer* clone() { return new PythonScriptInitializer(); };

        virtual void SetUp(ScriptRunner* runner) {
            loader_string = StringPrintf("%s/PythonLoader.py", ARG_root.c_str());
            memory_limit_str = StringPrintf("%d", runner->GetMemoryLimit());

            commands[0] = "/usr/bin/python";
            commands[1] = "python";
            commands[2] = "-B";
            commands[3] = loader_string.c_str();
            commands[4] = memory_limit_str.c_str();
            commands[5] = "p.py";
            commands[6] = NULL;

            runner->SetCommands(commands);
            runner->SetLoaderSyscallMagic(__NR_setrlimit, 2);
        };
    };

    class PerlScriptInitializer : public ScriptInitializer {
    private:
        string memory_limit_str;

    public:
        PerlScriptInitializer() : ScriptInitializer(6) {};

        virtual ScriptInitializer* clone() { return new PerlScriptInitializer(); };

        virtual void SetUp(ScriptRunner* runner) {
            memory_limit_str = StringPrintf("%d", runner->GetMemoryLimit());

            setenv("MEMORY_LIMIT", memory_limit_str.c_str(), 1);
            setenv("PERL5LIB", ARG_root.c_str(), 1);

            const static char* commands[] = {
                "/usr/bin/perl",
                "perl",
                "-mPerlLoader",
                "p.pl",
                NULL };
            runner->SetCommands(commands);
            runner->SetLoaderSyscallMagic(__NR_setrlimit, 2);
        };
    };

    class GuileScriptInitializer : public ScriptInitializer {
    private:
        string loader_string;
        string memory_limit_str;
        const char* commands[3];

    public:
        GuileScriptInitializer() : ScriptInitializer(7) {};

        virtual ScriptInitializer* clone() { return new GuileScriptInitializer(); };

        virtual void SetUp(ScriptRunner* runner) {
            loader_string = StringPrintf("%s/guile_loader", ARG_root.c_str());
            memory_limit_str = StringPrintf("%d", runner->GetMemoryLimit());

            commands[0] = loader_string.c_str();
            commands[1] = memory_limit_str.c_str();
            commands[2] = NULL;
    
            runner->SetCommands(commands);
            runner->SetLoaderSyscallMagic(__NR_setrlimit, 2);
        };
    };

    class PHPScriptInitializer : public ScriptInitializer {
    private:
        string loader_string;
        string memory_limit_str;
        const char* commands[8];

    public:
        PHPScriptInitializer() : ScriptInitializer(8) {};

        virtual ScriptInitializer* clone() { return new PHPScriptInitializer(); };

        virtual void SetUp(ScriptRunner* runner) {
            loader_string = StringPrintf("%s/PHPLoader.php", ARG_root.c_str());
            memory_limit_str = StringPrintf("memory_limit=%dk", runner->GetMemoryLimit());

            commands[0] = "/usr/bin/php";
            commands[1] = "php";
            commands[2] = "-d";
            commands[3] = memory_limit_str.c_str();
            commands[4] = "-d";
            commands[5] = "disable_functions=ini_set";
            commands[6] = loader_string.c_str();
            commands[7] = NULL;

            runner->SetCommands(commands);
            runner->SetLoaderSyscallMagic(__NR_uname, 1);
        };
    };

    class ScriptInitializerBuilder {
    private:
        ScriptInitializer* class_list_[4];

    public:
        ScriptInitializerBuilder() {
            class_list_[0] = new PythonScriptInitializer();
            class_list_[1] = new PerlScriptInitializer();
            class_list_[2] = new GuileScriptInitializer();
            class_list_[3] = new PHPScriptInitializer();
        }

        ~ScriptInitializerBuilder() {
            for (int i = 0; i < sizeof(class_list_) / sizeof(ScriptInitializer*); i++) {
                delete class_list_[i];
            }
        }

        ScriptInitializer* get(int language_id) {
            for (int i = 0; i < sizeof(class_list_) / sizeof(ScriptInitializer*); i++) {
                if (class_list_[i]->GetLanguageId() == language_id)
                    return class_list_[i]->clone();
            }
            return NULL;
        }
    };

    static ScriptInitializerBuilder SCRIPT_INITIALIZER_LIST;
}

ScriptInitializer* ScriptInitializer::create(int language_id) {
    ScriptInitializer* ret = SCRIPT_INITIALIZER_LIST.get(language_id);
    if (ret == NULL) {
        LOG(ERROR) << "initializer for script language " << language_id << "is not found";
    }
    return ret;
}
