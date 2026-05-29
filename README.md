Coupons Service

Prosty serwis REST do zarządzania kuponami rabatowymi.Projekt został przygotowany tak, jak robi się to w realnym systemie produkcyjnym — z podziałem na warstwy, czystą logiką biznesową, testami i obsługą konkurencji.

Funkcjonalności

tworzenie nowego kuponu,

użycie kuponu przez użytkownika,

limit użyć „kto pierwszy, ten lepszy”,

ograniczenie użycia do konkretnego kraju (na podstawie IP),

jeden użytkownik może użyć kupon tylko raz,

pełna obsługa błędów.

Technologie

Java 21

Spring Boot 3

Spring Data JPA + PostgreSQL

MapStruct

Testcontainers

JUnit 5, Mockito, RestAssured

Architektura

Projekt jest podzielony na trzy główne części:

1. Domena (domain)

Zawiera całą logikę biznesową:

model kuponu i użycia,

obiekty wartości (CouponCode, CountryCode),

wyjątki biznesowe,

porty (interfejsy repozytoriów i GeoIP).

Warstwa domenowa jest niezależna od Springa i bazy danych.

2. Aplikacja (application)

Warstwa realizująca use‑case’y:

tworzenie kuponu,

użycie kuponu,

retry przy konfliktach,

wywołanie eventu po użyciu kuponu.

Najważniejsza klasa: CouponService.

3. Adaptery (adapter.in / adapter.out)

REST API — kontrolery, DTO, globalny handler błędów

JPA — encje, repozytoria, mapowanie domena ↔ baza

GeoIP — klient HTTP pobierający kraj z IP

Domena widzi tylko interfejsy — szczegóły techniczne są ukryte w adapterach.

Obsługa konkurencji

System jest odporny na sytuacje, w których wiele osób próbuje użyć tego samego kuponu jednocześnie.

Wykorzystane mechanizmy:

optimistic locking (@Version),

pętla retry (domyślnie 3 próby),

unikalny indeks (coupon_id, user_id) — jeden user = jedno użycie,

test równoległy z ExecutorService.

Dzięki temu licznik użyć zawsze pozostaje poprawny.

API

Tworzenie kuponu

POST /api/v1/coupons

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

{
  "couponCode": "WIOSNA2024",
  "remainingUsages": 4
}

Testy

Projekt zawiera:

testy jednostkowe domeny,

testy mapperów,

testy serwisu aplikacyjnego,

testy integracyjne REST API,

testy repozytoriów JPA,

test konkurencji,

Testcontainers PostgreSQL.
