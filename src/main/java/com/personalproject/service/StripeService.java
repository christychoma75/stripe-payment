package com.personalproject.service;

import com.personalproject.dto.ProductRequest;
import com.personalproject.dto.StripeResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    @Value("${stripe.secretKey}")
    private String secretKey;
    //stripe -API
    //->productName, amount, quantity, currency
    //-> return sessionId and URL

    public StripeResponse checkoutProducts(ProductRequest productRequest) {
        //connect to stripe account
        Stripe.apiKey = secretKey;

        // amount must be in the smallest currency unit (e.g., cents)
        Long unitAmount = productRequest.getAmount();

        SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(productRequest.getProductName())
                .build();
        SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency(productRequest.getCurrency() != null ? productRequest.getCurrency() : "USD")
                .setUnitAmount(unitAmount)
                .setProductData(productData)
                .build();
        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                .setQuantity(productRequest.getQuantity())
                .setPriceData(priceData)
                .build();
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/success")
                .setCancelUrl("http://localhost:8080/cancel")
                .addLineItem(lineItem)
                .build();

        try {
            Session session = Session.create(params);
            if (session == null) {
                return StripeResponse.builder()
                        .status("FAILURE")
                        .message("Stripe returned null session")
                        .build();
            }
            return StripeResponse.builder()
                    .status("SUCCESS")
                    .message("checkout session created successfully")
                    .sessionId(session.getId())
                    .sessionUrl(session.getUrl())
                    .build();
        } catch (StripeException e) {
            System.out.println(e.getMessage());
            return StripeResponse.builder()
                    .status("FAILURE")
                    .message(e.getMessage())
                    .build();
        }
    }
}
