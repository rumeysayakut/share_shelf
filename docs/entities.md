# ShareShelf – Entity Yapıları

Bu dosya, ShareShelf projesinde kullanılan entity ve enum sınıflarını, görevlerini ve aralarındaki ilişkileri açıklar.

## 1. Kullanıcı ve Kitap Yönetimi

| Yapı | Ne işe yarar? |
|---|---|
| `User` | Sistemdeki kullanıcıları tutar. Ad, soyad, e-posta, şifre ve kullanıcı tarih bilgilerini içerir. |
| `Book` | ShareShelf'teki kitapların temel bilgilerini tutar. Kitap adı ve yazar zorunludur; ISBN, yayınevi, yayın yılı, sayfa sayısı, kapak ve özet gibi bilgiler isteğe bağlıdır. |

## 2. Kütüphane ve Okuma Listeleri

| Yapı | Ne işe yarar? |
|---|---|
| `ReadingStatus` | Kullanıcının bir kitabı okuma durumunu belirtir: `WANT_TO_READ`, `READING`, `READ`. |
| `UserLibrary` | Kullanıcının kütüphanesindeki kitapları tutar. Kullanıcı ile kitap arasındaki ilişkiyi kurar ve okuma durumunu saklar. Aynı kitap aynı kullanıcının kütüphanesine iki kez eklenemez. |
| `ReadingList` | Kullanıcının oluşturduğu okuma listelerini tutar. Liste adı ve gizli/açık olma durumunu (`isPublic`) içerir. Listeler varsayılan olarak gizlidir. |
| `ReadingListItem` | Bir okuma listesindeki kitapları tutar. Aynı kitap aynı listeye iki kez eklenemez. |

## 3. Kitap Değerlendirmeleri ve Alıntılar

| Yapı | Ne işe yarar? |
|---|---|
| `BookReview` | Kullanıcının bir kitap için verdiği 1–5 arası puanı ve isteğe bağlı yorumunu tutar. Aynı kullanıcı aynı kitap için yalnızca bir değerlendirme oluşturabilir. |
| `BookQuote` | Kullanıcının bir kitaptan kaydettiği alıntıyı ve isteğe bağlı sayfa numarasını tutar. Her alıntı ayrı ayrı herkese açık veya gizli olabilir. |
| `UserReview` | Bir kullanıcının başka bir kullanıcıya verdiği 1–5 arası puanı ve isteğe bağlı yorumunu tutar. |

## 4. Fiziksel Kitap Paylaşımı ve Ödünç Alma

| Yapı | Ne işe yarar? |
|---|---|
| `ShareType` | Fiziksel kitap paylaşımının türünü belirtir: `LOAN` (ödünç verme) veya `SALE` (satış). |
| `BookShareStatus` | Bir fiziksel kitap paylaşımının durumunu belirtir: `ACTIVE`, `RESERVED`, `UNAVAILABLE`, `CLOSED`. |
| `BookShare` | Bir kullanıcının sahip olduğu fiziksel kitabı ödünç vermek veya satmak üzere listelemesini sağlar. Paylaşım türünü, durumunu, varsa depozito veya satış fiyatını ve açıklamaları tutar. |
| `BorrowingStatus` | Ödünç alma sürecinin durumunu belirtir. Bekleyen, kabul edilen, reddedilen, iptal edilen, ödünç alınmış, iade bekleyen, iade edilmiş, gecikmiş ve sorun bildirilmiş durumları içerir. |
| `Borrowing` | Bir kullanıcının fiziksel kitabı ödünç alma talebini ve ödünç alma/iade sürecindeki tarihleri tutar. |
| `PurchaseStatus` | Satın alma talebinin durumunu belirtir: `PENDING`, `ACCEPTED`, `REJECTED`, `CANCELLED`, `COMPLETED`, `DISPUTED`. |
| `PurchaseRequest` | Bir kullanıcının satışa konulmuş fiziksel kitabı satın alma talebini ve talebin durumunu tutar. ShareShelf ödeme işlemi gerçekleştirmez. |

## 5. Teslim ve Doğrulama

| Yapı | Ne işe yarar? |
|---|---|
| `HandoverType` | Fiziksel kitap hareketinin türünü belirtir: `DELIVERY` (teslim) veya `RETURN` (iade). |
| `HandoverVerification` | Ödünç alma sürecindeki teslim veya iade doğrulaması için kodun özetini (hash), son kullanma zamanını ve doğrulama durumunu tutar. |
| `HandoverConfirmation` | Teslim veya iade sürecinde kullanıcıların ayrı ayrı verdiği onayları tutar. Aynı kullanıcı aynı doğrulama için birden fazla onay kaydı oluşturamaz. |

## 6. Sohbet ve Mesajlaşma

| Yapı | Ne işe yarar? |
|---|---|
| `Conversation` | Bir kitap paylaşımı hakkında iki kullanıcı arasındaki sohbeti temsil eder. |
| `Message` | Sohbetin içindeki mesajı, gönderen kullanıcıyı, mesaj içeriğini ve gönderim zamanını tutar. Kullanıcının yalnızca katılımcısı olduğu sohbetlere erişebilmesi servis katmanında kontrol edilmelidir. |

## 7. Bildirimler

| Yapı | Ne işe yarar? |
|---|---|
| `Notification` | Kullanıcıya gösterilecek uygulama içi bildirimin başlığını, mesajını, okunma durumunu ve oluşturulma zamanını tutar. |
