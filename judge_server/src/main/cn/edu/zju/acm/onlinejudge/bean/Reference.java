/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.bean;

import cn.edu.zju.acm.onlinejudge.bean.enumeration.ReferenceType;

/**
 * <p>Reference bean.</p>
 *
 * @author ZOJDEV
 *
 * @version 2.0
 */
public class Reference {

    /**
     * <p>Represents id.</p>
     */
    private long id = -1;

    /**
     * <p>Represents size.</p>
     */
    private long size = -1;

    /**
     * <p>Represents referenceType.</p>
     */
    private ReferenceType referenceType = null;

    /**
     * <p>Represents contentType.</p>
     */
    private String contentType = null;

    /**
     * <p>Represents name.</p>
     */
    private String name = null;

    /**
     * <p>Represents compressed.</p>
     */
    private boolean compressed = false;

    /**
     * <p>Represents content.</p>
     */
    private byte[] content = null;

    /**
     * <p>Empty constructor.</p>
     */
    public Reference() {
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
     * <p>Gets size.</p>
     *
     * @return size
     */
    public long getSize() {
        return this.size;
    }

    /**
     * <p>Sets size.</p>
     *
     * @param size size
     */
    public void setSize(long size) {
        this.size = size;
    }

    /**
     * <p>Gets referenceType.</p>
     *
     * @return referenceType
     */
    public ReferenceType getReferenceType() {
        return this.referenceType;
    }

    /**
     * <p>Sets referenceType.</p>
     *
     * @param referenceType referenceType
     */
    public void setReferenceType(ReferenceType referenceType) {
        this.referenceType = referenceType;
    }

    /**
     * <p>Gets contentType.</p>
     *
     * @return contentType
     */
    public String getContentType() {
        return this.contentType;
    }

    /**
     * <p>Sets contentType.</p>
     *
     * @param contentType contentType
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * <p>Gets name.</p>
     *
     * @return name
     */
    public String getName() {
        return this.name;
    }

    /**
     * <p>Sets name.</p>
     *
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>Gets compressed.</p>
     *
     * @return compressed
     */
    public boolean isCompressed() {
        return this.compressed;
    }

    /**
     * <p>Sets compressed.</p>
     *
     * @param compressed compressed
     */
    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }

    /**
     * <p>Gets content.</p>
     *
     * @return content
     */
    public byte[] getContent() {
        return this.content;
    }

    /**
     * <p>Sets content.</p>
     *
     * @param content content
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

}
