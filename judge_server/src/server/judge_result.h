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

#ifndef __JUDGE_RESULT_H
#define __JUDGE_RESULT_H

#define COMPILING 1
#define RUNNING 2
#define RUNTIME_ERROR 3
#define WRONG_ANSWER 4
#define ACCEPTED 5
#define TIME_LIMIT_EXCEEDED 6
#define MEMORY_LIMIT_EXCEEDED 7
#define OUTPUT_LIMIT_EXCEEDED 10
#define COMPILATION_ERROR 12
#define PRESENTATION_ERROR 13
#define INTERNAL_ERROR 14
#define FLOATING_POINT_ERROR 15
#define SEGMENTATION_FAULT 16
#define JUDGING 19
#define READY 900
#define PROBLEM_EXIST 901
#define NO_SUCH_PROBLEM 902
#define SERVER_ERROR 910

#endif
