# MMOCore Integration for RPGSystemLOL

## Tổng quan

RPGSystemLOL đã được tích hợp với hệ thống MMOCore/MythicLib để sử dụng API chính thức của MMOCore cho việc quản lý kỹ năng.

## Cấu trúc mới

### Package Structure

```
RPGSystemLOL/src/
├── neo/rpgsystem/
│   ├── integration/
│   │   └── mmocore/
│   │       ├── MMOCoreIntegration.java      # Quản lý tích hợp với MMOCore
│   │       └── HumanSkillsRegistry.java     # Đăng ký Human skills
│   └── abilities/
│       └── mmocore/
│           └── humans/
│               ├── HumansActive1Handler.java    # Kiếm Kỹ: Cường Hóa
│               ├── HumansActive2Handler.java    # Tiến Công
│               └── HumansPassive1Handler.java   # Bạt Đao Kiếm
```

## Human Skills

### 1. HumansActive1Handler - Kiếm Kỹ: Cường Hóa

**ID:** `HUMANS_ACTIVE_1`

**Mô tả:** Buff Strength (luôn có), Speed (level >= 3), Health Boost (level >= 5)

**Modifiers:**
- `duration`: Thời gian buff (giây) - Default: 6-10 tùy level
- `strength`: Cấp độ Strength - Default: level/3
- `speed`: Cấp độ Speed - Default: level/3
- `health-boost`: Cấp độ Health Boost - Default: level/3
- `cooldown`: Thời gian hồi chiêu (giây)
- `mana`: Mana cost

**Particle Effects:**
- Flame particles gathering effect
- Red dust circles for Strength
- Aqua dust transition for Speed (level >= 3)
- Heart particles for Health Boost (level >= 5)

**Cấu hình MMOCore skill example:**
```yaml
HUMANS_ACTIVE_1:
  name: "Kiếm Kỹ: Cường Hóa"
  type: HUMANS_ACTIVE_1
  modifiers:
    level: 1
    duration: 8
    strength: 1
    speed: 1
    health-boost: 1
    cooldown: 10
    mana: 20
```

### 2. HumansActive2Handler - Tiến Công

**ID:** `HUMANS_ACTIVE_2`

**Mô tả:** Nhảy theo hình vòng cung đến mục tiêu và gây sát thương

**Modifiers:**
- `damage`: Sát thương gây ra - Default: 120.0
- `range`: Tầm tìm mục tiêu - Default: 16
- `cooldown`: Thời gian hồi chiêu (giây)
- `mana`: Mana cost

**Particle Effects:**
- Sweep attack particles during dash
- Smoke particles trail
- Sound effects for jump and hit

**Cấu hình MMOCore skill example:**
```yaml
HUMANS_ACTIVE_2:
  name: "Tiến Công"
  type: HUMANS_ACTIVE_2
  modifiers:
    damage: 120
    range: 16
    cooldown: 8
    mana: 30
```

### 3. HumansPassive1Handler - Bạt Đao Kiếm

**ID:** `HUMANS_PASSIVE_1`

**Mô tả:** Chém ngang 2 lần, sau đó bắn kiếm khí (level >= 3), hất tung (level >= 5)

**Modifiers:**
- `slash-damage`: Sát thương chém ngang (%) - Default: 100 + level*5
- `beam-damage`: Sát thương kiếm khí (%) - Default: 80 + level*6
- `cooldown`: Thời gian hồi chiêu (giây)

**Particle Effects:**
- Instant effect particles for sword beam
- Sweep attack for slashes

**Cấu hình MMOCore skill example:**
```yaml
HUMANS_PASSIVE_1:
  name: "Bạt Đao Kiếm"
  type: HUMANS_PASSIVE_1
  modifiers:
    level: 1
    slash-damage: 105
    beam-damage: 86
    cooldown: 5
```

## Cài đặt

### Yêu cầu

Plugin RPGSystemLOL yêu cầu các plugin sau (softdepend):
- **MythicLib** - Bắt buộc cho skill system
- **MMOCore** - Khuyến nghị cho đầy đủ tính năng
- **MMOItems** - Tùy chọn cho item integration

### Khởi động

Plugin sẽ tự động:
1. Kiểm tra MythicLib và MMOCore khi khởi động
2. Đăng ký tất cả Human skill handlers sau 1 tick
3. Log thông báo về trạng thái integration

**Console output:**
```
[RPGSystemLOL] MythicLib detected!
[RPGSystemLOL] MMOCore detected!
[RPGSystemLOL] Successfully registered Human skill handlers with MythicLib!
```

## Backward Compatibility

Các class skill cũ vẫn được giữ lại nhưng đã được đánh dấu `@Deprecated`:
- `neo.rpgsystem.abilities.rpgclass.humans.HumansActive1`
- `neo.rpgsystem.abilities.rpgclass.humans.HumansActive2`
- `neo.rpgsystem.abilities.rpgclass.humans.HumansPassive1`

**Lưu ý:** Các class này sẽ được xóa trong phiên bản tương lai. Vui lòng chuyển sang sử dụng MMOCore skill handlers.

## Phát triển thêm

### Thêm skill handler mới

1. Tạo class extends `SkillHandler<SimpleSkillResult>` trong package `neo.rpgsystem.abilities.mmocore.humans`
2. Implement methods:
   - Constructor: đăng ký skill ID và modifiers
   - `getResult()`: kiểm tra điều kiện cast
   - `whenCast()`: logic khi cast skill
3. Đăng ký trong `HumanSkillsRegistry.registerAll()`

### Example:

```java
public class HumansActive3Handler extends SkillHandler<SimpleSkillResult> {
    
    public HumansActive3Handler() {
        super("HUMANS_ACTIVE_3");
        registerModifiers("damage", "cooldown", "mana");
    }
    
    @Override
    public SimpleSkillResult getResult(SkillMetadata meta) {
        // Check conditions
        return new SimpleSkillResult(true);
    }
    
    @Override
    public void whenCast(SimpleSkillResult result, SkillMetadata meta) {
        Player p = meta.getCaster().getPlayer();
        double damage = meta.getParameter("damage");
        // Skill logic here
    }
}
```

## Troubleshooting

### MythicLib not found warning
```
[RPGSystemLOL] MythicLib not found. MMOCore integration disabled.
```
**Giải pháp:** Cài đặt MythicLib plugin

### Skill registration failed
```
[RPGSystemLOL] Failed to register skill handlers: [error message]
```
**Giải pháp:** 
1. Kiểm tra MythicLib version compatibility
2. Xem log chi tiết để biết lỗi cụ thể
3. Đảm bảo MMOCore đã load trước RPGSystemLOL

## Tài liệu tham khảo

- [MythicLib Documentation](https://gitlab.com/phoenix-dvpmt/mythiclib/-/wikis/home)
- [MMOCore Documentation](https://gitlab.com/phoenix-dvpmt/mmocore/-/wikis/home)
