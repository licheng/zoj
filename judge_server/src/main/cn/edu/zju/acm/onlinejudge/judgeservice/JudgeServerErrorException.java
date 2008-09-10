/*
 * Copyright 2007 Xu, Chuan <xuchuan@gmail.com>
 * 
 * This file is part of ZOJ.
 * 
 * ZOJ is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either revision 3 of the License, or (at your option) any later revision.
 * 
 * ZOJ is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with ZOJ. if not, see
 * <http://www.gnu.org/licenses/>.
 */
package cn.edu.zju.acm.onlinejudge.judgeservice;

import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;

public class JudgeServerErrorException extends Exception {

    public JudgeServerErrorException(String message) {
        super(message);
    }

    public JudgeServerErrorException(String message, Exception e) {
        super(message, e);
    }

    public JudgeServerErrorException(PersistenceException e) {
        super(e);
    }
}
