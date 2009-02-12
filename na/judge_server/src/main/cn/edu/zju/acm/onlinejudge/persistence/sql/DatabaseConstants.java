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

package cn.edu.zju.acm.onlinejudge.persistence.sql;

/**
 * Databse constants.
 * 
 * @version 2.0
 * @author ZOIDEV
 */
public class DatabaseConstants {

    /**
     * COUNTRY table.
     */
    public static final String COUNTRY_TABLE = "country";

    /**
     * COUNTRY_ID column in COUNTRY table.
     */
    public static final String COUNTRY_COUNTRY_ID = "country_id";

    /**
     * NAME column in COUNTRY table.
     */
    public static final String COUNTRY_NAME = "name";

    /**
     * USER_PROFILE table.
     */
    public static final String USER_PROFILE_TABLE = "user_profile";

    /**
     * USER_PROFILE_ID column in USER_PROFILE table.
     */
    public static final String USER_PROFILE_USER_PROFILE_ID = "user_profile_id";

    /**
     * HANDLE column in USER_PROFILE table.
     */
    public static final String USER_PROFILE_HANDLE = "handle";
    
    /**
     * NICKNAME column in USER_PROFILE table.
     */
    public static final String USER_PROFILE_NICKNAME = "nickname";

    /**
     * PASSWORD column in USER_PROFILE table.
     */
    public static final String USER_PROFILE_PASSWORD = "password";

    /**
     * EMAIL_ADDRESS column in USER_PROFILE table.
     */
    public static final String USER_PROFILE_EMAIL_ADDRESS = "email_address";

    /**
     * REG_DATE column in USER_PROFILE table.
     */
    public static final String USER_PROFILE_REG_DATE = "reg_date";

    /**
     * FIRST_NAME column in USER_PROFILE table.
     */
    public static final String USER_PROFILE_FIRST_NAME = "first_name";

    /**
     * LAST_NAME column in USER_PROFILE table.
     */
    public static final String USER_PROFILE_LAST_NAME = "last_name";

    /**
     * ADDRESS_LINE1 column in USER_PROFILE table.
     */
    public static final String USER_PROFILE_ADDRESS_LINE1 = "address_line1";

    /**
     * ADDRESS_LINE2 column in USER_PROFILE table.
     */
    public static final String USER_PROFILE_ADDRESS_LINE2 = "address_line2";

    /**
     * CITY column in USER_PROFILE table.
     */
    public static final String USER_PROFILE_CITY = "city";

    /**
     * STATE column in USER_PROFILE table.
     */
    public static final String USER_PROFILE_STATE = "state";

    /**
     * COUNTRY_ID column in USER_PROFILE table.
     */
    public static final String USER_PROFILE_COUNTRY_ID = "country_id";

    /**
     * ZIP_CODE column in USER_PROFILE table.
     */
    public static final String USER_PROFILE_ZIP_CODE = "zip_code";

    /**
     * PHONE_NUMBER column in USER_PROFILE table.
     */
    public static final String USER_PROFILE_PHONE_NUMBER = "phone_number";

    /**
     * BIRTH_DATE column in USER_PROFILE table.
     */
    public static final String USER_PROFILE_BIRTH_DATE = "birth_date";

    /**
     * GENDER column in USER_PROFILE table.
     */
    public static final String USER_PROFILE_GENDER = "gender";

    /**
     * SCHOOL column in USER_PROFILE table.
     */
    public static final String USER_PROFILE_SCHOOL = "school";

    /**
     * MAJOR column in USER_PROFILE table.
     */
    public static final String USER_PROFILE_MAJOR = "major";

    /**
     * GRADUATE_STUDENT column in USER_PROFILE table.
     */
    public static final String USER_PROFILE_GRADUATE_STUDENT = "graduate_student";

    /**
     * GRADUATION_YEAR column in USER_PROFILE table.
     */
    public static final String USER_PROFILE_GRADUATION_YEAR = "graduation_year";

