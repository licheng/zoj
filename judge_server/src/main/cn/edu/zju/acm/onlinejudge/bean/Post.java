/*
 * Copyright 2007 Zhang, Zheng <oldbig@gmail.com>
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

package cn.edu.zju.acm.onlinejudge.bean;

/**
 * <p>Post bean.</p>
 *
 * @author ZOJDEV
 *
 * @version 2.0
 */
public class Post {

    /**
     * <p>Represents id.</p>
     */
    private long id = -1;

    /**
     * <p>Represents threadId.</p>
     */
    private long threadId = -1;
    
    /**
     * <p>Represents userProfileId.</p>
     */
    private long userProfileId = -1;

    /**
     * <p>Represents content.</p>
     */
    private String content = null;

    /**
     * <p>Empty constructor.</p>
     */
    public Post() {
    }

    /**
     * <p>Gets id.</p>
     *
     * @return id
     */
    public long getId() {
        return this.id;
    }

    /**
     * <p>Sets id.</p>
     *
     * @param id id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * <p>Gets threadId.</p>
     *
     * @return threadId
     */
    public long getThreadId() {
        return this.threadId;
    }

    /**
     * <p>Sets threadId.</p>
     *
     * @param threadId threadId
     */
    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    /**
     * <p>Gets userProfileId.</p>
     *
     * @return userProfileId
     */
    public long getUserProfileId() {
        return this.userProfileId;
    }

    /**
     * <p>Sets userProfileId.</p>
     *
     * @param userProfileId userProfileId
     */
    public void setUserProfileId(long userProfileId) {
        this.userProfileId = userProfileId;
    }

    /**
     * <p>Gets content.</p>
     *
     * @return content
     */
    public String getContent() {
        return this.content;
    }

    /**
     * <p>Sets content.</p>
     *
     * @param content content
     */
    public void setContent(String content) {
        this.content = content;
    }

}
