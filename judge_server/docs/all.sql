DROP TABLE IF EXISTS access_log;
DROP TABLE IF EXISTS user_ac;
DROP TABLE IF EXISTS submission_status;
DROP TABLE IF EXISTS user_contest_ip;

DROP TABLE IF EXISTS contest_reference;
DROP TABLE IF EXISTS problem_reference;
DROP TABLE IF EXISTS forum_reference;

DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS user_preference;
DROP TABLE IF EXISTS confirmation;
DROP TABLE IF EXISTS password_confirmation;


DROP TABLE IF EXISTS contest_permission;
DROP TABLE IF EXISTS forum_permission;
DROP TABLE IF EXISTS permission_level;
DROP TABLE IF EXISTS contest_language;

DROP TABLE IF EXISTS role;

DROP TABLE IF EXISTS reference;
DROP TABLE IF EXISTS reference_type;
DROP TABLE IF EXISTS submission;
DROP TABLE IF EXISTS problem;
DROP TABLE IF EXISTS contest;
DROP TABLE IF EXISTS language;
DROP TABLE IF EXISTS judge_reply;

DROP TABLE IF EXISTS post;
DROP TABLE IF EXISTS thread;
DROP TABLE IF EXISTS forum;


DROP TABLE IF EXISTS user_profile;
DROP TABLE IF EXISTS country;
DROP TABLE IF EXISTS configuration;
DROP TABLE IF EXISTS limits;

CREATE TABLE country (
    country_id          BIGINT          NOT NULL PRIMARY KEY,
    name                VARCHAR(128)    NOT NULL,

    create_user         BIGINT          NOT NULL,
    create_date         DATETIME        NOT NULL,
    last_update_user    BIGINT          NOT NULL,
    last_update_date    DATETIME        NOT NULL
) ENGINE = InnoDb;

CREATE TABLE user_profile (
    user_profile_id     BIGINT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    handle              VARCHAR(16)     NOT NULL,
    nickname            VARCHAR(128)    NULL,
    password            VARCHAR(32)     NOT NULL,
    email_address       VARCHAR(128)    NOT NULL,
    reg_date            DATETIME        NOT NULL,

    first_name          VARCHAR(32)     NULL,
    last_name           VARCHAR(32)     NULL,
    address_line1       VARCHAR(128)    NULL,
    address_line2       VARCHAR(128)    NULL,
    city                VARCHAR(32)     NULL,
    state               VARCHAR(32)     NULL,
    country_id          BIGINT          NULL,
    zip_code            VARCHAR(32)     NULL,
    phone_number        VARCHAR(32)     NULL,
    birth_date          DATETIME        NULL,
    gender              CHAR(1)         NOT NULL DEFAULT ' ',

    school              VARCHAR(128)    NULL,
    major               VARCHAR(64)     NULL,
    graduate_student    TINYINT         NULL,
    graduation_year     SMALLINT        NULL,
    student_number      VARCHAR(32)     NULL,

    active              TINYINT         NOT NULL DEFAULT 1,
    confirmed           TINYINT         NOT NULL DEFAULT 0,
    super_admin         TINYINT         NOT NULL DEFAULT 0,

    last_login_date     DATETIME        NULL,
    last_login_ip       VARCHAR(256)    NULL,
    
    old_email           VARCHAR(128)    NULL,
    old_password        VARCHAR(32)     NULL,
    
    create_user         BIGINT          NOT NULL,
    create_date         DATETIME        NOT NULL,
    last_update_user    BIGINT          NOT NULL,
    last_update_date    DATETIME        NOT NULL
) ENGINE = InnoDb;

CREATE TABLE user_preference (
    user_profile_id     BIGINT          NOT NULL PRIMARY KEY,
    plan                TEXT            NULL,
    problem_paging      INT             NOT NULL,
    submission_paging   INT             NOT NULL,
    status_paging       INT             NOT NULL,
    user_paging         INT             NOT NULL,
    post_paging         INT             NOT NULL,
    thread_paging       INT             NOT NULL,

    create_user         BIGINT          NOT NULL,
    create_date         DATETIME        NOT NULL,
    last_update_user    BIGINT          NOT NULL,
    last_update_date    DATETIME        NOT NULL
) ENGINE = InnoDb;

CREATE TABLE confirmation (
    user_profile_id     BIGINT          NOT NULL PRIMARY KEY,
    code                VARCHAR(32)     NOT NULL
) ENGINE = InnoDb;

CREATE TABLE password_confirmation (
    user_profile_id     BIGINT          NOT NULL PRIMARY KEY,
    code                VARCHAR(32)     NOT NULL
) ENGINE = InnoDb;