    /**
     * STUDENT_NUMBER column in USER_PROFILE table.
     */
    public static final String USER_PROFILE_STUDENT_NUMBER = "student_number";

    /**
     * ACTIVE column in USER_PROFILE table.
     */
    public static final String USER_PROFILE_ACTIVE = "active";

    /**
     * CONFIRMED column in USER_PROFILE table.
     */
    public static final String USER_PROFILE_CONFIRMED = "confirmed";

    /**
     * SUPER_ADMIN column in USER_PROFILE table.
     */
    public static final String USER_PROFILE_SUPER_ADMIN = "super_admin";

    /**
     * USER_PREFERENCE table.
     */
    public static final String USER_PREFERENCE_TABLE = "user_preference";

    /**
     * USER_PROFILE_ID column in USER_PREFERENCE table.
     */
    public static final String USER_PREFERENCE_USER_PROFILE_ID = "user_profile_id";

    /**
     * PLAN column in USER_PREFERENCE table.
     */
    public static final String USER_PREFERENCE_PLAN = "plan";

    /**
     * PROBLEM_PAGING column in USER_PREFERENCE table.
     */
    public static final String USER_PREFERENCE_PROBLEM_PAGING = "problem_paging";

    /**
     * SUBMISSION_PAGING column in USER_PREFERENCE table.
     */
    public static final String USER_PREFERENCE_SUBMISSION_PAGING = "submission_paging";

    /**
     * STATUS_PAGING column in USER_PREFERENCE table.
     */
    public static final String USER_PREFERENCE_STATUS_PAGING = "status_paging";

    /**
     * USER_PAGING column in USER_PREFERENCE table.
     */
    public static final String USER_PREFERENCE_USER_PAGING = "user_paging";

    /**
     * POST_PAGING column in USER_PREFERENCE table.
     */
    public static final String USER_PREFERENCE_POST_PAGING = "post_paging";

    /**
     * THREAD_PAGING column in USER_PREFERENCE table.
     */
    public static final String USER_PREFERENCE_THREAD_PAGING = "thread_paging";

    /**
     * CONFIRMATION table.
     */
    public static final String CONFIRMATION_TABLE = "confirmation";

    /**
     * USER_PROFILE_ID column in CONFIRMATION table.
     */
    public static final String CONFIRMATION_USER_PROFILE_ID = "user_profile_id";

    /**
     * CODE column in CONFIRMATION table.
     */
    public static final String CONFIRMATION_CODE = "code";

    /**
     * ROLE table.
     */
    public static final String ROLE_TABLE = "role";

    /**
     * ROLE_ID column in ROLE table.
     */
    public static final String ROLE_ROLE_ID = "role_id";

    /**
     * NAME column in ROLE table.
     */
    public static final String ROLE_NAME = "name";

    /**
     * DESCRIPTION column in ROLE table.
     */
    public static final String ROLE_DESCRIPTION = "description";

    /**
     * USER_ROLE table.
     */
    public static final String USER_ROLE_TABLE = "user_role";

    /**
     * USER_PROFILE_ID column in USER_ROLE table.
     */
    public static final String USER_ROLE_USER_PROFILE_ID = "user_profile_id";

    /**
     * ROLE_ID column in USER_ROLE table.
     */
    public static final String USER_ROLE_ROLE_ID = "role_id";

    /**
     * CONTEST_PERMISSION table.
     */
    public static final String CONTEST_PERMISSION_TABLE = "contest_permission";

    /**
     * ROLE_ID column in CONTEST_PERMISSION table.
     */
    public static final String CONTEST_PERMISSION_ROLE_ID = "role_id";

    /**
     * CONTEST_ID column in CONTEST_PERMISSION table.
     */
    public static final String CONTEST_PERMISSION_CONTEST_ID = "contest_id";

