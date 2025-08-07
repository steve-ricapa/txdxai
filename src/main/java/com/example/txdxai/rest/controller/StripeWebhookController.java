package com.example.txdxai.rest.controller;

import com.example.txdxai.core.service.CompanyService;
import com.example.txdxai.core.model.Company;
import com.stripe.model.checkout.Session;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/stripe/webhook")
public class StripeWebhookController {

    private final CompanyService companyService;
    private final String endpointSecret = "whsec_..."; // tu webhook secret de Stripe

    public StripeWebhookController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping
    public ResponseEntity<String> handleStripeEvent(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().get();

            // Buscar a qué empresa le corresponde este session_id (esto depende de cómo asocies empresa ↔ sesión)
            // Ejemplo:
            String companyId = session.getClientReferenceId();
            Company company = companyService.findById(Long.parseLong(companyId)).orElseThrow();

            company.setSubscriptionEndDate(LocalDate.now().plusMonths(6));
            company.setTokensUsed(0);
            company.setSubscriptionPlan("standard"); // o "enterprise"
            company.setTokenLimit("standard".equals(company.getSubscriptionPlan()) ? 100_000 : 1_000_000);

            companyService.update(company.getId(), company);
        }

        return ResponseEntity.ok("Evento recibido");
    }
}
