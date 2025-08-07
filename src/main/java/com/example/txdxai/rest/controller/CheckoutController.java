package com.example.txdxai.rest.controller;

import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class CheckoutController {

    @Value("${stripe.public-key}")
    private String publicKey;

    @PostMapping("/create-checkout-session")
    public ResponseEntity<Map<String, String>> createCheckoutSession(@RequestBody Map<String, String> body) throws Exception {
        String plan = body.get("plan"); // "standard" o "enterprise"
        String successUrl = body.get("successUrl");
        String cancelUrl = body.get("cancelUrl");

        String priceId = switch (plan.toLowerCase()) {
            case "standard" -> "price_1RtbMcHwQIAHovLfPyvCIour";   // Reemplaza con tus propios Price ID
            case "enterprise" -> "evt_1RtbN6HwQIAHovLfCYmDHnH1";
            default -> throw new IllegalArgumentException("Plan no válido");
        };

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(cancelUrl)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPrice(priceId)
                        .build())
                .build();

        Session session = Session.create(params);

        return ResponseEntity.ok(Map.of("checkoutUrl", session.getUrl()));
    }

    @GetMapping("/public-key")
    public ResponseEntity<Map<String, String>> getPublicKey() {
        return ResponseEntity.ok(Map.of("publicKey", publicKey));
    }
}
