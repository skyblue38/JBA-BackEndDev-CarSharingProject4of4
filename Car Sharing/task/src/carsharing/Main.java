package carsharing;
import java.util.*;
import java.sql.*;

public class Main {
    // JDBC driver name and database URL
    static String JDBC_DRIVER = "org.h2.Driver";
    static String DB_URL = "jdbc:h2:./src/carsharing/db/";
    static String dbArgName = "-databaseFileName";
    //  Database credentials (Not used in this exercise)
    // static String USER = "SA";
    // static String PASS = "";


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String mainMenuPrompt = "1. Log in as a manager\n2. Log in as a customer\n3. Create a customer\n0. Exit";
        String loginPrompt = "1. Company list\n2. Create a company\n0. Back";
        Connection conn;
        Statement stmt;
        String dbName = "";
        // get DB name from command Line
        int argLen = args.length;
        for (int i = 0; i < argLen - 1; i++) {
            if (args[i].equals(dbArgName)) {
                if (i + 1 < argLen) {
                    dbName = args[i + 1];
                }
                break;
            }
        }
        if (dbName.isEmpty()) {
            System.exit(-1);
        }  // exit if no filename
        try {
            //Register JDBC driver
            Class.forName(JDBC_DRIVER);
            //Open a connection
            conn = DriverManager.getConnection(DB_URL + dbName);
            conn.setAutoCommit(true);
            //Execute updates to create tables (if not already existing)
            stmt = conn.createStatement();
            String sql1 = "CREATE TABLE IF NOT EXISTS COMPANY " +
                    "(ID INTEGER AUTO_INCREMENT, " +
                    "NAME VARCHAR(255) UNIQUE not NULL, " +
                    "PRIMARY KEY (ID))";
            stmt.executeUpdate(sql1);
            String sql2 = "CREATE TABLE IF NOT EXISTS CAR" +
                    "(ID INTEGER AUTO_INCREMENT," +
                    "NAME VARCHAR(255) UNIQUE not NULL," +
                    "COMPANY_ID INTEGER not NULL," +
                    "PRIMARY KEY (ID)," +
                    "FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY(ID))";
            stmt.executeUpdate(sql2);
            String sql3 = "CREATE TABLE IF NOT EXISTS CUSTOMER" +
                    "(ID INTEGER AUTO_INCREMENT, " +
                    "NAME VARCHAR(255) UNIQUE not NULL, " +
                    "RENTED_CAR_ID INTEGER, " +
                    "PRIMARY KEY (ID), " +
                    "FOREIGN KEY (RENTED_CAR_ID) REFERENCES CAR(ID))";
            stmt.executeUpdate(sql3);
            // Main menu
            int menuChoice = 1;
            while (menuChoice != 0) {
                System.out.println(mainMenuPrompt);
                menuChoice = scanner.nextInt();
                switch (menuChoice) {
                    case 1:
                        //Login as Administrator
                        int loginChoice = 1;
                        while (loginChoice != 0) {
                            System.out.println(loginPrompt);
                            loginChoice = scanner.nextInt();
                            switch (loginChoice) {
                                case 1:
                                    doCompanyList(conn, scanner);
                                    break;
                                case 2:
                                    doCreateCo(conn, scanner);
                                    break;
                                case 0:
                                    break;
                                default:
                                    // just resume the while loop
                            }
                        }
                        break;
                    case 2:
                        //login as customer
                        doCustomerList(conn, scanner);
                        break;
                    case 3:
                        //Create Customer
                        doCreateCustomer(conn, scanner);
                        break;
                    case 0:
                        break;
                    default:
                        // resume the while loop
                }
            }
            // Clean-up environment
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    static void doCompanyList(Connection c, Scanner s) {
        String carListPrompt = "1. Car list\n2. Create a car\n0. Back";
        Integer coID;
        HashMap<Integer, String> coMap = new HashMap<>();
        try {
            Statement listStmt = c.createStatement();
            String sql = "SELECT * FROM COMPANY ORDER BY ID ASC";
            ResultSet r = listStmt.executeQuery(sql);
            if (r.next()) {
                System.out.println("Choose the company:");
                do {
                    coID = r.getInt("ID");
                    System.out.print(coID + ". ");
                    coMap.put(coID, r.getString("NAME"));
                    System.out.println(coMap.get(coID));
                } while (r.next());
                System.out.println("0. Back");
            } else {
                System.out.println("The company list is empty!");
                listStmt.close();
                return;
            }
            listStmt.close();
        } catch (SQLException se) {
            System.out.println(se);
            se.printStackTrace();
        }
        int coChoice = s.nextInt();
        if (coChoice == 0) { return; }
        System.out.println("'" + coMap.get(coChoice) + "' company:");
        int coMenuChoice = 1;
        while (coMenuChoice != 0) {
            System.out.println(carListPrompt);
            coMenuChoice = s.nextInt();
            switch (coMenuChoice) {
                case 1:
                    doCarList(c, coChoice);
                    break;
                case 2:
                    doCreateCar(c, s, coChoice);
                    break;
                case 0:
                    return;
                default:
                    // just resume the while loop
            }
        }
    }

    static void doCreateCo(Connection c, Scanner s) {
        System.out.println("Enter the company name:");
        String cName = "";
        while (cName.isEmpty()) {
            cName = s.nextLine();
        }
        try {
            Statement createStmt = c.createStatement();
            String cSql = "INSERT INTO COMPANY (NAME) VALUES ('" +
                    cName +
                    "')";
            int n = createStmt.executeUpdate(cSql);
            if (n > 0) {System.out.println("The company was created!");}
            createStmt.close();
        } catch (SQLException se) {
            System.out.println(se);
            se.printStackTrace();
        }
    }
    static void doCarList(Connection c, int coy) {
        int carIndex = 1;
        System.out.println("Car list:");
        try {
            Statement carListStmt = c.createStatement();
            String carListSql = "SELECT * FROM CAR WHERE COMPANY_ID=" + coy + " ORDER BY ID ASC";
            ResultSet r = carListStmt.executeQuery(carListSql);
            if (r.next()) {
                do {
                    //System.out.print(r.getInt("ID") + ". ");
                    System.out.print(carIndex + ". ");
                    carIndex++;
                    System.out.println(r.getString("NAME"));
                } while (r.next());
            } else {
                System.out.println("The car list is empty!");
            }
            carListStmt.close();
        } catch (SQLException se) {
            System.out.println(se);
            se.printStackTrace();
        }
    }
    static void doCreateCar(Connection c, Scanner s, int coy) {
        System.out.println("Enter the car name:");
        String carName = "";
        while (carName.isEmpty()) {
            carName = s.nextLine();
        }
        try {
            Statement createStmt = c.createStatement();
            String carSql = "INSERT INTO CAR (NAME, COMPANY_ID) VALUES ( '" +
                    carName + "', " + coy + ")";
            int n = createStmt.executeUpdate(carSql);
            if (n > 0) {System.out.println("The car was added!");}
            createStmt.close();
        } catch (SQLException se) {
            System.out.println(se);
            se.printStackTrace();
        }
    }
    static void doCustomerList(Connection c, Scanner s) {
        //choose from list of customers
        String custListPrompt = "1. Rent a car\n2. Return a rented car\n3. My rented car\n0. Back";
        Integer custID;
        HashMap<Integer, String> custMap = new HashMap<>();
        try {
            Statement listCustStmt = c.createStatement();
            String sql4 = "SELECT * FROM CUSTOMER ORDER BY ID ASC";
            ResultSet r = listCustStmt.executeQuery(sql4);
            if (r.next()) {
                System.out.println("Customer list:");
                do {
                    custID = r.getInt("ID");
                    System.out.print(custID + ". ");
                    custMap.put(custID, r.getString("NAME"));
                    System.out.println(custMap.get(custID));
                } while (r.next());
                System.out.println("0. Back");
            } else {
                System.out.println("The customer list is empty!");
                listCustStmt.close();
                return;
            }
            listCustStmt.close();
        } catch (SQLException se) {
            System.out.println(se);
            se.printStackTrace();
        }
        int custChoice = s.nextInt();
        if (custChoice == 0) { return; }
        //System.out.println("Customer '" + custMap.get(custChoice) + "'");
        int coMenuChoice = 1;
        while (coMenuChoice != 0) {
            System.out.println(custListPrompt);
            coMenuChoice = s.nextInt();
            switch (coMenuChoice) {
                case 1:
                    doRentACar(c, s, custChoice);
                    break;
                case 2:
                    doReturnRentedCar(c, s, custChoice);
                    break;
                case 3:
                    doMyRentedCar(c, s, custChoice);
                    break;
                case 0:
                    return;
                default:
                    // just resume the while loop
            }
        }
    }
    static void doCreateCustomer(Connection c, Scanner s) {
        //build a customer record
        System.out.println("Enter the customer name:");
        String custName = "";
        while (custName.isEmpty()) {
            custName = s.nextLine();
        }
        try {
            Statement createCustStmt = c.createStatement();
            String custSql = "INSERT INTO CUSTOMER (NAME, RENTED_CAR_ID) VALUES ( '" +
                    custName + "', NULL )";
            int n = createCustStmt.executeUpdate(custSql);
            if (n > 0) {System.out.println("The customer was added!");}
            createCustStmt.close();
        } catch (SQLException se) {
            System.out.println(se);
            se.printStackTrace();
        }
    }
    static void doRentACar(Connection c, Scanner s, int custID) {
        // if customer already rented a car, return with message
        int carID;
        try {
            Statement custStmt = c.createStatement();
            String custSql1 = "SELECT * FROM CUSTOMER WHERE ID=" + custID;
            ResultSet rCust = custStmt.executeQuery(custSql1);
            if (rCust.next()) {
                do {
                    int rentedCarID = rCust.getInt("RENTED_CAR_ID");
                    if (rCust.wasNull()) {
                        // List companies
                        // get company choice from customer
                        // Display message and return 0 if list empty or chose "Back"
                        // list available company cars in ID order (origin 1.) not including those already rented!
                        // message and return 0 if that company has no cars or chose "Back"
                        carID = doSelectRental(c, s);
                        // customer record is updated to include rental
                        if (carID > 0) {
                            String custSql2 = "UPDATE CUSTOMER SET RENTED_CAR_ID=" +
                                carID + " WHERE ID=" + custID;
                            int q = custStmt.executeUpdate(custSql2);
                            if (q > 0 ) {
                                System.out.println("You rented '" + getCarName(c, carID) + "'");
                                custStmt.close();
                                return;
                            }
                        }
                    } else {
                        System.out.println("You've already rented a car!");
                        custStmt.close();
                        return;
                    }
                } while (rCust.next());
            } else {
                System.out.println("No customer with ID: " + custID);
                custStmt.close();
                return;
            }
            custStmt.close();
        } catch (SQLException se) {
            System.out.println(se);
        }
    }
    static void doReturnRentedCar(Connection c, Scanner s, int custID) {
        // Set currently rented car ID to NULL in the Customer record
        int carRented;
        try {
            Statement custRentedStmt = c.createStatement();
            String rentedSQL = "UPDATE CUSTOMER SET RENTED_CAR_ID=NULL WHERE ID=" +
                    custID + " AND RENTED_CAR_ID IS NOT NULL";
            int n = custRentedStmt.executeUpdate(rentedSQL);
            if (n > 0) {
                System.out.println("You've returned a rented car!");
            } else {
                System.out.println("You didn't rent a car!");
            }
            custRentedStmt.close();
        } catch (SQLException se) {
            System.out.println(se);
        }
    }
    static void doMyRentedCar(Connection c, Scanner s, int custID) {
        // Display details of currently rented car if any...
        int rentedCarID;
        String rentedCarName;
        int rentedCoyID;
        String rentedCarCoyName;
        try {
            Statement custCarStmt = c.createStatement();
            Statement carStmt = c.createStatement();
            Statement coyStmt = c.createStatement();
            String sql6 = "SELECT * FROM CUSTOMER WHERE ID=" +
                    custID + " AND RENTED_CAR_ID IS NOT NULL";
            ResultSet r1 = custCarStmt.executeQuery(sql6);
            if (r1.next()) {
                do {
                    rentedCarID = r1.getInt("RENTED_CAR_ID");
                } while (r1.next());
            } else {
                System.out.println("You didn't rent a car!");
                custCarStmt.close();
                carStmt.close();
                coyStmt.close();
                return;
            }
            String sql7 = "SELECT * FROM CAR WHERE ID=" + rentedCarID;
            ResultSet r2 = carStmt.executeQuery(sql7);
            if (r2.next()) {
                do {
                    rentedCarName = r2.getString("NAME");
                    rentedCoyID = r2.getInt("COMPANY_ID");
                } while (r2.next());
            } else {
                System.out.println("Database Integrity Error: No such car ID:" + rentedCarID);
                custCarStmt.close();
                carStmt.close();
                coyStmt.close();
                return;
            }
            String sql8 = "SELECT * FROM COMPANY WHERE ID=" + rentedCoyID;
            ResultSet r3 = coyStmt.executeQuery(sql8);
            if (r3.next()) {
                do {
                    rentedCarCoyName = r3.getString("NAME");
                } while (r3.next());
            } else {
                System.out.println("Database Integrity Error: no such company ID:" + rentedCoyID);
                custCarStmt.close();
                carStmt.close();
                coyStmt.close();
                return;
            }
            System.out.println("Your rented car:");
            System.out.println(rentedCarName);
            System.out.println("Company:");
            System.out.println(rentedCarCoyName);
            custCarStmt.close();
            carStmt.close();
            coyStmt.close();
        } catch (SQLException se) {
            System.out.println(se);
            se.printStackTrace();
        }
    }
    static String getCarName(Connection c, int carID) {
        // retrieve the name of a given carID
        String carName = "";
        try {
            Statement cs = c.createStatement();
            String getCarSql = "SELECT * FROM CAR WHERE ID=" + carID;
            ResultSet rCar = cs.executeQuery(getCarSql);
            if (rCar.next()) {
                carName = rCar.getString("NAME");
            }
            cs.close();
        } catch (SQLException se) {
            System.out.println(se);
        }
        return carName;
    }
    static int doSelectRental(Connection c, Scanner s) {
        // select a car from a list of those available from a chosen company
        int carID = 0;
        // Display a list of companies
        HashMap<Integer, String> coyMap = new HashMap<>();
        HashMap<Integer, Integer> carIDMap = new HashMap<>();
        HashMap<Integer, String> carNameMap = new HashMap<>();
        int coyID = 0;
        try {
            Statement coyStmt = c.createStatement();
            String coySql = "SELECT * FROM COMPANY";
            ResultSet rCoy = coyStmt.executeQuery(coySql);
            if (rCoy.next()) {
                System.out.println("Choose a company:");
                do {
                    coyID = rCoy.getInt("ID");
                    System.out.print(coyID + ". ");
                    coyMap.put(coyID, rCoy.getString("NAME"));
                    System.out.println(coyMap.get(coyID));
                } while (rCoy.next());
                System.out.println("0. Back");
            } else {
                System.out.println("The company list is empty!");
                coyStmt.close();
                return 0;
            }
            coyStmt.close();
        } catch (SQLException se) {
            System.out.println("se");
        }
        //get company choice
        int coyChosen = s.nextInt();
        if (coyChosen == 0) { return 0;}
        // Build list the company cars
        try {
            int carSeq = 1;
            Statement carStmt = c.createStatement();
            String carSQL = "SELECT * FROM CAR WHERE COMPANY_ID=" +
                    coyChosen + " ORDER BY ID ASC";
            ResultSet rCar = carStmt.executeQuery(carSQL);
            if (rCar.next()) {
                do {
                    carIDMap.put(carSeq,rCar.getInt("ID"));
                    //System.out.print(carSeq + ". ");
                    carNameMap.put(carSeq, rCar.getString("NAME"));
                    //System.out.println(carNameMap.get(carSeq));
                    carSeq++;
                } while (rCar.next());
            } else {
                System.out.println("No available cars in the '" +
                        coyMap.get(coyID) + "' company");
                carStmt.close();
                return 0;
            }
            carStmt.close();
        } catch (SQLException se) {
            System.out.println(se);
        }
        // scan all customers with rentals
        // and remove already rented cars from the carMaps
        int rentalCarID;
        try {
            Statement custStmt = c.createStatement();
            String custSql = "SELECT * FROM CUSTOMER WHERE RENTED_CAR_ID IS NOT NULL";
            ResultSet rCust = custStmt.executeQuery(custSql);
            if (rCust.next()) {
                do {
                    rentalCarID = rCust.getInt("RENTED_CAR_ID");
                    int carIDkey = 0;
                    if (carIDMap.containsValue(rentalCarID)) {
                        for (Map.Entry<Integer, Integer> entry : carIDMap.entrySet()) {
                            if (Objects.equals(rentalCarID, entry.getValue())) {
                                carIDkey = entry.getKey();
                                break;
                            }
                        }
                        carIDMap.remove(carIDkey);
                        carNameMap.remove(carIDkey);
                    }
                } while (rCust.next());
            }
            custStmt.close();
        } catch (SQLException se) {
            System.out.println(se);
        }
        if (carIDMap.isEmpty()) {
            System.out.println("No available cars in the '" +
                    coyMap.get(coyID) + "' company");
            return 0;
        }
        // Display list of available cars
        System.out.println("Choose a car:");
        carNameMap.forEach((key, value) -> {
            System.out.println(key + ". " + value);
        });
        System.out.println("0. Back");
        // return chosen car
        int carSeqNo = s.nextInt();
        if (carSeqNo == 0) {
            carID = 0;
        } else {
            carID = carIDMap.get(carSeqNo);
        }
        return carID;
    }
}
