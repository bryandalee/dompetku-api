package com.bryan.dompetku.config;

import com.bryan.dompetku.dto.BudgetRequest;
import com.bryan.dompetku.dto.RegisterRequest;
import com.bryan.dompetku.dto.TransactionRequest;
import com.bryan.dompetku.entity.Category;
import com.bryan.dompetku.repository.CategoryRepository;
import com.bryan.dompetku.repository.UserRepository;
import com.bryan.dompetku.service.AuthService;
import com.bryan.dompetku.service.BudgetService;
import com.bryan.dompetku.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

@Component
@Profile("demo")
public class DemoDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);
    private static final String DEMO_EMAIL = "demo@dompetku.com";
    private static final String DEMO_PASSWORD = "demo12345";

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AuthService authService;
    private final TransactionService transactionService;
    private final BudgetService budgetService;

    public DemoDataSeeder(UserRepository userRepository,
                          CategoryRepository categoryRepository,
                          AuthService authService,
                          TransactionService transactionService,
                          BudgetService budgetService) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.authService = authService;
        this.transactionService = transactionService;
        this.budgetService = budgetService;
    }

    @Override
    public void run(String... args) {
        if (userRepository.existsByEmail(DEMO_EMAIL)) {
            log.info("Akun demo sudah ada, data demo tidak dibuat ulang");
            return;
        }

        Long userId = authService.register(new RegisterRequest("Akun Demo", DEMO_EMAIL, DEMO_PASSWORD)).user().id();
        Map<String, Long> categoryIds = new HashMap<>();
        for (Category category : categoryRepository.findByUserIdOrderByNameAsc(userId)) {
            categoryIds.put(category.getName(), category.getId());
        }

        YearMonth thisMonth = YearMonth.now();
        YearMonth lastMonth = thisMonth.minusMonths(1);

        addTransaction(userId, categoryIds, "Gaji", 5_000_000, lastMonth, 1, "Gaji bulanan");
        addTransaction(userId, categoryIds, "Tagihan", 350_000, lastMonth, 3, "Internet rumah");
        addTransaction(userId, categoryIds, "Belanja", 425_000, lastMonth, 6, "Belanja bulanan");
        addTransaction(userId, categoryIds, "Makan & Minum", 45_000, lastMonth, 8, "Makan siang");
        addTransaction(userId, categoryIds, "Transportasi", 120_000, lastMonth, 12, "Isi bensin");
        addTransaction(userId, categoryIds, "Hiburan", 100_000, lastMonth, 20, "Nonton bioskop");
        addTransaction(userId, categoryIds, "Makan & Minum", 60_000, lastMonth, 24, "Makan malam");

        addTransaction(userId, categoryIds, "Gaji", 5_000_000, thisMonth, 1, "Gaji bulanan");
        addTransaction(userId, categoryIds, "Tagihan", 350_000, thisMonth, 2, "Internet rumah");
        addTransaction(userId, categoryIds, "Tagihan", 200_000, thisMonth, 3, "Token listrik");
        addTransaction(userId, categoryIds, "Makan & Minum", 35_000, thisMonth, 5, "Nasi padang");
        addTransaction(userId, categoryIds, "Transportasi", 25_000, thisMonth, 7, "Ojol ke kampus");
        addTransaction(userId, categoryIds, "Belanja", 180_000, thisMonth, 9, "Belanja mingguan");
        addTransaction(userId, categoryIds, "Makan & Minum", 50_000, thisMonth, 10, "Kopi dan roti");
        addTransaction(userId, categoryIds, "Hiburan", 65_000, thisMonth, 11, "Langganan streaming");
        addTransaction(userId, categoryIds, "Transportasi", 150_000, thisMonth, 12, "Isi bensin");

        addBudget(userId, categoryIds, "Makan & Minum", 300_000, thisMonth);
        addBudget(userId, categoryIds, "Transportasi", 250_000, thisMonth);
        addBudget(userId, categoryIds, "Hiburan", 75_000, thisMonth);
        addBudget(userId, categoryIds, "Belanja", 500_000, thisMonth);

        log.info("Data demo dibuat. Login dengan {} / {}", DEMO_EMAIL, DEMO_PASSWORD);
    }

    private void addTransaction(Long userId, Map<String, Long> categoryIds, String categoryName,
                                long amount, YearMonth month, int day, String note) {
        LocalDate date = month.atDay(day);
        if (date.isAfter(LocalDate.now())) {
            date = LocalDate.now();
        }
        transactionService.create(userId,
                new TransactionRequest(BigDecimal.valueOf(amount), date, note, categoryIds.get(categoryName)));
    }

    private void addBudget(Long userId, Map<String, Long> categoryIds, String categoryName,
                           long amount, YearMonth month) {
        budgetService.save(userId,
                new BudgetRequest(categoryIds.get(categoryName), month, BigDecimal.valueOf(amount)));
    }
}
