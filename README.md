# FullStack E-Ticaret Projesi

Bu proje, Patika.dev & N11 Backend Bootcamp kapsamında geliştirilmiş fullstack bir e-ticaret uygulamasıdır.

Backend tarafı Spring Boot ile geliştirilmiş, frontend tarafı React.js ile hazırlanmıştır.
Projede ürün listeleme, ürün detay sayfası, sepet yönetimi, sipariş oluşturma ve ödeme işlemleri bulunmaktadır.

---

## Kullanılan Teknolojiler

### Backend
- Java 21
- Spring Boot
- Spring Security
- JWT Authentication
- PostgreSQL
- Spring Data JPA / Hibernate
- Swagger / OpenAPI
- Iyzico Sandbox Payment
- Docker

### Frontend
- React.js
- Axios
- React Hooks
- CSS

### DevOps
- Docker Compose
- GitHub Actions (CI Pipeline)

---

## Proje Özellikleri

- Ürün listeleme ve detay sayfası
- Pagination (sayfalama)
- Kategori filtreleme
- Ürün arama
- Sepete ürün ekleme / silme
- Kullanıcıya özel sepet yapısı
- Sepetten sipariş oluşturma
- Kullanıcıya özel sipariş yönetimi
- Iyzico sandbox ile ödeme işlemi
- JWT tabanlı authentication
- Role-based authorization (USER / ADMIN)
- Swagger ile API dokümantasyonu

---

## Güvenlik Yapısı

- Auth endpointleri public olarak ayarlanmıştır
- Product GET endpointleri public
- Product POST / PUT / DELETE sadece ADMIN
- Cart, Order ve Payment endpointleri USER veya ADMIN rolü gerektirir
- JWT token Authorization header üzerinden gönderilir

---

## Swagger

Backend çalıştıktan sonra:

http://localhost:8081/swagger-ui.html

---

## Docker ile Çalıştırma

Proje root klasöründe:

docker compose up --build

Bu komut ile:

- PostgreSQL container olarak ayağa kalkar
- Backend container olarak ayağa kalkar
- Backend, Docker içindeki veritabanına bağlanır

Containerları görmek için:

docker ps

Durdurmak için:

docker compose down

---

## CI/CD (GitHub Actions)

Projede GitHub Actions kullanılarak otomatik build pipeline kurulmuştur.

Her develop branch push işleminde:

- Proje otomatik build edilir
- Maven compile süreci çalıştırılır

Workflow dosyası:

.github/workflows/backend.yml

---

## Backend Çalıştırma (Docker olmadan)

cd backend

Windows:
.\mvnw spring-boot:run

Mac/Linux:
./mvnw spring-boot:run

---

## Frontend Çalıştırma

cd frontend
npm install
npm start

Frontend:
http://localhost:3000

---

## Test Kartı (Iyzico)

Kart Sahibi: John Doe  
Kart Numarası: 5528790000000008  
Ay: 12  
Yıl: 2030  
CVC: 123

---

## Proje Yapısı

FullStack-ETicaret
- backend
- frontend
- docker-compose.yml
- .github/workflows/backend.yml
- README.md

---

## Geliştirici

Yigit Irfan Turan  
Patika.dev & N11 Backend Bootcamp  
Egitmen: Ibrahim Gokyar