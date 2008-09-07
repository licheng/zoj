DROP TABLE IF EXISTS access_log;

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
