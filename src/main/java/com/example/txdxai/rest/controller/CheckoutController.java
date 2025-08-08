package com.example.txdxai.rest.controller;

import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class CheckoutController {

    @Value("${stripe.public-key:}")
    private String publicKey;

    @Value("${stripe.price.standard}")
    private String standardPriceId;

    @Value("${stripe.price.enterprise}")
    private String enterprisePriceId;

    @PostMapping("/create-checkout-session")
    public ResponseEntity<Map<String, String>> createCheckoutSession(@RequestBody Map<String, Object> body) throws Exception {
        try {
            Long companyId = Long.valueOf(String.valueOf(body.get("companyId")));
            String plan = String.valueOf(body.get("plan")).toUpperCase();
            String successUrl = String.valueOf(body.get("successUrl"));
            String cancelUrl = String.valueOf(body.get("cancelUrl"));

            String priceId = switch (plan) {
                case "STANDARD" -> standardPriceId;
                case "ENTERPRISE" -> enterprisePriceId;
                default -> throw new IllegalArgumentException("Plan no válido: " + plan);
            };

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                    .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(cancelUrl)
                    .setClientReferenceId(String.valueOf(companyId)) // ← para el webhook
                    .putMetadata("plan", plan)                       // ← para el webhook
                    .addLineItem(SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPrice(priceId)
                            .build())
                    .build();

            Session session = Session.create(params);
            log.info("[Stripe] Checkout session creada. companyId={}, plan={}, sessionId={}", companyId, plan, session.getId());

            return ResponseEntity.ok(Map.of("checkoutUrl", session.getUrl()));
        } catch (Exception e) {
            log.error("[Stripe] Error creando checkout session", e);
            throw e;
        }
    }

    @GetMapping("/public-key")
    public ResponseEntity<Map<String, String>> getPublicKey() {
        return ResponseEntity.ok(Map.of("publicKey", publicKey));
    }
}
