package org.example;

import java.sql.*;
import java.util.Scanner;

public class LibraryApp {

    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/Library";
    private static final String USERNAME = "Denis";
    private static final String PASSWORD = "Den123";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
             Scanner scanner = new Scanner(System.in)) {

            boolean running = true;
            while (running) {
                displayMenu();
                int choice = scanner.nextInt();
                scanner.nextLine(); // Считываем новую строку

                switch (choice) {
                    case 1:
                        displayBooks(connection);
                        break;
                    case 2:
                        addNewBook(connection, scanner);
                        break;
                    case 3:
                        editBook(connection, scanner);
                        break;
                    case 4:
                        removeBook(connection, scanner);
                        break;
                    case 0:
                        running = false;
                        break;
                    default:
                        System.out.println("Неверный ввод. Попробуйте еще раз.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());
        }
    }

    private static void displayMenu() {
        System.out.println("Выберите действие:");
        System.out.println("1 - Вывести список книг");
        System.out.println("2 - Добавить книгу");
        System.out.println("3 - Редактировать книгу");
        System.out.println("4 - Удалить книгу");
        System.out.println("0 - Выход");
    }

    private static void displayBooks(Connection connection) throws SQLException {
        String query = "SELECT * FROM Book";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                int year = resultSet.getInt("year");
                System.out.printf("%d. %s, Автор: %s, Год: %d%n", id, title, author, year);
            }
        }
    }

    private static void addNewBook(Connection connection, Scanner scanner) {
        try {
            System.out.print("Введите название книги: ");
            String title = scanner.nextLine();
            System.out.print("Введите автора книги: ");
            String author = scanner.nextLine();
            System.out.print("Введите год публикации: ");
            int year = scanner.nextInt();
            scanner.nextLine(); // Считываем новую строку

            String insertQuery = "INSERT INTO Book (title, author, year) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, title);
                preparedStatement.setString(2, author);
                preparedStatement.setInt(3, year);
                preparedStatement.executeUpdate();
                System.out.println("Книга успешно добавлена.");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении книги: " + e.getMessage());
        }
    }

    private static void removeBook(Connection connection, Scanner scanner) {
        System.out.print("Введите ID книги, которую хотите удалить: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Считываем новую строку

        try {
            String deleteQuery = "DELETE FROM Book WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setInt(1, id);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Книга успешно удалена.");
                } else {
                    System.out.println("Книга с указанным ID не найдена.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении книги: " + e.getMessage());
        }
    }

    private static void editBook(Connection connection, Scanner scanner) {
        System.out.print("Введите ID книги, которую хотите редактировать: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Считываем новую строку

        System.out.print("Введите новое название книги: ");
        String title = scanner.nextLine();
        System.out.print("Введите нового автора книги: ");
        String author = scanner.nextLine();
        System.out.print("Введите новый год публикации: ");
        int year = scanner.nextInt();
        scanner.nextLine(); // Считываем новую строку

        try {
            String updateQuery = "UPDATE Book SET title = ?, author = ?, year = ? WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, title);
                preparedStatement.setString(2, author);
                preparedStatement.setInt(3, year);
                preparedStatement.setInt(4, id);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Данные книги успешно обновлены.");
                } else {
                    System.out.println("Книга с указанным ID не найдена.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении книги: " + e.getMessage());
        }
    }
}
