-- Crear la nueva base de datos
drop database if exists cuentas_bancarias;
CREATE DATABASE cuentas_bancarias;
USE cuentas_bancarias;

-- Crear la tabla de clientes
CREATE TABLE IF NOT EXISTS clientes (
    dni VARCHAR(8) PRIMARY KEY,
    apellidos VARCHAR(20),
    nombre VARCHAR(20)
    );

-- Insertar clientes
INSERT INTO clientes (dni, apellidos, nombre) VALUES
                                                  ('12345678', 'Gómez', 'Juan'),
                                                  ('23456789', 'Martínez', 'Ana'),
                                                  ('34567890', 'López', 'Miguel');

-- Crear la tabla de cuentas
CREATE TABLE IF NOT EXISTS cuentas (
    num_cuenta VARCHAR(22) PRIMARY KEY,
    -- pin integer not null, versión posterior
    -- cuenta bloqueada, versión posterior
    saldo_apertura DECIMAL(8,2) DEFAULT 0,
    saldo_actual DECIMAL(8,2) DEFAULT 0,
    dni VARCHAR(8),
    FOREIGN KEY (dni) REFERENCES clientes(dni)
    );

-- Insertar cuentas
INSERT INTO cuentas (num_cuenta, saldo_apertura, saldo_actual, dni) VALUES
                                                                        ('ES12345678901234567890', 1000.00, 1000.00, '12345678'),
                                                                        ('ES23456789012345678901', 500.50, 500.50, '23456789'),
                                                                        ('ES34567890123456789012', 1200.75, 1200.75, '34567890'),
                                                                        ('ES45678901234567890123', 800.20, 800.20, '12345678'),
                                                                        ('ES56789012345678901234', 1500.30, 1500.30, '23456789'),
                                                                        ('ES67890123456789012345', 200.00, 200.00, '34567890');

-- Crear la tabla de movimientos
CREATE TABLE IF NOT EXISTS movimientos (
    num_cuenta VARCHAR(22),
    fecha DATETIME,
    importe DECIMAL(8,2),
    PRIMARY KEY (num_cuenta, fecha),
    FOREIGN KEY (num_cuenta) REFERENCES cuentas(num_cuenta)
    );

-- Crear el trigger para actualizar el saldo_actual
DELIMITER //

CREATE TRIGGER actualizar_saldo_trigger AFTER INSERT ON movimientos
    FOR EACH ROW
BEGIN
    UPDATE cuentas
    SET saldo_actual = saldo_actual + NEW.importe
    WHERE num_cuenta = NEW.num_cuenta;
END;
//

DELIMITER ;

-- Insertar movimientos
INSERT INTO movimientos (num_cuenta, fecha, importe) VALUES
                                                         ('ES12345678901234567890', '2024-01-16 12:00:00', 50.00),
                                                         ('ES12345678901234567890', '2024-01-16 12:30:00', -30.00),
                                                         ('ES12345678901234567890', '2024-01-17 09:45:00', 20.00),
                                                         ('ES23456789012345678901', '2024-01-16 14:20:00', -10.50),
                                                         ('ES23456789012345678901', '2024-01-16 14:45:00', 100.00),
                                                         ('ES23456789012345678901', '2024-01-17 10:30:00', -50.25),
                                                         ('ES34567890123456789012', '2024-01-16 16:00:00', 25.75),
                                                         ('ES34567890123456789012', '2024-01-16 16:30:00', -15.30),
                                                         ('ES34567890123456789012', '2024-01-17 11:15:00', 10.00),
                                                         ('ES45678901234567890123', '2024-01-16 18:00:00', -50.00),
                                                         ('ES45678901234567890123', '2024-01-16 18:30:00', 75.20),
                                                         ('ES45678901234567890123', '2024-01-17 12:00:00', -25.50),
                                                         ('ES56789012345678901234', '2024-01-16 20:00:00', 30.00),
                                                         ('ES56789012345678901234', '2024-01-16 20:30:00', -20.30),
                                                         ('ES56789012345678901234', '2024-01-17 13:15:00', 15.50),
                                                         ('ES67890123456789012345', '2024-01-16 22:00:00', -10.00),
                                                         ('ES67890123456789012345', '2024-01-16 22:30:00', 5.50),
                                                         ('ES67890123456789012345', '2024-01-17 14:00:00', -2.00);

select * from movimientos where num_cuenta = 'ES12345678901234567890';
select * from cuentas where num_cuenta = 'ES12345678901234567890';
INSERT INTO movimientos (num_cuenta, fecha, importe) VALUES ('ES12345678901234567890', '2024-01-17 12:00:00', 100.00);