/*
 * Copyright 2008 Chen, Zhengguang <cerrorism@gmail.com>
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

import java.util.ArrayList;
import java.util.List;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.util.ContestManager;

/**
 * <p>
 * SearchProblemAction
 * </p>
 * 
 * 
 * @author Chen, Zhengguang
 * @version 2.0
 */
public class SearchProblemAction extends BaseAction {

	@Override
	protected ActionForward execute(ActionMapping mapping, ActionForm form,
			ContextAdapter context) throws Exception {
		//System.out.println("SearchProblemAction begin");
		List<Problem> TitleQueryResult = new ArrayList<Problem>();
		List<Problem> AuthorQueryResult = new ArrayList<Problem>();
		List<Problem> SourceQueryResult = new ArrayList<Problem>();

		AbstractContest contest = context.getContest();
		String query = context.getRequest().getParameter("query").toLowerCase();
		String temp = context.getRequest().getParameter("titlefrom");
		int titlefrom = Integer.parseInt(temp);
		temp = context.getRequest().getParameter("authorfrom");
		int authorfrom = Integer.parseInt(temp);
		temp = context.getRequest().getParameter("sourcefrom");
		int sourcefrom = Integer.parseInt(temp);
		
		List<Problem> problems =
            ContestManager.getInstance().getContestProblems(contest.getId());
		for(int i=0;i<problems.size();++i) {
			Problem p=problems.get(i);
			if(p.getTitle()!=null) {
				if(p.getTitle().toLowerCase().indexOf(query)>=0) {
					TitleQueryResult.add(p);
				}
			}
			if(p.getAuthor()!=null) {
				if(p.getAuthor().toLowerCase().indexOf(query)>=0) {
					AuthorQueryResult.add(p);
				}
			}
			if(p.getSource()!=null) {
				if(p.getSource().toLowerCase().indexOf(query)>=0) {
					SourceQueryResult.add(p);
				}
			}
		}
		
		context.setAttribute("TitleQueryResultCount", TitleQueryResult.size());
		if(titlefrom*50+49>TitleQueryResult.size()) {
		context.setAttribute("TitleQueryResult", TitleQueryResult.subList(titlefrom*50, TitleQueryResult.size()));
		} else {
			context.setAttribute("TitleQueryResult", TitleQueryResult.subList(titlefrom*50, titlefrom*50+49));
		}
		context.setAttribute("titlefrom", titlefrom);
		
		context.setAttribute("AuthorQueryResultCount", AuthorQueryResult.size());
		if(authorfrom*50+49>AuthorQueryResult.size()) {
			context.setAttribute("AuthorQueryResult", AuthorQueryResult.subList(authorfrom*50, AuthorQueryResult.size()));
			} else {
				context.setAttribute("AuthorQueryResult", AuthorQueryResult.subList(authorfrom*50, authorfrom*50+49));
			}
		context.setAttribute("authorfrom", authorfrom);
		
		context.setAttribute("SourceQueryResultCount", SourceQueryResult.size());
		if(titlefrom*50+49>SourceQueryResult.size()) {
			context.setAttribute("SourceQueryResult", SourceQueryResult.subList(sourcefrom*50, SourceQueryResult.size()));
			} else {
				context.setAttribute("SourceQueryResult", SourceQueryResult.subList(sourcefrom*50, sourcefrom*50+49));
			}
		context.setAttribute("sourcefrom", sourcefrom);
		context.setAttribute("query", query);
		//System.out.println("SearchProblemAction end");
		return this.handleSuccess(mapping, context, "success");
	}
}
