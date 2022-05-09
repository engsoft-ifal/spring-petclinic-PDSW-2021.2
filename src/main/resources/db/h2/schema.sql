DROP TABLE vet_specialties IF EXISTS;
DROP TABLE vets IF EXISTS;
DROP TABLE specialties IF EXISTS;
DROP TABLE visits IF EXISTS;
DROP TABLE pets IF EXISTS;
DROP TABLE types IF EXISTS;
DROP TABLE owners IF EXISTS;


CREATE TABLE vets (
  id         INTEGER IDENTITY PRIMARY KEY,
  first_name VARCHAR(30),
  last_name  VARCHAR(30),
  avatar  LONGBLOB NULL
);
CREATE INDEX vets_last_name ON vets (last_name);

CREATE TABLE specialties (
  id   INTEGER IDENTITY PRIMARY KEY,
  name VARCHAR(80)
);
CREATE INDEX specialties_name ON specialties (name);

CREATE TABLE vet_specialties (
  vet_id       INTEGER NOT NULL,
  specialty_id INTEGER NOT NULL
);
ALTER TABLE vet_specialties ADD CONSTRAINT fk_vet_specialties_vets FOREIGN KEY (vet_id) REFERENCES vets (id);
ALTER TABLE vet_specialties ADD CONSTRAINT fk_vet_specialties_specialties FOREIGN KEY (specialty_id) REFERENCES specialties (id);

CREATE TABLE available_days (
  id   INTEGER IDENTITY PRIMARY KEY,
  name VARCHAR(80)
);

CREATE TABLE vet_days (
  vet_id INTEGER NOT NULL,
  day_id INTEGER NOT NULL
);
ALTER TABLE vet_days ADD CONSTRAINT fk_vet_days_vets FOREIGN KEY (vet_id) REFERENCES vets (id);
ALTER TABLE vet_days ADD CONSTRAINT fk_vet_days_days FOREIGN KEY (day_id) REFERENCES available_days (id);

CREATE TABLE types (
  id   INTEGER IDENTITY PRIMARY KEY,
  name VARCHAR(80)
);
CREATE INDEX types_name ON types (name);

CREATE TABLE owners (
  id            INTEGER IDENTITY PRIMARY KEY,
  first_name    VARCHAR(30),
  last_name     VARCHAR_IGNORECASE(30),
  address       VARCHAR(255),
  city          VARCHAR(80),
  telephone     VARCHAR(20),
  telephone_two VARCHAR(20),
  email         VARCHAR(100) NOT NULL UNIQUE 
);
CREATE INDEX owners_last_name ON owners (last_name);

CREATE TABLE pets (
  id         INTEGER IDENTITY PRIMARY KEY,
  name       VARCHAR(30),
  birth_date DATE,
  type_id    INTEGER NOT NULL,
  owner_id   INTEGER
);
ALTER TABLE pets ADD CONSTRAINT fk_pets_owners FOREIGN KEY (owner_id) REFERENCES owners (id);
ALTER TABLE pets ADD CONSTRAINT fk_pets_types FOREIGN KEY (type_id) REFERENCES types (id);
CREATE INDEX pets_name ON pets (name);

CREATE TABLE visits (
  id          INTEGER IDENTITY PRIMARY KEY,
  pet_id      INTEGER,
  visit_date  DATE,
  description VARCHAR(255),
  vet_id      INTEGER
);
ALTER TABLE visits ADD CONSTRAINT fk_visits_pets FOREIGN KEY (pet_id) REFERENCES pets (id);
ALTER TABLE visits ADD CONSTRAINT fk_visits_vet FOREIGN KEY (vet_id) REFERENCES vets (id);
CREATE INDEX visits_pet_id ON visits (pet_id);
CREATE INDEX visits_vet_id ON visits (vet_id);