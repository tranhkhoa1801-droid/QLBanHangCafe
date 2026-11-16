Thư mục chứa các thư viện jar ngoài (ví dụ: mssql-jdbc-*.jar).

1. Nếu bạn mở dự án bằng Maven (pom.xml) trên IntelliJ / VS Code / Eclipse:
   Maven sẽ tự động tải thư viện mssql-jdbc về máy. Bạn không cần chép thêm file jar vào đây.

2. Nếu bạn mở dự án dưới dạng Java Project thuần (không dùng Maven):
   Hãy chép file driver SQL Server (ví dụ: mssql-jdbc-12.6.1.jre11.jar) vào thư mục lib/ này.
   Cấu hình trong IntelliJ (.iml), Eclipse (.classpath), và VS Code (.vscode/settings.json)
   đã được thiết lập sẵn để tự động nhận diện tất cả các file jar trong thư mục lib/.
