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

package cn.edu.zju.acm.onlinejudge.action;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.edu.zju.acm.onlinejudge.util.ImageManager;

/**
 * <p>
 * ShowImageAction
 * </p>
 * 
 * 
 * @author Zhang, Zheng
 * @version 2.0
 */
public class ShowImageAction extends BaseAction {

    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public ShowImageAction() {
    // empty
    }

    /**
     * ShowSourceAction.
     * 
     * @param mapping
     *            action mapping
     * @param form
     *            action form
     * @param request
     *            http servlet request
     * @param response
     *            http servlet response
     * 
     * @return action forward instance
     * 
     * @throws Exception
     *             any errors happened
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, ContextAdapter context) throws Exception {
        HttpServletResponse response = context.getResponse();
        String name = context.getRequest().getParameter("name");
        byte[] image = ImageManager.getInstance().getImage(name);
        if (image == null) {
            response.sendError(404);
            return null;
        }
        String contentType = this.getImageContentType(name);
        if (contentType != null) {
            response.setContentType(contentType);
        }
        response.getOutputStream().write(image);
        response.getOutputStream().close();

        return null;

    }

    private String getImageContentType(String name) {
        int p = name.lastIndexOf('.');
        if (p == -1 || p == name.length() - 1) {
            return null;
        }
        String ext = name.substring(p + 1);

        if ("bmp".equalsIgnoreCase(ext)) {
            return "image/x-bmp";
        } else if ("tif".equalsIgnoreCase(ext)) {
            return "image/tiff";
        } else if ("png".equalsIgnoreCase(ext)) {
            return "image/x-png";
        } else if ("jpg".equalsIgnoreCase(ext) || "jpeg".equalsIgnoreCase(ext)) {
            return "image/jpeg";
        } else if ("gif".equalsIgnoreCase(ext)) {
            return "image/gif";
        }
        return null;
    }

}
