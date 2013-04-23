ALTER TABLE entry ADD COLUMN author character varying(1024);

INSERT INTO schemaversion VALUES (NOW(), 'translation', '0002');

