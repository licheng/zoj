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

package cn.edu.zju.acm.onlinejudge.bean.request;

/**
 * <p>
 * </p>
 * 
 * @poseidon-object-id [I81be8am1050b02b322mm507d]
 */
public class ProblemCriteria {

    /**
     * <p>
     * Represents ...
     * </p>
     * 
     * @poseidon-object-id [I81be8am1050b02b322mm506a]
     */
    private Long contestId;

    /**
     * <p>
     * Represents ...
     * </p>
     * 
     * @poseidon-object-id [I81be8am1050b02b322mm5059]
     */
    private String title;

    /**
     * <p>
     * Represents ...
     * </p>
     * 
     * @poseidon-object-id [I81be8am1050b02b322mm5048]
     */
    private String author;

    /**
     * <p>
     * Represents ...
     * </p>
     * 
     * @poseidon-object-id [I81be8am1050b02b322mm5037]
     */
    private Long userProfileId;

    /**
     * <p>
     * Represents ...
     * </p>
     * 
     * @poseidon-object-id [I81be8am1050b02b322mm5026]
     */
    private Boolean solved;

    /**
     * <p>
     * Represents ...
     * </p>
     * 
     * @poseidon-object-id [I81be8am1050b02b322mm5015]
     */
    private Boolean submitted;

    /**
     * <p>
     * Represents ...
     * </p>
     * 
     * @poseidon-object-id [I81be8am1050b02b322mm5004]
     */
    private String content;

    /**
     * 
     * @return
     */
    public Long getContestId() {
        return this.contestId;
    }

    public void setContestId(Long contestId) {
        this.contestId = contestId;
    }

    public Long getUserProfileId() {
        return this.userProfileId;
    }

    public void setUserProfileId(Long userProfileId) {
        this.userProfileId = userProfileId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = this.cal(this.author, hash);
        hash = this.cal(this.content, hash);
        hash = this.cal(this.contestId, hash);
        hash = this.cal(this.solved, hash);
        hash = this.cal(this.submitted, hash);
        hash = this.cal(this.title, hash);
        hash = this.cal(this.userProfileId, hash);
        return hash;
    }

    private int cal(Object obj, int hash) {
        hash = hash >>> 3;
        if (obj == null) {
            return hash ^ 1234567891;
        } else {
            return hash ^ obj.hashCode();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ProblemCriteria)) {
            return false;
        }
        ProblemCriteria that = (ProblemCriteria) obj;
        if (!this.equals(this.author, that.author)) {
            return false;
        }
        if (!this.equals(this.content, that.content)) {
            return false;
        }
        if (!this.equals(this.contestId, that.contestId)) {
            return false;
        }
        if (!this.equals(this.solved, that.solved)) {
            return false;
        }
        if (!this.equals(this.submitted, that.submitted)) {
            return false;
        }
        if (!this.equals(this.title, that.title)) {
            return false;
        }
        if (!this.equals(this.userProfileId, that.userProfileId)) {
            return false;
        }
        return true;

    }

    private boolean equals(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        }
        return o1.equals(o2);
    }
}
