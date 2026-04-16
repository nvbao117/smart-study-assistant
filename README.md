# SmartStudyAssistant

Ứng dụng Android hỗ trợ học tập thông minh, được phát triển bởi nhóm sinh viên HCMUTE.

---

## Cấu hình ứng dụng

| Thông số | Giá trị |
|----------|---------|
| Application ID | `hcmute.edu.vn.smartstudyassistant` |
| Min SDK | 24 (Android 7.0 Nougat) |
| Target SDK | 36 |
| Compile SDK | 36 |
| Version Name | 1.0 |
| Version Code | 1 |
| Ngôn ngữ | Kotlin |
| Build System | Gradle 9.0.1 (AGP) |
| Java Compatibility | VERSION_11 |

### Dependencies chính

| Thư viện | Phiên bản |
|----------|-----------|
| androidx.core:core-ktx | 1.17.0 |
| androidx.appcompat:appcompat | 1.7.1 |
| com.google.android.material:material | 1.13.0 |
| androidx.activity:activity | 1.13.0 |
| androidx.constraintlayout:constraintlayout | 2.2.1 |

### Cài đặt Gradle (`gradle.properties`)

```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
```

---

## Quy trình làm việc với Git

### Cấu trúc nhánh

```
main          ← nhánh chính, code ổn định
└── dev       ← nhánh tích hợp, merge trước khi lên main
    ├── bao   ← nhánh của thành viên bao
    ├── tai   ← nhánh của thành viên tai
    └── duy   ← nhánh của thành viên duy
```

---

### Lần đầu clone dự án

```bash
# 1. Clone repo về máy
git clone https://github.com/nvbao117/smart-study-assistant.git

# 2. Vào thư mục dự án
cd smart-study-assistant

# 3. Chuyển sang nhánh của mình (ví dụ: tai)
git checkout tai
```

---

### Quy trình làm việc hàng ngày

```bash
# 1. Cập nhật code mới nhất từ remote về nhánh của mình
git pull origin tai

# 2. Code tính năng / sửa bug...

# 3. Kiểm tra trạng thái thay đổi
git status

# 4. Thêm file vào staging
git add .

# 5. Commit với message rõ ràng
git commit -m "feat: mô tả tính năng"

# 6. Push lên remote
git push origin tai
```

---

### Cập nhật code từ nhánh dev vào nhánh cá nhân

> Thực hiện thường xuyên để tránh conflict lớn.

```bash
# Đang ở nhánh tai, kéo code mới từ dev về
git pull origin dev
```

Nếu có conflict → giải quyết conflict → commit lại.

---

### Merge code vào nhánh dev (khi hoàn thành tính năng)

```bash
# 1. Chuyển sang nhánh dev
git checkout dev

# 2. Kéo code mới nhất của dev
git pull origin dev

# 3. Merge nhánh cá nhân vào dev
git merge tai

# 4. Push dev lên remote
git push origin dev
```

---

### Merge dev vào main (khi sẵn sàng release)

> Chỉ merge khi code trên `dev` đã được kiểm tra kỹ.

```bash
git checkout main
git pull origin main
git merge dev
git push origin main
```

---

### Quy ước đặt tên commit message

```
feat: thêm tính năng mới
fix: sửa lỗi
ui: thay đổi giao diện
refactor: cải thiện code không thay đổi chức năng
docs: cập nhật tài liệu
test: thêm/sửa test
```

**Ví dụ:**
```
feat: thêm màn hình đăng nhập
fix: sửa lỗi crash khi mở app lần đầu
ui: cập nhật màu sắc theme theo design
```

---

### Xử lý khi bị conflict

```bash
# 1. Xem file bị conflict
git status

# 2. Mở file conflict, tìm và sửa các đoạn:
# <<<<<<< HEAD
# code của mình
# =======
# code từ nhánh khác
# >>>>>>> dev

# 3. Sau khi sửa xong, đánh dấu đã resolve
git add <tên-file>

# 4. Hoàn tất merge
git commit -m "fix: resolve conflict"
```

---

### Các lệnh Git hay dùng

```bash
git status                    # Xem trạng thái thay đổi
git log --oneline --graph     # Xem lịch sử commit dạng cây
git diff                      # Xem chi tiết thay đổi chưa stage
git stash                     # Lưu tạm thay đổi chưa commit
git stash pop                 # Lấy lại thay đổi đã stash
git branch -a                 # Xem tất cả nhánh (local + remote)
```