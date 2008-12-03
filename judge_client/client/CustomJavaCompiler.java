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

import java.io.File;
import java.io.IOException;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaFileObject.Kind;

public class CustomJavaCompiler {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Invalid arg length " + args.length);
            System.exit(-1);
        }
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager stdFileManager = compiler.getStandardFileManager(null, null, null);
        JavaFileManager fileManager = new CustomJavaFileManager(stdFileManager);
        System.out.println(new File(args[0]).getAbsolutePath());
        System.out.println(compiler.getTask(null, fileManager, null, null, null,
                                            stdFileManager.getJavaFileObjects(args[0])).call());
    }

    private static class CustomJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

        private int outputFileCount = 0;

        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) throws IOException {
            if (++outputFileCount > 32) {
                throw new IOException("Too many output files");
            }
            return super.getJavaFileForOutput(location, className, kind, sibling);
        }

        public CustomJavaFileManager(StandardJavaFileManager fileManager) {
            super(fileManager);
        }
    }
};