CREATE TABLE role (
    role_id             BIGINT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name                VARCHAR(32)     NOT NULL,
    description         VARCHAR(128)    NOT NULL,

    create_user         BIGINT          NOT NULL,
    create_date         DATETIME        NOT NULL,
    last_update_user    BIGINT          NOT NULL,
    last_update_date    DATETIME        NOT NULL
) ENGINE = InnoDb;

CREATE TABLE user_role (
    user_profile_id     BIGINT          NOT NULL,
    role_id             BIGINT          NOT NULL,
    PRIMARY KEY (user_profile_id, role_id)
) ENGINE = InnoDb;

CREATE TABLE contest_permission (
    role_id             BIGINT          NOT NULL,
    contest_id          BIGINT          NOT NULL,
    permission_level_id BIGINT          NOT NULL,
    PRIMARY KEY (role_id, contest_id)
) ENGINE = InnoDb;

CREATE TABLE forum_permission (
    role_id             BIGINT          NOT NULL,
    forum_id            BIGINT          NOT NULL,
    permission_level_id BIGINT          NOT NULL,
    PRIMARY KEY (role_id, forum_id)
) ENGINE = InnoDb;

CREATE TABLE permission_level (
    permission_level_id BIGINT          NOT NULL PRIMARY KEY,
    description         VARCHAR(32)     NOT NULL,

    create_user         BIGINT          NOT NULL,
    create_date         DATETIME        NOT NULL,
    last_update_user    BIGINT          NOT NULL,
    last_update_date    DATETIME        NOT NULL
) ENGINE = InnoDb;

CREATE TABLE contest (
    contest_id          BIGINT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    title               VARCHAR(128)    NOT NULL,
    description         VARCHAR(256)    NULL,
    start_time          DATETIME        NULL,
    end_time            DATETIME        NULL,
    forum_id            BIGINT          NOT NULL,
    limits_id           BIGINT          NOT NULL DEFAULT 1,

    check_ip            TINYINT         NOT NULL DEFAULT 0,
    active              TINYINT         NOT NULL DEFAULT 1,
    problemset          TINYINT         NOT NULL DEFAULT 0,

    create_user         BIGINT          NOT NULL,
    create_date         DATETIME        NOT NULL,
    last_update_user    BIGINT          NOT NULL,
    last_update_date    DATETIME        NOT NULL
) ENGINE = InnoDb;

CREATE TABLE language (
    language_id         BIGINT          NOT NULL PRIMARY KEY,
    name                VARCHAR(32)     NOT NULL,
    description         VARCHAR(128)    NULL,
    options             VARCHAR(128)    NULL,
    compiler            VARCHAR(128)    NULL,
    extension           VARCHAR(32)     NULL,
    create_user         BIGINT          NOT NULL,
    create_date         DATETIME        NOT NULL,
    last_update_user    BIGINT          NOT NULL,
    last_update_date    DATETIME        NOT NULL
) ENGINE = InnoDb;

CREATE TABLE contest_language (
    contest_id          BIGINT          NOT NULL,
    language_id         BIGINT          NOT NULL,
    PRIMARY KEY (contest_id, language_id)
) ENGINE = InnoDb;

CREATE TABLE limits (
    limits_id           BIGINT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    time_limit          INT             NOT NULL,
    memory_limit        INT             NOT NULL,
    output_limit        INT             NOT NULL,
    submission_limit    INT             NOT NULL
) ENGINE = InnoDb;

CREATE TABLE problem (
    problem_id          BIGINT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    contest_id          BIGINT          NOT NULL,
    title               VARCHAR(128)    NOT NULL,
    code                VARCHAR(8)      NOT NULL,
    limits_id           BIGINT          NOT NULL DEFAULT 1,

    color               VARCHAR(32)     NULL,
    author              VARCHAR(32)     NULL,
    source              VARCHAR(128)    NULL,
    contest             VARCHAR(128)    NULL,

    active              TINYINT         NOT NULL DEFAULT 1,
    checker             TINYINT         NOT NULL DEFAULT 0,
    revision            INT             NOT NULL DEFAULT 1,

    create_user         BIGINT          NOT NULL,
    create_date         DATETIME        NOT NULL,
    last_update_user    BIGINT          NOT NULL,
    last_update_date    DATETIME        NOT NULL,
    
    score    INT        NOT NULL DEFAULT 0
) ENGINE = InnoDb;

