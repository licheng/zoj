DROP TABLE IF EXISTS user_ac;

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

delimiter ;

DROP PROCEDURE IF EXISTS update_user_ac;

delimiter |
CREATE PROCEDURE update_user_ac(uid BIGINT, cid BIGINT, pid BIGINT, sid BIGINT)
BEGIN
	DECLARE c, t BIGINT;
    DECLARE solved_cur CURSOR FOR SELECT count(distinct problem_id) from submission where user_profile_id = uid AND judge_reply_id=5 AND contest_id=cid AND active=1;
    DECLARE total_cur CURSOR FOR SELECT count(*) from submission where user_profile_id = uid AND contest_id=cid AND active=1;
    
    OPEN solved_cur;
    FETCH solved_cur INTO c;
    CLOSE solved_cur;   

	OPEN total_cur;
    FETCH total_cur INTO t;
    CLOSE total_cur;   

    REPLACE INTO user_ac SET solved=c, tiebreak=t, user_profile_id=uid, contest_id=cid;
END|
delimiter ;

DROP PROCEDURE IF EXISTS refresh_user_ac;
delimiter |
CREATE PROCEDURE refresh_user_ac()
BEGIN    
    DECLARE done INT DEFAULT 0;
    DECLARE uid, pid, cid, c BIGINT;
    DECLARE solved_cur CURSOR FOR select count(distinct problem_id), user_profile_id, contest_id from submission where judge_reply_id=5 and active=1 group by user_profile_id, contest_id;
    DECLARE total_cur CURSOR FOR select count(*), user_profile_id, contest_id from submission where active=1 group by user_profile_id, contest_id;
    DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done = 1;
    
    DELETE FROM user_ac;
    
    OPEN solved_cur;
    REPEAT
        FETCH solved_cur INTO c, uid, cid;  
        REPLACE INTO user_ac (user_profile_id, contest_id, solved)VALUES(uid,cid,c);
    UNTIL done END REPEAT;
    CLOSE solved_cur;      
    
    SET done = 0;
    OPEN total_cur;
    REPEAT
        FETCH total_cur INTO c, uid, cid;  
        UPDATE user_ac SET tiebreak=c WHERE user_profile_id = uid AND contest_id=cid;
    UNTIL done END REPEAT;
    CLOSE total_cur;    
    
END|
delimiter ;

DROP TRIGGER IF EXISTS submission_insert;
DROP TRIGGER IF EXISTS submission_update;

delimiter |            
CREATE TRIGGER submission_insert
AFTER INSERT ON submission
FOR EACH ROW BEGIN
	IF (NEW.judge_reply_id = 5) THEN 
        CALL update_user_ac(NEW.user_profile_id, NEW.contest_id, NEW.problem_id, NEW.submission_id);    
    END IF;
END|
CREATE TRIGGER submission_update
AFTER UPDATE ON submission
FOR EACH ROW BEGIN
    IF (NEW.active != OLD.active OR NEW.judge_reply_id=5 OR OLD.judge_reply_id=5) THEN
        CALL update_user_ac(NEW.user_profile_id, NEW.contest_id, NEW.problem_id, NEW.submission_id);    
    END IF;
END|
delimiter ;





