INSERT INTO peoples (age_people,happy_day,first_last_name,employeename,second_last_name,email_people,full_name,identity_people,ingreso,estadocivil,cargo,genero,tdocumento,foto)VALUES ('29', '1995-07-10','Tamani', 'Raul', 'Arbildo', 'rtamanar@gmail.com', 'Raul Tamani Arbildo', '75887601','2023-01-23','Soltero','Soporte de Procesos','Hombre','D.N.I','');

INSERT INTO addresses (people_id, distrito, pais, region, direccion, referencia)VALUES ('1', 'Ate', 'Perú', 'Lima', 'Calle las Ñejas MZ Z1 Lote 1 SN', 'Por los Nogales de Puente amarillo');
INSERT INTO USERS(USERNAME,USERPASSWORD,USERENABLED,people_id,is_temporary_password)VALUES('rtamanar','$2a$10$ww0edmDL7JG3s3vXN8QlJu5zn2dtaVamx6YsU52eA/nB/mFAfmx5G',1,1,0);

INSERT INTO phones (id, people_id, phonenumber, cellphone) VALUES ('1', '1', '+012587894', '+51943292759');

INSERT INTO ROLS(USER_ID,AUTHORITY)VALUES(1,'ROLE_USER');
INSERT INTO ROLS(USER_ID,AUTHORITY) VALUES(1,'ROLE_ADMIN');