<!--
function MM_popupMsg(msg) { //v1.0
  alert(msg);
}

function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}

function MM_swapImgRestore() { //v3.0
  var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}

function MM_findObj(n, d) { //v4.01
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && d.getElementById) x=d.getElementById(n); return x;
}

function MM_swapImage() { //v3.0
  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
}

function MM_goToURL() { //v3.0
  var i, args=MM_goToURL.arguments; document.MM_returnValue = false;
  for (i=0; i<(args.length-1); i+=2) eval(args[i]+".location='"+args[i+1]+"'");
}

function Gen_link(user, para, path) {
	var showstr = "<table height=29 width=100% border=0 cellspacing=0 cellpadding=0>"
	+ "<td height=29 background='" + path + "/image/link_bg1.gif'><FONT class=largeblack>&nbsp;";

	if(user=="admin")
	{
		var homestr = "<a class=other href='./index_wishingbone.html'>Home</a> | ";
		var contstr = "<a class=other href='./contest_list.html'>Contests</a> | ";
		var probstr = "<a class=other href='./prob_list.html'>Problems</a> | ";
		var forumstr = "<a class=other href='#'>Forum</a>";
		var adminstr = "<a class=other href='./admin_sys.html'>Admin</a>";

		if(para=="Home") {
			showstr += "<font class=goldchar>Home</font> | " + contstr + probstr + forumstr + " | " + adminstr;
		}
		else if(para=="Contests") {
			showstr += homestr + "<font class=goldchar>Contests</font> | " + probstr + forumstr + " | " + adminstr;
		}
		else if(para=="Problems") {
			showstr += homestr + contstr + "<font class=goldchar>Problems</font> | " + forumstr + " | " + adminstr;
		}
		else if(para=="Forum") {
			showstr += homestr + contstr + probstr + "<font class=goldchar>Forum</font> | " + adminstr;
		}
		else if(para=="Admin") {
			showstr += homestr + contstr + probstr + forumstr + " | " + "<font class=goldchar>Admin</font>";
		}
	}
	else if(user=="user")
	{
		var homestr = "<a class=other href='./index_user.html'>Home</a> | ";
		var contstr = "<a class=other href='./user_contest_list.html'>Contests</a> | ";
		var probstr = "<a class=other href='./user_prob_list.html'>Problems</a> | ";
		var forumstr = "<a class=other href='#'>Forum</a>";

		if(para=="Home") {
			showstr += "<font class=goldchar>Home</font> | " + contstr + probstr + forumstr;
		}
		else if(para=="Contests") {
			showstr += homestr + "<font class=goldchar>Contests</font> | " + probstr + forumstr;
		}
		else if(para=="Problems") {
			showstr += homestr + contstr + "<font class=goldchar>Problems</font> | " + forumstr;
		}
		else if(para=="Forum") {
			showstr += homestr + contstr + probstr + "<font class=goldchar>Forum</font>";
		}
	}
	else if(user=="anonymouse")
	{
		var homestr = "<a class=other href='./index.html'>Home</a> | ";
		var contstr = "<a class=other href='./anony_contest_list.html'>Contests</a> | ";
		var probstr = "<a class=other href='./anony_prob_list.html'>Problems</a> | ";
		var forumstr = "<a class=other href='#'>Forum</a>";

		if(para=="Home") {
			showstr += "<font class=goldchar>Home</font> | " + contstr + probstr + forumstr;
		}
		else if(para=="Contests") {
			showstr += homestr + "<font class=goldchar>Contests</font> | " + probstr + forumstr;
		}
		else if(para=="Problems") {
			showstr += homestr + contstr + "<font class=goldchar>Problems</font> | " + forumstr;
		}
		else if(para=="Forum") {
			showstr += homestr + contstr + probstr + "<font class=goldchar>Forum</font>";
		}
		else if(para=="Login") {
			showstr += homestr + contstr + probstr + forumstr;
		}
	}
	showstr += "</FONT></td></table>";

	document.write (showstr);
}

function Gen_login(stat, user_id, role,path) {
	var showstr = "<table width=100% height=24 border=0 cellpadding=0 cellspacing=0><tr>"
	+"<td width=33%  height=29 valign=middle background='" + path + "/image/link_bg2.gif'>&nbsp;<font class=largewhite> Welcome to ZOJ!</font></td>";

	if(stat=="login")
	{
		showstr += "<td width=65% background='" + path + "/image/link_bg2.gif'><div align=right><font class=username>"
		+ user_id + "</font><font class=smallwhite>, logged as "+ role
		+ " | <a href='./index.html'>Logout</a></font></div></td>"
		+ "<td width=2% background='" + path + "/image/link_bg2.gif'>&nbsp;";
	}
	else
	{
		showstr += "<td width=65% background='" + path + "/image/link_bg2.gif'><div align=right><font class=smallwhite><a href='" + path + "/action/loginpage'>Login</a> "
		+ "| <a href='./register.html'>Register</a></font></div></td><td width=2% background='" + path + "/image/link_bg2.gif'>&nbsp;";
	}
	showstr += "</td></tr></table>";

	document.write (showstr);
}

function Gen_foot() {
	var showstr = "<hr width=100%><div align=center><br>"
	+ "Copyright @ 2001-2005, Zhejiang University ACM/ICPC Team, All rights reserved.</div><br>";
	document.write (showstr);
}

function Gen_subinter(path,bg, texts, level, sel, linker) {
	var four_blank = "&nbsp;&nbsp;&nbsp;&nbsp;";
	var showstr="<tr><td width=10% height=30 "+bg+"></td>"+"<td  width=78% "+bg+">";

	if(level==0)
	{
		if(sel==0)
		{
			showstr+="<strong><a href="+linker+">"+texts+"</a></strong></td>"
			+ "<td "+bg+"><img src='" + path + "/image/arrow_sub1.gif' width=12 height=12></td></tr>";
		}
		else if(sel==1)
		{
			showstr+="<strong><a href="+linker+">"+texts+"</a></strong></td>"
			+ "<td "+bg+"><img src='" + path + "/image/arrow_sub2.gif' width=12 height=12></td></tr>";
		}
	}
	else if(level==1)
	{
		if(bg!="background='" + path + "/image/buttonbg3.gif'")
		{
			showstr+=four_blank+"<a href="+linker+">"+texts+"</a></td>"
			+ "<td "+bg+">&nbsp;</td></tr>";
		}
		else
		{
			showstr+=four_blank+texts+"</td>"
			+ "<td "+bg+">&nbsp;</td></tr>";
		}
	}
	return showstr;
}