    /**
     * PERMISSION_LEVEL_ID column in CONTEST_PERMISSION table.
     */
    public static final String CONTEST_PERMISSION_PERMISSION_LEVEL_ID = "permission_level_id";

    /**
     * FORUM_PERMISSION table.
     */
    public static final String FORUM_PERMISSION_TABLE = "forum_permission";

    /**
     * ROLE_ID column in FORUM_PERMISSION table.
     */
    public static final String FORUM_PERMISSION_ROLE_ID = "role_id";

    /**
     * FORUM_ID column in FORUM_PERMISSION table.
     */
    public static final String FORUM_PERMISSION_FORUM_ID = "forum_id";

    /**
     * PERMISSION_LEVEL_ID column in FORUM_PERMISSION table.
     */
    public static final String FORUM_PERMISSION_PERMISSION_LEVEL_ID = "permission_level_id";

    /**
     * PERMISSION_LEVEL table.
     */
    public static final String PERMISSION_LEVEL_TABLE = "permission_level";

    /**
     * PERMISSION_LEVEL_ID column in PERMISSION_LEVEL table.
     */
    public static final String PERMISSION_LEVEL_PERMISSION_LEVEL_ID = "permission_level_id";

    /**
     * DESCRIPTION column in PERMISSION_LEVEL table.
     */
    public static final String PERMISSION_LEVEL_DESCRIPTION = "description";

    /**
     * CONTEST table.
     */
    public static final String CONTEST_TABLE = "contest";

    /**
     * CONTEST_ID column in CONTEST table.
     */
    public static final String CONTEST_CONTEST_ID = "contest_id";

    /**
     * TITLE column in CONTEST table.
     */
    public static final String CONTEST_TITLE = "title";

    /**
     * DESCRIPTION column in CONTEST table.
     */
    public static final String CONTEST_DESCRIPTION = "description";

    /**
     * START_TIME column in CONTEST table.
     */
    public static final String CONTEST_START_TIME = "start_time";

    /**
     * END_TIME column in CONTEST table.
     */
    public static final String CONTEST_END_TIME = "end_time";

    /**
     * FORUM_ID column in CONTEST table.
     */
    public static final String CONTEST_FORUM_ID = "forum_id";

    /**
     * LIMITS_ID column in CONTEST table.
     */
    public static final String CONTEST_LIMITS_ID = "limits_id";

    /**
     * ACTIVE column in CONTEST table.
     */
    public static final String CONTEST_ACTIVE = "active";

    /**
     * PROBLEMSET column in CONTEST table.
     */
    public static final String CONTEST_PROBLEMSET = "problemset";

    /**
     * LANGUAGE table.
     */
    public static final String LANGUAGE_TABLE = "language";

    /**
     * LANGUAGE_ID column in LANGUAGE table.
     */
    public static final String LANGUAGE_LANGUAGE_ID = "language_id";

    /**
     * NAME column in LANGUAGE table.
     */
    public static final String LANGUAGE_NAME = "name";

    /**
     * DESCRIPTION column in LANGUAGE table.
     */
    public static final String LANGUAGE_DESCRIPTION = "description";

    /**
     * OPTIONS column in LANGUAGE table.
     */
    public static final String LANGUAGE_OPTIONS = "options";

    /**
     * COMPILER column in LANGUAGE table.
     */
    public static final String LANGUAGE_COMPILER = "compiler";

    /**
     * CONTEST_LANGUAGE table.
     */
    public static final String CONTEST_LANGUAGE_TABLE = "contest_language";

    /**
     * CONTEST_ID column in CONTEST_LANGUAGE table.
     */
    public static final String CONTEST_LANGUAGE_CONTEST_ID = "contest_id";

    /**
     * LANGUAGE_ID column in CONTEST_LANGUAGE table.
     */
    public static final String CONTEST_LANGUAGE_LANGUAGE_ID = "language_id";

