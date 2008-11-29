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

#ifndef __KMMON_H
#define __KMMON_H

#define __NR_kmmon 251
#define KMMON_MASK 0x08000000
#define KMMON_TRACEME 0
#define KMMON_CONTINUE 1
#define KMMON_KILL 2
#define KMMON_READMEM 3
#define KMMON_GETREG 4
#define KMMON_CLEAR_ORPHANS 5
#define KMMON_SIG 51

#endif
