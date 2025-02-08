import java.sql.*;
import java.util.Scanner;

public class BaseballApp {
     // Connects to the Database
     private static final String URL = "jdbc:sqlserver://cxp-sql-03\\rxg636;" +
     "databaseName=Baseball League;" +
     "user=adminUser;" +
     "password=45BaseBall!@#054;" +
     "encrypt=true;" +
     "trustServerCertificate=true;" +
     "loginTimeout=15;";
    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL)) {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\n=== Baseball Management System ===");
                System.out.println("1. Player Management");
                System.out.println("2. Team Management");
                System.out.println("3. Statistics Management");
                System.out.println("4. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
    
                switch (choice) {
                    case 1:
                        playerManagementMenu(conn, scanner);
                        break;
                    case 2:
                        teamManagementMenu(conn, scanner);
                        break;
                    case 3:
                        statisticsManagementMenu(conn, scanner);
                        break;
                    case 4: {
                        System.out.println("Exiting...");
                        return;
                    }
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }                     

    // Player Management Menu
    private static void playerManagementMenu(Connection conn, Scanner scanner) throws SQLException {
        while (true) {
            System.out.println("\n=== Player Management ===");
            System.out.println("1. Fetch Player's Career Statistics");
            System.out.println("2. Back to Main Menu");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
    
            switch (choice) {
                case 1:
                    fetchPlayerCareerStats(conn, scanner);
                    break;
                case 2:
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
    
    // Team management Menu
    private static void teamManagementMenu(Connection conn, Scanner scanner) throws SQLException {
        while (true) {
            System.out.println("\n=== Team Management ===");
            System.out.println("1. Retrieve Players by Team");
            System.out.println("2. Retrieve Teams with Most Wins");
            System.out.println("3. Back to Main Menu");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
    
            switch (choice) {
                case 1:
                    retrievePlayersByTeam(conn, scanner);
                    break;
                case 2:
                    retrieveTeamsWithMostWins(conn, scanner);
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
    
    // Statistics Management Menu
    private static void statisticsManagementMenu(Connection conn, Scanner scanner) throws SQLException {
        while (true) {
            System.out.println("\n=== Statistics Management ===");
            System.out.println("1. Retrieve Top N Players by Statistic");
            System.out.println("2. Back to Main Menu");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
    
            switch (choice) {
                case 1:
                    retrieveTopNPlayersByStatistic(conn, scanner);
                    break;
                case 2:
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // Fetch Player's Career Statistics
    private static void fetchPlayerCareerStats(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter Player ID: ");
        int playerID = scanner.nextInt();
        System.out.print("Enter the aggregate statistic to fetch (e.g., 'AVG', 'SUM', 'MAX', 'MIN'): ");
        String aggregate = scanner.next().toUpperCase();
        System.out.print("Enter the statistic column name (e.g., 'hits', 'runs', 'strikeouts'): ");
        String statColumn = scanner.next().toLowerCase();

         // Query
        String query = String.format(
            "SELECT %s(%s) AS aggregate_stat FROM battingStats WHERE playerID = ?", aggregate, statColumn);

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, playerID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.printf("Player %d - %s of %s: %s%n",
                            playerID, aggregate, statColumn, rs.getString("aggregate_stat"));
                } else {
                    System.out.println("No statistics found for the specified player.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error executing query. Check your inputs.");
            e.printStackTrace();
        }
    }

     // Fectches the Top N players stats
    private static void retrieveTopNPlayersByStatistic(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter the number of players to retrieve (N): ");
        int topN = scanner.nextInt();
        System.out.print("Enter the statistic column name (e.g., 'hits', 'runs', 'strikeouts'): ");
        String statColumn = scanner.next().toLowerCase();
        System.out.print("Enter the year to analyze: ");
        int year = scanner.nextInt();
    
        // Query String 
        String query = String.format(
            "SELECT TOP %d p.playerID, p.firstName, p.lastName, SUM(b.%s) AS total_stat " +
            "FROM player p " +
            "JOIN battingStats b ON p.playerID = b.playerID " +
            "JOIN games g ON b.gameID = g.gameID " +
            "WHERE YEAR(g.date) = ? " +
            "GROUP BY p.playerID, p.firstName, p.lastName " +
            "ORDER BY total_stat DESC", topN, statColumn);
    
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, year);
    
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.printf("Top %d players for '%s' in %d:%n", topN, statColumn, year);
                while (rs.next()) {
                    System.out.printf("ID: %d, Name: %s %s, Total %s: %d%n",
                            rs.getInt("playerID"),
                            rs.getString("firstName"),
                            rs.getString("lastName"),
                            statColumn,
                            rs.getInt("total_stat"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error executing query. Check your inputs.");
            e.printStackTrace();
        }
    }

    // Retrieve Players by Team
    private static void retrievePlayersByTeam(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter Team ID: ");
        int teamID = scanner.nextInt();

         // Query String
        String query = "{CALL GetPlayersByTeam(?)}";
        try (CallableStatement stmt = conn.prepareCall(query)) {
            stmt.setInt(1, teamID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    System.out.printf("ID: %d, Name: %s %s, Position: %s, Salary: %d, Status: %s%n",
                            rs.getInt("playerID"),
                            rs.getString("firstName"),
                            rs.getString("lastName"),
                            rs.getString("position"),
                            rs.getInt("salary"),
                            rs.getString("status"));
                }
            }
        }
    }

     // Retrieves the team with the most wins
    private static void retrieveTeamsWithMostWins(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter the year to analyze: ");
        int year = scanner.nextInt();

         // Query String
        String query = 
            "SELECT t.teamID, t.teamName, t.teamAbbreviation, ts.wins " +
            "FROM teamStats ts " +
            "JOIN team t ON ts.teamID = t.teamID " +
            "WHERE ts.year = ? " +
            "ORDER BY ts.wins DESC";
    
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, year);
    
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.printf("\nTeams with the most wins in %d:%n", year);
                System.out.println("Team ID | Team Name               | Abbreviation | Wins");
                System.out.println("--------------------------------------------------------");
    
                while (rs.next()) {
                    System.out.printf("%-8d| %-23s| %-13s| %d%n",
                            rs.getInt("teamID"),
                            rs.getString("teamName"),
                            rs.getString("teamAbbreviation"),
                            rs.getInt("wins"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving teams. Please check your inputs.");
            e.printStackTrace();
        }
    }


}
