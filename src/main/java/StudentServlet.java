import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/students")
public class StudentServlet extends HttpServlet {
    private Connection connection;
    private final Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        String URL = "jdbc:postgresql://localhost:5432/student_db";
        String USERNAME = "postgres";
        String PASSWORD = "122711";
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            throw new ServletException("Could not connect to the database", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Student> studentList = getStudents();
        sendJsonResponse(resp, studentList, HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LocalDate birthDate;
        String fistName = req.getParameter("first_name");
        String lastName = req.getParameter("last_Name");
        String middleName = req.getParameter("middle_Name");
        birthDate = LocalDate.parse(String.valueOf(req.getParameter("birth_date")));
        String group = req.getParameter("group_name");
        Student student = new Student(fistName, lastName, middleName, birthDate, group);
        addStudent(student);
        resp.setStatus(HttpServletResponse.SC_CREATED);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        deleteStudent(id);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private List<Student> getStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";
        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)) {
            while(resultSet.next()) {
                Student student = new Student(
                        resultSet.getInt("id"),
                        resultSet.getString("first_Name"),
                        resultSet.getString("last_Name"),
                        resultSet.getString("middle_Name"),
                        resultSet.getDate("birth_date").toLocalDate(),
                        resultSet.getString("group_Name")
                );
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    private void addStudent(Student student) {
        String sql = "INSERT INTO students (first_name, last_name, middle_name, birth_date, group_name) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, student.getFirstName());
            preparedStatement.setString(2, student.getLastName());
            preparedStatement.setString(3, student.getMiddleName());
            preparedStatement.setDate(4, Date.valueOf(student.getBirthDate()));
            preparedStatement.setString(5, student.getGroup());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteStudent(int id) {
        String sql = "DELETE FROM students WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void sendJsonResponse(HttpServletResponse resp, Object data, int status) throws  IOException {
        resp.setContentType("application/json");
        resp.setStatus(status);
        resp.getWriter().print(gson.toJson(data));
    }
}
