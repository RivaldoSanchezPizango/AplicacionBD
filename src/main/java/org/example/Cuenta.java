package org.example;

import java.sql.*;


public class Cuenta {
    // VAMOS A CREAR UNA TABLA CUENTA CON 4 COLUMNAS (ID / NUMERO DE CUENTA / TITULAR / SALDO)

    private static final String SQL_TABLE_CREATE = "DROP TABLE IF EXISTS CUENTAS;" +
            "CREATE TABLE CUENTAS (" +
            "ID INT PRIMARY KEY," +
            "NUMERO_CUENTA INT NOT NULL," +
            "TITULAR VARCHAR (100) NOT NULL,"+
            "SALDO NUMERIC(10,2) NOT NULL" +
            ")";
            // NUMERIC (10, 2) EJ: 12345678,91

    private static final String SQL_INSERT = "INSERT INTO CUENTAS VALUES (?,?,?,?)";

    private static final String SQL_SELECT = "SELECT * FROM CUENTAS";

    private static  final String SQL_UPDATE = "UPDATE CUENTAS SET SALDO=? WHERE ID=?";

    public static void main(String[] args) {

        Connection connection = null;

        try {
            connection = getConnection();

            Statement statement = connection.createStatement();
            statement.execute(SQL_TABLE_CREATE);

            // VAMOS A INSERTAR VALORES EN LA TABLA
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT);

            // CARGAMOS VALORES
            preparedStatement.setInt(1, 1);
            preparedStatement.setInt(2,154697);
            preparedStatement.setString(3, "Rivaldo");
            preparedStatement.setDouble(4, 2354.45);

            // EJECUTAMOS LA ORDEN
            preparedStatement.execute();
            // GUARDAMOS Y IMPRIMIMOS
            ResultSet rs = statement.executeQuery(SQL_SELECT);

            while (rs.next()){
                System.out.println("Los datos de la cuenta y el saldo inicial: " +
                        "ID: " + rs.getInt(1) +
                        " - Nro cuenta: " + rs.getInt(2) + " - Nombre: " + rs.getString(3) +
                        " - Saldo: " + rs.getDouble(4));
            }

            // GENERAMOS UNA TRANSACCION
            connection.setAutoCommit(false); // para poder manejar el momento en el que vamos a hacer commit
            
            PreparedStatement preparedStatementUpdate = connection.prepareStatement(SQL_UPDATE);
            preparedStatementUpdate.setDouble(1, 3430.93);
            preparedStatementUpdate.setInt(2, 1);

            preparedStatementUpdate.execute();
            int exception = 4/0;;
            connection.commit();

            // BUENA PRACTICA
            connection.setAutoCommit(true);

            ResultSet rs1 = statement.executeQuery(SQL_SELECT);
            while (rs1.next()) {
                System.out.println("El saldo actualizado es el siguiente: " +
                        "ID: " + rs1.getInt(1) +
                        " - Nro cuenta: " + rs1.getInt(2) + " - Nombre: " + rs1.getString(3) +
                        " - Saldo: " + rs1.getDouble(4));
            }


        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            connection = getConnection();
            // nos volvimos a conectar a la base para consultar como quedaron los datos
            Statement statement = connection.createStatement();
            ResultSet rs2 = statement.executeQuery(SQL_SELECT); // SELECT * FROM CUENTAS

            while (rs2.next()) {
                System.out.println("El saldo de la cuenta luego del Rollback es : " +
                        "ID: " + rs2.getInt(1) +
                        " - Nro cuenta: " + rs2.getInt(2) + " - Nombre: " + rs2.getString(3) +
                        " - Saldo: " + rs2.getDouble(4));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private static Connection getConnection () throws Exception {
        Class.forName("org.h2.Driver");
        return DriverManager.getConnection("jdbc:h2:~/ConsultasYTransacciones", "sa", "sa");
    }
}