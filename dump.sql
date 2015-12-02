;             
CREATE USER IF NOT EXISTS INSTRUMENTS SALT 'eff7d3d18cb472c3' HASH 'e5f20e5e891125478e804c6f95d3e1727e14328eec2860d37101556f469a7eee' ADMIN;  
CREATE CACHED TABLE PUBLIC.USER(
    EMAIL VARCHAR(256) NOT NULL,
    PASSWORD VARCHAR(256)
);             
ALTER TABLE PUBLIC.USER ADD CONSTRAINT PUBLIC.CONSTRAINT_2 PRIMARY KEY(EMAIL);
-- 1 +/- SELECT COUNT(*) FROM PUBLIC.USER;    
INSERT INTO PUBLIC.USER(EMAIL, PASSWORD) VALUES
('a@a', 'a');
CREATE CACHED TABLE PUBLIC.INSTRUMENT(
    ID VARCHAR(256) NOT NULL,
    TYPE VARCHAR(256),
    MAKE VARCHAR(256),
    SERIAL VARCHAR(256),
    PRODUCT_NO VARCHAR(256),
    DESCRIPTION VARCHAR(256)
);               
ALTER TABLE PUBLIC.INSTRUMENT ADD CONSTRAINT PUBLIC.CONSTRAINT_7 PRIMARY KEY(ID);             
-- 2 +/- SELECT COUNT(*) FROM PUBLIC.INSTRUMENT;              
INSERT INTO PUBLIC.INSTRUMENT(ID, TYPE, MAKE, SERIAL, PRODUCT_NO, DESCRIPTION) VALUES
('4r87uyb7yqjrchwbv1bscbr8nr9qw1ddhrudwyzpty7gpjj7ts', '11', 'Besson', '1234', '1', 'En beskrivelse'),
('1il4n089wg74h35qphzfl07bjgn8tzw3uq386sgemyk9yyelm6', '15', 'Yamaha', '1234', '12', 'Hola');  
CREATE CACHED TABLE PUBLIC.MUSICIAN(
    ID VARCHAR(256) NOT NULL,
    FIRST_NAME VARCHAR(256),
    LAST_NAME VARCHAR(256)
);             
ALTER TABLE PUBLIC.MUSICIAN ADD CONSTRAINT PUBLIC.CONSTRAINT_6 PRIMARY KEY(ID);               
-- 2 +/- SELECT COUNT(*) FROM PUBLIC.MUSICIAN;
INSERT INTO PUBLIC.MUSICIAN(ID, FIRST_NAME, LAST_NAME) VALUES
('3nb2k0u44apv0y50ayw9xhhep49o7zgselu3mvt5bisupstabo', 'Petter', 'Tesdal'),
('9kmdd9d5vd1vb39klkrddcehjhhqnvd180usb36p8xjyemcni', 'Laura', 'Tesdal');         
CREATE CACHED TABLE PUBLIC.INSTRUMENT_TYPE(
    ID VARCHAR(256) NOT NULL,
    NAME VARCHAR(256)
);         
ALTER TABLE PUBLIC.INSTRUMENT_TYPE ADD CONSTRAINT PUBLIC.CONSTRAINT_64 PRIMARY KEY(ID);       
-- 23 +/- SELECT COUNT(*) FROM PUBLIC.INSTRUMENT_TYPE;        
INSERT INTO PUBLIC.INSTRUMENT_TYPE(ID, NAME) VALUES
('1', 'Kornett'),
('2', 'Trompet'),
('3', 'Klarinett'),
('4', 'Klarinett (Bass)'),
('5', 'Valthorn'),
('6', 'Tuba'),
('7', 'Baryton'),
('8', 'Horn'),
('9', 'Horn (Alt)'),
('10', 'Horn (Tenor)'),
('11', 'Horn (Flygel)'),
('12', 'Obo'),
('13', 'Fagott'),
('14', STRINGDECODE('Fl\u00f8yte')),
('15', STRINGDECODE('Fl\u00f8yte (Piccolo)')),
('16', 'Saksofon'),
('17', 'Saksofon (Alt)'),
('18', 'Saksofon (Tenor)'),
('19', 'Trombone'),
('20', 'Trombone (Alt)'),
('21', 'Trombone (Tenor)'),
('22', 'Perkusjon (Rytmisk)'),
('23', 'Perkusjon (Melodisk)');
CREATE CACHED TABLE PUBLIC.INSTRUMENT_STATE(
    INSTRUMENT_ID VARCHAR(256) NOT NULL,
    DATE DATETIME,
    STATE LONGVARCHAR,
    STATE_BY_USER VARCHAR(256)
);        
ALTER TABLE PUBLIC.INSTRUMENT_STATE ADD CONSTRAINT PUBLIC.CONSTRAINT_3 PRIMARY KEY(INSTRUMENT_ID);            
-- 0 +/- SELECT COUNT(*) FROM PUBLIC.INSTRUMENT_STATE;        
CREATE CACHED TABLE PUBLIC.MUSICIAN_INSTRUMENT(
    INSTRUMENT_ID VARCHAR(256),
    MUSICIAN_ID VARCHAR(256),
    OUT_AT DATETIME,
    OUT_BY_USER VARCHAR(256),
    IN_AT DATETIME,
    IN_BY_USER VARCHAR(256)
);    
-- 4 +/- SELECT COUNT(*) FROM PUBLIC.MUSICIAN_INSTRUMENT;     
INSERT INTO PUBLIC.MUSICIAN_INSTRUMENT(INSTRUMENT_ID, MUSICIAN_ID, OUT_AT, OUT_BY_USER, IN_AT, IN_BY_USER) VALUES
('4r87uyb7yqjrchwbv1bscbr8nr9qw1ddhrudwyzpty7gpjj7ts', '3nb2k0u44apv0y50ayw9xhhep49o7zgselu3mvt5bisupstabo', TIMESTAMP '2015-12-01 15:57:51.409', 'a@a', TIMESTAMP '2015-12-01 16:21:50.732', 'a@a'),
('4r87uyb7yqjrchwbv1bscbr8nr9qw1ddhrudwyzpty7gpjj7ts', '9kmdd9d5vd1vb39klkrddcehjhhqnvd180usb36p8xjyemcni', TIMESTAMP '2015-12-01 16:21:41.639', 'a@a', TIMESTAMP '2015-12-01 16:21:50.732', 'a@a'),
('4r87uyb7yqjrchwbv1bscbr8nr9qw1ddhrudwyzpty7gpjj7ts', '9kmdd9d5vd1vb39klkrddcehjhhqnvd180usb36p8xjyemcni', TIMESTAMP '2015-12-01 16:22:05.401', 'a@a', NULL, NULL),
('1il4n089wg74h35qphzfl07bjgn8tzw3uq386sgemyk9yyelm6', '9kmdd9d5vd1vb39klkrddcehjhhqnvd180usb36p8xjyemcni', TIMESTAMP '2015-12-01 16:38:05.861', 'a@a', NULL, NULL);    