    /**
     * LIMITS table.
     */
    public static final String LIMITS_TABLE = "limits";

    /**
     * LIMITS_ID column in LIMITS table.
     */
    public static final String LIMITS_LIMITS_ID = "limits_id";

    /**
     * TIME_LIMIT column in LIMITS table.
     */
    public static final String LIMITS_TIME_LIMIT = "time_limit";

    /**
     * MEMORY_LIMIT column in LIMITS table.
     */
    public static final String LIMITS_MEMORY_LIMIT = "memory_limit";

    /**
     * OUTPUT_LIMIT column in LIMITS table.
     */
    public static final String LIMITS_OUTPUT_LIMIT = "output_limit";

    /**
     * SUBMISSION_LIMIT column in LIMITS table.
     */
    public static final String LIMITS_SUBMISSION_LIMIT = "submission_limit";

    /**
     * PROBLEM table.
     */
    public static final String PROBLEM_TABLE = "problem";

    /**
     * PROBLEM_ID column in PROBLEM table.
     */
    public static final String PROBLEM_PROBLEM_ID = "problem_id";

    /**
     * CONTEST_ID column in PROBLEM table.
     */
    public static final String PROBLEM_CONTEST_ID = "contest_id";

    /**
     * TITLE column in PROBLEM table.
     */
    public static final String PROBLEM_TITLE = "title";

    /**
     * CODE column in PROBLEM table.
     */
    public static final String PROBLEM_CODE = "code";

    /**
     * LIMITS_ID column in PROBLEM table.
     */
    public static final String PROBLEM_LIMITS_ID = "limits_id";

    /**
     * AUTHOR column in PROBLEM table.
     */
    public static final String PROBLEM_AUTHOR = "author";

    /**
     * SOURCE column in PROBLEM table.
     */
    public static final String PROBLEM_SOURCE = "source";

    /**
     * CONTEST column in PROBLEM table.
     */
    public static final String PROBLEM_CONTEST = "contest";

    /**
     * ACTIVE column in PROBLEM table.
     */
    public static final String PROBLEM_ACTIVE = "active";

    /**
     * CHECKER column in PROBLEM table.
     */
    public static final String PROBLEM_CHECKER = "checker";

    /**
     * REVISION column in PROBLEM table.
     */
    public static final String PROBLEM_REVISION = "revision";

    /**
     * color column in PROBLEM table.
     */
    public static final String PROBLEM_COLOR = "color";

    /**
     * score column in PROBLEM table.
     */
    public static final String PROBLEM_SCORE = "score";

    /**
     * SUBMISSION table.
     */
    public static final String SUBMISSION_TABLE = "submission";

    /**
     * SUBMISSION_ID column in SUBMISSION table.
     */
    public static final String SUBMISSION_SUBMISSION_ID = "submission_id";

    /**
     * PROBLEM_ID column in SUBMISSION table.
     */
    public static final String SUBMISSION_PROBLEM_ID = "problem_id";

    /**
     * LANGUAGE_ID column in SUBMISSION table.
     */
    public static final String SUBMISSION_LANGUAGE_ID = "language_id";

    /**
     * JUDGE_REPLY_ID column in SUBMISSION table.
     */
    public static final String SUBMISSION_JUDGE_REPLY_ID = "judge_reply_id";

    /**
     * USER_PROFILE_ID column in SUBMISSION table.
     */
    public static final String SUBMISSION_USER_PROFILE_ID = "user_profile_id";

    /**
     * CONTENT column in SUBMISSION table.
     */
    public static final String SUBMISSION_CONTENT = "content";

    /**
     * TIME_CONSUMPTION column in SUBMISSION table.
     */
    public static final String SUBMISSION_TIME_CONSUMPTION = "time_consumption";

    /**
     * MEMORY_CONSUMPTION column in SUBMISSION table.
     */
    public static final String SUBMISSION_MEMORY_CONSUMPTION = "memory_consumption";

