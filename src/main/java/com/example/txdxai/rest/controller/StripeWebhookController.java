package com.example.txdxai.rest.controller;

import com.example.txdxai.core.service.CompanyService;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/stripe")
@RequiredArgsConstructor
public class StripeWebhookController {

    private final CompanyService companyService;

    @Value("${stripe.webhook-secret}")
    private String endpointSecret;

    // Para verificar rápidamente que este controller se está llamando
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        log.info("[Stripe] PING recibido");
        return ResponseEntity.ok("pong");
    }

    @PostMapping(
            value = "/webhook",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public ResponseEntity<String> handleStripeEvent(
            @RequestBody String payload,
            @RequestHeader(name = "Stripe-Signature", required = false) String sigHeader) {

        if (sigHeader == null) {
            log.warn("[Stripe] Falta cabecera Stripe-Signature");
            return ResponseEntity.badRequest().body("Missing Stripe-Signature");
        }

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (Exception e) {
            log.warn("[Stripe] Firma inválida del webhook: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        log.info("[Stripe] Evento recibido: {}", event.getType());

        if ("checkout.session.completed".equals(event.getType())) {
            var opt = event.getDataObjectDeserializer().getObject();
            if (opt.isEmpty()) {
                log.warn("[Stripe] checkout.session.completed sin objeto Session (deserializer vacío)");
                return ResponseEntity.ok("No session object");
            }

            Session session = (Session) opt.get();

            String sessionId = session.getId();
            String companyIdStr = session.getClientReferenceId(); // lo seteamos cuando creamos la sesión
            String plan = session.getMetadata() != null ? session.getMetadata().get("plan") : null;

            log.info("[Stripe] sessionId={}, clientReferenceId={}, metadata.plan={}",
                    sessionId, companyIdStr, plan);

            if (companyIdStr == null) {
                log.error("[Stripe] Falta client_reference_id para session {}", sessionId);
                return ResponseEntity.badRequest().body("Missing client_reference_id");
            }

            try {
                Long companyId = Long.valueOf(companyIdStr);
                companyService.activateSubscription(companyId, plan);
                log.info("[Stripe] Suscripción ACTIVADA: companyId={}, plan={}", companyId, plan);
            } catch (Exception e) {
                log.error("[Stripe] Error actualizando compañía para la sesión {}: {}", sessionId, e.getMessage(), e);
                return ResponseEntity.internalServerError().body("Update failed");
            }
        }

        // Acepta otros eventos sin hacer nada
        return ResponseEntity.ok("OK");
    }
}
