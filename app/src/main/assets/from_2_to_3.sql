CREATE TABLE GivenPresents_Names (
PresentId INTEGER,
PersonId INTEGER,
PRIMARY KEY (PresentId, PersonId),
FOREIGN KEY (PresentId) REFERENCES GivenPresents (PresentId)
ON DELETE CASCADE ON UPDATE NO ACTION,
FOREIGN KEY (PersonId) REFERENCES Names (PersonId)
ON DELETE CASCADE ON UPDATE NO ACTION
);

INSERT INTO GivenPresents_Names (PresentId, PersonId)
SELECT PresentId, PersonId
FROM GivenPresents;

PRAGMA foreign_keys=off;

BEGIN TRANSACTION;

ALTER TABLE GivenPresents RENAME TO _GivenPresents_old;

CREATE TABLE GivenPresents (
PresentId INTEGER PRIMARY KEY AUTOINCREMENT,
Year INTEGER,
Present TEXT NOT NULL,
Notes TEXT,
Bought INTEGER,
Sent INTEGER
)

INSERT INTO GivenPresents (PresentId, Year, Present, Notes, Bought, Sent)
SELECT PresentId, Year, Present, Notes, Bought, Sent
FROM _GivenPresents_old;

DROP TABLE _GivenPresents_old

COMMIT;

PRAGMA foreign_keys=on;