    /**
     * SUBMISSION_DATE column in SUBMISSION table.
     */
    public static final String SUBMISSION_SUBMISSION_DATE = "submission_date";

    /**
     * JUDGE_DATE column in SUBMISSION table.
     */
    public static final String SUBMISSION_JUDGE_DATE = "judge_date";

    /**
     * JUDGE_COMMENT column in SUBMISSION table.
     */
    public static final String SUBMISSION_JUDGE_COMMENT = "judge_comment";

    /**
     * ACTIVE column in SUBMISSION table.
     */
    public static final String SUBMISSION_ACTIVE = "active";

    /**
     * JUDGE_REPLY table.
     */
    public static final String JUDGE_REPLY_TABLE = "judge_reply";

    /**
     * JUDGE_REPLY_ID column in JUDGE_REPLY table.
     */
    public static final String JUDGE_REPLY_JUDGE_REPLY_ID = "judge_reply_id";

    /**
     * NAME column in JUDGE_REPLY table.
     */
    public static final String JUDGE_REPLY_NAME = "name";

    /**
     * DESCRIPTION column in JUDGE_REPLY table.
     */
    public static final String JUDGE_REPLY_DESCRIPTION = "description";

    /**
     * STYLE column in JUDGE_REPLY table.
     */
    public static final String JUDGE_REPLY_STYLE = "style";

    /**
     * COMMITTED column in JUDGE_REPLY table.
     */
    public static final String JUDGE_REPLY_COMMITTED = "committed";

    /**
     * FORUM table.
     */
    public static final String FORUM_TABLE = "forum";

    /**
     * FORUM_ID column in FORUM table.
     */
    public static final String FORUM_FORUM_ID = "forum_id";

    /**
     * NAME column in FORUM table.
     */
    public static final String FORUM_NAME = "name";

    /**
     * DESCRIPTION column in FORUM table.
     */
    public static final String FORUM_DESCRIPTION = "description";

    /**
     * ACTIVE column in FORUM table.
     */
    public static final String FORUM_ACTIVE = "active";

    /**
     * THREAD table.
     */
    public static final String THREAD_TABLE = "thread";

    /**
     * THREAD_ID column in THREAD table.
     */
    public static final String THREAD_THREAD_ID = "thread_id";

    /**
     * FORUM_ID column in THREAD table.
     */
    public static final String THREAD_FORUM_ID = "forum_id";

    /**
     * USER_PROFILE_ID column in THREAD table.
     */
    public static final String THREAD_USER_PROFILE_ID = "user_profile_id";

    /**
     * TITLE column in THREAD table.
     */
    public static final String THREAD_TITLE = "title";

    /**
     * ACTIVE column in THREAD table.
     */
    public static final String THREAD_ACTIVE = "active";

    /**
     * POST table.
     */
    public static final String POST_TABLE = "post";

    /**
     * POST_ID column in POST table.
     */
    public static final String POST_POST_ID = "post_id";

    /**
     * THREAD_ID column in POST table.
     */
    public static final String POST_THREAD_ID = "thread_id";

    /**
     * USER_PROFILE_ID column in POST table.
     */
    public static final String POST_USER_PROFILE_ID = "user_profile_id";

    /**
     * CONTENT column in POST table.
     */
    public static final String POST_CONTENT = "content";

    /**
     * ACTIVE column in POST table.
     */
    public static final String POST_ACTIVE = "active";

    /**
     * REFERENCE table.
     */
    public static final String REFERENCE_TABLE = "reference";

    /**
     * REFERENCE_ID column in REFERENCE table.
     */
    public static final String REFERENCE_REFERENCE_ID = "reference_id";

    /**
     * REFERENCE_TYPE_ID column in REFERENCE table.
     */
    public static final String REFERENCE_REFERENCE_TYPE_ID = "reference_type_id";

