# E-Commerce Spring Boot (Stripe) — `README.md`

## Overview
Simple Java Spring Boot e-commerce demo integrating Stripe Checkout. Backend endpoint used by the frontend `src/main/resources/templates/index.html` to create Stripe Checkout sessions.

## Tech stack
- Java (LTS)
- Spring Boot
- Maven
- Stripe Java SDK
- thymeleaf templating
- Plain HTML frontend (Bootstrap + Stripe.js)

## Prerequisites
- JDK 17\+ installed
- Maven installed
- Stripe account (publishable & secret keys)

## Configuration
Set your Stripe secret key in Spring configuration (example `src/main/resources/application.properties`):

    stripe.secretKey=sk_test_...

Do not commit secret keys to source control.

Also update the publishable key in `src/main/resources/templates/index.html` (replace `PROVIDE YOUR KEY`).

## Build & Run
From project root:

    mvn clean package
    mvn spring-boot:run

Or run the produced jar:

    java -jar target/<artifact>-<version>.jar

## API
POST `/product/v1/checkout`  
Request JSON (example):

    {
        name: "Smartphone",
        amount: 15000,     # amount in cents (smallest currency unit)
        quantity: 1,
        currency: "usd"    # optional, defaults to usd
    }

Response JSON contains `status`, `message`, and on success `sessionId` and `sessionUrl`. Frontend uses `sessionId` to call Stripe Checkout.

## Frontend
Open `/` served by the app. Ensure `src/main/resources/templates/index.html` has your Stripe publishable key and that product prices are expressed in cents (data-price attributes).

## Important notes & troubleshooting
- Stripe requires either an existing `price` ID or an inline `price_data` with `product`/`product_data`. The backend must attach `price_data` and include `product_data` when creating a price inline.
- Amounts must be in the smallest currency unit (cents for USD). Passing dollars will cause incorrect charges.
- If you see errors like:
  - "You must provide one of `price` or `price_data` for each line item..."
  - "You must specify either `product` or `product_data` when creating a price..."
  ensure the backend builds `SessionCreateParams.LineItem.PriceData` with `setCurrency`, `setUnitAmount`, and `setProductData(...)`.
- Avoid NullPointerException from a null Stripe session by returning a clear failure response when `Session.create(...)` returns null or throws `StripeException`. Check server logs and the Stripe `request-id` for Stripe support.

## Sample cURL
(Adjust host/port and JSON keys)

    curl -X POST http://localhost:8080/product/v1/checkout \
      -H "Content-Type: application/json" \
      -d '{"name":"Smartphone","amount":15000,"quantity":1}'

## Logging & debugging
- Check application logs for stack traces and Stripe exception messages.
- Use Stripe dashboard logs (request-id) to correlate failed API calls.

## License
MIT
