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

delimiter ;
DROP PROCEDURE IF EXISTS refresh_statistics;
DROP PROCEDURE IF EXISTS add_submission;
DROP PROCEDURE IF EXISTS add_submission_with_cid;
DROP PROCEDURE IF EXISTS delete_submission;
DROP PROCEDURE IF EXISTS delete_submission_with_cid;
DROP FUNCTION IF EXISTS get_contest_id;
delimiter |
CREATE FUNCTION get_contest_id (pid BIGINT) RETURNS BIGINT
BEGIN
    DECLARE done INT DEFAULT 0;
    DECLARE cid BIGINT;
    DECLARE contest_cur CURSOR FOR SELECT contest_id FROM problem WHERE problem_id=pid;
    DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done = 1;
    OPEN contest_cur;
    REPEAT
        FETCH contest_cur INTO cid;  
    UNTIL done END REPEAT;
    CLOSE contest_cur;    
    RETURN cid;
END|

CREATE PROCEDURE add_submission_with_cid(uid BIGINT, pid BIGINT, cid BIGINT, jid BIGINT)
BEGIN
    IF (cid <> NULL) THEN
        UPDATE user_statistics SET count=count+1 WHERE user_profile_id=uid AND contest_id=cid AND judge_reply_id=jid;
        IF (ROW_COUNT() <= 0) THEN
            INSERT INTO user_statistics(user_profile_id, contest_id, judge_reply_id, count) VALUES(uid, cid, jid, 1);
        END IF;
        
        UPDATE problem_statistics SET count=count+1 WHERE problem_id=pid AND judge_reply_id=jid;
        IF (ROW_COUNT() <= 0) THEN
            INSERT INTO problem_statistics(problem_id, judge_reply_id, count) VALUES(pid, jid, 1);
        END IF;
    END IF;
END|

CREATE PROCEDURE add_submission(uid BIGINT, pid BIGINT, jid BIGINT)
BEGIN
    DECLARE cid BIGINT;
    SET cid = get_contest_id(pid);
    CALL add_submission_with_cid(uid, pid, cid, jid);
END|

CREATE PROCEDURE delete_submission_with_cid(uid BIGINT, pid BIGINT, cid BIGINT, jid BIGINT)
BEGIN
    IF (cid <> NULL) THEN
        UPDATE user_statistics SET count=count-1 WHERE user_profile_id=uid AND contest_id=cid AND judge_reply_id=jid;    
        UPDATE problem_statistics SET count=count-1 WHERE problem_id=pid AND judge_reply_id=jid;
    END IF;
END|

CREATE PROCEDURE delete_submission(uid BIGINT, pid BIGINT, jid BIGINT)
BEGIN
    DECLARE cid BIGINT;
    SET cid = get_contest_id(pid);
    CALL delete_submission(uid, pid, cid, jid);
END|


    
CREATE PROCEDURE refresh_statistics()
BEGIN    
    DECLARE done INT DEFAULT 0;
    DECLARE uid, pid, cid, jid BIGINT;
    DECLARE submission_cur CURSOR FOR SELECT s.user_profile_id, s.problem_id, p.contest_id, s.judge_reply_id FROM submission s LEFT JOIN problem p ON s.problem_id=p.problem_id;
    DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done = 1;
    
    DELETE FROM user_statistics;
    DELETE FROM problem_statistics;
    
    OPEN submission_cur;
    
    REPEAT
        FETCH submission_cur INTO uid, pid, cid, jid;  
        CALL add_submission_with_cid(uid, pid, cid, jid);
    UNTIL done END REPEAT;
    
    CLOSE submission_cur;      
END|
delimiter ;

DROP TRIGGER IF EXISTS submission_insert;
DROP TRIGGER IF EXISTS submission_update;

delimiter |            
CREATE TRIGGER submission_insert
AFTER INSERT ON submission
FOR EACH ROW BEGIN
    CALL add_submission(NEW.user_profile_id, NEW.problem_id, NEW.judge_reply_id);    
END|
CREATE TRIGGER submission_update
AFTER UPDATE ON submission
FOR EACH ROW BEGIN
    IF (NEW.active=0) THEN
        IF (OLD.active=1) THEN 
            CALL delete_submission(OLD.user_profile_id, OLD.problem_id, OLD.submission_id, OLD.judge_reply_id);
        END IF;
    ELSE
        IF (OLD.active=0) THEN 
            CALL add_submission(NEW.user_profile_id, NEW.problem_id, NEW.submission_id, NEW.judge_reply_id);
        ELSE
            CALL delete_submission(OLD.user_profile_id, OLD.problem_id, OLD.submission_id, OLD.judge_reply_id);
            CALL add_submission(NEW.user_profile_id, NEW.problem_id, NEW.submission_id, NEW.judge_reply_id);    
        END IF;
    END IF;
END|
delimiter ;





