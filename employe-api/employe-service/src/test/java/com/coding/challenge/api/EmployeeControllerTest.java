package com.coding.challenge.api;

import com.coding.challenge.exception.EmployeeException;
import com.coding.challenge.exception.NotFoundEmployeeException;
import com.coding.challenge.exception.ValidationEmployeeException;
import com.coding.challenge.model.Employee;
import com.coding.challenge.model.Hobby;
import com.coding.challenge.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockitoSettings
class EmployeeControllerTest {

  private static final Employee EMPLOYEE = createTestingEmployee();

  private static final List<Employee> LIST_EMPLOY_RESPONSE = createEmployeeResponse();

  @Mock
  private EmployeeService employeeService;

  private EmployeeController testee;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {

    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    testee = new EmployeeController(employeeService);
    mockMvc = MockMvcBuilders.standaloneSetup(testee)
        .setMessageConverters(new MappingJackson2HttpMessageConverter())
        .build();
  }

  @Test
  void createEmployeeTest() throws Exception {
    // TODO: String expected = IOUtils.toString(getClass().getResource("created_employee.json"), UTF_8);
    Employee responseEmployee = LIST_EMPLOY_RESPONSE.get(0);
    doReturn(responseEmployee).when(employeeService).create(any());

    mockMvc.perform(post("/employee")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\n" +
                "    \"email\": \"test@test.com\",\n" +
                "    \"fullName\": \"First employee\",\n" +
                "    \"birthday\": \"2023-07-26\",\n" +
                "    \"hobbies\": []\n" +
                "}"))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers
            .content()
            .string("{\"uuid\":\"SOMEUIID\",\"email\":\"test@email.com\",\"fullName\":\"Test Lastname\",\"birthday\":[1996,7,15],\"hobbies\":[]}"));
  }

  @Test
  void updateEmployeeTest() throws Exception {
    // TODO: String expected = IOUtils.toString(getClass().getResource("updated_employee.json"), UTF_8);
    Employee responseEmployee = LIST_EMPLOY_RESPONSE.get(1);
    doReturn(responseEmployee).when(employeeService).update(any());

    mockMvc.perform(put("/employee")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\n" +
                "    \"email\": \"test@test.com\",\n" +
                "    \"fullName\": \"First employee\",\n" +
                "    \"birthday\": \"2023-07-26\",\n" +
                "    \"hobbies\": []\n" +
                "}"))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers
            .content()
            .string("{\"uuid\":\"RANDOMUIID\",\"email\":\"sTest@email.com\",\"fullName\":\"SecondTest Lastname\",\"birthday\":[1993,12,16],\"hobbies\":[{\"id\":null,\"name\":\"Play music\"},{\"id\":null,\"name\":\"HIIT\"}]}"));
  }

  @Test
  void getEmployeeTest() throws Exception {
    // TODO: String expected = IOUtils.toString(getClass().getResource("retrieved_employee.json"), UTF_8);
    doReturn(EMPLOYEE).when(employeeService).getById("123");

    mockMvc.perform(get("/employee/123")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers
            .content()
            .string("{\"uuid\":null,\"email\":\"test@email.com\",\"fullName\":\"Name Lastname\",\"birthday\":[1993,12,16],\"hobbies\":[{\"id\":null,\"name\":\"Play music\"},{\"id\":null,\"name\":\"HIIT\"}]}"));
  }

  @Test
  void retrieveEmployeesTest() throws Exception {
    // TODO: String expected = IOUtils.toString(getClass().getResource("retrieved_employee.json"), UTF_8);
    doReturn(LIST_EMPLOY_RESPONSE).when(employeeService).getListEmployee();

    mockMvc.perform(get("/employee/")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers
            .content()
            .string("[" +
                "{\"uuid\":\"SOMEUIID\",\"email\":\"test@email.com\",\"fullName\":\"Test Lastname\",\"birthday\":[1996,7,15],\"hobbies\":[]}," +
                "{\"uuid\":\"RANDOMUIID\",\"email\":\"sTest@email.com\",\"fullName\":\"SecondTest Lastname\",\"birthday\":[1993,12,16],\"hobbies\":[{\"id\":null,\"name\":\"Play music\"},{\"id\":null,\"name\":\"HIIT\"}]}]"));
  }

  @Test
  void deleteEmployeeTest() throws Exception {
    mockMvc.perform(delete("/employee")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\n" +
                "    \"email\": \"test@test.com\",\n" +
                "    \"fullName\": \"First employee\",\n" +
                "    \"birthday\": \"2023-07-26\",\n" +
                "    \"hobbies\": []\n" +
                "}"))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers
            .content()
            .string(""));

    verify(employeeService, times(1)).delete(any(Employee.class));
  }

  @Test
  void createEmployeeExceptionTest() throws Exception {
    doThrow(ValidationEmployeeException.class).when(employeeService).create(any());

    mockMvc.perform(post("/employee")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\n" +
                "    \"email\": \"test@test.com\",\n" +
                "    \"fullName\": \"First employee\",\n" +
                "    \"birthday\": \"2023-07-26\",\n" +
                "    \"hobbies\": []\n" +
                "}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void notFoundEmployeeExceptionTest() throws Exception {
    doThrow(NotFoundEmployeeException.class).when(employeeService).getById("123");

    mockMvc.perform(get("/employee/123")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void employeeExceptionTest() throws Exception {
    doThrow(EmployeeException.class).when(employeeService).update(any());

    mockMvc.perform(put("/employee")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\n" +
                "    \"email\": \"test@test.com\",\n" +
                "    \"fullName\": \"First employee\",\n" +
                "    \"birthday\": \"2023-07-26\",\n" +
                "    \"hobbies\": []\n" +
                "}"))
        .andExpect(status().isInternalServerError());
  }



  private static Employee createTestingEmployee() {
    Employee employee = new Employee();

    employee.setEmail("test@email.com");
    employee.setFullName("Name Lastname");
    LocalDate birthday = LocalDate.of(1993, 12, 16);
    employee.setBirthday(birthday);

    employee.setHobbies(retrieveListHobbies());
    return employee;
  }

  private static Set<Hobby> retrieveListHobbies() {
    Set<Hobby> hobbies = new HashSet<>();

    Hobby hobby = new Hobby();
    hobby.setName("Play music");
    hobbies.add(hobby);

    Hobby secondHobby = new Hobby();
    secondHobby.setName("HIIT");

    hobbies.add(secondHobby);

    return hobbies;
  }

  private static List<Employee> createEmployeeResponse() {
    List<Employee> responseEmployees = new ArrayList<>();
    Employee firstEmployee = new Employee();
    firstEmployee.setUuid("SOMEUIID");
    firstEmployee.setEmail("test@email.com");
    firstEmployee.setFullName("Test Lastname");
    LocalDate birthdayJuly = LocalDate.of(1996, 07, 15);
    firstEmployee.setBirthday(birthdayJuly);

    responseEmployees.add(firstEmployee);

    Employee secondEmployee = new Employee();

    secondEmployee.setUuid("RANDOMUIID");
    secondEmployee.setEmail("sTest@email.com");
    secondEmployee.setFullName("SecondTest Lastname");
    LocalDate birthdayDecember = LocalDate.of(1993, 12, 16);
    secondEmployee.setBirthday(birthdayDecember);
    secondEmployee.setHobbies(retrieveListHobbies());

    responseEmployees.add(secondEmployee);
    return responseEmployees;
  }
}
