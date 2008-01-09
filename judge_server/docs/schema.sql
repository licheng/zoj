DROP TABLE IF EXISTS country;
DROP TABLE IF EXISTS user_profile;
DROP TABLE IF EXISTS user_preference;
DROP TABLE IF EXISTS confirmation;

DROP TABLE IF EXISTS role;
DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS contest_permission;
DROP TABLE IF EXISTS forum_permission;
DROP TABLE IF EXISTS permission_level;

DROP TABLE IF EXISTS contest;
DROP TABLE IF EXISTS language;
DROP TABLE IF EXISTS contest_language;
DROP TABLE IF EXISTS limits;

DROP TABLE IF EXISTS problem;

DROP TABLE IF EXISTS submission;
DROP TABLE IF EXISTS judge_reply;

DROP TABLE IF EXISTS forum;
DROP TABLE IF EXISTS thread;
DROP TABLE IF EXISTS post;

DROP TABLE IF EXISTS reference;
DROP TABLE IF EXISTS reference_type;
DROP TABLE IF EXISTS contest_reference;
DROP TABLE IF EXISTS problem_reference;
DROP TABLE IF EXISTS forum_reference;

DROP TABLE IF EXISTS configuration;

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
    password            VARCHAR(32)     NOT NULL,
    email_address       VARCHAR(128)    NOT NULL,
    nickname            VARCHAR(64)     NULL,
    reg_date            DATETIME        NOT NULL,

    first_name          VARCHAR(32)     NOT NULL,
    last_name           VARCHAR(32)     NOT NULL,
    address_line1       VARCHAR(128)    NOT NULL,
    address_line2       VARCHAR(128)    NULL,
    city                VARCHAR(32)     NOT NULL,
    state               VARCHAR(32)     NOT NULL,
    country_id          BIGINT          NOT NULL,
    zip_code            VARCHAR(32)     NOT NULL,
    phone_number        VARCHAR(32)     NOT NULL,
    birth_date          DATETIME        NOT NULL,
    gender              CHAR(1)         NOT NULL DEFAULT ' ',

    school              VARCHAR(128)    NULL,
    major               VARCHAR(32)     NULL,
    graduate_student    TINYINT         NULL,
    graduation_year     SMALLINT        NULL,
    student_number      VARCHAR(32)     NULL,

    active              TINYINT         NOT NULL DEFAULT 1,
    confirmed           TINYINT         NOT NULL DEFAULT 0,
    super_admin         TINYINT         NOT NULL DEFAULT 0,

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
    description         VARCHAR(128)    NULL,
    start_time          DATETIME        NULL,
    end_time            DATETIME        NULL,
    forum_id            BIGINT          NOT NULL,
    limits_id           BIGINT          NOT NULL DEFAULT 1,

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

    author              VARCHAR(32)     NULL,
    source              VARCHAR(128)    NULL,
    contest             VARCHAR(128)    NULL,

    active              TINYINT         NOT NULL DEFAULT 1,
    checker             TINYINT         NOT NULL DEFAULT 0,
    revision            INT             NOT NULL DEFAULT 1,

    create_user         BIGINT          NOT NULL,
    create_date         DATETIME        NOT NULL,
    last_update_user    BIGINT          NOT NULL,
    last_update_date    DATETIME        NOT NULL
) ENGINE = InnoDb;

CREATE TABLE submission (
    submission_id       BIGINT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    problem_id          BIGINT          NOT NULL,
    language_id         BIGINT          NOT NULL,
    judge_reply_id      BIGINT          NOT NULL,
    user_profile_id     BIGINT          NOT NULL,

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
    user_profile_id     BIGINT          NOT NULL PRIMARY KEY,
    contest_id          BIGINT          NOT NULL,    
    ip                  VARCHAR(128)    NULL
) ENGINE = InnoDb;