CREATE TABLE submission (
    submission_id       BIGINT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    problem_id          BIGINT          NOT NULL,
    language_id         BIGINT          NOT NULL,
    judge_reply_id      BIGINT          NOT NULL,
    user_profile_id     BIGINT          NOT NULL,

    contest_id          BIGINT          NOT NULL,
    contest_order       BIGINT          NOT NULL DEFAULT -1,

    content             LONGTEXT        NULL,
    time_consumption    INT             NULL,
    memory_consumption  INT             NULL,
    submission_date     DATETIME        NULL,

    judge_date          DATETIME        NULL,
    judge_comment       LONGTEXT        NULL,

    active              TINYINT         NOT NULL DEFAULT 1,

    create_user         BIGINT          NOT NULL,
    create_date         DATETIME        NOT NULL,
    last_update_user    BIGINT          NOT NULL,
    last_update_date    DATETIME        NOT NULL
) ENGINE = InnoDb;

CREATE TABLE judge_reply (
    judge_reply_id      BIGINT          NOT NULL PRIMARY KEY,
    name                VARCHAR(32)     NOT NULL,
    description         VARCHAR(128)    NULL,
    style               VARCHAR(32)     NULL,
    committed           TINYINT         NOT NULL,

    create_user         BIGINT          NOT NULL,
    create_date         DATETIME        NOT NULL,
    last_update_user    BIGINT          NOT NULL,
    last_update_date    DATETIME        NOT NULL
) ENGINE = InnoDb;

CREATE TABLE forum (
    forum_id            BIGINT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name                VARCHAR(32)     NOT NULL,
    description         VARCHAR(128)    NULL,

    active              TINYINT         NOT NULL DEFAULT 1,

    create_user         BIGINT          NOT NULL,
    create_date         DATETIME        NOT NULL,
    last_update_user    BIGINT          NOT NULL,
    last_update_date    DATETIME        NOT NULL
) ENGINE = InnoDb;

CREATE TABLE thread (
    thread_id           BIGINT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    forum_id            BIGINT          NOT NULL,
    user_profile_id     BIGINT          NOT NULL,
    title               VARCHAR(128)    NOT NULL,

    active              TINYINT         NOT NULL DEFAULT 1,

    create_user         BIGINT          NOT NULL,
    create_date         DATETIME        NOT NULL,
    last_update_user    BIGINT          NOT NULL,
    last_update_date    DATETIME        NOT NULL
) ENGINE = InnoDb;

CREATE TABLE post (
    post_id             BIGINT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    thread_id           BIGINT          NOT NULL,
    user_profile_id     BIGINT          NOT NULL,
    content             TEXT            NOT NULL,

    active              TINYINT         NOT NULL DEFAULT 1,

    create_user         BIGINT          NOT NULL,
    create_date         DATETIME        NOT NULL,
    last_update_user    BIGINT          NOT NULL,
    last_update_date    DATETIME        NOT NULL
) ENGINE = InnoDb;

CREATE TABLE reference (
    reference_id        BIGINT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    reference_type_id   BIGINT          NOT NULL,
    name                VARCHAR(32)     NULL,
    content_type        VARCHAR(32)     NULL,
    content             LONGBLOB        NOT NULL,
    size                BIGINT          NOT NULL,
    compressed          TINYINT         NOT NULL DEFAULT 0,

    create_user         BIGINT          NOT NULL,
    create_date         DATETIME        NOT NULL,
    last_update_user    BIGINT          NOT NULL,
    last_update_date    DATETIME        NOT NULL
) ENGINE = InnoDb;

CREATE TABLE reference_type (
    reference_type_id   BIGINT          NOT NULL PRIMARY KEY,
    description         VARCHAR(32)     NOT NULL,

    create_user         BIGINT          NOT NULL,
    create_date         DATETIME        NOT NULL,
    last_update_user    BIGINT          NOT NULL,
    last_update_date    DATETIME        NOT NULL
) ENGINE = InnoDb;

CREATE TABLE contest_reference (
    reference_id        BIGINT          NOT NULL PRIMARY KEY,
    contest_id          BIGINT          NOT NULL
) ENGINE = InnoDb;

CREATE TABLE problem_reference (
    reference_id        BIGINT          NOT NULL PRIMARY KEY,
    problem_id          BIGINT          NOT NULL
) ENGINE = InnoDb;

CREATE TABLE forum_reference (
    reference_id        BIGINT          NOT NULL PRIMARY KEY,
    post_id             BIGINT          NOT NULL
) ENGINE = InnoDb;

