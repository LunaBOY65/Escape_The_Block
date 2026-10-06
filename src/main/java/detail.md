# Escape the Block: คู่มือเรียนรู้ OOP จากโค้ด

เอกสารนี้อธิบายการจัดโครงสร้างเกมให้แต่ละคลาสมีหน้าที่ชัดเจน และไล่ลำดับตั้งแต่เริ่มโปรแกรมจนจบรอบเกม โค้ดใช้ Java 17 และ Swing โดยเน้นตัวอย่าง OOP ที่พบได้จริงในโปรเจกต์นี้

## 1. โครงสร้างคลาส

| คลาส | หน้าที่ | แนวคิดสำคัญ |
|---|---|---|
| `GameStart` | แสดงหน้าต้อนรับและเริ่มเกมเมื่อกดปุ่ม | จุดเริ่มต้นของ UI |
| `Game` | ประกอบหน้าต่าง, โมเดล, input และ timer เข้าด้วยกัน | Composition, การประสานงาน |
| `GameModel` | เก็บสถานะและบังคับใช้กติกาของเกม | Encapsulation, Model |
| `Player` | เก็บตำแหน่งและตรวจว่าจุดเมาส์อยู่บนตัวผู้เล่นหรือไม่ | State และพฤติกรรมของ object |
| `Obstacle` | เก็บขนาด ตำแหน่ง ความเร็ว และการเคลื่อนที่ของสิ่งกีดขวาง | State และพฤติกรรมของ object |
| `GamePanel` | วาดภาพจากข้อมูลที่อ่านจาก `GameModel` | View, Inheritance, Polymorphism |
| `Mouse` | รับ mouse event จาก Swing แล้วเก็บสถานะ input ล่าสุด | Event handling, Adapter class |
| `Configuration` | อ่านและเขียนคะแนนเวลาที่ดีที่สุดใน `config.xml` | แยกความรับผิดชอบ, Encapsulation |

## 2. ลำดับการทำงาน: จากเริ่มโปรแกรมถึงเริ่มเกม

1. เรียก `GameStart.main()` แล้ว Swing สร้างหน้าต้อนรับบน Event Dispatch Thread (EDT)
2. ผู้เล่นกดปุ่ม Start และ `GameStart` สร้าง `Game`
3. `Game` สร้าง `Configuration` และส่งให้ `GameModel` ผ่าน constructor
4. `GameModel` โหลดเวลาที่ดีที่สุด และสร้างผู้เล่นกับสิ่งกีดขวางเริ่มต้น
5. `Game` สร้าง `GamePanel(model)`, เชื่อม `Mouse` เข้ากับ panel แล้วแสดงหน้าต่างเกม
6. Swing `Timer` เรียกอัปเดตประมาณ 60 ครั้งต่อวินาที โดยไม่ต้องสร้าง busy loop หรือ thread เกมแยก

การส่ง object ที่ `Game` สร้างเข้า constructor ของ `GameModel` เรียกว่า **constructor injection** เป็นวิธีหนึ่งของ Dependency Injection: โมเดลได้รับสิ่งที่ต้องใช้จากภายนอก แทนการสร้างหน้าต่างหรืออ่านไฟล์เอง

## 3. ลำดับการทำงาน: ระหว่างเล่นและวาดภาพ

1. `Mouse` รับ `mousePressed`, `mouseDragged` และ `mouseReleased` แล้วอัปเดตตำแหน่ง/สถานะลาก
2. ในแต่ละ tick, `Game` ส่ง `Mouse` ให้ `GameModel.update()`
3. โมเดลเริ่มจับเวลาเมื่อผู้เล่นเริ่มลากวงกลม จากนั้นขยับผู้เล่นและสิ่งกีดขวาง
4. โมเดลตรวจการชนและตรวจว่าผู้เล่นออกนอกพื้นที่เล่นหรือไม่
5. `Game` เรียก `GamePanel.repaint()`; Swing จะเรียก `paintComponent()`
6. `GamePanel` อ่านค่าจาก model แล้ววาดฉาก ผู้เล่น สิ่งกีดขวาง และข้อความ Game Over โดยไม่แก้สถานะเกม
7. เมื่อจบรอบ โมเดลคำนวณเวลา ถ้าเป็นสถิติใหม่จะให้ `Configuration` บันทึกลงไฟล์
8. หลังจบรอบ ต้องปล่อยเมาส์แล้วลากอีกครั้งเพื่อเริ่มรอบใหม่

การจับเวลาใช้ `System.nanoTime()` ซึ่งเหมาะกับการวัดช่วงเวลาที่ผ่านไป เพราะเป็นนาฬิกาแบบ monotonic ไม่ใช่นาฬิกาปฏิทิน

## 4. OOP concepts ที่เห็นในโค้ด

### Class และ Object

คลาสเป็นแบบพิมพ์เขียว ส่วน object คือ instance ที่สร้างจากคลาส เช่น `new Player(...)` สร้างผู้เล่นหนึ่งตัว และแต่ละ `Obstacle` มีตำแหน่งกับความเร็วของตัวเอง

### Encapsulation (การห่อหุ้มข้อมูล)

สถานะของ `Player`, `Obstacle`, `Mouse` และ `GameModel` เป็น `private` ผู้ใช้งานต้องเรียกเมธอดที่คลาสเปิดให้ เช่น `moveTo()`, `move()` หรือ `getBestTimeSeconds()` แทนการแก้ field ภายในโดยตรง ช่วยควบคุมว่าข้อมูลเปลี่ยนได้อย่างไร

### Abstraction (การซ่อนรายละเอียด)

