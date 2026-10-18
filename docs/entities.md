# Yapı	Ne işe yarıyor?
User:	Sistemdeki kullanıcıları tutar. Ad, soyad, e-posta, şifre ve kullanıcı tarih bilgilerini içerir.
Book:	ShareShelf'teki kitapların temel bilgilerini tutar. Kitap adı ve yazar zorunlu; ISBN, yayınevi, sayfa sayısı, kapak ve özet gibi bilgiler isteğe bağlı.<br>
ReadingStatus:	Kullanıcının bir kitabı okuma durumunu belirtir: WANT_TO_READ, READING, READ.<br>
UserLibrary:	Kullanıcının kütüphanesindeki kitapları tutar. Kullanıcı ile kitap arasındaki ilişkiyi kurar ve kitabın okuma durumunu saklar.<br>
ReadingList:	Kullanıcının oluşturduğu özel okuma listelerini tutar. Örneğin “Yazın Okuyacaklarım”. Ayrıca listenin gizli/açık (isPublic) olmasını belirler.<br>
ReadingListItem:	Bir okuma listesinin içinde hangi kitapların bulunduğunu tutar. Aynı kitap aynı listeye iki kez eklenemez.<br>
BookReview:	Kullanıcının bir kitap için verdiği puan ve yorumu tutar. Aynı kullanıcı aynı kitaba yalnızca bir değerlendirme yapabilir.