function Gen_submenu(menu, submenu, path) {
	var sub_bg1 = "background='" + path + "/image/buttonbg1.gif'";
	var sub_bg2 = "background='" + path + "/image/buttonbg2.gif'";
	var sub_bg3 = "background='" + path + "/image/buttonbg3.gif'";

	var cpc = "<tr><td height=10></td><td></td><td></td></tr>"
	+ "</table></td></tr><tr height=20><td></td></tr>"
	+ "<tr><td height=110 align=center valign=bottom><img src='" + path + "/image/cpc_acm.jpg' width=148 height=119></td></tr>"
	+ "<tr><td height=30></td></tr>"
	+ "<tr><td height=20></td></tr></table></td>";

	var showstr = "<td width=169 align=center valign=top class=bg6>"
	+ "<table width=100% height=100% border=0 align=center cellpadding=0 cellspacing=0><tr>"
	+ "<td align=center valign=top> <table width=100% border=0 align=center cellpadding=0 cellspacing=0>";

	if(menu=="anony_home")
	{
		if(submenu=="home")
		{
			showstr += Gen_subinter(path,sub_bg1, "Home", 0, 1, "index.html")
			+ Gen_subinter(path,sub_bg2, "FAQ", 1, 0, "faq.html")
			+ Gen_subinter(path,sub_bg2, "About This Site", 1, 0, "about.html")
			+ Gen_subinter(path,sub_bg2, "Contact Us", 1, 0, "email_us.html");
		}
		else if(submenu=="faq")
		{
			showstr += Gen_subinter(path,sub_bg1, "Home", 0, 1, "index.html")
			+ Gen_subinter(path,sub_bg3, "FAQ", 1, 0, "faq.html")
			+ Gen_subinter(path,sub_bg2, "About This Site", 1, 0, "about.html")
			+ Gen_subinter(path,sub_bg2, "Contact Us", 1, 0, "email_us.html");
		}
		else if(submenu=="about")
		{
			showstr += Gen_subinter(path,sub_bg1, "Home", 0, 1, "index.html")
			+ Gen_subinter(path,sub_bg2, "FAQ", 1, 0, "faq.html")
			+ Gen_subinter(path,sub_bg3, "About This Site", 1, 0, "about.html")
			+ Gen_subinter(path,sub_bg2, "Contact Us", 1, 0, "email_us.html");
		}
		else if(submenu=="contact")
		{
			showstr += Gen_subinter(path,sub_bg1, "Home", 0, 1, "index.html")
			+ Gen_subinter(path,sub_bg2, "FAQ", 1, 0, "faq.html")
			+ Gen_subinter(path,sub_bg2, "About This Site", 1, 0, "about.html")
			+ Gen_subinter(path,sub_bg3, "Contact Us", 1, 0, "email_us.html");
		}
	}
	else if(menu=="anony_contest")
	{
		if(submenu=="list")
		{
			showstr += Gen_subinter(path,sub_bg1, "Contests", 0, 1, "anony_contest_list.html")
			+ Gen_subinter(path,sub_bg3, "Select Contest", 1, 0, "#");
		}
		else if(submenu=="info")
		{
			showstr += Gen_subinter(path,sub_bg1, "Contest Select", 0, 1, "anony_contest_list.html")
			+ Gen_subinter(path,sub_bg3, "Information", 1, 0, "anony_contest.html")
			+ Gen_subinter(path,sub_bg2, "Problems", 1, 0, "anony_contest_problem.html")
			+ Gen_subinter(path,sub_bg2, "Runs", 1, 0, "anony_contest_runs.html")
			+ Gen_subinter(path,sub_bg2, "Statistics", 1, 0, "anony_contest_statistics.html")
			+ Gen_subinter(path,sub_bg2, "Ranklist", 1, 0, "anony_contest_rank.html");
		}
		else if(submenu=="info")
		{
			showstr += Gen_subinter(path,sub_bg1, "Contest Select", 0, 1, "anony_contest_list.html")
			+ Gen_subinter(path,sub_bg3, "Information", 1, 0, "anony_contest.html")
			+ Gen_subinter(path,sub_bg2, "Problems", 1, 0, "anony_contest_problem.html")
			+ Gen_subinter(path,sub_bg2, "Runs", 1, 0, "anony_contest_runs.html")
			+ Gen_subinter(path,sub_bg2, "Statistics", 1, 0, "anony_contest_statistics.html")
			+ Gen_subinter(path,sub_bg2, "Ranklist", 1, 0, "anony_contest_rank.html");
		}
		else if(submenu=="prob")
		{
			showstr += Gen_subinter(path,sub_bg1, "Contest Select", 0, 1, "anony_contest_list.html")
			+ Gen_subinter(path,sub_bg2, "Information", 1, 0, "anony_contest.html")
			+ Gen_subinter(path,sub_bg3, "Problems", 1, 0, "anony_contest_problem.html")
			+ Gen_subinter(path,sub_bg2, "Runs", 1, 0, "anony_contest_runs.html")
			+ Gen_subinter(path,sub_bg2, "Statistics", 1, 0, "anony_contest_statistics.html")
			+ Gen_subinter(path,sub_bg2, "Ranklist", 1, 0, "anony_contest_rank.html");
		}
		else if(submenu=="runs")
		{
			showstr += Gen_subinter(path,sub_bg1, "Contest Select", 0, 1, "anony_contest_list.html")
			+ Gen_subinter(path,sub_bg2, "Information", 1, 0, "anony_contest.html")
			+ Gen_subinter(path,sub_bg2, "Problems", 1, 0, "anony_contest_problem.html")
			+ Gen_subinter(path,sub_bg3, "Runs", 1, 0, "anony_contest_runs.html")
			+ Gen_subinter(path,sub_bg2, "Statistics", 1, 0, "anony_contest_statistics.html")
			+ Gen_subinter(path,sub_bg2, "Ranklist", 1, 0, "anony_contest_rank.html");
		}
		else if(submenu=="stat")
		{
			showstr += Gen_subinter(path,sub_bg1, "Contest Select", 0, 1, "anony_contest_list.html")
			+ Gen_subinter(path,sub_bg2, "Information", 1, 0, "anony_contest.html")
			+ Gen_subinter(path,sub_bg2, "Problems", 1, 0, "anony_contest_problem.html")
			+ Gen_subinter(path,sub_bg2, "Runs", 1, 0, "anony_contest_runs.html")
			+ Gen_subinter(path,sub_bg3, "Statistics", 1, 0, "anony_contest_statistics.html")
			+ Gen_subinter(path,sub_bg2, "Ranklist", 1, 0, "anony_contest_rank.html");
		}
		else if(submenu=="rank")
		{
			showstr += Gen_subinter(path,sub_bg1, "Contest Select", 0, 1, "anony_contest_list.html")
			+ Gen_subinter(path,sub_bg2, "Information", 1, 0, "anony_contest.html")
			+ Gen_subinter(path,sub_bg2, "Problems", 1, 0, "anony_contest_problem.html")
			+ Gen_subinter(path,sub_bg2, "Runs", 1, 0, "anony_contest_runs.html")
			+ Gen_subinter(path,sub_bg2, "Statistics", 1, 0, "anony_contest_statistics.html")
			+ Gen_subinter(path,sub_bg3, "Ranklist", 1, 0, "anony_contest_rank.html");
		}
		else if(submenu=="edit")
		{
			showstr += Gen_subinter(path,sub_bg1, "Contest Select", 0, 1, "anony_contest_list.html")
			+ Gen_subinter(path,sub_bg2, "Information", 1, 0, "anony_contest.html")
			+ Gen_subinter(path,sub_bg2, "Problems", 1, 0, "anony_contest_problem.html")
			+ Gen_subinter(path,sub_bg2, "Runs", 1, 0, "anony_contest_runs.html")
			+ Gen_subinter(path,sub_bg2, "Statistics", 1, 0, "anony_contest_statistics.html")
			+ Gen_subinter(path,sub_bg2, "Ranklist", 1, 0, "anony_contest_rank.html");
		}
		else if(submenu=="none")
		{
			showstr += Gen_subinter(path,sub_bg1, "Contest Select", 0, 1, "anony_contest_list.html")
			+ Gen_subinter(path,sub_bg2, "Information", 1, 0, "anony_contest.html")
			+ Gen_subinter(path,sub_bg2, "Problems", 1, 0, "anony_contest_problem.html")
			+ Gen_subinter(path,sub_bg2, "Runs", 1, 0, "anony_contest_runs.html")
			+ Gen_subinter(path,sub_bg2, "Statistics", 1, 0, "anony_contest_statistics.html")
			+ Gen_subinter(path,sub_bg2, "Ranklist", 1, 0, "anony_contest_rank.html");
		}
	}
	else if(menu=="anony_problem")
	{
		if(submenu=="list")
		{
			showstr += Gen_subinter(path,sub_bg1, "Problem Sets", 0, 1, "anony_prob_list.html")
			+ Gen_subinter(path,sub_bg3, "Select Problem", 1, 0, "anony_prob_list.html")
			+ Gen_subinter(path,sub_bg2, "Problem Search", 1, 0, "anony_pro_adv_search.html");
		}
		else if(submenu=="search")
		{
			showstr += Gen_subinter(path,sub_bg1, "Problem Sets", 0, 1, "anony_prob_list.html")
			+ Gen_subinter(path,sub_bg2, "Select Problem", 1, 0, "anony_prob_list.html")
			+ Gen_subinter(path,sub_bg3, "Problem Search", 1, 0, "anony_pro_adv_search.html");
		}
		else if(submenu=="add")
		{
			showstr += Gen_subinter(path,sub_bg1, "Problem Sets", 0, 1, "anony_prob_list.html")
			+ Gen_subinter(path,sub_bg2, "Select Problem", 1, 0, "anony_prob_list.html")
			+ Gen_subinter(path,sub_bg2, "Problem Search", 1, 0, "anony_pro_adv_search.html");
		}
		else if(submenu=="none")
		{
			showstr += Gen_subinter(path,sub_bg1, "Problem Sets", 0, 1, "anony_prob_list.html")
			+ Gen_subinter(path,sub_bg2, "Select Problem", 1, 0, "anony_prob_list.html")
			+ Gen_subinter(path,sub_bg2, "Problem Search", 1, 0, "anony_pro_adv_search.html");
		}
	}
	else if(menu=="anony_forum")
	{
	}
	else if(menu=="user_home" || menu=="admin_home")
	{
		if(submenu=="home")
		{
			showstr += Gen_subinter(path,sub_bg1, "Home", 0, 1, "index_wishingbone.html")
			+ Gen_subinter(path,sub_bg2, "Edit Profile", 1, 0, "profile.html")
			+ Gen_subinter(path,sub_bg2, "Edit Proference", 1, 0, "preference.html")
			+ Gen_subinter(path,sub_bg2, "FAQ", 1, 0, "user_faq.html")
			+ Gen_subinter(path,sub_bg2, "About This Site", 1, 0, "user_about.html")
			+ Gen_subinter(path,sub_bg2, "Contact Us", 1, 0, "user_email_us.html");
		}
		else if(submenu=="profile")
		{
			showstr += Gen_subinter(path,sub_bg1, "Home", 0, 1, "index_wishingbone.html")
			+ Gen_subinter(path,sub_bg3, "Edit Profile", 1, 0, "profile.html")
			+ Gen_subinter(path,sub_bg2, "Edit Proference", 1, 0, "preference.html")
			+ Gen_subinter(path,sub_bg2, "FAQ", 1, 0, "user_faq.html")
			+ Gen_subinter(path,sub_bg2, "About This Site", 1, 0, "user_about.html")
			+ Gen_subinter(path,sub_bg2, "Contact Us", 1, 0, "user_email_us.html");
		}
		else if(submenu=="preference")
		{
			showstr += Gen_subinter(path,sub_bg1, "Home", 0, 1, "index_wishingbone.html")
			+ Gen_subinter(path,sub_bg2, "Edit Profile", 1, 0, "profile.html")
			+ Gen_subinter(path,sub_bg3, "Edit Proference", 1, 0, "preference.html")
			+ Gen_subinter(path,sub_bg2, "FAQ", 1, 0, "user_faq.html")
			+ Gen_subinter(path,sub_bg2, "About This Site", 1, 0, "user_about.html")
			+ Gen_subinter(path,sub_bg2, "Contact Us", 1, 0, "user_email_us.html");
		}
		else if(submenu=="faq")
		{
			showstr += Gen_subinter(path,sub_bg1, "Home", 0, 1, "index_wishingbone.html")
			+ Gen_subinter(path,sub_bg2, "Edit Profile", 1, 0, "profile.html")
			+ Gen_subinter(path,sub_bg2, "Edit Proference", 1, 0, "preference.html")
			+ Gen_subinter(path,sub_bg3, "FAQ", 1, 0, "user_faq.html")
			+ Gen_subinter(path,sub_bg2, "About This Site", 1, 0, "user_about.html")
			+ Gen_subinter(path,sub_bg2, "Contact Us", 1, 0, "user_email_us.html");
		}
		else if(submenu=="about")
		{
			showstr += Gen_subinter(path,sub_bg1, "Home", 0, 1, "index_wishingbone.html")
			+ Gen_subinter(path,sub_bg2, "Edit Profile", 1, 0, "profile.html")
			+ Gen_subinter(path,sub_bg2, "Edit Proference", 1, 0, "preference.html")
			+ Gen_subinter(path,sub_bg2, "FAQ", 1, 0, "user_faq.html")
			+ Gen_subinter(path,sub_bg3, "About This Site", 1, 0, "user_about.html")
			+ Gen_subinter(path,sub_bg2, "Contact Us", 1, 0, "user_email_us.html");
		}
		else if(submenu=="contact")
		{
			showstr += Gen_subinter(path,sub_bg1, "Home", 0, 1, "index_wishingbone.html")
			+ Gen_subinter(path,sub_bg2, "Edit Profile", 1, 0, "profile.html")
			+ Gen_subinter(path,sub_bg2, "Edit Proference", 1, 0, "preference.html")
			+ Gen_subinter(path,sub_bg2, "FAQ", 1, 0, "user_faq.html")
			+ Gen_subinter(path,sub_bg2, "About This Site", 1, 0, "user_about.html")
			+ Gen_subinter(path,sub_bg3, "Contact Us", 1, 0, "user_email_us.html");
		}
	}
	else if(menu=="user_contest" || menu=="admin_contest")
	{
		if(submenu=="list")
		{
			showstr += Gen_subinter(path,sub_bg1, "Contests", 0, 1, "contest_list.html")
			+ Gen_subinter(path,sub_bg3, "Select Contest", 1, 0, "#");
		}
		else if(submenu=="info")
		{
			showstr += Gen_subinter(path,sub_bg1, "Contest Select", 0, 1, "contest_list.html")
			+ Gen_subinter(path,sub_bg3, "Information", 1, 0, "contest.html")
			+ Gen_subinter(path,sub_bg2, "Problems", 1, 0, "contest_problem.html")
			+ Gen_subinter(path,sub_bg2, "Runs", 1, 0, "contest_runs.html")
			+ Gen_subinter(path,sub_bg2, "Statistics", 1, 0, "contest_statistics.html")
			+ Gen_subinter(path,sub_bg2, "Ranklist", 1, 0, "contest_rank.html")
			+ Gen_subinter(path,sub_bg2, "Clarification", 1, 0, "#");

			if(menu=="admin_contest")
			{
				showstr += Gen_subinter(path,sub_bg2, "Rejudge", 1, 0, "contest_rejudge.html")
				+ Gen_subinter(path,sub_bg2, "Edit Contest", 1, 0, "edit_contest.html")
				+ Gen_subinter(path,sub_bg2, "Add Problem", 1, 0, "add_problem.html");
			}
		}
		else if(submenu=="info")
		{
			showstr += Gen_subinter(path,sub_bg1, "Contest Select", 0, 1, "contest_list.html")
			+ Gen_subinter(path,sub_bg3, "Information", 1, 0, "contest.html")
			+ Gen_subinter(path,sub_bg2, "Problems", 1, 0, "contest_problem.html")
			+ Gen_subinter(path,sub_bg2, "Runs", 1, 0, "contest_runs.html")
			+ Gen_subinter(path,sub_bg2, "Statistics", 1, 0, "contest_statistics.html")
			+ Gen_subinter(path,sub_bg2, "Ranklist", 1, 0, "contest_rank.html")
			+ Gen_subinter(path,sub_bg2, "Clarification", 1, 0, "#");

			if(menu=="admin_contest")
			{
				showstr += Gen_subinter(path,sub_bg2, "Rejudge", 1, 0, "contest_rejudge.html")
				+ Gen_subinter(path,sub_bg2, "Edit Contest", 1, 0, "edit_contest.html")
				+ Gen_subinter(path,sub_bg2, "Add Problem", 1, 0, "add_problem.html");
			}
		}
		else if(submenu=="prob")
		{
			showstr += Gen_subinter(path,sub_bg1, "Contest Select", 0, 1, "contest_list.html")
			+ Gen_subinter(path,sub_bg2, "Information", 1, 0, "contest.html")
			+ Gen_subinter(path,sub_bg3, "Problems", 1, 0, "contest_problem.html")
			+ Gen_subinter(path,sub_bg2, "Runs", 1, 0, "contest_runs.html")
			+ Gen_subinter(path,sub_bg2, "Statistics", 1, 0, "contest_statistics.html")
			+ Gen_subinter(path,sub_bg2, "Ranklist", 1, 0, "contest_rank.html")
			+ Gen_subinter(path,sub_bg2, "Clarification", 1, 0, "#");

			if(menu=="admin_contest")
			{
				showstr += Gen_subinter(path,sub_bg2, "Rejudge", 1, 0, "contest_rejudge.html")
				+ Gen_subinter(path,sub_bg2, "Edit Contest", 1, 0, "edit_contest.html")
				+ Gen_subinter(path,sub_bg2, "Add Problem", 1, 0, "add_problem.html");
			}
		}
		else if(submenu=="runs")
		{
			showstr += Gen_subinter(path,sub_bg1, "Contest Select", 0, 1, "contest_list.html")
			+ Gen_subinter(path,sub_bg2, "Information", 1, 0, "contest.html")
			+ Gen_subinter(path,sub_bg2, "Problems", 1, 0, "contest_problem.html")
			+ Gen_subinter(path,sub_bg3, "Runs", 1, 0, "contest_runs.html")
			+ Gen_subinter(path,sub_bg2, "Statistics", 1, 0, "contest_statistics.html")
			+ Gen_subinter(path,sub_bg2, "Ranklist", 1, 0, "contest_rank.html")
			+ Gen_subinter(path,sub_bg2, "Clarification", 1, 0, "#");

			if(menu=="admin_contest")
			{
				showstr += Gen_subinter(path,sub_bg2, "Rejudge", 1, 0, "contest_rejudge.html")
				+ Gen_subinter(path,sub_bg2, "Edit Contest", 1, 0, "edit_contest.html")
				+ Gen_subinter(path,sub_bg2, "Add Problem", 1, 0, "add_problem.html");
			}
		}
		else if(submenu=="stat")
		{
			showstr += Gen_subinter(path,sub_bg1, "Contest Select", 0, 1, "contest_list.html")
			+ Gen_subinter(path,sub_bg2, "Information", 1, 0, "contest.html")
			+ Gen_subinter(path,sub_bg2, "Problems", 1, 0, "contest_problem.html")
			+ Gen_subinter(path,sub_bg2, "Runs", 1, 0, "contest_runs.html")
			+ Gen_subinter(path,sub_bg3, "Statistics", 1, 0, "contest_statistics.html")
			+ Gen_subinter(path,sub_bg2, "Ranklist", 1, 0, "contest_rank.html")
			+ Gen_subinter(path,sub_bg2, "Clarification", 1, 0, "#");

			if(menu=="admin_contest")
			{
				showstr += Gen_subinter(path,sub_bg2, "Rejudge", 1, 0, "contest_rejudge.html")
				+ Gen_subinter(path,sub_bg2, "Edit Contest", 1, 0, "edit_contest.html")
				+ Gen_subinter(path,sub_bg2, "Add Problem", 1, 0, "add_problem.html");
			}
		}
		else if(submenu=="rank")
		{
			showstr += Gen_subinter(path,sub_bg1, "Contest Select", 0, 1, "contest_list.html")
			+ Gen_subinter(path,sub_bg2, "Information", 1, 0, "contest.html")
			+ Gen_subinter(path,sub_bg2, "Problems", 1, 0, "contest_problem.html")
			+ Gen_subinter(path,sub_bg2, "Runs", 1, 0, "contest_runs.html")
			+ Gen_subinter(path,sub_bg2, "Statistics", 1, 0, "contest_statistics.html")
			+ Gen_subinter(path,sub_bg3, "Ranklist", 1, 0, "contest_rank.html")
			+ Gen_subinter(path,sub_bg2, "Clarification", 1, 0, "#");

			if(menu=="admin_contest")
			{
				showstr += Gen_subinter(path,sub_bg2, "Rejudge", 1, 0, "contest_rejudge.html")
				+ Gen_subinter(path,sub_bg2, "Edit Contest", 1, 0, "edit_contest.html")
				+ Gen_subinter(path,sub_bg2, "Add Problem", 1, 0, "add_problem.html");
			}
		}
		else if(submenu=="clar")
		{
			showstr += Gen_subinter(path,sub_bg1, "Contest Select", 0, 1, "contest_list.html")
			+ Gen_subinter(path,sub_bg2, "Information", 1, 0, "contest.html")
			+ Gen_subinter(path,sub_bg2, "Problems", 1, 0, "contest_problem.html")
			+ Gen_subinter(path,sub_bg2, "Runs", 1, 0, "contest_runs.html")
			+ Gen_subinter(path,sub_bg2, "Statistics", 1, 0, "contest_statistics.html")
			+ Gen_subinter(path,sub_bg2, "Ranklist", 1, 0, "contest_rank.html")
			+ Gen_subinter(path,sub_bg3, "Clarification", 1, 0, "#");

			if(menu=="admin_contest")
			{
				showstr += Gen_subinter(path,sub_bg2, "Rejudge", 1, 0, "contest_rejudge.html")
				+ Gen_subinter(path,sub_bg2, "Edit Contest", 1, 0, "edit_contest.html")
				+ Gen_subinter(path,sub_bg2, "Add Problem", 1, 0, "add_problem.html");
			}
		}
		else if(submenu=="rejudge")
		{
			showstr += Gen_subinter(path,sub_bg1, "Contest Select", 0, 1, "contest_list.html")
			+ Gen_subinter(path,sub_bg2, "Information", 1, 0, "contest.html")
			+ Gen_subinter(path,sub_bg2, "Problems", 1, 0, "contest_problem.html")
			+ Gen_subinter(path,sub_bg2, "Runs", 1, 0, "contest_runs.html")
			+ Gen_subinter(path,sub_bg2, "Statistics", 1, 0, "contest_statistics.html")
			+ Gen_subinter(path,sub_bg2, "Ranklist", 1, 0, "contest_rank.html")
			+ Gen_subinter(path,sub_bg2, "Clarification", 1, 0, "#");

			if(menu=="admin_contest")
			{
				showstr += Gen_subinter(path,sub_bg3, "Rejudge", 1, 0, "contest_rejudge.html")
				+ Gen_subinter(path,sub_bg2, "Edit Contest", 1, 0, "edit_contest.html")
				+ Gen_subinter(path,sub_bg2, "Add Problem", 1, 0, "add_problem.html");
			}
		}
		else if(submenu=="edit")
		{
			showstr += Gen_subinter(path,sub_bg1, "Contest Select", 0, 1, "contest_list.html")
			+ Gen_subinter(path,sub_bg2, "Information", 1, 0, "contest.html")
			+ Gen_subinter(path,sub_bg2, "Problems", 1, 0, "contest_problem.html")
			+ Gen_subinter(path,sub_bg2, "Runs", 1, 0, "contest_runs.html")
			+ Gen_subinter(path,sub_bg2, "Statistics", 1, 0, "contest_statistics.html")
			+ Gen_subinter(path,sub_bg2, "Ranklist", 1, 0, "contest_rank.html")
			+ Gen_subinter(path,sub_bg2, "Clarification", 1, 0, "#");

			if(menu=="admin_contest")
			{
				showstr += Gen_subinter(path,sub_bg2, "Rejudge", 1, 0, "contest_rejudge.html")
				+ Gen_subinter(path,sub_bg3, "Edit Contest", 1, 0, "edit_contest.html")
				+ Gen_subinter(path,sub_bg2, "Add Problem", 1, 0, "add_problem.html");
			}
		}
		else if(submenu=="add")
		{
			showstr += Gen_subinter(path,sub_bg1, "Contest Select", 0, 1, "contest_list.html")
			+ Gen_subinter(path,sub_bg2, "Information", 1, 0, "contest.html")
			+ Gen_subinter(path,sub_bg2, "Problems", 1, 0, "contest_problem.html")
			+ Gen_subinter(path,sub_bg2, "Runs", 1, 0, "contest_runs.html")
			+ Gen_subinter(path,sub_bg2, "Statistics", 1, 0, "contest_statistics.html")
			+ Gen_subinter(path,sub_bg2, "Ranklist", 1, 0, "contest_rank.html")
			+ Gen_subinter(path,sub_bg2, "Clarification", 1, 0, "#");

			if(menu=="admin_contest")
			{
				showstr += Gen_subinter(path,sub_bg2, "Rejudge", 1, 0, "contest_rejudge.html")
				+ Gen_subinter(path,sub_bg2, "Edit Contest", 1, 0, "edit_contest.html")
				+ Gen_subinter(path,sub_bg3, "Add Problem", 1, 0, "add_problem.html");
			}
		}
		else if(submenu=="none")
		{
			showstr += Gen_subinter(path,sub_bg1, "Contest Select", 0, 1, "contest_list.html")
			+ Gen_subinter(path,sub_bg2, "Information", 1, 0, "contest.html")
			+ Gen_subinter(path,sub_bg2, "Problems", 1, 0, "contest_problem.html")
			+ Gen_subinter(path,sub_bg2, "Runs", 1, 0, "contest_runs.html")
			+ Gen_subinter(path,sub_bg2, "Statistics", 1, 0, "contest_statistics.html")
			+ Gen_subinter(path,sub_bg2, "Ranklist", 1, 0, "contest_rank.html")
			+ Gen_subinter(path,sub_bg2, "Clarification", 1, 0, "#");

			if(menu=="admin_contest")
			{
				showstr += Gen_subinter(path,sub_bg2, "Rejudge", 1, 0, "contest_rejudge.html")
				+ Gen_subinter(path,sub_bg2, "Edit Contest", 1, 0, "edit_contest.html")
				+ Gen_subinter(path,sub_bg2, "Add Problem", 1, 0, "add_problem.html");
			}
		}
	}
	else if(menu=="user_problem" || menu=="admin_problem")
	{
		if(submenu=="list")
		{
			showstr += Gen_subinter(path,sub_bg1, "Problem Sets", 0, 1, "probset_list.html")
			+ Gen_subinter(path,sub_bg3, "Select Problem", 1, 0, "prob_list.html")
			+ Gen_subinter(path,sub_bg2, "Problem Search", 1, 0, "pro_adv_search.html")
			+ Gen_subinter(path,sub_bg2, "Runs", 1, 0, "probset_runs.html")
			+ Gen_subinter(path,sub_bg2, "Ranklist", 1, 0, "probset_rank.html")
			+ Gen_subinter(path,sub_bg2, "Discussion", 1, 0, "#");

			if(menu=="admin_problem")
			{
				showstr += Gen_subinter(path,sub_bg2, "Edit Problem Set", 1, 0, "edit_probset.html")
				+ Gen_subinter(path,sub_bg2, "Rejudge", 1, 0, "probset_rejudge.html")
				+ Gen_subinter(path,sub_bg2, "Add Problem", 1, 0, "add_problem2.html");
			}
		}
		else if(submenu=="search")
		{
			showstr += Gen_subinter(path,sub_bg1, "Problem Sets", 0, 1, "probset_list.html")
			+ Gen_subinter(path,sub_bg2, "Select Problem", 1, 0, "prob_list.html")
			+ Gen_subinter(path,sub_bg3, "Problem Search", 1, 0, "pro_adv_search.html")
			+ Gen_subinter(path,sub_bg2, "Runs", 1, 0, "probset_runs.html")
			+ Gen_subinter(path,sub_bg2, "Ranklist", 1, 0, "probset_rank.html")
			+ Gen_subinter(path,sub_bg2, "Discussion", 1, 0, "#");

			if(menu=="admin_problem")
			{
				showstr += Gen_subinter(path,sub_bg2, "Edit Problem Set", 1, 0, "edit_probset.html")
				+ Gen_subinter(path,sub_bg2, "Rejudge", 1, 0, "probset_rejudge.html")
				+ Gen_subinter(path,sub_bg2, "Add Problem", 1, 0, "add_problem2.html");
			}
		}
		else if(submenu=="add")
		{
			showstr += Gen_subinter(path,sub_bg1, "Problem Sets", 0, 1, "probset_list.html")
			+ Gen_subinter(path,sub_bg2, "Select Problem", 1, 0, "prob_list.html")
			+ Gen_subinter(path,sub_bg2, "Problem Search", 1, 0, "pro_adv_search.html")
			+ Gen_subinter(path,sub_bg2, "Runs", 1, 0, "probset_runs.html")
			+ Gen_subinter(path,sub_bg2, "Ranklist", 1, 0, "probset_rank.html")
			+ Gen_subinter(path,sub_bg2, "Discussion", 1, 0, "#");

			if(menu=="admin_problem")
			{
				showstr += Gen_subinter(path,sub_bg2, "Edit Problem Set", 1, 0, "edit_probset.html")
				+ Gen_subinter(path,sub_bg2, "Rejudge", 1, 0, "probset_rejudge.html")
				+ Gen_subinter(path,sub_bg3, "Add Problem", 1, 0, "add_problem2.html");
			}
		}
		else if(submenu=="edit")
		{
			showstr += Gen_subinter(path,sub_bg1, "Problem Sets", 0, 1, "probset_list.html")
			+ Gen_subinter(path,sub_bg2, "Select Problem", 1, 0, "prob_list.html")
			+ Gen_subinter(path,sub_bg2, "Problem Search", 1, 0, "pro_adv_search.html")
			+ Gen_subinter(path,sub_bg2, "Runs", 1, 0, "probset_runs.html")
			+ Gen_subinter(path,sub_bg2, "Ranklist", 1, 0, "probset_rank.html")
			+ Gen_subinter(path,sub_bg2, "Discussion", 1, 0, "#");

			if(menu=="admin_problem")
			{
				showstr += Gen_subinter(path,sub_bg3, "Edit Problem Set", 1, 0, "edit_probset.html")
				+ Gen_subinter(path,sub_bg2, "Rejudge", 1, 0, "probset_rejudge.html")
				+ Gen_subinter(path,sub_bg2, "Add Problem", 1, 0, "add_problem2.html");
			}
		}
		else if(submenu=="rejudge")
		{
			showstr += Gen_subinter(path,sub_bg1, "Problem Sets", 0, 1, "probset_list.html")
			+ Gen_subinter(path,sub_bg2, "Select Problem", 1, 0, "prob_list.html")
			+ Gen_subinter(path,sub_bg2, "Problem Search", 1, 0, "pro_adv_search.html")
			+ Gen_subinter(path,sub_bg2, "Runs", 1, 0, "probset_runs.html")
			+ Gen_subinter(path,sub_bg2, "Ranklist", 1, 0, "probset_rank.html")
			+ Gen_subinter(path,sub_bg2, "Discussion", 1, 0, "#");

			if(menu=="admin_problem")
			{
				showstr += Gen_subinter(path,sub_bg2, "Edit Problem Set", 1, 0, "edit_probset.html")
				+ Gen_subinter(path,sub_bg3, "Rejudge", 1, 0, "probset_rejudge.html")
				+ Gen_subinter(path,sub_bg2, "Add Problem", 1, 0, "add_problem2.html");
			}
		}
		else if(submenu=="rank")
		{
			showstr += Gen_subinter(path,sub_bg1, "Problem Sets", 0, 1, "probset_list.html")
			+ Gen_subinter(path,sub_bg2, "Select Problem", 1, 0, "prob_list.html")
			+ Gen_subinter(path,sub_bg2, "Problem Search", 1, 0, "pro_adv_search.html")
			+ Gen_subinter(path,sub_bg2, "Runs", 1, 0, "probset_runs.html")
			+ Gen_subinter(path,sub_bg3, "Ranklist", 1, 0, "probset_rank.html")
			+ Gen_subinter(path,sub_bg2, "Discussion", 1, 0, "#");

			if(menu=="admin_problem")
			{
				showstr += Gen_subinter(path,sub_bg2, "Edit Problem Set", 1, 0, "edit_probset.html")
				+ Gen_subinter(path,sub_bg2, "Rejudge", 1, 0, "probset_rejudge.html")
				+ Gen_subinter(path,sub_bg2, "Add Problem", 1, 0, "add_problem2.html");
			}
		}
		else if(submenu=="discussion")
		{
			showstr += Gen_subinter(path,sub_bg1, "Problem Sets", 0, 1, "probset_list.html")
			+ Gen_subinter(path,sub_bg2, "Select Problem", 1, 0, "prob_list.html")
			+ Gen_subinter(path,sub_bg2, "Problem Search", 1, 0, "pro_adv_search.html")
			+ Gen_subinter(path,sub_bg2, "Runs", 1, 0, "probset_runs.html")
			+ Gen_subinter(path,sub_bg2, "Ranklist", 1, 0, "probset_rank.html")
			+ Gen_subinter(path,sub_bg3, "Discussion", 1, 0, "#");

			if(menu=="admin_problem")
			{
				showstr += Gen_subinter(path,sub_bg2, "Edit Problem Set", 1, 0, "edit_probset.html")
				+ Gen_subinter(path,sub_bg2, "Rejudge", 1, 0, "probset_rejudge.html")
				+ Gen_subinter(path,sub_bg2, "Add Problem", 1, 0, "add_problem2.html");
			}
		}
		else if(submenu=="none")
		{
			showstr += Gen_subinter(path,sub_bg1, "Problem Sets", 0, 1, "probset_list.html")
			+ Gen_subinter(path,sub_bg2, "Select Problem", 1, 0, "prob_list.html")
			+ Gen_subinter(path,sub_bg2, "Problem Search", 1, 0, "pro_adv_search.html")
			+ Gen_subinter(path,sub_bg2, "Runs", 1, 0, "probset_runs.html")
			+ Gen_subinter(path,sub_bg2, "Ranklist", 1, 0, "probset_rank.html")
			+ Gen_subinter(path,sub_bg2, "Discussion", 1, 0, "#");

			if(menu=="admin_problem")
			{
				showstr += Gen_subinter(path,sub_bg2, "Edit Problem Set", 1, 0, "edit_probset.html")
				+ Gen_subinter(path,sub_bg2, "Rejudge", 1, 0, "probset_rejudge.html")
				+ Gen_subinter(path,sub_bg2, "Add Problem", 1, 0, "add_problem2.html");
			}
		}
		else if(submenu=="runs")
		{
			showstr += Gen_subinter(path,sub_bg1, "Problem Sets", 0, 1, "probset_list.html")
			+ Gen_subinter(path,sub_bg2, "Select Problem", 1, 0, "prob_list.html")
			+ Gen_subinter(path,sub_bg2, "Problem Search", 1, 0, "pro_adv_search.html")
			+ Gen_subinter(path,sub_bg3, "Runs", 1, 0, "probset_runs.html")
			+ Gen_subinter(path,sub_bg2, "Ranklist", 1, 0, "probset_rank.html")
			+ Gen_subinter(path,sub_bg2, "Discussion", 1, 0, "#");

			if(menu=="admin_problem")
			{
				showstr += Gen_subinter(path,sub_bg2, "Edit Problem Set", 1, 0, "edit_probset.html")
				+ Gen_subinter(path,sub_bg2, "Rejudge", 1, 0, "probset_rejudge.html")
				+ Gen_subinter(path,sub_bg2, "Add Problem", 1, 0, "add_problem2.html");
			}
		}
		else if(submenu=="setlist")
		{
			showstr += Gen_subinter(path,sub_bg1, "Problem Sets", 0, 1, "probset_list.html")
			+ Gen_subinter(path,sub_bg3, "Select Problem Set", 1, 0, "prob_list.html");
		}
	}
	else if(menu=="user_forum")
	{
	}
	else if(menu=="admin_forum")
	{
	}
	else if(menu=="admin_admin")
	{
		if(submenu=="sys")
		{
			showstr += Gen_subinter(path,sub_bg1, "Configue system", 0, 1, "admin_sys.html")
			+ Gen_subinter(path,sub_bg3, "System Parameters", 1, 0, "admin_sys.html")
			+ Gen_subinter(path,sub_bg2, "Default Limits", 1, 0, "admin_limits.html")
			+ Gen_subinter(path,sub_bg2, "Role Management", 1, 0, "admin_role.html")
			+ Gen_subinter(path,sub_bg2, "Judge", 1, 0, "admin_judge.html")
			+ Gen_subinter(path,sub_bg2, "Language", 1, 0, "admin_lan.html")
			+ Gen_subinter(path,sub_bg2, "Judge reply", 1, 0, "admin_reply.html")
			+ Gen_subinter(path,sub_bg1, "Manage Users", 0, 0, "admin_user.html")
			+ Gen_subinter(path,sub_bg1, "Manage Contests", 0, 0, "admin_contest.html")
			+ Gen_subinter(path,sub_bg1, "Manage Forums", 0, 0, "admin_forum.html");
		}
		else if(submenu=="limits")
		{
			showstr += Gen_subinter(path,sub_bg1, "Configue system", 0, 1, "admin_sys.html")
			+ Gen_subinter(path,sub_bg2, "System Parameters", 1, 0, "admin_sys.html")
			+ Gen_subinter(path,sub_bg3, "Default Limits", 1, 0, "admin_limits.html")
			+ Gen_subinter(path,sub_bg2, "Role Management", 1, 0, "admin_role.html")
			+ Gen_subinter(path,sub_bg2, "Judge", 1, 0, "admin_judge.html")
			+ Gen_subinter(path,sub_bg2, "Language", 1, 0, "admin_lan.html")
			+ Gen_subinter(path,sub_bg2, "Judge reply", 1, 0, "admin_reply.html")
			+ Gen_subinter(path,sub_bg1, "Manage Users", 0, 0, "admin_user.html")
			+ Gen_subinter(path,sub_bg1, "Manage Contests", 0, 0, "admin_contest.html")
			+ Gen_subinter(path,sub_bg1, "Manage Forums", 0, 0, "admin_forum.html");
		}
		else if(submenu=="role")
		{
			showstr += Gen_subinter(path,sub_bg1, "Configue system", 0, 1, "admin_sys.html")
			+ Gen_subinter(path,sub_bg2, "System Parameters", 1, 0, "admin_sys.html")
			+ Gen_subinter(path,sub_bg2, "Default Limits", 1, 0, "admin_limits.html")
			+ Gen_subinter(path,sub_bg3, "Role Management", 1, 0, "admin_role.html")
			+ Gen_subinter(path,sub_bg2, "Judge", 1, 0, "admin_judge.html")
			+ Gen_subinter(path,sub_bg2, "Language", 1, 0, "admin_lan.html")
			+ Gen_subinter(path,sub_bg2, "Judge reply", 1, 0, "admin_reply.html")
			+ Gen_subinter(path,sub_bg1, "Manage Users", 0, 0, "admin_user.html")
			+ Gen_subinter(path,sub_bg1, "Manage Contests", 0, 0, "admin_contest.html")
			+ Gen_subinter(path,sub_bg1, "Manage Forums", 0, 0, "admin_forum.html");
		}
		else if(submenu=="judge")
		{
			showstr += Gen_subinter(path,sub_bg1, "Configue system", 0, 1, "admin_sys.html")
			+ Gen_subinter(path,sub_bg2, "System Parameters", 1, 0, "admin_sys.html")
			+ Gen_subinter(path,sub_bg2, "Default Limits", 1, 0, "admin_limits.html")
			+ Gen_subinter(path,sub_bg2, "Role Management", 1, 0, "admin_role.html")
			+ Gen_subinter(path,sub_bg3, "Judge", 1, 0, "admin_judge.html")
			+ Gen_subinter(path,sub_bg2, "Language", 1, 0, "admin_lan.html")
			+ Gen_subinter(path,sub_bg2, "Judge reply", 1, 0, "admin_reply.html")
			+ Gen_subinter(path,sub_bg1, "Manage Users", 0, 0, "admin_user.html")
			+ Gen_subinter(path,sub_bg1, "Manage Contests", 0, 0, "admin_contest.html")
			+ Gen_subinter(path,sub_bg1, "Manage Forums", 0, 0, "admin_forum.html");
		}
		else if(submenu=="lan")
		{
			showstr += Gen_subinter(path,sub_bg1, "Configue system", 0, 1, "admin_sys.html")
			+ Gen_subinter(path,sub_bg2, "System Parameters", 1, 0, "admin_sys.html")
			+ Gen_subinter(path,sub_bg2, "Default Limits", 1, 0, "admin_limits.html")
			+ Gen_subinter(path,sub_bg2, "Role Management", 1, 0, "admin_role.html")
			+ Gen_subinter(path,sub_bg2, "Judge", 1, 0, "admin_judge.html")
			+ Gen_subinter(path,sub_bg3, "Language", 1, 0, "admin_lan.html")
			+ Gen_subinter(path,sub_bg2, "Judge reply", 1, 0, "admin_reply.html")
			+ Gen_subinter(path,sub_bg1, "Manage Users", 0, 0, "admin_user.html")
			+ Gen_subinter(path,sub_bg1, "Manage Contests", 0, 0, "admin_contest.html")
			+ Gen_subinter(path,sub_bg1, "Manage Forums", 0, 0, "admin_forum.html");
		}
		else if(submenu=="reply")
		{
			showstr += Gen_subinter(path,sub_bg1, "Configue system", 0, 1, "admin_sys.html")
			+ Gen_subinter(path,sub_bg2, "System Parameters", 1, 0, "admin_sys.html")
			+ Gen_subinter(path,sub_bg2, "Default Limits", 1, 0, "admin_limits.html")
			+ Gen_subinter(path,sub_bg2, "Role Management", 1, 0, "admin_role.html")
			+ Gen_subinter(path,sub_bg2, "Judge", 1, 0, "admin_judge.html")
			+ Gen_subinter(path,sub_bg2, "Language", 1, 0, "admin_lan.html")
			+ Gen_subinter(path,sub_bg3, "Judge reply", 1, 0, "admin_reply.html")
			+ Gen_subinter(path,sub_bg1, "Manage Users", 0, 0, "admin_user.html")
			+ Gen_subinter(path,sub_bg1, "Manage Contests", 0, 0, "admin_contest.html")
			+ Gen_subinter(path,sub_bg1, "Manage Forums", 0, 0, "admin_forum.html");
		}
		else if(submenu=="user")
		{
			showstr += Gen_subinter(path,sub_bg1, "Configue system", 0, 0, "admin_sys.html")
			+ Gen_subinter(path,sub_bg1, "Manage Users", 0, 1, "admin_user.html")
			+ Gen_subinter(path,sub_bg3, "Manage Users", 1, 0, "admin_user.html")
			+ Gen_subinter(path,sub_bg1, "Manage Contests", 0, 0, "admin_contest.html")
			+ Gen_subinter(path,sub_bg1, "Manage Forums", 0, 0, "admin_forum.html");
		}
		else if(submenu=="contest")
		{
			showstr += Gen_subinter(path,sub_bg1, "Configue system", 0, 0, "admin_sys.html")
			+ Gen_subinter(path,sub_bg1, "Manage Users", 0, 0, "admin_user.html")
			+ Gen_subinter(path,sub_bg1, "Manage Contests", 0, 1, "admin_contest.html")
			+ Gen_subinter(path,sub_bg3, "Manage Contests", 1, 0, "admin_contest.html")
			+ Gen_subinter(path,sub_bg1, "Manage Forums", 0, 0, "admin_forum.html");
		}
		else if(submenu=="forum")
		{
			showstr += Gen_subinter(path,sub_bg1, "Configue system", 0, 0, "admin_sys.html")
			+ Gen_subinter(path,sub_bg1, "Manage Users", 0, 0, "admin_user.html")
			+ Gen_subinter(path,sub_bg1, "Manage Contests", 0, 0, "admin_contest.html")
			+ Gen_subinter(path,sub_bg1, "Manage Forums", 0, 1, "admin_forum.html")
			+ Gen_subinter(path,sub_bg3, "Manage Forums", 1, 0, "admin_forum.html");
		}
		else if(submenu=="add_contest")
		{
			showstr += Gen_subinter(path,sub_bg1, "Configue system", 0, 0, "admin_sys.html")
			+ Gen_subinter(path,sub_bg1, "Manage Users", 0, 0, "admin_user.html")
			+ Gen_subinter(path,sub_bg1, "Manage Contests", 0, 0, "admin_contest.html")
			+ Gen_subinter(path,sub_bg1, "Manage Forums", 0, 0, "admin_forum.html")
			+ Gen_subinter(path,sub_bg1, "Add Contest", 0, 1, "admin_addcont.html");
		}
	}
	showstr+=cpc;
	document.write (showstr);
}

//-->