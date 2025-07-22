-- Create Database Change Log Table
CREATE TABLE PUBLIC.DATABASECHANGELOG (ID VARCHAR(255) NOT NULL, AUTHOR VARCHAR(255) NOT NULL, FILENAME VARCHAR(255) NOT NULL, DATEEXECUTED TIMESTAMP NOT NULL, ORDEREXECUTED INT NOT NULL, EXECTYPE VARCHAR(10) NOT NULL, MD5SUM VARCHAR(35), DESCRIPTION VARCHAR(255), COMMENTS VARCHAR(255), TAG VARCHAR(255), LIQUIBASE VARCHAR(20), CONTEXTS VARCHAR(255), LABELS VARCHAR(255), DEPLOYMENT_ID VARCHAR(10));

-- Create Database Lock Table
CREATE TABLE PUBLIC.DATABASECHANGELOGLOCK (ID INT NOT NULL, LOCKED BOOLEAN NOT NULL, LOCKGRANTED TIMESTAMP, LOCKEDBY VARCHAR(255), CONSTRAINT PK_DATABASECHANGELOGLOCK PRIMARY KEY (ID));

-- Initialize Database Lock Table
DELETE FROM PUBLIC.DATABASECHANGELOGLOCK;

INSERT INTO PUBLIC.DATABASECHANGELOGLOCK (ID, LOCKED) VALUES (1, FALSE);

-- *********************************************************************
-- SQL to roll back currently unexecuted changes
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 22.07.2025, 21:50
-- Against: TEST@jdbc:h2:mem:shareit
-- Liquibase version: 4.29.0
-- *********************************************************************

-- Lock Database
UPDATE PUBLIC.DATABASECHANGELOGLOCK SET LOCKED = TRUE, LOCKEDBY = 'DESKTOP-1V696A1 (192.168.0.107)', LOCKGRANTED = NOW() WHERE ID = 1 AND LOCKED = FALSE;

-- Rolling Back ChangeSet: db/changelog/changeset/update-users-table-add-avatar-column.yaml::add-avatar-column::mira
ALTER TABLE PUBLIC.users DROP COLUMN avatar_url;

DELETE FROM PUBLIC.DATABASECHANGELOG WHERE ID = 'add-avatar-column' AND AUTHOR = 'mira' AND FILENAME = 'db/changelog/changeset/update-users-table-add-avatar-column.yaml';

-- Release Database Lock
UPDATE PUBLIC.DATABASECHANGELOGLOCK SET LOCKED = FALSE, LOCKEDBY = NULL, LOCKGRANTED = NULL WHERE ID = 1;

