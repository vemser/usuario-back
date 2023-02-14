CREATE TABLE cargo
(	id_cargo numeric NOT NULL,
     nome text NOT NULL,
     descricao text NOT NULL,
     PRIMARY KEY (id_cargo)
)

CREATE SEQUENCE seq_cargo
    increment 1
start 1;

INSERT INTO CHRONOS.CARGO (ID_CARGO, NOME, DESCRICAO) VALUES(nextval('seq_cargo'), 'ROLE_ADMIN', 'Administrador');
INSERT INTO CHRONOS.CARGO (ID_CARGO, NOME, DESCRICAO) VALUES(nextval('seq_cargo'), 'ROLE_GESTOR', 'Coordenador');
INSERT INTO CHRONOS.CARGO (ID_CARGO, NOME, DESCRICAO) VALUES(nextval('seq_cargo'), 'ROLE_GESTAO_DE_PESSOAS', 'Gestão de pessoas');
INSERT INTO CHRONOS.CARGO (ID_CARGO, NOME, DESCRICAO) VALUES(nextval('seq_cargo'), 'ROLE_INSTRUTOR', 'Instrutor');
INSERT INTO CHRONOS.CARGO (ID_CARGO, NOME, DESCRICAO) VALUES(nextval('seq_cargo'), 'ROLE_ALUNO', 'Aluno');
INSERT INTO CHRONOS.CARGO (ID_CARGO, NOME, DESCRICAO) VALUES(nextval('seq_cargo'), 'ROLE_COLABORADOR', 'Colaborador');

CREATE TABLE usuario
(	id_usuario numeric NOT NULL,
     login text unique NOT NULL,
     PRIMARY KEY (id_usuario)
)

CREATE SEQUENCE seq_usuario
    increment 1
start 1;

CREATE TABLE usuario_cargo
(
    id_usuario numeric NOT NULL,
    id_cargo numeric NOT NULL,
    PRIMARY KEY (id_usuario, id_cargo),
    CONSTRAINT FK_usuario_cargo_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario),
    CONSTRAINT FK_usuario_cargo_cargo FOREIGN KEY (id_cargo)
        REFERENCES cargo (id_cargo)
)

CREATE TABLE foto (
                      id_foto numeric NOT NULL ,
                      id_usuario numeric NOT NULL ,
                      nome text,
                      tipo text,
                      arquivo bytea,
                      PRIMARY KEY (id_foto),
                      CONSTRAINT fk_usuario_foto FOREIGN KEY (id_usuario)
                          REFERENCES usuario (id_usuario)
)

CREATE SEQUENCE seq_foto
    increment 1
start 1;