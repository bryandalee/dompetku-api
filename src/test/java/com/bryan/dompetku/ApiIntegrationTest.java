package com.bryan.dompetku;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void requestWithoutToken_isRejected() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void register_createsUserWithDefaultCategories() throws Exception {
        String token = register("budi@mail.com");

        mockMvc.perform(get("/api/categories").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6));
    }

    @Test
    void register_withSameEmail_returnsConflict() throws Exception {
        register("kembar@mail.com");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody("kembar@mail.com")))
                .andExpect(status().isConflict());
    }

    @Test
    void login_withWrongPassword_returnsUnauthorized() throws Exception {
        register("salah@mail.com");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "salah@mail.com", "password": "bukanpassword"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Email atau password salah"));
    }

    @Test
    void user_cannotAccessAnotherUsersCategory() throws Exception {
        String tokenA = register("userA@mail.com");
        String tokenB = register("userB@mail.com");
        Long categoryIdOfA = createCategory(tokenA, "Kopi");

        mockMvc.perform(get("/api/categories/" + categoryIdOfA).header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTransaction_isCountedInMonthlyReport() throws Exception {
        String token = register("laporan@mail.com");
        Long categoryId = createCategory(token, "Jajan");

        mockMvc.perform(post("/api/transactions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount": 15000, "transactionDate": "2026-10-05", "note": "Es kopi", "categoryId": %d}
                                """.formatted(categoryId)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/reports/monthly?month=2026-10").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalExpense").value(15000))
                .andExpect(jsonPath("$.balance").value(-15000));
    }

    private String register(String email) throws Exception {
        String response = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody(email)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(response, "$.token");
    }

    private String registerBody(String email) {
        return """
                {"name": "Tester", "email": "%s", "password": "rahasia123"}
                """.formatted(email);
    }

    private Long createCategory(String token, String name) throws Exception {
        String response = mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "%s", "type": "EXPENSE"}
                                """.formatted(name)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(response, "$.id")).longValue();
    }
}
