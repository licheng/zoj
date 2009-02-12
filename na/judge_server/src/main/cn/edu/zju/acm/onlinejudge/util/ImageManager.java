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

package cn.edu.zju.acm.onlinejudge.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import cn.edu.zju.acm.onlinejudge.persistence.PersistenceCreationException;
import cn.edu.zju.acm.onlinejudge.util.cache.Cache;

public class ImageManager {

    private final Cache<byte[]> imageCache;

    /**
     * ContestManager.
     */
    private static ImageManager instance = null;

    /**
     * <p>
     * Constructor of ContestManager class.
     * </p>
     * 
     * @throws PersistenceCreationException
     * 
     */
    private ImageManager() throws PersistenceCreationException {
        this.imageCache = new Cache<byte[]>(60000, 30);
    }

    /**
     * Gets the singleton instance.
     * 
     * @return the singleton instance.
     * @throws PersistenceCreationException
     */
    public static ImageManager getInstance() throws PersistenceCreationException {
        if (ImageManager.instance == null) {
            synchronized (ImageManager.class) {
                if (ImageManager.instance == null) {
                    ImageManager.instance = new ImageManager();
                }
            }
        }
        return ImageManager.instance;
    }

    public byte[] getImage(String name) {
        if (name == null || name.trim().length() == 0) {
            return null;
        }

        synchronized (this.imageCache) {
            byte[] image = this.imageCache.get(name);
            if (image == null) {
                image = this.getImageFile(name);
                this.imageCache.put(name, image);
            }
            return image;
        }
    }

    private byte[] getImageFile(String name) {

        File file = new File(ConfigManager.getImagePath(), name);
        FileInputStream in = null;
        try {
            if (!file.isFile() || !file.canRead()) {
                return null;
            }

            in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[102400];
            while (true) {
                int l = in.read(buffer);
                if (l == -1) {
                    break;
                }
                out.write(buffer, 0, l);
            }
            return out.toByteArray();
        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

}