`Game` ขอให้ `GameModel` อัปเดตเกมโดยไม่ต้องรู้สูตรตรวจชน ส่วน `Configuration` ซ่อนรายละเอียดการจัดเก็บ XML ไว้หลัง `loadBestTime()` และ `saveBestTime()`

### Inheritance และ Polymorphism

`GamePanel extends JPanel` เป็นการสืบทอดจาก Swing และ override `paintComponent(Graphics)` เพื่อกำหนดวิธีวาดของ panel เอง เมื่อ Swing เรียก `paintComponent()` มันเรียก implementation ของ `GamePanel` ตามชนิดจริงของ object นี่คือตัวอย่าง polymorphism

`Mouse extends MouseAdapter` ใช้ adapter class ของ Swing เพื่อ override เฉพาะ event ที่ต้องใช้ แทนการเขียนเมธอดว่างของ listener ทุกตัว

### Composition (ประกอบ object)

`Game` มี `GameModel`, `GamePanel`, `Mouse`, `JFrame` และ `Timer`; `GameModel` มี `Player` และรายการ `Obstacle` ความสัมพันธ์แบบ “มี” (has-a) นี้ทำให้พฤติกรรมใหญ่เกิดจาก object ที่รับผิดชอบคนละส่วน แทนการยัดทุกอย่างไว้ในคลาสเดียว

## 5. Design patterns และแนวทางที่ควรรู้

- **MVC แบบง่าย**: `GameModel` เป็น Model (สถานะ/กติกา), `GamePanel` เป็น View (การวาด) และ `Game` ทำหน้าที่ประสานงานระหว่าง Model, View และ input แนวทางนี้ช่วยให้เปลี่ยนหน้าตาโดยไม่ต้องเขียนกติกาเกมใหม่ ทั้งนี้เป็นการแยกชั้นแบบ MVC อย่างง่าย ไม่ใช่ MVC framework เต็มรูปแบบ
- **Observer / event-driven programming**: Swing แจ้ง listener เมื่อเกิด mouse event และ timer event ผู้เล่นไม่ต้องถามซ้ำตลอดเวลาว่ามี event เกิดขึ้นหรือยัง
- **Adapter class**: `MouseAdapter` เตรียม implementation ว่างของ listener methods ไว้แล้ว คลาส `Mouse` จึง override เฉพาะ event ที่สนใจ
- **Dependency Injection**: `GameModel` รับ `Configuration` ผ่าน constructor ทำให้ dependency ชัดเจนและเปลี่ยน implementation/configuration ได้ง่ายขึ้น
- **Single Responsibility Principle (SRP)**: แต่ละคลาสมีเหตุผลหลักในการเปลี่ยนเพียงอย่างเดียว เช่น รูปแบบการวาดอยู่ใน `GamePanel`, กติกาอยู่ใน `GameModel`, และการจัดเก็บไฟล์อยู่ใน `Configuration`

โค้ดนี้ **ไม่ได้ใช้ Strategy หรือ Factory pattern** โดยเจตนา: ยังไม่มีอัลกอริทึมหลายแบบให้สลับ หรือการสร้าง object ที่ซับซ้อนพอจะต้องมี pattern เหล่านั้น อย่าเพิ่ม pattern เพียงเพื่อให้มีชื่อ pattern ในโปรเจกต์

## 6. ทำไมแยกแบบนี้จึงช่วยเรื่องการเรียนรู้

- แก้รูปแบบการวาดได้ใน `GamePanel` โดยไม่เปลี่ยนกติกาใน `GameModel`
- ปรับการเคลื่อนที่ได้ใน `Obstacle` โดยไม่ต้องค้นหา logic ที่ปนอยู่กับ UI
- ปรับวิธีบันทึกคะแนนได้ใน `Configuration`
- อ่าน flow จาก `Game` ได้เป็นลำดับ: สร้าง dependency → แสดงหน้าต่าง → update → repaint
- หลีกเลี่ยง global mutable state (`static` ที่แก้ค่าได้) ทำให้แต่ละเกมมีสถานะของตัวเอง

## 7. จุดที่ควรสังเกตในการอ่านโค้ด
_แนะนำให้อ่านตามนี้: GameStart → Game → GameModel → Player และ Obstacle → GamePanel → Mouse และ Configuration_
1. เริ่มจาก `GameStart.main()` และตามไปยัง handler ของปุ่ม Start
2. อ่าน constructor ของ `Game` เพื่อดูว่า object ต่าง ๆ เชื่อมกันอย่างไร
3. อ่าน `GameModel.update()` เพื่อดูวงจร input → กติกา → สถานะ
4. อ่าน `Player` และ `Obstacle` เพื่อดูการห่อหุ้มข้อมูลและพฤติกรรมเฉพาะ object
5. อ่าน `GamePanel.paintComponent()` เพื่อแยกการวาดออกจากการเปลี่ยนสถานะ
6. อ่าน `Configuration` เพื่อดูการจัดการไฟล์และการส่งข้อผิดพลาดกลับไปให้ UI แจ้งผู้ใช้

## 8. รายละเอียดพฤติกรรม

- เวลาที่แสดงและบันทึกเป็นวินาทีจริง โดยคำนวณจาก nanoseconds ที่ผ่านไป
- เมื่อไม่มี `config.xml` ระบบสร้างไฟล์โดยใช้ค่าเริ่มต้น 3 วินาที
- หากไฟล์ตั้งค่ามีรูปแบบผิดหรือเขียนไม่ได้ โปรแกรมจะแสดงข้อผิดพลาด แทนการกลืน exception
- ความเร็วสิ่งกีดขวางเก็บเป็นจำนวนเต็ม เพราะตำแหน่งที่วาดบนจอเป็นพิกัดจำนวนเต็ม
