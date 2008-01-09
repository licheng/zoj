ALTER TABLE user_profile
    ADD CONSTRAINT fk_user_country FOREIGN KEY (country_id)
        REFERENCES country (country_id)
            ON DELETE RESTRICT;

ALTER TABLE user_preference
    ADD CONSTRAINT fk_user_preference FOREIGN KEY (user_profile_id)
        REFERENCES user_profile (user_profile_id)
            ON DELETE RESTRICT;

ALTER TABLE confirmation
    ADD CONSTRAINT fk_user_confirm FOREIGN KEY (user_profile_id)
        REFERENCES user_profile (user_profile_id)
            ON DELETE RESTRICT;

ALTER TABLE user_role
    ADD CONSTRAINT fk_user_role_user FOREIGN KEY (user_profile_id)
        REFERENCES user_profile (user_profile_id)
            ON DELETE RESTRICT;

ALTER TABLE user_role
    ADD CONSTRAINT fk_user_role_role FOREIGN KEY (role_id)
        REFERENCES role (role_id)
            ON DELETE RESTRICT;

ALTER TABLE contest_permission
    ADD CONSTRAINT fk_contest_permission_role FOREIGN KEY (role_id)
        REFERENCES role (role_id)
            ON DELETE RESTRICT;

ALTER TABLE contest_permission
    ADD CONSTRAINT fk_contest_permission_contest FOREIGN KEY (contest_id)
        REFERENCES contest (contest_id)
            ON DELETE RESTRICT;

ALTER TABLE contest_permission
    ADD CONSTRAINT fk_contest_permission_permission FOREIGN KEY (permission_level_id)
        REFERENCES permission_level (permission_level_id)
            ON DELETE RESTRICT;

ALTER TABLE forum_permission
    ADD CONSTRAINT fk_forum_permission_role FOREIGN KEY (role_id)
        REFERENCES role (role_id)
            ON DELETE RESTRICT;

ALTER TABLE forum_permission
    ADD CONSTRAINT fk_forum_permission_forum FOREIGN KEY (forum_id)
        REFERENCES forum (forum_id)
            ON DELETE RESTRICT;

ALTER TABLE forum_permission
    ADD CONSTRAINT fk_forum_permission_permission FOREIGN KEY (permission_level_id)
        REFERENCES permission_level (permission_level_id)
            ON DELETE RESTRICT;

ALTER TABLE contest
    ADD CONSTRAINT fk_contest_forum FOREIGN KEY (forum_id)
        REFERENCES forum (forum_id)
            ON DELETE RESTRICT;

ALTER TABLE contest
    ADD CONSTRAINT fk_contest_limits FOREIGN KEY (limits_id)
        REFERENCES limits (limits_id)
            ON DELETE RESTRICT;

ALTER TABLE contest_language
    ADD CONSTRAINT fk_contest_language_contest FOREIGN KEY (contest_id)
        REFERENCES contest (contest_id)
            ON DELETE RESTRICT;

ALTER TABLE contest_language
    ADD CONSTRAINT fk_contest_language_language FOREIGN KEY (language_id)
        REFERENCES language (language_id)
            ON DELETE RESTRICT;

ALTER TABLE problem
    ADD CONSTRAINT fk_contest_problem FOREIGN KEY (contest_id)
        REFERENCES contest (contest_id)
            ON DELETE RESTRICT;

ALTER TABLE problem
    ADD CONSTRAINT fk_problem_limits FOREIGN KEY (limits_id)
        REFERENCES limits (limits_id)
            ON DELETE RESTRICT;

ALTER TABLE submission
    ADD CONSTRAINT fk_submission_problem FOREIGN KEY (problem_id)
        REFERENCES problem (problem_id)
            ON DELETE RESTRICT;

ALTER TABLE submission
    ADD CONSTRAINT fk_submission_user FOREIGN KEY (user_profile_id)
        REFERENCES user_profile (user_profile_id)
            ON DELETE RESTRICT;

ALTER TABLE submission
    ADD CONSTRAINT fk_submission_language FOREIGN KEY (language_id)
        REFERENCES language (language_id)
            ON DELETE RESTRICT;

ALTER TABLE submission
    ADD CONSTRAINT fk_submission_reply FOREIGN KEY (judge_reply_id)
        REFERENCES judge_reply (judge_reply_id)
            ON DELETE RESTRICT;

ALTER TABLE thread
    ADD CONSTRAINT fk_thread_forum FOREIGN KEY (forum_id)
        REFERENCES forum (forum_id)
            ON DELETE RESTRICT;

ALTER TABLE thread
    ADD CONSTRAINT fk_thread_user FOREIGN KEY (user_profile_id)
        REFERENCES user_profile (user_profile_id)
            ON DELETE RESTRICT;

ALTER TABLE post
    ADD CONSTRAINT fk_post_thread FOREIGN KEY (thread_id)
        REFERENCES thread (thread_id)
            ON DELETE RESTRICT;

ALTER TABLE post
    ADD CONSTRAINT fk_post_user FOREIGN KEY (user_profile_id)
        REFERENCES user_profile (user_profile_id)
            ON DELETE RESTRICT;

ALTER TABLE reference
    ADD CONSTRAINT fk_reference_type FOREIGN KEY (reference_type_id)
        REFERENCES reference_type (reference_type_id)
            ON DELETE RESTRICT;

ALTER TABLE contest_reference
    ADD CONSTRAINT fk_contest_reference_contest FOREIGN KEY (contest_id)
        REFERENCES contest (contest_id)
            ON DELETE RESTRICT;

ALTER TABLE contest_reference
    ADD CONSTRAINT fk_contest_reference_reference FOREIGN KEY (reference_id)
        REFERENCES reference (reference_id)
            ON DELETE RESTRICT;

ALTER TABLE problem_reference
    ADD CONSTRAINT fk_problem_reference_problem FOREIGN KEY (problem_id)
        REFERENCES problem (problem_id)
            ON DELETE RESTRICT;

ALTER TABLE problem_reference
    ADD CONSTRAINT fk_problem_reference_reference FOREIGN KEY (reference_id)
        REFERENCES reference (reference_id)
            ON DELETE RESTRICT;

ALTER TABLE forum_reference
    ADD CONSTRAINT fk_forum_reference_post FOREIGN KEY (post_id)
        REFERENCES post (post_id)
            ON DELETE RESTRICT;

ALTER TABLE forum_reference
    ADD CONSTRAINT fk_forum_reference_reference FOREIGN KEY (reference_id)
        REFERENCES reference (reference_id)
            ON DELETE RESTRICT;