    /**
     * NAME column in REFERENCE table.
     */
    public static final String REFERENCE_NAME = "name";

    /**
     * CONTENT_TYPE column in REFERENCE table.
     */
    public static final String REFERENCE_CONTENT_TYPE = "content_type";

    /**
     * CONTENT column in REFERENCE table.
     */
    public static final String REFERENCE_CONTENT = "content";

    /**
     * SIZE column in REFERENCE table.
     */
    public static final String REFERENCE_SIZE = "size";

    /**
     * COMPRESSED column in REFERENCE table.
     */
    public static final String REFERENCE_COMPRESSED = "compressed";

    /**
     * REFERENCE_TYPE table.
     */
    public static final String REFERENCE_TYPE_TABLE = "reference_type";

    /**
     * REFERENCE_TYPE_ID column in REFERENCE_TYPE table.
     */
    public static final String REFERENCE_TYPE_REFERENCE_TYPE_ID = "reference_type_id";

    /**
     * DESCRIPTION column in REFERENCE_TYPE table.
     */
    public static final String REFERENCE_TYPE_DESCRIPTION = "description";

    /**
     * CONTEST_REFERENCE table.
     */
    public static final String CONTEST_REFERENCE_TABLE = "contest_reference";

    /**
     * REFERENCE_ID column in CONTEST_REFERENCE table.
     */
    public static final String CONTEST_REFERENCE_REFERENCE_ID = "reference_id";

    /**
     * CONTEST_ID column in CONTEST_REFERENCE table.
     */
    public static final String CONTEST_REFERENCE_CONTEST_ID = "contest_id";

    /**
     * PROBLEM_REFERENCE table.
     */
    public static final String PROBLEM_REFERENCE_TABLE = "problem_reference";

    /**
     * REFERENCE_ID column in PROBLEM_REFERENCE table.
     */
    public static final String PROBLEM_REFERENCE_REFERENCE_ID = "reference_id";

    /**
     * PROBLEM_ID column in PROBLEM_REFERENCE table.
     */
    public static final String PROBLEM_REFERENCE_PROBLEM_ID = "problem_id";

    /**
     * FORUM_REFERENCE table.
     */
    public static final String FORUM_REFERENCE_TABLE = "forum_reference";

    /**
     * REFERENCE_ID column in FORUM_REFERENCE table.
     */
    public static final String FORUM_REFERENCE_REFERENCE_ID = "reference_id";

    /**
     * POST_ID column in FORUM_REFERENCE table.
     */
    public static final String FORUM_REFERENCE_POST_ID = "post_id";

    /**
     * CONFIGURATION table.
     */
    public static final String CONFIGURATION_TABLE = "configuration";

    /**
     * NAME column in CONFIGURATION table.
     */
    public static final String CONFIGURATION_NAME = "name";

    /**
     * VALUE column in CONFIGURATION table.
     */
    public static final String CONFIGURATION_VALUE = "value";

    /**
     * DESCRIPTION column in CONFIGURATION table.
     */
    public static final String CONFIGURATION_DESCRIPTION = "description";

    /**
     * CREATE_USER column.
     */
    public static final String CREATE_USER = "create_user";

    /**
     * CREATE_DATE column.
     */
    public static final String CREATE_DATE = "create_date";

    /**
     * LAST_UPDATE_USER column.
     */
    public static final String LAST_UPDATE_USER = "last_update_user";

    /**
     * LAST_UPDATE_DATE column.
     */
    public static final String LAST_UPDATE_DATE = "last_update_date";

    /**
     * CONTEST_CHECK_IP column.
     */
    public static final String CONTEST_CHECK_IP = "check_ip";

    public static final String SUBMISSION_USERSTAT_TABLE = "UserStat";
    public static final String SUBMISSION_PROBLEMSTAT_TABLE = "ProblemStat";

    /**
     * Private constructor.
     */
    private DatabaseConstants() {
    // empty
    }

}
