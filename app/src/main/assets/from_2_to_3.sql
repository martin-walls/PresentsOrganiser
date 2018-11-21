CREATE TABLE GivenPresents_Names (
PresentId INTEGER,
PersonId INTEGER,
PRIMARY KEY (PresentId, PersonId),
FOREIGN KEY (PresentId) REFERENCES GivenPresents (PresentId)
ON DELETE CASCADE ON UPDATE NO ACTION,
FOREIGN KEY (PersonId) REFERENCES `Names` (PersonId)
ON DELETE CASCADE ON UPDATE NO ACTION
);

ALTER TABLE GivenPresents
