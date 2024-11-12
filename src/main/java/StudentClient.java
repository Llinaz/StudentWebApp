import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class StudentClient {
    private static final String URL = "http://localhost:8081/StudentApp/students";
    private final Gson gson = new Gson();


    public List<Student> getStudents() throws Exception {
        URL url = new URL(URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        List<Student> students = gson.fromJson(reader, new TypeToken<List<Student>>(){}.getType());
        reader.close();

        return students;
    }


    public void addStudent(Student student) throws Exception {
        URL url = new URL(URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);

        String data = String.format("first_name=%s&last_name=%s&middle_name=%s&birth_date=%s&group=%s",
                student.getFirstName(),
                student.getLastName(),
                student.getMiddleName(),
                student.getBirthDate().toString(),
                student.getGroup()
        );

        try (OutputStream outputStream = connection.getOutputStream()) {
            byte[] input = data.getBytes(StandardCharsets.UTF_8);
            outputStream.write(input, 0, input.length);
        }

        if (connection.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
            System.out.println("Student added successfully.");
        } else {
            System.out.println("Failed to add student. Response code: " + connection.getResponseCode());
        }
    }

    public void deleteStudent(int id) throws Exception {
        URL url = new URL(URL + "?id=" + id);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");

        if (connection.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
            System.out.println("Student deleted successfully.");
        } else {
            System.out.println("Failed to delete student. Response code: " + connection.getResponseCode());
        }
    }


    public void printStudents(List<Student> students) {
        students.forEach(System.out::println);
    }

}
