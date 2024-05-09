package com.example.shop.configuration;


import com.example.shop.enums.Role;
import com.example.shop.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean; // Annotation này được sử dụng để đánh dấu một phương thức là một bean và được quản lý bởi Spring Context. Trong đoạn mã này, nó được sử dụng để tạo một bean để thực hiện khởi tạo người dùng admin mặc định.
import org.springframework.context.annotation.Configuration;
import com.example.shop.entily.User;
import org.springframework.security.crypto.password.PasswordEncoder; // Interface này cung cấp các phương thức để mã hóa mật khẩu. Trong ứng dụng này, nó được sử dụng để mã hóa mật khẩu của người dùng trước khi lưu vào cơ sở dữ liệu.

import java.util.HashSet; // Đây là một lớp trong Java Collections Framework, một tập hợp không có thứ tự và không chứa các phần tử trùng lặp. Trong đoạn mã này, nó được sử dụng để lưu trữ vai trò của người dùng.
import java.util.Optional; // Đây là một lớp trong Java 8 được sử dụng để xử lý giá trị có thể null một cách an toàn. Trong đoạn mã này, nó được sử dụng để kiểm tra xem có người dùng nào tồn tại hay không khi tìm kiếm trong UserRepository

@Configuration
public class AppplicationInitConfig {

    // Autowired PasswordEncoder để mã hóa mật khẩu.
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Khai báo biến static initialized để theo dõi xem ứng dụng đã được khởi tạo chưa.
    private static boolean initialized = false;

    // Tạo một Bean kiểu ApplicationRunner, một cách để thực thi logic khi ứng dụng được khởi động, và chấp nhận một tham chiếu đến UserRepository.
    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        // Định nghĩa phần thân của phương thức run() của ApplicationRunner.
        return args -> {
            // Tìm người dùng có email là "admin@example.com" trong UserRepository.
            Optional<User> existingAdminUser = userRepository.findByEmail("admin@gmail.com");
            // Kiểm tra xem có người dùng admin nào tồn tại không.
            if (existingAdminUser.isEmpty()) {
                // Tạo một HashSet roles chứa vai trò của người dùng và thêm vai trò ADMIN vào đó.
                var roles = new HashSet<String>();
                roles.add(Role.ADMIN.name());

                // Tạo một đối tượng User mới, đặt email, mã hóa mật khẩu bằng passwordEncoder, và đặt vai trò ADMIN cho người dùng.
                User user = new User();
                user.setEmail("admin@gmail.com"); // Đặt email cho người dùng admin
                user.setPassword(passwordEncoder.encode("admin")); // Đặt mật khẩu cho người dùng admin
//                user.setRole(Role.ADMIN.name());
                // Thêm các thông tin khác cho người dùng nếu cần
                // Lưu người dùng mới vào UserRepository.
                userRepository.save(user);
                // Đánh dấu rằng quá trình khởi tạo đã hoàn thành.
                initialized = true;
            }
        };
    }
}
