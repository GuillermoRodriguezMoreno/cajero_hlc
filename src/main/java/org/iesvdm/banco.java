package banco;
import java.sql.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.time.*;

public class banco {
    static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String dni, consulta, nombre, apellidos, cuenta;
        double saldo = 0, importe= 0, importe_total = 0;
        LocalDateTime fecha;
        Date dia;
        Time hora;
        Connection connection;
        Statement st;
        ResultSet rs;

        System.setProperty("jdbc.drivers", "com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/cuentas_bancarias";
        String user = "mysql_miguel";
        String pass = "mysql_miguel";
        int opcion;
        try{
            connection = DriverManager.getConnection(url ,user, pass);
            System.out.println("Connection success.");
            do {
                System.out.println("\nCajero Automático");
                System.out.println("1- Retirar fondos");
                System.out.println("2- Ingresar fondos");
                System.out.println("3- Consultar movimientos");
                System.out.println("0- Salir");
                System.out.print("Seleccione una opción: ");
                opcion = scanner.nextInt();

                switch (opcion) {
                    case 1: // Retirar fondos
                        System.out.println("Introduzca la cuenta: ");
                        cuenta = scanner.next();
                        System.out.print("Ingrese la cantidad a retirar: ");
                        importe = scanner.nextDouble() * -1;
                        fecha = LocalDateTime.now();
                        consulta = "insert into movimientos values ("
                                + "'" + cuenta + "'" + ", " + "'" + fecha + "'" + " , " + importe + ")";
                        st = connection.createStatement();
                        st.executeUpdate(consulta);
                        break;
                    case 2: // Ingresar fondos
                        System.out.println("Introduzca la cuenta: ");
                        cuenta = scanner.next();
                        //cuenta = "12345678901234567890";
                        System.out.print("Ingrese la cantidad a depositar: ");
                        importe = scanner.nextDouble();
                        fecha = LocalDateTime.now();
                        consulta = "insert into movimientos values ("
                                + cuenta + ", " + "'" + fecha + "'" + " , " + importe + ")";
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
                        rs=st.executeQuery(consulta);
                        System.out.println("Cuenta Fecha Importe");
                        System.out.println("-------------------- ------------------- ------------- ");
                        while(rs.next())
                        {
                            cuenta = rs.getString("num_cuenta");
                            dia = rs.getDate("fecha");
                            hora = rs.getTime("fecha");
                            importe = rs.getFloat("importe");
                            System.out.println(cuenta + " " + dia + " " + hora + " " + importe);

                        }

                        break;
                    case 0: // Salir
                        System.out.println("Saliendo del sistema.");
                        break;
                    default:
                        System.out.println("Opción no válida. Intente nuevamente.");
                }
            } while (opcion != 0);

            scanner.close();

        } catch (SQLException sqle){
            System.out.println(sqle.getMessage());
        }
    }
}