DROP TABLE IF EXISTS user_statistics;
DROP TABLE IF EXISTS problem_statistics;
DROP TABLE IF EXISTS user_submission_ac;
DROP TABLE IF EXISTS problem_submission_ac;


CREATE TABLE user_statistics (
    user_profile_id     BIGINT          NOT NULL,
    contest_id          BIGINT          NOT NULL,
    judge_reply_id      BIGINT          NOT NULL,
    count               BIGINT          NOT NULL, 
    PRIMARY KEY (user_profile_id, contest_id, judge_reply_id)   
) ENGINE = InnoDb;

CREATE TABLE problem_statistics (
    problem_id          BIGINT          NOT NULL,
    judge_reply_id      BIGINT          NOT NULL,
    count               BIGINT          NOT NULL,   
    PRIMARY KEY (problem_id, judge_reply_id)    
) ENGINE = InnoDb;

CREATE TABLE user_submission_ac (
    user_profile_id     BIGINT          NOT NULL,
    submission_id       BIGINT          NOT NULL,
    PRIMARY KEY (user_profile_id, submission_id)    
) ENGINE = InnoDb;

CREATE TABLE problem_submission_ac (
    problem_id          BIGINT          NOT NULL,
    submission_id       BIGINT          NOT NULL,
    PRIMARY KEY (problem_id, submission_id)    
) ENGINE = InnoDb;




ALTER TABLE user_statistics
    ADD CONSTRAINT fk_user_statistics_user_profile FOREIGN KEY (user_profile_id)
        REFERENCES user_profile (user_profile_id)
            ON DELETE RESTRICT;
ALTER TABLE user_statistics
    ADD CONSTRAINT fk_user_statistics_contest FOREIGN KEY (contest_id)
        REFERENCES contest (contest_id)
            ON DELETE RESTRICT;
ALTER TABLE user_statistics
    ADD CONSTRAINT fk_user_statistics_judge_reply FOREIGN KEY (judge_reply_id)
        REFERENCES judge_reply (judge_reply_id)
            ON DELETE RESTRICT;  
  
ALTER TABLE problem_statistics
    ADD CONSTRAINT fk_problem_statistics_user_profile FOREIGN KEY (problem_id)
        REFERENCES problem (problem_id)
            ON DELETE RESTRICT;
ALTER TABLE problem_statistics
    ADD CONSTRAINT fk_problem_statistics_judge_reply FOREIGN KEY (judge_reply_id)
        REFERENCES judge_reply (judge_reply_id)
            ON DELETE RESTRICT;                     
            
ALTER TABLE user_submission_ac
    ADD CONSTRAINT fk_user_submission_ac_user_profile FOREIGN KEY (user_profile_id)
        REFERENCES user_profile (user_profile_id)
            ON DELETE RESTRICT;
ALTER TABLE user_submission_ac
    ADD CONSTRAINT fk_user_submission_ac_submission FOREIGN KEY (submission_id)
        REFERENCES submission (submission_id)
            ON DELETE RESTRICT;    
ALTER TABLE problem_submission_ac
    ADD CONSTRAINT fk_problem_submission_ac_problem FOREIGN KEY (problem_id)
        REFERENCES problem (problem_id)
            ON DELETE RESTRICT;
ALTER TABLE problem_submission_ac
    ADD CONSTRAINT fk_problem_submission_ac_submission FOREIGN KEY (submission_id)
        REFERENCES submission (submission_id)
            ON DELETE RESTRICT;  
            
  
DROP PROCEDURE IF EXISTS refresh_statistics;
DROP PROCEDURE IF EXISTS add_submission;
DROP PROCEDURE IF EXISTS delete_submission;
delimiter |
CREATE PROCEDURE add_submission(uid BIGINT, pid BIGINT, sid BIGINT, jid BIGINT)
BEGIN
    UPDATE user_statistics SET count=count+1 WHERE user_profile_id=uid AND judge_reply_id=jid;
    IF (ROW_COUNT() <= 0) THEN
        INSERT INTO user_statistics(user_profile_id, judge_reply_id, count) VALUES(uid, jid, 1);
    END IF;
    
    UPDATE problem_statistics SET count=count+1 WHERE problem_id=pid AND judge_reply_id=jid;
    IF (ROW_COUNT() <= 0) THEN
        INSERT INTO problem_statistics(problem_id, judge_reply_id, count) VALUES(pid, jid, 1);
    END IF;
    IF (jid = 5) THEN
        INSERT INTO user_submission_ac(user_profile_id, submission_id) VALUES(uid, sid);
        INSERT INTO problem_submission_ac(problem_id, submission_id) VALUES(pid, sid);
    END IF;    
END|

CREATE PROCEDURE delete_submission(uid BIGINT, pid BIGINT, sid BIGINT, jid BIGINT)
BEGIN
    UPDATE user_statistics SET count=count-1 WHERE user_profile_id=uid AND judge_reply_id=jid;    
    UPDATE problem_statistics SET count=count-1 WHERE problem_id=pid AND judge_reply_id=jid;
    IF (jid = 5) THEN
        DELETE FROM user_submission_ac WHERE submission_id=sid;
        DELETE FROM problem_submission_ac WHERE submission_id=sid;        
    END IF;    
END|

CREATE PROCEDURE refresh_statistics()
BEGIN    
    DECLARE done INT DEFAULT 0;
    DECLARE uid, pid, sid, jid BIGINT;
    DECLARE submission_cur CURSOR FOR SELECT user_profile_id, problem_id, submission_id, judge_reply_id FROM submission;
    DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done = 1;
    
    DELETE FROM user_submission_ac;
    DELETE FROM problem_submission_ac;
    DELETE FROM user_statistics;
    DELETE FROM problem_statistics;
    
    OPEN submission_cur;
    
    REPEAT
        FETCH submission_cur INTO uid, pid, sid, jid;  
        CALL add_submission(uid, pid, sid, jid);
    UNTIL done END REPEAT;
    
    CLOSE submission_cur;      
END|
delimiter ;

DROP TRIGGER submission_insert;
DROP TRIGGER submission_update;

delimiter |            
CREATE TRIGGER submission_insert
AFTER INSERT ON submission
FOR EACH ROW BEGIN
    CALL add_submission(NEW.user_profile_id, NEW.problem_id, NEW.submission_id, NEW.judge_reply_id);    
END|
CREATE TRIGGER submission_update
AFTER UPDATE ON submission
FOR EACH ROW BEGIN
    IF (NEW.active=0) THEN
        IF (OLD.active=1) THEN 
            CALL delete_submission(OLD.user_profile_id, OLD.problem_id, OLD.submission_id, OLD.judge_reply_id);
        END IF;
    ELSE
        IF (OLD.judge_reply_id <> NEW.judge_reply_id) THEN
            CALL delete_submission(OLD.user_profile_id, OLD.problem_id, OLD.submission_id, OLD.judge_reply_id);
            CALL add_submission(NEW.user_profile_id, NEW.problem_id, NEW.submission_id, NEW.judge_reply_id);    
        END IF;
    END IF;       
END|
delimiter ;
