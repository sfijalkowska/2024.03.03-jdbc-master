package pl.comarch.camp.it.jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class App {
    static Connection connection;
    static PreparedStatement saveStatement;
    private static final String SAVE_SQL = "INSERT INTO tuser (login, password, role) VALUES (?,?,?);";
    public static void main(String[] args) {
        connect();
        User user = new User();
        user.setLogin("zbyszek");
        user.setPassword("zbyszek123");
        user.setRole(User.Role.USER);
        save2(user);

        System.out.println(user);

        /*List<User> allUsers = getAllUsers();
        System.out.println(allUsers);*/

        /*getUserById(4).ifPresent(System.out::println);
        Optional<User> userBox = getUserById(2);
        if(userBox.isPresent()) {
            User user2 = userBox.get();
            user2.setPassword("turbo nowe haslo");
            user2.setLogin("nowy login");
            user2.setRole(User.Role.USER);
            updateUser(user2);
        }*/

        /*getUserById(3).ifPresent(App::deleteUser);
        System.out.println(getAllUsers());*/
        disconnect();
    }

    public static void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "");
            saveStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS);
        } catch (SQLException e) {
            System.out.println("Problem z polaczeniem do bazy !!");
        } catch (ClassNotFoundException e) {
            System.out.println("Nie ma sterownika !!");
        }
    }

    public static void save(User user) {
        try {
            String sql = new StringBuilder()
                    .append("INSERT INTO tuser (login, password, role) VALUES ('")
                    .append(user.getLogin()).append("', '")
                    .append(user.getPassword()).append("', '")
                    .append(user.getRole().toString())
                    .append("');").toString();

            Statement statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println("Cos nie pyklo !!");
        }
    }

    public static void save2(User user) {
        try {
            saveStatement.setString(1, user.getLogin());
            saveStatement.setString(2, user.getPassword());
            saveStatement.setString(3, user.getRole().name());

            saveStatement.executeUpdate();

            ResultSet rs = saveStatement.getGeneratedKeys();
            rs.next();
            user.setId(rs.getInt(1));

            saveStatement.clearParameters();
        } catch (SQLException e) {
            System.out.println("popsulo sie zapisywanie !!");
        }
    }

    public static Optional<User> getUserById(int id) {
        try {
            String sql = "SELECT * FROM tuser WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) {
                return Optional.of(new User(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("password"),
                        User.Role.valueOf(rs.getString("role"))));
            }
        } catch (SQLException e) {
            System.out.println("popsulo sie !!");
        }
        return Optional.empty();
    }

    public static List<User> getAllUsers() {
        List<User> result = new ArrayList<>();
        try {
            String sql = "SELECT * FROM tuser";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next()) {
                result.add(
                        new User(
                                rs.getInt("id"),
                                rs.getString("login"),
                                rs.getString("password"),
                                User.Role.valueOf(rs.getString("role")
                                )
                        ));
            }
        } catch (SQLException e) {
            System.out.println("pobieranie wszystkich userow sie zepsulo !!");
        }
        return result;
    }

    public static void updateUser(User user) {
        try {
            String sql = "UPDATE tuser SET login = ?, password = ?, role = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, user.getLogin());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getRole().name());
            preparedStatement.setInt(4, user.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("nie da sie zupdatowac usera !!");
        }
    }

    public static void deleteUser(User user) {
        try {
            String sql = "DELETE FROM tuser WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, user.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Zepsulo sie usuwanie !!");
        }
    }

    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("Nie udalo sie zamknac polaczenia !!!");
        }
    }

    public static void connectTest() {
        try(Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "")) {
            //Class.forName("com.mysql.cj.jdbc.Driver");
            ///connection.prepareCall()
        } catch (SQLException e) {

        }
    }
}