CREATE TABLE configuration (
    name                VARCHAR(32)     NOT NULL PRIMARY KEY,
    value               VARCHAR(128)    NOT NULL,
    description         VARCHAR(128)    NOT NULL,

    create_user         BIGINT          NOT NULL,
    create_date         DATETIME        NOT NULL,
    last_update_user    BIGINT          NOT NULL,
    last_update_date    DATETIME        NOT NULL
) ENGINE = InnoDb;

CREATE TABLE user_contest_ip (
    user_profile_id     BIGINT          NOT NULL,
    contest_id          BIGINT          NOT NULL,    
    ip                  VARCHAR(128)    NULL,
    PRIMARY KEY (user_profile_id, contest_id)
) ENGINE = InnoDb;

CREATE TABLE submission_status (
    user_profile_id     BIGINT          NOT NULL,
    problem_id          BIGINT          NOT NULL,   
    status              VARCHAR(32)     NULL  
) ENGINE = InnoDb;


CREATE TABLE access_log (
    access_log_id          BIGINT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_profile_id        BIGINT          NULL,
    handle                 VARCHAR(32)     NULL,
    action                 VARCHAR(128)    NOT NULL,
    url                    VARCHAR(512)    NOT NULL,
    ip                     VARCHAR(64)     NOT NULL,
    timestamp              DATETIME        NOT NULL,
    access_time             BIGINT          NOT NULL
) ENGINE = InnoDb;

CREATE INDEX index_access_log_timestamp
        ON access_log (timestamp);

CREATE TABLE user_ac (
    user_profile_id     BIGINT          NOT NULL,
    contest_id          BIGINT          NOT NULL,
    solved              BIGINT          NOT NULL DEFAULT 0,
    tiebreak            BIGINT          NULL,
    PRIMARY KEY (user_profile_id, contest_id)   
) ENGINE = InnoDb;

ALTER TABLE user_ac
    ADD CONSTRAINT fk_user_ac_user_profile FOREIGN KEY (user_profile_id)
        REFERENCES user_profile (user_profile_id)
        	ON DELETE RESTRICT;
ALTER TABLE user_ac
    ADD CONSTRAINT fk_user_ac_contest FOREIGN KEY (contest_id)
        REFERENCES contest (contest_id)
        	ON DELETE RESTRICT;


ALTER TABLE user_contest_ip
    ADD CONSTRAINT fk_user_contest_ip_user_profile FOREIGN KEY (user_profile_id)
        REFERENCES user_profile (user_profile_id)
            ON DELETE RESTRICT;

ALTER TABLE user_contest_ip
    ADD CONSTRAINT fk_user_contest_ip_contest FOREIGN KEY (contest_id)
        REFERENCES contest (contest_id)
            ON DELETE RESTRICT;            
            
CREATE UNIQUE INDEX unique_handle
        ON user_profile (handle);

CREATE UNIQUE INDEX unique_email
        ON user_profile (email_address);

CREATE UNIQUE INDEX unique_confirmation
        ON confirmation (code);

CREATE UNIQUE INDEX unique_language
        ON language (name);

CREATE UNIQUE INDEX index_problem_code
        ON problem (contest_id, code);

CREATE INDEX index_problem_title
        ON problem (contest_id, title);

CREATE UNIQUE INDEX unique_reply
        ON judge_reply (name);

CREATE INDEX index_submission_contest_order 
        ON submission (contest_id, contest_order);

CREATE INDEX index_submission_user_reply_contest
        ON submission (user_profile_id, judge_reply_id, contest_id);

CREATE INDEX index_submission_problem_reply
        ON submission (problem_id, judge_reply_id);

CREATE INDEX index_contest_reference
        ON contest_reference (contest_id);

CREATE INDEX index_problem_reference
        ON problem_reference (problem_id);

CREATE INDEX index_forum_reference
        ON forum_reference (post_id);

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
    ADD CONSTRAINT fk_submission_contest FOREIGN KEY (contest_id)
        REFERENCES contest (contest_id)
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

ALTER TABLE submission_status
    ADD CONSTRAINT fk_submission_status_user_profile_id FOREIGN KEY (user_profile_id)
        REFERENCES user_profile (user_profile_id)
            ON DELETE RESTRICT;
ALTER TABLE submission_status
    ADD CONSTRAINT fk_submission_status_problem_id FOREIGN KEY (problem_id)
        REFERENCES problem (problem_id)
            ON DELETE RESTRICT;    

DELETE FROM country;

INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(1, 'Afghanistan', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(2, 'Albania', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(3, 'Algeria', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(4, 'American Samoa', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(5, 'Andorra', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(6, 'Angola', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(7, 'Anguilla', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(8, 'Antarctica', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(9, 'Antigua and Barbuda', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(10, 'Argentina', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(11, 'Armenia', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(12, 'Aruba', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(13, 'Australia', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(14, 'Austria', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(15, 'Azerbaijan', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(16, 'Bahamas', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(17, 'Bahrain', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(18, 'Bangladesh', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(19, 'Barbados', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(20, 'Belarus', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(21, 'Belgium', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(22, 'Belize', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(23, 'Benin', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(24, 'Bermuda', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(25, 'Bhutan', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(26, 'Bolivia', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(27, 'Bosnia and Herzegovina', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(28, 'Botswana', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(29, 'Bouvet Island', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(30, 'Brazil', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(31, 'British Indian Ocean Territory', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(32, 'Brunei Darussalam', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(33, 'Bulgaria', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(34, 'Burkina Faso', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(35, 'Burundi', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(36, 'Cambodia', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(37, 'Cameroon', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(38, 'Canada', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(39, 'Cape Verde', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(40, 'Cayman Islands', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(41, 'Central African Republic', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(42, 'Chad', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(43, 'Chile', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(44, 'China', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(45, 'Christmas Island', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(46, 'Cocos (Keeling) Islands', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(47, 'Colombia', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(48, 'Comoros', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(49, 'Congo', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(50, 'Congo, the Democratic Republic of the', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(51, 'Cook Islands', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(52, 'Costa Rica', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(53, 'Cote D\'Ivoire', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(54, 'Croatia', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(55, 'Cuba', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(56, 'Cyprus', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(57, 'Czech Republic', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(58, 'Denmark', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(59, 'Djibouti', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(60, 'Dominica', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(61, 'Dominican Republic', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(62, 'Ecuador', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(63, 'Egypt', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(64, 'El Salvador', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(65, 'Equatorial Guinea', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(66, 'Eritrea', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(67, 'Estonia', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(68, 'Ethiopia', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(69, 'Falkland Islands (Malvinas)', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(70, 'Faroe Islands', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(71, 'Fiji', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(72, 'Finland', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(73, 'France', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(74, 'French Guiana', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(75, 'French Polynesia', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(76, 'French Southern Territories', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(77, 'Gabon', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(78, 'Gambia', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(79, 'Georgia', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(80, 'Germany', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(81, 'Ghana', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(82, 'Gibraltar', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(83, 'Greece', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(84, 'Greenland', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(85, 'Grenada', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(86, 'Guadeloupe', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(87, 'Guam', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(88, 'Guatemala', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(89, 'Guinea', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(90, 'Guinea-Bissau', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(91, 'Guyana', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(92, 'Haiti', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(93, 'Heard Island and Mcdonald Islands', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(94, 'Holy See (Vatican City State)', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(95, 'Honduras', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(96, 'Hong Kong', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(97, 'Hungary', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(98, 'Iceland', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(99, 'India', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(100, 'Indonesia', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(101, 'Iran, Islamic Republic of', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(102, 'Iraq', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(103, 'Ireland', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(104, 'Israel', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(105, 'Italy', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(106, 'Jamaica', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(107, 'Japan', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(108, 'Jordan', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(109, 'Kazakhstan', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(110, 'Kenya', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(111, 'Kiribati', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(112, 'Korea, Democratic People\'s Republic of', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(113, 'Korea, Republic of', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(114, 'Kuwait', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(115, 'Kyrgyzstan', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(116, 'Lao People\'s Democratic Republic', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(117, 'Latvia', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(118, 'Lebanon', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(119, 'Lesotho', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(120, 'Liberia', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(121, 'Libyan Arab Jamahiriya', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(122, 'Liechtenstein', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(123, 'Lithuania', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(124, 'Luxembourg', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(125, 'Macao', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(126, 'Macedonia, the Former Yugoslav Republic of', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(127, 'Madagascar', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(128, 'Malawi', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(129, 'Malaysia', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(130, 'Maldives', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(131, 'Mali', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(132, 'Malta', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(133, 'Marshall Islands', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(134, 'Martinique', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(135, 'Mauritania', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(136, 'Mauritius', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(137, 'Mayotte', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(138, 'Mexico', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(139, 'Micronesia, Federated States of', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(140, 'Moldova, Republic of', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(141, 'Monaco', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(142, 'Mongolia', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(143, 'Montserrat', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(144, 'Morocco', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(145, 'Mozambique', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(146, 'Myanmar', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(147, 'Namibia', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(148, 'Nauru', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(149, 'Nepal', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(150, 'Netherlands', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(151, 'Netherlands Antilles', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(152, 'New Caledonia', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(153, 'New Zealand', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(154, 'Nicaragua', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(155, 'Niger', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(156, 'Nigeria', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(157, 'Niue', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(158, 'Norfolk Island', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(159, 'Northern Mariana Islands', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(160, 'Norway', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(161, 'Oman', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(162, 'Pakistan', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(163, 'Palau', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(164, 'Palestinian Territory, Occupied', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(165, 'Panama', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(166, 'Papua New Guinea', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(167, 'Paraguay', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(168, 'Peru', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(169, 'Philippines', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(170, 'Pitcairn', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(171, 'Poland', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(172, 'Portugal', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(173, 'Puerto Rico', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(174, 'Qatar', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(175, 'Reunion', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(176, 'Romania', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(177, 'Russian Federation', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(178, 'Rwanda', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(179, 'Saint Helena', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(180, 'Saint Kitts and Nevis', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(181, 'Saint Lucia', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(182, 'Saint Pierre and Miquelon', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(183, 'Saint Vincent and the Grenadines', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(184, 'Samoa', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(185, 'San Marino', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(186, 'Sao Tome and Principe', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(187, 'Saudi Arabia', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(188, 'Senegal', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(189, 'Serbia and Montenegro', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(190, 'Seychelles', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(191, 'Sierra Leone', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(192, 'Singapore', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(193, 'Slovakia', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(194, 'Slovenia', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(195, 'Solomon Islands', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(196, 'Somalia', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(197, 'South Africa', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(198, 'South Georgia and the South Sandwich Islands', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(199, 'Spain', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(200, 'Sri Lanka', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(201, 'Sudan', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(202, 'Suriname', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(203, 'Svalbard and Jan Mayen', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(204, 'Swaziland', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(205, 'Sweden', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(206, 'Switzerland', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(207, 'Syrian Arab Republic', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(208, 'Taiwan, Province of China', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(209, 'Tajikistan', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(210, 'Tanzania, United Republic of', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(211, 'Thailand', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(212, 'Timor-Leste', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(213, 'Togo', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(214, 'Tokelau', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(215, 'Tonga', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(216, 'Trinidad and Tobago', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(217, 'Tunisia', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(218, 'Turkey', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(219, 'Turkmenistan', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(220, 'Turks and Caicos Islands', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(221, 'Tuvalu', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(222, 'Uganda', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(223, 'Ukraine', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(224, 'United Arab Emirates', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(225, 'United Kingdom', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(226, 'United States', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(227, 'United States Minor Outlying Islands', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(228, 'Uruguay', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(229, 'Uzbekistan', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(230, 'Vanuatu', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(231, 'Venezuela', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(232, 'Viet Nam', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(233, 'Virgin Islands, British', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(234, 'Virgin Islands, U.s.', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(235, 'Wallis and Futuna', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(236, 'Western Sahara', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(237, 'Yemen', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(238, 'Zambia', 1, NOW(), 1, NOW());
INSERT INTO country(country_id, name, create_user, create_date, last_update_user, last_update_date) VALUES(239, 'Zimbabwe', 1, NOW(), 1, NOW());

DELETE FROM role;

INSERT INTO role(role_id, name, description, create_user, create_date, last_update_user, last_update_date) VALUES(1, 'Anonymous', 'The privileges anonymous user poccesses.', 1, NOW(), 1, NOW());
INSERT INTO role(role_id, name, description, create_user, create_date, last_update_user, last_update_date) VALUES(2, 'User', 'The registered users.', 1, NOW(), 1, NOW());

DELETE FROM permission_level;

INSERT INTO permission_level(permission_level_id, description, create_user, create_date, last_update_user, last_update_date) VALUES(1, 'View', 1, NOW(), 1, NOW());
INSERT INTO permission_level(permission_level_id, description, create_user, create_date, last_update_user, last_update_date) VALUES(2, 'Participate', 1, NOW(), 1, NOW());
INSERT INTO permission_level(permission_level_id, description, create_user, create_date, last_update_user, last_update_date) VALUES(3, 'ParticipateViewSource', 1, NOW(), 1, NOW());
INSERT INTO permission_level(permission_level_id, description, create_user, create_date, last_update_user, last_update_date) VALUES(4, 'Admin', 1, NOW(), 1, NOW());

DELETE FROM limits;

INSERT INTO limits(limits_id, time_limit, memory_limit, output_limit, submission_limit) VALUES(1, 1, 32768, 16000, 32);

DELETE FROM reference_type;

INSERT INTO reference_type(reference_type_id, description, create_user, create_date, last_update_user, last_update_date) VALUES(1, 'Description', 1, NOW(), 1, NOW());
INSERT INTO reference_type(reference_type_id, description, create_user, create_date, last_update_user, last_update_date) VALUES(2, 'Input', 1, NOW(), 1, NOW());
INSERT INTO reference_type(reference_type_id, description, create_user, create_date, last_update_user, last_update_date) VALUES(3, 'Output', 1, NOW(), 1, NOW());
INSERT INTO reference_type(reference_type_id, description, create_user, create_date, last_update_user, last_update_date) VALUES(4, 'Auxiliary', 1, NOW(), 1, NOW());
INSERT INTO reference_type(reference_type_id, description, create_user, create_date, last_update_user, last_update_date) VALUES(5, 'Checker', 1, NOW(), 1, NOW());
INSERT INTO reference_type(reference_type_id, description, create_user, create_date, last_update_user, last_update_date) VALUES(6, 'Checker Source', 1, NOW(), 1, NOW());
INSERT INTO reference_type(reference_type_id, description, create_user, create_date, last_update_user, last_update_date) VALUES(7, 'Judge Solution', 1, NOW(), 1, NOW());
INSERT INTO reference_type(reference_type_id, description, create_user, create_date, last_update_user, last_update_date) VALUES(8, 'Download', 1, NOW(), 1, NOW());
INSERT INTO reference_type(reference_type_id, description, create_user, create_date, last_update_user, last_update_date) VALUES(9, 'Misc', 1, NOW(), 1, NOW());

DELETE FROM configuration;

DELETE FROM language;

INSERT INTO language (language_id, name, description, options, compiler, create_user, create_date, last_update_user, last_update_date)
  VALUES(1, 'C', 'C', 'c', 'gcc x.x.x', 1, NOW(), 1, NOW());
INSERT INTO language (language_id, name, description, options, compiler, create_user, create_date, last_update_user, last_update_date)
  VALUES(2, 'C++', 'C++', 'cc', 'g++ x.x.x', 1, NOW(), 1, NOW());
INSERT INTO language (language_id, name, description, options, compiler, create_user, create_date, last_update_user, last_update_date)
  VALUES(3, 'FPC', 'Free Pascal', 'pas', 'Free Pascal', 1, NOW(), 1, NOW());
INSERT INTO language (language_id, name, description, options, compiler, create_user, create_date, last_update_user, last_update_date)
  VALUES(4, 'Java', 'Java', 'java', 'java version 1.6.0_06', 1, NOW(), 1, NOW());


DELETE FROM forum;

INSERT INTO forum (forum_id, name, description, create_user, create_date, last_update_user, last_update_date)
  VALUES(1, 'ZOJ Forum', 'Zhejiang University Online Judge Forum', 1, NOW(), 1, NOW());



DELETE FROM judge_reply;
INSERT INTO judge_reply (judge_reply_id, name, description, committed, create_user, create_date, last_update_user, last_update_date)
VALUES(0, 'Queuing', 'Queuing', false, 1, NOW(), 1, NOW());
INSERT INTO judge_reply (judge_reply_id, name, description, committed, create_user, create_date, last_update_user, last_update_date)
VALUES(1, 'Compiling', 'Compiling', false, 1, NOW(), 1, NOW());
INSERT INTO judge_reply (judge_reply_id, name, description, committed, create_user, create_date, last_update_user, last_update_date)
VALUES(2, 'Running', 'Running', false, 1, NOW(), 1, NOW());
INSERT INTO judge_reply (judge_reply_id, name, description, committed, create_user, create_date, last_update_user, last_update_date)
VALUES(3, 'Runtime Error', 'Runtime Error', false, 1, NOW(), 1, NOW());
INSERT INTO judge_reply (judge_reply_id, name, description, committed, create_user, create_date, last_update_user, last_update_date)
VALUES(4, 'Wrong Answer', 'Wrong Answer', false, 1, NOW(), 1, NOW());
INSERT INTO judge_reply (judge_reply_id, name, description, committed, create_user, create_date, last_update_user, last_update_date)
VALUES(5, 'Accepted', 'Accepted', false, 1, NOW(), 1, NOW());
INSERT INTO judge_reply (judge_reply_id, name, description, committed, create_user, create_date, last_update_user, last_update_date)
VALUES(6, 'Time Limit Exceeded', 'Time Limit Exceeded', false, 1, NOW(), 1, NOW());
INSERT INTO judge_reply (judge_reply_id, name, description, committed, create_user, create_date, last_update_user, last_update_date)
VALUES(7, 'Memory Limit Exceeded', 'Memory Limit Exceeded', false, 1, NOW(), 1, NOW());
INSERT INTO judge_reply (judge_reply_id, name, description, committed, create_user, create_date, last_update_user, last_update_date)
VALUES(8, 'Out of Contest Time', 'Out of Contest Time', false, 1, NOW(), 1, NOW());
INSERT INTO judge_reply (judge_reply_id, name, description, committed, create_user, create_date, last_update_user, last_update_date)
VALUES(9, 'Restricted Function', 'Restricted Function', false, 1, NOW(), 1, NOW());
INSERT INTO judge_reply (judge_reply_id, name, description, committed, create_user, create_date, last_update_user, last_update_date)
VALUES(10, 'Output Limit Exceeded', 'Output Limit Exceeded', false, 1, NOW(), 1, NOW());
INSERT INTO judge_reply (judge_reply_id, name, description, committed, create_user, create_date, last_update_user, last_update_date)
VALUES(11, 'No such Problem', 'No such Problem', false, 1, NOW(), 1, NOW());
INSERT INTO judge_reply (judge_reply_id, name, description, committed, create_user, create_date, last_update_user, last_update_date)
VALUES(12, 'Compilation Error', 'Compilation Error', false, 1, NOW(), 1, NOW());
INSERT INTO judge_reply (judge_reply_id, name, description, committed, create_user, create_date, last_update_user, last_update_date)
VALUES(13, 'Presentation Error', 'Presentation Error', false, 1, NOW(), 1, NOW());
INSERT INTO judge_reply (judge_reply_id, name, description, committed, create_user, create_date, last_update_user, last_update_date)
VALUES(14, 'Judge Internal Error', 'Judge Internal Error', false, 1, NOW(), 1, NOW());
INSERT INTO judge_reply (judge_reply_id, name, description, committed, create_user, create_date, last_update_user, last_update_date)
VALUES(15, 'Floating Point Error', 'Floating Point Error', false, 1, NOW(), 1, NOW());
INSERT INTO judge_reply (judge_reply_id, name, description, committed, create_user, create_date, last_update_user, last_update_date)
VALUES(16, 'Segmentation Fault', 'Segmentation Fault', false, 1, NOW(), 1, NOW());
INSERT INTO judge_reply (judge_reply_id, name, description, committed, create_user, create_date, last_update_user, last_update_date)
VALUES(17, 'Prepare Compilation', 'Prepare Compilation', false, 1, NOW(), 1, NOW());
INSERT INTO judge_reply (judge_reply_id, name, description, committed, create_user, create_date, last_update_user, last_update_date)
VALUES(18, 'Prepare Execution', 'Prepare Execution', false, 1, NOW(), 1, NOW());
INSERT INTO judge_reply (judge_reply_id, name, description, committed, create_user, create_date, last_update_user, last_update_date)
VALUES(19, 'judging', 'Judging', false, 1, NOW(), 1, NOW());
INSERT INTO judge_reply (judge_reply_id, name, description, committed, create_user, create_date, last_update_user, last_update_date)
VALUES(20, 'Submission Limit Exceeded', 'Submission Limit Exceeded', false, 1, NOW(), 1, NOW());
INSERT INTO judge_reply (judge_reply_id, name, description, committed, create_user, create_date, last_update_user, last_update_date)
VALUES(101, 'Aborted', 'Aborted', false, 1, NOW(), 1, NOW());

INSERT INTO user_profile(user_profile_id, handle, nickname, password, email_address, reg_date, first_name, last_name, address_line1, address_line2, city, state, country_id, zip_code, phone_number, 
birth_date, gender, school, major, graduate_student, graduation_year, student_number, active, confirmed, super_admin, create_user, create_date, last_update_user, last_update_date)
VALUES(1, 'admin', 'admin', MD5('admin'), 'admin@admin.com', NOW(), 'first', 'last', 'address_line1', 'address_line2', 'city', 'state', 44, 'zip', 'phone', 
NOW(), 'M', NULL, NULL, 0, NULL, NULL, 1, 1, 1, 1, NOW(), 1, NOW());

INSERT INTO user_preference(user_profile_id, plan, problem_paging, submission_paging, status_paging, user_paging, post_paging, thread_paging, create_user, create_date, last_update_user, last_update_date)
VALUES(1, '', 100, 20, 20, 20, 20, 20, 1, NOW(), 1, NOW());
