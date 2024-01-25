package org.iesvdm;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Scanner;
import java.sql.Time;

public class CajeroAutomatico {

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        String cuenta;
        double importe;
        LocalDateTime fecha;
        String consulta;
        Connection connection;
        Statement st;

        System.setProperty("jdbc.drivers", "com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/cuentas_bancarias";
        String user = "root";
        String pass = "user";
        int opcion;

        try {
            connection = DriverManager.getConnection(url, user, pass);
            System.out.println("Conexión exitosa.");

            do {
                System.out.println("\nCajero Automático");
                System.out.println("1- Retirar fondos");
                System.out.println("2- Ingresar fondos");
                System.out.println("3- Consultar movimientos");
                System.out.println("4- Listar todas las cuentas de un cliente");
                System.out.println("5- Consultar cuentas con saldo menor a un monto");
                System.out.println("0- Salir");
                System.out.print("Seleccione una opción: ");
                opcion = scanner.nextInt();

                switch (opcion) {
                    case 1: // Retirar fondos
                        System.out.println("Introduzca la cuenta: ");
                        cuenta = scanner.next();
                        System.out.print("Ingrese la cantidad a retirar: ");
                        importe = scanner.nextDouble();

                        // Validar que el importe a retirar no sea mayor al saldo
                        if (validarSaldoSuficiente(connection, cuenta, importe)) {
                            fecha = LocalDateTime.now();
                            consulta = "insert into movimientos values ("
                                    + "'" + cuenta + "'" + ", " + "'" + fecha + "'" + " , " + (-importe) + ")";
                            st = connection.createStatement();
                            st.executeUpdate(consulta);
                        } else {
                            System.out.println("Error: Fondos insuficientes.");
                        }
                        break;

                    case 2: // Ingresar fondos
                        System.out.println("Introduzca la cuenta: ");
                        cuenta = scanner.next();
                        System.out.print("Ingrese la cantidad a depositar: ");
                        importe = scanner.nextDouble();
                        fecha = LocalDateTime.now();
                        consulta = "insert into movimientos values ("
                                + "'" + cuenta + "'" + ", " + "'" + fecha + "'" + " , " + importe + ")";
                        st = connection.createStatement();
                        st.executeUpdate(consulta);
                        break;

                    case 3: // Consultar movimientos
                        System.out.print("Introduzca el Número de Cuenta : ");
                        cuenta = scanner.next();
                        consulta = "select num_cuenta, fecha, importe from movimientos"
                                + " where num_cuenta = ";
                        consulta = consulta + "'" + cuenta + "'";
                        st = connection.createStatement();
                        ResultSet rs = st.executeQuery(consulta);
                        System.out.println("Cuenta Fecha Importe");
                        System.out.println("-------------------- ------------------- ------------- ");
                        while (rs.next()) {
                            cuenta = rs.getString("num_cuenta");
                            Date dia = rs.getDate("fecha");
                            Time hora = rs.getTime("fecha");
                            importe = rs.getFloat("importe");
                            System.out.println(cuenta + " " + dia + " " + hora + " " + importe);
                        }
                        break;

                    case 4: // Listar todas las cuentas de un cliente
                        System.out.print("Introduzca el DNI del cliente: ");
                        String dniCliente = scanner.next();
                        listarCuentasDeCliente(connection, dniCliente);
                        break;

                    case 5: // Consultar cuentas con saldo menor a un monto
                        System.out.print("Ingrese el monto máximo de saldo: ");
                        double montoMaximo = scanner.nextDouble();
                        consultarCuentasConSaldoMenorA(connection, montoMaximo);
                        break;

                    case 0: // Salir
                        System.out.println("Saliendo del sistema.");
                        break;

                    default:
                        System.out.println("Opción no válida. Intente nuevamente.");
                }
            } while (opcion != 0);

            scanner.close();

        } catch (SQLException sqle) {
            System.out.println(sqle.getMessage());
        }
    }

    // Método para validar que el importe a retirar no sea mayor al saldo
    private static boolean validarSaldoSuficiente(Connection connection, String cuenta, double importe) throws SQLException {
        String consultaSaldo = "select saldo from cuentas where num_cuenta = '" + cuenta + "'";
        Statement stSaldo = connection.createStatement();
        ResultSet rsSaldo = stSaldo.executeQuery(consultaSaldo);

        if (rsSaldo.next()) {
            double saldo = rsSaldo.getDouble("saldo");
            return saldo >= importe;
        }
        return false;
    }

    // Método para listar todas las cuentas de un cliente (por DNI)
    private static void listarCuentasDeCliente(Connection connection, String dniCliente) throws SQLException {
        String consultaCuentas = "select num_cuenta from cuentas where dni_cliente = '" + dniCliente + "'";
        Statement stCuentas = connection.createStatement();
        ResultSet rsCuentas = stCuentas.executeQuery(consultaCuentas);

        System.out.println("Cuentas del cliente " + dniCliente + ":");
        while (rsCuentas.next()) {
            String numCuenta = rsCuentas.getString("num_cuenta");
            System.out.println(numCuenta);
        }
    }

    // Método para consultar cuentas con saldo menor a un monto
    private static void consultarCuentasConSaldoMenorA(Connection connection, double montoMaximo) throws SQLException {
        String consultaCuentasSaldoMenor = "select * from cuentas where saldo < " + montoMaximo;
        Statement stCuentasSaldoMenor = connection.createStatement();
        ResultSet rsCuentasSaldoMenor = stCuentasSaldoMenor.executeQuery(consultaCuentasSaldoMenor);

        System.out.println("Cuentas con saldo menor a " + montoMaximo + ":");
        System.out.println("Num_Cuenta  DNI_Cliente  Saldo");
        System.out.println("---------------------------------");
        while (rsCuentasSaldoMenor.next()) {
            String numCuenta = rsCuentasSaldoMenor.getString("num_cuenta");
            String dniCliente = rsCuentasSaldoMenor.getString("dni_cliente");
            double saldo = rsCuentasSaldoMenor.getDouble("saldo");
            System.out.println(numCuenta + "  " + dniCliente + "  " + saldo);
        }
    }
}
