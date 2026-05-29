Coupons Service
Serwis REST do zarządzania kuponami rabatowymi.
Projekt został przygotowany tak, jak robi się to w realnym systemie produkcyjnym — z podziałem na warstwy, czystą logiką biznesową, testami i obsługą konkurencji.

Funkcjonalności
- tworzenie nowego kuponu,
- użycie kuponu przez użytkownika,
- limit użyć „kto pierwszy, ten lepszy”,
- ograniczenie użycia do konkretnego kraju (na podstawie IP),
- jeden użytkownik może użyć kupon tylko raz,
- pełna obsługa błędów.
Technologie
 -Java 21
 -Spring Boot 3
 -Spring Data JPA + PostgreSQL
 -MapStruct
 -Testcontainers
 -JUnit 5, Mockito, RestAssured
Architektura
Poniżej opisuję, jak zbudowany jest projekt i dlaczego podjąłem takie decyzje.

1. Domena (domain)
W tej warstwie znajduje się cała logika biznesowa:
model kuponu i użycia,
obiekty wartości (CouponCode, CountryCode),
wyjątki biznesowe,
porty (interfejsy repozytoriów i GeoIP).

Dlaczego tak?  
Chciałem, aby logika była czysta i niezależna od frameworków. Dzięki temu domena jest łatwa do testowania i nie zależy od Springa ani bazy danych. Ułatwia to utrzymanie i rozwój projektu.

2. Aplikacja (application)
Warstwa odpowiedzialna za wykonywanie use‑case’ów:
tworzenie kuponu,
użycie kuponu,
retry przy konfliktach,
wywołanie eventu po użyciu kuponu.
Najważniejsza klasa: CouponService.

Dlaczego tak?  
Oddzieliłem to, co system robi, od tego, jak to robi. CouponService steruje przepływem danych, ale nie zawiera logiki biznesowej — ta jest w domenie. Dzięki temu kod jest czytelny i łatwy do rozszerzenia.

3. Adaptery (adapter.in / adapter.out)
REST API (adapter.in.web)
kontrolery,
DTO,
globalny handler błędów.

Dlaczego tak?  
API ma być cienką warstwą — tylko przyjmuje requesty i zwraca odpowiedzi. Cała logika jest niżej.

Baza danych (adapter.out.persistence)
encje JPA,
repozytoria Spring Data,
mapowanie domena ↔ baza (MapStruct).

Dlaczego tak?  
Domena nie powinna znać JPA. MapStruct pozwala uniknąć ręcznego przepisywania pól i utrzymuje kod w czystości.

GeoIP (adapter.out.geo)
prosty klient HTTP,
pobieranie kraju na podstawie IP.
Dlaczego tak?  
Wymaganie zadania mówi o ustalaniu kraju z IP. Zrobiłem to jako osobny adapter, żeby łatwo można było podmienić usługę GeoIP.

Obsługa konkurencji
System jest odporny na sytuacje, w których wiele osób próbuje użyć tego samego kuponu jednocześnie.

Wykorzystane mechanizmy:
 -optimistic locking (@Version),
 -pętla retry (domyślnie 3 próby),
 -unikalny indeks (coupon_id, user_id) — jeden user = jedno użycie,
 -test równoległy z ExecutorService.

Dlaczego tak?  
Optimistic locking jest lekki i skalowalny. Retry pozwala rozwiązać większość konfliktów bez błędów dla użytkownika. Test równoległy potwierdza, że licznik użyć jest zawsze poprawny.

API

Tworzenie kuponu
POST /api/v1/coupons

json
{
  "code": "WIOSNA2024",
  "maxUsages": 5,
  "countryCode": "PL"
}

Użycie kuponu

POST /api/v1/coupons/{code}/use?userId=arek

Nagłówek:

X-Real-IP: 185.157.14.235

Przykładowa odpowiedź:
json
{
  "couponCode": "WIOSNA",
  "remainingUsages": 4
}

Testy

Projekt zawiera:
 -testy jednostkowe domeny,
 -testy mapperów,
 -testy serwisu aplikacyjnego,
 -testy integracyjne REST API,
 -testy repozytoriów JPA,
 -test konkurencji,
 -Testcontainers PostgreSQL.
