DROP TABLE IF EXISTS submission_status;
CREATE TABLE submission_status (
    user_profile_id     BIGINT          NOT NULL,
    problem_id          BIGINT          NOT NULL,   
    status              VARCHAR(32)     NULL  
) ENGINE = InnoDb;

ALTER TABLE submission_status
    ADD CONSTRAINT fk_submission_status_user_profile_id FOREIGN KEY (user_profile_id)
        REFERENCES user_profile (user_profile_id)
            ON DELETE RESTRICT;
ALTER TABLE submission_status
    ADD CONSTRAINT fk_submission_status_problem_id FOREIGN KEY (problem_id)
        REFERENCES problem (problem_id)
            ON DELETE RESTRICT;            

update problem set color='white' where contest_id=3 AND code='A';
update problem set color='red' where contest_id=3 AND code='B';
update problem set color='purple' where contest_id=3 AND code='C';
update problem set color='blue' where contest_id=3 AND code='D';
update problem set color='yellow' where contest_id=3 AND code='E';
update problem set color='pink' where contest_id=3 AND code='F';
update problem set color='green' where contest_id=3 AND code='G';
update problem set color='goldenrod' where contest_id=3 AND code='H';

update problem set color='red' where contest_id=14 AND code='A';
update problem set color='green' where contest_id=14 AND code='B';
