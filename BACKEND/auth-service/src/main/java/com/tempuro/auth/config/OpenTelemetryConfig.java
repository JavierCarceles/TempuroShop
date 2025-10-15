package com.tempuro.auth.config;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OpenTelemetryConfig {

    private static final String SERVICE_NAME = "auth-service";
    private static final String OTLP_ENDPOINT = "http://localhost:4317"; // Collector OTLP

    private static final Logger logger = LoggerFactory.getLogger(OpenTelemetryConfig.class);

    /**
     * Configura OpenTelemetry sin registrar globalmente (evita conflictos con MySQL y otros SDKs).
     */
    @PostConstruct
    public void initOpenTelemetry() {

        // 1️⃣ Exportador OTLP: envía spans al collector (Jaeger, Grafana, etc.)
        OtlpGrpcSpanExporter otlpExporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint(OTLP_ENDPOINT)
                .build();

        // 2️⃣ Procesador de spans en lotes
        BatchSpanProcessor spanProcessor = BatchSpanProcessor.builder(otlpExporter)
                .setScheduleDelay(Duration.ofMillis(1000))
                .setMaxQueueSize(2048)
                .setMaxExportBatchSize(512)
                .build();

        // 3️⃣ Metadatos del servicio (nombre, entorno, etc.)
        Resource serviceResource = Resource.create(
                Attributes.builder()
                        .put("service.name", SERVICE_NAME)
                        .build()
        );

        // 4️⃣ Proveedor de tracers (motor de trazas)
        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(spanProcessor)
                .setResource(serviceResource)
                .build();

        // 5️⃣ Creamos el SDK sin registrarlo globalmente (para evitar conflictos con MySQL)
        OpenTelemetrySdk openTelemetry = OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .build();

        // 6️⃣ Obtenemos un tracer desde nuestra instancia (sin usar GlobalOpenTelemetry)
        openTelemetry.getTracer(SERVICE_NAME);

        // 7️⃣ Log informativo
        logger.info("✅ OpenTelemetry inicializado correctamente para el servicio: {}", SERVICE_NAME);
    }
}
