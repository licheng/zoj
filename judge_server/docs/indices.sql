CREATE UNIQUE INDEX unique_handle
        ON user_profile (handle);

CREATE UNIQUE INDEX unique_email
        ON user_profile (email_address);

CREATE UNIQUE INDEX unique_confirmation
        ON confirmation (code);

CREATE UNIQUE INDEX unique_language
        ON language (name);

CREATE INDEX index_problem_code
        ON problem (contest_id, code);

CREATE INDEX index_problem_title
        ON problem (contest_id, title);

CREATE UNIQUE INDEX unique_reply
        ON judge_reply (name);

CREATE INDEX index_submission_user
        ON submission (user_profile_id, problem_id, judge_reply_id);

CREATE INDEX index_submission_problem
        ON submission (problem_id, language_id, judge_reply_id);

CREATE INDEX index_contest_reference
        ON contest_reference (contest_id);

CREATE INDEX index_problem_reference
        ON problem_reference (problem_id);

CREATE INDEX index_forum_reference
        ON forum_reference (post_id);
